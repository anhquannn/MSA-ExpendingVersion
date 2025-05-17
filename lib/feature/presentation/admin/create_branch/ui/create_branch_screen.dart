import 'dart:math';

import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';

import '../../../../../core/config/base_bloc.dart';
import '../../../../../widget/custom_dropshadow.dart';
import '../../../../../widget/custom_textfield.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';
import '../bloc/create_branch_bloc.dart';

class CreateBranchScreen extends BaseView<CreateBranchBloc> {
  const CreateBranchScreen({super.key});

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as CreateBranchBloc;
    return CustomScaffold(
      appBarGradient: false,
      centerTitle: true,
      title: Text(
        'Thêm chi nhánh mới',
        style: TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.bold,
          fontSize: 18,
        ),
      ),
      appBarLeading: iconBack(bloc.viewContext,size: 20),
      bodyBuilder: (controller) {
        return SingleChildScrollView(
          controller: controller,
          child: Column(
            crossAxisAlignment:
                CrossAxisAlignment.center, // Căn giữa theo chiều ngang
            mainAxisAlignment:
                MainAxisAlignment.start, // Căn giữa theo chiều dọc
            children: [
              Center(
                child: dropShadowContainer(
                  borderRadius: BorderRadius.circular(10),
                  direction: ShadowDirection.all,
                  // height: AppSize.h(0.7),
                  width: AppSize.w(0.95),
                  // color: toHexToColor(backgroundColor),
                  child: Padding(
                    padding: const EdgeInsets.all(15.0),
                    child: Column(
                      children: [
                        _buildField(
                          label: 'Tên chi nhánh',
                          hintText: 'Chi nhánh Nguyễn Thị Thập',
                          controller: bloc.nameController,
                          hasError: bloc.errName,
                          errorText: bloc.errTextName,
                        ),
                        _buildField(
                          label: 'Email',
                          hintText: 'examp@gmail.com',
                          controller: bloc.emailController,
                          hasError: bloc.errEmail,
                          errorText: bloc.errTextEmail,
                        ),
                        _buildField(
                          label: 'Số điện thoại',
                          hintText: '0123456789',
                          controller: bloc.phoneNumberController,
                          hasError: bloc.errPhoneNumber,
                          errorText: bloc.errTextPhoneNumber,
                        ),
                        _buildField(
                          label: 'Tỉnh/Thành phố',
                          hintText: 'Ho Chi Minh',
                          controller: bloc.provinceController,
                          hasError: bloc.errProvince,
                          errorText: bloc.errTextProvince,
                        ),
                        _buildField(
                          label: 'Quận/Huyện',
                          hintText: 'Quận 7',
                          controller: bloc.districtController,
                          hasError: bloc.errDistrict,
                          errorText: bloc.errTextDistrict,
                        ),
                        _buildField(
                          label: 'Phường/Xã',
                          hintText: 'Phường 4',
                          controller: bloc.wardController,
                          hasError: bloc.errWard,
                          errorText: bloc.errTextWard,
                        ),
                        _buildField(
                          label: 'Địa chỉ/Tên đường',
                          hintText: '123 Nguyễn Thị Thập',
                          controller: bloc.addressController,
                          hasError: bloc.errAddress,
                          errorText: bloc.errTextProvince,
                        ),
                      ],
                    ),
                  ),
                ),
              ),
              SizedBox(height: 10),
              Card(
                color: Colors.white,
                child: Container(
                  width: AppSize.w(0.95),
                  height: 80,
                  decoration: BoxDecoration(
                    color: Colors.white,
                    // color: toHexToColor(primaryButtonColor),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Center(
                    child: InkWell(
                      child: Container(
                        padding: EdgeInsets.symmetric(
                          vertical: 10,
                          horizontal: 10,
                        ),
                        width: min(250, AppSize.w(0.5)),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(10),
                          color: toHexToColor(primaryButtonColor),
                        ),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Container(
                              decoration: BoxDecoration(
                                color: Colors.white,
                                border: Border.all(
                                  color: toHexToColor(primaryButtonColor),
                                ),
                                borderRadius: BorderRadius.circular(100),
                              ),
                              width: 25,
                              height: 25,
                              child: Icon(
                                Icons.add,
                                color: toHexToColor(primaryButtonColor),
                              ),
                            ),
                            SizedBox(width: 10),
                            Text(
                              'Thêm chi nhánh',
                              style: TextStyle(color: Colors.white),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
              ),
              SizedBox(height: 20),
            ],
          ),
        );
      },
    );
  }

  @override
  CreateBranchBloc createBloc() => CreateBranchBloc();

  Widget _buildField({
    required String label,
    required String hintText,
    required TextEditingController controller,
    required bool hasError,
    required String errorText,
  }) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 10),
      child: Card(
        child: customTextField(
          errorText: hasError ? errorText : null,
          borderColors: Colors.white,
          controller,
          fillColor: Colors.white54,
          hintText: hintText,
          // isBorder: true,
          height: 45,
          textColor: toHexToColor(primaryTextColor),
          labelText: Text(label),
        ),
      ),
    );
  }
}
