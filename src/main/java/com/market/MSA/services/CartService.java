package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.CartMapper;
import com.market.MSA.models.Cart;
import com.market.MSA.models.User;
import com.market.MSA.repositories.CartRepository;
import com.market.MSA.repositories.UserRepository;
import com.market.MSA.requests.CartRequest;
import com.market.MSA.responses.CartResponse;
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
  CartRepository cartRepository;
  CartMapper cartMapper;
  UserRepository userRepository;

  public CartResponse createCart(CartRequest request) {
    Cart cart = cartMapper.toCartItem(request);
    cart = cartRepository.save(cart);
    return cartMapper.toCartResponse(cart);
  }

  public CartResponse updateCart(Long cartId, CartRequest request) {
    Optional<Cart> existingCart = cartRepository.findById(cartId);
    if (existingCart.isPresent()) {
      Cart cart = existingCart.get();
      cartMapper.updateCartFromRequest(request, cart);
      cartRepository.save(cart);
      return cartMapper.toCartResponse(cart);
    }
    throw new AppException(ErrorCode.CART_NOT_FOUND);
  }

  public void deleteCart(Long cartId) {
    cartRepository.deleteById(cartId);
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
            .status("active")
            .build();

    cartRepository.save(newCart);
    return cartMapper.toCartResponse(newCart);
  }
}
