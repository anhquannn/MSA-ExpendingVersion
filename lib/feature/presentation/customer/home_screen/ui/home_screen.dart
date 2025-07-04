import 'dart:ffi';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:card_swiper/card_swiper.dart';
import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/response/get_order_response_model.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/notification_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/persional/ui/persional_screen.dart';
import 'package:msa/feature/presentation/customer/product_list/ui/product_list_screen.dart';
import 'package:msa/widget/customBottomSheet.dart';
import 'package:msa/widget/custom_notification_bottomsheet.dart';
import 'package:rxdart/rxdart.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';
import 'package:auto_size_text/auto_size_text.dart';
import '../../../../../core/config/base_bloc.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_item_promocode.dart';
import '../../../../../widget/custom_notification.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';
import '../bloc/home_screen_bloc.dart';

class HomeScreen extends BaseView<HomeScreenBloc> {
  const HomeScreen({super.key});

  @override
  HomeScreenBloc createState() => HomeScreenBloc();

  @override
  HomeScreenBloc createBloc() => HomeScreenBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as HomeScreenBloc;

    return CustomScaffold(
      key: bloc.cartIconKey,
      appBarLeading: InkWell(
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => PersionalScreen()),
          );
        },
        child: Container(
          width: 45,
          height: 45,
          decoration: BoxDecoration(
            color: toHexToColor(backgroundColor),
            borderRadius: BorderRadius.circular(30),
          ),
          child: Center(
            child: ClipRRect(
              borderRadius: BorderRadius.circular(22.5),
              child: StreamBuilder(
                stream: bloc.streamUserModel,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const CircularProgressIndicator();
                  }
                  if (snapshot.hasError) {
                    return const Icon(Icons.error, color: Colors.red);
                  }
                  if (!snapshot.hasData) {
                    return const Icon(Icons.person, color: Colors.grey);
                  }

                  String? imagePath = snapshot.data?.image;
                  bool isValidImage =
                      imagePath != null && imagePath.trim().isNotEmpty;

                  String finalImagePath =
                      isValidImage
                          ? imagePath!
                          : (Storage.userModelGlobal?.image
                                      ?.trim()
                                      .isNotEmpty ==
                                  true
                              ? Storage.userModelGlobal!.image
                              : 'assets/images/default_avatar.jpg');

                  return Image.asset(
                    finalImagePath,
                    fit: BoxFit.cover,
                    width: 40,
                    height: 40,
                    errorBuilder:
                        (context, error, stackTrace) =>
                            const Icon(Icons.broken_image, size: 40),
                  );
                },
              ),
            ),
          ),
        ),
      ),
      title: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          StreamBuilder(
            stream: bloc.streamUserModel,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const CircularProgressIndicator();
              }
              if (snapshot.hasError) {
                return const Text(
                  'Lỗi khi tải thông tin người dùng',
                  style: TextStyle(color: Colors.red),
                );
              }
              if (!snapshot.hasData) {
                return const Text(
                  'Không có thông tin người dùng',
                  style: TextStyle(color: Colors.grey),
                );
              }
              return AutoSizeText(
                Storage.userModelGlobal?.fullName ?? '',
                minFontSize: 12,
                maxFontSize: 20,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
                softWrap: true,
                style: const TextStyle(color: Colors.white),
              );
            },
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 5),
            child: Row(
              children: [
                const Icon(
                  Icons.location_on_outlined,
                  color: Colors.white,
                  size: 15,
                ),
                StreamBuilder(
                  stream: bloc.streamUserModel,
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.waiting) {
                      return const CircularProgressIndicator();
                    }
                    if (snapshot.hasError) {
                      return const Text(
                        'Lỗi khi tải địa chỉ',
                        style: TextStyle(color: Colors.red),
                      );
                    }
                    if (!snapshot.hasData) {
                      return const Text(
                        'Không có địa chỉ',
                        style: TextStyle(color: Colors.grey),
                      );
                    }
                    return Expanded(
                      child: AutoSizeText(
                        '${Storage.addressModel?.street ?? ''} ${Storage.addressModel?.ward ?? ''} ${Storage.addressModel?.district ?? ''} ${Storage.addressModel?.city ?? ''}',
                        minFontSize: 8,
                        maxFontSize: 12,
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        softWrap: true,
                        style: const TextStyle(color: Colors.white),
                      ),
                    );
                  },
                ),
              ],
            ),
          ),
        ],
      ),
      appBarActions: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 5),
          child: InkWell(
            onTap: () {
              bloc.onSelectBranch(context);
              // bloc.onRefresh();
            },
            child: const Icon(Icons.house, color: Colors.white),
          ),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 5),
          child: InkWell(
            onTap: () {
              bloc.filter(context);
            },
            child: const Icon(Icons.filter_list_alt, color: Colors.white),
          ),
        ),
        const SizedBox(width: 10),
      ],
      appBarGradient: false,
      bodyBuilder: (controller) {
        final double width = AppSize.width();
        return StreamBuilder(
          stream: bloc.streamCategoryModels,
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const Center(child: CircularProgressIndicator());
            }
            if (snapshot.hasError) {
              return Center(
                child: Text('Lỗi khi tải danh mục: ${snapshot.error}'),
              );
            }
            if (!snapshot.hasData || snapshot.data!.isEmpty) {
              return Center(
                child: SizedBox(
                  height: 150,
                  child: Lottie.asset('assets/animations/loading.json'),
                ),
              );
            }

            return buildBodyContent(
              bloc: bloc,
              width: width,
              labels: snapshot.data!.map((e) => e).toList(),
              primaryButtonColor: primaryButtonColor,
              borderColor: borderColor,
            );
          },
        );
      },
      bottomBarItemsCustom: customBottomBar(bloc, bloc.indexScreen.value),
      hideBottomBarOnScroll: false,
    );
  }

  List<BottomBarItem> customBottomBar(HomeScreenBloc bloc, int selectedIndex) {
    return [
      BottomBarItem(
        icon: const Icon(Icons.notifications, color: Colors.white),
        label: 'Thông báo',
        isSelected: selectedIndex == 3,
        onTap: (index) {
          if (bloc.indexScreen.value != 3) {
            bloc.indexScreen.value = 3;
          }
        },
      ),
      BottomBarItem(
        icon: const Icon(Icons.receipt_long, color: Colors.white),
        label: 'Đơn hàng',
        onTap: (index) {
          if (bloc.indexScreen.value != 1) {
            bloc.indexScreen.value = 1;
          }
        },
        isSelected: selectedIndex == 1,
      ),
      BottomBarItem(
        key: bloc.cartIconKey1,
        icon: const Icon(Icons.shopping_cart, color: Colors.white),
        label: 'Giỏ hàng',
        isSelected: selectedIndex == 2,
        onTap: (index) {
          if (bloc.indexScreen.value != 2) {
            bloc.indexScreen.value = 2;
          }
          bloc.onGetUserCart(bcontext: context);
        },
      ),
      BottomBarItem(
        icon: const Icon(Icons.home_outlined, color: Colors.white),
        label: 'Trang chủ',
        onTap: (index) {
          if (bloc.indexScreen.value != 0) {
            bloc.indexScreen.value = 0;
          }
        },
        isSelected: selectedIndex == 0,
      ),
    ];
  }

  Widget buildBodyContent({
    required HomeScreenBloc bloc,
    required double width,
    required List<CategoryModel> labels,
    required String primaryButtonColor,
    required String borderColor,
  }) {
    return ValueListenableBuilder<int>(
      valueListenable: bloc.indexScreen,
      builder: (context, index, _) {
        return IndexedStack(
          index: index,
          children: [
            HomeTab(bloc, width, labels),
            OrderTab(bloc: bloc),
            CardTab(bloc: bloc),
            NotificationScreen(bloc),
          ],
        );
      },
    );
  }
}

