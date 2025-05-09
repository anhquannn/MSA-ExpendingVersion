import 'dart:math';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/custom_sliable_button.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

import '../../../../../widget/custom_item_product.dart';
import '../bloc/inventory_in_bloc.dart';

class InventoryInScreen extends BaseView<InventoryInBloc> {
  bool? isManager = false;
  InventoryInScreen({super.key, this.isManager});

  @override
  InventoryInBloc createState() => InventoryInBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as InventoryInBloc;
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
            isManager == true ? 'Yêu cầu nhập kho' : 'Nhập/Xuất kho',
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
                // Card(
                //   child: customButton(
                //     () {},
                //     AppSize.w(0.3),
                //     45,
                //     Text('Thêm mới', style: TextStyle(color: Colors.white)),
                //     typeButton: 1,
                //   ),
                // ),
                // SizedBox(height: 10),
                isManager == true
                    ? Container()
                    : Align(
                      alignment: Alignment.centerLeft,
                      child: Padding(
                        padding: EdgeInsets.only(left: AppSize.w(0.05)),
                        child: Text(
                          'Đến: ',
                          style: TextStyle(
                            color: Colors.black,
                            fontSize: 12,
                            fontStyle: FontStyle.italic,
                          ),
                        ),
                      ),
                    ),
                isManager == true
                    ? Container()
                    : Padding(
                      padding: const EdgeInsets.only(bottom: 20),
                      child: Card(
                        child: SizedBox(
                          width: AppSize.w(0.9),
                          child: customDropdownButton(
                            color: Colors.white60,
                            items: mockData,
                            hint: '',
                            onChanged: (value) {
                              _bloc.onSelect(value!);
                            },
                            selectedItem: _bloc.selectItem,
                          ),
                        ),
                      ),
                    ),
                isManager == true
                    ? Container()
                    : Padding(
                      padding: const EdgeInsets.only(bottom: 10),
                      child: Row(
                        children: [
                          Expanded(
                            child: Divider(
                              color: toHexToColor(primaryTextColor),
                              height: 1,
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 5),
                            child: Text('Danh sách sản phẩm'),
                          ),
                          Expanded(
                            child: Divider(
                              color: toHexToColor(primaryTextColor),
                              height: 1,
                            ),
                          ),
                        ],
                      ),
                    ),
                Dismissible(
                  key:
                      UniqueKey(), // Hoặc dùng key riêng theo id sản phẩm nếu có
                  direction: DismissDirection.endToStart,
                  background: Container(
                    alignment: Alignment.centerRight,
                    padding: EdgeInsets.symmetric(horizontal: 20),
                    color: Colors.red,
                    child: Icon(Icons.delete, color: Colors.white),
                  ),
                  onDismissed: (direction) {
                    // Gọi hàm xóa item ở đây
                    // ví dụ
                  },
                  child: customItemInventory(
                    () {}, // hàm click
                    () {}, // hàm edit
                    _bloc.controller,
                    'Cai',
                    () {}, // tùy thêm logic
                  ),
                ),

                customItemInventory(
                  isLow: true,
                  () {},
                  () {},
                  _bloc.controller,
                  'Cai',
                  () {},
                ),
                customItemInventory(
                  () {},
                  () {},
                  _bloc.controller,
                  'Cai',
                  () {},
                ),
                customItemInventory(
                  () {},
                  () {},
                  _bloc.controller,
                  'Cai',
                  () {},
                ),
                customItemInventory(
                  () {},
                  () {},
                  _bloc.controller,
                  'Cai',
                  () {},
                ),
                customItemInventory(
                  () {},
                  () {},
                  _bloc.controller,
                  'Cai',
                  () {},
                ),
                SizedBox(height: 10),

                SizedBox(
                  width: min(400, AppSize.w(0.7)),
                  height: 50,
                  child: customSliableButton(
                    instructionText: 'Yêu cầu nhập kho',
                    width: min(400, AppSize.w(0.7)),
                    height: 50,
                    onSwipeComplete: () {},
                  ),
                ),
                SizedBox(height: 20),
              ],
            ),
          ),
        );
      },
      hideBottomBarOnScroll: true,
      floatActionButton: Padding(
        padding: EdgeInsets.only(bottom: 80),
        child: InkWell(
          child: Container(
            width: 50,
            height: 50,
            decoration: BoxDecoration(
              color: toHexToColor(primaryButtonColor),
              shape: BoxShape.circle,
            ),
            child: Icon(Icons.add, color: Colors.white),
          ),
        ),
      ),
    );
  }

  @override
  InventoryInBloc createBloc() => InventoryInBloc();
}
