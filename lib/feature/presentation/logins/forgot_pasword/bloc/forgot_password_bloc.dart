import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import '../../../../../widget/loading.dart';
import '../../../../domain/usecase/user_use_case.dart';
import '../ui/forgot_password_screen.dart';

class ForgotPasswordBloc extends BaseBloc<ForgotPasswordScreen> {
  final TextEditingController emailController = TextEditingController();

  bool isValidEmail = false;
  String validEmail = '';
  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();

    @override
  String get contextKey => 'ForgotPasswordScreen';

  @override
  void onInit() {}

  @override
  void onDispose() {
    emailController.dispose();
  }

  Future<void> onNext(BuildContext context) async {
    final emailValid = validateEmail(emailController.text);
    setState(() {});

    if (!emailValid) return;

    bool success = await _userUseCases.resetPasswordWithoutOtp(
      emailController.text,
    );

    await showLoading(context: viewContext);
    if (success && context.mounted) {
      Navigator.pushAndRemoveUntil(
        context,
        MaterialPageRoute(builder: (viewContext) => LoginScreen()),
        (route) => true,
      );
    }
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
