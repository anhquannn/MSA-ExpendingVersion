import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/presentation/customer/product_detail/bloc/product_list_detail.dart';
import 'package:msa/feature/presentation/customer/product_detail/ui/product_detail_screen.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

class ProductListDetailScreen extends BaseView<ProductListDetailBloc> {
  final bool? isSale;
  final List<ProductModel>? productList;
  final CategoryModel? category;
  List<CartItemModel>? listCartItemModel = [];
  final String? title;
  ProductListDetailScreen({
    super.key,
    this.isSale,
    this.productList,
    this.category,
    this.listCartItemModel,
    this.title,
  });

  @override
  ProductListDetailBloc createBloc() => ProductListDetailBloc();

  // final bloc = (context as StatefulElement).state as ProductListDetailBloc;
  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as ProductListDetailBloc;
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(bloc.viewContext, size: 25),
      // centerTitle: true,
      title: customAutoSizeText(
        maxLine: 1,
        isBold: true,
        16,
        20,
        title ??
            (isSale == true
                ? 'Danh sách sản phẩm khuyến mãi'
                : 'Danh sách sản phẩm phổ biến'),
        textColor: Colors.white,
      ),
      centerTitle: true,
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
              child: StreamBuilder<List<ProductModel>>(
                stream: bloc.streamProductModels,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  }

                  if (snapshot.hasError) {
                    return Center(child: Text('Lỗi: ${snapshot.error}'));
                  }

                  if (!snapshot.hasData || snapshot.data!.isEmpty) {
                    return Center(
                      child: Text(
                        'Không có dữ liệu ${category?.name ?? ''}',
                        style: const TextStyle(fontSize: 16),
                      ),
                    );
                  }

                  final model = snapshot.data!;

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
                    itemCount: model.length,
                    itemBuilder: (context, index) {
                      final product = model[index];
                      return InkWell(
                        onTap: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder:
                                  (bContext) => ProductDetailCustomerScreen(
                                    productId: product.productId,
                                    productModel: product,
                                  ),
                            ),
                          );
                        },
                        child: customItemProductCustomer(
                          isDiscount: (product.discountPercentage ?? 0) > 0,
                          product,
                          AppSize.w(0.4),
                          onAddToCart: () {
                            bloc.onAddToCart(product, context);
                          },
                          onBuy: () {
                            bloc.onBuyNow(product, context);
                          },
                        ),
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
