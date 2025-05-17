import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../../../../../widget/custom_item_promocode.dart';
import '../bloc/product_list_bloc.dart';

class ProductListScreen extends BaseView<ProductListBloc> {
  final bool? isSale;
  final List<ProductModel>? productList;
  const ProductListScreen({super.key, this.isSale, this.productList});

  @override
  ProductListBloc createBloc() => ProductListBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as ProductListBloc;

    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(bloc.viewContext, size: 25),
      // centerTitle: true,
      title: customAutoSizeText(
        maxLine: 1,
        isBold: true,
        16,
        20,
        isSale == true
            ? 'Danh sách sản phẩm khuyến mãi'
            : 'Danh sách sản phẩm phổ biến',
        textColor: Colors.white,
      ),
      appBarActions: [
        Padding(
          padding: const EdgeInsets.only(right: 10),
          child: Icon(Icons.discount_outlined, color: Colors.white),
        ),
      ],
      isHide: false,
      bodyBuilder: (controller) {
        return CustomScrollView(
          // controller: controller,
          slivers: [
            SliverToBoxAdapter(
              child: StreamBuilder(
                stream: bloc.streamProducts,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  }
                  if (snapshot.hasError) {
                    return Center(child: Text('Lỗi: ${snapshot.error}'));
                  }
                  if (!snapshot.hasData || snapshot.data!.isEmpty) {
                    return const Center(child: Text('Không có sản phẩm'));
                  }
                  final products = snapshot.data!;
                  return GridView.builder(
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                          crossAxisCount: 2,
                          crossAxisSpacing: 3,
                          mainAxisSpacing: 3,
                          mainAxisExtent: 300,
                        ),
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount: products.length,
                    itemBuilder: (context, index) {
                      if (index == products.length) {
                        return const SizedBox(
                          // height: 2,
                        ); // SizedBox ở cuối danh sách
                      }
                      return customItemProductCustomer(
                        isDiscount: false,
                        products[index],
                        AppSize.w(0.4),
                      );
                    },
                  );
                },
              ),
            ),
          ],
        );
      },
      hideBottomBarOnScroll: true,
    );
  }
}
