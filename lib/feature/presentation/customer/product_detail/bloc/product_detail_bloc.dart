import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:rxdart/rxdart.dart';
import '../../../../data/datasources/local/starage.dart';
import '../ui/product_detail_screen.dart';

class ProductDetailBloc extends BaseBloc<ProductDetailCustomerScreen> {
  final CartItemUseCase _cartItemUseCase = GetIt.I<CartItemUseCase>();
  bool isExpanded = false;
  final productModels = BehaviorSubject<ProductFilterResult>();
  List<CartItemModel>? listCartItemModel = [];
  final streamCartItemModels = BehaviorSubject<List<CartItemModel>>.seeded([]);

  @override
  String get contextKey => 'ProductDetailScreen';

  void onChangeExpanded() {
    isExpanded = !isExpanded;
    setState(() {});
  }

  ProductModel productModel = ProductModel(
    name: 'Product Name',
    description: 'Product Description',
  );
  @override
  void onInit() {}

  @override
  void onDispose() {}

  @override
  void onReady() {
    onGetProduct();
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

  onGetProduct() async {
    try {
      ProductFilterRequest filter = ProductFilterRequest(
        page: 1,
        pageSize: 10,
        branchId: Storage.branchModelGlobal?.branchId,
      );

      ProductFilterResult product = await Repository.onFilterProducts(filter);

      productModels.add(product);
    } catch (e) {
      print('Lỗi khi lấy danh sách sản phẩm: $e');
      productModels.add(ProductFilterResult()); // Fallback nếu có lỗi
    }
  }
}
