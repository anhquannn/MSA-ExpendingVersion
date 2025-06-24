import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/domain/entities/product_model.dart';

class AllProductsResult {
  final PaginatedResult<ProductModel> productsPage;
  final PaginatedResult<ProductModel> discountedProductsPage;
  final List<ProductModel> products;
  final List<ProductModel> discountedProducts;

  AllProductsResult({
    required this.productsPage,
    required this.discountedProductsPage,
    required this.products,
    required this.discountedProducts,
  });

  factory AllProductsResult.fromJson(Map<String, dynamic> json) {
    // Hàm trợ giúp để parse một item Product từ JSON
    ProductModel productItemFromJson(dynamic itemJson) => ProductModel.fromJson(itemJson);
    
    // Hàm trợ giúp để parse một List<Product> từ JSON
    List<ProductModel> productListFromJson(List<dynamic> listJson) {
        return listJson.map((item) => ProductModel.fromJson(item)).toList();
    }

    return AllProductsResult(
      // Parse `productsPage` bằng PaginatedResult.fromJson,
      // truyền vào hàm parse cho từng item là productItemFromJson.
      productsPage: PaginatedResult.fromJson(
        json['productsPage'],
        productItemFromJson,
      ),
      
      discountedProductsPage: PaginatedResult.fromJson(
        json['discountedProductsPage'],
        productItemFromJson,
      ),
      
      // Parse `products` là một mảng đơn giản
      products: productListFromJson(json['products'] ?? []),
      
      discountedProducts: productListFromJson(json['discountedProducts'] ?? []),
    );
  }
}