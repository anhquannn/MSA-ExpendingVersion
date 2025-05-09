import 'package:flutter/material.dart';
import 'package:msa/core/config/constant.dart';
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
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10), // Bo góc cho dialog
        ),
        title: Container(
          width: width,
          height: height * 0.2,
          decoration: BoxDecoration(
            color: toHexToColor(appBarColor),
            borderRadius: BorderRadius.only(
              topRight: Radius.circular(10),
              topLeft: Radius.circular(10),
            ),
          ),
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
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            icon ?? Container(),
            SizedBox(height: 10),
            Center(child: content),
          ],
        ),
        actions: [
          Wrap(
            children: [
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
                      color: toHexToColor(secondaryColorOrange),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: Center(
                      child: Text(
                        'Đóng',
                        style: TextStyle(color: Colors.white),
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
