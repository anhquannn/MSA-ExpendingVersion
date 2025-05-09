import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../../../../../widget/custom_item_promocode.dart';
import '../bloc/promo_code_list_bloc.dart';

class PromoCodeListScreen extends BaseView<PromoCodeListBloc> {
  const PromoCodeListScreen({super.key});

  @override
  PromoCodeListBloc createBloc() => PromoCodeListBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as PromoCodeListBloc;

    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(size: 25),
      centerTitle: true,
      title: customAutoSizeText(
        16,
        20,
        'Danh sách mã giảm giá',
        textColor: Colors.white,
      ),
      bodyBuilder: (controller) {
        return SizedBox(
          width: AppSize.width(),
          child: SingleChildScrollView(
            controller: controller,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                SizedBox(
                  height: 180,
                  child: widgetCustomItemPromoCode(() {}, () {}),
                ),
                SizedBox(
                  height: 180,
                  child: widgetCustomItemPromoCode(() {}, () {}),
                ),
                SizedBox(
                  height: 180,
                  child: widgetCustomItemPromoCode(() {}, () {}),
                ),
                SizedBox(
                  height: 180,
                  child: widgetCustomItemPromoCode(() {}, () {}),
                ),
                SizedBox(
                  height: 180,
                  child: widgetCustomItemPromoCode(() {}, () {}),
                ),
              ],
            ),
          ),
        );
      },
      hideBottomBarOnScroll: true,
    );
  }
}

Widget widgetCustomItemPromoCode(VoidCallback onTap1, VoidCallback onTap2) {
  return Center(child: _customItemPromoCode(onTap1, onTap2));
}

Widget _customItemPromoCode(VoidCallback onTap1, VoidCallback onTap2) {
  return GestureDetector(
    onTap: onTap1,
    child: SizedBox(
      width: AppSize.w(0.9),
      // height: 300,
      // color: Colors.white,
      child: customItemPromoCode(() {}, () {}),
    ),
  );
}
