package com.market.MSA.mappers;

import com.market.MSA.models.CartItem;
import com.market.MSA.requests.CartItemRequest;
import com.market.MSA.responses.CartItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
  CartItem toCartItem(CartItemRequest request);

  CartItemResponse toCartItemResponse(CartItem cartItem);

  @Mapping(target = "cartItemId", ignore = true)
  void updateCartItemFromRequest(CartItemRequest request, @MappingTarget CartItem cartItem);
}
