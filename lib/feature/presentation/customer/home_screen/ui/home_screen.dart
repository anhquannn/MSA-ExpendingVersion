import 'dart:math';
import 'dart:ui';

import 'package:curved_labeled_navigation_bar/curved_navigation_bar.dart';
import 'package:curved_labeled_navigation_bar/curved_navigation_bar_item.dart';
import 'package:flutter/material.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';
import 'package:auto_size_text/auto_size_text.dart';

import '../../../../../core/config/base_bloc.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_dropshadow.dart';
import '../../../../../widget/custom_item_promocode.dart';
import '../../../../../widget/custom_notification.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';
import '../bloc/home_screen_bloc.dart';

class HomeScreen extends BaseView<HomeScreenBloc> {
  HomeScreen({super.key});

  @override
  HomeScreenBloc createState() => HomeScreenBloc();

  @override
  HomeScreenBloc createBloc() => HomeScreenBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as HomeScreenBloc;
    return CustomScaffold(
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
            child: Image.asset(
              avtWomen1,
              fit: BoxFit.cover,
              width: 40,
              height: 40,
            ),
          ),
        ),
      ),
      title: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          AutoSizeText(
            'Nguyen Van A',
            minFontSize: 12,
            maxFontSize: 20,
            style: const TextStyle(color: Colors.white),
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
                AutoSizeText(
                  '123 Cao Lỗ TP.HCM',
                  minFontSize: 8,
                  maxFontSize: 12,
                  style: const TextStyle(color: Colors.white),
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
              _bloc.onSetting();
            },
            child: const Icon(Icons.search, color: Colors.white),
          ),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 5),
          child: InkWell(
            onTap: () {
              _bloc.onLogout();
            },
            child: const Icon(Icons.filter_list_alt, color: Colors.white),
          ),
        ),
        const SizedBox(width: 10),
      ],
      appBarGradient: false,
      bodyBuilder: (controller) {
        final double width = AppSize.width();
        return buildBodyContent(
          bloc: _bloc,
          controller: controller,
          width: width,
          labels: [
            "La",
            "Lab",
            "Label 3",
            "Label 3",
            "Label 3",
            "Label 3",
            "Label 3",
            "Label 3",
            "Label 3",
          ],
          categoryController: _bloc.categoryController,
          primaryButtonColor: primaryButtonColor,
          borderColor: borderColor,
          selectedIndex: _bloc.indexScreen.value,
        );
      },
      bottomBarItemsCustom: customBottomBar(_bloc),
      hideBottomBarOnScroll: true,
    );
  }

  List<BottomBarItem> customBottomBar(HomeScreenBloc bloc) {
    return [
      // BottomBarItem(
      //   icon: const Icon(Icons.person, color: Colors.white),
      //   label: 'Tài khoản',
      //   onTap: (index) {
      //     bloc.indexScreen.value = index;
      //     print('@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   Tài khoản: $index');
      //   },
      // ),
      BottomBarItem(
        icon: const Icon(Icons.receipt_long, color: Colors.white),
        label: 'Đơn hàng',
        onTap: (index) {
          bloc.indexScreen.value = index;
          print('@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   Đơn hàng: $index');
        },
      ),
      BottomBarItem(
        icon: const Icon(Icons.home_outlined, color: Colors.white),
        label: 'Trang chủ',
        onTap: (index) {
          bloc.indexScreen.value = index;
          print('@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   Trang chủ: $index');
        },
      ),
      BottomBarItem(
        icon: const Icon(Icons.shopping_cart, color: Colors.white),
        label: 'Giỏ hàng',
        onTap: (index) {
          bloc.indexScreen.value = index;
          print('@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   Giỏ hàng: $index');
        },
      ),
      BottomBarItem(
        icon: const Icon(Icons.notifications, color: Colors.white),
        label: 'Thông báo',
        onTap: (index) {
          bloc.indexScreen.value = index;
          print('@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   Vận chuyển: $index');
        },
      ),
    ];
  }

  Widget buildCustomBottomBarAnimation({
    required bool isVisible,
    required int selectedIndex,
    required List<BottomBarItem> items,
    required bool isGradient,
  }) {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 200),
      height: isVisible ? kBottomNavigationBarHeight : 0,
      curve: Curves.easeInOut,
      child: AnimatedOpacity(
        opacity: isVisible && items.isNotEmpty ? 1.0 : 0.0,
        duration: const Duration(milliseconds: 200),
        child:
            isVisible && items.isNotEmpty
                ? CurvedNavigationBar(
                  color: toHexToColor(appBarColor),
                  backgroundColor: Colors.transparent,
                  index: selectedIndex,
                  animationDuration: const Duration(milliseconds: 400),
                  buttonBackgroundColor: toHexToColor(appBarColor),
                  items:
                      items
                          .asMap()
                          .entries
                          .map(
                            (entry) => CurvedNavigationBarItem(
                              child: entry.value.icon,
                              label: entry.value.label,
                              labelStyle: const TextStyle(
                                color: Colors.white,
                                overflow: TextOverflow.ellipsis,
                                fontSize: 12,
                              ),
                            ),
                          )
                          .toList(),
                  onTap: (index) {
                    if (index >= 0 && index < items.length) {
                      items[index].onTap(index);
                    }
                  },
                )
                : null,
      ),
    );
  }

  Widget buildBodyContent({
    required HomeScreenBloc bloc,
    required ScrollController controller,
    required double width,
    required List<String> labels,
    required PageController categoryController,
    required String primaryButtonColor,
    required String borderColor,
    required int selectedIndex, // Thêm selectedIndex
  }) {
    // Nội dung cho từng mục
    final contents = [
      buildFourTabBar(controller),
      widgetHome(controller, categoryController, width, labels),
      cardScreen(controller),
      notificationScreen(controller),
    ];

    return contents[selectedIndex];
  }

  Widget buildFourTabBar(ScrollController controller) {
    return DefaultTabController(
      length: 3, // Số lượng tab (đã sửa thành 3 vì chỉ có 3 tab)
      child: Column(
        children: [
          Container(
            color: toHexToColor(backgroundColor), // Hàm này cần được định nghĩa
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
                // Tab 1
                SizedBox(
                  width: AppSize.width(),
                  child: ListView(
                    controller: controller,
                    children: [
                      SizedBox(
                        width: AppSize.width(),
                        child: customCardOrder(AppSize.w(0.9), isPending: true),
                      ),
                      SizedBox(
                        width: AppSize.width(),
                        child: customCardOrder(AppSize.w(0.9), isPending: true),
                      ),
                      SizedBox(
                        width: AppSize.width(),
                        child: customCardOrder(AppSize.w(0.9), isPending: true),
                      ),
                    ],
                  ),
                ),
                //"Sản phẩm giảm giá
                SizedBox(
                  width: AppSize.width(),
                  child: ListView(
                    controller: controller,
                    children: [
                      SizedBox(
                        width: AppSize.width(),
                        child: customCardOrder(
                          AppSize.w(0.9),
                          isDelivering: true,
                        ),
                      ),
                      SizedBox(
                        width: AppSize.width(),
                        child: customCardOrder(
                          AppSize.w(0.9),
                          isDelivering: true,
                        ),
                      ),
                      SizedBox(
                        width: AppSize.width(),
                        child: customCardOrder(
                          AppSize.w(0.9),
                          isDelivering: true,
                        ),
                      ),
                    ],
                  ),
                ),
                SizedBox(
                  width: AppSize.width(),
                  child: ListView(
                    controller: controller,
                    children: [
                      SizedBox(
                        width: AppSize.width(),
                        child: customCardOrder(AppSize.w(0.9), isSuccess: true),
                      ),
                      SizedBox(
                        width: AppSize.width(),
                        child: customCardOrder(AppSize.w(0.9), isSuccess: true),
                      ),
                      SizedBox(
                        width: AppSize.width(),
                        child: customCardOrder(AppSize.w(0.9), isSuccess: true),
                      ),
                    ],
                  ),
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
                Expanded(
                  flex: 8,
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Expanded(
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
                      Expanded(
                        flex: 6,
                        child: Padding(
                          padding: const EdgeInsets.only(left: 10),
                          child: Container(
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.only(
                                topLeft: Radius.circular(10),
                                topRight: Radius.circular(10),
                              ),
                            ),
                            child: Column(
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
                                      color: toHexToColor(secondaryColorOrange),
                                      borderRadius: BorderRadius.circular(10),
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
                                      color: toHexToColor(secondaryColorPurple),
                                      borderRadius: BorderRadius.circular(10),
                                    ),
                                    child: buildSuccessBody(
                                      isSuccess: isSuccess,
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

  Widget cardScreen(ScrollController controller) {
    return ListView(
      controller: controller,
      children: [itemCard(), itemCard(), itemCard(), itemCard(), itemCard()],
    );
  }

  Widget notificationScreen(ScrollController controller) {
    return ListView(
      controller: controller,
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

  Widget widgetHome(
    ScrollController controller,
    PageController categoryController,
    double width,
    List<String> labels,
  ) {
    final chipList =
        labels
            .map(
              (label) => Chip(
                label: Text(label),
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 6),
                backgroundColor: toHexToColor(primaryButtonColor),
                labelStyle: const TextStyle(color: Colors.white),
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
    return ListView(
      controller: controller,
      children: [
        Padding(
          padding: const EdgeInsets.all(10.0),
          child: customTitleCategory('Mã giảm giá', 'Xem tất cả'),
        ),
        SizedBox(
          height: 180,
          child: PageView.builder(
            controller: categoryController,
            itemCount: 5,
            itemBuilder: (context, index) {
              return widgetCustomItemPromoCode(
                () {},
                () {},
                categoryController,
                index,
              );
            },
          ),
        ),
        Center(
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 10),
            child: SmoothPageIndicator(
              controller: categoryController,
              count: 10,
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
        Padding(
          padding: const EdgeInsets.all(10),
          child: customTitleCategory('Loại sản phẩm', 'Xem tất cả'),
        ),
        SizedBox(
          width: width,
          child: Padding(
            padding: const EdgeInsets.all(0.0),
            child: Wrap(children: dynamicChipList),
          ),
        ),
        Padding(
          padding: const EdgeInsets.all(10),
          child: customTitleCategory('Sản phẩm giảm giá', 'Xem tất cả'),
        ),
        SizedBox(
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
                return SizedBox(child: customItemProductCustomer(width * 0.4));
              },
            ),
          ),
        ),

        Padding(
          padding: const EdgeInsets.all(10),
          child: customTitleCategory('Sản phẩm phổ biến', 'Xem tất cả'),
        ),
        SizedBox(
          width: width,
          child: Padding(
            padding: EdgeInsets.all(10),
            child: GridView.builder(
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                crossAxisSpacing: 3,
                mainAxisSpacing: 3,
                childAspectRatio: (width / 2) / (AppSize.h(0.4)),
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
    );
  }

  Widget customTitleCategory(String title, String actionText) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          title,
          style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
        ),
        Text(
          actionText,
          style: const TextStyle(fontSize: 12, color: Colors.blue),
        ),
      ],
    );
  }

  Widget widgetCustomItemPromoCode(
    VoidCallback onTap1,
    VoidCallback onTap2,
    PageController controller,
    int index,
  ) {
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

  Widget itemCard({String? img, String? name, String? price}) {
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
                    child: Image.asset(avtWomen6, fit: BoxFit.cover),
                  ),
                ),
                Expanded(
                  flex: 6,
                  child: Padding(
                    padding: const EdgeInsets.only(left: 10),
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
                                '10%',
                                style: TextStyle(color: Colors.white),
                              ),
                            ),
                          ),
                        ),
                        Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          mainAxisSize: MainAxisSize.max,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            customAutoSizeText(
                              14,
                              18,
                              'Dưa lưới',
                              isBold: true,
                              textColor: toHexToColor(primaryTextColor),
                            ),
                            customAutoSizeText(8, 12, '100.000đ', isLine: true),
                            customAutoSizeText(
                              12,
                              16,
                              '90.000đ',
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
                                      // color: toHexToColor(primaryButtonColor),
                                      color: Colors.grey[300],
                                      borderRadius: BorderRadius.circular(5),
                                      // border: Border.all(
                                      //   color: toHexToColor(primaryButtonColor),
                                      // ),
                                    ),
                                    child: Row(
                                      children: [
                                        InkWell(
                                          child: Container(
                                            padding: EdgeInsets.symmetric(
                                              horizontal: 10,
                                              vertical: 3,
                                            ),
                                            decoration: BoxDecoration(
                                              borderRadius: BorderRadius.only(
                                                topLeft: Radius.circular(5),
                                                bottomLeft: Radius.circular(5),
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
                                          child: Text('100'),
                                        ),
                                        InkWell(
                                          child: Container(
                                            padding: EdgeInsets.symmetric(
                                              horizontal: 8,
                                              vertical: 3,
                                            ),
                                            decoration: BoxDecoration(
                                              borderRadius: BorderRadius.only(
                                                bottomRight: Radius.circular(5),
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
              ],
            ),
          ),
        ),
      ),
    );
  }
}
