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
    final bloc = (context as StatefulElement).state as AddPromoCodeBloc;

    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(bloc.viewContext, size: 25),
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
                        errText: bloc.nameError,
                        controller: bloc.nameController,
                        node: bloc.nameFocusNode,
                        hintText: 'Tên sản phẩm khuyến mãi',
                        label: 'Tên sản phẩm',
                        onSubmitted:
                            (val) => bloc.onFieldSubmitted(
                              context,
                              bloc.nameFocusNode,
                              bloc.codeFocusNode,
                            ),
                      ),
                      buildTextFieldCard(
                        errText: bloc.codeError,
                        controller: bloc.codeController,
                        node: bloc.codeFocusNode,
                        hintText: 'WELCOME10',
                        label: 'Mã giảm giá',
                        onSubmitted:
                            (val) => bloc.onFieldSubmitted(
                              context,
                              bloc.codeFocusNode,
                              bloc.descFocusNode,
                            ),
                      ),
                      buildTextFieldCard(
                        errText: bloc.descError,
                        controller: bloc.descController,
                        node: bloc.descFocusNode,
                        hintText:
                            'Giảm 10% cho đơn hàng đầu tiên, áp dụng cho tất cả sản phẩm...',
                        label: 'Mô tả mã giảm giá',
                        onSubmitted: (val) => bloc.onCreatePromoCode(),
                        maxLines: 5,
                      ),
                      Padding(
                        padding: const EdgeInsets.symmetric(
                          vertical: 5,
                          horizontal: 20,
                        ),
                        child: datePickerField(
                          errorText: bloc.startDateError,
                          controller: bloc.startDateController,
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
                          errorText: bloc.endDateError,
                          controller: bloc.endDateController,
                          context: context,
                          label: Text('Chọn ngày kết thúc'),
                        ),
                      ),
                      SizedBox(height: 20),
                      ValueListenableBuilder<bool>(
                        valueListenable: bloc.isShowMockup,
                        builder: (context, show, _) {
                          return show
                              ? SizedBox(
                                width: 300,
                                // width: AppSize.w(0.98),
                                height: AppSize.h(0.2),
                                child: customItemPromoCode(
                                  code: bloc.codeController.text,
                                  content: bloc.descController.text,
                                  date:
                                      '${bloc.startDateController.text} - ${bloc.endDateController.text}',
                                  title: bloc.nameController.text,
                                ),
                              )
                              : SizedBox.shrink();
                        },
                      ),
                      SizedBox(height: 20),
                      Center(
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
