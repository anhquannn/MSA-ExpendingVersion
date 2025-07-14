import 'package:cached_network_image/cached_network_image.dart';
import 'package:card_swiper/card_swiper.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/model/response/feedback_filter_response.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/presentation/customer/product_detail/ui/rating_screen.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';

import '../../../../../core/config/base_bloc.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';
import '../bloc/product_detail_bloc.dart';

class ProductDetailCustomerScreen extends BaseView<ProductDetailBloc> {
  final ProductModel? productModel;
  final int? productId;
  ProductDetailCustomerScreen({super.key, this.productModel, this.productId});

  @override
  ProductDetailBloc createState() => ProductDetailBloc();

  @override
  ProductDetailBloc createBloc() => ProductDetailBloc();
  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as ProductDetailBloc;
    return CustomScaffold(
      isHide: true,

      appBarLeading: Padding(
        padding: const EdgeInsets.only(top: 10),
        child: iconBack(bloc.viewContext, color: Colors.black),
      ),
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
            bloc.onAddToCart(bloc.productModel ?? ProductModel(), context);
          },
        ),
      ],
      hideBottomBarOnScroll: false,
    );
  }

  Widget buildBodyContent({
    required ProductDetailBloc bloc,
    required ScrollController controller,
  }) {
    return CustomScrollView(
      controller: controller,
      slivers: [
        SliverToBoxAdapter(
          child: itemImage(bloc.productModel?.productImages, bloc),
        ),
        const SliverToBoxAdapter(child: SizedBox(height: 5)),
        SliverToBoxAdapter(
          child: itemProductDetail(bloc.productModel ?? ProductModel()),
        ),
        const SliverToBoxAdapter(child: SizedBox(height: 5)),
        SliverToBoxAdapter(
          child: itemDec(bloc, bloc.productModel?.description ?? ''),
        ),
        SliverToBoxAdapter(
          child: StreamBuilder<List<FeedbacFilterkResponse>>(
            stream: bloc.streamListFeedbak.stream,
            builder: (context, snapshot) {
              final list = snapshot.data ?? [];

              return itemFeedBack(
                startCount: bloc.starCount.toStringAsFixed(1),
                feedbackCount: list.length,
                onSeeMore: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder:
                          (_) => RatingScreen(
                            productId: bloc.productModel?.productId ?? 0,
                          ),
                    ),
                  );
                },
                feedbackList: list,
                bloc: bloc,
              );
            },
          ),
        ),
        SliverToBoxAdapter(child: buildProductSuggestionSection(bloc, context)),
        SliverToBoxAdapter(child: buildProductPopular(bloc, context)),
        const SliverToBoxAdapter(child: SizedBox(height: 80)),
      ],
    );
  }

  Widget buildProductPopular(ProductDetailBloc bloc, BuildContext context) {
    return StreamBuilder(
      stream: bloc.streamProductPopular,
      builder: (context, snapshot) {
        if (snapshot.hasData && snapshot.data != null) {
          return Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: customTitleCategory(
                  'Sản phẩm phổ biến',
                  'Xem tất cả',
                  () => bloc.onTapPopularProduct(context, snapshot.data ?? []),
                ),
              ),
              MediaQuery.removePadding(
                context: context,
                removeTop: true,
                child: buildProductSwiper(
                  products: snapshot.data ?? [],
                  bloc: bloc,
                ),
              ),
            ],
          );
        } else {
          return const SizedBox.shrink();
        }
      },
    );
  }

  Widget buildProductSuggestionSection(
    ProductDetailBloc bloc,
    BuildContext context,
  ) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        StreamBuilder(
          stream: bloc.streamProductCombine,
          builder: (context, snapshot) {
            if (snapshot.hasData &&
                snapshot.data != null &&
                snapshot.data!.isNotEmpty) {
              return Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: customTitleCategory(
                      'Sản phẩm kết hợp',
                      'Xem tất cả',
                      () => bloc.onTapCombineProduct(
                        context,
                        snapshot.data ?? [],
                      ),
                    ),
                  ),
                  MediaQuery.removePadding(
                    context: context,
                    removeTop: true,
                    child: buildProductSwiper(
                      products: snapshot.data!,
                      bloc: bloc,
                    ),
                  ),
                ],
              );
            } else {
              return const SizedBox.shrink();
            }
          },
        ),
        StreamBuilder(
          stream: bloc.productModels,
          builder: (context, snapshot) {
            if (snapshot.hasData && snapshot.data != null) {
              final products = snapshot.data!;
              return Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: customTitleCategory(
                      'Sản phẩm liên quan',
                      'Xem tất cả',
                      () => bloc.onTapSeeAllProduct(
                        context,
                        products.productsPage?.content ?? [],
                      ),
                    ),
                  ),
                  MediaQuery.removePadding(
                    context: context,
                    removeTop: true,
                    child: buildProductSwiper(
                      products: products.productsPage?.content ?? [],
                      bloc: bloc,
                    ),
                  ),
                ],
              );
            } else {
              return const SizedBox.shrink();
            }
          },
        ),
      ],
    );
  }

  Widget buildProductSwiper({
    required List<ProductModel> products,
    required ProductDetailBloc bloc,
  }) {
    return SizedBox(
      height: 300,
      child: Swiper(
        itemCount: products.length,
        scrollDirection: Axis.horizontal,
        autoplay: false,
        viewportFraction: 0.55,
        scale: 0.9,
        itemBuilder: (BuildContext context, int index) {
          final product = products[index];
          return InkWell(
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder:
                      (_) => ProductDetailCustomerScreen(productModel: product),
                ),
              );
            },
            child: customItemProductCustomer(
              isDiscount: (product.discountPercentage ?? 0) > 0,
              product,
              AppSize.width() * 0.5,
              onBuy: () => bloc.onBuy(product.productId ?? 0),
              onAddToCart: () => bloc.onAddToCart(product, context),
            ),
          );
        },
      ),
    );
  }

  Widget buildHorizontalProductGrid({
    required List<ProductModel> products,
    required ProductDetailBloc bloc,
  }) {
    return SizedBox(
      height: 300, // đảm bảo có chiều cao cố định cho scroll ngang
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 8),
        itemCount: products.length,
        itemBuilder: (context, index) {
          final product = products[index];
          return Container(
            width: AppSize.width() * 0.3,
            margin: const EdgeInsets.symmetric(horizontal: 4),
            child: InkWell(
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder:
                        (_) =>
                            ProductDetailCustomerScreen(productModel: product),
                  ),
                );
              },
              child: customItemProductCustomer(
                isDiscount: (product.discountPercentage ?? 0) > 0,
                product,
                AppSize.width() * 0.3,
                onBuy: () => bloc.onBuy(product.productId ?? 0),
                onAddToCart: () => bloc.onAddToCart(product, context),
              ),
            ),
          );
        },
      ),
    );
  }

  Widget itemDec(ProductDetailBloc bloc, String desc) {
    return Card(
      child: Container(
        width: AppSize.w(0.95),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(10),
          color: Colors.white70,
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
                      return images?[index] != null
                          ? CachedNetworkImage(
                            imageUrl: images![index].imageUrl,
                            fit: BoxFit.contain,
                            placeholder:
                                (context, url) => const Center(
                                  child: CircularProgressIndicator(),
                                ),
                            errorWidget:
                                (context, url, error) =>
                                    Image.asset(imgBranch, fit: BoxFit.contain),
                          )
                          : Image.asset(imgBranch);
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
            count: images?.length ?? 0,
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

  Widget itemProductDetail(ProductModel model) {
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
              model.name ?? '',
              style: TextStyle(
                color: toHexToColor(primaryTextColor),
                fontWeight: FontWeight.bold,
                fontSize: 18,
              ),
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
              softWrap: true,
            ),
            Row(
              children: [
                Text(
                  formatCurrency(model.price ?? 0),
                  style: TextStyle(
                    color: toHexToColor(appBarColor),
                    fontWeight: FontWeight.bold,
                    fontSize: 18,
                  ),
                ),
                SizedBox(width: 10),
                if ((model.discountPercentage ?? 0) > 0) ...[
                  Text(
                    formatCurrency(
                      (model.discountPercentage ?? 0) * (model.price ?? 0),
                    ),
                    style: TextStyle(
                      color: toHexToColor(appBarColor),
                      fontStyle: FontStyle.italic,
                      fontSize: 14,
                      decoration: TextDecoration.lineThrough,
                    ),
                  ),
                ],
                Text(
                  '/${model.unit}',
                  style: TextStyle(
                    color: toHexToColor(appBarColor),
                    // fontStyle: FontStyle.italic,
                    fontSize: 16,
                  ),
                ),
                if ((model.discountPercentage ?? 0) > 0) ...[
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
                        '${model.discountPercentage ?? 0}%',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                ],
              ],
            ),
            SizedBox(width: 10),
            buildTextSpan(defaultText: 'Trọng lượng', text: model.netWeight),
            buildTextSpan(
              defaultText: 'Thông số sản phẩm',
              text: model.specification,
            ),
            buildTextSpan(
              defaultText: 'Sản phẩm thuộc loại',
              text: model.category?.name,
            ),
          ],
        ),
      ),
    );
  }

  Widget buildTextSpan({String? defaultText, String? text}) {
    if (text == null) return const SizedBox.shrink();

    return RichText(
      text: TextSpan(
        children: [
          TextSpan(
            text: '$defaultText: ',
            style: TextStyle(
              fontWeight: FontWeight.w400,
              color: toHexToColor(secondaryTextColor),
              fontSize: 14,
            ),
          ),
          TextSpan(
            text: text,
            style: TextStyle(
              color: toHexToColor(primaryTextColor),
              fontWeight: FontWeight.w600,
              fontSize: 15,
            ),
          ),
        ],
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

  Widget buildFeedBack({
    String? avt,
    String? name,
    String? desc,
    List<String>? imgFeedback,
    double? rating,
    ProductDetailBloc? bloc,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        buildCircleAvatar(avt ?? avtWomen1, name ?? '', rating ?? 5),
        // if (desc != null)
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          child: Text(
            desc ?? 'Chưa có mô tả đánh giá...',
            style: const TextStyle(fontSize: 14),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            softWrap: true,
          ),
        ),
        SizedBox(height: 3),
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
        SizedBox(height: 3),
      ],
    );
  }

  Widget itemFeedBack({
    required String startCount,
    required int feedbackCount,
    required VoidCallback onSeeMore,
    required List<FeedbacFilterkResponse> feedbackList,
    required ProductDetailBloc? bloc,
  }) {
    return Card(
      child: Container(
        width: AppSize.w(0.95),
        decoration: BoxDecoration(
          color: Colors.white70,
          borderRadius: BorderRadius.circular(10),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Container(
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(
                color: toHexToColor(secondaryButtonColor),
                borderRadius: const BorderRadius.only(
                  topRight: Radius.circular(10),
                  topLeft: Radius.circular(10),
                ),
              ),
              child: Row(
                children: [
                  Text(
                    startCount,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(width: 2),
                  const Icon(Icons.star, color: Colors.amber),
                  const SizedBox(width: 2),
                  Text(
                    'Đánh giá sản phẩm ($feedbackCount)',
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const Spacer(),
                  InkWell(
                    onTap: onSeeMore,
                    child: const Text(
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
            const SizedBox(height: 10),

            ...feedbackList.map((item) {
              return buildFeedBack(
                bloc: bloc,
                avt: item.user?.image,
                name: item.user?.fullName,
                desc: item.comments,
                rating: (item.rating ?? 0).toDouble(),
                imgFeedback: [], // TODO: nếu có ảnh feedback, truyền vào đây
              );
            }).toList(),

            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }

  Widget buildCircleAvatar(String image, String name, double rating) {
    return Row(
      children: [
        const SizedBox(width: 3),
        Container(
          width: 35,
          height: 35,
          decoration: const BoxDecoration(shape: BoxShape.circle),
          child: ClipOval(
            child: Image.asset(
              image,
              fit: BoxFit.cover,
              errorBuilder: (context, error, stackTrace) {
                // Nếu ảnh lỗi thì hiển thị ảnh mặc định
                return Image.asset(
                  'assets/images/avt_men5.jpg',
                  fit: BoxFit.cover,
                );
              },
            ),
          ),
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
        const Spacer(),
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

  Widget customTitleCategory(
    String title,
    String actionText,
    VoidCallback onTap,
  ) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          title,
          style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
        ),
        InkWell(
          onTap: onTap,
          child: Text(
            actionText,
            style: const TextStyle(fontSize: 12, color: Colors.blue),
          ),
        ),
      ],
    );
  }
}
