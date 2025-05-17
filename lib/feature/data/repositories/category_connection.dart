
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/category_repository.dart';

class CategoryRepositoryImpl extends ICategoryRepository {
  @override
  Future<CategoryModel?> onCreateCategory(CategoryModel category) async{
    final data= await HttpConnection.post(
      createCategory,
      body: category.toJson(),
    );
    return data.isSuccess ? CategoryModel.fromJson(data.data) : null;
  }

  @override
  Future<CategoryModel> onDeleteCategory(String id) async{
    final data = await HttpConnection.delete(
      '$deleteCategory/$id',
    );
    if (data.isSuccess) {
      return CategoryModel.fromJson(data.data);
    } else {
      throw Exception('Failed to delete category');
    }
  }

  @override
  Future<List<CategoryModel>> onGetAllCategories({int? page=1, int? pageSize=10}) async {
    String url = '$getAllCategories?page=$page&pageSize=$pageSize';
    final data = await HttpConnection.get(
      url,
    );
    if (data.isSuccess) {
      List<CategoryModel> categories = [];
      for (var item in data.data) {
        categories.add(CategoryModel.fromJson(item));
      }
      return categories;
    } else {
      throw Exception('Failed to load categories');
    }
  }

  @override
  Future<CategoryModel?> onGetCategoryById(String id)async {
    final data = await HttpConnection.get(
      '$getCategoryById/$id',
    );
    if (data.isSuccess) {
      return CategoryModel.fromJson(data.data);
    } else {
      throw Exception('Failed to load category');
    }
  }

  @override
  Future<CategoryModel?> onUpdateCategory(CategoryModel category) async{
    final data = await HttpConnection.put(
      updateCategory,
      body: category.toJson(),
    );
    if (data.isSuccess) {
      return CategoryModel.fromJson(data.data);
    } else {
      throw Exception('Failed to update category');
    }
  }
  

}