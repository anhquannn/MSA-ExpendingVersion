import 'package:get_it/get_it.dart';
import 'package:msa/feature/data/repositories/cart_connection.dart';
import 'package:msa/feature/data/repositories/cart_item_connection.dart';
import 'package:msa/feature/data/repositories/category_connection.dart';
import 'package:msa/feature/data/repositories/feedback_connection.dart';
import 'package:msa/feature/data/repositories/order_connection.dart';
import 'package:msa/feature/data/repositories/product_connection.dart';
import 'package:msa/feature/data/repositories/promo_code_connection.dart';
import 'package:msa/feature/domain/repositories/cart_item_repository.dart';
import 'package:msa/feature/domain/repositories/cart_repository.dart';
import 'package:msa/feature/domain/repositories/category_repository.dart';
import 'package:msa/feature/domain/repositories/feedback_repository.dart';
import 'package:msa/feature/domain/repositories/order_repository.dart';
import 'package:msa/feature/domain/repositories/product_repository.dart';
import 'package:msa/feature/domain/repositories/promo_code_repository.dart';
import 'package:msa/feature/domain/repositories/user_repository.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/domain/usecase/cart_use_case.dart';
import 'package:msa/feature/domain/usecase/category_use_case.dart';
import 'package:msa/feature/domain/usecase/feedback_use_case.dart';
import 'package:msa/feature/domain/usecase/order_use_case.dart';
import 'package:msa/feature/domain/usecase/product_use_case.dart';
import 'package:msa/feature/domain/usecase/promo_code_use_case.dart';

import '../feature/data/repositories/user_connection.dart';
import '../feature/domain/usecase/user_use_case.dart';

final GetIt locator = GetIt.instance;

