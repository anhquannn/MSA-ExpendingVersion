
import 'package:get_it/get_it.dart';
import 'package:msa/feature/data/repositories/category_connection.dart';
import 'package:msa/feature/data/repositories/product_connection.dart';
import 'package:msa/feature/data/repositories/promo_code_connection.dart';
import 'package:msa/feature/domain/repositories/category_repository.dart';
import 'package:msa/feature/domain/repositories/product_repository.dart';
import 'package:msa/feature/domain/repositories/promo_code_repository.dart';
import 'package:msa/feature/domain/repositories/user_repository.dart';
import 'package:msa/feature/domain/usecase/category_use_case.dart';
import 'package:msa/feature/domain/usecase/product_use_case.dart';
import 'package:msa/feature/domain/usecase/promo_code_use_case.dart';

import '../feature/data/repositories/user_connection.dart';
import '../feature/domain/usecase/user_use_case.dart';

final GetIt locator = GetIt.instance;

void setupLocator() {
  // Register Repositories as singleton
  locator.registerLazySingleton<IUserRepository>(() => UserRepositoryImpl());
  locator.registerLazySingleton<ICategoryRepository>(() => CategoryRepositoryImpl());
  locator.registerLazySingleton<IProductRepository>(() => ProductRepositoryImpl());
  locator.registerLazySingleton<IPromoCodeRepository>(() => PromoCodeRepositoryImpl());

  // Register UserUseCases as a group
  locator.registerLazySingleton(() => UserUseCases(
    login:  LoginUserUseCase(locator<IUserRepository>()),
    register:  RegisterUserUseCase(locator<IUserRepository>()),
    verifyOtp: VerifyOtpUserUseCase(locator<IUserRepository>()),
    resetPassword: ResetPasswordUseCase(locator<IUserRepository>()),
    resetPasswordWithoutOtp:  ResetPasswordWithoutOtpUseCase(locator<IUserRepository>()),
    update: UpdateUserUseCase(locator<IUserRepository>()),
    getUserByEmail: GetUserByEmailUseCase(locator<IUserRepository>()),
  ));

  locator.registerLazySingleton(() => CategoryUseCase(
    create: CreateCategoryUseCase(locator<ICategoryRepository>()),
    update: UpdateCategoryUseCase(locator<ICategoryRepository>()),
    delete: DeleteCategoryUseCase(locator<ICategoryRepository>()),
    getAll: GetAllCategoriesUseCase(locator<ICategoryRepository>()),
    getById: GetCategoryByIdUseCase(locator<ICategoryRepository>()),
  ));

  locator.registerLazySingleton(() => ProductUseCase(
    create: CreateProductUseCase(locator<IProductRepository>()),
    update: UpdateProductUseCase(locator<IProductRepository>()),
    delete: DeleteProductUseCase(locator<IProductRepository>()),
    getAll: GetAllProductsUseCase(locator<IProductRepository>()),
    getById: GetProductByIdUseCase(locator<IProductRepository>()),
    getAllInBranch: GetAllProductsInBranchUseCase(locator<IProductRepository>()),
    filterAndSort: FilterAndSortProductsUseCase(locator<IProductRepository>()),
    searchInBranch: SearchProductsInBranchUseCase(locator<IProductRepository>()), 
    search: SearchProductsUseCase(locator<IProductRepository>()),
  ));
  
  locator.registerLazySingleton(() => PromoCodeUseCase(
    create: CreatePromoCodeUseCase(locator<IPromoCodeRepository>()),
    update: UpdatePromoCodeUseCase(locator<IPromoCodeRepository>()),
    delete: DeletePromoCodeUseCase(locator<IPromoCodeRepository>()),
    getAll: GetAllPromoCodesUseCase(locator<IPromoCodeRepository>()),
    getById: GetPromoCodeByIdUseCase(locator<IPromoCodeRepository>()),
  ));
}
