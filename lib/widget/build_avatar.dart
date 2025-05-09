import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../core/config/constant.dart';
import '../core/utils/prarse_color.dart';

Widget buildAvatar({
  required String imagePath,
  required double size,
  String? borderColorHex,
}) {
  return Padding(
    padding: const EdgeInsets.all(8.0),
    child: Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border.all(
          width: 7,
          color: toHexToColor(borderColorHex ?? borderColor),
        ),
        shape: BoxShape.circle,
      ),
      child: ClipOval(
        child: Image.asset(
          imagePath,
          fit: BoxFit.cover,
          width: size,
          height: size,
        ),
      ),
    ),
  );
}

Widget buildAvatarNetwork({
  required String imagePath,
  required double size,
  String? borderColorHex,
}) {
  return Padding(
    padding: const EdgeInsets.all(8.0),
    child: Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border.all(
          width: 7,
          color: toHexToColor(borderColorHex ?? borderColor),
        ),
        shape: BoxShape.circle,
      ),
      child: ClipOval(
        child: Image.network(
          imagePath,
          fit: BoxFit.cover,
          width: size,
          height: size,
        ),
      ),
    ),
  );
}
