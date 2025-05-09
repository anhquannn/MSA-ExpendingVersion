import 'package:auto_size_text/auto_size_text.dart' as forgot_pasword_screen;
import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/loading.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../widget/custom_textfield.dart';
import '../bloc/forgot_password_bloc.dart';

class ForgotPasswordScreen extends BaseView<ForgotPasswordBloc> {
  const ForgotPasswordScreen({super.key});

  @override
  ForgotPasswordBloc createState() => ForgotPasswordBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as ForgotPasswordBloc;
    return Scaffold(
      appBar: AppBar(
        backgroundColor: toHexToColor(actionColor),
        leading: InkWell(
          onTap: () {
            Navigator.pop(context);
          },
          child: Icon(Icons.arrow_back_ios, color: Colors.white),
        ),
      ),
      backgroundColor: toHexToColor(actionColor),
      body: Center(
        child: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
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

                  width: AppSize.w(0.8), // Đặt chiều rộng
                  height: AppSize.w(
                    0.8,
                  ), // Đặt chiều cao bằng chiều rộng để tạo thành hình tròn
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(
                      AppSize.w(0.85) / 2,
                    ), // Bo tròn với bán kính bằng 1 nửa chiều rộng
                    child: Image.asset(
                      imgForgotPassword,
                      fit:
                          BoxFit.cover, // Tùy chọn fit để hình ảnh không bị méo
                    ),
                  ),
                ),
              ),
              SizedBox(height: 20),
              Center(
                child: AutoSizeText(
                  minFontSize: 18,
                  maxLines: 24,
                  'Quên mật khẩu.',
                  style: TextStyle(color: Colors.white),
                ),
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 15,
                      vertical: 5,
                    ),
                    child: AutoSizeText(
                      style: TextStyle(
                        color: Colors.white,
                        fontStyle: FontStyle.italic,
                      ),
                      'Nhập email',
                      minFontSize: 8,
                      maxFontSize: 16,
                    ),
                  ),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 10),
                    child: Center(
                      child: SizedBox(
                        width: AppSize.w(0.9),
                        child: customTextField(
                          textColor: toHexToColor(primaryTextColor),
                          errorText: bloc.isValidEmail ? bloc.validEmail : null,
                          prefixIcon: Icon(
                            Icons.email_outlined,
                            color: toHexToColor(appBarColor),
                          ),
                          borderColors: toHexToColor(borderColor),
                          isBorder: true,
                          bloc.emailController,
                          height: 45,
                          fillColor: toHexToColor(borderColor),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
              SizedBox(height: AppSize.h(0.15)),
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
                    border: Border.all(color: toHexToColor(borderColor)),
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
            ],
          ),
        ),
      ),
    );
  }

  @override
  ForgotPasswordBloc createBloc() => ForgotPasswordBloc();
}
