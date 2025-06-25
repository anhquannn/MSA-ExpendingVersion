package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.ProductImageMapper;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.product.ProductImage;
import com.market.MSA.repositories.product.ProductImageRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.requests.filters.ProductImageFilterRequest;
import com.market.MSA.requests.product.ProductImageRequest;
import com.market.MSA.responses.product.ProductImageResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductImageService {
  ProductImageRepository productImageRepository;
  ProductImageMapper productImageMapper;
  EntityFinderService entityFinderService;
  ProductRepository productRepository;

  @Transactional
  public ProductImageResponse createProductImage(ProductImageRequest request) {
    ProductImage productImage = productImageMapper.toProductImage(request);

    Product product =
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND);

    productImage.setProduct(product);

    ProductImage savedProductImage = productImageRepository.save(productImage);
    return productImageMapper.toProductImageResponse(savedProductImage);
  }

  @Transactional
  public ProductImageResponse updateProductImage(Long id, ProductImageRequest request) {
    ProductImage productImage =
        productImageRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND));

    if (!productImage.getProduct().getProductId().equals(request.getProductId())) {
      Product product =
          entityFinderService.findByIdOrThrow(
              productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND);
      productImage.setProduct(product);
    }

    productImageMapper.updateProductImage(request, productImage);
    ProductImage updatedProductImage = productImageRepository.save(productImage);
    return productImageMapper.toProductImageResponse(updatedProductImage);
  }

  @Transactional
  public boolean deleteProductImage(Long id) {
    if (!productImageRepository.existsById(id)) {
      throw new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
    }
    productImageRepository.deleteById(id);
    return true;
  }

  public ProductImageResponse getProductImageById(Long id) {
    ProductImage productImage =
        productImageRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND));
    return productImageMapper.toProductImageResponse(productImage);
  }

  @Cacheable("all_product_images")
  public List<ProductImageResponse> getAll() {
    return productImageRepository.findAll().stream()
        .map(productImageMapper::toProductImageResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("product_images_list")
  public List<ProductImageResponse> getAllProductImages(ProductImageFilterRequest request) {
    return productImageRepository.filter(request.getProductId()).stream()
        .map(productImageMapper::toProductImageResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("product_images_paging")
  public Page<ProductImageResponse> getAllProductImagesWithPaging(
      ProductImageFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return productImageRepository
        .filterWithPaging(request.getProductId(), pageable)
        .map(productImageMapper::toProductImageResponse);
  }
}
