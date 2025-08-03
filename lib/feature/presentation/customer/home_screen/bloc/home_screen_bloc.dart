import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/data/model/request/cartitem_selection_request_model.dart';
import 'package:msa/feature/data/model/request/category_filter_request.dart';
import 'package:msa/feature/data/model/request/feedback_request_model.dart';
import 'package:msa/feature/data/model/request/get_branch_request_model.dart';
import 'package:msa/feature/data/model/request/order_paging_request_model.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/request/promocode_request_model.dart';
import 'package:msa/feature/data/model/request/return_order_filter_request.dart'
    show ReturnOrderFilterRequest;
import 'package:msa/feature/data/model/request/update_notification_request.dart';
import 'package:msa/feature/data/model/response/branch_response_response.dart';
import 'package:msa/feature/data/model/response/get_order_response_model.dart';
import 'package:msa/feature/data/model/response/notification_request_model.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/address_model.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/notification_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/domain/usecase/cart_use_case.dart';
import 'package:msa/feature/domain/usecase/user_use_case.dart';
import 'package:msa/feature/presentation/customer/category_list/ui/category_list_screen.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/change_address_screen.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/selec_branch_screen.dart';
import 'package:msa/feature/presentation/customer/order_detail/ui/order_detail_screen.dart';
import 'package:msa/feature/presentation/customer/persional/ui/persional_screen.dart';
import 'package:msa/feature/presentation/customer/product_detail/ui/product_detail_screen.dart';
import 'package:msa/feature/presentation/customer/product_filter/ui/product_filter_screen.dart';
import 'package:msa/feature/presentation/customer/product_list/ui/product_list_screen.dart';
import 'package:msa/feature/presentation/customer/promo_code_list/ui/promo_code_list_screen.dart';
import 'package:msa/widget/animate_add_to_cart.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/custom_loading.dart';
import 'package:rxdart/rxdart.dart';
import '../../../../../widget/custom_customer_lead.dart';
import '../../../../data/datasources/local/starage.dart';
import '../../../../data/model/request/return_order_response_model.dart';
import '../ui/home_screen.dart';
import 'package:get/get.dart';

class HomeScreenBloc extends BaseBloc<HomeScreen> {
  /// ==== UseCases ====
  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();
  final CartUseCase _cartUseCase = GetIt.I<CartUseCase>();
  final CartItemUseCase _cartItemUseCase = GetIt.I<CartItemUseCase>();

  /// ==== GlobalKeys & Controllers ====
  final GlobalKey cartIconKey = GlobalKey();
  final GlobalKey cartIconKey1 = GlobalKey();
  final Map<int, GlobalKey> imageKeys = {};
  final PageController categoryController = PageController(initialPage: 0);
  final ScrollController scrollController = ScrollController();
  final ValueNotifier<int> indexScreen = ValueNotifier(0);
  final TextEditingController rateController = TextEditingController();

  /// ==== User & Cart Models ====
  UserModel? userModel;
  CartModel? cartModel;

  /// ==== Static Data ====
  ProductFilterResult? listProducts;
  List<CategoryModel>? listCategoryModel = [];
  List<PromoCodeModel>? listPromocode = [];
  List<CartItemModel>? listCartItemModel = [];
  List<NotificationModel>? listNotificattionRead;
  List<NotificationModel>? listNotificattionUnRead;

  /// ==== Orders ====
  List<OrderResponse> listPending = [];
  List<OrderResponse> listPaying = [];
  List<OrderResponse> listPaid = [];
  List<OrderResponse> listDelivering = [];
  List<OrderResponse> listShipped = [];
  List<OrderResponse> listCancelling = [];
  List<OrderResponse> listCancelled = [];
  List<OrderResponse> listCompleted = [];
  List<OrderResponse> listFailed = [];
  List<ReturnOrderModel> listReturn = [];

