import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_textfield.dart';
import '../../../../../widget/loading.dart';
import '../bloc/change_password_bloc.dart';

class ChangePasswordScreen extends BaseView<ChangePasswordBloc> {
  const ChangePasswordScreen({super.key});

  @override
  ChangePasswordBloc createState() => ChangePasswordBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as ChangePasswordBloc;
    return Scaffold(
      backgroundColor: toHexToColor(actionColor),
      body: Center(
        child: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              SizedBox(height: 35),
              Center(
                child: Container(
                  decoration: BoxDecoration(
                    border: Border.all(
                      color: toHexToColor(borderColor),
                      width: 10,
                    ),
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(200),
                  ),

                  width: AppSize.w(0.8),
                  height: AppSize.w(0.8),
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(AppSize.w(0.85) / 2),
                    child: Image.asset(imgResetPassword, fit: BoxFit.cover),
                  ),
                ),
              ),
              SizedBox(height: 10),
              Center(
                child: AutoSizeText(
                  minFontSize: 18,
                  maxLines: 24,
                  'Đặt lại mật khẩu',
                  style: TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              SizedBox(height: 20),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Mật khẩu',
                    style: TextStyle(
                      fontSize: 12,
                      fontStyle: FontStyle.italic,
                      color: Colors.white,
                    ),
                  ),
                  SizedBox(
                    width: AppSize.w(0.9),
                    child: customTextField(
                      textSize: 14,
                      fillColor: toHexToColor(borderColor),
                      errorText:
                          bloc.errPassword == true
                              ? bloc.errTextPassword
                              : null,
                      borderColors: toHexToColor(borderColorGreen),
                      isPassword: true,
                      isObscure: bloc.isObscurePassword,
                      isObscurePassword: (value) {
                        bloc.changeObscurePassword();
                      },
                      bloc.passwordController,
                      hintText: '******',
                      isBorder: true,
                      height: 50,
                      textColor: toHexToColor(primaryTextColor),
                    ),
                  ),
                ],
              ),
              SizedBox(height: 10),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Nhập lại Mật khẩu',
                    style: TextStyle(
                      fontSize: 12,
                      fontStyle: FontStyle.italic,
                      color: Colors.white,
                    ),
                  ),
                  SizedBox(
                    width: AppSize.w(0.9),
                    child: customTextField(
                      textSize: 14,
                      fillColor: toHexToColor(borderColor),
                      errorText:
                          bloc.errValidPassword == true
                              ? bloc.errTextValidPassword
                              : null,
                      borderColors: toHexToColor(borderColorGreen),
                      isPassword: true,
                      isObscure: bloc.isObscureValidPassword,
                      isObscurePassword: (value) {
                        bloc.changeObscureValidPassword();
                      },
                      bloc.validPasswordController,
                      hintText: '******',
                      isBorder: true,
                      height: 50,
                      textColor: toHexToColor(primaryTextColor),
                    ),
                  ),
                ],
              ),

              SizedBox(height: 40),
              InkWell(
                onTap: () async {
                  await showLoadingDialog(
                    context: context,
                    action: () async {
                      await bloc.onNext(); // Thực hiện hành động sau khi delay
                      // Đảm bảo chờ đợi thời gian 1 giây
                      await Future.delayed(
                        const Duration(milliseconds: 300),
                      ); // Thêm await
                    },
                  );
                },

                child: Container(
                  width: AppSize.w(0.4),
                  height: 45,
                  decoration: BoxDecoration(
                    color: toHexToColor(borderColor),
                    // color: toHexToColor(actionColor),
                    borderRadius: BorderRadius.circular(10),
                    border: Border.all(color: toHexToColor(appBarColor)),
                  ),
                  child: Center(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          'Tiếp tục',
                          style: TextStyle(
                            // color: toHexToColor(borderColor),
                            color: toHexToColor(appBarColor),
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        SizedBox(width: 10),
                        Container(
                          decoration: BoxDecoration(
                            color: toHexToColor(appBarColor),
                            borderRadius: BorderRadius.circular(50),
                          ),
                          child: Padding(
                            padding: const EdgeInsets.all(5.0),
                            child: Icon(
                              Icons.arrow_forward_ios_outlined,
                              size: 15,
                              color: toHexToColor(borderColor),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
              SizedBox(height: 20),
            ],
          ),
        ),
      ),
    );
  }

  @override
  ChangePasswordBloc createBloc() => ChangePasswordBloc();
}
