import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

import '../../../../../widget/custom_item_product.dart';
import '../bloc/list_branch_bloc.dart';

class ListBranchScreen extends BaseView<ListBranchBloc> {
  const ListBranchScreen({super.key});

  @override
  ListBranchBloc createState() => ListBranchBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as ListBranchBloc;
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(bloc.viewContext,size: 25),
      centerTitle: true,
      title: AutoSizeText(
        'Danh sách chi nhánh',
        maxFontSize: 24,
        minFontSize: 16,
        style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
      ),
      bodyBuilder: (controller) {
        return Container(
          width: AppSize.width(),
          color: toHexToColor(backgroundColor),
          child: SingleChildScrollView(
            controller: controller,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                SizedBox(height: 10),
                customItemBranch(
                  phoneNumber: '0123 456 789',
                  name: 'Nguyen Van A',
                  img: imgBranch,
                  location: 'Ho Chi Minh',
                  sale: '3.000.000',
                  onDelete: () {},
                  onUpdate: () {},
                ),
                customItemBranch(
                  phoneNumber: '0123 456 789',
                  name: 'Nguyen Van A',
                  img: imgBranch,
                  location: 'Ho Chi Minh',
                  sale: '3.000.000',
                  onDelete: () {},
                  onUpdate: () {},
                ),
                customItemBranch(
                  phoneNumber: '0123 456 789',
                  name: 'Nguyen Van A',
                  img: imgBranch,
                  location: 'Ho Chi Minh',
                  sale: '3.000.000',
                  onDelete: () {},
                  onUpdate: () {},
                ),
                customItemBranch(
                  phoneNumber: '0123 456 789',
                  name: 'Nguyen Van A',
                  img: imgBranch,
                  location: 'Ho Chi Minh',
                  sale: '3.000.000',
                  onDelete: () {},
                  onUpdate: () {},
                ),
                customItemBranch(
                  phoneNumber:
                      '0123 456 7890123 456 7890123 456 7890123 456 7890123 456 7890123 456 789',
                  name:
                      'Nguyen Van ANguyen Van ANguyen Van ANguyen Van ANguyen Van ANguyen Van ANguyen Van A',
                  img: imgBranch,
                  location: 'Ho Chi MinhHo Chi MinhHo Chi MinhHo Chi Minh',
                  sale: '3.000.000',
                  onDelete: () {},
                  onUpdate: () {},
                ),
                SizedBox(height: 20),
              ],
            ),
          ),
        );
      },
      hideBottomBarOnScroll: true,
    );
  }

  @override
  ListBranchBloc createBloc() => ListBranchBloc();
}
