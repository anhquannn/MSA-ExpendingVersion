import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_item_product.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

import '../../../../../widget/build_tabbar_section.dart';
import '../bloc/order_bloc.dart';

class OrderScreen extends BaseView<OrderBloc> {
  bool? isManager = false;
  OrderScreen({super.key, this.isManager});

  @override
  OrderBloc createBloc() => OrderBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as OrderBloc;

    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(bloc.viewContext,size: 25),
      centerTitle: true,
      title: Column(
        mainAxisSize: MainAxisSize.min,
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Text(
            'Đơn hàng',
            style: TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 18,
            ),
          ),
          SizedBox(height: 2),
          Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(Icons.location_on_outlined, color: Colors.white, size: 20),
              SizedBox(width: 4),
              Text(
                '123 Cao Lỗ Quận 8',
                style: TextStyle(color: Colors.white, fontSize: 12),
              ),
            ],
          ),
        ],
      ),
      bodyBuilder: (controller) {
        return Container(
          width: AppSize.width(),
          color: toHexToColor(backgroundColor),
          child: buildTabSection(
            tabs: [
              Tab(text: 'Chờ xác nhận'),
              Tab(text: 'Đang giao hàng'),
              Tab(text: 'Giao thành công'),
              Tab(text: 'Giao thành công'),
            ],
            tabBarView: TabBarView(
              children: [
                _buildGridItems(),
                _buildGridItems(),
                _buildGridItems(),
                _buildGridItems(),
              ],
            ),
          ),
        );
      },
      hideBottomBarOnScroll: true,
    );
  }
}

Widget _buildGridItems() {
  return SizedBox(
    width: AppSize.w(0.95),
    child: SingleChildScrollView(
      child: Column(
        children: [
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
          customItemProduct(() {}, () {}, isOrder: true),
        ],
      ),
    ),
  );
}
