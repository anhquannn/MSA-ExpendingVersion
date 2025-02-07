package com.market.MSA.mappers;

import com.market.MSA.models.Branch;
import com.market.MSA.requests.BranchRequest;
import com.market.MSA.responses.BranchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BranchMapper {
  Branch toBranch(BranchRequest request);

  BranchResponse toBranchResponse(Branch branch);

  @Mapping(target = "branchId", ignore = true)
  void updateBranchFromRequest(BranchRequest request, @MappingTarget Branch branch);
}
