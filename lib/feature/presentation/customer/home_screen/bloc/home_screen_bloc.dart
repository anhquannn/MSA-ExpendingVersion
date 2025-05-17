import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/domain/usecase/category_use_case.dart';
import 'package:msa/feature/domain/usecase/product_use_case.dart';
import 'package:msa/feature/domain/usecase/promo_code_use_case.dart';
import 'package:msa/feature/domain/usecase/user_use_case.dart';
import 'package:msa/feature/presentation/customer/category_list/ui/category_list_screen.dart';
import 'package:msa/feature/presentation/customer/product_list/ui/product_list_screen.dart';
import 'package:msa/feature/presentation/customer/promo_code_list/ui/promo_code_list_screen.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import 'package:rxdart/rxdart.dart';
import '../ui/home_screen.dart';

class HomeScreenBloc extends BaseBloc<HomeScreen> {
  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();
  final CategoryUseCase _categoryUseCase = GetIt.I<CategoryUseCase>();
  final ProductUseCase _productUseCase = GetIt.I<ProductUseCase>();
  final PromoCodeUseCase _promoCodeUseCase = GetIt.I<PromoCodeUseCase>();

  final PageController categoryController = PageController(initialPage: 0);
  // Đảm bảo luôn khởi tạo là 0
  final ValueNotifier<int> indexScreen = ValueNotifier(0);

  ScrollController scrollController = ScrollController();

  double currentPage = 0;

  final userModel = BehaviorSubject<UserModel>();
  final categoryModels = BehaviorSubject<List<CategoryModel>>();
  final productModels = BehaviorSubject<List<ProductModel>>();
  final promoCodeModels = BehaviorSubject<List<PromoCodeModel>>();

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
    indexScreen.value = 0; // Reset về 0 khi mở màn hình
    WidgetsBinding.instance.addPostFrameCallback((_) {
      userModel.add(UserModel());
      categoryModels.add([]);
      productModels.add([]);
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
    if (!_hasInitCalled) {
      _hasInitCalled = true;
      onRefresh();
    }
  }

  onRefresh() async {
    final List<Future<void>> futures = [
      onGetProfile().catchError((e) => print('Lỗi profile: $e')),
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
      List<PromoCodeModel>? promoCode = await _promoCodeUseCase
          .getAll()
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () {
              print('Timeout khi lấy danh sách mã giảm giá');
              return [];
            },
          );
      if (promoCode != null) {
        promoCodeModels.add(promoCode);
      } else {
        promoCodeModels.add([]);
      }
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
          print('Timeout khi lấy thông tin người dùng');
          return null;
        },
      );
      if (user != null) {
        userModel.add(user);
      } else {
        userModel.add(UserModel()); // Fallback nếu không có dữ liệu
      }
    } catch (e) {
      print('Lỗi khi lấy thông tin người dùng: $e');
      userModel.add(UserModel()); // Fallback nếu có lỗi
    }
  }

  onGetProduct() async {
    try {
      List<ProductModel>? product = await _productUseCase.getAll().timeout(
        const Duration(seconds: 10),
        onTimeout: () {
          print('Timeout khi lấy danh sách sản phẩm');
          return [];
        },
      );
      productModels.add(product);
    } catch (e) {
      print('Lỗi khi lấy danh sách sản phẩm: $e');
      productModels.add([]); // Fallback nếu có lỗi
    }
  }

  onGetCategory() async {
    try {
      List<CategoryModel>? category = await _categoryUseCase.getAll().timeout(
        const Duration(seconds: 10),
        onTimeout: () {
          print('Timeout khi lấy danh sách danh mục');
          return [];
        },
      );
      categoryModels.add(category);
    } catch (e) {
      print('Lỗi khi lấy danh sách danh mục: $e');
      categoryModels.add([]); // Fallback nếu có lỗi
    }
  }

  onSearch() {}
  onLogout() async {
    HttpConnection.onLogout();

    Navigator.pushAndRemoveUntil(
      context,
      MaterialPageRoute(builder: (context) => const LoginScreen()),
      (route) => false,
    );
  }

  onTapHone() {}

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
        builder: (context) => CategoryListScreen(categoryList: categoryModels.value,),
      ),
    );
  }
  onTapProductSale() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder: (context) => ProductListScreen(
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
        builder: (context) => ProductListScreen(
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
}
