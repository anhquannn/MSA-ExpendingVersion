package com.market.MSA.services.order;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.dtos.order.OrderItemDto;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.OrderMapper;
import com.market.MSA.models.order.Cart;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.order.OrderDetail;
import com.market.MSA.models.order.PromoCode;
import com.market.MSA.models.product.Branch;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.order.CartItemRepository;
import com.market.MSA.repositories.order.CartRepository;
import com.market.MSA.repositories.order.OrderDetailRepository;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.product.BranchRepository;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.OrderFilterRequest;
import com.market.MSA.requests.order.OrderRequest;
import com.market.MSA.requests.order.PromoCodeUsageRequest;
import com.market.MSA.responses.order.*;
import com.market.MSA.responses.order.BranchRevenueResponse;
import com.market.MSA.services.others.EmailService;
import com.market.MSA.services.others.NotificationService;
import com.market.MSA.services.others.PricingService;
import com.market.MSA.services.product.InventoryProductService;
import com.market.MSA.services.product.ProductService;
import com.market.MSA.services.user.RewardPointService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
  final CartItemRepository cartItemRepository;
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
  final PricingService pricingService;

  @Transactional
  public OrderResponse createOrder(
      Long userId,
      Long branchId,
      Long userAddressId,
      Long cartId,
      List<String> promoCodes,
      Double usePoints) {

    // 1. Lấy các sản phẩm trong giỏ hàng (bao gồm cả sản phẩm khuyến mãi miễn phí).
    List<CartItemResponse> cartItemHierarchical = cartItemService.getCartItemsByCartId(cartId);
    // Chuyển đổi cấu trúc cây -> danh sách phẳng để xử lý dễ dàng hơn.
    List<CartItemResponse> cartItems = new ArrayList<>();
    for (CartItemResponse ci : cartItemHierarchical) {
      cartItems.add(ci);
      if (ci.getFreeItems() != null && !ci.getFreeItems().isEmpty()) {
        cartItems.addAll(ci.getFreeItems());
      }
    }

    // Danh sách OrderItemDto dùng cho pricing / validation.
    List<OrderItemDto> orderItems =
        cartItems.stream()
            .map(ci -> new OrderItemDto(ci.getProduct().getProductId(), ci.getQuantity()))
            .toList();

    // 2. Tính tổng giá trị đơn hàng tạm thời để kiểm tra mã giảm giá.
    double totalCost =
        cartItems.stream()
            .mapToDouble(ci -> ci.isFreeItem() ? 0 : ci.getQuantity() * ci.getProduct().getPrice())
            .sum();

    // 3. KIỂM TRA MÃ GIẢM GIÁ TRƯỚC TIÊN - Đây là bước quan trọng được thêm vào.
    // Việc này đảm bảo mã giảm giá hợp lệ trước khi thực hiện các logic phức tạp khác.
    double discount =
        pricingService.validateAndCalculateDiscount(orderItems, promoCodes, userId, totalCost);

    // 4. Nếu mã giảm giá hợp lệ, tiếp tục tính toán tóm tắt đơn hàng (phí ship, tổng cuối...).
    OrderSummaryResponse orderSummary =
        calculateOrderSummary(branchId, userAddressId, userId, cartId, promoCodes, usePoints);
    double grandTotal = orderSummary.getGrandTotal();

    // 5. Xử lý việc sử dụng điểm thưởng.
    if (usePoints > 0) {
      // Kiểm tra xem người dùng có đủ điểm không.
      double availablePoints = rewardPointService.getAvailablePoints(userId);
      if (usePoints > availablePoints) {
        throw new AppException(ErrorCode.INSUFFICIENT_POINTS);
      }
      // Trừ số điểm được sử dụng vào tổng tiền, đảm bảo tổng tiền không âm.
      grandTotal = Math.max(0, grandTotal - usePoints);
    }

    // 6. Tìm các thực thể cần thiết từ CSDL (User, Cart, Branch).
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

    // 7. Tạo đối tượng 'Order' chính.
    Order order =
        Order.builder()
            .user(user)
            .cart(cart)
            .orderDate(LocalDateTime.now())
            .branch(branch)
            .grandTotal(grandTotal)
            .status(OrderStatus.PENDING) // Trạng thái ban đầu là "Chờ xử lý".
            .build();

    // Lưu đơn hàng vào CSDL để lấy được 'orderId'.
    order = orderRepository.save(order);

    // 8. Xử lý các mã giảm giá đã được áp dụng.
    if (promoCodes != null && !promoCodes.isEmpty()) {
      if (order.getPromoCodes() == null) {
        order.setPromoCodes(new ArrayList<>());
      }
      // Duyệt qua từng mã.
      for (String promoCode : promoCodes) {
        // Kiểm tra xem người dùng đã sử dụng mã này trước đây chưa.
        if (promoCodeService.hasUserUsedPromoCode(
            userId, promoCodeService.findPromoCodeByCode(promoCode).getPromoCodeId())) {
          throw new AppException(ErrorCode.PROMO_CODE_ALREADY_USED);
        }
        // Tìm mã giảm giá và kiểm tra trạng thái.
        PromoCode promo = promoCodeService.findPromoCodeByCode(promoCode);
        if (!promo.getStatus().equals(PromocodeStatus.EXPIRED)) {
          // Thêm mã vào danh sách mã của đơn hàng.
          order.getPromoCodes().add(promo);
          // Ghi lại lịch sử sử dụng mã giảm giá.
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

    // 9. Xử lý chi tiết đơn hàng (từng sản phẩm) – tối ưu: bulk fetch & batch save
    // Lấy tất cả productId duy nhất
    List<Long> productIds =
        cartItems.stream().map(ci -> ci.getProduct().getProductId()).distinct().toList();

    // Bulk fetch Product entities một lần
    Map<Long, Product> productMap =
        productService
            .getProductRepository() // thêm phương thức getter trong ProductService trả về repo
            .findAllById(productIds)
            .stream()
            .collect(Collectors.toMap(Product::getProductId, p -> p));

    List<OrderDetail> orderDetailsBatch = new ArrayList<>();

    for (CartItemResponse cartItem : cartItems) {
      Product product = productMap.get(cartItem.getProduct().getProductId());
      if (product == null) {
        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
      }
      boolean freeItem = cartItem.isFreeItem();
      double unitPrice = freeItem ? 0 : product.getPrice();

      OrderDetail orderDetail =
          OrderDetail.builder()
              .order(order)
              .name(product.getName())
              .product(product)
              .quantity(cartItem.getQuantity())
              .unitPrice(unitPrice)
              .status(OrderStatus.PENDING)
              .totalPrice(cartItem.getQuantity() * unitPrice)
              .isFreeItem(freeItem)
              .build();
      orderDetailsBatch.add(orderDetail);

      // Cập nhật tồn kho & doanh thu (có thể song song sau này)
      inventoryProductService.updateInventoryProduct(
          branchId, product.getProductId(), cartItem.getQuantity());
      productService.updateTotalRevenue(product.getProductId(), cartItem.getQuantity());
    }

    // Batch insert OrderDetail
    orderDetailRepository.saveAll(orderDetailsBatch);

    // 10. Xóa các sản phẩm đã đặt hàng khỏi giỏ hàng.
    cartItemService.clearCart(cartId);

    // 11. Nếu người dùng đã sử dụng điểm, thực hiện trừ điểm và ghi lại giao dịch điểm thưởng.
    if (usePoints > 0) {
      rewardPointService.redeemPoints(
          userId,
          order.getOrderId(),
          usePoints,
          "Redeemed points for order #" + order.getOrderId());
    }

    // 12. Gửi thông báo xác nhận đơn hàng đã được tạo.
    notificationService.sendOrderCreatedNotification(order.getOrderId());

    // 13. Map đối tượng 'Order' sang 'OrderResponse' và trả về cho client.
    return orderMapper.toOrderResponse(order);
  }

  @Transactional
  public OrderSummaryResponse calculateOrderSummary(
      Long branchId,
      Long userAddressId,
      Long userId,
      Long cartId,
      List<String> promoCodes,
      Double usePoints) {
    // 1. Lấy thông tin giỏ hàng của người dùng.
    CartResponse cart = cartService.getCartById(cartId);
    if (cart == null || !cart.getUser().getUserId().equals(userId)) {
      throw new AppException(ErrorCode.CART_NOT_FOUND);
    }
    // Chuyển đổi các mục trong giỏ hàng thành danh sách 'OrderItemDto'.
    List<OrderItemDto> items =
        cartItemService.getCartItemsByCartId(cartId).stream()
            .map(ci -> new OrderItemDto(ci.getProduct().getProductId(), ci.getQuantity()))
            .toList();

    // 2. Ủy quyền cho 'pricingService' để tính toán tóm tắt đơn hàng ban đầu
    // (tổng tiền hàng, giảm giá từ promo code, phí vận chuyển).
    OrderSummaryResponse summary =
        pricingService.calculateSummary(branchId, userAddressId, userId, items, promoCodes);

    // 3. Lấy số điểm thưởng hiện có của người dùng.
    double availablePoints = rewardPointService.getAvailablePoints(userId);
    // Tính số điểm tối đa có thể sử dụng (không thể vượt quá tổng tiền đơn hàng).
    double maxUsablePoints = Math.min(availablePoints, summary.getGrandTotal());

    // 4. Xử lý nếu người dùng muốn sử dụng điểm.
    if (usePoints > 0) {
      // Kiểm tra lại xem có đủ điểm không.
      if (usePoints > availablePoints) {
        throw new AppException(ErrorCode.INSUFFICIENT_POINTS);
      }
      // Tính toán số tiền được giảm giá (không thể vượt quá tổng tiền).
      double discount = Math.min(usePoints, summary.getGrandTotal());
      summary.setGrandTotal(summary.getGrandTotal() - discount);
      summary.setUsedPoints(usePoints);
    }

    // 5. Gán thông tin điểm thưởng vào response để hiển thị cho người dùng.
    summary.setAvailablePoints(availablePoints);
    summary.setMaxUsablePoints(maxUsablePoints);

    // 6. Trả về đối tượng tóm tắt đơn hàng.
    return summary;
  }

  @Transactional(readOnly = true)
  public OrderSummaryResponse previewBuyAgain(
      Long oldOrderId, Long userAddressId, List<String> promoCodes, Double usePoints) {

    // 1. Tìm lại đơn hàng cũ dựa vào 'oldOrderId'.
    Order oldOrder =
        orderRepository
            .findById(oldOrderId)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

    // 2. Chức năng "Mua lại" chỉ áp dụng cho các đơn hàng đã hoàn thành.
    if (oldOrder.getStatus() != OrderStatus.COMPLETED) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    // 3. Lấy thông tin cần thiết từ đơn hàng cũ (userId, branchId).
    Long userId = oldOrder.getUser().getUserId();
    Long branchId = oldOrder.getBranch().getBranchId();

    // 4. Chuyển đổi các chi tiết của đơn hàng cũ thành danh sách 'OrderItemDto' để tính toán lại.
    List<OrderItemDto> items =
        oldOrder.getOrderDetails().stream()
            .map(od -> new OrderItemDto(od.getProduct().getProductId(), od.getQuantity()))
            .toList();

    // 5. Xác định mã giảm giá sẽ được áp dụng.
    List<String> effectivePromoCodes;
    if (promoCodes != null && !promoCodes.isEmpty()) {
      // Nếu người dùng cung cấp mã mới, sử dụng chúng.
      effectivePromoCodes = promoCodes;
    } else {
      // Nếu không, tái sử dụng các mã từ đơn hàng cũ (nếu có).
      effectivePromoCodes =
          oldOrder.getPromoCodes() == null
              ? List.of() // Trả về danh sách rỗng nếu không có mã
              : oldOrder.getPromoCodes().stream().map(PromoCode::getCode).toList();
    }

    // 6. Tính toán lại tóm tắt đơn hàng với giá cả và khuyến mãi hiện tại.
    // Các bước còn lại tương tự như hàm 'calculateOrderSummary'.
    OrderSummaryResponse summary =
        pricingService.calculateSummary(
            branchId, userAddressId, userId, items, effectivePromoCodes);

    // Lấy số điểm có thể sử dụng.
    double availablePoints = rewardPointService.getAvailablePoints(userId);
    double maxUsablePoints = Math.min(availablePoints, summary.getGrandTotal());

    // Trừ điểm nếu có sử dụng.
    if (usePoints > 0) {
      if (usePoints > availablePoints) {
        throw new AppException(ErrorCode.INSUFFICIENT_POINTS);
      }
      double discount = Math.min(usePoints, summary.getGrandTotal());
      summary.setGrandTotal(summary.getGrandTotal() - discount);
      summary.setUsedPoints(usePoints);
    }

    // Đặt số điểm có thể sử dụng vào response.
    summary.setAvailablePoints(availablePoints);
    summary.setMaxUsablePoints(maxUsablePoints);

    return summary;
  }

  @Transactional
  public OrderResponse buyAgain(
      Long oldOrderId, Long userAddressId, List<String> promoCodes, Double usePoints) {
    // 1. Tìm lại đơn hàng cũ, tương tự như hàm preview.
    Order oldOrder =
        orderRepository
            .findById(oldOrderId)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

    // 2. Kiểm tra xem đơn hàng cũ đã hoàn thành chưa.
    if (oldOrder.getStatus() != OrderStatus.COMPLETED) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    // 3. Lấy thông tin người dùng và chi nhánh từ đơn hàng cũ.
    Long userId = oldOrder.getUser().getUserId();
    Long branchId = oldOrder.getBranch().getBranchId();

    // 4. Chuẩn bị danh sách sản phẩm và tổng giá trị để kiểm tra mã giảm giá.
    List<OrderItemDto> orderItems =
        oldOrder.getOrderDetails().stream()
            .map(
                detail ->
                    new OrderItemDto(detail.getProduct().getProductId(), detail.getQuantity()))
            .toList();

    double totalCost =
        oldOrder.getOrderDetails().stream()
            .mapToDouble(detail -> detail.getQuantity() * detail.getProduct().getPrice())
            .sum();

    // 5. Xác định các mã giảm giá sẽ được áp dụng (tương tự hàm preview).
    List<String> effectivePromoCodes;
    if (promoCodes != null && !promoCodes.isEmpty()) {
      effectivePromoCodes = promoCodes;
    } else {
      effectivePromoCodes =
          oldOrder.getPromoCodes() == null
              ? List.of()
              : oldOrder.getPromoCodes().stream().map(PromoCode::getCode).toList();
    }

    // 6. KIỂM TRA TÍNH HỢP LỆ CỦA MÃ GIẢM GIÁ TRƯỚC TIÊN.
    if (!effectivePromoCodes.isEmpty()) {
      pricingService.validateAndCalculateDiscount(
          orderItems, effectivePromoCodes, userId, totalCost);
    }

    // 7. Nếu mọi thứ hợp lệ, lấy hoặc tạo giỏ hàng cho người dùng.
    CartResponse cartResponse = cartService.getOrCreateCartForUser(userId);
    Long cartId = cartResponse.getCartId();

    // 8. Thêm các sản phẩm từ đơn hàng cũ vào giỏ hàng hiện tại.
    // Chỉ thêm nếu sản phẩm đó chưa có trong giỏ.
    for (OrderDetail detail : oldOrder.getOrderDetails()) {
      Long productId = detail.getProduct().getProductId();
      if (cartItemRepository.findByCart_CartIdAndProduct_ProductId(cartId, productId).isEmpty()) {
        cartItemService.addToCart(userId, productId, branchId, detail.getQuantity());
      }
    }

    // 9. Gọi lại hàm 'createOrder' để thực hiện quy trình tạo đơn hàng mới
    // với giỏ hàng đã được cập nhật và các thông tin khuyến mãi.
    // Điều này giúp tái sử dụng logic và tránh lặp code.
    return createOrder(userId, branchId, userAddressId, cartId, effectivePromoCodes, usePoints);
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
  @Transactional(readOnly = true)
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
  @Transactional(readOnly = true)
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
      String subject = "Xác nhận đơn hàng - Chi tiết đơn hàng của bạn";
      String emailBody = buildOrderConfirmationEmail(order);
      emailService.sendEmail(userEmail, subject, emailBody);
    } catch (Exception ignored) {
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
					<h2>Cảm ơn bạn đã đặt hàng!</h2>
					<p>Đơn hàng #%d đã được xác nhận và đang được xử lý.</p>
					<p>Ngày đặt hàng: %s</p>
					<hr style="border: 1px solid #eee; margin: 20px 0;">
				""",
        order.getOrderId(), order.getOrderDate().toString());
  }

  private String createOrderSummary(Order order) {
    return String.format(
        """
				<div style="margin-bottom: 20px;">
					<h3>Tóm tắt đơn hàng</h3>
					<p><strong>Mã đơn hàng:</strong> %d</p>
					<p><strong>Trạng thái:</strong> %s</p>
					<p><strong>Tổng thanh toán:</strong> %.2f VNĐ</p>
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
							<h3>Sản phẩm trong đơn hàng</h3>
							<table style="width: 100%; border-collapse: collapse; margin: 20px 0;">
								<thead>
									<tr style="background-color: #f5f5f5;">
										<th style="padding: 10px; text-align: left; border-bottom: 1px solid #ddd;">Sản phẩm</th>
										<th style="padding: 10px; text-align: right; border-bottom: 1px solid #ddd;">Số lượng</th>
										<th style="padding: 10px; text-align: right; border-bottom: 1px solid #ddd;">Đơn giá</th>
										<th style="padding: 10px; text-align: right; border-bottom: 1px solid #ddd;">Thành tiền</th>
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
			<p>Cảm ơn bạn đã mua sắm tại Elosyia!</p>
			<p>Nếu bạn có bất kỳ câu hỏi nào về đơn hàng, vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi.</p>
			<p>Trân trọng,<br>Đội ngũ Elosyia</p>
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
  public List<BranchRevenueResponse> getBranchRevenueComparison(
      int year, int month, int periodMonths) {
    java.time.LocalDateTime end = java.time.LocalDateTime.of(year, month, 1, 0, 0).plusMonths(1);
    java.time.LocalDateTime start = end.minusMonths(periodMonths);
    List<Object[]> rows = orderDetailRepository.findRevenueByBranch(start, end);
    return rows.stream()
        .map(
            r ->
                BranchRevenueResponse.builder()
                    .branchId((Long) r[0])
                    .name((String) r[1])
                    .totalRevenue(((Number) r[2]).doubleValue())
                    .build())
        .toList();
  }

  @Transactional
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
                        .productId(((Number) row[0]).longValue())
                        .name((String) row[1])
                        .totalQuantity(((Number) row[2]).longValue())
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

    // Update order status for Order, DeliveryInfo and OrderDetails
    order.setStatus(updatedStatus);

    // Propagate to delivery info
    if (order.getDeliveryInfo() != null) {
      order.getDeliveryInfo().setStatus(updatedStatus);
    }

    // Propagate to each order detail
    if (order.getOrderDetails() != null) {
      order.getOrderDetails().forEach(od -> od.setStatus(updatedStatus));
    }

    order = orderRepository.saveAndFlush(order);

    return orderMapper.toOrderResponse(order);
  }

  private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
    // Disallow any transition once the order is in a terminal state
    if (currentStatus == OrderStatus.CANCELLED
        || currentStatus == OrderStatus.COMPLETED
        || currentStatus == OrderStatus.FAILED) {
      throw new AppException(ErrorCode.INVALID_STATUS);
    }

    // Only allow moving to DELIVERING from PENDING (COD) or PAID (VNPay)
    if (newStatus == OrderStatus.DELIVERING
        && !(currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.PAID)) {
      throw new AppException(ErrorCode.INVALID_STATUS);
    }

    // Only allow cancelling when the order is still PENDING or currently DELIVERING
    if (newStatus == OrderStatus.CANCELLED
        && !(currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.DELIVERING)) {
      throw new AppException(ErrorCode.INVALID_STATUS);
    }

    // Additional rules can be added here as business grows
  }

  @Transactional(readOnly = true)
  public List<TopCustomerResponse> getTopCustomers(
      int year, int month, Long branchId, int topPeriod, int topLimit) {
    java.time.LocalDateTime end = java.time.LocalDateTime.of(year, month, 1, 0, 0).plusMonths(1);
    java.time.LocalDateTime start = end.minusMonths(topPeriod);
    org.springframework.data.domain.Pageable pageable =
        org.springframework.data.domain.PageRequest.of(0, topLimit);
    List<Object[]> rows = orderRepository.findTopCustomers(branchId, start, end, pageable);
    return rows.stream()
        .map(
            r ->
                TopCustomerResponse.builder()
                    .customerId((Long) r[0])
                    .name((String) r[1])
                    .totalSpent(((Number) r[2]).doubleValue())
                    .build())
        .toList();
  }
}
