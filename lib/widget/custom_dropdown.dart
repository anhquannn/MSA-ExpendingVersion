import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/prarse_color.dart';

Widget customDropdownButton({
  required List<dynamic> items,
  required String hint,
  required ValueChanged<String?> onChanged,
  String? selectedItem,
  bool isBorder = false,
  Color? color,
}) {
  return Container(
    padding: const EdgeInsets.symmetric(horizontal: 10),
    decoration: BoxDecoration(
      color: color ?? toHexToColor(secondaryColorGreen),
      borderRadius: BorderRadius.circular(8),
      border: Border.all(
        color:
            isBorder == true
                ? toHexToColor(primaryColorGreen)
                : toHexToColor(secondaryColorGreen), // Màu viền
        width: 1,
      ),
    ),
    child: DropdownButton<String>(
      value: selectedItem,
      hint: Text(hint),
      // Văn bản hiển thị khi chưa chọn item
      icon: Icon(Icons.arrow_drop_down, color: Colors.black),
      // Mũi tên
      isExpanded: true,
      underline: SizedBox(),
      // Không có đường dưới
      style: TextStyle(color: Colors.black, fontSize: 16),
      onChanged: onChanged,
      items:
          items.map<DropdownMenuItem<String>>((dynamic value) {
            return DropdownMenuItem<String>(value: value, child: Text(value));
          }).toList(),
    ),
  );
}

Future<void> showCustomDialog(
  BuildContext context,
  double width,
  double height,
  String title,
  Widget? content,
  bool close,
  bool submit,
  Widget? icon, {
  VoidCallback? onSubmit,
  VoidCallback? onClose,
}) async {
  showDialog(
    context: context,
    barrierDismissible: false,
    builder: (BuildContext context) {
      final screenWidth = MediaQuery.of(context).size.width;
      final double dialogWidth = (width == 0) ? screenWidth * 0.9 : width;
      final double dialogHeight = (height == 0) ? dialogWidth : height;

      return AlertDialog(
        backgroundColor: toHexToColor(backgroundColor),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        titlePadding: EdgeInsets.zero,
        contentPadding: const EdgeInsets.fromLTRB(12, 12, 12, 12),
        title: Container(
          width: dialogWidth,
          decoration: BoxDecoration(
            color: toHexToColor(appBarColor),
            borderRadius: const BorderRadius.only(
              topRight: Radius.circular(20),
              topLeft: Radius.circular(20),
            ),
          ),
          padding: EdgeInsets.symmetric(vertical: 12),
          child: Center(
            child: Text(
              title,
              style: TextStyle(
                color: Colors.white,
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ),
        content: Container(
          width: dialogWidth,
          decoration: BoxDecoration(
            color: toHexToColor(backgroundColor),
            borderRadius: const BorderRadius.only(
              bottomRight: Radius.circular(10),
              bottomLeft: Radius.circular(10),
            ),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (icon != null) icon,
              const SizedBox(height: 10),
              if (content != null) Center(child: content),
            ],
          ),
        ),
        actionsPadding: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
        actions: [
          Wrap(
            children: [
              if (submit)
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 8),
                  child: InkWell(
                    onTap: () {
                      onSubmit != null
                          ? onSubmit()
                          : Navigator.of(context).pop(false);
                    },
                    child: Container(
                      width: dialogWidth * 0.3,
                      height: 40,
                      decoration: BoxDecoration(
                        color: toHexToColor(secondaryErrorColor),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: const Center(
                        child: Text(
                          'Đồng ý',
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                    ),
                  ),
                ),
              if (close)
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 8),
                  child: InkWell(
                    onTap: () {
                      onClose != null
                          ? onClose()
                          : Navigator.of(context).pop(false);
                    },
                    child: Container(
                      width: dialogWidth * 0.3,
                      height: 40,
                      decoration: BoxDecoration(
                        color: toHexToColor(borderColor),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Center(
                        child: Text(
                          'Đóng',
                          style: TextStyle(
                            color: toHexToColor(primaryTextColor),
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
            ],
          ),
        ],
      );
    },
  );
}

showCustomMessageError(BuildContext context) async {
  return await showCustomDialog(
    context,
    AppSize.w(0.8),
    150,
    'Thông báo',
    Text(
      messageError == '' ? 'Lỗi dữ liệu' : messageError,
      style: TextStyle(color: toHexToColor(primaryTextColor), fontSize: 16),
    ),
    true,
    false,
    null,
  );
}

showCustomSuccessError(
  BuildContext context,
  String text,
  VoidCallback onClose,
) async {
  return await showCustomDialog(
    context,
    AppSize.w(0.8),
    150,
    'Thông báo',
    Text(
      text,
      style: TextStyle(color: toHexToColor(primaryTextColor), fontSize: 16),
    ),
    true,
    false,
    onClose: () {
      onClose();
    },
    Icon(Icons.check_circle, color: Colors.green),
  );
}
