import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/selec_branch_screen.dart';
import 'package:msa/feature/presentation/customer/persional/bloc/persional_bloc.dart';
import 'package:msa/widget/custom_textfield.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

class PersionalScreen extends BaseView<PersionalBloc> {
  const PersionalScreen({super.key});

  @override
  PersionalBloc createState() => PersionalBloc();

  @override
  PersionalBloc createBloc() => PersionalBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as PersionalBloc;
    return CustomScaffold(
      appBarLeading: InkWell(
        onTap: () {
          Navigator.pop(context);
        },
        child: Icon(Icons.arrow_back_ios, color: Colors.white, size: 24),
      ),
      centerTitle: true,
      title: Text(
        'Thông tin cá nhân',
        style: TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.bold,
          fontSize: 18,
        ),
      ),
      bodyBuilder: (controller) {
        return CustomScrollView(
          slivers: [SliverToBoxAdapter(child: _buildBody(context, bloc))],
        );
      },
    );
  }

  Widget _buildBody(BuildContext context, PersionalBloc bloc) {
    return MediaQuery.removePadding(
      context: context,
      child: Container(
        width: MediaQuery.sizeOf(context).width,
        padding: EdgeInsets.symmetric(horizontal: 12),
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
              type: TextInputType.number,
            ),

            InkWell(
              onTap: () {
                bloc.showPicker(context);
              },
              child: _buildField(
                enable: false,
                bloc.birthDayController,
                'Ngày sinh',
                '2023-06-08 15:30:00',
                bloc.errorBirthDay,
                bloc.errTextBirthDay,
              ),
            ),
            InkWell(
              onTap: () {
                bloc.showCitySelector(context);
              },
              child: _buildField(
                enable: false,
                bloc.provinceController,
                'Tỉnh/Thành phố',
                'Ho Chi Minh',
                bloc.errProvince,
                bloc.errTextProvince,
              ),
            ),
            Row(
              children: [
                Expanded(
                  child: InkWell(
                    onTap: () {
                      bloc.showDistrictSelector(context);
                    },
                    child: _buildField(
                      enable: false,
                      bloc.districtController,
                      'Quận/Huyện',
                      'Quận 8',
                      bloc.errDistrict,
                      bloc.errTextDistrict,
                    ),
                  ),
                ),
                SizedBox(width: 10),
                Expanded(
                  child: InkWell(
                    onTap: () {
                      bloc.showWardSelector(context);
                    },
                    child: _buildField(
                      enable: false,
                      bloc.wardController,
                      'Phường/Xã',
                      'Phường 4',
                      bloc.errWard,
                      bloc.errTextWard,
                    ),
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
            SizedBox(height: 5),
            Divider(),
            SizedBox(height: 5),
            InkWell(
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => SelectBranchScreen()),
                );
              },
              child: _buildField(
                enable: false,
                bloc.branchController,
                'Chọn chi nhánh',
                '',
                bloc.errorBranch,
                bloc.errTextBranch,
              ),
            ),
            _buildPasswordField(
              bloc,
              bloc.passwordController,
              '******',
              bloc.obscurePassword!,
              bloc.changObscurePassword,
              bloc.errPassword,
              bloc.errTextValidPassword,
            ),
            SizedBox(height: 5),
            Divider(),
            SizedBox(height: 5),
            Row(
              children: [
                Expanded(
                  child: _buildButton(
                    buttonColor: toHexToColor(primaryButtonColor),
                    isBorderType: true,
                    onTap: () {
                      bloc.onLogout();
                    },
                    text: 'Đăng xuất',
                  ),
                ),
                SizedBox(width: 10),
                Expanded(
                  child: _buildButton(
                    buttonColor: toHexToColor(primaryButtonColor),
                    isBorderType: false,
                    onTap: bloc.onUpdate,
                    text: 'Chỉnh sửa',
                  ),
                ),
              ],
            ),
            SizedBox(width: 10),
          ],
        ),
      ),
    );
  }

  Widget _buildButton({
    String? text,
    bool isBorderType = false,
    VoidCallback? onTap,
    Color? buttonColor,
  }) {
    return InkWell(
      child: Container(
        padding: EdgeInsets.symmetric(horizontal: 10, vertical: 10),
        decoration:
            isBorderType == true
                ? BoxDecoration(
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: buttonColor!, width: 1),
                )
                : BoxDecoration(
                  color: buttonColor,

                  borderRadius: BorderRadius.circular(8),
                ),
        child: Center(
          child: Text(
            text!,
            style: TextStyle(
              color: isBorderType == true ? buttonColor : Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildField(
    TextEditingController controller,
    String label,
    String hint,
    bool? hasError,
    String? errorText, {
    TextInputType? type,
    bool? enable = true,
  }) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 10),
      child: customTextField(
        typeInput: type,
        enable: enable,
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
    PersionalBloc? bloc,
    TextEditingController controller,
    String label,
    bool isObscure,
    Function(bool) toggleObscure,
    bool? hasError,
    String? errorText,
  ) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 10),
      child: InkWell(
        onTap: () {
          bloc?.onChangePassword();
        },
        child: Container(
          width: MediaQuery.sizeOf(context).width,
          height: 45,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(10),
            border: Border.all(color: toHexToColor(borderColorGreen)),
          ),
          child: Row(
            children: [
              Text('Đổi mật khẩu...'),
              Spacer(),
              Icon(Icons.arrow_forward_ios),
            ],
          ),
        ),
      ),
    );
  }
}
