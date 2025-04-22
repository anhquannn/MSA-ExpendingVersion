package com.market.MSA.mappers.order;

import com.market.MSA.models.order.CartItem;
import com.market.MSA.requests.order.CartItemRequest;
import com.market.MSA.responses.order.CartItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface CartItemMapper {
  CartItem toCartItem(CartItemRequest request);

  CartItemResponse toCartItemResponse(CartItem cartItem);

  @Mapping(target = "cartItemId", ignore = true)
  void updateCartItemFromRequest(CartItemRequest request, @MappingTarget CartItem cartItem);
}
