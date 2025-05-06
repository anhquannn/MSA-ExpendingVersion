package com.market.MSA.mappers.order;

import com.market.MSA.mappers.product.ProductMapper;
import com.market.MSA.models.order.CartItem;
import com.market.MSA.requests.order.CartItemRequest;
import com.market.MSA.responses.order.CartItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {ProductMapper.class, CartMapper.class})
@Component
public interface CartItemMapper {
  CartItem toCartItem(CartItemRequest request);

  @Mapping(target = "cartItemId", source = "cartItemId")
  @Mapping(target = "isSelected", source = "selected")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "quantity", source = "quantity")
  @Mapping(target = "product.orderDetails", ignore = true)
  @Mapping(target = "product", source = "product")
  @Mapping(target = "cart", source = "cart")
  CartItemResponse toCartItemResponse(CartItem cartItem);

  @Mapping(target = "cartItemId", ignore = true)
  void updateCartItemFromRequest(CartItemRequest request, @MappingTarget CartItem cartItem);
}
