// TODO: Custom Button
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../core/config/constant.dart';
import '../core/utils/prarse_color.dart';

Widget customButton(
  VoidCallback onTap,
  double? width,
  double? height,
  Widget text, {
  Color? backgroundColorButton,
  bool? isBoxShadow = false,
  int? typeButton = 0,
}) {
  return InkWell(
    onTap: onTap,
    child: Container(
      width: width,
      height: height,
      decoration:
          typeButton == 1
              ? BoxDecoration(
                color: toHexToColor(primaryButtonColor),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(
                  color: toHexToColor(primaryButtonColor),
                  width: 1,
                ),
                boxShadow:
                    isBoxShadow == true
                        ? [
                          BoxShadow(
                            color: Color(
                              0xFF888888,
                            ).withOpacity(0.4), // Màu xám nhẹ với độ mờ 40%
                            spreadRadius: 1,
                            blurRadius: 10,
                            offset: const Offset(0, 1), // Đổ bóng theo trục Y
                          ),
                        ]
                        : null,
              )
              : BoxDecoration(
                color: backgroundColorButton,
                borderRadius: BorderRadius.circular(10),
                border: Border.all(
                  color: toHexToColor(primaryButtonColor),
                  width: 1,
                ),
                boxShadow:
                    isBoxShadow == true
                        ? [
                          BoxShadow(
                            color: Color(
                              0xFF888888,
                            ).withOpacity(0.4), // Màu xám nhẹ với độ mờ 40%
                            spreadRadius: 1,
                            blurRadius: 10,
                            offset: const Offset(0, 1), // Đổ bóng theo trục Y
                          ),
                        ]
                        : null,
              ),
      child: Center(child: text),
    ),
  );
}
