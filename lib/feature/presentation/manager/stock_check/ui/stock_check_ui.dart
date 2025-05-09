import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/widget/custom_textfield.dart';

import '../../../../../core/config/base_bloc.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_item_product.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';
import '../bloc/stock_check_bloc.dart';

class StockCheckScreen extends BaseView<StockCheckBloc> {
  const StockCheckScreen({super.key});

  @override
  StockCheckBloc createState() => StockCheckBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as StockCheckBloc;
    final List<String> mockData = [
      '123 Cho Lon',
      '123 Ho Chi Minh',
      '123 Vung Tau',
    ];
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(size: 25),
      centerTitle: true,
      title: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Text(
            'Kiểm kho',
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
              Icon(Icons.location_on_outlined, size: 20, color: Colors.white),
              SizedBox(width: 4),
              Flexible(
                child: Text(
                  'Cho Lon',
                  overflow: TextOverflow.ellipsis,
                  softWrap: true,
                  maxLines: 1,
                  style: TextStyle(color: Colors.white, fontSize: 14),
                ),
              ),
            ],
          ),
        ],
      ),
      bodyBuilder: (controller) {
        final double _width = min(500, AppSize.width());
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
                SizedBox(
                  width: _width * 0.9,
                  height: 45,
                  child: Card(
                    child: customTextField(
                      prefixIcon: Icon(Icons.search, color: Colors.black),
                      onSubmit: (p0) {},
                      _bloc.controller,
                      fillColor: toHexToColor(borderColor),
                    ),
                  ),
                ),
                SizedBox(height: 10),
                customItemInventory(
                  isCheck: true,
                  loss: '10000100001000010000',
                  () {}, // hàm click
                  () {}, // hàm edit
                  _bloc.controller,
                  'Cai',
                  () {}, // tùy thêm logic
                ),
                customItemInventory(
                  isCheck: true,
                  loss: '10000100001000010000',
                  () {}, // hàm click
                  () {}, // hàm edit
                  _bloc.controller,
                  'Cai',
                  () {}, // tùy thêm logic
                ),
                customItemInventory(
                  isCheck: true,
                  loss: '10000100001000010000',
                  () {}, // hàm click
                  () {}, // hàm edit
                  _bloc.controller,
                  'Cai',
                  () {}, // tùy thêm logic
                ),
                customItemInventory(
                  isCheck: true,
                  // loss: '10000100001000010000',
                  () {}, // hàm click
                  () {}, // hàm edit
                  _bloc.controller,
                  'Cai',
                  () {}, // tùy thêm logic
                ),
                customItemInventory(
                  isCheck: true,
                  // loss: '10000100001000010000',
                  () {}, // hàm click
                  () {}, // hàm edit
                  _bloc.controller,
                  'Cai',
                  () {}, // tùy thêm logic
                ),
                customItemInventory(
                  isCheck: true,
                  loss: '10000100001000010000',
                  () {}, // hàm click
                  () {}, // hàm edit
                  _bloc.controller,
                  'Cai',
                  () {}, // tùy thêm logic
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
  StockCheckBloc createBloc() => StockCheckBloc();
}
