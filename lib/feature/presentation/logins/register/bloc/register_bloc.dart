import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/user_register_request.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/domain/usecase/user_use_case.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import 'package:msa/feature/presentation/logins/register/ui/register_screen.dart';

class RegisterBloc extends BaseBloc<RegisterScreen> {
  TextEditingController nameController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  TextEditingController phoneNumberController = TextEditingController();
  TextEditingController passwordController = TextEditingController();
  TextEditingController validPasswordController = TextEditingController();
  TextEditingController provinceController = TextEditingController(); //tp
  TextEditingController districtController = TextEditingController(); //quan
  TextEditingController wardController = TextEditingController(); //xa
  TextEditingController streetController = TextEditingController(); //duong

  bool? obscurePassword;
  bool? obscureValidPassword;

  bool? errName;
  bool? errEmail;
  bool? errPhoneNumber;
  bool? errPassword;
  bool? errValidPassword;
  bool? errProvince;
  bool? errDistrict;
  bool? errStreet;
  bool? errWard;

  String? errTextName;
  String? errTextEmail;
  String? errTextPhoneNumber;
  String? errTextPassword;
  String? errTextValidPassword;
  String? errTextProvince;
  String? errTextDistrict;
  String? errTextStreet;
  String? errTextWard;

  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();
  String get formattedAddress =>
      '${streetController.text}, ${wardController.text}, ${districtController.text}, ${provinceController.text}';

  @override
  void onDispose() {
    nameController.dispose();
    emailController.dispose();
    phoneNumberController.dispose();
    passwordController.dispose();
    validPasswordController.dispose();
    provinceController.dispose();
    districtController.dispose();
    wardController.dispose();
    streetController.dispose();
  }

  Future<void> register() async {
    validateFields();
  }

  @override
  void onInit() {
    obscurePassword = true;
    obscureValidPassword = true;

    errName = false;
    errEmail = false;
    errPhoneNumber = false;
    errPassword = false;
    errValidPassword = false;
    errProvince = false;
    errDistrict = false;
    errStreet = false;

    errTextName = '';
    errTextEmail = '';
    errTextPhoneNumber = '';
    errTextPassword = '';
    errTextValidPassword = '';
    errTextProvince = '';
    errTextDistrict = '';
    errTextStreet = '';

    streetController.text = '';
    districtController.text = '';
    provinceController.text = '';
    wardController.text = '';
    nameController.text = '';
    emailController.text = '';
    phoneNumberController.text = '';
    passwordController.text = '';
    validPasswordController.text = '';
  }

  @override
  void onReady() {}

  @override
  void onResumed() {}

  void changObscurePassword(bool value) {
    obscurePassword = value;
    setState(() {});
  }

  void changValidObscurePassword(bool value) {
    obscureValidPassword = value;
    setState(() {});
  }

  Future<void> onRegister() async {
    final isValidSuccess = validateFields();
    if (isValidSuccess) {
      UserModel? user = await _userUseCases.register(
        UserRegisterRequest(
          fullName: nameController.text,
          email: emailController.text,
          phoneNumber: phoneNumberController.text,
          password: passwordController.text,
          address: formattedAddress,
          birthday: '',
        ),
      );
      if (user != null) {
        HttpConnection.userModelGlobal = user;
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => LoginScreen()),
        );
      }
    }
  }

  bool validateFields() {
    bool isValid = true;
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

    // Validate Password
    isValid &= validateField(
      controller: passwordController,
      errorText: 'Vui lòng nhập mật khẩu',
      validate: (text) => text.length >= 6,
      onError: (error) {
        errPassword = error.isNotEmpty;
        errTextPassword = error;
      },
    );

    // Validate Valid Password (confirm password)
    isValid &= validateField(
      controller: validPasswordController,
      errorText: 'Vui lòng nhập lại mật khẩu',
      validate: (text) => text == passwordController.text,
      onError: (error) {
        errValidPassword = error.isNotEmpty;
        errTextValidPassword = error;
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
      controller: streetController,
      errorText: 'Vui lòng nhập địa chỉ đường/số nhà',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errStreet = error.isNotEmpty;
        errTextStreet = error;
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
