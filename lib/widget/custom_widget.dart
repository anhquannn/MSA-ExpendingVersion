import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import '../core/config/config.dart' as Config;
import '../core/config/constant.dart';
import '../core/utils/prarse_color.dart';

// TODO: Custom Icon quay lại
Widget iconBack({double? size}) {
  return InkWell(
    onTap: () {
      Navigator.of(Config.context, rootNavigator: true).pop();
    },
    child: Icon(Icons.arrow_back_ios, color: Colors.white, size: size),
  );
}

Widget buildIconButton({
  required VoidCallback onTap,
  required IconData icon,
  required Color color,
  double radius = 40,
  double padding = 7,
  Color iconColor = Colors.white,
}) {
  return InkWell(
    onTap: onTap,
    borderRadius: BorderRadius.circular(radius),
    child: Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(radius),
        color: color,
      ),
      padding: EdgeInsets.all(padding),
      child: Icon(icon, color: iconColor),
    ),
  );
}

Widget buildInfoContainer({
  required Widget label,
  required Color color,
  double? width,
  bool? isBold = false,
}) {
  return Padding(
    padding: const EdgeInsets.only(bottom: 5),
    child: Container(
      width: width,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10),
        color: color,
      ),
      child: Padding(
        padding: const EdgeInsets.all(7.0),
        child: Center(child: label),
      ),
    ),
  );
}

Widget customDivider({required Widget text, required Color color}) {
  return Row(
    children: [
      Expanded(child: Divider(color: color, height: 1)),
      Padding(padding: const EdgeInsets.all(8.0), child: text),
      Expanded(child: Divider(color: color, height: 1)),
    ],
  );
}

Widget customAutoSizeText(
  double min,
  double max,
  String text, {
  bool? isBold = false,
  bool? isLine = false,
  Color? textColor,
  int? maxLine = 2,
}) {
  return AutoSizeText(
    minFontSize: min,
    maxFontSize: max,
    text,
    maxLines: maxLine,
    overflow: TextOverflow.ellipsis,
    softWrap: true,
    style: TextStyle(
      color: textColor ?? toHexToColor(primaryTextColor),
      fontWeight: isBold == true ? FontWeight.bold : null,
      decoration: isLine == true ? TextDecoration.lineThrough : null,
    ),
  );
}

Widget customItemProductCustomer(double width, {bool? isDiscount = false}) {
  return Card(
    color: Colors.white,
    child: Container(
      decoration: BoxDecoration(
        color: Color(0xFFE6F4EA), // Nền dịu nhẹ
        borderRadius: BorderRadius.circular(10),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.only(
                topLeft: Radius.circular(10),
                topRight: Radius.circular(10),
              ),
            ),

            child: Stack(
              children: [
                // Hình ảnh nền chiếm toàn bộ vùng stack
                Center(
                  child: Container(
                    width: width,
                    height: 150,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.only(
                        topLeft: Radius.circular(10),
                        topRight: Radius.circular(10),
                      ),
                      image: DecorationImage(
                        image: AssetImage(imgCategoryBotGiat),
                        fit: BoxFit.cover, // Quan trọng để ảnh chiếm toàn bộ
                      ),
                    ),
                  ),
                ),
                // Thẻ giảm giá
                isDiscount == true
                    ? Positioned(
                      top: 3,
                      right: 3,
                      child: Card(
                        child: Container(
                          padding: EdgeInsets.symmetric(
                            horizontal: 6,
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            color: toHexToColor(primaryErrorColor),
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            '-10%',
                            style: TextStyle(
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                              fontSize: 12,
                            ),
                          ),
                        ),
                      ),
                    )
                    : Container(),
              ],
            ),
          ),

          Container(
            height: 80,
            decoration: BoxDecoration(
              color: Color(0xFFE6F4EA), // Xanh lá nhạt tươi sáng
            ),
            padding: EdgeInsets.all(8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                AutoSizeText(
                  'Dưa lưới',
                  minFontSize: 14,
                  maxFontSize: 18,
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                AutoSizeText(
                  '100.000đ',
                  minFontSize: 10,
                  maxFontSize: 14,
                  style: TextStyle(
                    color: Color(0xFF888888),
                    decoration: TextDecoration.lineThrough,
                  ),
                ),
                AutoSizeText(
                  '90.000đ',
                  minFontSize: 12,
                  maxFontSize: 16,
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF28A745), // Giá nổi bật
                  ),
                ),
              ],
            ),
          ),
          Spacer(),
          Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.only(
                bottomRight: Radius.circular(10),
                bottomLeft: Radius.circular(10),
              ),
              gradient: LinearGradient(
                colors: [Color(0xFF28A745), Color(0xFF218838)],
              ),
            ),
            height: 40,
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  flex: 7,
                  child: Center(
                    child: AutoSizeText(
                      'Mua ngay',
                      minFontSize: 14,
                      maxFontSize: 18,
                      style: TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
                Expanded(
                  flex: 3,
                  child: InkWell(
                    child: Icon(
                      Icons.add_shopping_cart_outlined,
                      color: Colors.white,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    ),
  );
}
