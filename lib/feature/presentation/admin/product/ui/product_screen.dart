import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_textfield.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

import '../../../../../widget/custom_item_product.dart';
import '../bloc/product_bloc.dart';

class ProductScreen extends BaseView<ProductBloc> {
  const ProductScreen({super.key});

  @override
  ProductBloc createState() => ProductBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as ProductBloc;
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(bloc.viewContext,size: 25),
      centerTitle: true,
      title: customTextField(
        node: bloc.searchFocusNode,
        borderRadius: 30,
        fillColor: Colors.white,
        bloc.searchController,
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
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
                  },
                ),
                customItemProduct(
                  () {
                    bloc.onUpdateProduct();
                  },
                  () {
                    bloc.onDeleteProduct();
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
