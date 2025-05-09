import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../ui/product_screen.dart';

class ProductBloc extends BaseBloc<ProductScreen> {
  final TextEditingController searchController = TextEditingController();
  final FocusNode searchFocusNode = FocusNode();

  @override
  void onInit() {}

  @override
  void onDispose() {
    searchController.dispose();
  }

  @override
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  void onSetting() {}
  void onLogout() {}
  void onTapHone() {}
  void onUpdateProduct() {}
  void onDeleteProduct() {}
}