  /// ==== Streams ====
  final BehaviorSubject<UserModel> streamUserModel =
      BehaviorSubject<UserModel>();
  final BehaviorSubject<CartModel> streamCartModel =
      BehaviorSubject<CartModel>();
  final BehaviorSubject<List<CategoryModel>> streamCategoryModels =
      BehaviorSubject<List<CategoryModel>>();
  final BehaviorSubject<ProductFilterResult> streamProductModels =
      BehaviorSubject<ProductFilterResult>();
  final BehaviorSubject<List<PromoCodeModel>> streamPromoCodeModels =
      BehaviorSubject<List<PromoCodeModel>>();
  final BehaviorSubject<List<CartItemModel>> streamCartItemModels =
      BehaviorSubject<List<CartItemModel>>.seeded([mockCartItem]);
  final BehaviorSubject<CartModel> cartModels = BehaviorSubject<CartModel>();

  final BehaviorSubject<List<OrderResponse>> streamPending =
      BehaviorSubject<List<OrderResponse>>();
  final BehaviorSubject<List<OrderResponse>> streamPaying =
      BehaviorSubject<List<OrderResponse>>();
  final BehaviorSubject<List<OrderResponse>> streamPaid =
      BehaviorSubject<List<OrderResponse>>();
  final BehaviorSubject<List<OrderResponse>> streamDelivering =
      BehaviorSubject<List<OrderResponse>>();
  final BehaviorSubject<List<OrderResponse>> streamShipped =
      BehaviorSubject<List<OrderResponse>>();
  final BehaviorSubject<List<OrderResponse>> streamCancelling =
      BehaviorSubject<List<OrderResponse>>();
  final BehaviorSubject<List<OrderResponse>> streamCancelled =
      BehaviorSubject<List<OrderResponse>>();
  final BehaviorSubject<List<OrderResponse>> streamCompleted =
      BehaviorSubject<List<OrderResponse>>();
  final BehaviorSubject<List<OrderResponse>> streamFailed =
      BehaviorSubject<List<OrderResponse>>();
  final BehaviorSubject<List<ReturnOrderModel>> streamReturn =
      BehaviorSubject<List<ReturnOrderModel>>();

  final BehaviorSubject<List<NotificationModel>> streamListNotificationRead =
      BehaviorSubject<List<NotificationModel>>();
  final BehaviorSubject<List<NotificationModel>> streamListNotificationUnRead =
      BehaviorSubject<List<NotificationModel>>();

  final BehaviorSubject<double> streamCaculate = BehaviorSubject<double>();

  double reward = 0;
  final streamReward = BehaviorSubject<double>();
  int pageReturnOrder = 1;

  /// ==== UI Flags ====
  bool _isManuallyScrolling = false;
  bool _isAnimatingPage = false;
  bool _hasInitCalled = false;

