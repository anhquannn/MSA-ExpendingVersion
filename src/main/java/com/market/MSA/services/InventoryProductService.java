package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.InventoryProductMapper;
import com.market.MSA.models.InventoryProduct;
import com.market.MSA.repositories.InventoryProductRepository;
import com.market.MSA.repositories.InventoryRepository;
import com.market.MSA.repositories.ProductRepository;
import com.market.MSA.requests.InventoryProductRequest;
import com.market.MSA.responses.InventoryProductResponse;
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
public class InventoryProductService {
  final InventoryProductRepository inventoryProductRepository;
  final EntityFinderService entityFinderService;
  final InventoryRepository inventoryRepository;
  final InventoryProductMapper inventoryProductMapper;
  private final ProductRepository productRepository;

  @Transactional
  public InventoryProductResponse createInventoryProduct(InventoryProductRequest request) {
    InventoryProduct inventoryProduct = inventoryProductMapper.toInventoryProduct(request);
    inventoryProduct.setInventory(
        entityFinderService.findByIdOrThrow(
            inventoryRepository, request.getInventoryId(), ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));
    inventoryProduct.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));

    inventoryProductRepository.save(inventoryProduct);
    return inventoryProductMapper.toInventoryProductResponse(inventoryProduct);
  }

  @Transactional
  public InventoryProductResponse updateInventoryProduct(Long id, InventoryProductRequest request) {
    InventoryProduct inventoryProduct =
        inventoryProductRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));
    inventoryProduct.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    inventoryProduct.setInventory(
        entityFinderService.findByIdOrThrow(
            inventoryRepository, request.getInventoryId(), ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));

    inventoryProductMapper.updateInventoryProductFromRequest(request, inventoryProduct);
    InventoryProduct inventoryProductUpdated = inventoryProductRepository.save(inventoryProduct);

    return inventoryProductMapper.toInventoryProductResponse(inventoryProductUpdated);
  }

  @Transactional
  public boolean deleteInventoryProduct(Long id) {
    if (!inventoryProductRepository.existsById(id)) {
      throw new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND);
    }
    inventoryProductRepository.deleteById(id);
    return true;
  }

  @Transactional
  public InventoryProductResponse getInventoryProductById(Long id) {
    InventoryProduct inventoryProduct =
        inventoryProductRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));
    return inventoryProductMapper.toInventoryProductResponse(inventoryProduct);
  }

  public List<InventoryProductResponse> getInventoryProductByProductId(Long productId) {
    List<InventoryProduct> inventoryProducts =
        inventoryProductRepository.findByProductId_ProductId(productId);
    return inventoryProducts.stream()
        .map(inventoryProductMapper::toInventoryProductResponse)
        .collect(Collectors.toList());
  }

  public List<InventoryProductResponse> getInventoryProductByInventoryId(Long inventoryId) {
    List<InventoryProduct> inventoryProducts =
        inventoryProductRepository.findByInventory_InventoryId(inventoryId);
    return inventoryProducts.stream()
        .map(inventoryProductMapper::toInventoryProductResponse)
        .collect(Collectors.toList());
  }
}
