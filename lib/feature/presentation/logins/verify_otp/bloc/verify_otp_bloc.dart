import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../../welcom/welcom1.dart';
import '../ui/verify_otp_screen.dart';

class VerifyOtpBloc extends BaseBloc<VerifyOtpScreen> {
  final List<TextEditingController> controllers = List.generate(
    6,
    (_) => TextEditingController(),
  );
  final List<FocusNode> focusNodes = List.generate(6, (_) => FocusNode());
  bool isKeyboardVisible = false;

  bool isValid = false;

  @override
  void onInit() {}

  @override
  void onDispose() {
    for (var node in focusNodes) {
      node.dispose();
    }
    super.dispose();
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

  void onOtpSubmit() {
    String otp = controllers.map((controller) => controller.text).join();
    if (otp.length < 6) {
      isValid = true;
    } else {
      isValid = false;
    }
    setState(() {});
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
