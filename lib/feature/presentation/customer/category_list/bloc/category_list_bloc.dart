import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/presentation/customer/product_list/ui/product_list_screen.dart';

import '../ui/category_list_screen.dart';

class CategoryListBloc extends BaseBloc<CategoryListScreen> {
  @override
  void onInit() {}

  @override
  void onDispose() {}

  @override
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);
  onTapCategory(CategoryModel model, BuildContext bCOntext) {
    Navigator.push(
      bCOntext,
      MaterialPageRoute(
        builder:
            (bCOntext) => ProductListScreen(
              category: model,
              title: 'Danh sách ${model.name}',
              productList: null,
            ),
      ),
    );
  }
}
