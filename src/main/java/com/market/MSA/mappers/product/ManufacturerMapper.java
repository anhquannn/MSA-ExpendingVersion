package com.market.MSA.mappers.product;

import com.market.MSA.models.product.Manufacturer;
import com.market.MSA.requests.product.ManufacturerRequest;
import com.market.MSA.responses.product.ManufacturerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface ManufacturerMapper {
  Manufacturer toManufacturer(ManufacturerRequest request);

  ManufacturerResponse toManufacturerResponse(Manufacturer manufacturer);

  @Mapping(target = "manufacturerId", ignore = true)
  void updateManufacturerFromRequest(
      ManufacturerRequest request, @MappingTarget Manufacturer manufacturer);
}
