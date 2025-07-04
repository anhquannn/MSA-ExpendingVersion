import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/data/model/request/cartitem_selection_request_model.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/custom_loading.dart';
import 'package:rxdart/subjects.dart';

import '../ui/product_list_screen.dart';

class ProductListBloc extends BaseBloc<ProductListScreen> {
  ProductFilterResult? listProducts;
  final streamProductModels = BehaviorSubject<ProductFilterResult>();
  final CartItemUseCase _cartItemUseCase = GetIt.I<CartItemUseCase>();
  int page = 1;

  @override
  String get contextKey => 'ProductListScreen';
  @override
  void onInit() {
    listProducts = widget.productList;
    streamProductModels.set(listProducts ?? ProductFilterResult());
  }

  @override
  void onDispose() {}

  @override
  void onReady() async {
    await onGetProduct();
  }

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  onGetProduct({bool isLoadMore = false, int? pagei}) async {
    if (isLoadMore) {
      page = pagei ?? page + 1;
    } else {
      page = page;
    }
    try {
      ProductFilterRequest filter = ProductFilterRequest(
        page: page,
        pageSize: 10,
        categoryId: widget.category?.categoryId,
        branchId: Storage.branchModelGlobal?.branchId,
      );

      ProductFilterResult product = await Repository.onFilterProducts(filter);
      if (isLoadMore) {
        listProducts?.products?.addAll(product.products ?? []);
        listProducts?.discountedProductsPage?.content.addAll(
          product.discountedProductsPage?.content ?? [],
        );
        listProducts?.productsPage?.content.addAll(
          product.productsPage?.content ?? [],
        );
        listProducts?.discountedProducts?.addAll(
          product.discountedProducts ?? [],
        );
      } else {
        listProducts = product;
      }
      streamProductModels.add(listProducts ?? ProductFilterResult());
      setState(() {});
    } catch (e, stack) {
      print('❌ Lỗi khi lấy danh sách sản phẩm: $e');
      print('📛 Stacktrace: $stack');
      streamProductModels.add(ProductFilterResult());
    }
    setState(() {});
  }

  onBuyNow(ProductModel model, BuildContext bContext) async {
    showFullScreenLoading(bContext);
    List<int> cartIds = [];
    print('🛒 1111111111Danh sách cartItemIds: $cartIds');
    widget.listCartItemModel?.forEach((element) {
      print(element.cartItemId);
      cartIds.add(element.cartItemId ?? 0);
    });

    print('🛒 Danh sách cartItemIds: $cartIds');

    final CartItemSelectionRequest request = CartItemSelectionRequest(
      cartId: Storage.cartModelGlobal?.cartId ?? 0,
      cartItemIds: cartIds,
    );

    print('📦 Request cập nhật cart selection: ${request.toJson()}');

    final updateResponse = await Repository.onUpdateCartItemsSelectionAPI(
      request,
      false,
    );

    print('✅ Kết quả cập nhật cart selection: $updateResponse');

    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: model.productId ?? 0,
        branchId: Storage.branchModelGlobal?.branchId ?? 0,
        quantity: 1,
      ),
    );

    print('🛒 Kết quả thêm vào giỏ: $data');

    if (data) {
      hideFullScreenLoading(bContext);
      Navigator.push(
        bContext,
        MaterialPageRoute(builder: (bContext) => CreateOrderScreen()),
      );
    } else {
      hideFullScreenLoading(bContext);
      print('❌ Không thể thêm sản phẩm vào giỏ hàng.');
    }
  }

  onAddToCart(ProductModel model, BuildContext bcontext) async {
    showFullScreenLoading(bcontext);
    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: model.productId ?? 0,
        branchId: Storage.branchModelGlobal?.branchId ?? 0,
        quantity: 1,
      ),
    );
    if (data) {
      hideFullScreenLoading(bcontext);
      showCustomSuccessError(bcontext, 'Thêm vào giỏ hàng thành công', () {
        Navigator.pushAndRemoveUntil(
          context,
          MaterialPageRoute(builder: (context) => HomeScreen()),
          (route) => false,
        );
      });
      // Navigator.pop(bcontext);
    } else {
      showCustomMessageError(bcontext);
    }
  }
}
