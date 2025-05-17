import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../ui/add_manufacturer_screen.dart';

class AddManufacturerBloc extends BaseBloc<AddManufacturerScreen> {
  final TextEditingController nameController = TextEditingController();
  final TextEditingController contactController = TextEditingController();
  final TextEditingController addressController = TextEditingController();

  final FocusNode nameFocusNode = FocusNode();
  final FocusNode contactFocusNode = FocusNode();
  final FocusNode addressFocusNode = FocusNode();

  String? nameError;
  String? contactError;
  String? addressError;

  @override
  void onInit() {}

  bool _isFieldEmpty(TextEditingController controller) {
    return controller.text.trim().isEmpty;
  }

  bool validateForm({bool shouldSetState = true}) {
    bool isValid = true;

    if (_isFieldEmpty(nameController)) {
      nameError = 'Vui lòng nhập tên nhà sản xuất';
      isValid = false;
    } else {
      nameError = null;
    }

    if (_isFieldEmpty(contactController)) {
      contactError = 'Vui lòng nhập liên lạc';
      isValid = false;
    } else {
      contactError = null;
    }
    if (_isFieldEmpty(addressController)) {
      addressError = 'Vui lòng nhập địa chỉ';
      isValid = false;
    } else {
      addressError = null;
    }
    setState(() {});
    return isValid;
  }

  void onFieldSubmitted(
    BuildContext context,
    FocusNode current,
    FocusNode next,
  ) {
    current.unfocus();
    FocusScope.of(context).requestFocus(next);
  }

  @override
  void onDispose() {
    nameFocusNode.dispose();
    contactFocusNode.dispose();
    addressController.dispose();

    nameController.dispose();
    contactController.dispose();
    addressFocusNode.dispose();

    super.dispose();
  }

  @override
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  void onCreatePromoCode() {}
}
