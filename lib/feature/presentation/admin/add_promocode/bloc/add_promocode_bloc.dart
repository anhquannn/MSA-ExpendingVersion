import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../ui/add_promocode_screen.dart';

class AddPromoCodeBloc extends BaseBloc<AddPromoCodeScreen> {
  final TextEditingController nameController = TextEditingController();
  final TextEditingController codeController = TextEditingController();
  final TextEditingController descController = TextEditingController();
  final TextEditingController startDateController = TextEditingController();
  final TextEditingController endDateController = TextEditingController();

  final FocusNode nameFocusNode = FocusNode();
  final FocusNode codeFocusNode = FocusNode();
  final FocusNode descFocusNode = FocusNode();

  String? nameError;
  String? codeError;
  String? descError;
  String? startDateError;
  String? endDateError;

  // bool isShowMockup = false;
  final ValueNotifier<bool> isShowMockup = ValueNotifier(false);

  @override
  void onInit() {
    nameController.addListener(_onFormChange);
    codeController.addListener(_onFormChange);
    descController.addListener(_onFormChange);
    startDateController.addListener(_onFormChange);
    endDateController.addListener(_onFormChange);
  }

  void _onFormChange() {
    final isValid = _validateRealtime();
    if (isShowMockup.value != isValid) {
      isShowMockup.value = isValid;
    }
  }

  bool _validateRealtime() {
    final valid =
        !_isFieldEmpty(nameController) &&
        !_isFieldEmpty(codeController) &&
        !_isFieldEmpty(descController) &&
        !_isFieldEmpty(startDateController) &&
        !_isFieldEmpty(endDateController) &&
        _isValidDateRange();
    return valid;
  }

  bool _isValidDateRange() {
    try {
      final format = DateFormat('dd/MM/yyyy');
      final start = format.parse(startDateController.text);
      final end = format.parse(endDateController.text);
      return !start.isAfter(end);
    } catch (_) {
      return false;
    }
  }

  bool _isFieldEmpty(TextEditingController controller) {
    return controller.text.trim().isEmpty;
  }

  bool isStartDateAfterEndDate() {
    if (_isFieldEmpty(startDateController)) {
      startDateError = 'Ngày bắt đầu không được để trống';
      return false;
    } else {
      startDateError = null;
    }

    if (_isFieldEmpty(endDateController)) {
      endDateError = 'Ngày kết thúc không được để trống';
      return false;
    } else {
      endDateError = null;
    }

    try {
      final format = DateFormat('dd/MM/yyyy');
      final start = format.parse(startDateController.text);
      final end = format.parse(endDateController.text);

      if (start.isAfter(end)) {
        startDateError = 'Ngày bắt đầu không được sau ngày kết thúc';
        return false;
      }

      return true;
    } catch (e) {
      return false;
    }
  }

  bool validateForm({bool shouldSetState = true}) {
    bool isValid = true;

    if (_isFieldEmpty(nameController)) {
      nameError = 'Vui lòng nhập tên sản phẩm mã giảm giá';
      isValid = false;
    } else {
      nameError = null;
    }

    if (_isFieldEmpty(codeController)) {
      codeError = 'Vui lòng nhập mã giảm giá';
      isValid = false;
    } else {
      codeError = null;
    }

    if (_isFieldEmpty(descController)) {
      descError = 'Vui lòng nhập mô tả';
      isValid = false;
    } else {
      descError = null;
    }

    if (!isStartDateAfterEndDate()) {
      isValid = false;
    }

    if (shouldSetState) {
      isShowMockup.value = isValid;
    }

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
    codeFocusNode.dispose();
    descFocusNode.dispose();

    nameController.removeListener(_onFormChange);
    codeController.removeListener(_onFormChange);
    descController.removeListener(_onFormChange);
    startDateController.removeListener(_onFormChange);
    endDateController.removeListener(_onFormChange);

    nameController.dispose();
    codeController.dispose();
    descController.dispose();
    startDateController.dispose();
    endDateController.dispose();
    isShowMockup.dispose();

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
