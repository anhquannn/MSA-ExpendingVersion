import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/data/model/request/category_filter_request.dart';
import 'package:msa/feature/data/model/request/get_branch_request_model.dart';
import 'package:msa/feature/data/model/request/order_paging_request_model.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/request/promocode_request_model.dart';
import 'package:msa/feature/data/model/response/branch_response_response.dart';
import 'package:msa/feature/data/model/response/get_order_response_model.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/domain/usecase/cart_use_case.dart';
import 'package:msa/feature/domain/usecase/user_use_case.dart';
import 'package:msa/feature/presentation/customer/category_list/ui/category_list_screen.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/selec_branch_screen.dart';
import 'package:msa/feature/presentation/customer/persional/ui/persional_screen.dart';
import 'package:msa/feature/presentation/customer/product_detail/ui/product_detail_screen.dart';
import 'package:msa/feature/presentation/customer/product_list/ui/product_list_screen.dart';
import 'package:msa/feature/presentation/customer/promo_code_list/ui/promo_code_list_screen.dart';
import 'package:msa/widget/animate_add_to_cart.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:rxdart/rxdart.dart';
import '../../../../data/datasources/local/starage.dart';
import '../ui/home_screen.dart';
import 'package:get/get.dart';

class HomeScreenBloc extends BaseBloc<HomeScreen> {
  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();
  final CartItemUseCase _cartItemUseCase = GetIt.I<CartItemUseCase>();
  final CartUseCase _cartUseCase = GetIt.I<CartUseCase>();
  final GlobalKey cartIconKey = GlobalKey();
  final GlobalKey cartIconKey1 = GlobalKey();
  Map<int, GlobalKey> imageKeys = {};

  UserModel? userModel;
  ProductFilterResult? listProducts;
  List<CategoryModel>? listCategoryModel = [];
  List<PromoCodeModel>? listPromocode = [];
  List<CartItemModel>? listCartItemModel = [];
  CartModel? cartModel;

  final PageController categoryController = PageController(initialPage: 0);
  final ValueNotifier<int> indexScreen = ValueNotifier(0);

  ScrollController scrollController = ScrollController();

  double currentPage = 0;

  final streamCaculate = BehaviorSubject<double>();
  final streamUserModel = BehaviorSubject<UserModel>();
  final streamCartModel = BehaviorSubject<CartModel>();
  final streamCategoryModels = BehaviorSubject<List<CategoryModel>>();
  final streamProductModels = BehaviorSubject<ProductFilterResult>();
  final streamPromoCodeModels = BehaviorSubject<List<PromoCodeModel>>();
  final streamCartItemModels = BehaviorSubject<List<CartItemModel>>.seeded([
    mockCartItem,
  ]);
  final cartModels = BehaviorSubject<CartModel>();

  bool _isManuallyScrolling = false;
  bool _isAnimatingPage = false;
  bool _hasInitCalled = false;
  final Map<int, Debouncer> _debouncers = {};

  List<OrderResponse> listPending = [];
  List<OrderResponse> listPaying = [];
  List<OrderResponse> listPaid = [];
  List<OrderResponse> listDelivering = [];
  List<OrderResponse> listShipped = [];
  List<OrderResponse> listCancelling = [];
  List<OrderResponse> listCancelled = [];
  List<OrderResponse> listCompleted = [];
  List<OrderResponse> listFailed = [];

