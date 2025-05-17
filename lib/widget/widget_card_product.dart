import 'dart:math';

import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/catch_image_network.dart';

Widget(String img, String title, {bool isInternet = false, String? content}) {
  final double width = min(AppSize.w(0.5), 400);
  final double height = min(AppSize.h(0.4), 400);
  return SizedBox(
    width: width,
    height: height,
    child: Card(
      elevation: 3,
      child: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(10),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Container(
              decoration: BoxDecoration(color: Colors.white),
              child: Padding(
                padding: EdgeInsets.symmetric(vertical: 1, horizontal: 1),
                child:
                    isInternet
                        ? ClipRect(child: Image.asset(img))
                        : networkImageWidget(
                          imageUrl: img,
                          width: width * 0.9,
                          height: height * 0.4,
                        ),
              ),
            ),
            Padding(
              padding: EdgeInsets.symmetric(horizontal: 2),
              child: AutoSizeText(
                title,
                minFontSize: 16,
                maxFontSize: 20,
                style: TextStyle(
                  color: toHexToColor(primaryTextColor),
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            content != null
                ? Padding(
                  padding: EdgeInsets.symmetric(horizontal: 2),
                  child: AutoSizeText(
                    content,
                    minFontSize: 16,
                    maxFontSize: 20,
                    style: TextStyle(
                      color: toHexToColor(secondaryTextColor),
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                )
                : Container(),
          ],
        ),
      ),
    ),
  );
}
