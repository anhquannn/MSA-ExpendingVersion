import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:go_router/go_router.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import '../../../../domain/usecase/user_use_case.dart';
import '../ui/verify_otp_screen.dart';

class VerifyOtpBloc extends BaseBloc<VerifyOtpScreen> {
  final List<TextEditingController> controllers = List.generate(
    6,
    (_) => TextEditingController(),
  );
  final List<FocusNode> focusNodes = List.generate(6, (_) => FocusNode());
  bool isKeyboardVisible = false;

  bool isValid = false;
  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();

  @override
  void onInit() {}

  @override
  void onDispose() {
  }

  @override
  void onReady() {}

  @override
  void onResumed() {}

  void addToOtp(String value) {
    for (int i = 0; i < 6; i++) {
      if (controllers[i].text.isEmpty) {
        controllers[i].text = value;
        if (i < 5) {
          FocusScope.of(context).nextFocus();
        }
        break;
      }
    }
  }

  void onTapTextField(int index) {
    setState(() {
      isKeyboardVisible = true;
    });
    FocusScope.of(context).requestFocus(focusNodes[index]);
  }

  Future<bool> onOtpSubmit(BuildContext context) async {
    bool isSuccess = false;
    String otp = controllers.map((controller) => controller.text).join();

    if (otp.length < 6) {
      isValid = true;
      setState(() {});
      return false;
    }
    if(otp==HttpConnection.otp){
      context.push('/login');
    }
    return isSuccess;
  }
  void onHide() {
    setState(() {
      isKeyboardVisible = false;
    });
  }

  void onDelete() {
    // Xóa ký tự cuối trong OTP
    for (int i = 5; i >= 0; i--) {
      if (controllers[i].text.isNotEmpty) {
        controllers[i].clear();
        FocusScope.of(context).previousFocus();
        break;
      }
    }
  }

  @override
  Widget build(BuildContext context) => widget.build(context);
}
