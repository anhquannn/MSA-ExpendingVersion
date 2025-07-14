import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/response/order_detail_response_model.dart';
import 'package:msa/feature/domain/entities/address_model.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/order_preview_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/presentation/customer/createorder/bloc/create_order_bloc.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/change_address_screen.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/widget/custom_item_promocode.dart';

import '../../../../../core/config/base_bloc.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';

class CreateOrderScreen extends BaseView<CreateOrderBloc> {
  final int? orderId;
  final bool? isBuyAgain;
  final List<OrderDetailResponse>? orderDetail;
  const CreateOrderScreen({
    super.key,
    this.orderId,
    this.isBuyAgain = false,
    this.orderDetail,
  });

  @override
  CreateOrderBloc createState() => CreateOrderBloc();

  @override
  CreateOrderBloc createBloc() => CreateOrderBloc();
  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as CreateOrderBloc;
    return CustomScaffold(
      isHide: false,
      centerTitle: true,

      title: Text('Tạo đơn hàng', style: TextStyle(color: Colors.white)),
      appBarLeading: InkWell(
        onTap: () {
          Navigator.pushAndRemoveUntil(
            context,
            MaterialPageRoute(builder: (context) => HomeScreen()),
            (route) => true,
          );
        },
        child: Icon(Icons.arrow_back_ios, color: Colors.white, size: 24),
      ),
      bodyBuilder: (controller) {
        return Padding(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
          child: buildBodyContent(bloc: bloc, bcontext: context),
        );
      },
      hideBottomBarOnScroll: true,
    );
  }

  Widget buildBodyContent({
    required CreateOrderBloc bloc,
    // required ScrollController controller,
    BuildContext? bcontext,
  }) {
    return CustomScrollView(
      // controller: controller,
      slivers: [
        SliverToBoxAdapter(child: itemInfomation(bloc, bcontext!)),
        SliverToBoxAdapter(child: _divider('Danh sách sản phẩm')),
        SliverToBoxAdapter(child: listItemOrder(bloc)),
        SliverToBoxAdapter(child: _divider('Chi tiết đơn hàng')),
        SliverToBoxAdapter(child: _itemCarrier(bcontext, bloc)),
        SliverToBoxAdapter(child: _customUserReward(bloc)),
        SliverToBoxAdapter(child: _divider('Danh sách mã giảm giá')),
        SliverToBoxAdapter(child: _itemSelectPromoCode(bloc, bcontext)),
        SliverToBoxAdapter(child: _itemPromoCode(bloc, bcontext)),
        SliverToBoxAdapter(child: _divider('Phương thức thanh toán')),
        SliverToBoxAdapter(child: _paymentList(bloc, bcontext)),
        SliverToBoxAdapter(child: _buildButton(bcontext, bloc)),
        const SliverToBoxAdapter(child: SizedBox(height: 10)),
      ],
    );
  }

  Widget _customUserReward(CreateOrderBloc bloc) {
    return StreamBuilder(
      stream: bloc.streamReward,
      builder: (context, snapshot) {
        final data = snapshot.data;
        return
        //  data == 0
        //     ? Container()
        //     :
        UsePointSwitchRow(
          point: data ?? 0,
          isUsingPoint: bloc.isUseReward,
          onChanged: (value) {
            bloc.onChangeReward(value);
          },
        );
      },
    );
  }

  Widget _paymentList(CreateOrderBloc bloc, BuildContext context) {
    return StreamBuilder(
      initialData: bloc.paymentMethods,
      stream: bloc.streamPaymentMethod,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          final data = snapshot.data;
          return MediaQuery.removePadding(
            context: context,
            removeBottom: true,
            removeTop: true,
            child: ListView.builder(
              shrinkWrap: true,
              physics: NeverScrollableScrollPhysics(),
              itemCount: data?.length,
              itemBuilder: (context, index) {
                final model = data?[index];
                return InkWell(
                  onTap: () {
                    bloc.onSelectdPaymentMethod(model);
                  },
                  child: _itemPayment(model!),
                );
              },
            ),
          );
        }
        return SizedBox.shrink();
      },
    );
  }

  Widget _itemPayment(PaymentMethod model) {
    return Card(
      color: Colors.white,
      child: Container(
        padding: EdgeInsets.all(8),
        width: AppSize.w(1),
        height: 60,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(8),
          // border: Border.all(color: toHexToColor(borderColor), width: 1),
        ),
        child: Row(
          children: [
            SizedBox(
              width: 60,
              height: 60,
              child: Padding(
                padding: EdgeInsetsDirectional.all(5),
                child: Image.asset(model.iconUrl!),
              ),
            ),
            Padding(
              padding: EdgeInsets.all(8),
              child: Center(child: Text(model.name!)),
            ),
            Spacer(),
            model.selected!
                ? Icon(Icons.check_circle_rounded, color: model.color)
                : Icon(Icons.circle_outlined, color: model.color),
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

  Widget itemInfomation(CreateOrderBloc bloc, BuildContext bContext) {
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
                            imgBranch,
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
                      // '${bloc.model?.street} ${bloc.model?.ward} ${bloc.model?.district} ${bloc.model?.city}',
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
              // Spacer(),
              InkWell(
                onTap: () {
                  Navigator.push(
                    bContext,
                    MaterialPageRoute(
                      builder:
                          (bContext) => AddressListWidget(
                            addresses:
                                Storage.addressModel ?? UserAddressModel(),
                            isBuyAgain: isBuyAgain ?? false,
                            orderDetail: orderDetail,
                            orderId: orderId ?? 0,
                          ),
                    ),
                  );
                },
                child: Icon(Icons.arrow_forward_ios, color: Colors.black),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget listItemOrder(CreateOrderBloc bloc) {
    return isBuyAgain == false
        ? StreamBuilder(
          stream: bloc.streamListCartItem.output,
          builder: (context, snapshot) {
            if (!snapshot.hasData || snapshot.data == null) {
              return const Center(child: CircularProgressIndicator());
            }

            final list = snapshot.data as List<CartItemModel>;
            if (list.isEmpty) {
              return const Center(
                child: Text('Không có sản phẩm nào trong giỏ hàng.'),
              );
            }
            return MediaQuery.removePadding(
              removeTop: true,
              removeBottom: true,
              context: context,
              child: ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: list.length,
                itemBuilder: (context, index) {
                  final data = list[index];
                  return _itemCard(bloc: bloc, model: data);
                },
              ),
            );
          },
        )
        : StreamBuilder(
          stream: bloc.streamOrderDetail.output,
          builder: (context, snapshot) {
            if (!snapshot.hasData || snapshot.data == null) {
              return const Center(child: CircularProgressIndicator());
            }

            final list = snapshot.data as List<OrderDetailResponse>;
            if (list.isEmpty) {
              return const Center(
                child: Text('Không có sản phẩm nào trong giỏ hàng.'),
              );
            }
            return MediaQuery.removePadding(
              removeTop: true,
              removeBottom: true,
              context: context,
              child: ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: list.length,
                itemBuilder: (context, index) {
                  final data = list[index];
                  return _itemCardBuyAgain(model: data, bloc: bloc);
                },
              ),
            );
          },
        );
  }

  Widget _itemCardBuyAgain({
    OrderDetailResponse? model,
    CreateOrderBloc? bloc,
  }) {
    final ProductModel? product = model?.product;
    return SizedBox(
      width: AppSize.width(),
      height: 150,
      child: Padding(
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
                                      fit: BoxFit.cover,
                                    ),
                                width: 100,
                                height: 100,
                                fit: BoxFit.cover,
                              )
                              : Image.asset(imgBranch, fit: BoxFit.cover),
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
                              model?.product?.name ?? '',
                              isBold: true,
                              textColor: toHexToColor(primaryTextColor),
                            ),
                            customAutoSizeText(
                              12,
                              16,
                              formatCurrencyVN(model?.product?.price ?? 0.0),
                              isBold: true,
                              // isLine: true,
                              textColor: toHexToColor(primaryButtonColor),
                            ),
                            Spacer(),
                            // Row(
                            //   children: [
                            //     Spacer(),
                            //     Card(
                            //       color: Colors.white,
                            //       child: Container(
                            //         decoration: BoxDecoration(
                            //           color: Colors.grey[300],
                            //           borderRadius: BorderRadius.circular(5),
                            //         ),
                            //         child: Row(
                            //           children: [
                            //             InkWell(
                            //               onTap: () {
                            //                 bloc?.onCaculate(
                            //                   model ?? CartItemModel(),
                            //                   true,
                            //                 );
                            //               },
                            //               child: Container(
                            //                 padding: EdgeInsets.symmetric(
                            //                   horizontal: 10,
                            //                   vertical: 3,
                            //                 ),
                            //                 decoration: BoxDecoration(
                            //                   borderRadius: BorderRadius.only(
                            //                     topLeft: Radius.circular(5),
                            //                     bottomLeft: Radius.circular(5),
                            //                   ),
                            //                 ),
                            //                 child: Text(' - '),
                            //               ),
                            //             ),
                            //             Container(
                            //               padding: EdgeInsets.symmetric(
                            //                 horizontal: 8,
                            //                 vertical: 3,
                            //               ),
                            //               color: Colors.white,
                            //               child: Text(
                            //                 model?.quantity.toString() ?? '0',
                            //               ),
                            //             ),
                            //             InkWell(
                            //               onTap: () {
                            //                 bloc?.onCaculate(
                            //                   model ?? CartItemModel(),
                            //                   false,
                            //                 );
                            //               },
                            //               child: Container(
                            //                 padding: EdgeInsets.symmetric(
                            //                   horizontal: 8,
                            //                   vertical: 3,
                            //                 ),
                            //                 decoration: BoxDecoration(
                            //                   borderRadius: BorderRadius.only(
                            //                     bottomRight: Radius.circular(5),
                            //                     topRight: Radius.circular(5),
                            //                   ),
                            //                   // color: Colors.white,
                            //                 ),
                            //                 child: Text(' + '),
                            //               ),
                            //             ),
                            //           ],
                            //         ),
                            //       ),
                            //     ),
                            //   ],
                            // ),
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
      ),
    );
  }

  Widget _itemCard({CartItemModel? model, CreateOrderBloc? bloc}) {
    final ProductModel? product = model?.product;
    return SizedBox(
      width: AppSize.width(),
      height: 150,
      child: Padding(
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
                                      fit: BoxFit.cover,
                                    ),
                                width: 100,
                                height: 100,
                                fit: BoxFit.cover,
                              )
                              : Image.asset(imgBranch, fit: BoxFit.cover),
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
                              model?.product?.name ?? '',
                              isBold: true,
                              textColor: toHexToColor(primaryTextColor),
                            ),
                            customAutoSizeText(
                              12,
                              16,
                              formatCurrencyVN(model?.product?.price ?? 0.0),
                              isBold: true,
                              // isLine: true,
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
                                            bloc?.onCaculate(
                                              model ?? CartItemModel(),
                                              true,
                                            );
                                          },
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
                                          child: Text(
                                            model?.quantity.toString() ?? '0',
                                          ),
                                        ),
                                        InkWell(
                                          onTap: () {
                                            bloc?.onCaculate(
                                              model ?? CartItemModel(),
                                              false,
                                            );
                                          },
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
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _itemCarrier(BuildContext bContext, CreateOrderBloc bloc) {
    return StreamBuilder(
      stream: bloc.streamPreviewOrder,
      builder: (context, snapshot) {
        if (snapshot.data != null) {
          bloc.onSetCanBuy(true);
          final data = snapshot.data;
          return Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: [
              _buildItemGrandTotal(data ?? OrderPreviewModel(), context),
              _divider('Đơn vị vận chuyển'),
              _buildItemRate(data ?? OrderPreviewModel(), context),
            ],
          );
        } else {
          bloc.onSetCanBuy(false);
          return Center(
            child: Text('Không có đơn vị vận chuyển cho địa chỉ của bạn.'),
          );
        }
      },
    );
  }

  Widget _buildItemGrandTotal(OrderPreviewModel model, BuildContext bContext) {
    return Container(
      width: AppSize.width(),
      decoration: BoxDecoration(borderRadius: BorderRadius.circular(8)),
      child: Card(
        color: Colors.white70,
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _itemGrandTotal(
                'Tạm tính: ',
                formatCurrencyVN(
                  double.parse((model.totalCost ?? 0).toString()),
                ),
              ),
              _itemGrandTotal(
                'Số tiền giảm giá: ',
                formatCurrencyVN(
                  double.parse((model.discount ?? 0).toString()),
                ),
              ),
              _itemGrandTotal(
                'Tổng tiền: ',
                formatCurrencyVN(
                  double.parse((model.grandTotal ?? 0).toString()),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildItemRate(OrderPreviewModel? model, BuildContext bContext) {
    return model != null
        ? InkWell(
          onTap: () {
            showRateDetailBottomSheet(
              context: bContext,
              rate: model.rates ?? RateModel(),
            );
          },
          child: Card(
            color: Colors.white70,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8),
            ),
            elevation: 2,
            child: Stack(
              children: [
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Row(
                    children: [
                      // Logo bên trái
                      Container(
                        width: 80,
                        height: 80,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child:
                            model.rates?.carrierLogo != null
                                ? ClipRRect(
                                  borderRadius: BorderRadius.circular(8),
                                  child: Image.network(
                                    model.rates!.carrierLogo!,
                                    fit: BoxFit.cover,
                                  ),
                                )
                                : const Icon(Icons.image),
                      ),
                      const SizedBox(width: 8),

                      // Nội dung bên phải
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              model.rates?.carrierName ?? '',
                              style: const TextStyle(
                                fontWeight: FontWeight.bold,
                              ),
                              overflow: TextOverflow.ellipsis,
                            ),
                            Text(
                              model.rates?.expected ?? '',
                              overflow: TextOverflow.ellipsis,
                            ),
                            Text(
                              'Phí giao hàng: ${formatCurrencyVN(model.rates?.totalFee ?? 0)}',
                              overflow: TextOverflow.ellipsis,
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),

                // Positioned label ở góc phải trên cùng của Card
                Positioned(
                  top: 0,
                  right: 0,
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 8,
                      vertical: 4,
                    ),
                    decoration: BoxDecoration(
                      color: toHexToColor(primaryColorGreen),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      model.rates?.service ?? '',
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        )
        : Center(
          child: Text('Không có đơn vị vận chuyển cho địa chỉ của bạn.'),
        );
  }

  Widget itemDetail(CreateOrderBloc bloc, BuildContext bContext) {
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
                      // '${bloc.model?.street} ${bloc.model?.ward} ${bloc.model?.district} ${bloc.model?.city}',
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
              // Spacer(),
              InkWell(
                onTap: () {
                  Navigator.push(
                    bContext,
                    MaterialPageRoute(
                      builder:
                          (bContext) => AddressListWidget(
                            addresses:
                                Storage.addressModel ?? UserAddressModel(),
                          ),
                    ),
                  );
                },
                child: Icon(
                  Icons.arrow_forward_ios,
                  color: Colors.black,
                  size: 24,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _itemGrandTotal(String text, String value) {
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

  Widget _itemSelectPromoCode(CreateOrderBloc bloc, BuildContext bContext) {
    return StreamBuilder(
      stream: bloc.streamPromoCodeModels,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          final data = snapshot.data;
          return Padding(
            padding: EdgeInsets.symmetric(vertical: 8),
            child: SizedBox(
              width: AppSize.width(),
              height: 20,
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    'Danh sách mã giảm giá',
                    style: TextStyle(color: toHexToColor(primaryTextColor)),
                  ),
                  Spacer(),
                  InkWell(
                    onTap: () {
                      showPromoCodeBottomSheet(
                        promoCodes: data!,
                        bcontext: bContext,
                        bloc: bloc,
                      );
                    },
                    child: Icon(
                      Icons.add_circle_outline,
                      color: toHexToColor(primaryColorGreen),
                    ),
                  ),
                ],
              ),
            ),
          );
        }
        return SizedBox();
      },
    );
  }

  Widget _itemPromoCode(CreateOrderBloc bloc, BuildContext bContext) {
    return StreamBuilder(
      stream: bloc.streamPromoCodeModels,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          final data = snapshot.data;
          final selectedPromoCodes =
              data?.where((e) => e.selected == true).toList() ?? [];

          if (selectedPromoCodes.isEmpty) return SizedBox.shrink();

          return MediaQuery.removePadding(
            context: bContext,
            removeBottom: true,
            removeTop: true,
            child: ListView.builder(
              itemCount: selectedPromoCodes.length,
              shrinkWrap: true,
              physics: NeverScrollableScrollPhysics(),
              itemBuilder: (context, index) {
                return _customItemPromoCode(
                  selectedPromoCodes[index],
                  bloc,
                  bContext,
                  selectedPromoCodes,
                );
              },
            ),
          );
        }
        return SizedBox.shrink();
      },
    );
  }

  Widget _customItemPromoCode(
    PromoCodeModel model,
    CreateOrderBloc bloc,
    BuildContext bContext,
    List<PromoCodeModel> listPromoCode,
  ) {
    return Stack(
      children: [
        SizedBox(
          width: AppSize.w(1),
          height: 170,
          child: customItemPromoCode(model, () {}, () {}),
        ),
        // Nút Xóa
        Positioned(
          top: 4,
          right: 4,
          child: InkWell(
            onTap: () {
              bloc.onSelectPromoCode(model, bContext: bContext);
            },
            child: Container(
              padding: const EdgeInsets.all(4),
              decoration: BoxDecoration(
                color: Colors.red.withOpacity(0.8),
                shape: BoxShape.circle,
              ),
              child: const Icon(Icons.close, size: 18, color: Colors.white),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildButton(BuildContext bContext, CreateOrderBloc bloc) {
    return StreamBuilder<bool>(
      stream: bloc.streamCanBuy,
      builder: (context, snapshot) {
        final isEnabled = snapshot.data == true;
        return InkWell(
          onTap:
              isEnabled
                  ? () =>
                      isBuyAgain == false
                          ? bloc.onBuy(bContext)
                          : bloc.onBuyAgain(bContext)
                  : null,
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 12),
            child: Card(
              elevation: isEnabled ? 4 : 0,
              child: Container(
                height: 45,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(10),
                  gradient:
                      isEnabled
                          ? LinearGradient(
                            colors: [
                              toHexToColor(primaryButtonColor),
                              Colors.blueGrey,
                            ],
                          )
                          : LinearGradient(
                            colors: [
                              Colors.grey.shade400,
                              Colors.grey.shade500,
                            ],
                          ),
                ),
                child: Center(
                  child: Text(
                    'Đặt hàng',
                    style: TextStyle(
                      color: isEnabled ? Colors.white : Colors.black38,
                      fontSize: 18,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ),
            ),
          ),
        );
      },
    );
  }

  showPromoCodeBottomSheet({
    CreateOrderBloc? bloc,
    required BuildContext bcontext,
    required List<PromoCodeModel> promoCodes,
  }) {
    showModalBottomSheet(
      context: bcontext,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(12)),
      ),
      builder: (_) {
        return Container(
          padding: const EdgeInsets.all(8),
          height: MediaQuery.sizeOf(bcontext).height * 0.7,
          decoration: const BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.vertical(top: Radius.circular(12)),
          ),
          child: Column(
            children: [
              const Text(
                'Danh sách mã giảm giá',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const Divider(),
              Expanded(
                child: ListView.builder(
                  itemCount: promoCodes.length,
                  itemBuilder: (_, index) {
                    final promo = promoCodes[index];
                    return InkWell(
                      onTap: () {
                        bloc?.onSelectPromoCode(promo, bContext: bcontext);
                        Navigator.pop(bcontext);
                      },
                      child: SizedBox(
                        height: 170,
                        child: customItemPromoCode(
                          promo,
                          () {},
                          () {},
                          isSelected: promo.selected ?? false,
                        ),
                      ),
                    );
                  },
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  showRateDetailBottomSheet({
    required BuildContext context,
    required RateModel rate,
  }) {
    final screenHeight = MediaQuery.of(context).size.height;
    final screenWidth = MediaQuery.of(context).size.width;

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(10)),
      ),
      builder: (_) {
        return Container(
          height: screenHeight * 0.6,
          width: screenWidth,
          padding: const EdgeInsets.all(16),
          decoration: const BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.vertical(top: Radius.circular(10)),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Text(
                'Thông tin vận chuyển',
                textAlign: TextAlign.center,
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
              ),
              const Divider(),
              Expanded(
                child: SingleChildScrollView(
                  child: _RateDetailWidget(rate: rate),
                ),
              ),
              const SizedBox(height: 16),
              InkWell(
                onTap: () => Navigator.pop(context),
                child: Container(
                  height: 50,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(10),
                    gradient: LinearGradient(
                      colors: [
                        toHexToColor(primaryButtonColor),
                        Colors.blueGrey,
                      ],
                    ),
                  ),
                  child: const Center(
                    child: Text('Đóng', style: TextStyle(color: Colors.white)),
                  ),
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}

class _RateDetailWidget extends StatelessWidget {
  final RateModel rate;

  const _RateDetailWidget({super.key, required this.rate});

  @override
  Widget build(BuildContext context) {
    TextStyle labelStyle = const TextStyle(fontWeight: FontWeight.w600);
    TextStyle valueStyle = const TextStyle(color: Colors.black87);

    Widget row(String label, String value) => Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: labelStyle),
          Text(value, style: valueStyle),
        ],
      ),
    );

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Logo + tên đơn vị vận chuyển
        Row(
          children: [
            Image.network(rate.carrierLogo!, width: 40, height: 40),
            const SizedBox(width: 10),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    rate.carrierName!,
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 16,
                    ),
                  ),
                  Text(
                    '${rate.service} - ${rate.expected}',
                    style: const TextStyle(color: Colors.grey),
                  ),
                ],
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),

        row('Phí vận chuyển', formatCurrencyVN(rate.totalFee!)),

        if ((rate.discount ?? 0) > 0)
          row(
            'Giảm giá vận chuyển',
            '-${formatCurrencyVN(rate.discount ?? 0)}',
          ),

        const SizedBox(height: 8),
        const Text(
          'Chi tiết phí',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        const Divider(),
        if ((rate.locationFirstFee ?? 0) > 0)
          row('Phí chặng đầu', formatCurrencyVN(rate.locationFirstFee!)),
        if ((rate.serviceFee ?? 0) > 0)
          row('Phí dịch vụ', formatCurrencyVN(rate.serviceFee!)),
        if ((rate.codFee ?? 0) > 0)
          row('Phí COD', formatCurrencyVN(rate.codFee!)),
        if ((rate.remoteAreaFee ?? 0) > 0)
          row('Phí vùng xa', formatCurrencyVN(rate.remoteAreaFee!)),
        if ((rate.insurranceFee ?? 0) > 0)
          row('Phí bảo hiểm', formatCurrencyVN(rate.insurranceFee!)),

        const SizedBox(height: 16),
        const Divider(),

        row('Tổng thanh toán', formatCurrencyVN(rate.totalAmount!)),

        const SizedBox(height: 16),
        const Text(
          'Hiệu suất giao hàng',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        const Divider(),
        row(
          'Tỉ lệ thành công',
          '${rate.report?.successPercent?.toStringAsFixed(1)}%',
        ),
        row(
          'Tỉ lệ hoàn hàng',
          '${rate.report?.returnPercent?.toStringAsFixed(1)}%',
        ),
        row(
          'Điểm đánh giá',
          '${rate.report?.scorePercent?.toStringAsFixed(1)}/10',
        ),
      ],
    );
  }
}

class UsePointSwitchRow extends StatelessWidget {
  final double point;
  final bool isUsingPoint;
  final ValueChanged<bool> onChanged;

  const UsePointSwitchRow({
    super.key,
    required this.point,
    required this.isUsingPoint,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Row(
        children: [
          Expanded(
            child: Text(
              'Bạn có $point điểm. Bạn có muốn sử dụng?',
              style: TextStyle(fontSize: 16),
            ),
          ),
          Switch(
            value: isUsingPoint,
            onChanged: onChanged,
            activeColor: toHexToColor(primaryButtonColor),
          ),
        ],
      ),
    );
  }
}
