package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.OrderMapper;
import com.market.MSA.models.Cart;
import com.market.MSA.models.Order;
import com.market.MSA.models.OrderDetail;
import com.market.MSA.models.Product;
import com.market.MSA.models.PromoCode;
import com.market.MSA.models.User;
import com.market.MSA.repositories.CartRepository;
import com.market.MSA.repositories.OrderDetailRepository;
import com.market.MSA.repositories.OrderRepository;
import com.market.MSA.repositories.UserRepository;
import com.market.MSA.requests.OrderRequest;
import com.market.MSA.responses.CartItemResponse;
import com.market.MSA.responses.CartResponse;
import com.market.MSA.responses.OrderResponse;
import com.market.MSA.responses.PromoCodeResponse;
import jakarta.mail.MessagingException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
  OrderRepository orderRepository;
  CartService cartService;
  CartRepository cartRepository;
  CartItemService cartItemService;
  PromoCodeService promoCodeService;
  UserRepository userRepository;
  EmailService emailService;
  ProductService productService;
  OrderDetailRepository orderDetailRepository;
  OrderDetailService orderDetailService;
  OrderMapper orderMapper;

  public OrderResponse createOrder(Long userId, Long cartId, String promoCode)
      throws MessagingException {
    // Tính toán tổng tiền đơn hàng và giảm giá
    OrderResponse orderSummary = calculateOrderSummary(userId, cartId, promoCode);
    double discount = orderSummary.getDiscount();
    double grandTotal = orderSummary.getGrandTotal();

    // Lấy thông tin người dùng
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

    // Tạo đơn hàng mới
    Order order =
        Order.builder().user(user).cart(cart).grandTotal(grandTotal).status("PENDING").build();

    order = orderRepository.save(order);

    // Nếu có mã giảm giá, lưu vào bảng OrderPromoCode
    if (discount > 0) {
      PromoCode promo = promoCodeService.findPromoCodeByCode(promoCode);
      order.getPromoCodes().add(promo);
    }

    // Lấy danh sách sản phẩm từ giỏ hàng
    List<CartItemResponse> cartItems = cartItemService.getCartItemsByCartId(cartId);

    for (CartItemResponse cartItem : cartItems) {
      // Lấy Product từ database dựa vào productId
      Product product = productService.findProductById(cartItem.getProduct().getProductId());

      // Tạo chi tiết đơn hàng
      OrderDetail orderDetail =
          OrderDetail.builder()
              .order(order)
              .product(product) // Dùng Product thay vì
              // ProductResponse
              .quantity(cartItem.getQuantity())
              .unitPrice(product.getPrice())
              .totalPrice(cartItem.getQuantity() * product.getPrice())
              .build();

      orderDetailRepository.save(orderDetail);

      // Cập nhật số lượng tồn kho sản phẩm
      productService.updateStockNumber(product.getProductId(), cartItem.getQuantity());
    }

    // Xóa giỏ hàng sau khi đặt hàng
    cartItemService.clearCart(cartId);

    // Gửi email xác nhận đơn hàng
    sendOrderDetails(order, user.getEmail());

    return orderMapper.toOrderResponse(order);
  }

  public OrderResponse calculateOrderSummary(Long userId, Long cartId, String promoCode) {
    CartResponse cart = cartService.getCartById(cartId);

    if (cart == null) {
      throw new AppException(ErrorCode.CART_NOT_FOUND);
    }

    double totalCost = cartItemService.calculateCartTotal(cartId);
    double discount = 0.0;
    double grandTotal = totalCost;

    if (!promoCode.isEmpty()) {
      PromoCodeResponse promo = promoCodeService.getPromoCodeByCode(promoCode);
      if (totalCost >= promo.getMinimumOrderValue()) {
        discount = totalCost * (promo.getDiscountPercentage() / 100);
        grandTotal -= discount;
      }
    }
    return OrderResponse.builder()
        .totalCost(totalCost)
        .discount(discount)
        .grandTotal(grandTotal)
        .build();
  }

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

  public void deleteOrder(Long orderId) {
    if (!orderRepository.existsById(orderId)) {
      throw new AppException(ErrorCode.ORDER_NOT_FOUND);
    }
    orderRepository.deleteById(orderId);
  }

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

  public List<OrderResponse> getAllOrders(int page, int pageSize) {
    List<Order> orders = orderRepository.findAll(PageRequest.of(page, pageSize)).getContent();
    return orders.stream().map(orderMapper::toOrderResponse).collect(Collectors.toList());
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
}
