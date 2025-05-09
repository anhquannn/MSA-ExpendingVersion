import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/presentation/admin/product/product_detail/ui/product_detail_screen.dart';

import '../../../../../../widget/custom_dialog.dart';

class ProductDetailBloc extends BaseBloc<ProductDetailScreen> {
  // Controllers
  final TextEditingController nameController = TextEditingController();
  final TextEditingController priceController = TextEditingController();
  final TextEditingController currentPriceController = TextEditingController();
  final TextEditingController unitController = TextEditingController();
  final TextEditingController specificationController = TextEditingController();
  final TextEditingController descController = TextEditingController();
  final TextEditingController expiryController = TextEditingController();

  // FocusNodes
  final FocusNode nameFocus = FocusNode();
  final FocusNode priceFocus = FocusNode();
  final FocusNode currentPriceFocus = FocusNode();
  final FocusNode unitFocus = FocusNode();
  final FocusNode specificationFocus = FocusNode();
  final FocusNode descFocus = FocusNode();
  final FocusNode expiryFocus = FocusNode();

  // Error texts for validation
  String? nameError;
  String? priceError;
  String? currentPriceError;
  String? unitError;
  String? specificationError;
  String? descError;
  String? expiryError;

  String color = 'FFFFFF';

  @override
  void onInit() {}

  @override
  void onDispose() {
    // Dispose controllers
    nameController.dispose();
    priceController.dispose();
    currentPriceController.dispose();
    unitController.dispose();
    specificationController.dispose();
    descController.dispose();
    expiryController.dispose();

    // Dispose focus nodes
    nameFocus.dispose();
    priceFocus.dispose();
    currentPriceFocus.dispose();
    unitFocus.dispose();
    specificationFocus.dispose();
    descFocus.dispose();
    expiryFocus.dispose();
  }

  @override
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  // Dummy methods
  void onSetting() {}

  void onLogout() {}

  void onTapHone() {}

  void onUpdateProduct() {}

  void onDeleteProduct() {}

  // Validation function
  bool validateFields() {
    bool isValid = true;

    nameError = _validateNotEmpty(nameController.text);
    priceError = _validateNotEmpty(priceController.text);
    currentPriceError = _validateNotEmpty(currentPriceController.text);
    unitError = _validateNotEmpty(unitController.text);
    specificationError = _validateNotEmpty(specificationController.text);
    descError = _validateNotEmpty(descController.text);
    expiryError = _validateNotEmpty(expiryController.text);
    if (expiryError == null) {
      try {
        final inputDate = DateFormat(
          'dd/MM/yyyy',
        ).parse(expiryController.text.trim());
        final today = DateTime.now();
        final now = DateTime(today.year, today.month, today.day);

        if (!inputDate.isAfter(now)) {
          expiryError = 'Ngày hết hạn phải lớn hơn hôm nay';
          isValid = false;
        }
      } catch (e) {
        expiryError = 'Định dạng ngày không hợp lệ';
        isValid = false;
      }
    }

    if (nameError != null ||
        priceError != null ||
        currentPriceError != null ||
        unitError != null ||
        specificationError != null ||
        descError != null ||
        expiryError != null) {
      isValid = false;
    }

    setState(() {});
    return isValid;
  }

  String? _validateNotEmpty(String value) {
    return value.trim().isEmpty ? 'Không được để trống' : null;
  }

  Future<void> selectColor() async {
    String? selectedColor = await selectColorDialog(context);

    if (selectedColor != null) {
      setState(() {
        color = selectedColor;
      });
    }
  }

  Color getOppositeColor(String hexColor) {
    // Nếu màu hex không hợp lệ, trả về màu mặc định
    if (hexColor.length != 7 || !hexColor.startsWith('#')) {
      return Colors.black; // Default fallback color
    }

    // Chuyển hexColor thành giá trị RGB
    int r = int.parse(hexColor.substring(1, 3), radix: 16);
    int g = int.parse(hexColor.substring(3, 5), radix: 16);
    int b = int.parse(hexColor.substring(5, 7), radix: 16);

    // Tính toán màu đối lập
    int oppositeR = 255 - r;
    int oppositeG = 255 - g;
    int oppositeB = 255 - b;

    // Trả về màu đối lập
    return Color.fromRGBO(oppositeR, oppositeG, oppositeB, 1);
  }
}
