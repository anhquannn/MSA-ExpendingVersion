import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import '../ui/product_detail_screen.dart';

class ProductDetailBloc extends BaseBloc<ProductDetailCustomerScreen> {
  bool isExpanded=false;

  void onChangeExpanded(){
    isExpanded=!isExpanded;
    setState(() {

    });
  }

  ProductModel productModel = ProductModel(
   
    name: 'Product Name',
    description: 'Product Description',
   
  );
  @override
  void onInit() {}

  @override
  void onDispose() {
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
}
