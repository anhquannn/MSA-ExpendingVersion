
import 'package:flutter/material.dart';
import 'package:msa/feature/domain/entities/product_model.dart';

import '../../../../../core/config/base_bloc.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_notification.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';
import '../bloc/product_detail_bloc.dart';

class ProductDetailCustomerScreen extends BaseView<ProductDetailBloc> {
  const ProductDetailCustomerScreen({super.key});

  @override
  ProductDetailBloc createState() => ProductDetailBloc();

  @override
  ProductDetailBloc createBloc() => ProductDetailBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as ProductDetailBloc;
    return CustomScaffold(
      isHide: true,
      appBarLeading: iconBack( bloc.viewContext,color: Colors.black),
      bodyBuilder: (controller) {
        final double width = AppSize.width();
        return buildBodyContent(bloc: bloc, controller: controller);
      },

      bottomBarItems: [
        BottomBarItem(
          label: 'Mua ngay',
          onTap: (index) {
            index == 0
                ? print('Thêm vào giỏ hàng11')
                : print('Thêm vào giỏ hàng1');
          },
        ),
        BottomBarItem(
          label: 'Thêm vào\n giỏ hàng',
          onTap: (index) {
            print('Thêm vào giỏ hàng');
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
    return ListView(
      controller: controller,
      // padding: EdgeInsets.all(5),
      children: [
        itemImage(imgCategorySuaTam),
        const SizedBox(height: 5),
        itemProductDetail('Dưa lưới', '100.000đ', priceDiscount: '900.000đ'),
        const SizedBox(height: 5),
        itemDelivery(
          'Nhận hàng 12/12/2024 - 13/12/2024',
          'Tặng voucher 20.000 đ nếu giao sau thời gian trên',
        ),
        const SizedBox(height: 5),
        itemFeedBack('4.9', 100, () {}),
        const SizedBox(height: 5),
        itemDesc(
          bloc,
          'Dưa lưới là loại trái cây cao cấp, được yêu thích bởi vị ngọt thanh mát, thơm nhẹ và hàm lượng dinh dưỡng cao. Với lớp vỏ ngoài màu vàng cam hoặc xanh nhạt, có các đường gân lưới đặc trưng, bên trong là phần ruột màu cam hoặc xanh ngọc, mọng nước và ngọt dịu.Đặc điểm nổi bật: Nguồn gốc rõ ràng: Được trồng tại các trang trại sạch, áp dụng quy trình canh tác hữu cơ hiện đại. Giàu dưỡng chất: Cung cấp vitamin A, C, kali và chất xơ, tốt cho hệ miễn dịch, hỗ trợ tiêu hóa và làm đẹp da. An toàn - không hóa chất: Cam kết không thuốc trừ sâu, không chất bảo quản, đảm bảo an toàn cho cả gia đình. Thưởng thức đa dạng: Có thể ăn trực tiếp, làm sinh tố, salad, nước ép hoặc kết hợp trong các món tráng miệng. Trọng lượng: ~1.2kg - 1.8kg/quảBảo quản: Nơi khô ráo, thoáng mát hoặc ngăn mát tủ lạnh, dùng trong vòng 3-5 ngày sau khi cắt.',
        ),
        customDivider(
          color: toHexToColor(primaryTextColor),
          text: Text('Sản phẩm liên quan'),
        ),
        _buildGridItems(bloc.productModel),
        Container(height: 1000),
      ],
    );
  }

  Widget _buildGridItems(ProductModel model) {
    return SizedBox(
      width: AppSize.width(),
      child: SingleChildScrollView(
        child: Column(
          children: [
            SizedBox(
              width: AppSize.width(),
              child: Padding(
                padding: EdgeInsets.all(10),
                child: GridView.builder(
                  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    childAspectRatio: (AppSize.width() / 2) / (AppSize.h(0.4)),
                  ),
                  physics: NeverScrollableScrollPhysics(),
                  shrinkWrap: true,
                  itemCount: 6,
                  itemBuilder: (context, index) {
                    return SizedBox(
                      child: customItemProductCustomer(
                        model,
                        AppSize.w(1) * 0.4,
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
  }

  Widget itemDesc(ProductDetailBloc bloc, String desc) {
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

  Widget itemImage(String image) {
    return Center(
      child: Container(
        width: AppSize.w(0.95),
        height: AppSize.w(0.95),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(10),
          color: Colors.transparent,
        ),
        child: Image.asset(imgCategorySuaTam),
      ),
    );
  }

  Widget itemProductDetail(String name, String price, {String? priceDiscount}) {
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
              name ?? '',
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
                  price,
                  style: TextStyle(
                    color: toHexToColor(appBarColor),
                    fontWeight: FontWeight.bold,
                    fontSize: 18,
                  ),
                ),
                SizedBox(width: 10),
                Text(
                  priceDiscount ?? '',
                  style: TextStyle(
                    color: toHexToColor(appBarColor),
                    fontStyle: FontStyle.italic,
                    fontSize: 14,
                    decoration: TextDecoration.lineThrough,
                  ),
                ),
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
                      color: toHexToColor(primaryTextColor),
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
                      color: toHexToColor(primaryTextColor),
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
                        color: toHexToColor(primaryColorGreen),
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

  Widget showImageDialog({required ImageProvider imageProvider}) {
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
                Navigator.of(
                  context,
                  rootNavigator: true,
                ).pop(); // để đảm bảo thoát đúng dialog
              },
            ),
          ),
        ],
      ),
    );
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
        // child: customItemPromoCode(() {}, () {}),
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
