import 'package:msa/feature/data/model/request/product_get_all_request_model.dart' show ProductGetAllRequest;
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/product_repository.dart';

class ProductUseCase {
  final CreateProductUseCase create;
  final UpdateProductUseCase update;
  final DeleteProductUseCase delete;
  final GetAllProductsUseCase getAll;
  final GetProductByIdUseCase getById;
  final GetAllProductsInBranchUseCase getAllInBranch;
  final FilterAndSortProductsUseCase filterAndSort;
  final SearchProductsInBranchUseCase searchInBranch;
  final SearchProductsUseCase search;

  ProductUseCase({
    required this.create,
    required this.update,
    required this.delete,
    required this.getAll,
    required this.getById,
    required this.getAllInBranch,
    required this.filterAndSort,
    required this.searchInBranch,
    required this.search,
  });

}

class CreateProductUseCase{
  final IProductRepository _productRepository;

  CreateProductUseCase(this._productRepository);

  Future<void> call(ProductModel product) async {
    await _productRepository.onCreateProduct(product);
  }
}
class UpdateProductUseCase{
  final IProductRepository _productRepository;

  UpdateProductUseCase(this._productRepository);

  Future<void> call(ProductModel product, String id) async {
    await _productRepository.onUpdateProduct(product, id);
  }
}
class DeleteProductUseCase{
  final IProductRepository _productRepository;

  DeleteProductUseCase(this._productRepository);

  Future<void> call(String id) async {
    await _productRepository.onDeleteProduct(id);
  }
}
class GetAllProductsUseCase{
  final IProductRepository _productRepository;

  GetAllProductsUseCase(this._productRepository);

  Future<List<ProductModel>> call({int page = 1, int pageSize = 10}) async {
    return await _productRepository.onGetAllProducts(page: page, pageSize: pageSize);
  }
}
class GetProductByIdUseCase{
  final IProductRepository _productRepository;

  GetProductByIdUseCase(this._productRepository);

  Future<ProductModel?> call(String id) async {
    return await _productRepository.onGetProductById(id);
  }
}
class GetAllProductsInBranchUseCase{
  final IProductRepository _productRepository;

  GetAllProductsInBranchUseCase(this._productRepository);

  Future<List<ProductModel>> call(int branchId, {int page = 1, int pageSize = 10, String? sortBy, String? sortDirection}) async {
    return await _productRepository.onGetAllProductsInBranch(branchId, page: page, pageSize: pageSize, sortBy: sortBy, sortDirection: sortDirection);
  }
}
class FilterAndSortProductsUseCase{
  final IProductRepository _productRepository;

  FilterAndSortProductsUseCase(this._productRepository);

  Future<List<ProductModel>> call(ProductGetAllRequest model) async {
    return await _productRepository.onFilterAndSortProducts(model);
  }
}
class SearchProductsInBranchUseCase{
  final IProductRepository _productRepository;

  SearchProductsInBranchUseCase(this._productRepository);

  Future<List<ProductModel>> call({int page = 1, int pageSize = 10, String keyword = '', int minPrice = 0, int maxPrice = 0, String color = '', int categoryId = 0, String sortBy = '', String sortDirection = ''}) async {
    return await _productRepository.onSearchProductsInBranch(page: page, pageSize: pageSize, keyword: keyword, minPrice: minPrice, maxPrice: maxPrice, color: color, categoryId: categoryId, sortBy: sortBy, sortDirection: sortDirection);
  }
}
class SearchProductsUseCase{
  final IProductRepository _productRepository;

  SearchProductsUseCase(this._productRepository);

  Future<List<ProductModel>> call({int page = 1, int pageSize = 10, String keyword = '', int minPrice = 0, int maxPrice = 0, String color = '', int categoryId = 0, int manufacturerId = 0, String sortBy = '', String sortDirection = ''}) async {
    return await _productRepository.onSearchProducts(page: page, pageSize: pageSize, keyword: keyword, minPrice: minPrice, maxPrice: maxPrice, color: color, categoryId: categoryId, manufacturerId: manufacturerId, sortBy: sortBy, sortDirection: sortDirection);
  }
}
