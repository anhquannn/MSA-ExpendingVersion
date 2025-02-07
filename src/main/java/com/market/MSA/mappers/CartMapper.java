package com.market.MSA.mappers;

import com.market.MSA.models.Cart;
import com.market.MSA.requests.CartRequest;
import com.market.MSA.responses.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface CartMapper {
  Cart toCartItem(CartRequest request);

  CartResponse toCartResponse(Cart Cart);

  @Mapping(target = "cartId", ignore = true)
  void updateCartFromRequest(CartRequest request, @MappingTarget Cart cart);
}
