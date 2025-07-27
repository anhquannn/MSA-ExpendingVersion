// file: lib/models/product_filter_result.dart
import 'dart:convert';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/domain/entities/product_model.dart';

class ProductFilterResult {
  final PaginatedResult<ProductModel>? productsPage;
  final PaginatedResult<ProductModel>? discountedProductsPage;
  final List<ProductModel>? products;
  final List<ProductModel>? discountedProducts;
  final List<FreePromotionGroup>? freePromotionGroups;

  ProductFilterResult({
    this.productsPage,
    this.discountedProductsPage,
    this.products,
    this.discountedProducts,
    this.freePromotionGroups,
  });

  factory ProductFilterResult.fromJson(Map<String, dynamic> json) {
    // Helper function để parse danh sách sản phẩm (dạng phẳng)
    List<ProductModel> _parseProductList(dynamic productList) {
      if (productList is List) {
        return productList
            .whereType<Map<String, dynamic>>() // Chỉ lấy các item là Map
            .map((item) => ProductModel.fromJson(item))
            .toList();
      }
      return [];
    }

    final productsPage = PaginatedResult<ProductModel>.fromJson(
      json['productsPage'] ?? {},
      (item) => ProductModel.fromJson(item as Map<String, dynamic>),
    );

    final discountedProductsPageJson = json['discountedProductsPage'];
    final PaginatedResult<ProductModel>? discountedPage =
        discountedProductsPageJson != null
            ? PaginatedResult<ProductModel>.fromJson(
              discountedProductsPageJson,
              (item) {
                final productData = (item as Map<String, dynamic>)['product'];
                if (productData != null) {
                  return ProductModel.fromJson(
                    productData as Map<String, dynamic>,
                  );
                }
                return ProductModel();
              },
            )
            : null;

    discountedPage?.content.removeWhere((product) => product.productId == null);

    final List<FreePromotionGroup> parsedFreePromotionGroups =
        (json['freePromotionGroups'] as List<dynamic>?)
            ?.map((item) => FreePromotionGroup.fromJson(item))
            .toList() ??
        [];

    return ProductFilterResult(
      productsPage: productsPage,
      discountedProductsPage: discountedPage,
      products: _parseProductList(json['products']),
      discountedProducts: _parseProductList(json['discountedProducts']),
      freePromotionGroups: parsedFreePromotionGroups,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'products': products?.map((item) => item.toJson()).toList(),
      'discountedProducts':
          discountedProducts?.map((item) => item.toJson()).toList(),
      'freePromotionGroups':
          freePromotionGroups?.map((e) => e.toJson()).toList(),
    };
  }
}

class ProductImage {
  final int productImageId;
  final String imageUrl;
  final int sortOrder;
  final bool isPrimary;

  ProductImage({
    required this.productImageId,
    required this.imageUrl,
    required this.sortOrder,
    required this.isPrimary,
  });

  factory ProductImage.fromJson(Map<String, dynamic> json) {
    return ProductImage(
      productImageId: json['productImageId'] ?? 0,
      imageUrl: json['imageUrl'] ?? '',
      sortOrder: json['sortOrder'] ?? 99,
      isPrimary: json['primary'] ?? false,
    );
  }

  Map<String, dynamic> toJson() => {
    'productImageId': productImageId,
    'imageUrl': imageUrl,
    'sortOrder': sortOrder,
    'primary': isPrimary,
  };

  static ProductImage empty() => ProductImage(
    productImageId: 0,
    imageUrl: '',
    sortOrder: 99,
    isPrimary: false,
  );
}

class FreePromotionGroup {
  final ProductModel? mainProduct;
  final List<ProductModel>? freeProducts;

  FreePromotionGroup({this.mainProduct, this.freeProducts});

  factory FreePromotionGroup.fromJson(Map<String, dynamic> json) {
    return FreePromotionGroup(
      mainProduct:
          json['mainProduct'] != null
              ? ProductModel.fromJson(json['mainProduct'])
              : null,
      freeProducts:
          (json['freeProducts'] as List<dynamic>?)
              ?.map((item) => ProductModel.fromJson(item))
              .toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'mainProduct': mainProduct?.toJson(),
      'freeProducts': freeProducts?.map((e) => e.toJson()).toList(),
    };
  }
}
