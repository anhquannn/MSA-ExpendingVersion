// import 'package:auto_size_text/auto_size_text.dart';
// import 'package:flutter/material.dart';
// import 'package:go_router/go_router.dart';
// import 'package:msa/core/config/base_bloc.dart';
// import 'package:msa/core/utils/prarse_color.dart';
// import 'package:msa/feature/presentation/logins/verify_otp/ui/verify_otp_screen.dart';
// import 'package:msa/main.dart';
// import 'package:msa/widget/loading.dart';
// import '../../../../../core/config/config.dart';
// import '../../../../../core/config/constant.dart';
// import '../../../../../widget/custom_button.dart';
// import '../../../../../widget/custom_textfield.dart';
// import '../../../../../widget/custom_widget.dart';
// import '../bloc/login_bloc.dart';

// class LoginScreen extends BaseView<LoginBloc> {
//   const LoginScreen({super.key});

//   @override
//   LoginBloc createState() => LoginBloc();

//   Widget build(BuildContext context) {

//     return Scaffold(
//       backgroundColor: toHexToColor(backgroundColor),
//       appBar: PreferredSize(
//         preferredSize: Size.fromHeight(kToolbarHeight),
//         child: AppBar(
//           automaticallyImplyLeading: false,
//           elevation: 0, // Tắt shadow của AppBar
//           flexibleSpace: Container(
//             decoration: BoxDecoration(
//               gradient: LinearGradient(
//                 begin: Alignment.centerLeft,
//                 end: Alignment.centerRight,
//                 colors: [
//                   toHexToColor(primaryButtonColor),
//                   toHexToColor(appBarColor),
//                 ],
//               ),
//             ),
//           ),
//           // title: Text('Tiêu đề của AppBar'),
//         ),
//       ),

//       body: Builder(
//         builder: (context) {
//           final bloc = context.findAncestorStateOfType<LoginBloc>()!;
//           return SizedBox.expand(
//             child: Stack(
//               children: [
//                 ClipPath(
//                   clipper: SmoothOvalTopClipper(),
//                   child: Container(
//                     height: 50,
//                     width: double.infinity,
//                     decoration: BoxDecoration(
//                       gradient: LinearGradient(
//                         begin: Alignment.centerLeft,
//                         end: Alignment.centerRight,
//                         colors: [
//                           toHexToColor(primaryButtonColor),
//                           toHexToColor(appBarColor),
//                         ],
//                       ),
//                     ),
//                   ),
//                 ),

//                 Positioned(
//                   left: 0,
//                   right: 0,
//                   child: Align(
//                     alignment: Alignment.topCenter,
//                     child: Text(
//                       'Chào mừng trở lại !!!!',
//                       style: TextStyle(
//                         color: Colors.white,
//                         fontWeight: FontWeight.bold,
//                         fontSize: 20,
//                       ),
//                     ),
//                   ),
//                 ),
//                 Positioned(
//                   left: 0,
//                   right: 0,
//                   child: Center(
//                     child: SizedBox(
//                       height: AppSize.h(0.24),
//                       child: Image.asset(imgIconApp, fit: BoxFit.cover),
//                     ),
//                   ),
//                 ),
//                 Positioned(
//                   top: AppSize.h(0.2),
//                   left: 20,
//                   right: 20,
//                   child: Container(
//                     height: AppSize.h(0.62),
//                     decoration: BoxDecoration(
//                       color: Colors.white,
//                       borderRadius: BorderRadius.circular(10),
//                       boxShadow: [
//                         BoxShadow(
//                           color: toHexToColor(dropShadowColor),
//                           blurRadius: 10,
//                           spreadRadius: 2,
//                           offset: const Offset(1, 1),
//                         ),
//                       ],
//                     ),
//                     child: _login(bloc),
//                   ),
//                 ),
//               ],
//             ),
//           );
//         },
//       ),
//     );
//   }

