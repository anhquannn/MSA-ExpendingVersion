import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/user_login_request.dart';
import 'package:msa/feature/presentation/logins/forgot_pasword/ui/forgot_password_screen.dart';
import 'package:msa/feature/presentation/logins/register/ui/register_screen.dart';
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
  bool isShowPass = false;
  String validPassword = '';
  String validEmail = '';

  bool? isSuccess = false;

  // Override contextKey để đảm bảo nhận dạng đúng context của LoginScreen
  @override
  String get contextKey => 'LoginScreen';

  @override
  void onInit() {
    // Gọi khi khởi tạo bloc
  }

  @override
  void onDispose() {
    emailController.dispose();
    passwordController.dispose();
    emailNode.dispose();
    passwordNode.dispose();
  }

  Future<bool> login() async {
    final email = emailController.text;
    final password = passwordController.text;

    final isEmailValid = validateEmail(email);
    final isPasswordValid = validatePassword(password);

    if (!isEmailValid || !isPasswordValid) {
      viewSetState(() {});
      return false;
    }

    isSuccess = await _userUseCases.login.call(
      UserLoginRequest(email: email, password: password),
    );

    HttpConnection.email = email;
    viewSetState(() {});
    return isSuccess == true;
  }

  void forgotPassword() {
    Navigator.push(
      viewContext,
      MaterialPageRoute(builder: (viewContext) => const ForgotPasswordScreen()),
    );
  }

  void onRegister() {
    Navigator.push(
      viewContext,
       MaterialPageRoute(builder: (viewContext) => const RegisterScreen()),
    );
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
    if (value.length < 2) {
      isValidPassword = true;
      validPassword = 'Mật khẩu phải lớn hơn 6 ký tự';
      return false;
    }
    isValidPassword = false;
    return true;
  }

  void obscurePassword(bool value) {
    isShowPass = value;
    // Sử dụng viewSetState thay vì setState để cập nhật UI
    viewSetState(() {});
  }

  // Hàm tiện ích để hiển thị thông báo từ bất kỳ đâu
  void showErrorMessage(String message) {
    ScaffoldMessenger.of(
      viewContext,
    ).showSnackBar(SnackBar(content: Text(message)));
  }

  // Hàm tiện ích để truy cập từ bên ngoài
  static void showLoginError(String message) {
    final context = AppContext.of('LoginScreen');
    if (context != null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text(message)));
    }
  }

  @override
  Widget build(BuildContext context) => widget.build(context);
}
