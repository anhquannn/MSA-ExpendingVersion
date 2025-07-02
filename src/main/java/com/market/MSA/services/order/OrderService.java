package com.market.MSA.services.order;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.constants.RewardPointTransactionType;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.OrderMapper;
import com.market.MSA.models.order.Cart;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.order.OrderDetail;
import com.market.MSA.models.order.PromoCode;
import com.market.MSA.models.product.Branch;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.user.RewardPointTransaction;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.order.CartRepository;
import com.market.MSA.repositories.order.OrderDetailRepository;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.product.BranchRepository;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.OrderFilterRequest;
import com.market.MSA.requests.order.OrderRequest;
import com.market.MSA.requests.order.PromoCodeUsageRequest;
import com.market.MSA.responses.goship.RatesResponse;
import com.market.MSA.responses.order.*;
import com.market.MSA.services.others.EmailService;
import com.market.MSA.services.others.GoshipService;
import com.market.MSA.services.others.NotificationService;
import com.market.MSA.services.product.InventoryProductService;
import com.market.MSA.services.product.ProductService;
import com.market.MSA.services.user.RewardPointService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
  final OrderRepository orderRepository;
  final CartService cartService;
  final CartRepository cartRepository;
  final BranchRepository branchRepository;
  final CartItemService cartItemService;
  final PromoCodeService promoCodeService;
  final UserRepository userRepository;
  final EmailService emailService;
  final ProductService productService;
  final OrderDetailRepository orderDetailRepository;
  final OrderDetailService orderDetailService;
  final OrderMapper orderMapper;
  final InventoryProductService inventoryProductService;
  final InventoryProductRepository inventoryProductRepository;
  final NotificationService notificationService;
  final PromoCodeUsageService promoCodeUsageService;
  final RewardPointService rewardPointService;
  private final GoshipService goshipService;

  @Transactional
  public OrderResponse createOrder(
      Long userId, Long branchId, Long userAddressId, Long cartId, List<String> promoCodes) {
    // Tính toán tổng tiền và giảm giá
    OrderSummaryResponse orderSummary =
        calculateOrderSummary(branchId, userAddressId, userId, cartId, promoCodes);
    double grandTotal = orderSummary.getGrandTotal();

    // Lấy thông tin user, cart, branch
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

    Branch branch =
        branchRepository
            .findById(branchId)
            .orElseThrow(() -> new AppException(ErrorCode.BRANCH_NOT_FOUND));

    // Tạo đơn hàng mới
    Order order =
        Order.builder()
            .user(user)
            .cart(cart)
            .orderDate(LocalDateTime.now())
            .branch(branch)
            .grandTotal(grandTotal)
            .status(OrderStatus.PENDING)
            .build();

    order = orderRepository.save(order);

    // Nếu có mã giảm giá, lưu vào bảng OrderPromoCode và ghi nhận sử dụng
    if (promoCodes != null && !promoCodes.isEmpty()) {
      if (order.getPromoCodes() == null) {
        order.setPromoCodes(new ArrayList<>()); // Khởi tạo nếu bị null
      }

      for (String promoCode : promoCodes) {
        // Check if user has already used this promo code
        if (promoCodeService.hasUserUsedPromoCode(
            userId, promoCodeService.findPromoCodeByCode(promoCode).getPromoCodeId())) {
          throw new AppException(ErrorCode.PROMO_CODE_ALREADY_USED);
        }

        PromoCode promo = promoCodeService.findPromoCodeByCode(promoCode);
        if (!promo.getStatus().equals(PromocodeStatus.EXPIRED)) {
          order.getPromoCodes().add(promo);

          // Record promo code usage
          PromoCodeUsageRequest usageRequest =
              PromoCodeUsageRequest.builder()
                  .usedAt(LocalDateTime.now())
                  .promoCodeId(promo.getPromoCodeId())
                  .orderId(order.getOrderId())
                  .userId(userId)
                  .build();
          promoCodeUsageService.createPromoCodeUsage(usageRequest);
        }
      }
    }

    // Lấy danh sách sản phẩm từ giỏ hàng
    List<CartItemResponse> cartItems = cartItemService.getCartItemsByCartId(cartId);

    for (CartItemResponse cartItem : cartItems) {
      Product product = productService.findProductById(cartItem.getProduct().getProductId());

      // Tạo chi tiết đơn hàng
      OrderDetail orderDetail =
          OrderDetail.builder()
              .order(order)
              .product(product)
              .quantity(cartItem.getQuantity())
              .unitPrice(product.getPrice())
              .totalPrice(cartItem.getQuantity() * product.getPrice())
              .build();

      orderDetailRepository.save(orderDetail);

      // Cập nhật số lượng tồn kho sản phẩm
      inventoryProductService.updateInventoryProduct(
          branchId, product.getProductId(), cartItem.getQuantity());

      // Cập nhật doanh số sản phẩm
      productService.updateTotalRevenue(product.getProductId(), cartItem.getQuantity());
    }

    // Xóa giỏ hàng sau khi đặt hàng
    cartItemService.clearCart(cartId);

    // Send notification
    notificationService.sendOrderCreatedNotification(order.getOrderId());

    return orderMapper.toOrderResponse(order);
  }

  @Transactional
  public OrderSummaryResponse calculateOrderSummary(
      Long branchId, Long userAddressId, Long userId, Long cartId, List<String> promoCodes) {
    CartResponse cart = cartService.getCartById(cartId);

    if (cart == null || !cart.getUser().getUserId().equals(userId)) {
      throw new AppException(ErrorCode.CART_NOT_FOUND);
    }

    double totalCost = cartItemService.calculateCartTotal(cartId);
    double discount = 0.0;
    double grandTotal = totalCost;

    if (promoCodes != null && !promoCodes.isEmpty()) {
      for (String promoCode : promoCodes) {
        // Pass userId to check if promo code has been used
        PromoCodeResponse promo = promoCodeService.getPromoCodeByCode(promoCode, userId);
        if (totalCost >= promo.getMinimumOrderValue()
            && !promo.getStatus().equals(PromocodeStatus.EXPIRED)) {
          double currentDiscount = totalCost * (promo.getDiscountPercentage() / 100);
          discount += currentDiscount;
          grandTotal -= currentDiscount;
        }
      }
    }

    if (grandTotal < 0) {
      throw new AppException(ErrorCode.WRONG_PROMO_CODE);
    }

    List<RatesResponse> rates = goshipService.createRates(branchId, userAddressId, grandTotal);
    if (rates == null || rates.isEmpty()) {
      throw new AppException(ErrorCode.RATES_NOT_FOUND);
    }

    // Get the first rate
    RatesResponse firstRate = rates.getFirst();
    grandTotal += firstRate.getTotalAmount();

    return OrderSummaryResponse.builder()
        .totalCost(totalCost)
        .discount(discount)
        .grandTotal(grandTotal)
        .rates(firstRate)
        .build();
  }

  @Transactional
  public OrderResponse updateOrder(Long orderId, OrderRequest request) {
    Optional<Order> existingOrder = orderRepository.findById(orderId);
    if (existingOrder.isPresent()) {
      Order order = existingOrder.get();
      orderMapper.updateOrderFromRequest(request, order);
      orderRepository.save(order);
      return orderMapper.toOrderResponse(order);
    }
    throw new AppException(ErrorCode.ORDER_NOT_FOUND);
  }

  @Transactional
  public boolean deleteOrder(Long orderId) {
    if (!orderRepository.existsById(orderId)) {
      throw new AppException(ErrorCode.ORDER_NOT_FOUND);
    }
    orderRepository.deleteById(orderId);
    return true;
  }

  public OrderResponse getOrderById(Long orderId) {
    return orderRepository
        .findById(orderId)
        .map(orderMapper::toOrderResponse)
        .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
  }

  @Cacheable("orders")
  public List<OrderResponse> getAll() {
    return orderRepository.findAll().stream()
        .map(orderMapper::toOrderResponse)
        .collect(Collectors.toList());
  }

  @Cacheable(
      value = "all_orders",
      key =
          "{#request.branchId, #request.userId, #request.status, #request.phoneNumber, "
              + "#request.sortBy, #request.sortDirection}")
  public List<OrderResponse> getAllOrders(OrderFilterRequest request) {
    // Validate sort direction
    Sort.Direction direction;
    try {
      direction = Sort.Direction.fromString(request.getSortDirection().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid sort direction. Use 'asc' or 'desc'");
    }

    // Handle special case for user phone number sorting
    Sort sort;
    if ("user-phoneNumber".equals(request.getSortBy())) {
      sort = Sort.by(direction, "user.phoneNumber");
    } else {
      validateSortField(request.getSortBy());
      sort = Sort.by(direction, request.getSortBy());
    }

    // Get all orders with filters (without pagination)
    List<Order> orders =
        orderRepository.filter(
            request.getStatus(),
            request.getUserId(),
            request.getBranchId(),
            request.getPhoneNumber(),
            request.getFromDate(),
            request.getToDate(),
            sort);

    // Convert to response DTOs
    return orders.stream().map(orderMapper::toOrderResponse).collect(Collectors.toList());
  }

  @Cacheable(
      value = "orders_paging",
      key =
          "{#request.branchId, #request.userId, #request.status, #request.phoneNumber, "
              + "#request.page, #request.pageSize, #request.sortBy, #request.sortDirection}")
  public Page<OrderResponse> getAllOrdersWithPaging(OrderFilterRequest request) {
    // Convert from 1-based to 0-based page index
    int page = request.getPage() > 0 ? request.getPage() - 1 : 0;
    int size = request.getPageSize();

    // Validate page size
    if (size > 100) {
      size = 100;
    }

    // Validate sort direction
    Sort.Direction direction;
    try {
      direction = Sort.Direction.fromString(request.getSortDirection().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid sort direction. Use 'asc' or 'desc'");
    }

    // Handle special case for user phone number sorting
    Sort sort;
    if ("user-phoneNumber".equals(request.getSortBy())) {
      sort = Sort.by(direction, "user.phoneNumber");
    } else {
      validateSortField(request.getSortBy()); // Your existing validation method
      sort = Sort.by(direction, request.getSortBy());
    }

    Pageable pageable = PageRequest.of(page, size, sort);

    // Get orders with filters
    Page<Order> orders =
        orderRepository.filterWithPaging(
            request.getStatus(),
            request.getUserId(),
            request.getBranchId(),
            request.getPhoneNumber(),
            request.getFromDate(),
            request.getToDate(),
            pageable);

    return orders.map(orderMapper::toOrderResponse);
  }

  @Async
  public void sendRecipe(Long orderId, String email) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    CompletableFuture.runAsync(() -> sendOrderDetails(order, email));
  }

  @Async
  public void sendOrderDetails(Order order, String userEmail) {
    try {
      String subject = "Order Confirmation - Your Order Details";
      String emailBody = buildOrderConfirmationEmail(order);
      emailService.sendEmail(userEmail, subject, emailBody);
    } catch (Exception e) {
      log.error("Failed to send order details email for order: {}", order.getOrderId(), e);
    }
  }

  private String buildOrderConfirmationEmail(Order order) {
    StringBuilder emailBody = new StringBuilder();

    // Header
    emailBody.append(createEmailHeader(order));

    // Order Summary
    emailBody.append(createOrderSummary(order));

    // Order Items
    emailBody.append(createOrderItemsList(order));

    // Footer
    emailBody.append(createEmailFooter());

    return emailBody.toString();
  }

  private String createEmailHeader(Order order) {
    return String.format(
        """
			<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
				<h2>Thank you for your order!</h2>
				<p>Order #%d has been confirmed and is being processed.</p>
				<p>Order Date: %s</p>
				<hr style="border: 1px solid #eee; margin: 20px 0;">
			""",
        order.getOrderId(), order.getOrderDate().toString());
  }

  private String createOrderSummary(Order order) {
    return String.format(
        """
			<div style="margin-bottom: 20px;">
				<h3>Order Summary</h3>
				<p><strong>Order ID:</strong> %d</p>
				<p><strong>Status:</strong> %s</p>
				<p><strong>Total Amount:</strong> %.2f VNĐ</p>
			</div>
			<hr style="border: 1px solid #eee; margin: 20px 0;">
			""",
        order.getOrderId(), order.getStatus(), order.getGrandTotal());
  }

  private String createOrderItemsList(Order order) {
    StringBuilder itemsList =
        new StringBuilder(
            """
				<div>
					<h3>Order Items</h3>
					<table style="width: 100%; border-collapse: collapse; margin: 20px 0;">
						<thead>
							<tr style="background-color: #f5f5f5;">
								<th style="padding: 10px; text-align: left; border-bottom: 1px solid #ddd;">Product</th>
								<th style="padding: 10px; text-align: right; border-bottom: 1px solid #ddd;">Quantity</th>
								<th style="padding: 10px; text-align: right; border-bottom: 1px solid #ddd;">Price</th>
								<th style="padding: 10px; text-align: right; border-bottom: 1px solid #ddd;">Total</th>
							</tr>
						</thead>
						<tbody>
				""");

    orderDetailService
        .findOrderDetailsByOrderId(order.getOrderId())
        .forEach(
            detail ->
                itemsList.append(
                    String.format(
                        """
							<tr>
								<td style="padding: 10px; border-bottom: 1px solid #eee;">%s</td>
								<td style="padding: 10px; text-align: right; border-bottom: 1px solid #eee;">%d</td>
								<td style="padding: 10px; text-align: right; border-bottom: 1px solid #eee;">%.2f VNĐ</td>
								<td style="padding: 10px; text-align: right; border-bottom: 1px solid #eee;">%.2f VNĐ</td>
							</tr>
							""",
                        detail.getProduct().getName(),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getQuantity() * detail.getUnitPrice())));

    itemsList.append("""
				</tbody>
			</table>
		</div>
		""");

    return itemsList.toString();
  }

  private String createEmailFooter() {
    return """
		<div style="margin-top: 30px; padding: 15px; background-color: #f9f9f9; border-radius: 5px;">
			<p>Thank you for shopping with us!</p>
			<p>If you have any questions about your order, please contact our support team.</p>
			<p>Best regards,<br>Market Team</p>
		</div>
		</div> <!-- Close main container -->
		""";
  }

  private void validateSortField(String sortBy) {
    List<String> validSortFields = Arrays.asList("orderDate", "grandTotal", "status");
    if (!validSortFields.contains(sortBy)) {
      throw new AppException(ErrorCode.INVALID_SORT_FIELD);
    }
  }

  @Transactional(readOnly = true)
  public Double calculateMonthlyRevenue(int year, int month) {
    return orderRepository.calculateMonthlyRevenue(year, month);
  }

  @Transactional(readOnly = true)
  public Double calculateYearlyRevenue(int year) {
    return orderRepository.calculateYearlyRevenue(year);
  }

  @Transactional(readOnly = true)
  public Double calculateMonthlyRevenueByBranch(int year, int month, Long branchId) {
    return orderRepository.calculateMonthlyRevenueByBranch(year, month, branchId);
  }

  @Transactional(readOnly = true)
  public Double calculateYearlyRevenueByBranch(int year, Long branchId) {
    return orderRepository.calculateYearlyRevenueByBranch(year, branchId);
  }

  @Transactional(readOnly = true)
  public Double calculateMonthlyRevenueByUser(int year, int month, Long userId) {
    return orderRepository.calculateMonthlyRevenueByUser(year, month, userId);
  }

  @Transactional(readOnly = true)
  public Double calculateYearlyRevenueByUser(int year, Long userId) {
    return orderRepository.calculateYearlyRevenueByUser(year, userId);
  }

  @Transactional(readOnly = true)
  public Double calculateMonthlyRevenueByBranchAndUser(
      int year, int month, Long branchId, Long userId) {
    return orderRepository.calculateMonthlyRevenueByBranchAndUser(year, month, branchId, userId);
  }

  @Transactional(readOnly = true)
  public Double calculateYearlyRevenueByBranchAndUser(int year, Long branchId, Long userId) {
    return orderRepository.calculateYearlyRevenueByBranchAndUser(year, branchId, userId);
  }

  @Transactional(readOnly = true)
  public RevenueStatisticsResponse getRevenueStatistics(
      int year,
      int month,
      Long branchId,
      Long userId,
      int topPeriod,
      int topLimit,
      int expiringDays) {
    // Calculate current month's data
    Double currentMonthRevenue =
        branchId != null
            ? calculateMonthlyRevenueByBranch(year, month, branchId)
            : calculateMonthlyRevenue(year, month);
    Long currentMonthOrderCount =
        branchId != null
            ? orderRepository.countByMonthAndBranch(year, month, branchId)
            : orderRepository.countByMonth(year, month);

    // Calculate previous month's data
    int prevYear = month == 1 ? year - 1 : year;
    int prevMonth = month == 1 ? 12 : month - 1;

    Double prevMonthRevenue =
        branchId != null
            ? calculateMonthlyRevenueByBranch(prevYear, prevMonth, branchId)
            : calculateMonthlyRevenue(prevYear, prevMonth);
    Long prevMonthOrderCount =
        branchId != null
            ? orderRepository.countByMonthAndBranch(prevYear, prevMonth, branchId)
            : orderRepository.countByMonth(prevYear, prevMonth);

    // Calculate percentage changes
    double revenueChangePercent =
        prevMonthRevenue != 0
            ? ((currentMonthRevenue - prevMonthRevenue) / prevMonthRevenue) * 100
            : (currentMonthRevenue > 0 ? 100.0 : 0.0);

    double orderCountChangePercent =
        prevMonthOrderCount != 0
            ? ((double) (currentMonthOrderCount - prevMonthOrderCount) / prevMonthOrderCount) * 100
            : (currentMonthOrderCount > 0 ? 100.0 : 0.0);

    // Build response
    RevenueStatisticsResponse.RevenueStatisticsResponseBuilder builder =
        RevenueStatisticsResponse.builder()
            .totalMonthlyRevenue(currentMonthRevenue)
            .revenueChangePercent(Math.round(revenueChangePercent * 10.0) / 10.0)
            .totalOrders(currentMonthOrderCount)
            .orderCountChangePercent(Math.round(orderCountChangePercent * 10.0) / 10.0)
            .totalYearlyRevenue(
                branchId != null
                    ? calculateYearlyRevenueByBranch(year, branchId)
                    : calculateYearlyRevenue(year));

    // Add user specific data if userId is provided
    if (userId != null) {
      builder
          .userMonthlyRevenue(calculateMonthlyRevenueByUser(year, month, userId))
          .userYearlyRevenue(calculateYearlyRevenueByUser(year, userId));
    }

    // Add branch specific data if branchId is provided
    if (branchId != null) {
      builder
          .branchMonthlyRevenue(calculateMonthlyRevenueByBranch(year, month, branchId))
          .branchYearlyRevenue(calculateYearlyRevenueByBranch(year, branchId));
    }

    // Add combined branch and user data if both are provided
    if (branchId != null && userId != null) {
      builder
          .branchUserMonthlyRevenue(
              calculateMonthlyRevenueByBranchAndUser(year, month, branchId, userId))
          .branchUserYearlyRevenue(calculateYearlyRevenueByBranchAndUser(year, branchId, userId));
    }

    // Fetch top-selling and expiring low-stock products
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusMonths(topPeriod);
    Pageable pageable = PageRequest.of(0, topLimit);

    List<Object[]> topProductRows =
        orderDetailRepository.findTopSellingProducts(branchId, startDate, endDate, pageable);
    List<TopSellingProductResponse> topSellingProducts =
        topProductRows.stream()
            .map(
                row ->
                    TopSellingProductResponse.builder()
                        .productId((Long) row[0])
                        .name((String) row[1])
                        .totalQuantity((Long) row[2])
                        .build())
            .toList();

    LocalDateTime thresholdDate = endDate.plusDays(expiringDays);
    List<com.market.MSA.models.product.InventoryProduct> inventoryProducts =
        inventoryProductRepository.findExpiringLowStock(thresholdDate);
    List<ExpiringProductResponse> expiringProducts =
        inventoryProducts.stream()
            .map(
                ip ->
                    ExpiringProductResponse.builder()
                        .productId(ip.getProduct().getProductId())
                        .name(ip.getProduct().getName())
                        .quantity(ip.getStockNumber())
                        .expDate(ip.getExpDate())
                        .build())
            .toList();

    // Map monthly revenues
    java.util.List<Object[]> revRows =
        orderDetailRepository.findMonthlyRevenue(branchId, startDate, endDate);
    java.util.List<MonthlyRevenueDataResponse> revenues =
        revRows.stream()
            .map(
                r ->
                    new MonthlyRevenueDataResponse(
                        (Integer) r[0], (Integer) r[1], ((Number) r[2]).doubleValue()))
            .toList();

    builder
        .revenues(revenues)
        .topSellingProducts(topSellingProducts)
        .expiringLowStockProducts(expiringProducts);

    return builder.build();
  }

  @Transactional
  public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
    // Validate status string
    if (!OrderStatus.isValidStatus(newStatus)) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

    OrderStatus currentStatus = order.getStatus();
    OrderStatus updatedStatus = OrderStatus.from(newStatus);

    // Validate status transition
    validateStatusTransition(currentStatus, updatedStatus);

    // If order is being marked as completed and wasn't before
    if (updatedStatus == OrderStatus.COMPLETED && currentStatus != OrderStatus.COMPLETED) {
      if (!isPointsAwarded(order)) {
        long pointsEarned = (long) Math.floor(order.getGrandTotal());
        if (pointsEarned > 0) {
          rewardPointService.earnPoints(order.getUser().getUserId(), orderId, pointsEarned);
        }
      }
    }

    // Update order status
    order.setStatus(updatedStatus);
    order = orderRepository.save(order);

    return orderMapper.toOrderResponse(order);
  }

  private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
    if (currentStatus == OrderStatus.CANCELLED
        || currentStatus == OrderStatus.COMPLETED
        || currentStatus == OrderStatus.FAILED) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    // Add more specific rules if needed
  }

  private boolean isPointsAwarded(Order order) {
    List<RewardPointTransaction> transactions = order.getRewardPointTransactions();
    if (transactions == null || transactions.isEmpty()) {
      return false;
    }

    return transactions.stream()
        .anyMatch(
            tx -> tx.getPointChange() > 0 && RewardPointTransactionType.EARN.equals(tx.getType()));
  }
}
