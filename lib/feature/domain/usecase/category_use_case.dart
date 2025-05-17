import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/category_repository.dart';

class CategoryUseCase {
  final CreateCategoryUseCase create;
  final UpdateCategoryUseCase update;
  final DeleteCategoryUseCase delete;
  final GetAllCategoriesUseCase getAll;
  final GetCategoryByIdUseCase getById;

  CategoryUseCase({
    required this.create,
    required this.update,
    required this.delete,
    required this.getAll,
    required this.getById,
  });
}

class CreateCategoryUseCase {
  final ICategoryRepository repository;
  CreateCategoryUseCase(this.repository);
  Future<CategoryModel?> call(CategoryModel category) {
    return repository.onCreateCategory(category);
  }
}

class UpdateCategoryUseCase {
  final ICategoryRepository repository;
  UpdateCategoryUseCase(this.repository);
  Future<CategoryModel?> call(CategoryModel category) {
    return repository.onUpdateCategory(category);
  }
}

class DeleteCategoryUseCase {
  final ICategoryRepository repository;
  DeleteCategoryUseCase(this.repository);
  Future<CategoryModel> call(String id) {
    return repository.onDeleteCategory(id);
  }
}

class GetAllCategoriesUseCase {
  final ICategoryRepository repository;
  final String? page;
  final String? pageSize;
  GetAllCategoriesUseCase(this.repository, {this.page, this.pageSize});
  Future<List<CategoryModel>> call({int page = 1, int pageSize = 10}) {
    return repository.onGetAllCategories(page: page, pageSize: pageSize);
  }
}

class GetCategoryByIdUseCase {
  final ICategoryRepository repository;
  GetCategoryByIdUseCase(this.repository);
  Future<CategoryModel?> call(String id) {
    return repository.onGetCategoryById(id);
  }
}
