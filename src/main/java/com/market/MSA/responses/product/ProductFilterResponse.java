package com.market.MSA.responses.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductFilterResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("products")
  List<ProductResponse> products = new ArrayList<>();

  @JsonProperty("discountedProducts")
  List<InventoryProductResponse> discountedProducts = new ArrayList<>();

  @JsonProperty("bestSellingAProducts")
  List<ProductResponse> bestSellingAProducts = new ArrayList<>();

  @JsonProperty("freePromotionGroups")
  List<FreePromotionGroupResponse> freePromotionGroups = new ArrayList<>();

  // Pagination fields
  @lombok.Builder.Default @JsonProperty int page = 0;

  @lombok.Builder.Default @JsonProperty int size = 10;

  @lombok.Builder.Default @JsonProperty long totalProducts = 0;

  @lombok.Builder.Default @JsonProperty long totalDiscountedProducts = 0;

  @lombok.Builder.Default @JsonProperty long totalBestSellingAProducts = 0;

  @lombok.Builder.Default @JsonProperty long totalFreePromotionGroups = 0;

  // Helper method to convert to Page
  @JsonProperty("productsPage")
  public Page<ProductResponse> getProductsPage() {
    return new PageImpl<>(
        products != null ? products : new ArrayList<>(), PageRequest.of(page, size), totalProducts);
  }

  // Helper method to convert to Page
  @JsonProperty("discountedProductsPage")
  public Page<InventoryProductResponse> getDiscountedProductsPage() {
    return new PageImpl<>(
        discountedProducts != null ? discountedProducts : new ArrayList<>(),
        PageRequest.of(page, size),
        totalDiscountedProducts);
  }

  // Static factory method to create from Pages
  public static ProductFilterResponse fromPages(
      Page<ProductResponse> productsPage,
      Page<InventoryProductResponse> discountedPage,
      Page<ProductResponse> bestSellingAPage,
      Page<FreePromotionGroupResponse> freePromotionGroupPage) {
    return ProductFilterResponse.builder()
        .products(productsPage != null ? productsPage.getContent() : new ArrayList<>())
        .discountedProducts(
            discountedPage != null ? discountedPage.getContent() : new ArrayList<>())
        .bestSellingAProducts(
            bestSellingAPage != null ? bestSellingAPage.getContent() : new ArrayList<>())
        .freePromotionGroups(
            freePromotionGroupPage != null
                ? freePromotionGroupPage.getContent()
                : new ArrayList<>())
        .page(productsPage != null ? productsPage.getNumber() : 0)
        .size(productsPage != null ? productsPage.getSize() : 10)
        .totalProducts(productsPage != null ? productsPage.getTotalElements() : 0)
        .totalDiscountedProducts(discountedPage != null ? discountedPage.getTotalElements() : 0)
        .totalBestSellingAProducts(
            bestSellingAPage != null ? bestSellingAPage.getTotalElements() : 0)
        .totalFreePromotionGroups(
            freePromotionGroupPage != null ? freePromotionGroupPage.getTotalElements() : 0)
        .build();
  }

  // Overload for backward compatibility (without best-selling & free promotion)
  public static ProductFilterResponse fromPages(
      Page<ProductResponse> productsPage, Page<InventoryProductResponse> discountedPage) {
    return fromPages(productsPage, discountedPage, null, null);
  }
}
