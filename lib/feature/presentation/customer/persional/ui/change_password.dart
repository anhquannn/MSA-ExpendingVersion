import 'package:flutter/material.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/change_password_request_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/customer/persional/bloc/persional_bloc.dart';
import 'package:msa/widget/custom_textfield.dart';

class ChangePasswordDialog extends StatefulWidget {
  const ChangePasswordDialog({super.key});

  @override
  State<ChangePasswordDialog> createState() => _ChangePasswordDialogState();
}

class _ChangePasswordDialogState extends State<ChangePasswordDialog> {
  final _oldPasswordController = TextEditingController();
  final _newPasswordController = TextEditingController();

  bool _oldPasswordError = false;
  bool _newPasswordError = false;

  void _submit(BuildContext context) async {
    setState(() {
      _oldPasswordError = _oldPasswordController.text.isEmpty;
      _newPasswordError = _newPasswordController.text.length < 6;
    });

    if (!_oldPasswordError && !_newPasswordError) {
      final response = await Repository.onChangePassword(
        UpdatePasswordRequest(
          oldPassword: _oldPasswordController.text,
          newPassword: _newPasswordController.text,
        ),
        Storage.userModelGlobal?.userId ?? 0,
      );
      Navigator.pop(context, response);
    }
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
      padding: const EdgeInsets.symmetric(vertical: 10),
      child: customTextField(
        controller,
        typeInput: type,
        enable: enable,
        borderColors: toHexToColor(borderColorGreen),
        hintText: hint,
        isBorder: true,
        height: 45,
        textColor: toHexToColor(primaryTextColor),
        labelText: Text(label),
        errorText: hasError == true ? errorText : null,
        isObscure: true,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final double screenWidth = MediaQuery.of(context).size.width;

    return Dialog(
      insetPadding: EdgeInsets.symmetric(horizontal: 10),
      backgroundColor: Colors.white,
      child: SizedBox(
        width: screenWidth,
        height: 350,
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                'Đổi mật khẩu',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),
              Expanded(
                child: SingleChildScrollView(
                  child: Column(
                    children: [
                      _buildField(
                        _oldPasswordController,
                        'Mật khẩu cũ',
                        'Nhập mật khẩu hiện tại',
                        _oldPasswordError,
                        'Vui lòng nhập mật khẩu cũ',
                      ),
                      _buildField(
                        _newPasswordController,
                        'Mật khẩu mới',
                        'Nhập mật khẩu mới',
                        _newPasswordError,
                        'Mật khẩu ít nhất 6 ký tự',
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
              Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: 10,
                  vertical: 15,
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    Expanded(
                      child: _buildButton(
                        buttonColor: toHexToColor(primaryButtonColor),
                        isBorderType: true,
                        onTap: () => Navigator.of(context).pop(),
                        text: 'Hủy',
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _buildButton(
                        buttonColor: toHexToColor(primaryButtonColor),
                        isBorderType: false,
                        onTap: () => _submit(context),
                        text: 'Xác nhận',
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
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
      onTap: () => onTap!(),
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
}
