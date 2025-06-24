// SỬA: category_repository_impl.dart

import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/model/request/category_filter_request.dart';
import 'package:msa/feature/domain/entities/product_model.dart'; // Note: tên file là product_model nhưng class là CategoryModel
import 'package:msa/feature/domain/repositories/category_repository.dart';

class CategoryRepositoryImpl extends ICategoryRepository {
  @override
  Future<CategoryModel?> onCreateCategory(CategoryModel category) async {
    final data = await HttpConnection.post<CategoryModel>(
      createCategory,
      body: category.toJson(),
      fromJsonT: (json) => CategoryModel.fromJson(json),
    );
    return data.result;
  }

  @override
  Future<CategoryModel> onDeleteCategory(String id) async {
    final data = await HttpConnection.delete<CategoryModel>(
      '$deleteCategory/$id',
      fromJsonT: (json) => CategoryModel.fromJson(json),
    );
    if (data.result == null) {
      throw Exception('Delete failed or no data returned');
    }
    return data.result!;
  }

  @override
  Future<List<CategoryModel>> onGetAllCategories({
    int page = 1,
    int pageSize = 10,
  }) async {
    final path = HttpConnection.buildUrlWithQueryParams(getAllCategories, {
      'page': page,
      'pageSize': pageSize,
    });

    final data = await HttpConnection.get<PaginatedResult<CategoryModel>>(
      path,
      fromJsonT:
          (json) => PaginatedResult.fromJson(
            json,
            (itemJson) => CategoryModel.fromJson(itemJson),
          ),
    );
    return data.result?.content ?? [];
  }

  @override
  Future<CategoryModel?> onGetCategoryById(String id) async {
    final data = await HttpConnection.get<CategoryModel>(
      '$getCategoryById/$id',
      fromJsonT: (json) => CategoryModel.fromJson(json),
    );
    return data.result;
  }

  @override
  Future<CategoryModel?> onUpdateCategory(CategoryModel category) async {
    final data = await HttpConnection.put<CategoryModel>(
      updateCategory,
      body: category.toJson(),
      fromJsonT: (json) => CategoryModel.fromJson(json),
    );
    return data.result;
  }

  static Future<List<CategoryModel>> getAllCategory(CategoryFilterRequest model) async {
    final response = await HttpConnection.post<PaginatedResult<CategoryModel>>(
      'category/paging',
      fromJsonT:
          (json) => PaginatedResult.fromJson(
            json,
            (itemJson) => CategoryModel.fromJson(itemJson),
          ),
    );
    return response.result?.content ?? [];
  }
}
