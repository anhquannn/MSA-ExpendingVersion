import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';

import '../../../../../core/config/base_bloc.dart';
import '../../../../../widget/custom_sliable_button.dart';
import '../../../../../widget/custom_textfield.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';
import '../bloc/register_bloc.dart';

class RegisterScreen extends BaseView<RegisterBloc> {
  const RegisterScreen({super.key});

  @override
  RegisterBloc createBloc() => RegisterBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as RegisterBloc;
    return CustomScaffold(
      isHide: false,
      appBarLeading: InkWell(
        onTap: () => context.go('/login'),
        child: Icon(Icons.arrow_back_ios_new, color: Colors.white),
      ),
      centerTitle: true,
      // appBarGradient: false,
      title: Text(
        'Đăng ký',
        style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
      ),
      bodyBuilder: (controller) => buildBody(bloc),
    );
  }

  Widget buildBody(RegisterBloc bloc) {
    return Column(
      children: [
        Expanded(
          child: RegisterBody(bloc: bloc)
        ),
      ],
    );
  }
}

class RegisterBody extends StatelessWidget {
  final RegisterBloc bloc;
  const RegisterBody({super.key, required this.bloc});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            RegisterForm(bloc: bloc),
            SizedBox(height: 10),
            RegisterButton(bloc: bloc),
            SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}

class RegisterForm extends StatelessWidget {
  final RegisterBloc bloc;
  const RegisterForm({super.key, required this.bloc});

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            spreadRadius: 2,
            blurRadius: 4,
            offset: Offset(2, 2),
          ),
        ],
        borderRadius: BorderRadius.circular(10),
      ),
      width: AppSize.w(0.9),
      child: Padding(
        padding: const EdgeInsets.all(15.0),
        child: Column(
          children: [
            _buildField(
              bloc.nameController,
              'Họ và tên',
              'Nguyen Van A',
              bloc.errName,
              bloc.errTextName,
            ),
            _buildField(
              bloc.emailController,
              'Email',
              'examp@gmail.com',
              bloc.errEmail,
              bloc.errTextEmail,
            ),
            _buildField(
              bloc.phoneNumberController,
              'Số điện thoại',
              '0123456789',
              bloc.errPhoneNumber,
              bloc.errTextPhoneNumber,
            ),
            _buildPasswordField(
              bloc.passwordController,
              'Mật khẩu',
              bloc.obscurePassword!,
              bloc.changObscurePassword,
              bloc.errPassword,
              bloc.errTextValidPassword,
            ),
            _buildPasswordField(
              bloc.validPasswordController,
              'Nhập lại mật khẩu',
              bloc.obscureValidPassword!,
              bloc.changValidObscurePassword,
              bloc.errValidPassword,
              bloc.errTextValidPassword,
            ),
            _buildField(
              bloc.provinceController,
              'Tỉnh/Thành phố',
              'Ho Chi Minh',
              bloc.errProvince,
              bloc.errTextProvince,
            ),
            Row(
              children: [
                Expanded(
                  child: _buildField(
                    bloc.districtController,
                    'Quận/Huyện',
                    'Quận 8',
                    bloc.errDistrict,
                    bloc.errTextDistrict,
                  ),
                ),
                SizedBox(width: 10),
                Expanded(
                  child: _buildField(
                    bloc.wardController,
                    'Phường/Xã',
                    'Phường 4',
                    bloc.errWard,
                    bloc.errTextWard,
                  ),
                ),
              ],
            ),
            _buildField(
              bloc.streetController,
              'Số nhà/địa chỉ',
              '123 Cao Lỗ',
              bloc.errStreet,
              bloc.errTextStreet,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildField(
    TextEditingController controller,
    String label,
    String hint,
    bool? hasError,
    String? errorText,
  ) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 10),
      child: customTextField(
        borderColors: toHexToColor(borderColorGreen),
        controller,
        hintText: hint,
        isBorder: true,
        height: 45,
        textColor: toHexToColor(primaryTextColor),
        labelText: Text(label),
        errorText: hasError == true ? errorText : null,
      ),
    );
  }

  Widget _buildPasswordField(
    TextEditingController controller,
    String label,
    bool isObscure,
    Function(bool) toggleObscure,
    bool? hasError,
    String? errorText,
  ) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 10),
      child: customTextField(
        borderColors: toHexToColor(borderColorGreen),
        isPassword: true,
        isObscure: isObscure,
        isObscurePassword: toggleObscure,
        controller,
        hintText: '******',
        isBorder: true,
        height: 45,
        textColor: toHexToColor(primaryTextColor),
        labelText: Text(label),
        errorText: hasError == true ? errorText : null,
      ),
    );
  }
}

class RegisterButton extends StatelessWidget {
  final RegisterBloc bloc;
  const RegisterButton({super.key, required this.bloc});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 80,
      width: AppSize.w(0.9),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            spreadRadius: 2,
            blurRadius: 4,
            offset: Offset(2, 2),
          ),
        ],
        borderRadius: BorderRadius.circular(10),
      ),
      child: Align(
        alignment: Alignment.center,
        child: SizedBox(
          width: AppSize.w(0.8),
          height: 45,
          child: customSliableButton(
            borderRadius: 10,
            buttonColor: toHexToColor(primaryButtonColor),
            thumbColor: Colors.white,
            instructionText: 'Đăng ký ngay',
            onSwipeComplete: () async{
              await bloc.onRegister();
            },
            width: AppSize.w(0.8),
            height: 45,
          ),
        ),
      ),
    );
  }
}
