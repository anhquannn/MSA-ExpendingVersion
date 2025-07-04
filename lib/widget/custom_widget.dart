import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import '../core/config/config.dart' as Config;
import '../core/config/constant.dart';
import '../core/utils/prarse_color.dart';

Widget customTextSpan(
  String title,
  String body,
  TextStyle primaryStyle,
  TextStyle? secondaryStyle,
) {
  return RichText(
    text: TextSpan(
      children: [
        TextSpan(text: title, style: primaryStyle),
        TextSpan(text: body, style: secondaryStyle),
      ],
    ),
  );
}

Widget iconBack(BuildContext context, {double? size, Color? color}) {
  return InkWell(
    onTap: () {
      Navigator.of(context, rootNavigator: true).pop();
    },
    child: Icon(Icons.arrow_back_ios, color: color ?? Colors.white, size: size),
  );
}

Widget buildStarRating({
  required double rating, // ví dụ: 4.8
  Color color = Colors.amber, // màu sao mặc định
  double size = 20.0, // kích thước sao mặc định
  Color backgroundColor = Colors.grey, // màu sao rỗng
}) {
  List<Widget> stars = [];

  for (int i = 0; i < 5; i++) {
    if (i < rating.floor()) {
      // Sao đầy
      stars.add(Icon(Icons.star, color: color, size: size));
    } else if (i < rating && rating - i < 1) {
      // Sao nửa
      stars.add(Icon(Icons.star_half, color: color, size: size));
    } else {
      // Sao rỗng
      stars.add(Icon(Icons.star_border, color: backgroundColor, size: size));
    }
  }

  return Row(mainAxisSize: MainAxisSize.min, children: stars);
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

Widget customItemProductCustomer(
  ProductModel model,
  width, {
  bool? isDiscount = false,
  VoidCallback? onBuy,
  VoidCallback? onAddToCart,
}) {
  if (model.price != null &&
      model.price! > 0 &&
      model.currentPrice != null &&
      model.currentPrice! > 0) {}
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
                      borderRadius: const BorderRadius.only(
                        topLeft: Radius.circular(10),
                        topRight: Radius.circular(10),
                      ),
                    ),
                    clipBehavior: Clip.hardEdge, // Đảm bảo ảnh bo góc
                    child: Image.network(
                      model.image ?? imgProductDefault,
                      fit: BoxFit.contain,
                      errorBuilder: (context, error, stackTrace) {
                        return Image.asset(imgBranch, fit: BoxFit.contain);
                      },
                    ),
                  ),
                ),

                // Thẻ giảm giá
                model.discountPercentage != 0
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
                          child:
                              model.discountPercentage != 0
                                  ? Text(
                                    '${model.discountPercentage}%',

                                    style: TextStyle(
                                      color: Colors.white,
                                      fontWeight: FontWeight.bold,
                                      fontSize: 12,
                                    ),
                                  )
                                  : SizedBox(),
                        ),
                      ),
                    )
                    : Container(),
              ],
            ),
          ),
          Container(
            height: 100,
            decoration: BoxDecoration(
              color: Color(0xFFE6F4EA), // Xanh lá nhạt tươi sáng
            ),
            padding: EdgeInsets.all(8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                AutoSizeText(
                  model.name ?? '',
                  minFontSize: 14,
                  maxFontSize: 18,
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                (model.branchCurrentPrice != 0&& model.branchCurrentPrice !=null)
                    ? AutoSizeText(
                      formatCurrencyVN((model.branchCurrentPrice ?? 0)),
                      minFontSize: 10,
                      maxFontSize: 14,
                      style: TextStyle(
                        color: Color(0xFF888888),
                        decoration: TextDecoration.lineThrough,
                      ),
                    )
                    // : SizedBox()
                    : Container(),
                AutoSizeText(
                  formatCurrencyVN(model.price ?? 0),
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
                  child: InkWell(
                    onTap: () {
                      if (onBuy != null) {
                        onBuy();
                      }
                    },
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
                ),
                Expanded(
                  flex: 3,
                  child: InkWell(
                    onTap: () {
                      if (onAddToCart != null) {
                        onAddToCart();
                      }
                    },
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
