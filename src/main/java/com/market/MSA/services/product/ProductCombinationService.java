package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.ProductCombinationMapper;
import com.market.MSA.models.product.ProductCombination;
import com.market.MSA.repositories.product.ProductCombinationRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.requests.filters.ProductCombinationFilterRequest;
import com.market.MSA.requests.product.ProductCombinationRequest;
import com.market.MSA.responses.product.ProductCombinationResponse;
import com.market.MSA.responses.product.ProductResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCombinationService {

  ProductCombinationRepository pcRepository;
  ProductRepository productRepository;

  @Qualifier("productCombinationMapper")
  ProductCombinationMapper pcMapper;

  EntityFinderService entityFinderService;

  @Transactional
  public ProductCombinationResponse create(ProductCombinationRequest request) {
    ProductCombination pc = pcMapper.toProductCombination(request);
    pc.setProduct1(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId1(), ErrorCode.PRODUCT_NOT_FOUND));
    pc.setProduct2(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId2(), ErrorCode.PRODUCT_NOT_FOUND));
    return pcMapper.toProductCombinationResponse(pcRepository.save(pc));
  }

  @Transactional
  public ProductCombinationResponse update(Long id, ProductCombinationRequest request) {
    ProductCombination pc =
        pcRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_COMBINATION_NOT_FOUND));
    pcMapper.updateProductCombinationFromRequest(request, pc);
    if (request.getProductId1() != null) {
      pc.setProduct1(
          entityFinderService.findByIdOrThrow(
              productRepository, request.getProductId1(), ErrorCode.PRODUCT_NOT_FOUND));
    }
    if (request.getProductId2() != null) {
      pc.setProduct2(
          entityFinderService.findByIdOrThrow(
              productRepository, request.getProductId2(), ErrorCode.PRODUCT_NOT_FOUND));
    }
    return pcMapper.toProductCombinationResponse(pcRepository.save(pc));
  }

  @Transactional
  public boolean delete(Long id) {
    if (!pcRepository.existsById(id))
      throw new AppException(ErrorCode.PRODUCT_COMBINATION_NOT_FOUND);
    pcRepository.deleteById(id);
    return true;
  }

  public ProductCombinationResponse getById(Long id) {
    return pcMapper.toProductCombinationResponse(
        pcRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_COMBINATION_NOT_FOUND)));
  }

  @Cacheable("product-combinations")
  @Transactional(readOnly = true)
  public List<ProductCombinationResponse> filter(ProductCombinationFilterRequest req) {
    return pcRepository.filter(req.getProductId1(), req.getProductId2()).stream()
        .map(pcMapper::toProductCombinationResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("product-combinations-page")
  @Transactional(readOnly = true)
  public Page<ProductCombinationResponse> filterPaging(ProductCombinationFilterRequest req) {
    Sort sort = Sort.by(Sort.Direction.fromString(req.getSortDirection()), req.getSortBy());
    Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize(), sort);
    return pcRepository
        .filterWithPaging(req.getProductId1(), req.getProductId2(), pageable)
        .map(pcMapper::toProductCombinationResponse);
  }

  @Cacheable("product-combinations-page-products")
  @Transactional(readOnly = true)
  public Page<ProductResponse> filterPagingProducts(ProductCombinationFilterRequest req) {
    Sort sort = Sort.by(Sort.Direction.fromString(req.getSortDirection()), req.getSortBy());
    Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize(), sort);
    Page<ProductCombination> page =
        pcRepository.filterWithPaging(req.getProductId1(), req.getProductId2(), pageable);

    // Tạo list các productId2 từ page
    List<ProductResponse> products =
        page.getContent().stream()
            .map(pc -> pcMapper.toProductCombinationResponse(pc).getProductId2())
            .collect(Collectors.toList());

    // Tạo response với list products
    return new PageImpl<>(products, pageable, page.getTotalElements());
  }
}