final ValueNotifier<bool> isBarVisible = ValueNotifier(true);

class HomeTab extends StatefulWidget {
  final HomeScreenBloc bloc;
  final double width;
  final List<CategoryModel> labels;
  const HomeTab(this.bloc, this.width, this.labels, {super.key});
  @override
  State<HomeTab> createState() => _HomeTabState();
}

class _HomeTabState extends State<HomeTab> with AutomaticKeepAliveClientMixin {
  late final PageController promoPageController;
  bool _isDisposed = false;

  @override
  void initState() {
    super.initState();
    promoPageController = PageController();
  }

  @override
  void dispose() {
    _isDisposed = true;
    promoPageController.dispose();
    super.dispose();
  }

  @override
  bool get wantKeepAlive => true;

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return Builder(
      builder: (innerContext) {
        return widgetHome(
          widget.bloc,
          widget.width,
          widget.labels,
          promoPageController,
          innerContext, // truyền đúng context nằm trong Navigator
        );
      },
    );
  }

  Widget widgetHome(
    HomeScreenBloc bloc,
    double width,
    List<CategoryModel> labels,
    PageController promoPageController,
    BuildContext bContext,
  ) {
    final chipList =
        labels
            .map(
              (label) => Builder(
                builder:
                    (context) => InkWell(
                      onTap: () {
                        Navigator.push(
                          bContext,
                          MaterialPageRoute(
                            builder:
                                (_) => ProductListScreen(
                                  category: label,
                                  listCartItemModel: bloc.listCartItemModel,
                                ),
                          ),
                        );
                      },
                      child: Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 3),
                        child: Chip(
                          label: Text(label.name ?? ''),
                          padding: const EdgeInsets.symmetric(
                            horizontal: 3,
                            vertical: 3,
                          ),
                          backgroundColor: toHexToColor(primaryButtonColor),
                          labelStyle: const TextStyle(color: Colors.white),
                        ),
                      ),
                    ),
              ),
            )
            .toList();

    const chipWidth = 100.0;
    final maxChips = (width / chipWidth).floor();
    final dynamicChipList =
        chipList.length < maxChips
            ? [
              ...chipList,
              ...List.generate(
                maxChips - chipList.length,
                (i) => InkWell(
                  onTap: () {},
                  child: Chip(
                    label: Text('Chip ${chipList.length + i + 1}'),
                    padding: const EdgeInsets.symmetric(
                      horizontal: 6,
                      vertical: 6,
                    ),
                    backgroundColor: toHexToColor(primaryButtonColor),
                    labelStyle: const TextStyle(color: Colors.white),
                  ),
                ),
              ),
            ]
            : chipList;

    return CustomScrollView(
      slivers: [
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(10.0),
            child: customTitleCategory(
              'Mã giảm giá',
              'Xem tất cả',
              () => bloc.onTapListPromoCode(),
            ),
          ),
        ),
        SliverToBoxAdapter(
          child: StreamBuilder(
            stream: bloc.streamPromoCodeModels,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const Center(child: CircularProgressIndicator());
              }
              if (snapshot.hasError) {
                return Center(
                  child: Text('Lỗi khi tải mã giảm giá: ${snapshot.error}'),
                );
              }
              if (!snapshot.hasData || snapshot.data!.isEmpty) {
                return const Center(child: Text('Không có mã giảm giá nào'));
              }

              final promoCodes = snapshot.data!;

              return SizedBox(
                height: 200,
                child: Swiper(
                  itemCount: promoCodes.length,
                  scrollDirection: Axis.horizontal,
                  autoplay: false,
                  viewportFraction: 0.85,
                  scale: 0.9,
                  pagination: SwiperPagination(
                    alignment: Alignment.bottomCenter,
                    margin: const EdgeInsets.only(bottom: 0),
                    builder: DotSwiperPaginationBuilder(
                      activeColor: toHexToColor(primaryButtonColor),
                      color: toHexToColor(borderColor),
                      size: 8.0,
                      activeSize: 10.0,
                    ),
                  ),
                  itemBuilder: (context, index) {
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 15),
                      child: widgetCustomItemPromoCode(promoCodes[index], () {
                        if (!_isDisposed) {
                          _showPromoCodeSheet(
                            context,
                            '',
                            Container(),
                            promoCodes[index],
                          );
                        }
                      }),
                    );
                  },
                ),
              );
            },
          ),
        ),

        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: customTitleCategory(
              'Loại sản phẩm',
              'Xem tất cả',
              () => bloc.onTapListCategory(),
            ),
          ),
        ),
        buildCategoryChips(context, labels, (model) {
          bloc.onTapCategory(model);
        }),
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: customTitleCategory(
              'Sản phẩm giảm giá',
              'Xem tất cả',
              () => bloc.onTapProductSale(),
            ),
          ),
        ),

        SliverToBoxAdapter(
          child: StreamBuilder(
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
              final products = snapshot.data?.discountedProductsPage?.content;
              return MediaQuery.removePadding(
                context: context,
                removeTop: true,
                removeBottom: true,
                child: GridView.builder(
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    crossAxisSpacing: 3,
                    mainAxisSpacing: 3,
                    mainAxisExtent: 300,
                  ),
                  physics: const NeverScrollableScrollPhysics(),
                  shrinkWrap: true,
                  itemCount:
                      (products?.length ?? 0) > 20 ? 20 : (products?.length),
                  itemBuilder: (context, index) {
                    final key = bloc.imageKeys.putIfAbsent(
                      (products?[index].productId ?? 0) + 99999,
                      () => GlobalKey(),
                    );
                    return InkWell(
                      key: key,
                      onTap: () {
                        bloc.onTapProductDetail(
                          products?[index] ?? ProductModel(),
                        );
                      },
                      child: customItemProductCustomer(
                        onBuy: () {
                          bloc.onBuyNow(
                            products?[index] ?? ProductModel(),
                            context,
                          );
                        },
                        onAddToCart: () {
                          bloc.onAddToCart(
                            products?[index] ?? ProductModel(),
                            context,
                            key,
                          );
                        },
                        isDiscount: false,
                        products?[index] ?? ProductModel(),
                        width * 0.4,
                      ),
                    );
                  },
                ),
              );
            },
          ),
        ),
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: customTitleCategory(
              'Sản phẩm phổ biến',
              'Xem tất cả',
              () => bloc.onTapPopularProduct(),
            ),
          ),
        ),
        // Sản phẩm phổ biến
        SliverToBoxAdapter(
          child: StreamBuilder(
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
              final products = snapshot.data?.productsPage?.content;
              return MediaQuery.removePadding(
                context: context,
                removeTop: true,
                child: GridView.builder(
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    crossAxisSpacing: 3,
                    mainAxisSpacing: 3,
                    mainAxisExtent: 300,
                  ),
                  physics: const NeverScrollableScrollPhysics(),
                  shrinkWrap: true,
                  itemCount:
                      (products?.length ?? 0) > 20 ? 20 : products?.length,
                  itemBuilder: (context, index) {
                    final key = bloc.imageKeys.putIfAbsent(
                      (products?[index].productId ?? 0),
                      () => GlobalKey(),
                    );
                    return InkWell(
                      key: key,
                      onTap: () {
                        bloc.onTapProductDetail(
                          products?[index] ?? ProductModel(),
                        );
                      },
                      child: customItemProductCustomer(
                        onBuy: () {
                          bloc.onBuyNow(
                            products?[index] ?? ProductModel(),
                            context,
                          );
                        },
                        onAddToCart: () {
                          bloc.onAddToCart(
                            products?[index] ?? ProductModel(),
                            context,
                            key,
                          );
                        },
                        isDiscount: false,
                        products?[index] ?? ProductModel(),
                        width * 0.4,
                      ),
                    );
                  },
                ),
              );
            },
          ),
        ),
        SliverToBoxAdapter(child: SizedBox(height: 20)),
      ],
    );
  }

  void _showPromoCodeSheet(
    BuildContext context,
    String title,
    Widget bodyWidget,
    PromoCodeModel model,
  ) {
    if (_isDisposed) return;

    showCustomBottomSheet(
      context: context,
      title: model.name ?? '',
      bodyWidget: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _customTextSpan('Mã giảm giá: ', model.code ?? ''),
          const SizedBox(height: 10),
          _customTextSpan('Mô tả: ', model.description ?? ''),
          const SizedBox(height: 10),
          _customTextSpan(
            'Hạn sử dụng: ',
            '${model.startDate ?? ''} -- ${model.endDate ?? ''}',
          ),
          const SizedBox(height: 10),
          _customTextSpan(
            'Điều kiện áp dụng: ',
            'Dành cho đơn hàng có giá trị trên ${model.minimumOrderValue ?? ''}đ',
          ),
          const SizedBox(height: 10),
          _customTextSpan(
            'Giảm giá: ',
            '${model.discountPercentage.toString()}đ',
          ),
          const SizedBox(height: 50),
        ],
      ),
    );
  }

  Widget _customTextSpan(String title, String body) {
    return customTextSpan(
      title,
      body,
      TextStyle(fontSize: 14, color: toHexToColor(secondaryTextColor)),
      TextStyle(
        fontSize: 15,
        color: toHexToColor(primaryButtonColor),
        fontWeight: FontWeight.bold,
      ),
    );
  }

  Widget widgetCustomItemPromoCode(PromoCodeModel model, VoidCallback onTap) {
    return Center(
      child: InkWell(onTap: onTap, child: _customItemPromoCode(model)),
    );
  }

  Widget _customItemPromoCode(PromoCodeModel model) {
    return GestureDetector(
      child: SizedBox(
        width: AppSize.w(0.9),
        child: customItemPromoCode(model, () {}, () {}),
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

  Widget buildCategoryChips(
    BuildContext context,
    List<CategoryModel> labels,
    Function(CategoryModel model) onTap,
  ) {
    return SliverToBoxAdapter(
      child: SizedBox(
        height: 35,
        child: ListView.separated(
          scrollDirection: Axis.horizontal,
          padding: const EdgeInsets.symmetric(horizontal: 8),
          itemCount: labels.length,
          separatorBuilder: (_, __) => const SizedBox(width: 6),
          itemBuilder: (ctx, index) {
            final label = labels[index];
            return GestureDetector(
              onTap: () {
                onTap(label);
              },
              child: Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 12,
                  vertical: 8,
                ),
                decoration: BoxDecoration(
                  color: toHexToColor(primaryButtonColor),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Text(
                  label.name ?? '',
                  style: const TextStyle(color: Colors.white),
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}

class OrderTab extends StatefulWidget {
  final HomeScreenBloc bloc;
  const OrderTab({super.key, required this.bloc});
  @override
  State<OrderTab> createState() => _OrderTabState();
}

class _OrderTabState extends State<OrderTab>
    with AutomaticKeepAliveClientMixin, SingleTickerProviderStateMixin {
  late TabController _tabController;
  final List<OrderStatus> orderStatuses = OrderStatus.values;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: orderStatuses.length, vsync: this);

    _tabController.addListener(() {
      if (_tabController.indexIsChanging) return;
      final status = orderStatuses[_tabController.index];
      widget.bloc.onGetListOrderByStatus(status); // Gọi API khi đổi tab
    });

    // Gọi tab đầu tiên khi khởi tạo
    widget.bloc.onGetListOrderByStatus(orderStatuses[0]);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  bool get wantKeepAlive => true;

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      child: Column(
        children: [
          Container(
            color: toHexToColor(backgroundColor),
            child: TabBar(
              isScrollable: true,
              controller: _tabController,
              dividerHeight: 0,
              labelColor: Colors.blueGrey,
              unselectedLabelColor: Colors.black,
              indicatorColor: Colors.blueGrey,
              tabs:
                  orderStatuses
                      .map((status) => Tab(text: status.description))
                      .toList(),
            ),
          ),
          const SizedBox(height: 10),
          Expanded(
            child: TabBarView(
              controller: _tabController,
              children:
                  orderStatuses.map((status) {
                    return StreamBuilder<List<OrderResponse>>(
                      stream: getStreamByStatus(status),
                      builder: (context, snapshot) {
                        final data = snapshot.data ?? [];
                        return MediaQuery.removePadding(
                          context: context,
                          removeTop: true,
                          child: ListView.builder(
                            itemCount: data.length,
                            itemBuilder: (context, index) {
                              final order = data[index];
                              return customCardOrder(
                                AppSize.w(1),
                                order: order,
                                status: status,
                                bCOntext: context,
                                bloc: widget.bloc,
                              );
                            },
                          ),
                        );
                      },
                    );
                  }).toList(),
            ),
          ),
        ],
      ),
    );
  }

  /// Lấy stream tương ứng theo OrderStatus
  Stream<List<OrderResponse>> getStreamByStatus(OrderStatus status) {
    switch (status) {
      case OrderStatus.pending:
        return widget.bloc.streamPending.stream;
      case OrderStatus.paying:
        return widget.bloc.streamPaying.stream;
      case OrderStatus.paid:
        return widget.bloc.streamPaid.stream;
      case OrderStatus.delivering:
        return widget.bloc.streamDelivering.stream;
      case OrderStatus.shipped:
        return widget.bloc.streamShipped.stream;
      case OrderStatus.cancelling:
        return widget.bloc.streamCancelling.stream;
      case OrderStatus.cancelled:
        return widget.bloc.streamCancelled.stream;
      case OrderStatus.completed:
        return widget.bloc.streamCompleted.stream;
      case OrderStatus.failed:
        return widget.bloc.streamFailed.stream;
    }
  }

  Widget customCardOrder(
    double width, {
    required OrderResponse order,
    OrderStatus? status,
    BuildContext? bCOntext,
    HomeScreenBloc? bloc,
  }) {
    final orderCode = order.orderId;
    final total = order.grandTotal ?? 0;

    return InkWell(
      onTap: () {
        bloc?.onTapOrderDetail(bCOntext!, order);
      },
      child: Stack(
        children: [
          SizedBox(
            width: width,
            height: 150,
            child: Card(
              color: Colors.white,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  SizedBox(
                    height: 110,
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Flexible(
                          flex: 4,
                          child: Padding(
                            padding: const EdgeInsets.all(5.0),
                            child: Container(
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.only(
                                  topLeft: Radius.circular(8),
                                  topRight: Radius.circular(8),
                                ),
                                image: DecorationImage(
                                  image: AssetImage(imgOrder),
                                  fit: BoxFit.cover,
                                ),
                              ),
                            ),
                          ),
                        ),
                        Flexible(
                          flex: 6,
                          child: Padding(
                            padding: const EdgeInsets.only(left: 10),
                            child: SizedBox(
                              height: 70,
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  customAutoSizeText(
                                    14,
                                    20,
                                    'Mã đơn hàng #$orderCode',
                                    isBold: true,
                                  ),
                                  SizedBox(height: 8),
                                  Card(
                                    child: Container(
                                      padding: EdgeInsets.symmetric(
                                        vertical: 5,
                                        horizontal: 10,
                                      ),
                                      decoration: BoxDecoration(
                                        color: toHexToColor(
                                          secondaryColorOrange,
                                        ),
                                        borderRadius: BorderRadius.circular(8),
                                      ),
                                      child: customAutoSizeText(
                                        12,
                                        18,
                                        'Tổng tiền: ${formatCurrencyVN(total)}',
                                        textColor: toHexToColor(
                                          secondaryTextColor,
                                        ),
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    child: Container(
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.only(
                          bottomLeft: Radius.circular(8),
                          bottomRight: Radius.circular(8),
                        ),
                        color: toHexToColor(primaryButtonColor),
                      ),
                      height: 20,
                      child: buildOrderStatusWidget(
                        status: status ?? OrderStatus.pending,
                        bContext: bCOntext,
                        model: order,
                        bloc: bloc,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          Positioned(
            top: 4,
            right: 4,
            child: Container(
              decoration: BoxDecoration(
                color: toHexToColor(primaryErrorColor),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 2, horizontal: 5),
                child: Text(
                  order.status ?? '',
                  style: TextStyle(color: Colors.white, fontSize: 10),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget buildOrderStatusWidget({
    required OrderStatus status,
    OrderResponse? model,
    BuildContext? bContext,
    HomeScreenBloc? bloc,
  }) {
    switch (status) {
      case OrderStatus.pending:
        return _buildText('Ngày đặt hàng: ${model?.orderDate}');
      case OrderStatus.paying:
        return _buildTapText('Thanh toán', bContext, model ?? OrderResponse());
      case OrderStatus.delivering:
        return _buildText('Đơn hàng của bạn đang được vận chuyển');
      // return _buildTapText(
      //   'Đơn hàng của bạn đang được vận chuyển',
      //   bContext,
      //   model ?? OrderResponse(),
      //   isIcon: false,
      // );
      case OrderStatus.completed:
        return _buildText('Đơn hàng đã giao thành công');
      case OrderStatus.shipped:
        return _buildText('Đơn hàng đã giao thành công');
      // return _buildSuccessButtons(bContext, bloc!, model!);
      case OrderStatus.cancelling:
        return _buildText('Đơn hàng đang được hủy');
      // return _buildTapText(
      //   'Đơn hàng đang được hủy',
      //   bContext,
      //   model ?? OrderResponse(),
      //   isIcon: false,
      // );
      case OrderStatus.cancelled:
        return _buildText('Đơn hàng đã được hủy');
      // return _buildTapText(
      //   'Đơn hàng đã được hủy',
      //   bContext,
      //   model ?? OrderResponse(),
      //   isIcon: false,
      // );
      case OrderStatus.paid:
        return _buildText('Đơn hàng đang được xử lí');
      case OrderStatus.failed:
        return _buildText('Giao hàng thất bại');
      default:
        return const SizedBox(); // hoặc có thể custom thêm
    }
  }

  Widget _buildText(String text) {
    return Center(
      child: customAutoSizeText(
        12,
        18,
        text,
        textColor: Colors.white,
        isBold: true,
      ),
    );
  }

  Widget _buildTapText(
    String text,
    BuildContext? bContext,
    OrderResponse model, {
    bool isIcon = true,
  }) {
    return InkWell(
      onTap: () {
        if (bContext != null) {
          Navigator.push(
            bContext,
            MaterialPageRoute(builder: (_) => CreateOrderScreen()),
          );
        }
      },
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          customAutoSizeText(
            12,
            18,
            text,
            textColor: Colors.white,
            isBold: true,
          ),
          if (isIcon == true) ...[
            SizedBox(width: 20),
            Icon(Icons.arrow_forward_ios, color: Colors.white),
          ],
        ],
      ),
    );
  }

  Widget _buildSuccessButtons(
    BuildContext? context,
    HomeScreenBloc bloc,
    OrderResponse model,
  ) {
    return InkWell(
      onTap: () {
        // bloc.onTapOrderDetail(context!, model);
      },
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Expanded(
            flex: 5,
            child: Container(
              decoration: BoxDecoration(
                color: toHexToColor(primaryButtonColor),
                borderRadius: const BorderRadius.only(
                  bottomLeft: Radius.circular(10),
                ),
              ),
              child: Center(
                child: customAutoSizeText(
                  12,
                  18,
                  'Mua lại',
                  textColor: Colors.white,
                  isBold: true,
                ),
              ),
            ),
          ),
          Expanded(
            flex: 5,
            child: InkWell(
              onTap: () {
                // bloc.onCreateRate();
              },
              child: Container(
                decoration: const BoxDecoration(
                  color: Colors.blueGrey,
                  borderRadius: BorderRadius.only(
                    bottomRight: Radius.circular(10),
                  ),
                ),
                child: Center(
                  child: customAutoSizeText(
                    12,
                    18,
                    'Đánh giá',
                    textColor: Colors.white,
                    isBold: true,
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class CardTab extends StatelessWidget {
  final HomeScreenBloc? bloc;
  const CardTab({super.key, this.bloc});
  @override
  Widget build(BuildContext context) {
    return cardScreen();
  }

  Widget cardScreen() {
    return StreamBuilder<List<CartItemModel>>(
      stream: bloc?.streamCartItemModels,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          final data = snapshot.data ?? [];

          return StreamBuilder(
            stream: bloc?.streamCaculate,
            builder: (context, snapshot) {
              final caculate = snapshot.data;
              return MediaQuery.removePadding(
                removeTop: true,
                context: context,
                child: ListView.builder(
                  padding: const EdgeInsets.only(bottom: 100),
                  itemCount: data.length + 1,
                  itemBuilder: (context, index) {
                    if (index == 0) {
                      return Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: InkWell(
                          onTap: () {
                            bloc?.onTapCreateOrder(context);
                          },
                          child: Container(
                            width: double.infinity,
                            height: 45,
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(10),
                              color: toHexToColor(primaryButtonColor),
                            ),
                            child: Center(
                              child: Text(
                                'Tạo đơn hàng',
                                style: TextStyle(
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                          ),
                        ),
                      );
                    }
                    final item = data[index - 1]; // lùi 1 vì index 0 là nút
                    return itemCard(model: item);
                  },
                ),
              );
            },
          );
        }
        return const Center(child: Text('Không có dữ liệu....'));
      },
    );
  }

  Widget itemCard({CartItemModel? model}) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 3, vertical: 2),
      child: InkWell(
        onTap: () {
          bloc?.onTapProductDetail(model?.product ?? ProductModel());
        },
        child: Card(
          color: Colors.white,
          child: Padding(
            padding: const EdgeInsets.all(8),
            child: Stack(
              children: [
                Row(
                  children: [
                    Expanded(
                      flex: 3,
                      child: Center(
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(10),
                          // child: Image.asset(avtWomen6, fit: BoxFit.cover),
                          child: CachedNetworkImage(
                            imageUrl: model?.product?.image ?? '',
                            placeholder:
                                (context, url) => CircularProgressIndicator(),
                            errorWidget:
                                (context, url, error) =>
                                    Image.asset(imgBranch, fit: BoxFit.cover),
                            width: 100,
                            height: 100,
                            fit: BoxFit.contain,
                          ),
                        ),
                      ),
                    ),
                    Expanded(
                      flex: 6,
                      child: Padding(
                        padding: const EdgeInsets.only(left: 10),
                        child: SizedBox(
                          height: 100,
                          child: Stack(
                            children: [
                              model?.product?.discountPercentage != 0
                                  ? Positioned(
                                    top: 1,
                                    right: 1,
                                    child: Container(
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
                                          '${model?.product?.discountPercentage}%',
                                          style: TextStyle(color: Colors.white),
                                        ),
                                      ),
                                    ),
                                  )
                                  : SizedBox(),

                              Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                mainAxisSize: MainAxisSize.max,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Padding(
                                    padding: const EdgeInsets.only(right: 30),
                                    child: customAutoSizeText(
                                      14,
                                      18,
                                      model?.product?.name ?? '',
                                      isBold: true,
                                      textColor: toHexToColor(primaryTextColor),
                                    ),
                                  ),
                                  // customAutoSizeText(8, 12, '100.000đ', isLine: true),
                                  model?.product?.discountPercentage != 0
                                      ? customAutoSizeText(
                                        12,
                                        16,
                                        '${formatCurrencyVN((model?.product?.price ?? 0) * (model?.product?.discountPercentage ?? 0))}',
                                        isBold: true,
                                        isLine: true,
                                        textColor: toHexToColor(
                                          primaryButtonColor,
                                        ),
                                      )
                                      : SizedBox(),
                                  customAutoSizeText(
                                    12,
                                    16,
                                    '${formatCurrencyVN(model?.price ?? 0)}',
                                    isBold: true,
                                    textColor: toHexToColor(primaryButtonColor),
                                  ),
                                  Spacer(),
                                  Row(
                                    children: [
                                      Spacer(),
                                      Card(
                                        color: Colors.white,
                                        child: Container(
                                          decoration: BoxDecoration(
                                            color: Colors.grey[300],
                                            borderRadius: BorderRadius.circular(
                                              5,
                                            ),
                                          ),
                                          child: Row(
                                            children: [
                                              InkWell(
                                                onTap: () {
                                                  bloc?.onCaculate(
                                                    model!,
                                                    true,
                                                  );
                                                },
                                                child: Container(
                                                  padding: EdgeInsets.symmetric(
                                                    horizontal: 10,
                                                    vertical: 3,
                                                  ),
                                                  decoration: BoxDecoration(
                                                    borderRadius:
                                                        BorderRadius.only(
                                                          topLeft:
                                                              Radius.circular(
                                                                5,
                                                              ),
                                                          bottomLeft:
                                                              Radius.circular(
                                                                5,
                                                              ),
                                                        ),
                                                  ),
                                                  child: Text(' - '),
                                                ),
                                              ),
                                              Container(
                                                padding: EdgeInsets.symmetric(
                                                  horizontal: 8,
                                                  vertical: 3,
                                                ),
                                                color: Colors.white,
                                                child: Text(
                                                  model?.quantity.toString() ??
                                                      '0',
                                                ),
                                              ),
                                              InkWell(
                                                onTap: () {
                                                  bloc?.onCaculate(
                                                    model!,
                                                    false,
                                                  );
                                                },
                                                child: Container(
                                                  padding: EdgeInsets.symmetric(
                                                    horizontal: 8,
                                                    vertical: 3,
                                                  ),
                                                  decoration: BoxDecoration(
                                                    borderRadius:
                                                        BorderRadius.only(
                                                          bottomRight:
                                                              Radius.circular(
                                                                5,
                                                              ),
                                                          topRight:
                                                              Radius.circular(
                                                                5,
                                                              ),
                                                        ),
                                                    // color: Colors.white,
                                                  ),
                                                  child: Text(' + '),
                                                ),
                                              ),
                                            ],
                                          ),
                                        ),
                                      ),
                                    ],
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
                Positioned(
                  top: 1,
                  right: 1,
                  child: InkWell(
                    onTap: () {
                      // bloc?.onUpdateSelected(
                      //   (model?.selected ?? false),
                      //   model!,
                      // );
                      bloc?.onTapCartItem(model!);
                    },

                    child:
                        model?.selected == false
                            ? Icon(
                              size: 30,
                              Icons.check_box_outline_blank,
                              color: toHexToColor(primaryColorGreen),
                            )
                            : Icon(
                              size: 30,
                              Icons.check_box,
                              color: toHexToColor(primaryColorGreen),
                            ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class NotificationScreen extends StatelessWidget {
  final HomeScreenBloc bloc;
  const NotificationScreen(this.bloc, {super.key});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 2,
      child: TabBarView(
        children: [
          NotificationListTab(
            stream: bloc.streamListNotificationUnRead,
            emptyMessage: 'Không có thông báo mới',
            icon: Icons.notifications_active,
          ),
          NotificationListTab(
            stream: bloc.streamListNotificationRead,
            emptyMessage: 'Không có thông báo đã đọc',
            icon: Icons.notifications,
          ),
        ],
      ),
    );
  }
}

class NotificationListTab extends StatelessWidget {
  final BehaviorSubject<List<NotificationModel>> stream;
  final String emptyMessage;
  final IconData icon;

  const NotificationListTab({
    required this.stream,
    required this.emptyMessage,
    required this.icon,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<List<NotificationModel>>(
      stream: stream,
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return const Center(child: CircularProgressIndicator());
        }
        final data = snapshot.data!;
        if (data.isEmpty) {
          return Center(child: Text(emptyMessage));
        }

        return ListView.separated(
          itemCount: data.length,
          separatorBuilder: (_, __) => const Divider(height: 0),
          itemBuilder: (context, index) {
            final n = data[index];
            return ListTile(
              leading: Icon(icon, color: toHexToColor(primaryColorPurple)),
              title: Text(n.message ?? ''),
              subtitle: Text(
                n.message ?? '',
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              trailing: Text(
                formatDateString(
                  n.notificationDate ?? formatDateTime(DateTime.now()),
                ),
                style: const TextStyle(fontSize: 12),
              ),
              onTap: () {
                showModalBottomSheet(
                  context: context,
                  isScrollControlled: true,
                  shape: const RoundedRectangleBorder(
                    borderRadius: BorderRadius.vertical(
                      top: Radius.circular(20),
                    ),
                  ),
                  builder:
                      (_) => NotificationDetailBottomSheet(notification: n),
                );
              },
            );
          },
        );
      },
    );
  }
}
