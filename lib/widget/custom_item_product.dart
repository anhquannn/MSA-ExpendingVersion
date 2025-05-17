
import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/domain/entities/product_model.dart';

Widget customItemProduct(
  VoidCallback? onUpdate,
  VoidCallback? onDelete, {
  bool? isOrder = false,
  ProductModel? model,
  bool isInventory = false,
  int? sale,
  int? stock,
  String? stockLevel,
}) {
  ProductModel model = ProductModel();
  final width = AppSize.w(0.98);
  final height = AppSize.h(0.4);
  return Padding(
    padding: const EdgeInsets.only(bottom: 5),
    child: SizedBox(
      width: width,
      child: Card(
        elevation: 3,
        color: Colors.white,
        child: Column(
          children: [
            SizedBox(height: 5),
            Row(
              spacing: 0,
              children: [
                // CachedImageWidget(imageUrl: model.image, width: width * 0.4),
                Container(
                  width: width * 0.45,
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.only(
                      topLeft: Radius.circular(10),
                      bottomLeft: Radius.circular(10),
                    ),
                  ),
                  child: Image.asset(imgCategoryBanhNgot, fit: BoxFit.contain),
                ),
                SizedBox(width: 5),
                SizedBox(
                  // width: width * 0.5,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          Padding(
                            padding: const EdgeInsets.only(bottom: 5),
                            child: Container(
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(10),
                                color: toHexToColor(secondaryColorOrange),
                              ),
                              child: Padding(
                                padding: const EdgeInsets.all(7.0),
                                child:
                                    isOrder == false
                                        ? Text('Đã bán: $sale')
                                        : Text('Mã đơn 001'),
                              ),
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.only(bottom: 5),
                            child: Container(
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(10),
                                color: toHexToColor(secondaryColorPurple),
                              ),
                              child: Padding(
                                padding: const EdgeInsets.all(7.0),
                                child:
                                    isOrder == false
                                        ? Text('Tồn kho: $stock')
                                        : Text('Tổng tiền: ${model.price}'),
                              ),
                            ),
                          ),
                          isOrder == false
                              ? Padding(
                                padding: const EdgeInsets.only(bottom: 5),
                                child: Container(
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(10),
                                    color: toHexToColor(secondaryErrorColor),
                                  ),
                                  child: Padding(
                                    padding: const EdgeInsets.all(7.0),
                                    child: Text(stockLevel??''),
                                  ),
                                ),
                              )
                              : Padding(
                                padding: const EdgeInsets.only(bottom: 5),
                                child: Container(
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(10),
                                    color: toHexToColor(secondaryColorGreen),
                                  ),
                                  child: Padding(
                                    padding: const EdgeInsets.all(7.0),
                                    child: Text('Sản phẩm: 4'),
                                  ),
                                ),
                              ),
                        ],
                      ),
                    ],
                  ),
                ),
              ],
            ),
            isOrder == false
                ? Container(
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.only(
                      bottomRight: Radius.circular(10),
                      bottomLeft: Radius.circular(10),
                    ),
                    color: toHexToColor(secondaryColorGreen),
                  ),
                  width: width,
                  child: Row(
                    children: [
                      Padding(
                        padding: const EdgeInsets.all(10.0),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            AutoSizeText(
                              minFontSize: 16,
                              maxFontSize: 24,
                              model.name??'',
                              overflow: TextOverflow.ellipsis,
                              softWrap: true,
                              maxLines: 1,
                              style: TextStyle(
                                color: toHexToColor(primaryTextColor),
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            AutoSizeText(
                              minFontSize: 10,
                              maxFontSize: 24,
                              model.price.toString(),
                              overflow: TextOverflow.ellipsis,
                              softWrap: true,
                              maxLines: 1,
                              style: TextStyle(
                                color: toHexToColor(secondaryTextColor),
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ],
                        ),
                      ),
                      Spacer(),
                      InkWell(
                        onTap: onUpdate,
                        child: Container(
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(20),
                            color: toHexToColor(primaryColorPurple),
                          ),
                          child: Padding(
                            padding: const EdgeInsets.all(7.0),
                            child: Icon(Icons.edit, color: Colors.white),
                          ),
                        ),
                      ),
                      SizedBox(width: 10),
                      InkWell(
                        onTap: onUpdate,
                        child: Container(
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(40),
                            color: toHexToColor(primaryErrorColor),
                          ),
                          child: Padding(
                            padding: const EdgeInsets.all(7.0),
                            child: Icon(
                              Icons.delete_outline,
                              color: Colors.white,
                            ),
                          ),
                        ),
                      ),
                      SizedBox(width: 10),
                    ],
                  ),
                )
                : InkWell(
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.only(
                        bottomRight: Radius.circular(10),
                        bottomLeft: Radius.circular(10),
                      ),
                      color: toHexToColor(primaryButtonColor),
                    ),
                    width: width,
                    height: 30,
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          'Chuyển sang đơn vị vận chuyển',
                          style: TextStyle(fontSize: 14, color: Colors.white),
                        ),
                        SizedBox(width: 10),
                        Icon(
                          Icons.arrow_forward_ios_rounded,
                          color: Colors.white,
                        ),
                      ],
                    ),
                  ),
                ),
          ],
        ),
      ),
    ),
  );
}

