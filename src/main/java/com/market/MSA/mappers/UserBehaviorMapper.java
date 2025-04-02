package com.market.MSA.mappers;

import com.market.MSA.models.UserBehavior;
import com.market.MSA.requests.UserBehaviorRequest;
import com.market.MSA.responses.UserBehaviorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserBehaviorMapper {
  UserBehavior toUserBehavior(UserBehaviorRequest request);

  UserBehaviorResponse toUserBehaviorResponse(UserBehavior userBehavior);

  @Mapping(target = "userBehaviorId", ignore = true)
  void updateUserBehaviorFromRequest(
      UserBehaviorRequest request, @MappingTarget UserBehavior userBehavior);
}
