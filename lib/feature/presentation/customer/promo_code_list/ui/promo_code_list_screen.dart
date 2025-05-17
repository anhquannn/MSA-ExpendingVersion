import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/widget/customBottomSheet.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../../../../../widget/custom_item_promocode.dart';
import '../bloc/promo_code_list_bloc.dart';

class PromoCodeListScreen extends BaseView<PromoCodeListBloc> {
  final List<PromoCodeModel> promoCodeList;
  const PromoCodeListScreen({super.key, required this.promoCodeList});

  @override
  PromoCodeListBloc createBloc() => PromoCodeListBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as PromoCodeListBloc;

    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(context, size: 25),
      centerTitle: true,
      title: customAutoSizeText(
        16,
        20,
        'Danh sách mã giảm giá',
        textColor: Colors.white,
      ),
      bodyBuilder: (controller) {
        return Padding(
          padding: const EdgeInsets.all(8.0),
          child: listPromoCode(promoCodeList, controller),
        );
      },
      hideBottomBarOnScroll: true,
    );
  }
}

Widget listPromoCode(List<PromoCodeModel> promoCodeList, ScrollController controller) {
  return ListView.builder(
    controller: controller,
    itemCount: promoCodeList.length,
    itemBuilder: (context, index) {
      final model = promoCodeList[index];
      return SizedBox(
        height: 180,
        child: widgetCustomItemPromoCode(
          model,
          () => _showPromoCodeSheet(context, 'Mã giảm giá', const SizedBox(), model),
        
        ),
      );
    },
  );
}

void _showPromoCodeSheet(
  BuildContext context,
  String title,
  Widget bodyWidget,
  PromoCodeModel model,
) {
  showCustomBottomSheet(
    context: context,
    title: model.name ?? '',
    bodyWidget: Column(
      mainAxisSize: MainAxisSize.min,
      spacing: 10,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _customTextSpan('Mã giảm giá: ', model.code ?? ''),
        _customTextSpan('Mô tả: ', model.description ?? ''),
        _customTextSpan(
          'Hạn sử dụng: ',
          '${model.startDate ?? ''} -- ${model.endDate ?? ''}',
        ),
        _customTextSpan(
          'Điều kiện áp dụng: ',
          'Dành cho đơn hàng có giá trị trên ${model.minimumOrderValue ?? ''}%',
        ),
        _customTextSpan(
          'Giảm giá: ',
          '${model.discountPercentage.toString()}đ',
        ),
        const SizedBox(height: 50), // để thử cuộn
      ],
    ),
  );
}

Widget _customTextSpan(String title, String body) {
  return customTextSpan(
    title,
    body,
    TextStyle(fontSize: 14, color: toHexToColor(secondaryTextColor)),
    TextStyle(
      fontSize: 15,
      color: toHexToColor(primaryButtonColor),
      fontWeight: FontWeight.bold,
    ),
  );
}

Widget widgetCustomItemPromoCode(
  PromoCodeModel model,
  VoidCallback onTap,
) {
  return InkWell(
    onTap: onTap,
    child: customItemPromoCode(model, () {
      
    }, () {}));
}
