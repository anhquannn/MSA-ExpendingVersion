import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';

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
        onTap: () {
          Navigator.pop(context);
        },
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
    return Column(children: [Expanded(child: RegisterBody(bloc: bloc))]);
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
              type: TextInputType.number,
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
            onSwipeComplete: () async {
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

class SelectorDialog<T extends Nameable> extends StatefulWidget {
  final List<T> items;
  final String title;
  final void Function(T) onConfirm;

  const SelectorDialog({
    super.key,
    required this.items,
    required this.title,
    required this.onConfirm,
  });

  @override
  State<SelectorDialog<T>> createState() => _SelectorDialogState<T>();
}

class _SelectorDialogState<T extends Nameable>
    extends State<SelectorDialog<T>> {
  T? selectedItem;

  String searchQuery = '';

  List<dynamic> get filteredItems {
    if (searchQuery.isEmpty) return widget.items;
    return widget.items
        .where(
          (item) => item.name.toLowerCase().contains(searchQuery.toLowerCase()),
        )
        .toList();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      backgroundColor: Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      title: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Center(
            child: Text(
              widget.title,
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
          ),
          const SizedBox(height: 8),
          TextField(
            decoration: InputDecoration(
              prefixIcon: const Icon(Icons.search),
              hintText: 'Tìm kiếm...',
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              contentPadding: const EdgeInsets.symmetric(
                vertical: 0,
                horizontal: 8,
              ),
            ),
            onChanged: (value) {
              setState(() {
                searchQuery = value;
              });
            },
          ),
        ],
      ),
      content: SizedBox(
        width: MediaQuery.sizeOf(context).width * 0.9,
        height: 300,
        child: Scrollbar(
          thumbVisibility: true,
          child: ListView.separated(
            itemCount: filteredItems.length,
            separatorBuilder: (_, __) => const Divider(height: 0),
            itemBuilder: (context, index) {
              final item = filteredItems[index];
              final isSelected = item == selectedItem;

              return InkWell(
                borderRadius: BorderRadius.circular(8),
                onTap: () {
                  setState(() {
                    selectedItem = item;
                  });
                },
                child: Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 14,
                  ),
                  decoration: BoxDecoration(
                    color:
                        isSelected
                            ? toHexToColor(appBarColor).withOpacity(0.1)
                            : null,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Row(
                    children: [
                      Expanded(
                        child: Text(
                          item.name,
                          style: TextStyle(
                            fontWeight:
                                isSelected
                                    ? FontWeight.w600
                                    : FontWeight.normal,
                            color:
                                isSelected
                                    ? Theme.of(context).primaryColor
                                    : null,
                          ),
                        ),
                      ),
                      if (isSelected)
                        Icon(
                          Icons.check_circle,
                          color: toHexToColor(appBarColor),
                        ),
                    ],
                  ),
                ),
              );
            },
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed:
              selectedItem != null
                  ? () => widget.onConfirm(selectedItem!)
                  : null,
          style: TextButton.styleFrom(
            backgroundColor:
                selectedItem != null
                    ? toHexToColor(appBarColor)
                    : Colors.grey[300],
            foregroundColor: Colors.white,
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8),
            ),
          ),
          child: Text(
            'Xác nhận',
            style: TextStyle(
              color:
                  selectedItem == null
                      ? toHexToColor(appBarColor)
                      : Colors.white,
            ),
          ),
        ),
      ],
    );
  }
}
