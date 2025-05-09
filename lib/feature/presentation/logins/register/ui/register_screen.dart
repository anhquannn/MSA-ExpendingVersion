import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';

import '../../../../../core/config/base_bloc.dart';
import '../../../../../widget/custom_sliable_button.dart';
import '../../../../../widget/custom_textfield.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';
import '../bloc/register_bloc.dart';

class RegisterScreen extends BaseView<RegisterBloc> {
  RegisterScreen({super.key});

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as RegisterBloc;
    return CustomScaffold(
      centerTitle: true,
      appBarGradient: false,
      title: Text(
        'Đăng ký',
        style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
      ),

      bodyBuilder: (controller) {
        return Center(
          child: SingleChildScrollView(
            child: Column(
              crossAxisAlignment:
                  CrossAxisAlignment.center, // Căn giữa theo chiều ngang
              mainAxisAlignment:
                  MainAxisAlignment.start, // Căn giữa theo chiều dọc
              children: [
                Center(
                  child: Container(
                    decoration: BoxDecoration(
                      color: Colors.white,
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.1),
                          spreadRadius: 2,
                          blurRadius: 4,
                          offset: Offset(2, 2), // đổ bóng theo trục Y
                        ),
                      ],
                      borderRadius: BorderRadius.circular(10),
                    ),
                    // height: AppSize.h(0.7),
                    width: AppSize.w(0.9),
                    // color: toHexToColor(backgroundColor),
                    child: Padding(
                      padding: const EdgeInsets.all(15.0),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Padding(
                            padding: EdgeInsets.symmetric(vertical: 5),
                            child: customTextField(
                              errorText:
                                  bloc.errName == true
                                      ? bloc.errTextName
                                      : null,
                              borderColors: toHexToColor(borderColorGreen),
                              bloc.nameController,
                              hintText: 'Nguyen Van A',
                              isBorder: true,
                              height: 45,
                              textColor: toHexToColor(primaryTextColor),
                              labelText: Text('Họ và tên'),
                            ),
                          ),
                          Padding(
                            padding: EdgeInsets.symmetric(vertical: 10),
                            child: customTextField(
                              errorText:
                                  bloc.errEmail == true
                                      ? bloc.errTextEmail
                                      : null,
                              borderColors: toHexToColor(borderColorGreen),
                              bloc.emailController,
                              hintText: 'examp@gmail.com',
                              isBorder: true,
                              height: 45,
                              textColor: toHexToColor(primaryTextColor),
                              labelText: Text('Email'),
                            ),
                          ),
                          Padding(
                            padding: EdgeInsets.symmetric(vertical: 10),
                            child: customTextField(
                              errorText:
                                  bloc.errPhoneNumber == true
                                      ? bloc.errTextPhoneNumber
                                      : null,
                              borderColors: toHexToColor(borderColorGreen),
                              bloc.phoneNumberController,
                              hintText: '0123456789',
                              isBorder: true,
                              height: 45,
                              textColor: toHexToColor(primaryTextColor),
                              labelText: Text('Số điện thoại'),
                            ),
                          ),
                          Padding(
                            padding: EdgeInsets.symmetric(vertical: 10),
                            child: customTextField(
                              errorText:
                                  bloc.errPassword == true
                                      ? bloc.errTextValidPassword
                                      : null,
                              borderColors: toHexToColor(borderColorGreen),
                              isPassword: true,
                              isObscure: bloc.obscurePassword!,
                              isObscurePassword: (value) {
                                bloc.changObscurePassword(value);
                              },
                              bloc.passwordController,
                              hintText: '******',
                              isBorder: true,
                              height: 45,
                              textColor: toHexToColor(primaryTextColor),
                              labelText: Text('Mật khẩu'),
                            ),
                          ),
                          Padding(
                            padding: EdgeInsets.symmetric(vertical: 10),
                            child: customTextField(
                              errorText:
                                  bloc.errValidPassword == true
                                      ? bloc.errTextValidPassword
                                      : null,
                              borderColors: toHexToColor(borderColorGreen),
                              isPassword: true,
                              isObscure: bloc.obscureValidPassword!,
                              isObscurePassword: (value) {
                                bloc.changValidObscurePassword(value);
                              },
                              bloc.validPasswordController,
                              hintText: '******',
                              isBorder: true,
                              height: 45,
                              textColor: toHexToColor(primaryTextColor),
                              labelText: Text('Nhập lại mật khẩu'),
                            ),
                          ),
                          Padding(
                            padding: EdgeInsets.symmetric(vertical: 10),
                            child: customTextField(
                              errorText:
                                  bloc.errProvince == true
                                      ? bloc.errTextProvince
                                      : null,
                              borderColors: toHexToColor(borderColorGreen),
                              bloc.provinceController,
                              hintText: 'Ho Chi Minh',
                              isBorder: true,
                              height: 45,
                              textColor: toHexToColor(primaryTextColor),
                              labelText: Text('Tỉnh/Thành phố'),
                            ),
                          ),
                          Row(
                            children: [
                              Expanded(
                                child: Padding(
                                  padding: EdgeInsets.symmetric(vertical: 10),
                                  child: customTextField(
                                    errorText:
                                        bloc.errDistrict == true
                                            ? bloc.errTextDistrict
                                            : null,
                                    borderColors: toHexToColor(
                                      borderColorGreen,
                                    ),
                                    bloc.districtController,
                                    hintText: 'Quận/Huyện',
                                    isBorder: true,
                                    height: 45,
                                    textColor: toHexToColor(primaryTextColor),
                                    labelText: Text('Quận 8'),
                                  ),
                                ),
                              ),
                              SizedBox(
                                width: 10,
                              ), // Thêm một khoảng cách giữa các widget
                              Expanded(
                                child: Padding(
                                  padding: EdgeInsets.symmetric(vertical: 10),
                                  child: customTextField(
                                    errorText:
                                        bloc.errWard == true
                                            ? bloc.errTextWard
                                            : null,
                                    borderColors: toHexToColor(
                                      borderColorGreen,
                                    ),
                                    bloc.wardController,
                                    hintText: 'Phường/Xã',
                                    isBorder: true,
                                    height: 45,
                                    textColor: toHexToColor(primaryTextColor),
                                    labelText: Text('Phường 4'),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          Padding(
                            padding: EdgeInsets.symmetric(vertical: 10),
                            child: customTextField(
                              errorText:
                                  bloc.errStreet == true
                                      ? bloc.errTextStreet
                                      : null,
                              borderColors: toHexToColor(borderColorGreen),
                              bloc.streetController,
                              hintText: 'Số nhà/địa chỉ',
                              isBorder: true,
                              height: 45,
                              textColor: toHexToColor(primaryTextColor),
                              labelText: Text('123 Cao Lỗ'),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
                SizedBox(height: 10),
                Center(
                  child: Container(
                    height: 80,
                    width: AppSize.w(0.9),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.1),
                          spreadRadius: 2,
                          blurRadius: 4,
                          offset: Offset(2, 2), // đổ bóng theo trục Y
                        ),
                      ],
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: Align(
                      alignment: Alignment.center,
                      child: SizedBox(
                        width: AppSize.w(0.8), // Chiều rộng cố định
                        height: 45, // Chiều cao cố định
                        child: customSliableButton(
                          borderRadius: 10,
                          buttonColor: toHexToColor(primaryButtonColor),
                          thumbColor: Colors.white,
                          instructionText: 'Đăng ký ngay',
                          onSwipeComplete: () {
                            // bloc.onRegister();
                          },
                          width: AppSize.w(0.8),
                          height: 45,
                        ),
                      ),
                    ),
                  ),
                ),
                SizedBox(height: 20),
              ],
            ),
          ),
        );
      },
    );
  }

  @override
  RegisterBloc createBloc() => RegisterBloc();
}
