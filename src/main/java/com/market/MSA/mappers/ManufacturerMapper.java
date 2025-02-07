package com.market.MSA.mappers;

import com.market.MSA.models.Manufacturer;
import com.market.MSA.requests.ManufacturerRequest;
import com.market.MSA.responses.ManufacturerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ManufacturerMapper {
  Manufacturer toManufacturer(ManufacturerRequest request);

  ManufacturerResponse toManufacturerResponse(Manufacturer manufacturer);

  @Mapping(target = "manufacturerId", ignore = true)
  void updateManufacturerFromRequest(
      ManufacturerRequest request, @MappingTarget Manufacturer manufacturer);
}
