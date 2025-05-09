import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_item_promocode.dart';
import '../bloc/category_list_bloc.dart';

class CategoryListScreen extends BaseView<CategoryListBloc> {
  const CategoryListScreen({super.key});

  @override
  CategoryListBloc createBloc() => CategoryListBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as CategoryListBloc;
    double width = AppSize.width();
    List<String> labels = [
      "La",
      "Lab",
      "Label 3",
      "Label 3",
      "Label 3",
      "Label 3",
      "Label 3",
      "Label 3",
      "Label 3",
    ];
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(size: 25),
      centerTitle: true,
      title: customAutoSizeText(
        16,
        20,
        'Danh sách loại sản phẩm',
        textColor: Colors.white,
      ),
      bodyBuilder: (controller) {
        final chipList =
            labels
                .map(
                  (label) => Chip(
                    label: Text(label),
                    padding: const EdgeInsets.symmetric(
                      horizontal: 6,
                      vertical: 6,
                    ),
                    backgroundColor: toHexToColor(primaryButtonColor),
                    labelStyle: const TextStyle(color: Colors.white),
                  ),
                )
                .toList();

        const chipWidth = 100.0;
        final maxChips = (width / chipWidth).floor();
        final dynamicChipList =
            chipList.length < maxChips
                ? [
                  ...chipList,
                  ...List.generate(
                    maxChips - chipList.length,
                    (i) => Chip(
                      label: Text('Chip ${chipList.length + i + 1}'),
                      padding: const EdgeInsets.symmetric(
                        horizontal: 6,
                        vertical: 6,
                      ),
                      backgroundColor: toHexToColor(primaryButtonColor),
                      labelStyle: const TextStyle(color: Colors.white),
                    ),
                  ),
                ]
                : chipList;
        return SizedBox(
          width: AppSize.width(),
          child: SingleChildScrollView(
            controller: controller,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                SizedBox(
                  width: AppSize.width(),
                  child: Padding(
                    padding: const EdgeInsets.all(0.0),
                    child: Wrap(children: dynamicChipList),
                  ),
                ),
              ],
            ),
          ),
        );
      },
      hideBottomBarOnScroll: true,
    );
  }
}

Widget widgetCustomItemPromoCode(VoidCallback onTap1, VoidCallback onTap2) {
  return Center(child: _customItemPromoCode(onTap1, onTap2));
}

Widget _customItemPromoCode(VoidCallback onTap1, VoidCallback onTap2) {
  return GestureDetector(
    onTap: onTap1,
    child: SizedBox(
      width: AppSize.w(0.9),
      // height: 300,
      // color: Colors.white,
      child: customItemPromoCode(() {}, () {}),
    ),
  );
}
