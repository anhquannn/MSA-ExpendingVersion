package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.SupplierMapper;
import com.market.MSA.models.product.Supplier;
import com.market.MSA.repositories.product.SupplierRepository;
import com.market.MSA.requests.product.SupplierRequest;
import com.market.MSA.responses.product.SupplierResponse;
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
public class SupplierService {
  final SupplierRepository supplierRepository;
  final SupplierMapper supplierMapper;

  @Transactional
  public SupplierResponse createSupplier(SupplierRequest request) {
    Supplier supplier = supplierMapper.toSupplier(request);
    supplier = supplierRepository.save(supplier);
    return supplierMapper.toSupplierResponse(supplier);
  }

  @Transactional
  public SupplierResponse updateSupplier(Long supplierId, SupplierRequest request) {
    Supplier supplier =
        supplierRepository
            .findById(supplierId)
            .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

    supplierMapper.updateSupplierFromRequest(request, supplier);

    supplier = supplierRepository.save(supplier);
    return supplierMapper.toSupplierResponse(supplier);
  }

  @Transactional
  public boolean deleteSupplier(Long supplierId) {
    if (!supplierRepository.existsById(supplierId)) {
      throw new AppException(ErrorCode.SUPPLIER_NOT_FOUND);
    }
    supplierRepository.deleteById(supplierId);
    return true;
  }

  public SupplierResponse getSupplierById(Long supplierId) {
    Supplier supplier =
        supplierRepository
            .findById(supplierId)
            .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
    return supplierMapper.toSupplierResponse(supplier);
  }

  public SupplierResponse getSupplierByName(String name) {
    Supplier supplier =
        supplierRepository
            .findByName(name)
            .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
    return supplierMapper.toSupplierResponse(supplier);
  }

  public List<SupplierResponse> getAllSuppliers() {
    return supplierRepository.findAll().stream()
        .map(supplierMapper::toSupplierResponse)
        .collect(Collectors.toList());
  }
}