//   Widget _login(LoginBloc bloc) {
//     return Column(
//       mainAxisSize: MainAxisSize.min,
//       children: [
//         Padding(
//           padding: EdgeInsets.only(top: 10, bottom: 30),
//           child: AutoSizeText(
//             'Đăng nhập',
//             minFontSize: 20,
//             maxLines: 24,
//             style: TextStyle(
//               color: toHexToColor(primaryTextColor),
//               fontWeight: FontWeight.bold,
//             ),
//           ),
//         ),
//         Padding(
//           padding: EdgeInsets.symmetric(vertical: 10, horizontal: 20),
//           child: customTextField(
//             height: 45,
//             borderColors: toHexToColor(appBarColor),
//             errorText: bloc.isValidEmail == true ? bloc.validEmail : null,
//             node: bloc.emailNode,
//             textSize: 16,
//             bloc.emailController,
//             hintText: 'examp@gmail.com',
//             labelText: Text('Email'),
//             isBorder: true,
//             textColor: toHexToColor(primaryTextColor),
//           ),
//         ),
//         Padding(
//           padding: EdgeInsets.symmetric(horizontal: 20),
//           child: Column(
//             mainAxisSize: MainAxisSize.min,
//             children: [
//               customTextField(
//                 height: 45,
//                 isPassword: true,
//                 borderColors: toHexToColor(appBarColor),
//                 isObscurePassword: (value) {
//                   bloc.obscurePassword(value);
//                 },
//                 isObscure: bloc.isShowPass,
//                 errorText:
//                     bloc.isValidPassword == true ? bloc.validPassword : null,
//                 node: bloc.passwordNode,
//                 textSize: 16,
//                 bloc.passwordController,
//                 hintText: '******',
//                 labelText: Text('Password'),
//                 isBorder: true,
//                 textColor: toHexToColor(primaryTextColor),
//               ),
//               Builder(
//                 builder: (context) {
//                   return Align(
//                     alignment: Alignment.centerRight,
//                     child: TextButton(
//                       onPressed: () {
//                         context.push('/forgot_password');
//                       },
//                       child: AutoSizeText(
//                         'Quên mật khẩu',
//                         style: TextStyle(
//                           fontStyle: FontStyle.italic,
//                           decoration: TextDecoration.underline,
//                           color: Colors.blueAccent,
//                           fontSize: 12,
//                         ),
//                       ),
//                     ),
//                   );
//                 },
//               ),
//             ],
//           ),
//         ),
//         SizedBox(height: 25),
//         Builder(
//           builder:
//               (ctx) => customButton(
//                 isBoxShadow: false,
//                 () async {
//                   CustomLoadingDialog.show(
//                     context: context,
//                     text: 'Đang xử lý...',
//                     loadingWidget: const CircularProgressIndicator(
//                       valueColor: AlwaysStoppedAnimation<Color>(Colors.blue),
//                     ),
//                   );
//                   await bloc.login();
//                   CustomLoadingDialog.hide(context);

//                   if (bloc.isSuccess == true && ctx.mounted) {
//                     ctx.push('/verify_otp');
//                     Navigator.push(
//                       ctx,
//                       MaterialPageRoute(builder: (_) => VerifyOtpScreen()),
//                     );
//                   } else if (ctx.mounted) {
//                     showDialog(
//                       context: ctx,
//                       builder:
//                           (_) => AlertDialog(
//                             title: const Text('Đăng nhập thất bại'),
//                             content: const Text(
//                               'Email hoặc mật khẩu không đúng.',
//                             ),
//                             actions: [
//                               TextButton(
//                                 onPressed: () => Navigator.of(ctx).pop(),
//                                 child: const Text('OK'),
//                               ),
//                             ],
//                           ),
//                     );
//                   }
//                 },
//                 AppSize.w(0.4),
//                 40,
//                 const Text(
//                   'Đăng nhập',
//                   style: TextStyle(
//                     color: Colors.white,
//                     fontSize: 16,
//                     fontWeight: FontWeight.bold,
//                   ),
//                 ),
//                 typeButton: 1,
//               ),
//         ),
//         Padding(
//           padding: const EdgeInsets.symmetric(vertical: 20),
//           child: Row(
//             children: [
//               Expanded(
//                 child: Divider(
//                   color: toHexToColor(appBarColor),
//                   thickness: 1.5,
//                 ),
//               ),
//               Padding(
//                 padding: const EdgeInsets.symmetric(horizontal: 8),
//                 child: Text(
//                   'Đăng nhập bằng cách khác',
//                   style: TextStyle(
//                     color: toHexToColor(primaryTextColor),
//                     fontSize: 12,
//                   ),
//                 ),
//               ),
//               Expanded(
//                 child: Divider(
//                   color: toHexToColor(appBarColor),
//                   thickness: 1.5,
//                 ),
//               ),
//             ],
//           ),
//         ),
//         Row(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: [
//             customButton(
//               bloc.loginWithGoogle,
//               45,
//               45,
//               Padding(
//                 padding: const EdgeInsets.all(10),
//                 child: Image.asset(iconGoogle),
//               ),
//               backgroundColorButton: Colors.white,
//             ),
//             // SizedBox(width: 20),
//             // customButton(
//             //   _bloc.loginWithGoogle,
//             //   45,
//             //   45,
//             //   Padding(
//             //     padding: const EdgeInsets.all(10),
//             //     child: Image.asset(iconFacebook),
//             //   ),
//             //   backgroundColorButton: Colors.white,
//             // ),
//           ],
//         ),
//         Spacer(),
//         Row(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: [
//             Text(
//               'Bạn chưa có tài khoản?',
//               style: TextStyle(
//                 fontStyle: FontStyle.italic,
//                 color: toHexToColor(primaryTextColor),
//                 fontSize: 12,
//               ),
//             ),
//             TextButton(
//               onPressed: bloc.createUser,
//               child: Text(
//                 'Đăng ký',
//                 style: TextStyle(
//                   fontStyle: FontStyle.italic,
//                   decoration: TextDecoration.underline,
//                   color: Colors.blueAccent,

