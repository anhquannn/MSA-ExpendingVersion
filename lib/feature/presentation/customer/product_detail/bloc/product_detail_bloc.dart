import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/data/model/request/feedback_filter_request_model.dart';
import 'package:msa/feature/data/model/request/product_conbine_model_request.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/response/feedback_filter_response.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/product_detail/ui/product_list_detail_screen.dart';
import 'package:msa/feature/presentation/customer/product_list/ui/product_list_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:rxdart/rxdart.dart';
import '../../../../../core/config/config.dart';
import '../../../../../widget/custom_loading.dart';
import '../../../../data/datasources/local/starage.dart';
import '../../../../data/model/request/cartitem_selection_request_model.dart';
import '../ui/product_detail_screen.dart';

class ProductDetailBloc extends BaseBloc<ProductDetailCustomerScreen> {
  final CartItemUseCase _cartItemUseCase = GetIt.I<CartItemUseCase>();
  bool isExpanded = false;

  final productModels = BehaviorSubject<ProductFilterResult>();
  List<CartItemModel>? listCartItemModel = [];

  final streamCartItemModels = BehaviorSubject<List<CartItemModel>>.seeded([]);

  ProductModel? productModel;

  List<ProductModel>? productPopular = [];
  final streamProductPopular = BehaviorSubject<List<ProductModel>?>();

  List<ProductModel>? productCombine = [];
  final streamProductCombine = BehaviorSubject<List<ProductModel>?>();

  List<FeedbacFilterkResponse> listFeedback = [];
  final streamListFeedbak = BehaviorSubject<List<FeedbacFilterkResponse>>();

  double starCount = 0;
  @override
  String get contextKey => 'ProductDetailScreen';

  void onChangeExpanded() {
    isExpanded = !isExpanded;
    setState(() {});
  }

  @override
  void onInit() {
    if (widget.productModel != null) {
      productModel = widget.productModel;
    }
  }

  @override
  void onDispose() {}

  @override
  void onReady() {
    Future.wait<void>([
      onGetProduct(),
      onGetProducts(),
      onGetProductCombination(),
      onGetProductPopular(),
      // onGetFeedBack(),
    ]);
  }

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  onTapBack() {}

  onBuy(int productId) {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => CreateOrderScreen()),
    );
  }

  buildCheck(BuildContext bContext) {
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
    }
  }

  onBuyNow(ProductModel model, BuildContext bContext) async {
    buildCheck(bContext);
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

  onAddToCart(ProductModel model, BuildContext bcontext) async {
    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: model.productId ?? 0,
        branchId: mockBranch.branchId ?? 0,
        quantity: 1,
      ),
    );
    if (data) {
      onGetUserCart();
      Navigator.pop(bcontext);
      return;
    } else {
      showCustomMessageError(bcontext);
    }
  }

  onAddOtherProductToCart(ProductModel model, BuildContext bcontext) async {
    showFullScreenLoading(bcontext);
    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: model.productId ?? 0,
        branchId: mockBranch.branchId ?? 0,
        quantity: 1,
      ),
    );

    hideFullScreenLoading(bcontext);
    if (data) {
      showCustomDialog(
        bcontext,
        AppSize.width(),
        AppSize.width(),
        'Thông báo',
        Text('Thêm sản phẩm vào giỏ hàng thành công'),
        true,
        false,
        Icon(Icons.check_circle_outline, color: Colors.green),
        onClose: () {
          Navigator.pop(bcontext);
        },
      );
    } else {
      showCustomMessageError(bcontext);
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

  onGetProducts() async {
    try {
      ProductFilterRequest filter = ProductFilterRequest(
        page: 1,
        pageSize: 10,
        categoryId: [(productModel?.category?.categoryId) ?? 0],
        branchId: Storage.branchModelGlobal?.branchId,
      );

      ProductFilterResult product = await Repository.onFilterProducts(filter);

      productModels.add(product);
    } catch (e) {
      print('Lỗi khi lấy danh sách sản phẩm: $e');
      productModels.add(ProductFilterResult()); // Fallback nếu có lỗi
    }
  }

  onGetProductPopular() async {
    try {
      ProductFilterRequest filter = ProductFilterRequest(
        page: 1,
        pageSize: 10,
        branchId: Storage.branchModelGlobal?.branchId,
      );

      ProductFilterResult product = await Repository.onFilterProducts(filter);
      productPopular = product.productsPage?.content;
      streamProductPopular.add(productPopular);
    } catch (e) {
      print('Lỗi khi lấy danh sách sản phẩm: $e');
      productModels.add(ProductFilterResult()); // Fallback nếu có lỗi
    }
  }

  onGetProduct() async {
    if (widget.productId == null) return;
    if (widget.productId != null) {
      final data = await Repository.onGetProductById(widget.productId ?? 0);
      if (data != null) {
        productModel = data;
        onGetFeedBack(id: productModel?.productId);
        setState(() {});
      }
    }
  }

  onGetProductCombination() async {
    final ProductCombinationFilterRequest request =
        ProductCombinationFilterRequest(productId1: productModel?.productId);
    final response = await Repository.onGetConbineProduct(request);
    if (response != null) {
      productCombine = response;
      streamProductCombine.set(productCombine);
    }
  }

  onTapPopularProduct(BuildContext bContext, List<ProductModel> models) {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => ProductListDetailScreen(
              productList: models,
              listCartItemModel: listCartItemModel,
              isSale: true,
              title: 'Sản phẩm phổ biến',
            ),
      ),
    );
  }

  onTapCombineProduct(BuildContext bContext, List<ProductModel> models) {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => ProductListDetailScreen(
              productList: models,
              listCartItemModel: listCartItemModel,
              isSale: true,
              title: 'Sản phẩm kết hợp',
            ),
      ),
    );
  }

  onTapSeeAllProduct(BuildContext bContext, List<ProductModel> models) {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder:
            (context) => ProductListDetailScreen(
              productList: models,
              listCartItemModel: listCartItemModel,
              isSale: true,
              title: 'Sản phẩm liên quan',
            ),
      ),
    );
  }

  onGetFeedBack({int? id}) async {
    final response = await Repository.getFeedback(
      FeedbackFilterRequest(pageSize: 10, productId: id),
    );

    int count = 0;
    response.forEach((element) {
      count += element.rating ?? 0;
    });

    if (count > 0) {
      starCount = count / response.length;
    } else {
      starCount = 0;
    }

    if (response.isNotEmpty && response != []) {
      listFeedback = response;
    }
    streamListFeedbak.set(listFeedback);
    setState(() {});
  }
}
