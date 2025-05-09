import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import 'package:msa/feature/presentation/logins/register/ui/register_screen.dart';

import '../ui/create_branch_screen.dart';

class CreateBranchBloc extends BaseBloc<CreateBranchScreen> {
  TextEditingController nameController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  TextEditingController phoneNumberController = TextEditingController();
  TextEditingController provinceController = TextEditingController(); //tp
  TextEditingController districtController = TextEditingController(); //quan
  TextEditingController wardController = TextEditingController(); //xa
  TextEditingController addressController = TextEditingController(); //duong

  bool errName = false;
  bool errEmail = false;
  bool errPhoneNumber = false;
  bool errProvince = false;
  bool errDistrict = false;
  bool errAddress = false;
  bool errWard = false;

  String errTextName = '';
  String errTextEmail = '';
  String errTextPhoneNumber = '';
  String errTextProvince = '';
  String errTextDistrict = '';
  String errTextAddress = '';
  String errTextWard = '';

  @override
  void onDispose() {
    nameController.dispose();
    emailController.dispose();
    phoneNumberController.dispose();
    provinceController.dispose();
    districtController.dispose();
    wardController.dispose();
    addressController.dispose();
  }

  Future<void> register() async {
    validateFields();
  }

  @override
  void onInit() {}

  @override
  void onReady() {}

  @override
  void onResumed() {}

  bool validateFields() {
    bool isValid = true;

    // Hàm kiểm tra chung cho từng trường
    bool validateField({
      required TextEditingController controller,
      required String errorText,
      required bool Function(String) validate,
      required ValueSetter<String> onError,
    }) {
      final text = controller.text.trim();
      if (text.isEmpty) {
        onError(errorText);
        return false;
      } else if (!validate(text)) {
        onError('Dữ liệu không hợp lệ');
        return false;
      }
      onError('');
      return true;
    }

    // Validate Name
    isValid &= validateField(
      controller: nameController,
      errorText: 'Vui lòng nhập họ tên',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errName = error.isNotEmpty;
        errTextName = error;
      },
    );

    // Validate Email
    isValid &= validateField(
      controller: emailController,
      errorText: 'Vui lòng nhập email',
      validate:
          (text) => RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(text),
      onError: (error) {
        errEmail = error.isNotEmpty;
        errTextEmail = error;
      },
    );

    // Validate Phone Number
    isValid &= validateField(
      controller: phoneNumberController,
      errorText: 'Vui lòng nhập số điện thoại',
      validate: (text) => RegExp(r'^\d{10,11}$').hasMatch(text),
      onError: (error) {
        errPhoneNumber = error.isNotEmpty;
        errTextPhoneNumber = error;
      },
    );

    // Validate Province
    isValid &= validateField(
      controller: provinceController,
      errorText: 'Vui lòng chọn tỉnh/thành phố',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errProvince = error.isNotEmpty;
        errTextProvince = error;
      },
    );

    // Validate District
    isValid &= validateField(
      controller: districtController,
      errorText: 'Vui lòng chọn quận/huyện',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errDistrict = error.isNotEmpty;
        errTextDistrict = error;
      },
    );

    // Validate Street
    isValid &= validateField(
      controller: addressController,
      errorText: 'Vui lòng nhập địa chỉ đường/số nhà',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errAddress = error.isNotEmpty;
        errTextAddress = error;
      },
    );

    // Validate Ward
    isValid &= validateField(
      controller: wardController,
      errorText: 'Vui lòng nhập địa chỉ đường/số nhà',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errWard = error.isNotEmpty;
        errTextWard = error;
      },
    );

    setState(() {}); // Cập nhật lại UI

    return isValid;
  }

  @override
  Widget build(BuildContext context) => widget.build(context);
}
