package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.ManufacturerMapper;
import com.market.MSA.models.product.Manufacturer;
import com.market.MSA.repositories.product.ManufacturerRepository;
import com.market.MSA.requests.product.ManufacturerRequest;
import com.market.MSA.responses.product.ManufacturerResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ManufacturerService {
  final ManufacturerRepository manufacturerRepository;
  final ManufacturerMapper manufacturerMapper;

  @Transactional
  public ManufacturerResponse createManufacturer(ManufacturerRequest request) {
    Manufacturer manufacturer = manufacturerMapper.toManufacturer(request);
    manufacturer = manufacturerRepository.save(manufacturer);
    return manufacturerMapper.toManufacturerResponse(manufacturer);
  }

  @Transactional
  public ManufacturerResponse updateManufacturer(Long manufacturerId, ManufacturerRequest request) {
    Manufacturer manufacturer =
        manufacturerRepository
            .findById(manufacturerId)
            .orElseThrow(() -> new AppException(ErrorCode.MANUFACTURER_NOT_FOUND));

    manufacturerMapper.updateManufacturerFromRequest(request, manufacturer);

    manufacturer = manufacturerRepository.save(manufacturer);
    return manufacturerMapper.toManufacturerResponse(manufacturer);
  }

  @Transactional
  public boolean deleteManufacturer(Long manufacturerId) {
    if (!manufacturerRepository.existsById(manufacturerId)) {
      throw new IllegalArgumentException("Manufacturer not found");
    }
    manufacturerRepository.deleteById(manufacturerId);
    return true;
  }

  public ManufacturerResponse getManufacturerById(Long manufacturerId) {
    Manufacturer manufacturer =
        manufacturerRepository
            .findById(manufacturerId)
            .orElseThrow(() -> new AppException(ErrorCode.MANUFACTURER_NOT_FOUND));
    return manufacturerMapper.toManufacturerResponse(manufacturer);
  }

  public ManufacturerResponse getManufacturerByName(String name) {
    Manufacturer manufacturer =
        manufacturerRepository
            .findByName(name)
            .orElseThrow(() -> new AppException(ErrorCode.MANUFACTURER_NOT_FOUND));
    return manufacturerMapper.toManufacturerResponse(manufacturer);
  }

  public List<ManufacturerResponse> getAllManufacturers() {
    return manufacturerRepository.findAll().stream()
        .map(manufacturerMapper::toManufacturerResponse)
        .collect(Collectors.toList());
  }
}
