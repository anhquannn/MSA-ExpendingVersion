import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:rxdart/rxdart.dart';
import '../../../../data/datasources/local/starage.dart';
import '../ui/product_detail_screen.dart';

class ProductDetailBloc extends BaseBloc<ProductDetailCustomerScreen> {
  final CartItemUseCase _cartItemUseCase = GetIt.I<CartItemUseCase>();
  bool isExpanded = false;
  final productModels = BehaviorSubject<List<ProductModel>>();

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
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  onTapProductDetail() {}
  onTapBack() {}

  onBuy(int productId) {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => CreateOrderScreen()),
    );
  }

  onAddToCart(int productId) async {
    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: productId,
        branchId: mockBranch.branchId ?? 0,
        quantity: 1,
      ),
    );
    if (data) {
      Navigator.pop(viewContext);
      return;
    }
    showCustomMessageError(viewContext);
  }
}
