import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';

import '../core/config/constant.dart';
import '../core/utils/prarse_color.dart';
import 'custom_widget.dart';

Widget customItemPromoCode(
  VoidCallback onUpdate,
  VoidCallback onDelete, {
  bool isShow = true,
  // ProductModel? model,
  // required PromoCodeBloc bloc,
}) {
  final width = AppSize.w(0.95);
  return Padding(
    padding: const EdgeInsets.only(bottom: 5),
    child: SizedBox(
      width: width,
      // height: height,
      child: Card(
        color: Colors.white,
        elevation: 3,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10), // Viền bo tròn
        ),
        child: Container(
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(
              10,
            ), // Viền bo tròn cho Container
            gradient: LinearGradient(
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
              colors: [
                toHexToColor(primaryButtonColor),
                Colors.blueGrey,
              ], // Các màu của gradient
            ),
          ),
          child: Column(
            children: [
              Padding(
                padding: const EdgeInsets.all(5.0),
                child: Center(
                  child: AutoSizeText(
                    'Mã giảm giá 01',
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
                    'Giảm 40% cho sản phẩm đầu tiên bạn mua.Giảm 40% cho sản phẩm đầu tiên bạn mua.Giảm 40% cho sản phẩm đầu tiên bạn mua.',
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
              buildBottomInfo(() {}, () {}, width, isShow),
            ],
          ),
        ),
      ),
    ),
  );
}

Widget buildBottomInfo(
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
            width: isShow ? width * 0.3 : width * 0.2,
            color: toHexToColor(primaryColorOrange),
            label: AutoSizeText(
              'MAGIAM01',
              maxLines: 1,
              softWrap: true,
              overflow: TextOverflow.ellipsis,
              style: TextStyle(color: Colors.white),
            ),
            isBold: true,
          ),
          SizedBox(width: 10),
          buildInfoContainer(
            width: width * 0.4,
            color: toHexToColor(primaryColorPurple),
            label: AutoSizeText(
              '10/10/2020 - 10/10/2021',
              maxLines: 1,
              softWrap: true,
              overflow: TextOverflow.ellipsis,
              style: TextStyle(color: Colors.white),
            ),
          ),
          if (isShow == false) ...[
            Spacer(),
            buildIconButton(
              onTap: onDelete,
              icon: Icons.delete_outline,
              color: toHexToColor(primaryErrorColor),
            ),
            SizedBox(width: 10),
            buildIconButton(
              onTap: onDelete,
              icon: Icons.edit,
              color: toHexToColor(primaryColorPurple),
            ),
          ],
        ],
      ),
    ),
  );
}
