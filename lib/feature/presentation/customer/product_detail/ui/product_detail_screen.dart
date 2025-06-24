import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';

import '../../../../../core/config/base_bloc.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';
import '../bloc/product_detail_bloc.dart';

class ProductDetailCustomerScreen extends BaseView<ProductDetailBloc> {
  final ProductModel productModel;
  ProductDetailCustomerScreen({super.key, required this.productModel});

  @override
  ProductDetailBloc createState() => ProductDetailBloc();

  @override
  ProductDetailBloc createBloc() => ProductDetailBloc();
  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as ProductDetailBloc;
    return CustomScaffold(
      isHide: true,
      appBarLeading: iconBack(bloc.viewContext, color: Colors.black),
      bodyBuilder: (controller) {
        return buildBodyContent(bloc: bloc, controller: controller);
      },

      bottomBarItems: [
        BottomBarItem(
          label: 'Mua ngay',
          onTap: (index) {
            bloc.onBuy(mockProduct.productId ?? 0);
          },
        ),
        BottomBarItem(
          label: 'Thêm vào\n giỏ hàng',
          onTap: (index) {
            bloc.onAddToCart(mockProduct.productId ?? 0);
          },
        ),
      ],
      hideBottomBarOnScroll: true,
    );
  }

  Widget buildBodyContent({
    required ProductDetailBloc bloc,
    required ScrollController controller,
  }) {
    return CustomScrollView(
      controller: controller,
      slivers: [
        SliverToBoxAdapter(child: itemImage(productModel.productImages, bloc)),
        const SliverToBoxAdapter(child: SizedBox(height: 5)),
        SliverToBoxAdapter(
          child: itemProductDetail(
            productModel.name ?? '',
            productModel.currentPrice ?? 0,
            priceDiscount: productModel.price ?? 0,
          ),
        ),
        const SliverToBoxAdapter(child: SizedBox(height: 5)),
        SliverToBoxAdapter(
          child: itemDelivery(
            'Nhận hàng 12/12/2024 - 13/12/2024',
            'Tặng voucher 20.000 đ nếu giao sau thời gian trên',
          ),
        ),
        const SliverToBoxAdapter(child: SizedBox(height: 5)),
        SliverToBoxAdapter(child: itemFeedBack('4.9', 100, () {}, bloc)),
        const SliverToBoxAdapter(child: SizedBox(height: 5)),
        SliverToBoxAdapter(
          child: itemDec(bloc, 'Dưa lưới là loại trái cây cao cấp...'),
        ),
        SliverToBoxAdapter(
          child: customDivider(
            color: toHexToColor(primaryTextColor),
            text: Text('Sản phẩm liên quan'),
          ),
        ),
        SliverToBoxAdapter(child: _buildGridItems(bloc)),
        const SliverToBoxAdapter(child: SizedBox(height: 1000)),
      ],
    );
  }

  Widget _buildGridItems(ProductDetailBloc bloc) {
    return SizedBox(
      width: AppSize.width(),
      child: SingleChildScrollView(
        child: Column(
          children: [
            StreamBuilder(
              stream: bloc.productModels,
              builder: (context, snapshot) {
                // if (snapshot.connectionState == ConnectionState.waiting) {
                //   return const Center(child: CircularProgressIndicator());
                // }
                // if (snapshot.hasError) {
                //   return Center(child: Text('Lỗi: ${snapshot.error}'));
                // }
                // if (!snapshot.hasData || snapshot.data!.isEmpty) {
                //   return const Center(child: Text('Không có sản phẩm'));
                // }
                // final products = snapshot.data!;
                return GridView.builder(
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    crossAxisSpacing: 3,
                    mainAxisSpacing: 3,
                    mainAxisExtent: 300,
                  ),
                  physics: const NeverScrollableScrollPhysics(),
                  shrinkWrap: true,
                  itemCount: 10, //products.length > 10 ? 10 : products.length,
                  itemBuilder: (context, index) {
                    return InkWell(
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder:
                                (context) => ProductDetailCustomerScreen(
                                  productModel: mockProduct,
                                ),
                          ),
                        );
                        // bloc.onTapProductDetail(products[index]);
                      },
                      child: customItemProductCustomer(
                        isDiscount: true,
                        // products[index],
                        mockProduct,
                        AppSize.width() * 0.4,
                        onBuy: () {
                          bloc.onBuy(mockProduct.productId ?? 0);
                        },
                        onAddToCart: () {
                          bloc.onAddToCart(mockProduct.productId ?? 0);
                        },
                      ),
                    );
                  },
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget itemDec(ProductDetailBloc bloc, String desc) {
    return Card(
      child: Container(
        width: AppSize.w(0.95),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(10),
          color: Colors.white,
        ),
        padding: const EdgeInsets.all(10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Text(
              'Chi tiết sản phẩm',
              style: TextStyle(
                color: toHexToColor(primaryTextColor),
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 5),
            AnimatedCrossFade(
              firstChild: Text(
                desc,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: TextStyle(color: toHexToColor(primaryTextColor)),
              ),
              secondChild: Text(
                desc,
                style: TextStyle(color: toHexToColor(primaryTextColor)),
              ),
              crossFadeState:
                  bloc.isExpanded
                      ? CrossFadeState.showSecond
                      : CrossFadeState.showFirst,
              duration: const Duration(milliseconds: 300),
            ),
            Align(
              alignment: Alignment.bottomCenter,
              child: TextButton(
                onPressed: () {
                  bloc.onChangeExpanded();
                },
                child: Text(
                  bloc.isExpanded ? 'Thu gọn' : 'Xem thêm',
                  style: TextStyle(
                    color: toHexToColor(primaryColorGreen),
                    fontStyle: FontStyle.italic,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget itemImage(List<ProductImage>? images, ProductDetailBloc bloc) {
    final PageController _pageController = PageController();
    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          SizedBox(
            width: AppSize.w(0.95),
            height: AppSize.w(0.95),
            child: Stack(
              children: [
                ClipRRect(
                  borderRadius: BorderRadius.circular(10),
                  child: PageView.builder(
                    controller: _pageController,
                    itemCount: images?.length,
                    itemBuilder: (context, index) {
                      return CachedNetworkImage(
                        imageUrl: images![index].imageUrl,
                        fit: BoxFit.cover,
                        placeholder:
                            (context, url) => const Center(
                              child: CircularProgressIndicator(),
                            ),
                        errorWidget:
                            (context, url, error) =>
                                Image.asset(imgBranch, fit: BoxFit.cover),
                      );
                    },
                  ),
                ),
                Positioned(
                  top: kToolbarHeight / 2,
                  left: 8,
                  child: InkWell(
                    onTap: () {
                      Navigator.pop(bloc.viewContext);
                    },
                    borderRadius: BorderRadius.circular(20),
                    child: const Icon(
                      Icons.arrow_back_ios,
                      color: Colors.black,
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8),
          SmoothPageIndicator(
            controller: _pageController,
            count: images?.length??0,
            effect: WormEffect(
              dotHeight: 8,
              dotWidth: 8,
              activeDotColor: toHexToColor(primaryButtonColor),
              dotColor: Colors.grey.shade300,
            ),
          ),
        ],
      ),
    );
  }

  Widget itemProductDetail(String name, double price, {double? priceDiscount}) {
    String discount = '0';

    if (price > 0 && priceDiscount != null && priceDiscount > 0) {
      double discountPercent = 100 - ((priceDiscount / price) * 100);
      discount = discountPercent.toStringAsFixed(0);
    }
    return Card(
      child: Container(
        padding: EdgeInsets.all(10),
        width: AppSize.w(0.95),
        decoration: BoxDecoration(
          color: toHexToColor(secondaryActionColor),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              name,
              style: TextStyle(
                color: toHexToColor(primaryTextColor),
                fontWeight: FontWeight.bold,
                fontSize: 18,
              ),
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              softWrap: true,
            ),
            Row(
              children: [
                Text(
                  formatCurrency(price),
                  style: TextStyle(
                    color: toHexToColor(appBarColor),
                    fontWeight: FontWeight.bold,
                    fontSize: 18,
                  ),
                ),
                SizedBox(width: 10),
                if (price != priceDiscount) ...[
                  Text(
                    formatCurrency(priceDiscount ?? 0),
                    style: TextStyle(
                      color: toHexToColor(appBarColor),
                      fontStyle: FontStyle.italic,
                      fontSize: 14,
                      decoration: TextDecoration.lineThrough,
                    ),
                  ),
                  Spacer(),
                  Container(
                    decoration: BoxDecoration(
                      color: toHexToColor(primaryErrorColor),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: Padding(
                      padding: const EdgeInsets.symmetric(
                        vertical: 2,
                        horizontal: 8,
                      ),
                      child: Text(
                        '$discount%',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                ],
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget itemDelivery(String time, String desc) {
    return Card(
      child: Container(
        padding: EdgeInsets.all(10),
        width: AppSize.w(0.95),
        decoration: BoxDecoration(
          color: toHexToColor(secondaryActionColor),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              time ?? '',
              style: TextStyle(
                color: toHexToColor(primaryTextColor),
                fontSize: 12,
              ),
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              softWrap: true,
            ),
            Text(
              desc ?? '',
              style: TextStyle(
                color: toHexToColor(primaryTextColor),
                fontSize: 12,
              ),
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              softWrap: true,
            ),
          ],
        ),
      ),
    );
  }

  Widget itemFeedBack(
    String startCount,
    int feedbackCount,
    VoidCallback onSeeMore,
    ProductDetailBloc? bloc,
  ) {
    return Card(
      child: Container(
        // padding: EdgeInsets.all(10),
        width: AppSize.w(0.95),
        decoration: BoxDecoration(
          color: toHexToColor(secondaryActionColor),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          spacing: 5,
          children: [
            Container(
              padding: EdgeInsets.all(10),
              decoration: BoxDecoration(
                color: toHexToColor(secondaryButtonColor),
                borderRadius: BorderRadius.only(
                  topRight: Radius.circular(10),
                  topLeft: Radius.circular(10),
                ),
              ),
              child: Row(
                children: [
                  Text(
                    startCount,
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  SizedBox(width: 2),
                  Icon(Icons.star, color: Colors.amber),
                  SizedBox(width: 2),
                  Text(
                    'Đánh giá sản phẩm ($feedbackCount)',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Spacer(),
                  InkWell(
                    onTap: () => onSeeMore(),
                    child: Text(
                      'Xem tất cả',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 12,
                        fontStyle: FontStyle.italic,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            SizedBox(height: 10),

            buildFeedBack(
              bloc: bloc,
              avt: avtWomen1,
              name: 'NguyenVanA NguyenVanA NguyenVanA',
              desc:
                  'NguyenVanA NguyenVanA NguyenVanANguyenVanA NguyenVanA NguyenVanANguyenVanA NguyenVanA NguyenVanANguyenVanA NguyenVanA NguyenVanANguyenVanA NguyenVanA NguyenVanA',
              rating: 3.8,
              imgFeedback: [
                imgCategoryBanhNgot,
                imgAppBarIllustration,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
              ],
            ),
            buildFeedBack(
              bloc: bloc,
              avt: avtWomen1,
              name: 'NguyenVanA NguyenVanA NguyenVanA',
              desc:
                  'NguyenVanA NguyenVanA NguyenVanANguyenVanA NguyenVanA NguyenVanANguyenVanA NguyenVanA NguyenVanANguyenVanA NguyenVanA NguyenVanANguyenVanA NguyenVanA NguyenVanA',
              rating: 3.8,
              imgFeedback: [
                imgCategoryBanhNgot,
                imgAppBarIllustration,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
                imgCategoryBotGiat,
              ],
            ),
            SizedBox(height: 10),
          ],
        ),
      ),
    );
  }

  Widget buildFeedBack({
    String? avt,
    String? name,
    String? desc,
    List<String>? imgFeedback,
    double? rating,
    ProductDetailBloc? bloc,
  }) {
    return Column(
      spacing: 5,
      children: [
        buildCircleAvatar(avt ?? avtWomen1, name ?? '', rating ?? 5),
        if (imgFeedback != null && imgFeedback.isNotEmpty)
          SizedBox(
            width: AppSize.w(0.95),
            height: 80,
            child: ListView.builder(
              itemCount: imgFeedback.length,
              scrollDirection: Axis.horizontal,
              itemBuilder: (context, index) {
                return Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 2),
                  child: SizedBox(
                    height: 80,
                    width: 80,
                    child: GestureDetector(
                      onTap: () {
                        showDialog(
                          context: context,
                          builder:
                              (_) => showImageDialog(
                                imageProvider: AssetImage(imgFeedback[index]),
                                bloc: bloc,
                              ),
                        );
                      },
                      child: buildItemFeedBack(imgFeedback[index]),
                    ),
                  ),
                );
              },
            ),
          ),
      ],
    );
  }

  Widget buildCircleAvatar(String image, String name, double rating) {
    return Row(
      children: [
        const SizedBox(width: 3),
        Container(
          width: 35,
          height: 35,
          decoration: BoxDecoration(shape: BoxShape.circle),
          child: ClipOval(child: Image.asset(image, fit: BoxFit.contain)),
        ),
        const SizedBox(width: 7),
        SizedBox(
          width: AppSize.w(0.35),
          height: 20,
          child: Text(
            name,
            style: TextStyle(color: toHexToColor(primaryTextColor)),
            overflow: TextOverflow.ellipsis,
            maxLines: 1,
            softWrap: true,
          ),
        ),

        Spacer(),
        buildStarRating(rating: rating),
        const SizedBox(width: 3),
      ],
    );
  }

  Widget buildItemFeedBack(String img) {
    return Container(
      width: 80,
      height: 80,
      padding: EdgeInsets.all(5),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: toHexToColor(borderColor), width: 2),
        color: Colors.white,
      ),
      child: ClipRect(child: Image.asset(img)),
    );
  }

  Widget showImageDialog({
    required ImageProvider imageProvider,
    ProductDetailBloc? bloc,
  }) {
    return Dialog(
      backgroundColor: Colors.transparent,
      insetPadding: const EdgeInsets.all(16),
      child: Stack(
        children: [
          ClipRRect(
            borderRadius: BorderRadius.circular(12),
            child: InteractiveViewer(child: Image(image: imageProvider)),
          ),
          Positioned(
            top: 8,
            right: 8,
            child: IconButton(
              icon: const Icon(Icons.close, color: Colors.white),
              onPressed: () {
                bloc?.onTapBack();
              },
            ),
          ),
        ],
      ),
    );
  }
}