  /// ==== Debouncers ====
  final Map<int, Debouncer> _debouncers = {};
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
    WidgetsBinding.instance.addPostFrameCallback((_) {
      indexScreen.value = 0;
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
    scrollController.dispose();
    rateController.dispose();

    indexScreen.dispose();

    // Dispose toàn bộ streams
    streamUserModel.close();
    streamCartModel.close();
    streamCategoryModels.close();
    streamProductModels.close();
    streamPromoCodeModels.close();
    streamCartItemModels.close();
    cartModels.close();

    streamPending.close();
    streamPaying.close();
    streamPaid.close();
    streamDelivering.close();
    streamShipped.close();
    streamCancelling.close();
    streamCancelled.close();
    streamCompleted.close();
    streamFailed.close();

    streamListNotificationRead.close();
    streamListNotificationUnRead.close();

    streamCaculate.close();
  }

  @override
  void onReady() {
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

  onGetAddress() async {
    if (Storage.addressModel == null) {
      final response = await Repository.getUserAddresses();
      Storage.saveAddress(response[0]);
    } else {}
  }

  onCheckBranch(BuildContext context) async {
    if (Storage.branchModelGlobal == null) {
      Navigator.push(
        this.context,
        MaterialPageRoute(builder: (context) => SelectBranchScreen()),
      );
    }
  }

  onRefresh() async {
    if (Storage.token == '') {
      final List<Future<void>> future = [
        onGetPromoCode().catchError((e) => ('Lỗi promo code: $e')),
        onGetCategory().catchError((e) => ('Lỗi category: $e')),
        onGetProduct().catchError((e) => ('Lỗi product: $e')),
      ];
      try {
        await Future.wait(future);
      } catch (e) {
        // Bạn có thể xử lý lỗi tổng quát ở đây, ví dụ hiển thị thông báo lỗi cho người dùng
      }
    }
    final List<Future<void>> futures = [
      onGetProfile().catchError((e) => ('Lỗi profile: $e')),
      onGetPromoCode().catchError((e) => ('Lỗi promo code: $e')),
      onGetCategory().catchError((e) => ('Lỗi category: $e')),
      onGetProduct().catchError((e) => ('Lỗi product: $e')),
      onGetAddress().catchError((e) => ('Lỗi onGetAddress: $e')),
      onGetUserCart(),
      // onCheckBranch().catchError((e) => ('Lỗi onCheckBranch: $e')),
      // onGetUserCart().catchError((e) => ('Lỗi onGetUserCart: $e')),
      onGetReward().catchError((e) => ('Lỗi onGetReward: $e')),
      onGetNotification().catchError((e) => ('Lỗi onGetNotification: $e')),
      Repository.onUpdateDeviceId(Storage.userModelGlobal?.userId ?? 0),
    ];

    try {
      await Future.wait(futures);
    } catch (e) {
      // Bạn có thể xử lý lỗi tổng quát ở đây, ví dụ hiển thị thông báo lỗi cho người dùng
    }
  }

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext viewContext) => widget.build(viewContext);

  onGetPromoCode() async {
    try {
      List<PromoCodeModel>? promoCode = await Repository.onGetAllPromoCode(
        PromoCodeRequestModel(userId: Storage.userModelGlobal?.userId ?? 0),
      );

      streamPromoCodeModels.add(promoCode ?? []);
      listPromocode = promoCode;
    } catch (e) {
      streamPromoCodeModels.add([]);
    }
  }

  onGetProfile({BuildContext? bcontext}) async {
    try {
      UserModel? user = await Repository.onGetUserInfo();
      if (user != null) {
        streamUserModel.add(user);
        Storage.userModelGlobal = user;
        Storage.saveUserModel(user);
        userModelGlobal = user;
        userModel = user;
        await onGetOrCreateCart(bcontext: bcontext);
      } else {
        streamUserModel.add(UserModel());
      }
    } catch (e) {
      streamUserModel.add(UserModel());
    }
  }

  onGetProduct() async {
    try {
      ProductFilterRequest filter = ProductFilterRequest(
        page: 1,
        pageSize: 50,
        branchId: Storage.branchModelGlobal?.branchId,
      );
      final ProductFilterResult? product = await Repository.onFilterProducts(
        filter,
      );

      if (product != null) {
        listProducts = product;
        streamProductModels.add(product);
      } else {}
    } catch (e, stack) {
      streamProductModels.add(ProductFilterResult());
    }

    if (mounted) {
      setState(() {});
    }
  }

  onGetCategory() async {
    try {
      final response = await Repository.onGetAllCategory(
        CategoryFilterRequest(),
      );
      listCategoryModel = response;
      streamCategoryModels.set(response);
    } catch (e) {
      streamCategoryModels.add([]); // Fallback nếu có lỗi
    }
  }

  filter(BuildContext ctex) {
    Navigator.push(
      ctex,
      MaterialPageRoute(
        builder:
            (ctex) => ProductFilterScreen(listCartItemModel: listCartItemModel),
      ),
    );
  }

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

  onTapCategory(CategoryModel model) {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => ProductListScreen(
              // productList: listProducts,
              title: 'Sản phẩm ${model.name}',
              listCartItemModel: listCartItemModel,
              isSale: true,
              category: model,
            ),
      ),
    );
  }

  onTapProductSale() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => ProductListScreen(
              productList: listProducts,
              listCartItemModel: listCartItemModel,
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
              productList: listProducts,
              isSale: false,
              listCartItemModel: listCartItemModel,
            ),
      ),
    );
  }

  onTapProductDetail(ProductModel model, {FreePromotionGroup? promotion}) {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => ProductDetailCustomerScreen(
              productModel: model,
              productId: model.productId,
              promotion: promotion,
            ),
      ),
    );
  }

  onBuyNow(ProductModel model, BuildContext bContext) async {
    if (buildCheck(bContext) == false) return;
    showFullScreenLoading(bContext);
    List<int> cartIds = [];
    listCartItemModel?.forEach((element) {
      cartIds.add(element.cartItemId ?? 0);
    });

    final CartItemSelectionRequest request = CartItemSelectionRequest(
      cartId: Storage.cartModelGlobal?.cartId ?? 0,
      cartItemIds: cartIds,
    );

    final updateResponse = await Repository.onUpdateCartItemsSelectionAPI(
      request,
      false,
    );

    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: model.productId ?? 0,
        branchId: Storage.branchModelGlobal?.branchId ?? 0,
        quantity: 1,
      ),
    );

    if (data) {
      hideFullScreenLoading(bContext);
      Navigator.push(
        bContext,
        MaterialPageRoute(builder: (bContext) => CreateOrderScreen()),
      );
    } else {
      hideFullScreenLoading(bContext);
    }
  }

  buildCheck(BuildContext bContext) {
    final isLogin = checkLogin(bContext);
    if (isLogin == false) return false;
    if (Storage.branchModelGlobal == null) {
      showCustomDialog(
        bContext,
        AppSize.width(),
        AppSize.width(),
        'Thông báo',
        Text('Bạn chưa chọn chi nhánh'),
        true,
        false,
        Icon(Icons.warning, color: Colors.yellow),
        onClose: () {
          Navigator.pop(bContext);
        },
      );
      return false;
    }
    return true;
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
            bcontext != null ? showCustomMessageError(bcontext) : null;
            return <CartItemModel>[];
          },
        );
    // await onCaculateCart();
    if (data != null) {
      listCartItemModel = data ?? [];
      streamCartItemModels.add(data);
    }
  }

  onCaculate(CartItemModel model, bool isMinus) {
    if (model.quantity == 1 && isMinus == true) return;
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

  onAddToCart(ProductModel model, BuildContext bcontext, GlobalKey key) async {
    if (buildCheck(bcontext) == false) return;
    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: model.productId ?? 0,
        branchId: Storage.branchModelGlobal?.branchId ?? 0,
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
      await onGetUserCart();
      return;
    }
    showCustomMessageError(bcontext);
  }

  runAddToCartAnimation({
    required BuildContext context,
    required GlobalKey cartKey,
    required GlobalKey imageKey,
    String? urlImage,
  }) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    const totalItems = 4;
    const cartIndex = 1;
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

  onUpdateSelected(CartItemModel model, BuildContext bContext) async {
    buildCheck(bContext);
    showFullScreenLoading(bContext);
    final response = await Repository.onUpdateQuantity(
      branchId: Storage.branchModelGlobal?.branchId,
      cartItemId: model.cartItemId,
      quantity: model.quantity,
      select: !(model.selected ?? true),
    );

    if (response) {
      await onGetUserCart();
    }

    hideFullScreenLoading(bContext);
  }

  onTapCreateOrder(BuildContext bContext) {
    buildCheck(bContext);
    Navigator.push(
      bContext,
      MaterialPageRoute(builder: (bContext) => CreateOrderScreen()),
    );
  }

  onCaculateCart() async {
    // final response = await Repository.onCaculateOrder(
    //   Storage.cartModelGlobal?.cartId ?? 0,
    // );
    //
    // streamCaculate.set(double.tryParse(response ?? 0) ?? 0);
  }

  onGetListOrderByStatus(OrderStatus status) async {
    final model = OrderFilterRequest(
      // branchId: Storage.branchModelGlobal?.branchId,
      page: 1,
      pageSize: 20,
      userId: Storage.userModelGlobal?.userId,
      status: status,
    );

    final modelReturn = ReturnOrderFilterRequest(
      page: pageReturnOrder,
      userId: Storage.userModelGlobal?.userId,
    );

    List<OrderResponse>? response = [];
    List<ReturnOrderModel>? returnResponse = [];
    if (status == OrderStatus.returnOrder) {
      returnResponse = await Repository.getAllReturnOrder(modelReturn);
    } else {
      response = await Repository.onGetListOrder(model);
    }

    switch (status) {
      case OrderStatus.pending:
        listPending = response ?? [];
        streamPending.add(listPending);
        break;
      case OrderStatus.paying:
        listPaying = response ?? [];
        streamPaying.add(listPaying);
        break;
      case OrderStatus.paid:
        listPaid = response ?? [];
        streamPaid.add(listPaid);
        break;
      case OrderStatus.delivering:
        listDelivering = response ?? [];
        streamDelivering.add(listDelivering);
        break;
      case OrderStatus.shipped:
        listShipped = response ?? [];
        streamShipped.add(listShipped);
        break;
      case OrderStatus.cancelling:
        listCancelling = response ?? [];
        streamCancelling.add(listCancelling);
        break;
      case OrderStatus.cancelled:
        listCancelled = response ?? [];
        streamCancelled.add(listCancelled);
        break;
      case OrderStatus.completed:
        listCompleted = response ?? [];
        streamCompleted.add(listCompleted);
        break;
      case OrderStatus.failed:
        listFailed = response ?? [];
        streamFailed.add(listFailed);
      case OrderStatus.returnOrder:
        listReturn = returnResponse ?? [];
        streamReturn.add(listReturn);
        break;
    }
  }

  onTapOrderDetail(BuildContext bContext, OrderResponse model) {
    Navigator.push(
      bContext,
      MaterialPageRoute(builder: (bContext) => OrderDetailScreen(order: model)),
    );
  }

  onSelectBranch(BuildContext bcontext) async {
    final data = await Navigator.push(
      bcontext,
      MaterialPageRoute(
        builder:
            (bcontext) => SelectBranchScreen(
              isPrimary: true,
              branchId: Storage.branchModelGlobal?.branchId,
            ),
      ),
    );
  }

  onGetNotificationRead() async {
    final model = NotificationFilterRequest(
      page: 1,
      pageSize: 20,
      isRead: true,
      userId: Storage.userModelGlobal?.userId,
    );

    final List<NotificationModel>? response = await Repository.getNotification(
      model,
    );

    if (response != null) {
      listNotificattionRead = response;
      streamListNotificationRead.set(listNotificattionRead ?? []);
    } else {}
  }

  onGetNotificationUnRead() async {
    final model = NotificationFilterRequest(
      page: 1,
      pageSize: 20,
      isRead: false,
      userId: Storage.userModelGlobal?.userId,
    );

    final List<NotificationModel>? response = await Repository.getNotification(
      model,
    );

    if (response != null) {
      listNotificattionUnRead = response;
      streamListNotificationUnRead.set(listNotificattionUnRead ?? []);
    }
  }

  onGetNotification() async {
    Future.wait<void>([onGetNotificationRead(), onGetNotificationUnRead()]);
  }

  onUpdateNotification(int notificationId) async {
    final response = await Repository.updateNotification(
      UpdateNotificationRequest(isRead: true),
      notificationId,
    );
    if (response) {
      await onGetNotification();
    }
  }

  onGetReward() async {
    final response = await Repository.getReward();
    reward = response;
    streamReward.set(response);
  }
}
