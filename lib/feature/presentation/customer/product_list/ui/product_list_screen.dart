import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../../../../../widget/custom_item_promocode.dart';
import '../bloc/product_list_bloc.dart';

class ProductListScreen extends BaseView<ProductListBloc> {
  final bool? isSale;
  ProductListScreen({super.key, this.isSale});

  @override
  ProductListBloc createBloc() => ProductListBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as ProductListBloc;

    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(size: 25),
      centerTitle: true,
      title: customAutoSizeText(
        16,
        20,
        isSale == true ? 'Danh sách sản phẩm giảm giá' : 'Danh sách sản phẩm',
        textColor: Colors.white,
      ),
      bodyBuilder: (controller) {
        double width = AppSize.width();
        return SizedBox(
          width: AppSize.width(),
          child: SingleChildScrollView(
            controller: controller,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                isSale == true
                    ? SizedBox(
                      width: width,
                      child: Padding(
                        padding: EdgeInsets.all(10),
                        child: GridView.builder(
                          gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                            crossAxisCount: 2,
                            crossAxisSpacing: 3,
                            mainAxisSpacing: 3,
                            // childAspectRatio: 0.6, // Điều chỉnh tỷ lệ chiều rộng/chiều cao
                            childAspectRatio: (width / 2) / (AppSize.h(0.4)),
                          ),
                          physics: NeverScrollableScrollPhysics(),
                          shrinkWrap: true,
                          itemCount: 6,
                          itemBuilder: (context, index) {
                            return SizedBox(
                              child: customItemProductCustomer(
                                width * 0.4,
                                isDiscount: true,
                              ),
                            );
                          },
                        ),
                      ),
                    )
                    : SizedBox(
                      width: width,
                      child: Padding(
                        padding: EdgeInsets.all(10),
                        child: GridView.builder(
                          gridDelegate:
                              SliverGridDelegateWithFixedCrossAxisCount(
                                crossAxisCount: 2,
                                crossAxisSpacing: 3,
                                mainAxisSpacing: 3,
                                childAspectRatio:
                                    (width / 2) / (AppSize.h(0.4)),
                              ),
                          physics: NeverScrollableScrollPhysics(),
                          shrinkWrap: true,
                          itemCount: 6,
                          itemBuilder: (context, index) {
                            return SizedBox(
                              child: customItemProductCustomer(
                                width * 0.4,
                                isDiscount: false,
                              ),
                            );
                          },
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
