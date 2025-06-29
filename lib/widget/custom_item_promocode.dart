import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';

import '../core/config/constant.dart';
import '../core/utils/prarse_color.dart';
import 'custom_widget.dart';

Widget customItemPromoCode(
  PromoCodeModel model,
  VoidCallback onUpdate,
  VoidCallback onDelete, {
  bool isShow = true,
  bool isSelected = false,
  // ProductModel? model,
  // required PromoCodeBloc bloc,
}) {
  final width = AppSize.w(0.9);
  return Padding(
    padding: const EdgeInsets.only(bottom: 5),
    child: SizedBox(
      width: width,
      height: 200,
      child: Card(
        color: Colors.white,
        elevation: 3,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10), // Viền bo tròn
        ),
        child: Container(
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(10),
            border:
                isSelected == true
                    ? Border.all(color: Colors.redAccent, width: 3)
                    : null,
            gradient: LinearGradient(
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
              colors:
                  generatePastelGradientForWhiteText(), // Gọi hàm tạo gradient pastel
            ),
          ),
          child: Column(
            children: [
              Padding(
                padding: const EdgeInsets.all(5.0),
                child: Center(
                  child: AutoSizeText(
                    model.name ?? '',
                    minFontSize: 16,
                    maxFontSize: 24,
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                    ),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(5.0),
                child: Center(
                  child: AutoSizeText(
                    model.description ?? '',
                    minFontSize: 14,
                    maxFontSize: 20,
                    overflow: TextOverflow.ellipsis,
                    softWrap: true,
                    maxLines: 2,

                    style: TextStyle(color: Colors.white),
                  ),
                ),
              ),
              Spacer(),
              buildBottomInfo(model, () {}, () {}, width, isShow),
            ],
          ),
        ),
      ),
    ),
  );
}

Widget buildBottomInfo(
  PromoCodeModel model,
  VoidCallback onDelete,
  VoidCallback onUpdate,
  double width,
  bool isShow,
) {
  return Padding(
    padding: const EdgeInsets.all(10),
    child: Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.only(
          bottomRight: Radius.circular(10),
          bottomLeft: Radius.circular(10),
        ),
        // color: Colors.blueGrey,
      ),
      width: width,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.end,
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          buildInfoContainer(
            width:  width * 0.3,
            color: toHexToColor(primaryColorOrange),
            label: AutoSizeText(
              model.code ?? '',
              maxLines: 1,
              softWrap: true,
              overflow: TextOverflow.ellipsis,
              style: TextStyle(color: Colors.white),
            ),
            isBold: true,
          ),
          SizedBox(width: 5),
          model.startDate != ''
              ? buildInfoContainer(
                width: width * 0.5,
                color: toHexToColor(primaryColorPurple),
                label: AutoSizeText(
                  '${formatDateString(model.startDate ?? '')} - ${formatDateString(model.endDate ?? '')}',
                  maxLines: 1,
                  softWrap: true,
                  overflow: TextOverflow.ellipsis,
                  style: TextStyle(color: Colors.white),
                ),
              )
              : SizedBox.shrink(),
          // if (isShow == false) ...[
          //   Spacer(),
          //   buildIconButton(
          //     onTap: onDelete,
          //     icon: Icons.delete_outline,
          //     color: toHexToColor(primaryErrorColor),
          //   ),
          //   SizedBox(width: 10),
          //   buildIconButton(
          //     onTap: onDelete,
          //     icon: Icons.edit,
          //     color: toHexToColor(primaryColorPurple),
          //   ),
          // ],
        ],
      ),
    ),
  );
}
