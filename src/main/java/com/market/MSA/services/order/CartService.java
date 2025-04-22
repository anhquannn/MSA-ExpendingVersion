package com.market.MSA.services.order;

import com.market.MSA.constants.CartStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.CartMapper;
import com.market.MSA.models.order.Cart;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.order.CartRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.order.CartRequest;
import com.market.MSA.responses.order.CartResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
  final EntityFinderService entityFinderService;

  final CartRepository cartRepository;
  final UserRepository userRepository;

  final CartMapper cartMapper;

  public CartResponse createCart(CartRequest request) {
    Cart cart = cartMapper.toCartItem(request);
    cart.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.CART_NOT_FOUND));
    cart.setStatus(CartStatus.CART_STATUS_1.getStatus());
    cart = cartRepository.save(cart);
    return cartMapper.toCartResponse(cart);
  }

  public CartResponse updateCart(Long cartId, CartRequest request) {
    Optional<Cart> existingCart = cartRepository.findById(cartId);
    if (existingCart.isPresent()) {
      Cart cart = existingCart.get();
      cartMapper.updateCartFromRequest(request, cart);
      cart.setUser(
          entityFinderService.findByIdOrThrow(
              userRepository, request.getUserId(), ErrorCode.CART_NOT_FOUND));
      cartRepository.save(cart);
      return cartMapper.toCartResponse(cart);
    }
    throw new AppException(ErrorCode.CART_NOT_FOUND);
  }

  public boolean deleteCart(Long cartId) {
    if (!cartRepository.existsById(cartId)) {
      throw new AppException(ErrorCode.CART_NOT_FOUND);
    }
    cartRepository.deleteById(cartId);
    return true;
  }

  public CartResponse getCartById(Long cartId) {
    return cartRepository
        .findById(cartId)
        .map(cartMapper::toCartResponse)
        .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
  }

  public CartResponse getOrCreateCartForUser(Long userId) {
    Optional<Cart> existingCart = cartRepository.findByUser_UserId(userId);
    if (existingCart.isPresent()) {
      return cartMapper.toCartResponse(existingCart.get());
    }

    // Fetch user entity first
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    Cart newCart =
        Cart.builder()
            .user(user) // Use the fetched User entity
            .status(CartStatus.CART_STATUS_1.getStatus())
            .build();

    cartRepository.save(newCart);
    return cartMapper.toCartResponse(newCart);
  }
}
