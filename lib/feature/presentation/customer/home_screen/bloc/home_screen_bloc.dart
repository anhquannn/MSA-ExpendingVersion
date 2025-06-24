import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/category_filter_request.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/request/product_get_all_request_model.dart';
import 'package:msa/feature/data/model/request/promocode_request_model.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/domain/usecase/cart_use_case.dart';
import 'package:msa/feature/domain/usecase/category_use_case.dart';
import 'package:msa/feature/domain/usecase/product_use_case.dart';
import 'package:msa/feature/domain/usecase/promo_code_use_case.dart';
import 'package:msa/feature/domain/usecase/user_use_case.dart';
import 'package:msa/feature/presentation/customer/branch_list/ui/branch_list_screen.dart';
import 'package:msa/feature/presentation/customer/category_list/ui/category_list_screen.dart';
import 'package:msa/feature/presentation/customer/product_detail/ui/product_detail_screen.dart';
import 'package:msa/feature/presentation/customer/product_list/ui/product_list_screen.dart';
import 'package:msa/feature/presentation/customer/promo_code_list/ui/promo_code_list_screen.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:rxdart/rxdart.dart';
import '../../../../data/datasources/local/starage.dart';
import '../ui/home_screen.dart';

class HomeScreenBloc extends BaseBloc<HomeScreen> {
  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();
  final CartItemUseCase _cartItemUseCase = GetIt.I<CartItemUseCase>();
  final CartUseCase _cartUseCase = GetIt.I<CartUseCase>();

  final PageController categoryController = PageController(initialPage: 0);
  final ValueNotifier<int> indexScreen = ValueNotifier(0);

  ScrollController scrollController = ScrollController();

  double currentPage = 0;

  final userModel = BehaviorSubject<UserModel>();
  final categoryModels = BehaviorSubject<List<CategoryModel>>();
  final productModels = BehaviorSubject<ProductFilterResult>();
  final promoCodeModels = BehaviorSubject<List<PromoCodeModel>>();
  final listCartItemModels = BehaviorSubject<List<CartItemModel>>.seeded([
    mockCartItem,
  ]);
  final cartModels = BehaviorSubject<CartModel>();

  bool _isManuallyScrolling = false;
  bool _isAnimatingPage = false;
  bool _hasInitCalled = false;

  @override
  String get contextKey => 'HomeScreen';

  void _onCategoryScroll() {
    if (!categoryController.hasClients) return;
    final page = categoryController.page;
    if (page == null) return;

    final rounded = page.round();
    if (indexScreen.value != rounded && !_isManuallyScrolling) {
      _isManuallyScrolling = true;
      indexScreen.value = rounded;
      Future.delayed(const Duration(milliseconds: 300), () {
        _isManuallyScrolling = false;
      });
    }
  }

