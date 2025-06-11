import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/domain/usecase/cart_use_case.dart';
import 'package:msa/feature/domain/usecase/category_use_case.dart';
import 'package:msa/feature/domain/usecase/product_use_case.dart';
import 'package:msa/feature/domain/usecase/promo_code_use_case.dart';
import 'package:msa/feature/domain/usecase/user_use_case.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';

class CreateOrderBloc extends BaseBloc<CreateOrderScreen> {
  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();
  final CategoryUseCase _categoryUseCase = GetIt.I<CategoryUseCase>();
  final ProductUseCase _productUseCase = GetIt.I<ProductUseCase>();
  final PromoCodeUseCase _promoCodeUseCase = GetIt.I<PromoCodeUseCase>();
  final CartItemUseCase _cartItemUseCase = GetIt.I<CartItemUseCase>();
  final CartUseCase _cartUseCase = GetIt.I<CartUseCase>();

  bool isZaloPaySelected = false;
  @override
  String get contextKey => 'CreateOrderScreen';

  @override
  void onInit() {}

  @override
  void onDispose() {}

  @override
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext viewContext) => widget.build(viewContext);

  onBuy() {}
  onMinus(int data) {}
  onPlus(int data) {}
}
