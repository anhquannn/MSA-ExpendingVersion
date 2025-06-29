import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../bloc/product_list_bloc.dart';

class ProductListScreen extends BaseView<ProductListBloc> {
  final bool? isSale;
  final ProductFilterResult? productList;
  final CategoryModel? category;
  const ProductListScreen({super.key, this.isSale, this.productList, this.category});

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
                  if (!snapshot.hasData || snapshot == null) {
                    return const Center(child: Text('Không có sản phẩm'));
                  }
                  final List<ProductModel>? model =
                      isSale == true
                          ? productList?.discountedProductsPage?.content
                          : productList?.productsPage?.content;
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
                    itemCount: model?.length,
                    itemBuilder: (context, index) {
                      if (index == model?.length) {
                        return const SizedBox(
                          // height: 2,
                        ); // SizedBox ở cuối danh sách
                      }
                      return customItemProductCustomer(
                        isDiscount: false,
                        model?[index] ?? ProductModel(),
                        AppSize.w(0.4),
                      );
                    },
                  );
                },
              ),
            ),
            // Thêm các Sliver khác nếu có
          ],
        );
      },
      hideBottomBarOnScroll: true,
    );
  }
}