//                   fontSize: 14,
//                 ),
//               ),
//             ),
//           ],
//         ),
//       ],
//     );
//   }

//   @override
//   LoginBloc createBloc() => LoginBloc();
// }

// class SmoothOvalTopClipper extends CustomClipper<Path> {
//   @override
//   Path getClip(Size size) {
//     final path = Path();

//     path.lineTo(0, size.height * 0.6);
//     path.quadraticBezierTo(
//       size.width / 2,
//       size.height * 1.2,
//       size.width,
//       size.height * 0.6,
//     );
//     path.lineTo(size.width, 0);
//     path.close();

//     return path;
//   }

//   @override
//   bool shouldReclip(CustomClipper<Path> oldClipper) => false;
// }
import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/presentation/logins/verify_otp/ui/verify_otp_screen.dart';
import 'package:msa/widget/loading.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../widget/custom_button.dart';
import '../../../../../widget/custom_textfield.dart';
import '../bloc/login_bloc.dart';

class LoginScreen extends BaseView<LoginBloc> {
  const LoginScreen({super.key});

  @override
  LoginBloc createBloc() => LoginBloc();

  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: toHexToColor(backgroundColor),
      appBar: PreferredSize(
        preferredSize: Size.fromHeight(kToolbarHeight),
        child: AppBar(
          automaticallyImplyLeading: false,
          elevation: 0, // Tắt shadow của AppBar
          flexibleSpace: Container(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.centerLeft,
                end: Alignment.centerRight,
                colors: [
                  toHexToColor(primaryButtonColor),
                  toHexToColor(appBarColor),
                ],
              ),
            ),
          ),
          // title: Text('Tiêu đề của AppBar'),
        ),
      ),

      body: Builder(
        builder: (context) {
          // Sử dụng AppContext để lấy bloc thay vì findAncestorStateOfType
          final bloc = context.findAncestorStateOfType<LoginBloc>()!;
          // Hoặc có thể dùng: AppContext.ofType<LoginBloc>() nếu cần truy cập từ widget khác

          return SizedBox.expand(
            child: Stack(
              children: [
                ClipPath(
                  clipper: SmoothOvalTopClipper(),
                  child: Container(
                    height: 50,
                    width: double.infinity,
                    decoration: BoxDecoration(
                      gradient: LinearGradient(
                        begin: Alignment.centerLeft,
                        end: Alignment.centerRight,
                        colors: [
                          toHexToColor(primaryButtonColor),
                          toHexToColor(appBarColor),
                        ],
                      ),
                    ),
                  ),
                ),

                Positioned(
                  left: 0,
                  right: 0,
                  child: Align(
                    alignment: Alignment.topCenter,
                    child: Text(
                      'Chào mừng trở lại !!!!',
                      style: TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                        fontSize: 20,
                      ),
                    ),
                  ),
                ),
                Positioned(
                  left: 0,
                  right: 0,
                  child: Center(
                    child: SizedBox(
                      height: AppSize.h(0.24),
                      child: Image.asset(imgIconApp, fit: BoxFit.cover),
                    ),
                  ),
                ),
                Positioned(
                  top: AppSize.h(0.2),
                  left: 20,
                  right: 20,
                  child: Container(
                    height: AppSize.h(0.62),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(10),
                      boxShadow: [
                        BoxShadow(
                          color: toHexToColor(dropShadowColor),
                          blurRadius: 10,
                          spreadRadius: 2,
                          offset: const Offset(1, 1),
                        ),
                      ],
                    ),
                    child: _login(bloc, context),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _login(LoginBloc bloc, BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Padding(
          padding: EdgeInsets.only(top: 10, bottom: 30),
          child: AutoSizeText(
            'Đăng nhập',
            minFontSize: 20,
            maxLines: 24,
            style: TextStyle(
              color: toHexToColor(primaryTextColor),
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        Padding(
          padding: EdgeInsets.symmetric(vertical: 10, horizontal: 20),
          child: customTextField(
            height: 45,
            borderColors: toHexToColor(appBarColor),
            errorText: bloc.isValidEmail == true ? bloc.validEmail : null,
            node: bloc.emailNode,
            textSize: 16,
            
            bloc.emailController,
            hintText: 'examp@gmail.com',
            labelText: Text('Email'),
            isBorder: true,
            textColor: toHexToColor(primaryTextColor),
          ),
        ),
        Padding(
          padding: EdgeInsets.symmetric(horizontal: 20),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              customTextField(
                height: 45,
                isPassword: true,
                borderColors: toHexToColor(appBarColor),
                isObscurePassword: (value) {
                  bloc.obscurePassword(value);
                },
                isObscure: bloc.isShowPass,
                errorText:
                    bloc.isValidPassword == true ? bloc.validPassword : null,
                node: bloc.passwordNode,
                textSize: 16,
                
                bloc.passwordController,
                hintText: '******',
                labelText: Text('Password'),
                isBorder: true,
                textColor: toHexToColor(primaryTextColor),
              ),
              Align(
                alignment: Alignment.centerRight,
                child: TextButton(
                  onPressed:
                      bloc.forgotPassword, // Sử dụng phương thức của bloc
                  child: AutoSizeText(
                    'Quên mật khẩu',
                    style: TextStyle(
                      fontStyle: FontStyle.italic,
                      decoration: TextDecoration.underline,
                      color: Colors.blueAccent,
                      fontSize: 12,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
        SizedBox(height: 25),
        customButton(
          isBoxShadow: false,
          () async {
            CustomLoadingDialog.show(
              context: context,
              text: 'Đang xử lý...',
              loadingWidget: const CircularProgressIndicator(
                valueColor: AlwaysStoppedAnimation<Color>(Colors.blue),
              ),
            );

            final success = await bloc.login();
            CustomLoadingDialog.hide(bloc.viewContext);

            if (success) {
              Navigator.push(
                bloc.viewContext,
                MaterialPageRoute(builder: (_) => VerifyOtpScreen()),
              );
            } else {
              // Sử dụng showErrorMessage từ bloc
              showDialog(
                context: bloc.viewContext,
                builder:
                    (_) => AlertDialog(
                      title: const Text('Đăng nhập thất bại'),
                      content: const Text('Email hoặc mật khẩu không đúng.'),
                      actions: [
                        TextButton(
                          onPressed: () => Navigator.of(bloc.viewContext).pop(),
                          child: const Text('OK'),
                        ),
                      ],
                    ),
              );
            }
          },
          AppSize.w(0.4),
          40,
          const Text(
            'Đăng nhập',
            style: TextStyle(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          typeButton: 1,
        ),
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 20),
          child: Row(
            children: [
              Expanded(
                child: Divider(
                  color: toHexToColor(appBarColor),
                  thickness: 1.5,
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8),
                child: Text(
                  'Đăng nhập bằng cách khác',
                  style: TextStyle(
                    color: toHexToColor(primaryTextColor),
                    fontSize: 12,
                  ),
                ),
              ),
              Expanded(
                child: Divider(
                  color: toHexToColor(appBarColor),
                  thickness: 1.5,
                ),
              ),
            ],
          ),
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            customButton(
              bloc.loginWithGoogle,
              45,
              45,
              Padding(
                padding: const EdgeInsets.all(10),
                child: Image.asset(iconGoogle),
              ),
              backgroundColorButton: Colors.white,
            ),
            // SizedBox(width: 20),
            // customButton(
            //   _bloc.loginWithGoogle,
            //   45,
            //   45,
            //   Padding(
            //     padding: const EdgeInsets.all(10),
            //     child: Image.asset(iconFacebook),
            //   ),
            //   backgroundColorButton: Colors.white,
            // ),
          ],
        ),
        Spacer(),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Bạn chưa có tài khoản?',
              style: TextStyle(
                fontStyle: FontStyle.italic,
                color: toHexToColor(primaryTextColor),
                fontSize: 12,
              ),
            ),
            TextButton(
              onPressed: bloc.onRegister,
              child: Text(
                'Đăng ký',
                style: TextStyle(
                  fontStyle: FontStyle.italic,
                  decoration: TextDecoration.underline,
                  color: Colors.blueAccent,
                  fontSize: 14,
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class SmoothOvalTopClipper extends CustomClipper<Path> {
  @override
  Path getClip(Size size) {
    final path = Path();

    path.lineTo(0, size.height * 0.6);
    path.quadraticBezierTo(
      size.width / 2,
      size.height * 1.2,
      size.width,
      size.height * 0.6,
    );
    path.lineTo(size.width, 0);
    path.close();

    return path;
  }

  @override
  bool shouldReclip(CustomClipper<Path> oldClipper) => false;
}
