import 'package:flutter/cupertino.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/request/product_get_all_request_model.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/product_repository.dart';

class ProductRepositoryImpl extends IProductRepository {
  // Hàm helper để tránh lặp code
  Future<List<ProductModel>> _parseProductList(
    ApiResponse<PaginatedResult<ProductModel>> response,
  ) async {
    if (!response.isSuccess) {
      // Có thể log lỗi hoặc gán messageError ở đây nếu cần
    }
    return response.result?.content ?? [];
  }

  // Hàm helper cho các API trả về List<ProductModel> nhưng không phân trang
  Future<List<ProductModel>> _parseSimpleProductList(
    ApiResponse<List<ProductModel>> response,
  ) async {
    if (!response.isSuccess) {
      // Có thể log lỗi hoặc gán messageError ở đây nếu cần
    }
    return response.result ?? [];
  }

  @override
  Future<List<ProductModel>> onFilterAndSortProducts(
    ProductGetAllRequest model,
  ) async {
    final response = await HttpConnection.post<PaginatedResult<ProductModel>>(
      filterAndSortProducts,
      body: model.toJson(),
      fromJsonT:
          (json) => PaginatedResult.fromJson(
            json,
            (item) => ProductModel.fromJson(item),
          ),
    );
    return _parseProductList(response);
  }

  @override
  Future<List<ProductModel>> onGetAllProductsInBranch(
    int branchId, {
    int page = 1,
    int pageSize = 10,
    String? sortBy,
    String? sortDirection,
  }) async {
    final url = HttpConnection.buildUrlWithQueryParams(
      '$getAllProductsInBranch/$branchId',
      {
        'page': page,
        'pageSize': pageSize,
        'sortBy': sortBy,
        'sortDirection': sortDirection,
      },
    );

    // Giả sử API này trả về List trực tiếp, không có vỏ PaginatedResult
    final response = await HttpConnection.get<List<ProductModel>>(
      url,
      fromJsonT:
          (json) =>
              (json as List)
                  .map((item) => ProductModel.fromJson(item))
                  .toList(),
    );
    return _parseSimpleProductList(response);
  }

  @override
  Future<ProductModel?> onCreateProduct(ProductModel product) async {
    final response = await HttpConnection.post<ProductModel>(
      createProduct,
      body: product.toJson(),
      fromJsonT: (json) => ProductModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<bool> onDeleteProduct(String id) async {
    final response = await HttpConnection.delete<dynamic>(
      '$deleteProduct$id',
      fromJsonT: (json) => json,
    );
    return response.isSuccess;
  }

  @override
  Future<List<ProductModel>> onGetAllProducts({
    int page = 1,
    int pageSize = 10,
  }) async {
    final url = HttpConnection.buildUrlWithQueryParams(getAllProducts, {
      'page': page,
      'pageSize': pageSize,
    });
    final response = await HttpConnection.get<List<ProductModel>>(
      url,
      fromJsonT:
          (json) =>
              (json as List)
                  .map((item) => ProductModel.fromJson(item))
                  .toList(),
    );
    return _parseSimpleProductList(response);
  }

  @override
  Future<ProductModel?> onGetProductById(String id) async {
    final response = await HttpConnection.get<ProductModel>(
      '$getProductById$id',
      fromJsonT: (json) => ProductModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<ProductModel?> onUpdateProduct(ProductModel product, String id) async {
    final response = await HttpConnection.put<ProductModel>(
      '$updateProduct$id',
      body: product.toJson(),
      fromJsonT: (json) => ProductModel.fromJson(json),
    );
    return response.result;
  }

  // Hai hàm search này gần như giống hệt nhau, có thể gộp lại nếu path giống nhau.
  // Tạm thời sửa cả hai.
  @override
  Future<List<ProductModel>> onSearchProducts({
    int page = 1,
    int pageSize = 10,
    String? keyword,
    int? minPrice,
    int? maxPrice,
    String? color,
    int? categoryId,
    int? manufacturerId,
    String? sortBy,
    String? sortDirection,
  }) async {
    final queryParams = {
      if (keyword != null) 'keyword': keyword,
      if (minPrice != null) 'minPrice': '$minPrice',
      if (maxPrice != null) 'maxPrice': '$maxPrice',
      if (color != null) 'color': color,
      if (categoryId != null) 'categoryId': '$categoryId',
      'page': '$page',
      'pageSize': '$pageSize',
      if (sortBy != null) 'sortBy': sortBy,
      if (sortDirection != null) 'sortDirection': sortDirection,
    };
    final url = HttpConnection.buildUrlWithQueryParams(
      searchProducts,
      queryParams,
    );
    final response = await HttpConnection.get<List<ProductModel>>(
      url,
      fromJsonT:
          (json) =>
              (json as List)
                  .map((item) => ProductModel.fromJson(item))
                  .toList(),
    );
    return _parseSimpleProductList(response);
  }

  @override
  Future<List<ProductModel>> onSearchProductsInBranch({
    int page = 1,
    int pageSize = 10,
    String? keyword,
    int? minPrice,
    int? maxPrice,
    String? color,
    int? categoryId,
    String? sortBy,
    String? sortDirection,
    BuildContext? context,
  }) async {
    final queryParams = {
      if (keyword != null) 'keyword': keyword,
      if (minPrice != null) 'minPrice': '$minPrice',
      if (maxPrice != null) 'maxPrice': '$maxPrice',
      if (color != null) 'color': color,
      if (categoryId != null) 'categoryId': '$categoryId',
      'page': '$page',
      'pageSize': '$pageSize',
      if (sortBy != null) 'sortBy': sortBy,
      if (sortDirection != null) 'sortDirection': sortDirection,
    };
    final url = HttpConnection.buildUrlWithQueryParams(
      searchProductsInBranch,
      queryParams,
    );
    final response = await HttpConnection.get<List<ProductModel>>(
      context: context,
      url,
      fromJsonT:
          (json) =>
              (json as List)
                  .map((item) => ProductModel.fromJson(item))
                  .toList(),
    );
    return _parseSimpleProductList(response);
  }

  // static Future<ProductFilterResult?> onFilterProducts(
  //   ProductFilterRequest request, {
  //   BuildContext? context,
  // }) async {
  //   const String endpoint = 'product/filter';

  //   final response = await HttpConnection.post<ProductFilterResult>(
  //     context: context,
  //     endpoint,
  //     body: request.toJson(),
  //     isToken: true,
  //     fromJsonT: (json) => ProductFilterResult.fromJson(json),
  //   );

  //   if (!response.isSuccess) {
  //     return response.result;
  //   }
  //   return null;
  // }
  static Future<ProductFilterResult?> onFilterProducts(
    ProductFilterRequest request, {
    BuildContext? context,
  }) async {
    const String endpoint = 'product/filter';
    final response = await HttpConnection.post<ProductFilterResult>(
      endpoint,
      context: context,
      body: request.toJson(),
      isToken: true,
      fromJsonT: (json) => ProductFilterResult.fromJson(json),
    );
    if (response.isSuccess) {
      return response.result;
    }
    print('Lỗi khi lọc sản phẩm: ${response.message}');
    return null;
  }
}
