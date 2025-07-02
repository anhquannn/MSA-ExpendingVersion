import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../bloc/category_list_bloc.dart';

class CategoryListScreen extends BaseView<CategoryListBloc> {
  final List<CategoryModel> categoryList;
  const CategoryListScreen({super.key, required this.categoryList});

  @override
  CategoryListBloc createBloc() => CategoryListBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as CategoryListBloc;
    double width = AppSize.width();
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(bloc.viewContext, size: 25),
      centerTitle: true,
      title: customAutoSizeText(
        16,
        20,
        'Danh sách loại sản phẩm',
        textColor: Colors.white,
      ),
      bodyBuilder: (controller) {
        final chipList =
            categoryList.map((label) {
              return InkWell(
                onTap: () {
                  bloc.onTapCategory(label, context);
                },
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 4),
                  child: Chip(
                    label: Text(label.name ?? ''),
                    padding: const EdgeInsets.symmetric(
                      horizontal: 6,
                      vertical: 6,
                    ),
                    backgroundColor: toHexToColor(primaryButtonColor),
                    labelStyle: const TextStyle(color: Colors.white),
                  ),
                ),
              );
            }).toList();

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
