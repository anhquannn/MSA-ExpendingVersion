import 'dart:ffi';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/widget/customBottomSheet.dart';
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
      appBarLeading: Container(
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
              stream: bloc.userModel,
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
                return Image.asset(
                  (snapshot.data?.image != '')
                      ? snapshot.data!.image
                      : avtWomen1,
                  fit: BoxFit.cover,
                  width: 40,
                  height: 40,
                );
              },
            ),
          ),
        ),
      ),
      title: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          StreamBuilder(
            stream: bloc.userModel,
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
                snapshot.data?.fullName ?? '',
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
                  stream: bloc.userModel,
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
                        snapshot.data?.address ?? '',
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
              // bloc.onSearch();
              bloc.onRefresh();
            },
            child: const Icon(Icons.search, color: Colors.white),
          ),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 5),
          child: InkWell(
            onTap: () {
              bloc.onLogout();
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
          stream: bloc.categoryModels,
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
              return const Center(child: Text('Không có danh mục nào'));
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
            NotificationTab(bloc),
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
    return widgetHome(
      widget.bloc,
      widget.width,
      widget.labels,
      promoPageController,
    );
  }

  Widget widgetHome(
    HomeScreenBloc bloc,
    double width,
    List<CategoryModel> labels,
    PageController promoPageController,
  ) {
    final chipList =
        labels
            .map(
              (label) => InkWell(
                onTap: () {
                  bloc.onTapCategory(label);
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
                (i) => Chip(
                  label: Text('Chip ${chipList.length + i + 1}'),
                  padding: const EdgeInsets.symmetric(
                    horizontal: 6,
                    vertical: 6,
                  ),
                  backgroundColor: toHexToColor(primaryButtonColor),
                  labelStyle: const TextStyle(color: Colors.white),
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
            stream: bloc.promoCodeModels,
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
              return Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  SizedBox(
                    height: 180,
                    child: PageView.builder(
                      controller: promoPageController,
                      itemCount: snapshot.data!.length,
                      itemBuilder: (context, index) {
                        if (_isDisposed) return const SizedBox();
                        return widgetCustomItemPromoCode(
                          snapshot.data![index],
                          () {
                            if (!_isDisposed) {
                              _showPromoCodeSheet(
                                context,
                                '',
                                Container(),
                                snapshot.data![index],
                              );
                            }
                          },
                        );
                      },
                    ),
                  ),
                  Center(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(vertical: 10),
                      child: SmoothPageIndicator(
                        controller: promoPageController,
                        count: snapshot.data!.length,
                        effect: SlideEffect(
                          activeDotColor: toHexToColor(primaryButtonColor),
                          dotColor: toHexToColor(borderColor),
                          dotHeight: 10,
                          dotWidth: 10,
                          spacing: 4,
                        ),
                      ),
                    ),
                  ),
                ],
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
        SliverToBoxAdapter(
          child: SizedBox(
            width: width,
            child: Padding(
              padding: const EdgeInsets.all(0.0),
              child: Wrap(children: dynamicChipList),
            ),
          ),
        ),
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
        // Sản phẩm giảm giá
        SliverToBoxAdapter(
          child: StreamBuilder(
            stream: bloc.productModels,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const Center(child: CircularProgressIndicator());
              }
              if (snapshot.hasError) {
                return Center(child: Text('Lỗi: ${snapshot.error}'));
              }
              if (!snapshot.hasData || snapshot.data != null) {
                return const Center(child: Text('Không có sản phẩm'));
              }
              final products = snapshot.data?.discountedProductsPage?.content;
              return GridView.builder(
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  crossAxisSpacing: 3,
                  mainAxisSpacing: 3,
                  mainAxisExtent: 300,
                ),
                physics: const NeverScrollableScrollPhysics(),
                shrinkWrap: true,
                itemCount: products!.length > 10 ? 10 : products.length,
                itemBuilder: (context, index) {
                  final key = bloc.imageKeys.putIfAbsent(
                    products[index].productId!,
                    () => GlobalKey(),
                  );
                  return InkWell(
                    key: key,
                    onTap: () {
                      // bloc.onTapProductDetail();
                      bloc.onTapProductDetail(products[index]);
                    },
                    child: customItemProductCustomer(
                      onBuy: () {
                        bloc.onBuy(products[index],context);
                      },
                      onAddToCart: () {
                        bloc.onAddToCart(products[index], context, key);
                      },
                      isDiscount: true,
                      products[index],
                      width * 0.4,
                    ),
                  );
                },
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
            stream: bloc.productModels,
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
              return GridView.builder(
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  crossAxisSpacing: 3,
                  mainAxisSpacing: 3,
                  mainAxisExtent: 300,
                ),
                physics: const NeverScrollableScrollPhysics(),
                shrinkWrap: true,
                itemCount: products!.length > 20 ? 20 : products.length,
                itemBuilder: (context, index) {
                  final key = bloc.imageKeys.putIfAbsent(
                    products[index].productId!,
                    () => GlobalKey(),
                  );
                  return InkWell(
                    key: key,
                    onTap: () {
                      // bloc.onTapProductDetail();
                      bloc.onTapProductDetail(products[index]);
                    },
                    child: customItemProductCustomer(
                      onBuy: () {
                        bloc.onBuy(products[index],context);
                      },
                      onAddToCart: () {
                        bloc.onAddToCart(products[index], context, key);
                      },
                      isDiscount: false,
                      products[index],
                      width * 0.4,
                    ),
                  );
                },
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
}

class OrderTab extends StatefulWidget {
  final HomeScreenBloc bloc;
  const OrderTab({super.key, required this.bloc});
  @override
  State<OrderTab> createState() => _OrderTabState();
}

class _OrderTabState extends State<OrderTab>
    with AutomaticKeepAliveClientMixin {
  late final ScrollController _tab1Controller;
  late final ScrollController _tab2Controller;
  late final ScrollController _tab3Controller;
  double _lastOffset1 = 0, _lastOffset2 = 0, _lastOffset3 = 0;

  // Store the listener functions
  late final VoidCallback _tab1Listener;
  late final VoidCallback _tab2Listener;
  late final VoidCallback _tab3Listener;

  @override
  void initState() {
    super.initState();
    _tab1Controller = ScrollController();
    _tab2Controller = ScrollController();
    _tab3Controller = ScrollController();

    // Create the listener functions
    _tab1Listener = () => _onScroll(_tab1Controller, 1);
    _tab2Listener = () => _onScroll(_tab2Controller, 2);
    _tab3Listener = () => _onScroll(_tab3Controller, 3);

    // Add the listeners
    _tab1Controller.addListener(_tab1Listener);
    _tab2Controller.addListener(_tab2Listener);
    _tab3Controller.addListener(_tab3Listener);
  }

  void _onScroll(ScrollController controller, int tab) {
    if (!mounted) return;

    double lastOffset =
        tab == 1
            ? _lastOffset1
            : tab == 2
            ? _lastOffset2
            : _lastOffset3;

    if (controller.offset > lastOffset && controller.offset > 50) {
      isBarVisible.value = false;
    } else if (controller.offset < lastOffset) {
      isBarVisible.value = true;
    }

    if (tab == 1) _lastOffset1 = controller.offset;
    if (tab == 2) _lastOffset2 = controller.offset;
    if (tab == 3) _lastOffset3 = controller.offset;
  }

  @override
  void dispose() {
    // Remove the stored listener functions
    _tab1Controller.removeListener(_tab1Listener);
    _tab2Controller.removeListener(_tab2Listener);
    _tab3Controller.removeListener(_tab3Listener);

    _tab1Controller.dispose();
    _tab2Controller.dispose();
    _tab3Controller.dispose();
    super.dispose();
  }

  @override
  bool get wantKeepAlive => true;
  @override
  Widget build(BuildContext context) {
    super.build(context);
    return buildFourTabBar();
  }

  Widget buildFourTabBar() {
    return DefaultTabController(
      length: 3,
      child: Column(
        children: [
          Container(
            color: toHexToColor(backgroundColor),
            child: const TabBar(
              dividerHeight: 0,
              labelColor: Colors.blueGrey,
              unselectedLabelColor: Colors.black,
              indicatorColor: Colors.blueGrey,
              tabs: [
                Tab(text: "Đang xử lí"),
                Tab(text: "Đang giao"),
                Tab(text: "Hoàn tất"),
              ],
            ),
          ),
          const SizedBox(height: 10),
          Expanded(
            child: TabBarView(
              children: [
                ListView(
                  controller: _tab1Controller,
                  children: [
                    customCardOrder(AppSize.w(0.9), isPending: true),
                    customCardOrder(AppSize.w(0.9), isPending: true),
                    customCardOrder(AppSize.w(0.9), isPending: true),
                  ],
                ),
                ListView(
                  controller: _tab2Controller,
                  children: [
                    customCardOrder(AppSize.w(0.9), isDelivering: true),
                    customCardOrder(AppSize.w(0.9), isDelivering: true),
                    customCardOrder(AppSize.w(0.9), isDelivering: true),
                  ],
                ),
                ListView(
                  controller: _tab3Controller,
                  children: [
                    customCardOrder(AppSize.w(0.9), isSuccess: true),
                    customCardOrder(AppSize.w(0.9), isSuccess: true),
                    customCardOrder(AppSize.w(0.9), isSuccess: true),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget customCardOrder(
    double width, {
    bool isPending = false,
    bool isDelivering = false,
    bool isSuccess = false,
  }) {
    return Padding(
      padding: EdgeInsets.all(10.0),
      child: SizedBox(
        width: width,
        height: 200,
        child: Card(
          color: Colors.white,
          child: Padding(
            padding: const EdgeInsets.only(top: 10),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Flexible(
                  flex: 8,
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
                                topLeft: Radius.circular(10),
                                topRight: Radius.circular(10),
                              ),
                              image: DecorationImage(
                                image: AssetImage(imgCategoryBotGiat),
                                fit:
                                    BoxFit
                                        .cover, // Quan trọng để ảnh chiếm toàn bộ
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
                            height: 100, // Đảm bảo Stack có height xác định
                            child: Stack(
                              children: [
                                Positioned(
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
                                        // '${model?.discountPercentage}%',
                                        '',
                                        style: TextStyle(color: Colors.white),
                                      ),
                                    ),
                                  ),
                                ),
                                Column(
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  // mainAxisSize: MainAxisSize.min,
                                  children: [
                                    customAutoSizeText(
                                      14,
                                      20,
                                      'Mã đơn hàng #122222',
                                      isBold: true,
                                    ),
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
                                          borderRadius: BorderRadius.circular(
                                            10,
                                          ),
                                        ),
                                        child: customAutoSizeText(
                                          12,
                                          18,
                                          'Tổng sản phẩm: 3',
                                          textColor: toHexToColor(
                                            secondaryTextColor,
                                          ),
                                        ),
                                      ),
                                    ),
                                    Card(
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 5,
                                          horizontal: 10,
                                        ),
                                        decoration: BoxDecoration(
                                          color: toHexToColor(
                                            secondaryColorPurple,
                                          ),
                                          borderRadius: BorderRadius.circular(
                                            10,
                                          ),
                                        ),
                                        child: buildSuccessBody(
                                          isSuccess: isSuccess,
                                        ),
                                      ),
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
                ),
                Flexible(
                  flex: 2,
                  child: customBottomCard(
                    isPending: isPending,
                    isSuccess: isSuccess,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget buildSuccessBody({bool isSuccess = false}) {
    return !isSuccess
        ? Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            customAutoSizeText(
              12,
              18,
              'Tổng tiền: 100.000đ',
              textColor: toHexToColor(secondaryTextColor),
              maxLine: 1,
            ),
            customAutoSizeText(
              12,
              18,
              'Tổng tiền: 100.000đ',
              textColor: toHexToColor(secondaryTextColor),
              maxLine: 1,
            ),
            customAutoSizeText(
              12,
              18,
              'Tổng tiền: 100.000đ',
              textColor: toHexToColor(secondaryTextColor),
              maxLine: 1,
            ),
          ],
        )
        : Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            customAutoSizeText(
              12,
              18,
              'Tổng tiền: 100.000đ',
              textColor: toHexToColor(secondaryTextColor),
              maxLine: 1,
            ),
          ],
        );
  }

  Widget customBottomCard({bool isPending = false, bool isSuccess = false}) {
    return isPending
        ? Row(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Expanded(
              flex: 7,
              child: Container(
                decoration: BoxDecoration(
                  color: toHexToColor(primaryButtonColor),
                  borderRadius: BorderRadius.only(
                    bottomLeft: Radius.circular(10),
                    bottomRight:
                        isPending ? Radius.circular(0) : Radius.circular(10),
                  ),
                ),
                child: Center(
                  child: customAutoSizeText(
                    12,
                    18,
                    'Tổng tiền: 100.000đ',
                    textColor: Colors.white,
                    isBold: true,
                  ),
                ),
              ),
            ),
            isPending
                ? Expanded(
                  flex: 3,
                  child: Container(
                    decoration: BoxDecoration(
                      color: Colors.blueGrey,
                      borderRadius: BorderRadius.only(
                        bottomRight: Radius.circular(10),
                      ),
                    ),

                    child: Center(
                      child: customAutoSizeText(
                        12,
                        18,
                        'Trả hàng',
                        textColor: Colors.white,
                        isBold: true,
                      ),
                    ),
                  ),
                )
                : Container(
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.only(
                      bottomRight: Radius.circular(10),
                    ),
                  ),
                ),
          ],
        )
        : isSuccess
        ? Row(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Expanded(
              flex: 5,
              child: Container(
                decoration: BoxDecoration(
                  color: toHexToColor(primaryButtonColor),
                  borderRadius: BorderRadius.only(
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
              child: Container(
                decoration: BoxDecoration(
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
          ],
        )
        : Row(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Expanded(
              child: Container(
                decoration: BoxDecoration(
                  color: toHexToColor(primaryButtonColor),
                  borderRadius: BorderRadius.only(
                    bottomLeft: Radius.circular(10),
                    bottomRight: Radius.circular(10),
                  ),
                ),
                child: Center(
                  child: customAutoSizeText(
                    12,
                    18,
                    'Đơn hàng sẽ được giao trong 12/12/2020',
                    textColor: Colors.white,
                    isBold: true,
                  ),
                ),
              ),
            ),
          ],
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
      stream: bloc?.listCartItemModels,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          final data = snapshot.data;
          return ListView.builder(
            itemCount: data?.length ?? 0,
            itemBuilder: (context, index) {
              return itemCard(model: data?[index]);
            },
          );
        }
        return Center(child: Text('Không có dữ liệu....'));
      },
    );
  }

  Widget itemCard({
    String? img,
    String? name,
    String? price,
    CartItemModel? model,
    PromoCodeModel? promoCodeModel,
  }) {
    String discount =
        (model?.product?.currentPrice != null &&
                model?.product?.price != null &&
                model!.product!.price != 0)
            ? (100 -
                    ((model.product!.currentPrice! / model.product!.price!) *
                        100))
                .toStringAsFixed(0)
            : '0';
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 3, vertical: 2),
      child: Card(
        color: Colors.white,
        child: Padding(
          padding: const EdgeInsets.all(10),
          child: SizedBox(
            height: 100,
            child: Row(
              children: [
                Expanded(
                  flex: 4,
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
                      fit: BoxFit.cover,
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
                          discount != '0'
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
                                      '$discount%',
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
                              customAutoSizeText(
                                14,
                                18,
                                model?.product?.name ?? '',
                                isBold: true,
                                textColor: toHexToColor(primaryTextColor),
                              ),
                              // customAutoSizeText(8, 12, '100.000đ', isLine: true),
                              model?.product?.currentPrice !=
                                      model?.product?.price
                                  ? customAutoSizeText(
                                    12,
                                    16,
                                    '${model?.product?.price.toString() ?? ''}đ',
                                    isBold: true,
                                    isLine: true,
                                    textColor: toHexToColor(primaryButtonColor),
                                  )
                                  : SizedBox(),
                              customAutoSizeText(
                                12,
                                16,
                                '${model?.product?.currentPrice.toString() ?? ''}đ',
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
                                        borderRadius: BorderRadius.circular(5),
                                      ),
                                      child: Row(
                                        children: [
                                          InkWell(
                                            onTap: () {
                                              bloc?.onMinus(0);
                                            },
                                            child: Container(
                                              padding: EdgeInsets.symmetric(
                                                horizontal: 10,
                                                vertical: 3,
                                              ),
                                              decoration: BoxDecoration(
                                                borderRadius: BorderRadius.only(
                                                  topLeft: Radius.circular(5),
                                                  bottomLeft: Radius.circular(
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
                                              model?.quantity.toString() ?? '0',
                                            ),
                                          ),
                                          InkWell(
                                            onTap: () {
                                              bloc?.onPlus(0);
                                            },
                                            child: Container(
                                              padding: EdgeInsets.symmetric(
                                                horizontal: 8,
                                                vertical: 3,
                                              ),
                                              decoration: BoxDecoration(
                                                borderRadius: BorderRadius.only(
                                                  bottomRight: Radius.circular(
                                                    5,
                                                  ),
                                                  topRight: Radius.circular(5),
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
          ),
        ),
      ),
    );
  }
}

class NotificationTab extends StatelessWidget {
  final HomeScreenBloc bloc;
  const NotificationTab(this.bloc, {super.key});
  @override
  Widget build(BuildContext context) {
    return notificationScreen();
  }

  Widget notificationScreen() {
    return ListView(
      children: [
        buildItemNotification(
          'Bạn có đơn hàng mới',
          'Đơn hàng của bạn sẽ được giao sau 2 ngày. Vui lòng chú ý điện thoại.',
          isRead: false,
        ),
        buildItemNotification(
          'Bạn có đơn hàng mới',
          'Đơn hàng của bạn sẽ được giao sau 2 ngày. Vui lòng chú ý điện thoại.',
          isRead: false,
        ),
        buildItemNotification(
          'Bạn có đơn hàng mới',
          'Đơn hàng của bạn sẽ được giao sau 2 ngày. Vui lòng chú ý điện thoại.',
          isRead: false,
        ),
        buildItemNotification(
          'Bạn có đơn hàng mới',
          'Đơn hàng của bạn sẽ được giao sau 2 ngày. Vui lòng chú ý điện thoại.',
          isRead: false,
        ),
      ],
    );
  }
}