  @override
  void onInit() {
    print("initState called");
    indexScreen.value = 0;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      userModel.add(UserModel());
      categoryModels.add([]);
      productModels.add(ProductFilterResult());
      if (indexScreen.value == 0) {
        categoryController.addListener(_onCategoryScroll);
      }
      indexScreen.addListener(() {
        if (!categoryController.hasClients || _isAnimatingPage) return;

        final targetPage = indexScreen.value;
        if (categoryController.page?.round() != targetPage) {
          _isAnimatingPage = true;
          categoryController
              .animateToPage(
                targetPage,
                duration: const Duration(milliseconds: 300),
                curve: Curves.easeInOut,
              )
              .then((_) {
                _isAnimatingPage = false;
              });
        }
      });
    });
  }

  @override
  void onDispose() {
    categoryController.dispose();
    indexScreen.dispose();
    userModel.close();
    categoryModels.close();
    productModels.close();
  }

  @override
  void onReady() {
    print("onReady called");
    if (!_hasInitCalled) {
      _hasInitCalled = true;
      onRefresh();
    }
  }

  onRefresh() async {
    print("onRefresh started");
    await onGetProfile().catchError((e) => print('Lỗi profile: $e'));
    final List<Future<void>> futures = [
      onGetPromoCode().catchError((e) => print('Lỗi promo code: $e')),
      onGetCategory().catchError((e) => print('Lỗi category: $e')),
      onGetProduct().catchError((e) => print('Lỗi product: $e')),
    ];

    await Future.wait(futures);
  }

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext viewContext) => widget.build(viewContext);

  onTapPromoCode(PromoCodeModel model) {}

  onGetPromoCode() async {
    try {
      List<PromoCodeModel>? promoCode = await Repository.onGetAllPromoCode(
        PromoCodeRequestModel(userId: Storage.userModelGlobal?.userId ?? 0),
      );
      promoCodeModels.add(promoCode ?? []);
    } catch (e) {
      print('Lỗi khi lấy danh sách mã giảm giá: $e');
      promoCodeModels.add([]);
    }
  }

  onGetProfile() async {
    try {
      UserModel? user = await _userUseCases.getUserByEmail().timeout(
        const Duration(seconds: 10),
        onTimeout: () {
          showCustomMessageError(viewContext);
          return null;
        },
      );
      if (user != null) {
        userModel.add(user);
        userModelGlobal = user;
        await onGetOrCreateCart(user.userId ?? 0);
      } else {
        userModel.add(UserModel());
      }
      print('Lỗi khi lấy thông tin người dùng: ${userModelGlobal?.userId}');
    } catch (e) {
      print('Lỗi khi lấy thông tin người dùng: $e');
      userModel.add(UserModel());
    }
  }

  // onGetProduct() async {
  //   try {
  //     ProductFilterRequest filter = ProductFilterRequest(
  //       page: 1,
  //       pageSize: 10,
  //       branchId: Storage.branchModelGlobal?.branchId,
  //     );

  //     ProductFilterResult product = await Repository.onFilterProducts(filter);

  //     productModels.add(product);
  //   } catch (e) {
  //     print('Lỗi khi lấy danh sách sản phẩm: $e');
  //     productModels.add(ProductFilterResult()); // Fallback nếu có lỗi
  //   }
  // }
  onGetProduct() async {
    try {
      ProductFilterRequest filter = ProductFilterRequest(
        page: 1,
        pageSize: 10,
        branchId: Storage.branchModelGlobal?.branchId,
      );

      ProductFilterResult product = await Repository.onFilterProducts(filter);

      // In danh sách sản phẩm thường
      print('📦 Danh sách sản phẩm thường:');
      product.products?.forEach((p) {
        print('→ ${p.name} | ID: ${p.productId}');
      });

      // In danh sách sản phẩm đang giảm giá
      print('🔥 Danh sách sản phẩm giảm giá:');
      product.discountedProducts?.forEach((p) {
        print('→ ${p.name} | ID: ${p.productId}');
      });

      // In danh sách từ productsPage (nếu có)
      print('📄 Danh sách productsPage (paginated):');
      product.productsPage?.content.forEach((p) {
        print('→ ${p.name} | ID: ${p.productId}');
      });

      productModels.add(product);
    } catch (e) {
      print('❌ Lỗi khi lấy danh sách sản phẩm: $e');
      productModels.add(ProductFilterResult()); // Fallback nếu có lỗi
    }
  }

  onGetCategory() async {
    try {
      final response = await Repository.onGetAllCategory(
        CategoryFilterRequest(),
      );

      categoryModels.set(response);
    } catch (e) {
      print('Lỗi khi lấy danh sách danh mục: $e');
      categoryModels.add([]); // Fallback nếu có lỗi
    }
  }

  onSearch() {}

  onLogout() async {
    // Storage.onLogout();

    // Navigator.pushAndRemoveUntil(
    //   context,
    //   MaterialPageRoute(builder: (context) => const LoginScreen()),
    //   (route) => false,
    // );
  }

  onTapListPromoCode() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) =>
                PromoCodeListScreen(promoCodeList: promoCodeModels.value),
      ),
    );
  }

  onTapListCategory() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => CategoryListScreen(categoryList: categoryModels.value),
      ),
    );
  }

  onTapProductSale() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => ProductListScreen(
              productList: productModels.value,
              isSale: true,
            ),
      ),
    );
  }

  onTapPopularProduct() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => ProductListScreen(
              productList: productModels.value,
              isSale: true,
            ),
      ),
    );
  }

  onTapCategory(CategoryModel model) {
    // Navigator.push(
    //   viewContext,
    //   MaterialPageRoute(
    //     builder: (context) => ProductListScreen(
    //       productList: productModels.value
    //           .where((product) => product.categoryId == model.id)
    //           .toList(),
    //       isSale: false,
    //     ),
    //   ),
    // );
  }

  // onTapProductDetail(ProductModel model) {
  onTapProductDetail() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => ProductDetailCustomerScreen(productModel: mockProduct),
      ),
    );
  }

  onGetOrCreateCart(int userId) async {
    final data = await _cartUseCase
        .create(userId)
        .timeout(
          const Duration(seconds: 10),
          onTimeout: () {
            showCustomMessageError(viewContext);
            return null;
          },
        );
    if (data != null) {
      cartModelGlobal = data;
      cartModels.add(data);
      await onGetUserCart(data.cartId ?? 0);
    }
  }

  /// MOCK DATA
  onGetUserCart(int cartId) async {
    final data = await _cartItemUseCase
        .getCartItemsByCartId(userModelGlobal?.userId ?? 0)
        .timeout(
          const Duration(seconds: 10),
          onTimeout: () {
            showCustomMessageError(viewContext);
            return <CartItemModel>[];
          },
        );
    if (data != null) {
      listCartItemModels.add(data);
    }
  }

  onMinus(int id) {}

  onPlus(int id) {}

  onBuy() async {
    if (Storage.branchModelGlobal == null) {
      final id = await Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => BranchListScreen()),
      );
    }
  }

  onAddToCart() {}
}
