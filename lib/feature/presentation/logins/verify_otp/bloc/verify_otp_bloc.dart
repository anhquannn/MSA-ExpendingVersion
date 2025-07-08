import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:go_router/go_router.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/user_login_request.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:rxdart/rxdart.dart';
import '../../../../data/datasources/local/starage.dart';
import '../../../../domain/usecase/user_use_case.dart';
import '../ui/verify_otp_screen.dart';

class VerifyOtpBloc extends BaseBloc<VerifyOtpScreen> {
  final List<TextEditingController> controllers = List.generate(
    6,
    (_) => TextEditingController(),
  );
  final List<FocusNode> focusNodes = List.generate(6, (_) => FocusNode());
  bool isKeyboardVisible = false;
  int secondsRemaining = 0;
  final streamSecondsRemaining = BehaviorSubject<int>();
  Timer? timer;
  bool isValid = false;
  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();

  @override
  String get contextKey => 'VerifyOtpScreen';

  @override
  void onInit() {}

  @override
  void onDispose() {}

  @override
  void onReady() {
    startCountdown();
  }

  @override
  void onResumed() {}

  void startCountdown() {
    setState(() {
      secondsRemaining = 1;
    });

    timer?.cancel();
    timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (secondsRemaining <= 1) {
        setState(() {
          secondsRemaining = 0;
        });
      } else {
        setState(() {
          secondsRemaining--;
        });
      }
    });
  }

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

  onResend() async {
    await Repository.onResendOtp(
      widget.request ?? UserLoginRequest(password: '', email: ''),
    );
  }

  Future<bool> onOtpSubmit(BuildContext context) async {
    bool isSuccess = false;
    String otp = controllers.map((controller) => controller.text).join();

    if (otp.length < 6) {
      isValid = true;
      setState(() {});
      return false;
    }
    bool success = await _userUseCases.verifyOtp(otp);

    // await Repository.onUpdateDeviceId();
    if (success) {
      // Navigator.pushAndRemoveUntil(
      //   viewContext,
      //   MaterialPageRoute(builder: (context) => HomeScreen()),
      //   (route) => false,
      // );
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => HomeScreen()),
      );
    } else {
      showLoginError('Sai OPT', viewContext);
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
