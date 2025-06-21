package com.market.MSA.mappers.product;

import com.market.MSA.models.product.Supplier;
import com.market.MSA.requests.product.SupplierRequest;
import com.market.MSA.responses.product.SupplierResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface SupplierMapper {
  Supplier toSupplier(SupplierRequest request);

  SupplierResponse toSupplierResponse(Supplier supplier);

  @Mapping(target = "supplierId", ignore = true)
  void updateSupplierFromRequest(SupplierRequest request, @MappingTarget Supplier supplier);
}
