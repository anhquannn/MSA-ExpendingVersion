import 'package:msa/feature/domain/entities/product_model.dart';

abstract class ICategoryRepository {
  Future<List<CategoryModel>> onGetAllCategories({int page, int pageSize});
  Future<CategoryModel?> onGetCategoryById(String id);
  Future<CategoryModel?> onCreateCategory(CategoryModel category);
  Future<CategoryModel?> onUpdateCategory(CategoryModel category);
  Future<CategoryModel> onDeleteCategory(String id);
}
