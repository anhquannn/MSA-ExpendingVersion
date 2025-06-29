// file: lib/models/product_filter_result.dart
import 'dart:convert';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/domain/entities/product_model.dart';

class ProductFilterResult {
  final PaginatedResult<ProductModel>? productsPage;
  final PaginatedResult<ProductModel>? discountedProductsPage;
  final List<ProductModel>? products;
  final List<ProductModel>? discountedProducts;

  ProductFilterResult({
    this.productsPage,
    this.discountedProductsPage,
    this.products,
    this.discountedProducts,
  });

  // factory ProductFilterResult.fromJson(Map<String, dynamic> json) {
  //   List<ProductModel> parseProducts(dynamic productList) {
  //     if (productList is List) {
  //       return productList.map((item) => ProductModel.fromJson(item)).toList();
  //     }
  //     return [];
  //   }

  //   return ProductFilterResult(
  //     productsPage: PaginatedResult.fromJson(
  //       json['productsPage'] ?? {},
  //       (item) => ProductModel.fromJson(item),
  //     ),
  //     discountedProductsPage: PaginatedResult.fromJson(
  //       json['discountedProductsPage'] ?? {},
  //       (item) => ProductModel.fromJson(item),
  //     ),
  //     products: parseProducts(json['products']),
  //     discountedProducts: parseProducts(json['discountedProducts']),
  //   );
  // }
  factory ProductFilterResult.fromJson(Map<String, dynamic> json) {
    List<ProductModel> parseProducts(dynamic productList) {
      if (productList is List) {
        return productList.map((item) => ProductModel.fromJson(item)).toList();
      }
      return [];
    }

    // ✅ parse productsPage bình thường
    final productsPage = PaginatedResult.fromJson(
      json['productsPage'] ?? {},
      (item) => ProductModel.fromJson(item),
    );

    // ✅ parse discountedProductsPage: lấy product từ mỗi item
    final discountedPageJson = json['discountedProductsPage'];
    final discountedPage =
        discountedPageJson != null
            ? PaginatedResult.fromJson(
              discountedPageJson,
              (item) => ProductModel.fromJson(
                item['product'] ?? {},
              ), // ⚠ Lấy từ item['product']
            )
            : null;

    return ProductFilterResult(
      productsPage: productsPage,
      discountedProductsPage: discountedPage,
      products: parseProducts(json['products']),
      discountedProducts: parseProducts(json['discountedProducts']),
    );
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
