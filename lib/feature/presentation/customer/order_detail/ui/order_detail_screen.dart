import 'dart:math';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/response/get_order_response_model.dart';
import 'package:msa/feature/data/model/response/order_detail_response_model.dart';
import 'package:msa/feature/domain/entities/order_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/presentation/admin/product/product_detail/ui/product_detail_screen.dart';
import 'package:msa/feature/presentation/customer/order_detail/bloc/order_detail_bloc.dart';
import 'package:msa/feature/presentation/customer/product_detail/ui/product_detail_screen.dart';
import 'package:msa/widget/customBottomSheet.dart';
import 'package:msa/widget/custom_button.dart';
import 'package:msa/widget/custom_item_promocode.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

class OrderDetailScreen extends BaseView<OrderDetailBloc> {
  final OrderResponse? order;
  const OrderDetailScreen({super.key, this.order});

  @override
  OrderDetailBloc createState() => OrderDetailBloc();

  @override
  OrderDetailBloc createBloc() => OrderDetailBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as OrderDetailBloc;
    return CustomScaffold(
      appBarLeading: InkWell(
        onTap: () {
          Navigator.pop(context);
        },
        child: Icon(Icons.arrow_back_ios, color: Colors.white, size: 24),
      ),
      centerTitle: true,
      title: Text(
        'Chi tiết đơn hàng',
        style: TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.bold,
          fontSize: 18,
        ),
      ),
      appBarActions: [
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: Container(
            padding: EdgeInsets.symmetric(vertical: 3, horizontal: 5),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(8),
              color: toHexToColor(primaryColorPurple),
            ),
            child: Text(
              order?.status ?? '',
              style: TextStyle(color: Colors.white),
            ),
          ),
        ),
      ],
      bodyBuilder: (controller) {
        return StreamBuilder<List<OrderDetailResponse>>(
          stream: bloc.streamOrderDetail.output,
          builder: (context, snapshot) {
            if (!snapshot.hasData) {
              return const Center(child: CircularProgressIndicator());
            }

            final List<OrderDetailResponse> listOrderDetail =
                snapshot.data ?? [];

            if (listOrderDetail.isEmpty || listOrderDetail[0].order == null) {
              return const Center(child: Text('Không có dữ liệu đơn hàng'));
            }

            final OrderModel order = listOrderDetail[0].order!;

            return StreamBuilder(
              stream: bloc.streamListProduct,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  final listProduct = snapshot.data;

                  return SingleChildScrollView(
                    padding: const EdgeInsets.all(10),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        itemInfomation(context),
                        _divider('Thông tin đơn hàng'),
                        _buildItemOrder(bloc, context, order),
                        _divider('Danh sách sản phẩm'),
                        _listProduct(
                          listProduct!,
                          listOrderDetail,
                          context,
                          bloc,
                        ),
                        _buildListPromoCode(context, bloc),
                        _buildBottomCondition(
                          context,
                          bloc,
                          OrderStatusExtension.fromString(
                                order.status ?? OrderStatus.pending.name,
                              ) ??
                              OrderStatus.pending,
                        ),
                        SizedBox(height: 30),
                      ],
                    ),
                  );
                }
                return const Center(child: CircularProgressIndicator());
              },
            );
          },
        );
      },
    );
  }

  Widget itemInfomation(BuildContext bContext) {
    return Card(
      color: Colors.white,
      child: Center(
        child: Container(
          width: AppSize.w(1),
          height: 100,
          decoration: BoxDecoration(borderRadius: BorderRadius.circular(10)),
          padding: EdgeInsets.symmetric(horizontal: 10),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
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
              SizedBox(width: 8),
              SizedBox(
                // padding: const EdgeInsets.all(8.0),
                width: AppSize.w(0.6),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    SizedBox(height: 15),
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
                      '${Storage.addressModel?.street} ${Storage.addressModel?.ward} ${Storage.addressModel?.district} ${Storage.addressModel?.city}',
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
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildItemOrder(
    OrderDetailBloc bloc,
    BuildContext bContext,
    OrderModel model,
  ) {
    return Card(
      color: Colors.white70,
      child: Container(
        width: AppSize.width(),
        decoration: BoxDecoration(borderRadius: BorderRadius.circular(8)),
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _itemInfo('Mã đơn hàng: ', '#${bloc.model?.orderId}'),
              _itemInfo('Ngày đặt hàng: ', bloc.model?.orderDate ?? ''),
              _itemInfo(
                'Phí sản phẩm: ',
                formatCurrencyVN(bloc.getTotalProduct()),
              ),
              _itemInfo(
                'Phí vận chuyển: ',
                formatCurrencyVN(bloc.getTotalShip()),
              ),

              _itemInfo(
                'Tổng tiền: ',
                formatCurrencyVN(bloc.model?.grandTotal ?? 0),
              ),
              model.discount != null
                  ? _itemInfo('Giảm giá: ', '${model.discount}%')
                  : SizedBox(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _itemInfo(String text, String value) {
    return Row(
      children: [
        Text(
          text,
          style: TextStyle(
            color: toHexToColor(secondaryTextColor),
            fontSize: 14,
          ),
        ),
        Spacer(),
        Text(
          value,
          style: TextStyle(color: toHexToColor(primaryTextColor), fontSize: 14),
        ),
      ],
    );
  }

  Widget _divider(String text) {
    return customDivider(
      text: Text(text, style: TextStyle(color: toHexToColor(primaryTextColor))),
      color: toHexToColor(borderColor),
    );
  }

  Widget _listProduct(
    List<ProductModel> model,
    List<OrderDetailResponse> orderModel,
    BuildContext bContext,
    OrderDetailBloc bloc,
  ) {
    return MediaQuery.removePadding(
      removeTop: true,
      removeBottom: true,
      context: bContext,
      child: ListView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: min(model.length, orderModel.length),

        itemBuilder: (bContext, index) {
          final product = model[index];
          final order = orderModel[index];
          return _itemCard(
            product: product,
            orderModel: order,
            bContext: bContext,
            bloc: bloc,
          );
        },
      ),
    );
  }

  Widget _itemCard({
    ProductModel? product,
    OrderDetailResponse? orderModel,
    BuildContext? bContext,
    OrderDetailBloc? bloc,
  }) {
    return InkWell(
      onTap: () {
        Navigator.push(
          bContext!,
          MaterialPageRoute(
            builder:
                (bContext) =>
                    ProductDetailCustomerScreen(productId: product?.productId),
          ),
        );
      },
      child: SizedBox(
        width: AppSize.width(),
        height: 160,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 3, vertical: 2),
          child: Card(
            color: Colors.white,
            child: SizedBox(
              height: 100,
              child: Column(
                children: [
                  Row(
                    children: [
                      Expanded(
                        flex: 4,
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(10),
                          // child: Image.asset(avtWomen6, fit: BoxFit.cover),
                          child:
                              (product?.image != null)
                                  ? CachedNetworkImage(
                                    imageUrl: product!.image!,
                                    placeholder:
                                        (context, url) =>
                                            CircularProgressIndicator(),
                                    errorWidget:
                                        (context, url, error) => Image.asset(
                                          imgBranch,
                                          fit: BoxFit.contain,
                                        ),
                                    width: 100,
                                    height: 100,
                                    fit: BoxFit.contain,
                                  )
                                  : Image.asset(imgBranch, fit: BoxFit.contain),
                        ),
                      ),
                      Expanded(
                        flex: 6,
                        child: Padding(
                          padding: const EdgeInsets.only(left: 10),
                          child: SizedBox(
                            height: 100,
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              mainAxisSize: MainAxisSize.max,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                customAutoSizeText(
                                  14,
                                  18,
                                  product?.name ?? '',
                                  isBold: true,
                                  textColor: toHexToColor(primaryTextColor),
                                ),
                                // customAutoSizeText(8, 12, '100.000đ', isLine: true),
                                customAutoSizeText(
                                  12,
                                  16,
                                  formatCurrencyVN(
                                    orderModel?.totalPrice ?? 0.0,
                                  ),
                                  isBold: true,
                                  // isLine: true,
                                  textColor: toHexToColor(primaryButtonColor),
                                ),
                                customAutoSizeText(
                                  10,
                                  12,
                                  '${orderModel?.quantity} x ${formatCurrencyVN(product?.price ?? 0.0)}',
                                  isBold: true,
                                  // isLine: true,
                                  textColor: toHexToColor(primaryTextColor),
                                ),
                              ],
                            ),
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
      ),
    );
  }

  Widget _buildBottomCondition(
    BuildContext bContext,
    OrderDetailBloc bloc,
    OrderStatus status,
  ) {
    return bloc.onCheckStatus(
          OrderStatusExtension.fromString(order!.status ?? '') ??
              OrderStatus.failed,
        )
        ? Center(
          child: SizedBox(
            height: 100,
            child: _buildBottom(
              bContext,
              bloc,
              OrderStatusExtension.fromString(order!.status ?? '') ??
                  OrderStatus.failed,
            ),
          ),
        )
        : SizedBox();
  }

  Widget _buildBottom(
    BuildContext bContext,
    OrderDetailBloc bloc,
    OrderStatus status,
  ) {
    switch (status) {
      case OrderStatus.pending:
        return Padding(
          padding: const EdgeInsets.only(top: 6),
          child: customButton(
            () {
              bloc.onReturnProducts(context: bContext);
            },
            AppSize.w(0.4),
            40,
            Text(
              'Trả hàng',
              style: TextStyle(
                color: toHexToColor(primaryTextColor),
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
            typeButton: 0,
          ),
        );
      case OrderStatus.paying:
        return Container();
      // return Padding(
      //   padding: const EdgeInsets.only(top: 6),
      //   child: customButton(
      //     () {
      //       // bloc.onCreateRatesForProducts(context: bContext);
      //     },
      //     AppSize.w(0.4),
      //     40,
      //     Text(
      //       'Thanh toán',
      //       style: TextStyle(
      //         color: toHexToColor(primaryTextColor),
      //         fontSize: 14,
      //         fontWeight: FontWeight.w600,
      //       ),
      //     ),
      //     typeButton: 0,
      //   ),
      // );
      case OrderStatus.paid:
        return Container();
      case OrderStatus.delivering:
        return Container();
      case OrderStatus.shipped:
        return Padding(
          padding: const EdgeInsets.only(top: 6),
          child: _buildSuccess(bContext, bloc),
        );
      case OrderStatus.cancelling:
        return Container();
      case OrderStatus.cancelled:
        return Container();
      case OrderStatus.completed:
        return Padding(
          padding: const EdgeInsets.only(top: 6),
          child: _buildComplete(bContext, bloc),
        );
      case OrderStatus.failed:
        return Container();
    }
  }

  Widget _buildSuccess(BuildContext bContext, OrderDetailBloc bloc) {
    return SizedBox(
      width: AppSize.width(),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          customButton(
            () {
              bloc.onCreateRatesForProducts(context: bContext);
            },
            AppSize.w(0.4),
            40,
            Text(
              'Đánh giá',
              style: TextStyle(color: toHexToColor(primaryTextColor)),
            ),
            typeButton: 0,
          ),
          customButton(
            () {
              bloc.onReturnProducts(context: bContext);
            },
            AppSize.w(0.4),
            40,
            Text('Trả hàng', style: TextStyle(color: Colors.white)),
            typeButton: 1,
          ),
        ],
      ),
    );
  }

  Widget _buildComplete(BuildContext bContext, OrderDetailBloc bloc) {
    return SizedBox(
      width: AppSize.width(),
      height: 100,
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              customButton(
                () {
                  bloc.onCreateRatesForProducts(context: bContext);
                },
                AppSize.w(0.4),
                40,
                Text(
                  'Đánh giá',
                  style: TextStyle(color: toHexToColor(primaryTextColor)),
                ),
                typeButton: 0,
              ),
              customButton(
                () {
                  bloc.onReturnProducts(context: bContext);
                },
                AppSize.w(0.4),
                40,
                Text('Trả hàng', style: TextStyle(color: Colors.white)),
                typeButton: 1,
              ),
            ],
          ),
          SizedBox(height: 10),
          customButton(
            () {
              bloc.onBuyAgain(bContext);
            },
            AppSize.w(0.4),
            40,
            backgroundColorButton: toHexToColor(secondaryButtonColor),
            Text('Mua lại', style: TextStyle(color: Colors.white)),
            typeButton: 1,
          ),
        ],
      ),
    );
  }

  Widget _buildListPromoCode(BuildContext bContext, OrderDetailBloc bloc) {
    return StreamBuilder(
      stream: bloc.streamListPromoCode,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return MediaQuery.removePadding(
            context: bContext,
            removeTop: true,
            removeBottom: true,
            child: ListView.builder(
              shrinkWrap: true,
              physics: NeverScrollableScrollPhysics(),
              itemCount: snapshot.data?.length,
              itemBuilder: (context, index) {
                return InkWell(
                  onTap: () {
                    _showPromoCodeSheet(
                      context,
                      '',
                      Container(),
                      snapshot.data?[index] ?? PromoCodeModel(),
                    );
                  },
                  child: customItemPromoCode(
                    snapshot.data?[index] ?? PromoCodeModel(),
                    () {},
                    () {},
                  ),
                );
              },
            ),
          );
        }
        return SizedBox();
      },
    );
  }

  _showPromoCodeSheet(
    BuildContext context,
    String title,
    Widget bodyWidget,
    PromoCodeModel model,
  ) {
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
}
