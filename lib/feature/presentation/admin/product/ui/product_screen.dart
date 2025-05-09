import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/custom_textfield.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

import '../../../../../widget/custom_dropshadow.dart';
import '../../../../../widget/custom_item_product.dart';
import '../../../../../widget/icon_wrap.dart';
import '../bloc/product_bloc.dart';

class ProductScreen extends BaseView<ProductBloc> {
  const ProductScreen({super.key});

  @override
  ProductBloc createState() => ProductBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as ProductBloc;
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(size: 25),
      centerTitle: true,
      title: customTextField(
        node: _bloc.searchFocusNode,
        borderRadius: 30,
        fillColor: Colors.white,
        _bloc.searchController,
        prefixIcon: Icon(Icons.search, color: toHexToColor(iconColor)),
      ),
      bodyBuilder: (controller) {
        return Container(
          width: AppSize.width(),
          color: toHexToColor(backgroundColor),
          child: SingleChildScrollView(
            controller: controller,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                SizedBox(height: 10),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    _bloc.onUpdateProduct();
                  },
                  () {
                    _bloc.onDeleteProduct();
                  },
                ),
                SizedBox(height: 20),
              ],
            ),
          ),
        );
      },
      hideBottomBarOnScroll: true,
    );
  }

  @override
  ProductBloc createBloc() => ProductBloc();
}
