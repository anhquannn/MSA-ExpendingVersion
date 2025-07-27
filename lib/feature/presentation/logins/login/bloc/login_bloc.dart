import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/login_request_model.dart';
import 'package:msa/feature/data/model/request/user_login_request.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/feature/presentation/logins/forgot_pasword/ui/forgot_password_screen.dart';
import 'package:msa/feature/presentation/logins/register/ui/register_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import '../../../../data/datasources/local/starage.dart';
import '../../../../domain/usecase/user_use_case.dart';
import '../ui/login_screen.dart';

class LoginBloc extends BaseBloc<LoginScreen> {
  final FocusNode emailNode = FocusNode();
  final FocusNode passwordNode = FocusNode();
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();

  bool isValidPassword = false;
  bool isValidEmail = false;
  bool isShowPass = true;
  String validPassword = '';
  String validEmail = '';

  bool? isSuccess = false;

  // Override contextKey để đảm bảo nhận dạng đúng context của LoginScreen
  @override
  String get contextKey => 'LoginScreen';

  @override
  void onInit() {
    isShowPass = true;
  }

  @override
  void onDispose() {
    emailController.dispose();
    passwordController.dispose();
    emailNode.dispose();
    passwordNode.dispose();
  }

  login() async {
    final email = emailController.text;
    final password = passwordController.text;

    final isEmailValid = validateEmail(email);
    final isPasswordValid = validatePassword(password);

    if (!isEmailValid || !isPasswordValid) {
      viewSetState(() {});
      return false;
    }
    isSuccess = await Repository.onLoginFCM(
      LoginRequest(
        email: email,
        password: password,
        fcmToken: Storage.deviceId ?? '',
        platform: PlatformType.ANDROID,
      ),
    );

    Storage.email = email;
    if (isSuccess == false) {
      showLoginError('Sai mật khẩu!!!');
    } else {
      // await Repository.onUpdateDeviceId();
    }
    viewSetState(() {});
    return isSuccess == true;
  }

  getFcmToken() async {
    final fcmToken = await FirebaseMessaging.instance.getToken();
    if (fcmToken != null) {
      Storage.deviceId = fcmToken ?? '';
      Storage.saveDeviceId(fcmToken ?? '');
      print('FCM Token: $fcmToken');
    }
  }

  forgotPassword() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(builder: (viewContext) => const ForgotPasswordScreen()),
    );
  }

  onRegister() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(builder: (viewContext) => const RegisterScreen()),
    );
  }

  onLoginWithoutAccount(BuildContext context) {
    Storage.isLogin = false;
    Navigator.push(
      viewContext,
      MaterialPageRoute(builder: (viewContext) => const HomeScreen()),
    );
  }

  @override
  void onReady() {
    isShowPass = true;
  }

  @override
  void onResumed() {
    // Gọi khi ứng dụng quay lại từ nền
  }

  validateEmail(String? value) {
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

  validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      isValidPassword = true;
      validPassword = 'Mật khẩu không được để trống';
      return false;
    }
    if (value.length < 2) {
      isValidPassword = true;
      validPassword = 'Mật khẩu phải lớn hơn 6 ký tự';
      return false;
    }
    isValidPassword = false;
    return true;
  }

  obscurePassword(bool value) {
    isShowPass = value;
    viewSetState(() {});
  }

  showErrorMessage(String message) {
    ScaffoldMessenger.of(
      viewContext,
    ).showSnackBar(SnackBar(content: Text(message)));
  }

  static showLoginError(String message) {
    final context = AppContext.of('LoginScreen');
    if (context != null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text(message)));
    }
  }

  loginWithGoogle(BuildContext bContext) async {
    Storage.isLogin = true;
    // if (Storage.deviceId == null) {
    await getFcmToken();
    // }
    // try {
    final googleUser = await GoogleSignIn().signIn();
    if (googleUser == null) {
      print('❌ Người dùng đã hủy đăng nhập');
      return null;
    }

    final googleAuth = await googleUser.authentication;
    final accessToken = googleAuth.accessToken;
    final idToken = googleAuth.idToken;

    print('❌ Người dùng đã hủy đăng nhập ___ $idToken');
    print('❌ Người dùng đã hủy đăng nhập ___ $accessToken');

    print('👉 Gọi loginWithGoogleToken...');
    final isSuccess = await Repository.loginWithGoogleToken(accessToken ?? '');
    print('✅ Kết quả loginWithGoogleToken: $isSuccess');
    if (isSuccess) {
      final UserModel response = await Repository.onGetUserInfo();

      await Repository.onUpdateDeviceId(response.userId ?? 0);
      final addressSuccess = await onGetAddress();
      showCustomDialog(
        bContext,
        AppSize.w(0.9),
        AppSize.w(0.9),
        'Thông báo',
        Text('Đăng nhập thành công !!!'),
        true,
        false,
        Icon(
          Icons.check_circle_outline,
          color: toHexToColor(primaryColorGreen),
        ),
        onClose: () {
          Navigator.push(
            bContext,
            MaterialPageRoute(builder: (context) => HomeScreen()),
          );
        },
      );
    } else {
      showCustomDialog(
        bContext,
        AppSize.w(0.9),
        AppSize.w(0.9),
        'Thông báo',
        Text('Đăng nhập không thành công !!!'),
        true,
        false,
        Icon(
          Icons.warning_amber_rounded,
          color: toHexToColor(primaryColorGreen),
        ),
        onClose: () {
          Navigator.pop(bContext);
          // Navigator.push(
          //   bContext,
          //   MaterialPageRoute(builder: (context) => HomeScreen()),
          // );
        },
      );
    }

    print('🔑 Access Token Google: $accessToken');

    return accessToken;
    // } catch (e, stack) {
    //   print('❌ Lỗi khi đăng nhập bằng Google: $e');
    //   print('📛 Stacktrace: $stack');
    //   return null;
    // }
  }

  onGetAddress() async {
    try {
      final response = await Repository.getUserAddresses();
      if (response[0] != null && response[0] != []) {
        Storage.saveAddress(response[0]);
      }
    } catch (_) {}
  }

  logoutFromGoogle() async {
    final GoogleSignIn googleSignIn = GoogleSignIn();
    try {
      final isSignedIn = await googleSignIn.isSignedIn();
      if (isSignedIn) {
        await googleSignIn.signOut();
        print('✅ Đã đăng xuất Google thành công');
      } else {
        print('⚠️ Người dùng chưa đăng nhập bằng Google');
      }
    } catch (e) {
      print('❌ Lỗi khi đăng xuất Google: $e');
    }
  }

  @override
  Widget build(BuildContext context) => widget.build(context);
}
