import 'dart:math';
import 'dart:ui';

import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';

import '../core/config/config.dart';
import '../core/utils/prarse_color.dart';

// Widget buildNotificationItems(
//   String title,
//   String content, {
//   bool? isRead = false,
// }) {
//   return SizedBox(
//     width: AppSize.w(0.95),
//     child: SingleChildScrollView(
//       child: ListView.builder(
//         shrinkWrap: true, // Giúp ListView không chiếm quá nhiều không gian
//         physics:
//             NeverScrollableScrollPhysics(), // Tắt scroll của ListView vì đã có SingleChildScrollView
//         itemCount: 10, // Chỉ có một item
//         itemBuilder: (BuildContext context, int index) {
//           return _buildItemNotification(
//             title: title,
//             content: content,
//             isRead: isRead ?? false,
//           );
//         },
//       ),
//     ),
//   );
// }

Widget buildItemNotification(
  String title,
  String content, {
  bool isRead = false,
}) {
  double width = min(500, AppSize.w(0.95));
  return Card(
    color: Colors.white,
    child: Container(
      padding: EdgeInsets.symmetric(horizontal: 8, vertical: 8),
      width: width,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10),
        gradient: LinearGradient(
          // colors: [toHexToColor(secondaryButtonColor), Colors.teal],
          colors: [
            // toHexToColor(secondaryButtonColor),
            // toHexToColor('008080'),
            toHexToColor('FFFDF4'),
            toHexToColor('F4FFF8'),
            // toHexToColor('156A54'),
            // toHexToColor('0F3D33'),
            // toHexToColor('3DD1AC'),
          ],
        ),
      ),
      child: Stack(
        children: [
          Positioned(
            right: 2,
            child: ImageFiltered(
              imageFilter: ImageFilter.blur(sigmaX: 3.0, sigmaY: 3.0),
              child: Icon(
                Icons.circle,
                size: 17,
                color: isRead == true ? Colors.transparent : Colors.red,
              ),
            ),
          ),
          Column(
            children: [
              AutoSizeText(
                title,
                style: TextStyle(
                  color: Colors.black,
                  fontWeight: FontWeight.bold,
                ),
                minFontSize: 16,
                maxFontSize: 20,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 5),
                child: SizedBox(
                  width: width,
                  // child: Divider(color: Colors.grey, height: 1),
                  child: Divider(color: toHexToColor('CDE7D8'), height: 1),
                ),
              ),
              AutoSizeText(
                content,
                // style: TextStyle(color: toHexToColor(primaryTextColor)),
                style: TextStyle(color: Colors.black),
                minFontSize: 14,
                maxFontSize: 18,
              ),
            ],
          ),
        ],
      ),
    ),
  );
}
