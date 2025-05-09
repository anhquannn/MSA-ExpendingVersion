import 'dart:math';

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_button.dart';
import 'package:msa/widget/custom_textfield.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../../../../../../widget/custom_dialog.dart';
import '../../../../../../widget/custom_dropshadow.dart';
import '../bloc/product_detail_bloc.dart';

class ProductDetailScreen extends BaseView<ProductDetailBloc> {
  const ProductDetailScreen({super.key});

  @override
  ProductDetailBloc createState() => ProductDetailBloc();

  @override
  ProductDetailBloc createBloc() => ProductDetailBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as ProductDetailBloc;
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(size: 25),
      centerTitle: true,
      title: Text(
        'Thêm sản phẩm',
        style: TextStyle(
          fontWeight: FontWeight.bold,
          color: Colors.white,
          fontSize: 20,
        ),
      ),
      bodyBuilder: (controller) {
        final double width = min(500, AppSize.width());
        return Container(
          width: AppSize.width(),
          color: toHexToColor(backgroundColor),
          child: SingleChildScrollView(
            controller: controller,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SizedBox(height: 10),
                Center(
                  child: Card(
                    color: Colors.white70,
                    // borderRadius: BorderRadius.circular(10),
                    // width: width * 0.95,
                    child: Container(
                      width: width * 0.95,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Column(
                        children: [
                          SizedBox(height: 10),
                          buildTextFieldCard(
                            prefixIcon: Icon(Icons.inventory_2_outlined),
                            hintText: 'Tên sản phẩm',
                            label: 'Tên sản phẩm',
                            controller: _bloc.nameController,
                            node: _bloc.nameFocus,
                            onSubmitted: (p0) {},
                            errText: _bloc.nameError,
                          ),

                          SizedBox(
                            width: width * 0.9,
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                // Widget con thứ nhất
                                Expanded(
                                  flex: 5, // Tỉ lệ 5 phần cho widget này
                                  child: buildTextFieldCard(
                                    prefixIcon: Icon(Icons.money_outlined),
                                    hPadding: 5,
                                    hintText: '120.000đ',
                                    label: 'Giá sản phẩm',
                                    controller: _bloc.priceController,
                                    node: _bloc.priceFocus,
                                    onSubmitted: (p0) {},
                                    errText: _bloc.priceError,
                                  ),
                                ),
                                // Widget con thứ hai
                                Expanded(
                                  flex: 5, // Tỉ lệ 5 phần cho widget này
                                  child: buildTextFieldCard(
                                    prefixIcon: Icon(Icons.price_check),
                                    hPadding: 5,
                                    hintText: '100.000đ',
                                    label: 'Giá hiện tại',
                                    controller: _bloc.currentPriceController,
                                    node: _bloc.currentPriceFocus,
                                    onSubmitted: (p0) {},
                                    errText: _bloc.currentPriceError,
                                  ),
                                ),
                              ],
                            ),
                          ),

                          buildTextFieldCard(
                            prefixIcon: Icon(Icons.build_circle_outlined),
                            hintText: 'Thông số sản phẩm',
                            label: 'Thông số',
                            controller: _bloc.specificationController,
                            node: _bloc.specificationFocus,
                            onSubmitted: (p0) {},
                            maxLines: 10,
                            errText: _bloc.specificationError,
                          ),
                          buildTextFieldCard(
                            prefixIcon: Icon(Icons.description),
                            hintText: 'Mô tả sản phẩm',
                            label: 'Mô tả',
                            controller: _bloc.descController,
                            node: _bloc.descFocus,
                            onSubmitted: (p0) {},
                            maxLines: 10,
                            errText: _bloc.descError,
                          ),
                          Padding(
                            padding: const EdgeInsets.symmetric(
                              vertical: 10,
                              horizontal: 20,
                            ),
                            child: Card(
                              color: Colors.white,
                              child: datePickerField(
                                isPrefix: true,
                                context: context,
                                initialDate: DateFormat(
                                  'dd/MM/yyyy',
                                ).format(DateTime.now()),
                                label: Text(
                                  'Hạn sử dụng',
                                  style: TextStyle(fontSize: 14),
                                ),
                                controller: _bloc.expiryController,
                                errorText: _bloc.expiryError, // Nếu có lỗi
                              ),
                            ),
                          ),
                          SizedBox(
                            width: width * 0.9,
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                // Widget con thứ nhất
                                Expanded(
                                  flex: 8,
                                  child: buildTextFieldCard(
                                    hPadding: 5,
                                    prefixIcon: Icon(Icons.straighten),
                                    hintText: 'Kg',
                                    label: 'Đơn vị tính',
                                    controller: _bloc.unitController,
                                    node: _bloc.unitFocus,
                                    onSubmitted: (p0) {},
                                    errText: _bloc.unitError,
                                  ),
                                ),
                                Expanded(
                                  flex: 2,
                                  child: SizedBox(
                                    width: width * 0.28,
                                    height: 60,
                                    child: Card(
                                      color: toHexToColor(_bloc.color),
                                      child: InkWell(
                                        onTap: () async {
                                          await _bloc.selectColor();
                                        },
                                        child: Container(
                                          width: 20,
                                          height: 20,
                                          decoration: BoxDecoration(
                                            color: toHexToColor(_bloc.color),
                                            shape: BoxShape.circle,
                                          ),
                                          child: Icon(
                                            Icons.color_lens,
                                            color: _bloc.getOppositeColor(
                                              _bloc.color,
                                            ),
                                          ),
                                        ),
                                      ),
                                    ),
                                  ),
                                ),
                                SizedBox(height: 10),
                              ],
                            ),
                          ),
                          SizedBox(height: 20),
                        ],
                      ),
                    ),
                  ),
                ),
                // customDivider(color: Colors.black, text: Text('Hình ảnh')),
                SizedBox(height: 10),
                Center(
                  child: Card(
                    color: Colors.white70,
                    // borderRadius: BorderRadius.circular(10),
                    // width: width * 0.95,
                    child: Container(
                      padding: EdgeInsets.only(bottom: 30, left: 10, right: 10),
                      width: width * 0.95,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: buildGridFromString(
                        [
                          'assets/images/icon_app.png',
                          'assets/images/banh_ngot.png',
                          'assets/images/bot_giat.png',
                          'assets/images/do_hop.png',
                          'assets/images/gao.png',
                          'assets/images/gia_vi.png',
                          'assets/images/mi.png',
                          'assets/images/ngu_coc.png',
                          // 'assets/images/nuoc_ngot.png',
                        ],
                        (index) {
                          print('+++++++++++++@@@@@@@@@@@@@@@@@@@$index');
                        },
                        (index) {},
                      ),
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.symmetric(vertical: 20),
                  child: Center(
                    child: customButton(
                      () {},
                      width * 0.4,
                      40,
                      typeButton: 1,
                      Text(
                        'Thêm sản phẩm',
                        style: TextStyle(color: Colors.white),
                      ),
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

  Widget buildGridFromString(
    List<String> data,
    Function(int index) onAddImage,
    Function(int index) onLongPress,
  ) {
    final List<String> fixedData = List.from(data)
      ..addAll(List.filled(9 - data.length, ''));

    return GridView.builder(
      shrinkWrap: true,
      physics: NeverScrollableScrollPhysics(),
      itemCount: 9,
      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 3,
        crossAxisSpacing: 6,
        mainAxisSpacing: 6,
      ),
      itemBuilder: (context, index) {
        return Card(
          color: Colors.white,
          child: SizedBox(
            height: 150, // hoặc đặt theo nhu cầu
            child: Container(
              alignment: Alignment.center,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(10),
                color:
                    fixedData[index].isNotEmpty
                        ? Colors.white
                        : Colors.grey[200],
                border: Border.all(color: Colors.black),
              ),
              child:
                  fixedData[index].isNotEmpty
                      ? GestureDetector(
                        onLongPress: () {
                          onLongPress(index);
                        },
                        child: Image.asset(fixedData[index], fit: BoxFit.cover),
                      )
                      : GestureDetector(
                        onTap: () {
                          onAddImage(index);
                        },
                        child: Icon(Icons.add, size: 40, color: Colors.grey),
                      ),
            ),
          ),
        );
      },
    );
  }
}
