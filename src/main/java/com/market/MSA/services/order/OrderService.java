package com.market.MSA.services.order;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.constants.PromocodeStatus;
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
import com.market.MSA.repositories.order.CartRepository;
import com.market.MSA.repositories.order.OrderDetailRepository;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.product.BranchRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.order.OrderRequest;
import com.market.MSA.responses.order.CartItemResponse;
import com.market.MSA.responses.order.CartResponse;
import com.market.MSA.responses.order.OrderResponse;
import com.market.MSA.responses.order.PromoCodeResponse;
import com.market.MSA.services.others.NotificationService;
import com.market.MSA.services.product.BranchService;
import com.market.MSA.services.product.InventoryProductService;
import com.market.MSA.services.product.ProductService;
import com.market.MSA.services.user.EmailService;
import jakarta.mail.MessagingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  final BranchService branchService;
  final NotificationService notificationService;

  @Transactional
  public OrderResponse createOrder(Long userId, Long branchId, Long cartId, List<String> promoCodes)
      throws MessagingException {
    // Tính toán tổng tiền và giảm giá
    OrderResponse orderSummary = calculateOrderSummary(userId, cartId, promoCodes);
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

    Date orderDate = new Date();

    // Tạo đơn hàng mới
    Order order =
        Order.builder()
            .user(user)
            .cart(cart)
            .orderDate(orderDate)
            .branch(branch)
            .grandTotal(grandTotal)
            .status(OrderStatus.ORDER_STATUS_1.getStatus())
            .build();

    order = orderRepository.save(order);

    // Nếu có mã giảm giá, lưu vào bảng OrderPromoCode
    if (promoCodes != null && !promoCodes.isEmpty()) {
      if (order.getPromoCodes() == null) {
        order.setPromoCodes(new ArrayList<>()); // Khởi tạo nếu bị null
      }

      for (String promoCode : promoCodes) {
        PromoCode promo = promoCodeService.findPromoCodeByCode(promoCode);
        if (!promo.getStatus().equals(PromocodeStatus.PROMO_CODE_STATUS_2.getStatus())) {
          order.getPromoCodes().add(promo);
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
              .unitPrice(product.getCurrentPrice())
              .totalPrice(cartItem.getQuantity() * product.getCurrentPrice())
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

    // Gửi email xác nhận đơn hàng
    sendOrderDetails(order, user.getEmail());

    // Send notification
    notificationService.sendOrderCreatedNotification(order.getOrderId());

    return orderMapper.toOrderResponse(order);
  }

  public OrderResponse calculateOrderSummary(Long userId, Long cartId, List<String> promoCodes) {
    CartResponse cart = cartService.getCartById(cartId);

    if (cart == null || !cart.getUser().getUserId().equals(userId)) {
      throw new AppException(ErrorCode.CART_NOT_FOUND);
    }

    double totalCost = cartItemService.calculateCartTotal(cartId);
    double discount = 0.0;
    double grandTotal = totalCost;

    if (promoCodes != null && !promoCodes.isEmpty()) {
      for (String promoCode : promoCodes) {
        PromoCodeResponse promo = promoCodeService.getPromoCodeByCode(promoCode);
        if (totalCost >= promo.getMinimumOrderValue()
            && !promo.getStatus().equals(PromocodeStatus.PROMO_CODE_STATUS_2.getStatus())) {
          double currentDiscount = totalCost * (promo.getDiscountPercentage() / 100);
          discount += currentDiscount;
          grandTotal -= currentDiscount;
        }
      }
    }

    if (grandTotal < 0) {
      throw new AppException(ErrorCode.WRONG_PROMO_CODE);
    }

    return OrderResponse.builder()
        .totalCost(totalCost)
        .discount(discount)
        .grandTotal(grandTotal)
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

  public boolean deleteOrder(Long orderId) {
    if (!orderRepository.existsById(orderId)) {
      throw new AppException(ErrorCode.ORDER_NOT_FOUND);
    }
    orderRepository.deleteById(orderId);
    return true;
  }

  @Transactional
  public OrderResponse getOrderById(Long orderId) {
    return orderRepository
        .findById(orderId)
        .map(orderMapper::toOrderResponse)
        .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
  }

  public List<OrderResponse> searchOrderByPhoneNumber(String phoneNumber, int page, int pageSize) {
    Page<Order> orderPage =
        orderRepository.findByUser_PhoneNumber(phoneNumber, PageRequest.of(page, pageSize));
    List<Order> orders = orderPage.getContent(); // Get the content as a List
    return orders.stream().map(orderMapper::toOrderResponse).collect(Collectors.toList());
  }

  @Cacheable(
      value = "orders",
      key = "'all_' + #page + '_' + #size + '_' + #sortBy + '_' + #sortDirection")
  public Page<OrderResponse> getAllOrders(int page, int size, String sortBy, String sortDirection) {
    // Create pageable with sorting
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

    // Get all orders with pagination
    Page<Order> orders = orderRepository.findAll(pageable);

    // Convert to response DTOs
    return orders.map(orderMapper::toOrderResponse);
  }

  public List<OrderResponse> getOrdersByUserIDWithStatus(
      Long userId, String status, int page, int size) {
    Page<Order> orderPage =
        orderRepository.findByUser_UserIdAndStatus(userId, status, PageRequest.of(page, size));
    List<Order> orders = orderPage.getContent(); // Get the content as a List
    return orders.stream().map(orderMapper::toOrderResponse).collect(Collectors.toList());
  }

  public void sendOrderDetails(Order order, String userEmail) throws MessagingException {
    // Tạo nội dung email cảm ơn và xác nhận đơn hàng
    StringBuilder emailBody = new StringBuilder();
    emailBody.append(
        String.format(
            "Thank you for your order\n\nOrder Confirmation\n\n-------------------------------------\nOrderID: %d\nGrand Total: %.2f\n",
            order.getOrderId(), order.getGrandTotal()));

    // Lấy thông tin chi tiết đơn hàng
    Iterable<OrderDetail> orderDetails =
        orderDetailService.findOrderDetailsByOrderId(order.getOrderId());

    // Thêm thông tin sản phẩm vào nội dung email
    for (OrderDetail detail : orderDetails) {
      Product product = detail.getProduct(); // Assuming you have a method to get Product info from
      // OrderDetail
      emailBody.append(
          String.format(
              "\n-------------------------------------\nProduct: %s\nQuantity: %d\nUnit Price: %.2f\n\n",
              product.getName(), detail.getQuantity(), detail.getUnitPrice()));
    }

    // Gửi email với tiêu đề "Xác nhận đơn hàng"
    String subject = "Order Confirmation - Your Order Details";
    emailService.sendEmail(userEmail, subject, emailBody.toString());
  }

  @Cacheable(
      value = "orders",
      key =
          "'branch_' + #branchId + '_' + #page + '_' + #size + '_' + #sortBy + '_' + #sortDirection")
  public Page<OrderResponse> getOrdersByBranchId(
      Long branchId, int page, int size, String sortBy, String sortDirection) {
    // Check if branch exists
    branchService.getBranchById(branchId);

    // Create pageable with sorting
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());

    // Handle special sorting by user phone number
    if ("user-phoneNumber".equals(sortBy)) {
      // Create a custom sort that joins with the user table
      Sort sort = Sort.by(direction, "user.phoneNumber");
      Pageable pageable = PageRequest.of(page, size, sort);

      // Use a custom query to join with user table
      Page<Order> orders = orderRepository.findByBranch_BranchIdWithUserSort(branchId, pageable);
      return orders.map(orderMapper::toOrderResponse);
    } else {
      // Validate sort field
      validateSortField(sortBy);

      // Standard sorting for order fields (orderDate, grandTotal, status)
      Sort sort = Sort.by(direction, sortBy);
      Pageable pageable = PageRequest.of(page, size, sort);
      Page<Order> orders = orderRepository.findByBranch_BranchId(branchId, pageable);
      return orders.map(orderMapper::toOrderResponse);
    }
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
  public Map<String, Double> getRevenueStatistics(int year, int month, Long branchId, Long userId) {
    Map<String, Double> statistics = new HashMap<>();

    // Calculate total revenue
    statistics.put("totalMonthlyRevenue", calculateMonthlyRevenue(year, month));
    statistics.put("totalYearlyRevenue", calculateYearlyRevenue(year));

    // Calculate branch revenue if branchId is provided
    if (branchId != null) {
      statistics.put(
          "branchMonthlyRevenue", calculateMonthlyRevenueByBranch(year, month, branchId));
      statistics.put("branchYearlyRevenue", calculateYearlyRevenueByBranch(year, branchId));
    }

    // Calculate user revenue if userId is provided
    if (userId != null) {
      statistics.put("userMonthlyRevenue", calculateMonthlyRevenueByUser(year, month, userId));
      statistics.put("userYearlyRevenue", calculateYearlyRevenueByUser(year, userId));
    }

    // Calculate combined branch and user revenue if both are provided
    if (branchId != null && userId != null) {
      statistics.put(
          "branchUserMonthlyRevenue",
          calculateMonthlyRevenueByBranchAndUser(year, month, branchId, userId));
      statistics.put(
          "branchUserYearlyRevenue", calculateYearlyRevenueByBranchAndUser(year, branchId, userId));
    }

    return statistics;
  }
}
