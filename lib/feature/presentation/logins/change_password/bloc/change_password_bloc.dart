import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import '../ui/change_password_screen.dart';

class ChangePasswordBloc extends BaseBloc<ChangePasswordScreen> {
  final TextEditingController passwordController = TextEditingController();
  final TextEditingController validPasswordController = TextEditingController();

  bool isObscurePassword = false;
  bool isObscureValidPassword = false;

  bool errPassword = false;
  bool errValidPassword = false;

  String errTextPassword = '';
  String errTextValidPassword = '';

  @override
  void onInit() {}

  @override
  void onDispose() {
    passwordController.dispose();
    validPasswordController.dispose();
  }

  @override
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  Future<void> onNext() async {
    final isValidate = validatePasswords();
    if (isValidate) {
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => LoginScreen()),
      );
    }
  }

  void changeObscurePassword() {
    isObscurePassword = !isObscurePassword;
    setState(() {});
  }

  void changeObscureValidPassword() {
    isObscureValidPassword = !isObscureValidPassword;
    setState(() {});
  }

  bool validatePasswords() {
    if (passwordController.text.isEmpty) {
      errPassword = true;
      errTextPassword = 'Mật khẩu không được để trống';
      setState(() {});
      return false;
    } else if (passwordController.text.length < 6) {
      errPassword = true;
      errTextPassword = 'Mật khẩu phải có ít nhất 6 ký tự';
      setState(() {});
      return false;
    }
    if (validPasswordController.text.isEmpty) {
      errValidPassword = true;
      errTextValidPassword = 'Xác nhận mật khẩu không được để trống';
      setState(() {});
      return false;
    } else if (validPasswordController.text != passwordController.text) {
      errValidPassword = true;
      errTextValidPassword = 'Mật khẩu xác nhận không khớp';
      setState(() {});
      return false;
    }
    setState(() {});
    return true;
  }
}