void setupLocator() {
  // Register Repositories as singleton
  locator.registerLazySingleton<IUserRepository>(() => UserRepositoryImpl());
  locator.registerLazySingleton<ICategoryRepository>(
    () => CategoryRepositoryImpl(),
  );
  locator.registerLazySingleton<IProductRepository>(
    () => ProductRepositoryImpl(),
  );
  locator.registerLazySingleton<IPromoCodeRepository>(
    () => PromoCodeRepositoryImpl(),
  );
  locator.registerLazySingleton<ICartItemRepository>(
    () => CartItemRepositoryImpl(),
  );
  locator.registerLazySingleton<ICartRepository>(() => CartRepositoryImpl());
  locator.registerLazySingleton<IOrderRepository>(() => OrderRepositoryImpl());
  locator.registerLazySingleton<IFeedbackRepository>(
    () => FeedbackRepositoryImpl(),
  );

  // Register UserUseCases as a group
  locator.registerLazySingleton(
    () => UserUseCases(
      login: LoginUserUseCase(locator<IUserRepository>()),
      register: RegisterUserUseCase(locator<IUserRepository>()),
      verifyOtp: VerifyOtpUserUseCase(locator<IUserRepository>()),
      resetPassword: ResetPasswordUseCase(locator<IUserRepository>()),
      resetPasswordWithoutOtp: ResetPasswordWithoutOtpUseCase(
        locator<IUserRepository>(),
      ),
      update: UpdateUserUseCase(locator<IUserRepository>()),
      getUserByEmail: GetUserByEmailUseCase(locator<IUserRepository>()),
    ),
  );

  locator.registerLazySingleton(
    () => CategoryUseCase(
      create: CreateCategoryUseCase(locator<ICategoryRepository>()),
      update: UpdateCategoryUseCase(locator<ICategoryRepository>()),
      delete: DeleteCategoryUseCase(locator<ICategoryRepository>()),
      getAll: GetAllCategoriesUseCase(locator<ICategoryRepository>()),
      getById: GetCategoryByIdUseCase(locator<ICategoryRepository>()),
    ),
  );

  locator.registerLazySingleton(
    () => ProductUseCase(
      create: CreateProductUseCase(locator<IProductRepository>()),
      update: UpdateProductUseCase(locator<IProductRepository>()),
      delete: DeleteProductUseCase(locator<IProductRepository>()),
      getAll: GetAllProductsUseCase(locator<IProductRepository>()),
      getById: GetProductByIdUseCase(locator<IProductRepository>()),
      getAllInBranch: GetAllProductsInBranchUseCase(
        locator<IProductRepository>(),
      ),
      filterAndSort: FilterAndSortProductsUseCase(
        locator<IProductRepository>(),
      ),
      searchInBranch: SearchProductsInBranchUseCase(
        locator<IProductRepository>(),
      ),
      search: SearchProductsUseCase(locator<IProductRepository>()),
    ),
  );

  locator.registerLazySingleton(
    () => PromoCodeUseCase(
      create: CreatePromoCodeUseCase(locator<IPromoCodeRepository>()),
      update: UpdatePromoCodeUseCase(locator<IPromoCodeRepository>()),
      delete: DeletePromoCodeUseCase(locator<IPromoCodeRepository>()),
      getAll: GetAllPromoCodesUseCase(locator<IPromoCodeRepository>()),
      getById: GetPromoCodeByIdUseCase(locator<IPromoCodeRepository>()),
    ),
  );

  locator.registerLazySingleton(
    () => CartItemUseCase(
      getCartItem: GetCartItemUseCase(locator<ICartItemRepository>()),

      getCartItemById: GetCartItemByIdUseCase(locator<ICartItemRepository>()),
      getCartItemsByCartId: GetCartItemsByCartIdUseCase(
        locator<ICartItemRepository>(),
      ),
      addToCart: AddToCartUseCase(locator<ICartItemRepository>()),
      createCartItem: CreateCartItemUseCase(locator<ICartItemRepository>()),
      updateCartItemsSelection: UpdateCartItemsSelectionUseCase(
        locator<ICartItemRepository>(),
      ),
      deleteCartItem: DeleteCartItemUseCase(locator<ICartItemRepository>()),
      clearCart: ClearCartUseCase(locator<ICartItemRepository>()),
    ),
  );

  locator.registerLazySingleton(
    () => CartUseCase(
      create: GetOrCreateCartForUserUseCase(locator<ICartRepository>()),
    ),
  );

  locator.registerLazySingleton(
    () => OrderUseCases(
      createOrder: CreateOrderUseCase(locator<IOrderRepository>()),
      getOrderById: GetOrderByIdUseCase(locator<IOrderRepository>()),
      searchOrdersByPhoneNumber: SearchOrdersByPhoneNumberUseCase(
        locator<IOrderRepository>(),
      ),
      getOrdersByUserIdAndStatus: GetOrdersByUserIdAndStatusUseCase(
        locator<IOrderRepository>(),
      ),
      getAllOrders: GetAllOrdersUseCase(locator<IOrderRepository>()),
      getRevenueStatistics: GetRevenueStatisticsUseCase(
        locator<IOrderRepository>(),
      ),
      getOrdersByBranchId: GetOrdersByBranchIdUseCase(
        locator<IOrderRepository>(),
      ),
      updateOrder: UpdateOrderUseCase(locator<IOrderRepository>()),
      deleteOrder: DeleteOrderUseCase(locator<IOrderRepository>()),
    ),
  );

  locator.registerLazySingleton(
    () => FeedbackUseCases(
      getFeedbackById: GetFeedbackByIdUseCase(locator<IFeedbackRepository>()),
      getAllFeedbacksByProductId: GetAllFeedbacksByProductIdUseCase(
        locator<IFeedbackRepository>(),
      ),
      createFeedback: CreateFeedbackUseCase(locator<IFeedbackRepository>()),
      updateFeedback: UpdateFeedbackUseCase(locator<IFeedbackRepository>()),
      deleteFeedback: DeleteFeedbackUseCase(locator<IFeedbackRepository>()),
    ),
  );
}
