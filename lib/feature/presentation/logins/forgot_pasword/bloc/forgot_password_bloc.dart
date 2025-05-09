import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../../welcom/welcom1.dart';
import '../ui/forgot_password_screen.dart';

class ForgotPasswordBloc extends BaseBloc<ForgotPasswordScreen> {
  final TextEditingController emailController = TextEditingController();

  bool isValidEmail = false;
  String validEmail = '';

  @override
  void onInit() {}

  @override
  void onDispose() {
    emailController.dispose();
  }

  Future<void> onNext() async {
    // final email = validateEmail(emailController.text);
    setState(() {});
    // if (email) {
    //   Navigator.push(
    //     context,
    //     MaterialPageRoute(builder: (context) => OnboardingScreen()),
    //   );
    // }
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

  @override
  Widget build(BuildContext context) => widget.build(context);
}
