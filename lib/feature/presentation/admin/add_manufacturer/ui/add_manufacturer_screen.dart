import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_button.dart';
import 'package:msa/widget/custom_textfield.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

import '../bloc/add_manufacturer_bloc.dart';

class AddManufacturerScreen extends BaseView<AddManufacturerBloc> {
  const AddManufacturerScreen({super.key});

  @override
  AddManufacturerBloc createBloc() => AddManufacturerBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as AddManufacturerBloc;

    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(bloc.viewContext,size: 25),
      centerTitle: true,
      title: const AutoSizeText(
        'Thêm nhà sản xuất',
        style: TextStyle(color: Colors.white),
        minFontSize: 16,
        maxFontSize: 24,
      ),
      bodyBuilder: (controller) {
        return Container(
          width: AppSize.width(),
          color: toHexToColor(backgroundColor),
          child: CustomScrollView(
            controller: controller,
            slivers: [
              SliverFillRemaining(
                hasScrollBody: false,
                child: Padding(
                  padding: const EdgeInsets.symmetric(vertical: 20),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      buildTextFieldCard(
                        errText: bloc.nameError,
                        controller: bloc.nameController,
                        node: bloc.nameFocusNode,
                        hintText: 'Siêu thị GO',
                        label: 'Tên nhà sản xuất',
                        onSubmitted:
                            (val) => bloc.onFieldSubmitted(
                              context,
                              bloc.nameFocusNode,
                              bloc.contactFocusNode,
                            ),
                      ),

                      buildTextFieldCard(
                        errText: bloc.addressError,
                        controller: bloc.addressController,
                        node: bloc.addressFocusNode,
                        hintText: '123 Nguyễn Thị Thập Quận 7 TP.Hồ Chí Minh',
                        label: 'Địa chỉ',
                        onSubmitted: (val) => bloc.onCreatePromoCode(),
                        maxLines: 5,
                      ),
                      buildTextFieldCard(
                        errText: bloc.contactError,
                        controller: bloc.contactController,
                        node: bloc.contactFocusNode,
                        hintText: '0123 456 678',
                        label: 'Số điện thoại',
                        onSubmitted: (val) => bloc.onCreatePromoCode(),
                        maxLines: 5,
                      ),
                      SizedBox(height: 20),
                      Center(
                        child: Card(
                          elevation: 3,
                          child: customButton(
                            () {
                              bloc.validateForm();
                            },
                            typeButton: 1,
                            AppSize.w(0.5),
                            50,
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              crossAxisAlignment: CrossAxisAlignment.center,
                              children: [
                                Container(
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    borderRadius: BorderRadius.circular(50),
                                    border: Border.all(
                                      style: BorderStyle.solid,
                                      color: toHexToColor(primaryButtonColor),
                                    ),
                                  ),

                                  child: Icon(
                                    Icons.add,
                                    color: toHexToColor(primaryButtonColor),
                                  ),
                                ),
                                Text(
                                  'Tạo nhà sản xuất',
                                  style: TextStyle(color: Colors.white),
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
            ],
          ),
        );
      },
      hideBottomBarOnScroll: true,
    );
  }
}