Widget customItemBranch({
  required String img,
  required String location,
  required String name,
  required String phoneNumber,
  required String sale,
  required VoidCallback onDelete,
  required VoidCallback onUpdate,
}) {
  final width = AppSize.w(0.95);
  return Card(
    color: Colors.white,
    child: SizedBox(
      width: width,
      child: Column(
        children: [
          Row(
            children: [
              Container(
                width: width * 0.5,
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.only(
                    topLeft: Radius.circular(10),
                    bottomLeft: Radius.circular(10),
                  ),
                ),
                child: Image.asset(img, fit: BoxFit.contain),
              ),
              SizedBox(width: 5),
              Column(
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.only(bottom: 5),
                    child: Container(
                      width: width * 0.45,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(10),
                        color: toHexToColor(secondaryColorOrange),
                      ),
                      child: Padding(
                        padding: const EdgeInsets.all(7.0),
                        child: Row(
                          children: [
                            Icon(Icons.location_on_outlined, size: 25),
                            Expanded(
                              child: Text(
                                overflow: TextOverflow.ellipsis,
                                softWrap: true,
                                maxLines: 1,
                                location,
                                style: TextStyle(
                                  color: toHexToColor(primaryTextColor),
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),

                  Padding(
                    padding: const EdgeInsets.only(bottom: 5),
                    child: Container(
                      width: width * 0.45,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(10),
                        color: toHexToColor(secondaryColorPurple),
                      ),
                      child: Padding(
                        padding: const EdgeInsets.all(7.0),
                        child: Column(
                          children: [
                            Text(
                              name,
                              overflow: TextOverflow.ellipsis,
                              softWrap: true,
                              maxLines: 1,
                              style: TextStyle(
                                color: toHexToColor(secondaryTextColor),
                              ),
                            ),
                            Text(
                              phoneNumber,
                              overflow: TextOverflow.ellipsis,
                              softWrap: true,
                              maxLines: 1,
                              style: TextStyle(
                                color: toHexToColor(secondaryTextColor),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
          Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.only(
                bottomRight: Radius.circular(10),
                bottomLeft: Radius.circular(10),
              ),
              color: toHexToColor(secondaryButtonColor),
            ),
            width: width,
            height: 60,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Text(
                    sale,
                    style: TextStyle(
                      fontSize: 16,
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                Spacer(),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: InkWell(
                    onTap: onUpdate,
                    child: Container(
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(20),
                        color: toHexToColor(primaryColorPurple),
                      ),
                      child: Padding(
                        padding: const EdgeInsets.all(7.0),
                        child: Icon(Icons.edit, color: Colors.white),
                      ),
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: InkWell(
                    onTap: onUpdate,
                    child: Container(
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(40),
                        color: toHexToColor(primaryErrorColor),
                      ),
                      child: Padding(
                        padding: const EdgeInsets.all(7.0),
                        child: Icon(Icons.delete_outline, color: Colors.white),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    ),
  );
}

Widget customItemInventory(
  VoidCallback? onUpdate,
  VoidCallback? onDelete,
  TextEditingController controller,
  String unit,
  VoidCallback onTap, {
  bool isChoose = false,
  bool? isLow = false,
  ProductModel? model,
  bool? isCheck = false,
  String? loss,
  Function(String)? onSubmitUnit,
  int? sale,
  int? stock,
  String? stockLevel,
}) {
  final width = AppSize.w(0.98);
  final height = AppSize.h(0.4);
  return Padding(
    padding: const EdgeInsets.only(bottom: 5),
    child: SizedBox(
      width: width,
      child: GestureDetector(
        onTap: onTap,
        child: Card(
          elevation: 3,
          color: isLow == true ? Colors.red[100] : Colors.white,
          child: Container(
            decoration: BoxDecoration(
              border:
                  isChoose == true
                      ? Border.all(color: Colors.blue, width: 2)
                      : isLow == true
                      ? Border.all(
                        color: toHexToColor(primaryErrorColor),
                        width: 2,
                      )
                      : null,
              borderRadius: BorderRadius.circular(10),
            ),
            child: Column(
              children: [
                SizedBox(height: 5),
                Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  spacing: 0,
                  children: [
                    // CachedImageWidget(imageUrl: model.image, width: width * 0.4),
                    Container(
                      width: width * 0.45,
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.only(
                          topLeft: Radius.circular(10),
                          bottomLeft: Radius.circular(10),
                        ),
                      ),
                      child: Image.asset(
                        imgCategoryBanhNgot,
                        fit: BoxFit.contain,
                      ),
                    ),
                    SizedBox(width: 5),
                    isCheck == true
                        ? Container(
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(10),
                            color: toHexToColor(secondaryColorPurple),
                          ),
                          child: _buildBottomBarInventory(
                            width,
                            model?.name??'',
                            model!.price.toString(),
                          ),
                        )
                        : SizedBox(
                          // width: width * 0.5,
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                mainAxisAlignment: MainAxisAlignment.start,
                                children: [
                                  Padding(
                                    padding: const EdgeInsets.only(bottom: 5),
                                    child: Container(
                                      decoration: BoxDecoration(
                                        borderRadius: BorderRadius.circular(10),
                                        color: toHexToColor(
                                          secondaryColorOrange,
                                        ),
                                      ),
                                      child: Padding(
                                        padding: const EdgeInsets.all(7.0),
                                        child: Text('Đã bán: $sale'),
                                      ),
                                    ),
                                  ),
                                  Padding(
                                    padding: const EdgeInsets.only(bottom: 5),
                                    child: Container(
                                      decoration: BoxDecoration(
                                        borderRadius: BorderRadius.circular(10),
                                        color: toHexToColor(
                                          secondaryColorPurple,
                                        ),
                                      ),
                                      child: Padding(
                                        padding: const EdgeInsets.all(7.0),
                                        child: Text('Tồn kho: $stock'),
                                      ),
                                    ),
                                  ),

                                  Padding(
                                    padding: const EdgeInsets.only(bottom: 5),
                                    child: Container(
                                      decoration: BoxDecoration(
                                        borderRadius: BorderRadius.circular(10),
                                        color: toHexToColor(
                                          secondaryErrorColor,
                                        ),
                                      ),
                                      child: Padding(
                                        padding: const EdgeInsets.all(7.0),
                                        child: Text(stockLevel??''),
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                  ],
                ),
                Container(
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.only(
                      bottomRight: Radius.circular(10),
                      bottomLeft: Radius.circular(10),
                    ),
                    color: toHexToColor(secondaryColorGreen),
                  ),
                  width: width,
                  height: 70,
                  child: Row(
                    children: [
                      isCheck == true
                          ? Expanded(
                            child: Padding(
                              padding: const EdgeInsets.only(
                                left: 10,
                                right: 10,
                              ),
                              child:
                                  loss != null
                                      ? Container(
                                        height: 40,
                                        decoration: BoxDecoration(
                                          borderRadius: BorderRadius.circular(
                                            10,
                                          ),
                                          color: toHexToColor(
                                            secondaryErrorColor,
                                          ),
                                        ),
                                        child: Padding(
                                          padding: const EdgeInsets.all(7.0),
                                          child: Text(
                                            style: TextStyle(
                                              color: Colors.white,
                                            ),
                                            loss,
                                            maxLines: 1,
                                            softWrap: true,
                                            overflow: TextOverflow.ellipsis,
                                          ),
                                        ),
                                      )
                                      : Container(),
                            ),
                          )
                          : _buildBottomBarInventory(
                            width,
                            model?.name??'',
                            model!.price.toString(),
                          ),
                      Container(
                        height: 40,
                        // width: width * 0.45,
                        width: loss != null ? width * 0.45 : width * 0.9,
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: Row(
                          children: [
                            SizedBox(
                              height: 40,
                              width: loss != null ? width * 0.3 : width * 0.75,
                              child: TextFormField(
                                onFieldSubmitted: (value) {
                                  onSubmitUnit!(value);
                                },
                                controller: controller,
                                style: TextStyle(
                                  color: toHexToColor(primaryTextColor),
                                ),
                                decoration: InputDecoration(
                                  // filled: true,
                                  fillColor: Colors.white,
                                  // isDense: true,
                                  labelStyle: TextStyle(
                                    fontSize: 14,
                                    color: toHexToColor(primaryTextColor),
                                  ),
                                  focusedErrorBorder: OutlineInputBorder(
                                    borderSide: BorderSide(
                                      color: Colors.transparent,
                                    ),
                                    borderRadius: BorderRadius.only(
                                      topLeft: Radius.circular(10),
                                      bottomLeft: Radius.circular(10),
                                    ),
                                  ),
                                  border: OutlineInputBorder(
                                    borderSide: BorderSide(
                                      color: Colors.transparent,
                                    ),
                                    borderRadius: BorderRadius.only(
                                      topLeft: Radius.circular(10),
                                      bottomLeft: Radius.circular(10),
                                    ),
                                  ),

                                  enabledBorder: OutlineInputBorder(
                                    borderSide: BorderSide(
                                      color: Colors.transparent,
                                    ),
                                    borderRadius: BorderRadius.only(
                                      topLeft: Radius.circular(10),
                                      bottomLeft: Radius.circular(10),
                                    ),
                                  ),
                                  focusedBorder: OutlineInputBorder(
                                    borderSide: BorderSide(
                                      color: Colors.transparent,
                                    ),
                                    borderRadius: BorderRadius.only(
                                      topLeft: Radius.circular(10),
                                      bottomLeft: Radius.circular(10),
                                    ),
                                  ),
                                  contentPadding: EdgeInsets.zero,
                                ),
                              ),
                            ),
                            SizedBox(
                              height: 40,
                              width: width * 0.15,
                              child: Container(
                                decoration: BoxDecoration(
                                  color: toHexToColor(primaryColorOrange),
                                  borderRadius: BorderRadius.only(
                                    topRight: Radius.circular(10),
                                    bottomRight: Radius.circular(10),
                                  ),
                                  // border: Border.all(color: Colors.black),
                                ),
                                child: Center(
                                  child: Text(
                                    unit,
                                    style: TextStyle(
                                      color: toHexToColor(primaryTextColor),
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                      SizedBox(width: 10),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    ),
  );
}

Widget _buildBottomBarInventory(double width, String name, String price) {
  return SizedBox(
    width: width * 0.45,
    child: Padding(
      padding: const EdgeInsets.all(10.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          AutoSizeText(
            minFontSize: 16,
            maxFontSize: 24,
            name,
            overflow: TextOverflow.ellipsis,
            softWrap: true,
            maxLines: 1,
            style: TextStyle(
              color: toHexToColor(primaryTextColor),
              fontWeight: FontWeight.bold,
            ),
          ),
          AutoSizeText(
            minFontSize: 10,
            maxFontSize: 24,
            price,
            overflow: TextOverflow.ellipsis,
            softWrap: true,
            maxLines: 1,
            style: TextStyle(
              color: toHexToColor(secondaryTextColor),
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    ),
  );
}


