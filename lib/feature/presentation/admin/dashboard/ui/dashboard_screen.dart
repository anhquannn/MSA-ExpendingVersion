import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

import '../../../../../widget/custom_dropshadow.dart';
import '../../../../../widget/icon_wrap.dart';
import '../bloc/dashboard_bloc.dart';

class DashBoardScreen extends BaseView<DashBoardBloc> {
  bool? isManager = false;
  DashBoardScreen({super.key, this.isManager});

  @override
  DashBoardBloc createState() => DashBoardBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as DashBoardBloc;
    return CustomScaffold(
      appBarGradient: false,

      appBarLeading: Container(
        width: 45,
        height: 45,
        decoration: BoxDecoration(
          color: toHexToColor(backgroundColor),
          borderRadius: BorderRadius.circular(30),
        ),
        child: Center(
          child: ClipRRect(
            borderRadius: BorderRadius.circular(22.5), // Bo tròn hình ảnh
            child: Image.asset(
              isManager == true ? avtManager : avtAdmin,
              fit: BoxFit.cover,
              width: 40,
              height: 40,
            ),
          ),
        ),
      ),

      title: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          AutoSizeText(
            'Nguyen Van A',
            minFontSize: 12,
            maxFontSize: 20,
            style: TextStyle(color: Colors.white),
          ),
          AutoSizeText(
            isManager == true ? 'Manager' : 'Admin',
            minFontSize: 10,
            maxFontSize: 14,
            style: TextStyle(color: Colors.white),
          ),
        ],
      ),
      appBarActions: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 5),
          child: InkWell(
            onTap: () {
              _bloc.onSetting();
            },
            child: Icon(Icons.settings, color: Colors.white),
          ),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 5),
          child: InkWell(
            onTap: () {
              _bloc.onLogout();
            },
            child: Icon(Icons.logout, color: Colors.white),
          ),
        ),
        SizedBox(width: 10),
      ],
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
                dropShadowContainer(
                  direction: ShadowDirection.all,
                  borderRadius: BorderRadius.circular(10),
                  child: Padding(
                    padding: const EdgeInsets.all(15.0),
                    child: buildIconWrapWithLabel(
                      icons: [
                        itemIconFunction(Icons.inventory, () {}),
                        itemIconFunction(Icons.add_box, () {}),
                        itemIconFunction(Icons.receipt_long, () {}),
                        itemIconFunction(Icons.playlist_add, () {}),
                        itemIconFunction(Icons.business, () {}),
                        itemIconFunction(Icons.add_business, () {}),
                        itemIconFunction(Icons.local_offer, () {}),
                        itemIconFunction(Icons.category, () {}),
                        itemIconFunction(Icons.inventory_2, () {}),
                        itemIconFunction(Icons.request_page, () {}),
                      ],
                      labels: [
                        'Sản phẩm',
                        'Sản phẩm',
                        'Đơn hàng',
                        'Đơn hàng',
                        'Chi nhánh',
                        'Chi nhánh',
                        'Mã giảm giá',
                        'Loại sản phẩm',
                        'Nhập kho',
                        'Nhập kho',
                      ],
                    ),
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

  @override
  DashBoardBloc createBloc() => DashBoardBloc();
}
