import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../ui/add_category_screen.dart';

class AddCategoryBloc extends BaseBloc<AddCategoryScreen> {
  final TextEditingController nameController = TextEditingController();
  final TextEditingController descController = TextEditingController();

  final FocusNode nameFocusNode = FocusNode();
  final FocusNode descFocusNode = FocusNode();

  String? nameError;
  String? descError;

  @override
  void onInit() {}

  bool _isFieldEmpty(TextEditingController controller) {
    return controller.text.trim().isEmpty;
  }

  bool validateForm({bool shouldSetState = true}) {
    bool isValid = true;

    if (_isFieldEmpty(nameController)) {
      nameError = 'Vui lòng nhập tên loại sản phẩm';
      isValid = false;
    } else {
      nameError = null;
    }

    if (_isFieldEmpty(descController)) {
      descError = 'Vui lòng nhập mô tả';
      isValid = false;
    } else {
      descError = null;
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
    descFocusNode.dispose();

    nameController.dispose();
    descController.dispose();

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
