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

// TODO: Custom Dialog
Future<void> showCustomDialog(
  BuildContext context,
  double width,
  double height,
  String title,
  Widget? content,
  bool close,
  bool submit,
  Widget? icon,
) async {
  showDialog(
    context: context,
    barrierDismissible: true,
    builder: (BuildContext context) {
      return AlertDialog(
        backgroundColor: toHexToColor(backgroundColor),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        titlePadding:
            EdgeInsets.zero, // bỏ padding mặc định của title để full width
        contentPadding: EdgeInsets.fromLTRB(
          24,
          20,
          24,
          24,
        ), // hoặc tuỳ chỉnh padding content
        title: Container(
          // Bỏ width, height cố định
          decoration: BoxDecoration(
            color: toHexToColor(appBarColor),
            borderRadius: BorderRadius.only(
              topRight: Radius.circular(10),
              topLeft: Radius.circular(10),
            ),
          ),
          padding: EdgeInsets.symmetric(
            vertical: height * 0.05,
          ), // hoặc padding theo ý bạn
          child: Center(
            child: Text(
              title,
              style: TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
        content: Container(
          decoration: BoxDecoration(
            color: toHexToColor(backgroundColor),
            borderRadius: BorderRadius.only(
              bottomRight: Radius.circular(10),
              bottomLeft: Radius.circular(10),
            ),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (icon != null) icon!,
              SizedBox(height: 10),
              Center(child: content),
            ],
          ),
        ),
        actionsPadding: EdgeInsets.symmetric(horizontal: 8, vertical: 10),
        actions: [
          Wrap(
            children: [
              if (submit == true)
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 8),
                  child: InkWell(
                    onTap: () {
                      Navigator.of(context).pop(false);
                    },
                    child: Container(
                      width: width * 0.3,
                      height: 40,
                      decoration: BoxDecoration(
                        color: toHexToColor(secondaryErrorColor),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Center(
                        child: Text(
                          'Đồng ý',
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                    ),
                  ),
                ),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8),
                child: InkWell(
                  onTap: () {
                    Navigator.of(context).pop(false);
                  },
                  child: Container(
                    width: width * 0.3,
                    height: 40,
                    decoration: BoxDecoration(
                      color: toHexToColor(borderColor),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: Center(
                      child: Text(
                        'Đóng',
                        style: TextStyle(color: toHexToColor(primaryTextColor)),
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

Future<void> showCustomMessageError(BuildContext context) async {
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
