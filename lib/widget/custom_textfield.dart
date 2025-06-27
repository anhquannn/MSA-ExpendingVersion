// TODO: Custom TextFromField
import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';

import '../core/config/constant.dart';
import '../core/utils/prarse_color.dart';

Widget customTextField(
  TextEditingController controller, {
  String? hintText,
  Widget? labelText,
  bool? isBorder = false,
  Color? borderColors,
  Color? textColor,
  int? maxLine = 1,
  double? textSize = 14,
  double? height,
  FocusNode? node,
  String? errorText,
  bool isObscure = false,
  bool isPassword = false,
  Widget? prefixIcon,
  Widget? suffixIcon,
  bool isSuffixIcon=false,
  Color? fillColor,
  double borderRadius = 10,
  Function(String)? onSubmit,
  Function(bool value)? isObscurePassword,
  bool? enable = true,
  TextInputType? typeInput,
}) {
  return SizedBox(
    height:
        errorText == null
            ? height
            : height != null
            ? height += 20
            : 0, // Giữ chiều cao cố định
    child: TextFormField(
      keyboardType: typeInput,
      onFieldSubmitted: onSubmit,
      obscureText: isObscure,
      focusNode: node,
      controller: controller,
      enabled: enable,
      style: TextStyle(fontSize: textSize, color: textColor ?? Colors.white),
      maxLines: maxLine,
      decoration: InputDecoration(
        errorBorder: OutlineInputBorder(
          borderSide: BorderSide(color: Colors.red),
          borderRadius: BorderRadius.circular(10),
        ),
        filled: true,
        fillColor: fillColor ?? Colors.transparent,
        prefixIcon: prefixIcon,
        suffixIcon: isSuffixIcon==true?suffixIcon:
            isPassword
                ? IconButton(
                  onPressed: () {
                    isObscurePassword!(!isObscure);
                  },
                  icon:
                      isObscure
                          ? Icon(
                            Icons.visibility_off,
                            color: toHexToColor(iconColor),
                          )
                          : Icon(
                            Icons.visibility,
                            color: toHexToColor(iconColor),
                          ),
                )
                : null,
        isDense: true,
        labelStyle: TextStyle(
          fontSize: textSize,
          color: toHexToColor(primaryTextColor),
        ),
        hintStyle: TextStyle(
          fontSize: textSize,
          color: toHexToColor(secondaryTextColor),
          overflow: TextOverflow.ellipsis,
        ),
        // labelText: labelText ?? '',
        label: labelText,
        hintText: hintText ?? '',
        errorText: errorText,
        // Hiển thị lỗi nếu có
        focusedErrorBorder: OutlineInputBorder(
          borderSide: BorderSide(
            color: borderColors ?? toHexToColor(borderColor),
            width: 1,
          ),
          borderRadius: BorderRadius.circular(borderRadius),
        ),
        border:
            isBorder == true
                ? OutlineInputBorder(
                  borderRadius: BorderRadius.circular(borderRadius),
                  borderSide: BorderSide(
                    color: borderColors ?? toHexToColor(borderColor),
                    width: 1,
                  ),
                )
                : null,
        enabledBorder:
            isBorder == true
                ? OutlineInputBorder(
                  borderRadius: BorderRadius.circular(borderRadius),
                  borderSide: BorderSide(
                    color: borderColors ?? toHexToColor(borderColor),
                    width: 1,
                  ),
                )
                : OutlineInputBorder(
                  borderSide: BorderSide(
                    color: borderColors ?? toHexToColor(borderColor),
                  ),
                  borderRadius: BorderRadius.circular(borderRadius),
                ),
        focusedBorder: OutlineInputBorder(
          borderSide: BorderSide(
            color: borderColors ?? toHexToColor(borderColor),
            width: 1,
          ),
          borderRadius: BorderRadius.circular(borderRadius),
        ),
        contentPadding: EdgeInsets.symmetric(vertical: 12, horizontal: 12),
      ),
    ),
  );
}

Widget buildTextFieldCard({
  TextEditingController? controller,
  FocusNode? node,
  String? hintText,
  String? label,
  String? errText,
  Function(String)? onSubmitted,
  int maxLines = 1,
  Widget? prefixIcon,
  double? hPadding,
}) {
  return Padding(
    padding: EdgeInsets.symmetric(horizontal: hPadding ?? 20, vertical: 5),
    child: Card(
      color: Colors.white,
      child: customTextField(
        prefixIcon: prefixIcon,
        height: 50,
        errorText: errText,
        controller ?? TextEditingController(),
        node: node,
        textColor: toHexToColor(primaryTextColor),
        hintText: hintText ?? '',
        labelText: AutoSizeText(
          label ?? '',
          minFontSize: 12,
          maxFontSize: 16,
          overflow: TextOverflow.ellipsis,
          softWrap: true,
          maxLines: 1,
        ),
        isBorder: false,
        fillColor: Colors.white,
        borderColors: Colors.white,
        borderRadius: 10,
        onSubmit: onSubmitted,
        // maxLines: maxLines,
      ),
    ),
  );
}
