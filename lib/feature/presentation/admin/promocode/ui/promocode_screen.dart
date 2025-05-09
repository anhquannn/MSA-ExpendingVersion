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
import '../../../../../widget/custom_item_promocode.dart';
import '../bloc/promocode_bloc.dart';

class PromoCodeScreen extends BaseView<PromoCodeBloc> {
  const PromoCodeScreen({super.key});

  @override
  PromoCodeBloc createState() => PromoCodeBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as PromoCodeBloc;
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(size: 25),
      centerTitle: true,
      title: customTextField(
        node: _bloc.searchFocusNode,
        borderRadius: 30,
        _bloc.searchController,
        prefixIcon: Icon(Icons.search, color: toHexToColor(iconColor)),
      ),
      bodyBuilder: (controller) {
        return Container(
          width: AppSize.width(),
          color: toHexToColor(backgroundColor),
          child: CustomScrollView(
            controller: controller,
            slivers: [
              SliverToBoxAdapter(child: SizedBox(height: 10)),
              SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              SliverToBoxAdapter(child: SizedBox(height: 10)),
            ],
          ),
        );
      },
      hideBottomBarOnScroll: true,
    );
  }

  @override
  PromoCodeBloc createBloc() => PromoCodeBloc();
}

class ProductModel {
  final String image =
      'https://pixabay.com/illustrations/draw-nature-landscape-free-image-3583548/';
  final String name = 'Dưa hấu';
  final String price = '1.000.000đ';
  final String sale = '10000';
  final String stock = '100';
  final String stockLevel = 'Low';
}
