import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/presentation/customer/branch_list/bloc/branch_list_bloc.dart';
import 'package:msa/feature/presentation/customer/createorder/bloc/create_order_bloc.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';

import '../../../../../core/config/base_bloc.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';

class BranchListScreen extends BaseView<BranchListBloc> {
  BranchListScreen({super.key});

  @override
  BranchListBloc createState() => BranchListBloc();

  @override
  BranchListBloc createBloc() => BranchListBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as BranchListBloc;
    return CustomScaffold(
      isHide: false,
      centerTitle: true,
      title: Text('Chọn Chi Nhánh', style: TextStyle(color: Colors.white)),
      appBarLeading: iconBack(bloc.viewContext, color: Colors.white),
      bodyBuilder: (controller) {
        return buildBodyContent(bloc: bloc, controller: controller);
      },
      floatActionButton: SizedBox(
        width: MediaQuery.of(context).size.width,
        height: 45, // Chiều cao tùy bạn
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: toHexToColor(appBarColor),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
              ),
            ),
            onPressed: () {
              // Xử lý khi nhấn nút
            },
            child: const Text(
              'Xác nhận chi nhánh',
              style: TextStyle(fontSize: 16),
            ),
          ),
        ),
      ),

      hideBottomBarOnScroll: true,
    );
  }

  Widget buildBodyContent({
    required BranchListBloc bloc,
    required ScrollController controller,
  }) {
    return CustomScrollView(
      controller: controller,
      slivers: [
        SliverToBoxAdapter(
          child: StreamBuilder<List<BranchModel>>(
            stream: bloc.branchModel.stream,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const Center(child: CircularProgressIndicator());
              }

              if (snapshot.hasError) {
                return Center(child: Text('Lỗi: ${snapshot.error}'));
              }

              final branches = snapshot.data ?? [];

              return ListView.builder(
                itemCount: branches.length,
                itemBuilder: (context, index) {
                  final model = branches[index];
                  return itemBranch(
                    context,
                    model,
                    selectedBranchId: bloc.selectedBranchId,
                    onChanged: (value) {
                      bloc.onChangeBranch(value ?? 1);
                    },
                  );
                },
              );
            },
          ),
        ),

        const SliverToBoxAdapter(child: SizedBox(height: 5)),
      ],
    );
  }

  Widget itemBranch(
    BuildContext context,
    BranchModel model, {
    required int selectedBranchId,
    required Function(int?) onChanged,
  }) {
    return Card(
      elevation: 3,
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: Container(
        padding: const EdgeInsets.all(12),
        width: MediaQuery.sizeOf(context).width,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(10),
          color: Colors.white,
        ),
        child: Row(
          children: [
            // Radio button (chiếm 1 phần)
            Expanded(
              flex: 1,
              child: Radio<int>(
                value: model.branchId ?? -1,
                groupValue: selectedBranchId,
                onChanged: onChanged,
              ),
            ),

            // Thông tin chi nhánh (chiếm 2 phần)
            Expanded(
              flex: 2,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    model.name ?? 'Chi nhánh không tên',
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 6),
                  Text("Số điện thoại: ${model.phone ?? 'Không có'}"),
                  const SizedBox(height: 4),
                  Text(
                    "Địa chỉ: ${[model.street, model.ward, model.district, model.city].where((e) => e != null && e.isNotEmpty).join(', ')}",
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _paymentList(CreateOrderBloc bloc) {
    return itemPayment(() {}, 'Zalo Pay', bloc);
  }

  Widget itemPayment(VoidCallback onTap, String text, CreateOrderBloc bloc) {
    return Card(
      color: Colors.white,
      child: Container(
        width: AppSize.w(0.9),
        height: 100,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: toHexToColor(borderColor), width: 1),
        ),
        child: Row(
          children: [
            Flexible(
              flex: 2,
              child: Padding(
                padding: EdgeInsetsDirectional.all(5),
                child: Image.asset(imgBranch),
              ),
            ),
            Flexible(
              flex: 6,
              child: Padding(
                padding: EdgeInsets.all(10),
                child: Center(child: Text(text)),
              ),
            ),
            Spacer(),
            Flexible(
              flex: 2,
              child: InkWell(
                onTap: () {
                  // TODO: Xử lý khi nhấn
                },
                borderRadius: BorderRadius.circular(20),
                child: Container(
                  width: 24,
                  height: 24,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    border: Border.all(
                      color: Colors.blue, // màu viền
                      width: 2,
                    ),
                    color:
                        bloc.isZaloPaySelected
                            ? Colors.blue
                            : Colors.transparent, // màu nền khi chọn
                  ),
                  child:
                      bloc.isZaloPaySelected
                          ? Center(
                            child: Container(
                              width: 12,
                              height: 12,
                              decoration: BoxDecoration(
                                shape: BoxShape.circle,
                                color: Colors.white, // màu chấm bên trong
                              ),
                            ),
                          )
                          : null,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _divider(String text) {
    return customDivider(
      text: Text(text, style: TextStyle(color: toHexToColor(primaryTextColor))),
      color: toHexToColor(borderColor),
    );
  }

  Widget itemInfomation(CreateOrderBloc bloc) {
    return Card(
      color: Colors.white,
      child: Container(
        width: AppSize.w(0.95),
        height: 100,
        decoration: BoxDecoration(borderRadius: BorderRadius.circular(10)),
        child: Row(
          children: [
            Flexible(
              flex: 2,
              child: Container(
                width: 50,
                height: 50,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: Colors.grey[200], // Màu nền tùy chọn
                ),
                child: ClipOval(
                  child:
                      (userModelGlobal?.image != null &&
                              userModelGlobal?.image != '')
                          ? CachedNetworkImage(
                            imageUrl: userModelGlobal!.image,
                            placeholder:
                                (context, url) => Center(
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2,
                                  ),
                                ),
                            errorWidget:
                                (context, url, error) =>
                                    Image.asset(imgBranch, fit: BoxFit.cover),
                            width: 60,
                            height: 60,
                            fit: BoxFit.cover,
                          )
                          : Image.asset(
                            avtWomen3,
                            width: 60,
                            height: 60,
                            fit: BoxFit.cover,
                          ),
                ),
              ),
            ),
            Flexible(
              flex: 7,
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      userModelGlobal?.fullName ?? '',
                      overflow: TextOverflow.ellipsis,
                      maxLines: 2,
                      style: TextStyle(
                        color: toHexToColor(primaryTextColor),
                        fontSize: 18,
                      ),
                    ),
                    Text(
                      userModelGlobal?.address ?? '',
                      overflow: TextOverflow.ellipsis,
                      maxLines: 2,
                      style: TextStyle(
                        color: toHexToColor(primaryTextColor),
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ),
            ),
            Spacer(),
            Flexible(
              flex: 1,
              child: InkWell(
                child: Icon(Icons.arrow_forward_ios, color: Colors.black),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget listItemOrder(CreateOrderBloc bloc) {
    return SizedBox(
      width: AppSize.w(0.9),
      child: ListView.builder(
        itemCount: 10,
        shrinkWrap: true,
        physics: NeverScrollableScrollPhysics(),
        itemBuilder: (context, index) {
          return _itemCard(model: mockCartItem, bloc: bloc);
        },
      ),
    );
  }

  Widget _itemCard({CartItemModel? model, CreateOrderBloc? bloc}) {
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
