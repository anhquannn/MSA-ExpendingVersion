import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_textfield.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../bloc/promocode_bloc.dart';

class PromoCodeScreen extends BaseView<PromoCodeBloc> {
  const PromoCodeScreen({super.key});

  @override
  PromoCodeBloc createState() => PromoCodeBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as PromoCodeBloc;
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(bloc.viewContext,size: 25),
      centerTitle: true,
      title: customTextField(
        node: bloc.searchFocusNode,
        borderRadius: 30,
        bloc.searchController,
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
              // SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              // SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              // SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              // SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              // SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              // SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              // SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
              // SliverToBoxAdapter(child: customItemPromoCode(() {}, () {})),
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
