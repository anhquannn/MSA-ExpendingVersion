import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/product_repository.dart';

class ProductRepositoryImpl extends IProductRepository {
  @override
  Future<List<ProductModel>> onFilterAndSortProducts({
    int? minPrice,
    int? maxPrice,
    String? color,
    int? categoryId,
    int? page,
    int? pageSize,
  }) async {
    final queryParams = {
      if (minPrice != null) 'minPrice': '$minPrice',
      if (maxPrice != null) 'maxPrice': '$maxPrice',
      if (color != null) 'color': color,
      if (categoryId != null) 'categoryId': '$categoryId',
      'page': '$page',
      'pageSize': '$pageSize',
    };
    final url = HttpConnection.buildUrlWithQueryParams(
      filterAndSortProducts,
      queryParams,
    );
    final data = await HttpConnection.get(url);
    if (data.isSuccess) {
      List<ProductModel> products = [];
      for (var item in data.data) {
        products.add(ProductModel.fromJson(item));
      }
      return products;
    } else {
      throw Exception('Failed to load products');
    }
  }

  @override
  Future<List<ProductModel>> onGetAllProductsInBranch(
    int branchId, {
    int page = 1,
    int pageSize = 10,
    String? sortBy,
    String? sortDirection,
  }) {
    final queryParams = {
      'page': '$page',
      'pageSize': '$pageSize',
      if (sortBy != null) 'sortBy': sortBy,
      if (sortDirection != null) 'sortDirection': sortDirection,
    };

    final baseUrl = '$getAllProductsInBranch/$branchId';

    final url = HttpConnection.buildUrlWithQueryParams(baseUrl, queryParams);

    return HttpConnection.get(url).then((data) {
      if (data.isSuccess) {
        return (data.data as List)
            .map((item) => ProductModel.fromJson(item))
            .toList();
      } else {
        throw Exception('Failed to load products');
      }
    });
  }

  @override
  Future<ProductModel?> onCreateProduct(ProductModel product) async {
    final data = await HttpConnection.post(
      createProduct,
      body: product.toJson(),
    );
    if (data.isSuccess) {
      return ProductModel.fromJson(data.data);
    } else {
      throw Exception('Failed to create product');
    }
  }

  @override
  Future<bool> onDeleteProduct(String id) async {
    final data = await HttpConnection.delete('$deleteProduct$id');
    return data.isSuccess;
  }

  @override
  Future<List<ProductModel>> onGetAllProducts({
    int page = 1,
    int pageSize = 10,
  }) {
    final queryParams = {'page': '$page', 'pageSize': '$pageSize'};
    final url = HttpConnection.buildUrlWithQueryParams(
      getAllProducts,
      queryParams,
    );
    return HttpConnection.get(url).then((data) {
      if (data.isSuccess) {
        return (data.data as List)
            .map((item) => ProductModel.fromJson(item))
            .toList();
      } else {
        throw Exception('Failed to load products');
      }
    });
  }

  @override
  Future<ProductModel?> onGetProductById(String id) {
    final url = '$getProductById$id';
    return HttpConnection.get(url).then((data) {
      if (data.isSuccess) {
        return ProductModel.fromJson(data.data);
      } else {
        throw Exception('Failed to load product');
      }
    });
  }

  @override
  Future<ProductModel?> onUpdateProduct(ProductModel product, String id) {
    final url = '$updateProduct$id';
    return HttpConnection.put(url, body: product.toJson()).then((data) {
      if (data.isSuccess) {
        return ProductModel.fromJson(data.data);
      } else {
        throw Exception('Failed to update product');
      }
    });
  }

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
  }) {
    final queryParams = {
      if (keyword != null) 'keyword': keyword,
      if (minPrice != null) 'minPrice': '$minPrice',
      if (maxPrice != null) 'maxPrice': '$maxPrice',
      if (color != null) 'color': color,
      if (categoryId != null) 'categoryId': '$categoryId',
      if (manufacturerId != null) 'manufacturerId': '$manufacturerId',
      'page': '$page',
      'pageSize': '$pageSize',
      if (sortBy != null) 'sortBy': sortBy,
      if (sortDirection != null) 'sortDirection': sortDirection,
    };
    final url = HttpConnection.buildUrlWithQueryParams(
      searchProducts,
      queryParams,
    );
    return HttpConnection.get(url).then((data) {
      if (data.isSuccess) {
        return (data.data as List)
            .map((item) => ProductModel.fromJson(item))
            .toList();
      } else {
        throw Exception('Failed to load products');
      }
    });
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
  }) {
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
    return HttpConnection.get(url).then((data) {
      if (data.isSuccess) {
        return (data.data as List)
            .map((item) => ProductModel.fromJson(item))
            .toList();
      } else {
        throw Exception('Failed to load products');
      }
    });
  }
}
