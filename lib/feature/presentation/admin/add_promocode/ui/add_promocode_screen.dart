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

import '../../../../../widget/custom_dialog.dart';
import '../../../../../widget/widget_promo_code.dart';
import '../bloc/add_promocode_bloc.dart';

class AddPromoCodeScreen extends BaseView<AddPromoCodeBloc> {
  const AddPromoCodeScreen({super.key});
  @override
  AddPromoCodeBloc createBloc() => AddPromoCodeBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as AddPromoCodeBloc;

    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(size: 25),
      centerTitle: true,
      title: const AutoSizeText(
        'Thêm mã giảm giá',
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
                        errText: _bloc.nameError,
                        controller: _bloc.nameController,
                        node: _bloc.nameFocusNode,
                        hintText: 'Tên sản phẩm khuyến mãi',
                        label: 'Tên sản phẩm',
                        onSubmitted:
                            (val) => _bloc.onFieldSubmitted(
                              context,
                              _bloc.nameFocusNode,
                              _bloc.codeFocusNode,
                            ),
                      ),
                      buildTextFieldCard(
                        errText: _bloc.codeError,
                        controller: _bloc.codeController,
                        node: _bloc.codeFocusNode,
                        hintText: 'WELCOME10',
                        label: 'Mã giảm giá',
                        onSubmitted:
                            (val) => _bloc.onFieldSubmitted(
                              context,
                              _bloc.codeFocusNode,
                              _bloc.descFocusNode,
                            ),
                      ),
                      buildTextFieldCard(
                        errText: _bloc.descError,
                        controller: _bloc.descController,
                        node: _bloc.descFocusNode,
                        hintText:
                            'Giảm 10% cho đơn hàng đầu tiên, áp dụng cho tất cả sản phẩm...',
                        label: 'Mô tả mã giảm giá',
                        onSubmitted: (val) => _bloc.onCreatePromoCode(),
                        maxLines: 5,
                      ),
                      Padding(
                        padding: const EdgeInsets.symmetric(
                          vertical: 5,
                          horizontal: 20,
                        ),
                        child: datePickerField(
                          errorText: _bloc.startDateError,
                          controller: _bloc.startDateController,
                          context: context,
                          label: Text('Chọn ngày bắt đầu'),
                        ),
                      ),
                      Padding(
                        padding: const EdgeInsets.symmetric(
                          vertical: 5,
                          horizontal: 20,
                        ),
                        child: datePickerField(
                          errorText: _bloc.endDateError,
                          controller: _bloc.endDateController,
                          context: context,
                          label: Text('Chọn ngày kết thúc'),
                        ),
                      ),
                      SizedBox(height: 20),
                      ValueListenableBuilder<bool>(
                        valueListenable: _bloc.isShowMockup,
                        builder: (context, show, _) {
                          return show
                              ? SizedBox(
                                width: 300,
                                // width: AppSize.w(0.98),
                                height: AppSize.h(0.2),
                                child: customItemPromoCode(
                                  code: _bloc.codeController.text,
                                  content: _bloc.descController.text,
                                  date:
                                      '${_bloc.startDateController.text} - ${_bloc.endDateController.text}',
                                  title: _bloc.nameController.text,
                                ),
                              )
                              : SizedBox.shrink();
                        },
                      ),
                      SizedBox(height: 20),
                      Center(
                        child: customButton(
                          () {
                            _bloc.validateForm();
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
                                'Tạo mã giảm giá',
                                style: TextStyle(color: Colors.white),
                              ),
                            ],
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
