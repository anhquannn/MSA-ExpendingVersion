import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../../welcom/welcom1.dart';
import '../ui/login_screen.dart';

class LoginBloc extends BaseBloc<LoginScreen> {
  final FocusNode emailNode = FocusNode();
  final FocusNode passwordNode = FocusNode();

  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  bool isValidPassword = false;
  bool isValidEmail = false;
  bool isShowPass = false;
  String validPassword = '';
  String validEmail = '';

  @override
  void onInit() {}

  @override
  void onDispose() {
    emailController.dispose();
    passwordController.dispose();
    emailNode.dispose();
    passwordNode.dispose();
  }

  void login() {
    final email = validateEmail(emailController.text);
    final password = validatePassword(passwordController.text);
    setState(() {});
    if (email && password) {
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => OnboardingScreen()),
      );
    }
  }

  void forgotPassword() {
    print("Quên mật khẩu");
    // TODO: Viết logic forgot password
  }

  void createUser() {
    print("Tạo tài khoản mới");
    // TODO: Viết logic tạo tài khoản
  }

  void loginWithGoogle() {
    print("Login bằng Google");
    // TODO: Viết logic login Google
  }

  @override
  void onReady() {
    // Gọi khi màn hình đã sẵn sàng
  }

  @override
  void onResumed() {
    // Gọi khi ứng dụng quay lại từ nền
  }

  bool validateEmail(String? value) {
    if (value == null || value.isEmpty) {
      isValidEmail = true;
      validEmail = 'Email không được để trống';
      return false;
    }
    const pattern = r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$';
    final regex = RegExp(pattern);
    if (!regex.hasMatch(value)) {
      isValidEmail = true;
      validEmail = 'Email không hợp lệ';
      return false;
    }
    isValidEmail = false;
    return true;
  }

  bool validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      isValidPassword = true;
      validPassword = 'Mật khẩu không được để trống';
      return false;
    }
    if (value.length < 6) {
      isValidPassword = true;
      validPassword = 'Mật khẩu phải lớn hơn 6 ký tự';
      return false;
    }
    isValidPassword = false;
    return true;
  }

  void obscurePassword(bool value) {
    isShowPass = value;
    setState(() {});
  }

  @override
  Widget build(BuildContext context) => widget.build(context);
}