  // Stream controller tương ứng
  final streamPending = BehaviorSubject<List<OrderResponse>>();
  final streamPaying = BehaviorSubject<List<OrderResponse>>();
  final streamPaid = BehaviorSubject<List<OrderResponse>>();
  final streamDelivering = BehaviorSubject<List<OrderResponse>>();
  final streamShipped = BehaviorSubject<List<OrderResponse>>();
  final streamCancelling = BehaviorSubject<List<OrderResponse>>();
  final streamCancelled = BehaviorSubject<List<OrderResponse>>();
  final streamCompleted = BehaviorSubject<List<OrderResponse>>();
  final streamFailed = BehaviorSubject<List<OrderResponse>>();

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
    init(context);
    indexScreen.value = 0;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      streamUserModel.add(UserModel());
      streamCategoryModels.add([]);
      streamProductModels.add(ProductFilterResult());
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
    streamUserModel.close();
    streamCategoryModels.close();
    streamProductModels.close();
  }

  @override
  void onReady() {
    onCheckBranch();
    print("onReady called");
    if (!_hasInitCalled) {
      _hasInitCalled = true;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (!_hasInitCalled) {
          _hasInitCalled = true;
        }
      });

      onRefresh();
    }
  }

  // onGetAddress() async {
  //   if (Storage.addressModel != null) {
  //     final response = await Repository.getUserAddresses();
  //     Storage.saveAddress(response);
  //   }
  // }
  onGetAddress() async {
    if (Storage.addressModel == null) {
      final response = await Repository.getUserAddresses();
      Storage.saveAddress(response[0]);
    } else {
      print('⚠️ Không có địa chỉ trong Storage.addressModel để kiểm tra.');
    }
  }

  onCheckBranch() async {
    if (Storage.branchModelGlobal == null) {
      Navigator.push(
        this.context,
        MaterialPageRoute(builder: (context) => SelectBranchScreen()),
      );
    }
  }

  onRefresh() async {
    print("onRefresh started");
    await onGetProfile().catchError((e) => print('Lỗi profile: $e'));
    final List<Future<void>> futures = [
      onGetPromoCode().catchError((e) => print('Lỗi promo code: $e')),
      onGetCategory().catchError((e) => print('Lỗi category: $e')),
      onGetProduct().catchError((e) => print('Lỗi product: $e')),
      // onGetUserCart().catchError((e) => print('Lỗi product: $e')),
      onCaculateCart().catchError((e) => print('Lỗi onCaculateCart: $e')),
      onGetAddress().catchError((e) => print('Lỗi onGetAddress: $e')),
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

      streamPromoCodeModels.add(promoCode ?? []);
      listPromocode = promoCode;
    } catch (e) {
      print('Lỗi khi lấy danh sách mã giảm giá: $e');
      streamPromoCodeModels.add([]);
    }
  }

  onGetProfile({BuildContext? bcontext}) async {
    try {
      UserModel? user = await _userUseCases.getUserByEmail().timeout(
        const Duration(seconds: 10),
        onTimeout: () {
          showCustomMessageError(viewContext);
          return null;
        },
      );
      if (user != null) {
        streamUserModel.add(user);
        userModelGlobal = user;
        userModel = user;
        await onGetOrCreateCart(bcontext: bcontext);
      } else {
        streamUserModel.add(UserModel());
      }
    } catch (e) {
      print('Lỗi khi lấy thông tin người dùng: $e');
      streamUserModel.add(UserModel());
    }
  }

  onGetProduct() async {
    try {
      ProductFilterRequest filter = ProductFilterRequest(
        page: 1,
        pageSize: 10,
        branchId: Storage.branchModelGlobal?.branchId,
      );

      ProductFilterResult product = await Repository.onFilterProducts(filter);

      listProducts = product;
      streamProductModels.add(product);
    } catch (e) {
      print('❌ Lỗi khi lấy danh sách sản phẩm: $e');
      streamProductModels.add(ProductFilterResult()); // Fallback nếu có lỗi
    }
    setState(() {});
  }

  onGetCategory() async {
    try {
      final response = await Repository.onGetAllCategory(
        CategoryFilterRequest(),
      );
      listCategoryModel = response;
      streamCategoryModels.set(response);
    } catch (e) {
      print('Lỗi khi lấy danh sách danh mục: $e');
      streamCategoryModels.add([]); // Fallback nếu có lỗi
    }
  }

  onSearch() {}

  filter() {}

  onTapListPromoCode() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) =>
                PromoCodeListScreen(promoCodeList: streamPromoCodeModels.value),
      ),
    );
  }

  onTapListCategory() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) =>
                CategoryListScreen(categoryList: streamCategoryModels.value),
      ),
    );
  }

  onTapProductSale() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => ProductListScreen(
              productList: streamProductModels.value,
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
              productList: streamProductModels.value,
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
  onTapProductDetail(ProductModel model) {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder: (context) => ProductDetailCustomerScreen(productModel: model),
      ),
    );
  }

  onGetOrCreateCart({BuildContext? bcontext}) async {
    final data = await _cartUseCase
        .create(Storage.userModelGlobal?.userId ?? 0)
        .timeout(
          const Duration(seconds: 10),
          onTimeout: () {
            showCustomMessageError(bcontext!);
            return null;
          },
        );
    if (data != null) {
      cartModelGlobal = data;
      cartModels.add(data);
      streamCartModel.set(data);
      cartModel = data;
      Storage.cartModelGlobal = data;
      Storage.saveCartModel(data);
      // await onGetUserCart(bcontext: bcontext);
      final total = await Repository.onCaculateOrder(
        Storage.cartModelGlobal?.cartId ?? 0,
      );
      streamCaculate.set(total);
    }
  }

  onGetUserCart({BuildContext? bcontext}) async {
    final data = await _cartItemUseCase
        .getCartItemsByCartId(Storage.cartModelGlobal?.cartId ?? 0)
        .timeout(
          const Duration(seconds: 10),
          onTimeout: () {
            showCustomMessageError(bcontext!);
            return <CartItemModel>[];
          },
        );
    // await onCaculateCart();
    if (data != null) {
      listCartItemModel = data ?? [];
      streamCartItemModels.add(data);
    }
  }

  void onCaculate(CartItemModel model, bool isMinus) {
    final quantity =
        isMinus ? (model.quantity ?? 1) - 1 : (model.quantity ?? 1) + 1;

    // ✅ Update UI ngay lập tức
    final index = listCartItemModel?.indexWhere(
      (e) => e.cartItemId == model.cartItemId,
    );
    if (index != null && index >= 0) {
      listCartItemModel![index].quantity = quantity;
      // Nếu có stream để hiển thị giỏ hàng, thì set lại stream ở đây
      streamCartItemModels.set(listCartItemModel!);
    }

    // ✅ Debounce gọi API
    _debouncers[model.cartItemId!] ??= Debouncer(milliseconds: 600);
    _debouncers[model.cartItemId!]!.run(() async {
      final response = await Repository.onUpdateQuantity(
        branchId: Storage.branchModelGlobal?.branchId ?? 3,
        cartItemId: model.cartItemId,
        quantity: quantity,
        select: model.selected ?? true,
      );

      if (response) {
        await onGetUserCart();
      }
    });
  }

  onBuy(ProductModel model, BuildContext? context) async {
    Navigator.push(
      context!,
      MaterialPageRoute(builder: (context) => CreateOrderScreen()),
    );
  }

  onAddToCart(ProductModel model, BuildContext bcontext, GlobalKey key) async {
    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: model.productId ?? 0,
        branchId: mockBranch.branchId ?? 0,
        quantity: 1,
      ),
    );
    if (data) {
      runAddToCartAnimation(
        imageKey: key,
        cartKey: cartIconKey,
        context: bcontext,
        urlImage: model.image,
      );
      onGetUserCart();
      return;
    }
    showCustomMessageError(bcontext);
  }

  void runAddToCartAnimation({
    required BuildContext context,
    required GlobalKey cartKey,
    required GlobalKey imageKey,
    String? urlImage,
  }) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    const totalItems = 4;
    const cartIndex = 2;
    final bottomBarHeight = 60.0;
    final RenderBox? cartBox =
        cartKey.currentContext?.findRenderObject() as RenderBox?;
    final RenderBox? imageBox =
        imageKey.currentContext?.findRenderObject() as RenderBox?;

    if (cartBox == null || imageBox == null) return;

    final cartPosition = Offset(
      screenWidth / totalItems * (cartIndex + 0.5) - 15,
      screenHeight - bottomBarHeight / 2 - 30,
    );

    final imagePosition =
        imageBox.localToGlobal(Offset.zero) +
        Offset(imageBox.size.width / 2, imageBox.size.height / 2);

    final imageSize = imageBox.size;

    final overlay = Overlay.of(context);
    late OverlayEntry overlayEntry;

    overlayEntry = OverlayEntry(
      builder:
          (context) => Positioned.fill(
            child: Stack(
              children: [
                AnimatedAddToCart(
                  start: imagePosition,
                  end: cartPosition,
                  imageSize: imageSize,
                  onComplete: () {
                    overlayEntry.remove();
                  },
                  urlImage: urlImage,
                ),
              ],
            ),
          ),
    );

    overlay.insert(overlayEntry);
  }

  onTapCartItem(CartItemModel model) async {
    // listCartItemModel
    //     ?.firstWhere((element) => element.cartItemId == model.cartItemId)
    //     .isSelect = !(model.isSelect ?? true);
    streamCartItemModels.set(listCartItemModel!);
    // await onCaculateCart();
    onUpdateSelected(model);
  }

  onUpdateSelected(CartItemModel model) async {
    final response = await Repository.onUpdateQuantity(
      branchId: Storage.branchModelGlobal?.branchId,
      cartItemId: model.cartItemId,
      quantity: model.quantity,
      select: !(model.selected ?? true),
    );

    if (response) {
      await onGetUserCart();
    }
  }

  onTapCreateOrder(BuildContext bContext) {
    Navigator.push(
      bContext,
      MaterialPageRoute(builder: (bContext) => CreateOrderScreen()),
    );
  }

  onCaculateCart() async {
    final response = await Repository.onCaculateOrder(
      Storage.cartModelGlobal?.cartId ?? 0,
    );

    streamCaculate.set(double.tryParse(response ?? 0) ?? 0);
  }

  onGetListOrderByStatus(OrderStatus status) async {
    final model = OrderFilterRequest(
      branchId: Storage.branchModelGlobal?.branchId,
      page: 1,
      pageSize: 20,
      userId: Storage.userModelGlobal?.userId,
      status: status,
    );

    final response = await Repository.onGetListOrder(model);

    switch (status) {
      case OrderStatus.pending:
        listPending = response;
        streamPending.add(listPending);
        break;
      case OrderStatus.paying:
        listPaying = response;
        streamPaying.add(listPaying);
        break;
      case OrderStatus.paid:
        listPaid = response;
        streamPaid.add(listPaid);
        break;
      case OrderStatus.delivering:
        listDelivering = response;
        streamDelivering.add(listDelivering);
        break;
      case OrderStatus.shipped:
        listShipped = response;
        streamShipped.add(listShipped);
        break;
      case OrderStatus.cancelling:
        listCancelling = response;
        streamCancelling.add(listCancelling);
        break;
      case OrderStatus.cancelled:
        listCancelled = response;
        streamCancelled.add(listCancelled);
        break;
      case OrderStatus.completed:
        listCompleted = response;
        streamCompleted.add(listCompleted);
        break;
      case OrderStatus.failed:
        listFailed = response;
        streamFailed.add(listFailed);
        break;
    }
  }
}
