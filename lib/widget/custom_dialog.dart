import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';

import '../core/config/config.dart';

//
// Widget datePickerField({
//   required BuildContext context,
//   String? initialDate,
//   required String label,
//   required TextEditingController controller,
//   bool isBorder = false,
//   String? errorText,
//   double? height = 45,
// }) {
//   final DateFormat _dateFormat = DateFormat('dd/MM/yyyy');
//
//   Future<void> _pickDate() async {
//     final DateTime now = DateTime.now();
//     final DateTime? picked = await showDatePicker(
//       context: context,
//       initialDate: now,
//       firstDate: DateTime(2000),
//       lastDate: DateTime(2100),
//       builder: (context, child) {
//         return Theme(
//           data: ThemeData(
//             primaryColor: Colors.blue, // Màu của các nút (OK, Cancel)
//             buttonTheme: ButtonThemeData(
//               textTheme: ButtonTextTheme.accent, // Màu chữ nút
//             ), // Nền của bảng
//             textTheme: TextTheme(),
//             dialogTheme: DialogThemeData(backgroundColor: Colors.white),
//           ),
//           child: child!,
//         );
//       },
//     );
//
//     if (picked != null) {
//       final String formatted = _dateFormat.format(picked);
//       controller.text = formatted;
//     }
//   }
//
//   return GestureDetector(
//     onTap: _pickDate,
//     child: SizedBox(
//       height:
//           errorText == null
//               ? height
//               : height != null
//               ? height += 20
//               : 0,
//       child: AbsorbPointer(
//         child: TextFormField(
//           controller: controller,
//           decoration: InputDecoration(
//             filled: true,
//             suffixIcon: Icon(
//               Icons.calendar_month_outlined,
//               color: Colors.black,
//             ),
//             fillColor: Colors.white,
//             isDense: true,
//             errorText: errorText,
//             label: Text(label),
//             errorBorder: OutlineInputBorder(
//               borderSide: BorderSide(color: Colors.red),
//               borderRadius: BorderRadius.circular(10),
//             ),
//             border:
//                 isBorder == true
//                     ? OutlineInputBorder(
//                       borderRadius: BorderRadius.circular(10),
//                       borderSide: BorderSide(
//                         color:
//                             isBorder ? toHexToColor(borderColor) : Colors.white,
//                         width: 1,
//                       ),
//                     )
//                     : null,
//             enabledBorder:
//                 isBorder == true
//                     ? OutlineInputBorder(
//                       borderRadius: BorderRadius.circular(10),
//                       borderSide: BorderSide(
//                         color:
//                             isBorder ? toHexToColor(borderColor) : Colors.white,
//                         width: 1,
//                       ),
//                     )
//                     : OutlineInputBorder(
//                       borderSide: BorderSide(
//                         color:
//                             isBorder ? toHexToColor(borderColor) : Colors.white,
//                       ),
//                       borderRadius: BorderRadius.circular(10),
//                     ),
//             focusedBorder: OutlineInputBorder(
//               borderSide: BorderSide(
//                 color: toHexToColor(borderColor),
//                 width: 1,
//               ),
//               borderRadius: BorderRadius.circular(10),
//             ),
//             contentPadding: EdgeInsets.symmetric(vertical: 12, horizontal: 12),
//           ),
//         ),
//       ),
//     ),
//   );
// }
Widget datePickerField({
  required BuildContext context,
  String? initialDate,
  required Widget label,
  required TextEditingController controller,
  bool isBorder = false,
  String? errorText,
  double? height = 45,
  Color borderColor = Colors.blue, // Màu mặc định của border
  bool? isPrefix = false,
}) {
  final DateFormat _dateFormat = DateFormat('dd/MM/yyyy');

  // Hàm chọn ngày
  Future<void> _pickDate() async {
    final DateTime now = DateTime.now();
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate:
          initialDate != null
              ? DateFormat('dd/MM/yyyy').parse(initialDate)
              : now,
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
      builder: (context, child) {
        return Theme(
          data: ThemeData(
            primaryColor: Colors.blue, // Màu của các nút (OK, Cancel)
            buttonTheme: ButtonThemeData(
              textTheme: ButtonTextTheme.accent, // Màu chữ nút
            ), // Nền của bảng
            textTheme: TextTheme(),
            dialogTheme: DialogThemeData(backgroundColor: Colors.white),
          ),
          child: child!,
        );
      },
    );

    if (picked != null) {
      final String formatted = _dateFormat.format(picked);
      controller.text =
          formatted; // Cập nhật giá trị ngày vào TextEditingController
    }
  }

  return GestureDetector(
    onTap: _pickDate,
    child: SizedBox(
      height:
          errorText == null
              ? height
              : height! + 20, // Tăng chiều cao khi có lỗi
      child: AbsorbPointer(
        child: TextFormField(
          controller: controller,
          decoration: InputDecoration(
            filled: true,
            suffixIcon:
                isPrefix == true
                    ? null
                    : Icon(Icons.calendar_today, color: Colors.black),
            prefixIcon:
                isPrefix == false
                    ? null
                    : Icon(Icons.calendar_today, color: Colors.black),
            fillColor: Colors.white,
            isDense: true,
            errorText: errorText,
            label: label,
            errorBorder: OutlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
              borderRadius: BorderRadius.circular(10),
            ),
            border:
                isBorder
                    ? OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: BorderSide(color: borderColor, width: 1),
                    )
                    : null,
            enabledBorder:
                isBorder
                    ? OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: BorderSide(color: borderColor, width: 1),
                    )
                    : OutlineInputBorder(
                      borderSide: BorderSide(color: Colors.transparent),
                      borderRadius: BorderRadius.circular(10),
                    ),
            focusedBorder: OutlineInputBorder(
              borderSide: BorderSide(color: borderColor, width: 1),
              borderRadius: BorderRadius.circular(10),
            ),
            contentPadding: EdgeInsets.symmetric(vertical: 12, horizontal: 12),
          ),
        ),
      ),
    ),
  );
}

Future<String?> selectColorDialog(BuildContext context) async {
  // Danh sách các màu mở rộng

  // Hiển thị dialog chọn màu
  return showDialog<String>(
    context: context,
    builder: (context) {
      return AlertDialog(
        elevation: 3,
        backgroundColor: Colors.white,
        title: Center(child: Text('Chọn Màu')),
        content: GridView.builder(
          gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 4, // Số cột là 4 để hiển thị nhiều màu hơn
            crossAxisSpacing: 10,
            mainAxisSpacing: 10,
          ),
          itemCount: listColor.length,
          itemBuilder: (context, index) {
            return GestureDetector(
              onTap: () {
                // Trả về mã hex của màu đã chọn
                String hexColor =
                    '#${listColor[index].value.toRadixString(16).substring(2)}';
                Navigator.of(context).pop(hexColor);
              },
              child: Container(
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(100),
                  color: listColor[index],
                ),
                // margin: EdgeInsets.all(5),
                width: 50,
                height: 50,
              ),
            );
          },
        ),
      );
    },
  );
}
