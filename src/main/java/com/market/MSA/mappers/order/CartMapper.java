package com.market.MSA.mappers.order;

import com.market.MSA.mappers.user.UserMapper;
import com.market.MSA.models.order.Cart;
import com.market.MSA.requests.order.CartRequest;
import com.market.MSA.responses.order.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class})
@Component
public interface CartMapper {
  Cart toCartItem(CartRequest request);

  @Mapping(target = "user", source = "user")
  CartResponse toCartResponse(Cart cart);

  @Mapping(target = "cartId", ignore = true)
  void updateCartFromRequest(CartRequest request, @MappingTarget Cart cart);
}
