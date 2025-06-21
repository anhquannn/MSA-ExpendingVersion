package com.market.MSA.mappers.product;

import com.market.MSA.models.product.Branch;
import com.market.MSA.requests.product.BranchRequest;
import com.market.MSA.responses.product.BranchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface BranchMapper {
  Branch toBranch(BranchRequest request);

  @Mapping(target = "inventory", ignore = true)
  BranchResponse toBranchResponse(Branch branch);

  @Mapping(target = "branchId", ignore = true)
  void updateBranchFromRequest(BranchRequest request, @MappingTarget Branch branch);
}
