import 'package:msa/feature/domain/entities/product_model.dart';

abstract class IProductRepository {
  Future<List<ProductModel>> onGetAllProducts({int page, int pageSize});
  Future<List<ProductModel>> onFilterAndSortProducts({int minPrice, int maxPrice, String color, int categoryId, int page, int pageSize});
  Future<List<ProductModel>> onGetAllProductsInBranch(int branchId,{int page, int pageSize, String? sortBy, String? sortDirection});
  Future<List<ProductModel>> onSearchProductsInBranch({int page, int pageSize, String keyword, int minPrice, int maxPrice, String color, int categoryId, String sortBy, String sortDirection});
  Future<List<ProductModel>> onSearchProducts({int page, int pageSize, String keyword, int minPrice, int maxPrice, String color, int categoryId, int manufacturerId, String sortBy, String sortDirection});
  Future<ProductModel?> onGetProductById(String id);
  Future<ProductModel?> onCreateProduct(ProductModel product);
  Future<ProductModel?> onUpdateProduct(ProductModel product, String id);
  Future<bool> onDeleteProduct(String id);
}