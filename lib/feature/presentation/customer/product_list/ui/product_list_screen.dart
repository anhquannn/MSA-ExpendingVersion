import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/presentation/customer/product_detail/ui/product_detail_screen.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../bloc/product_list_bloc.dart';

class ProductListScreen extends BaseView<ProductListBloc> {
  final bool? isSale;
  final ProductFilterResult? productList;
  final CategoryModel? category;
  List<CartItemModel>? listCartItemModel = [];
  final String? title;
  ProductListScreen({
    super.key,
    this.isSale,
    this.productList,
    this.category,
    this.listCartItemModel,
    this.title,
  });

  @override
  ProductListBloc createBloc() => ProductListBloc();

  // final bloc = (context as StatefulElement).state as ProductListBloc;
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
              child: StreamBuilder<ProductFilterResult>(
                stream: bloc.streamProductModels,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  }

                  if (snapshot.hasError) {
                    return Center(child: Text('Lỗi: ${snapshot.error}'));
                  }

                  if (!snapshot.hasData || snapshot.data == null) {
                    return const Center(child: Text('Không có sản phẩm'));
                  }

                  // ✅ Lấy data từ snapshot thay vì bloc.listProducts
                  final productFilter = snapshot.data!;
                  final List<ProductModel>? model =
                      category != null
                          ? productFilter.products
                          : isSale == true
                          ? productFilter.discountedProductsPage?.content
                          : productFilter.productsPage?.content;

                  if (model != null && model.isNotEmpty) {
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
                  } else {
                    return Center(
                      child: Text(
                        'Không có dữ liệu ${category?.name ?? ''}',
                        style: const TextStyle(fontSize: 16),
                      ),
                    );
                  }
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
