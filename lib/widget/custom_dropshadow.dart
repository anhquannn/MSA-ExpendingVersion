import 'package:flutter/material.dart';

/// Tùy chọn các hướng đổ bóng
enum ShadowDirection { all, top, bottom, left, right }

Widget dropShadowContainer({
  required Widget child,
  ShadowDirection direction = ShadowDirection.all,
  double blurRadius = 5,
  double spreadRadius = 5,
  Color shadowColor = const Color.fromRGBO(0, 0, 0, 0.08),
  double? width,
  double? height,
  BoxBorder? boxBorder,
  Color? color,
  BorderRadius? borderRadius,
}) {
  Offset offset;

  switch (direction) {
    case ShadowDirection.top:
      offset = const Offset(0, -2);
      break;
    case ShadowDirection.bottom:
      offset = const Offset(0, 2);
      break;
    case ShadowDirection.left:
      offset = const Offset(-2, 0);
      break;
    case ShadowDirection.right:
      offset = const Offset(2, 0);
      break;
    case ShadowDirection.all:
      offset = const Offset(0, 0);
      break;
  }

  return Container(
    width: width,
    height: height,
    decoration: BoxDecoration(
      border: boxBorder,
      color: color ?? Colors.white,
      borderRadius: borderRadius,
      boxShadow: [
        BoxShadow(
          color: shadowColor,
          blurRadius: blurRadius,
          spreadRadius: spreadRadius,
          offset: offset,
        ),
      ],
    ),
    child: child,
  );
}
