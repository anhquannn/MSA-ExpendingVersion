import 'dart:io';

import 'package:android_intent_plus/android_intent.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/create_order_model_request.dart';
import 'package:msa/feature/data/model/request/promocode_request_model.dart';
import 'package:msa/feature/data/model/response/create_order_response_model.dart';
import 'package:msa/feature/data/model/response/order_detail_response_model.dart';
import 'package:msa/feature/domain/entities/address_model.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/order_preview_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/change_address_screen.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/vnpay_webview.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/custom_loading.dart';
import 'package:rxdart/rxdart.dart';
import 'package:url_launcher/url_launcher.dart';

class CreateOrderBloc extends BaseBloc<CreateOrderScreen> {
  UserAddressModel? model;

  List<CartItemModel>? listCartItem;
  final streamListCartItem = BehaviorSubject<List<CartItemModel>>();

  OrderPreviewModel? previewOrder;
  final streamPreviewOrder = BehaviorSubject<OrderPreviewModel>();

  List<PaymentMethod>? paymentMethods;
  final streamPaymentMethod = BehaviorSubject<List<PaymentMethod>?>();

  List<PromoCodeModel>? listPromocode = [];
  final streamPromoCodeModels = BehaviorSubject<List<PromoCodeModel>>();

  final streamCanBuy = BehaviorSubject<bool>();

  List<OrderDetailResponse>? orderDetail;
  final streamOrderDetail = BehaviorSubject<List<OrderDetailResponse>>();

  String vnPayurl = '';

  int orderId = 0;
  List<OrderDetailResponse>? orderDetails = [];

  double reward = 0;
  final streamReward = BehaviorSubject<double>();
  bool isUseReward = false;

  final Map<int, Debouncer> _debouncers = {};
  bool isZaloPaySelected = true;
  @override
  String get contextKey => 'CreateOrderScreen';

  @override
  void onInit() {
    initPaymentMethod();
    if (widget.isBuyAgain == true) {
      orderDetail = widget.orderDetail;
      streamOrderDetail.set(orderDetail!);
      orderId = widget.orderId ?? 0;
      orderDetails = widget.orderDetail;
    }
  }

  @override
  void onDispose() {}

  @override
  void onReady() {
    onInitData();
    if (widget.isBuyAgain == true) {
      Future.wait<void>([
        onPreviewOrderAgain(),
        onGetPromoCode(),
        onGetReward(),
      ]);
    } else {
      Future.wait<void>([
        onGetCartItem(),
        onGetPreviewOrder(),
        onGetPromoCode(),
        onGetReward(),
      ]);
    }
  }

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext viewContext) => widget.build(viewContext);

  initPaymentMethod() {
    List<PaymentMethod> paymentMethod = [
      PaymentMethod(
        id: 'cod',
        name: 'Tiền mặt',
        iconUrl: iconCash,
        selected: true,
        color: Colors.tealAccent,
      ),
      PaymentMethod(
        id: 'vnpay',
        name: 'VNPAY',
        iconUrl: iconVnPay,
        selected: false,
        color: Colors.blue,
      ),
    ];
    paymentMethods = paymentMethod;
    streamPaymentMethod.set(paymentMethod);
  }

  onInitData() {
    model = Storage.addressModel;
    setState(() {});
  }

  onChangeAddress(BuildContext bcontext) async {
    final data = await Navigator.push(
      bcontext,
      MaterialPageRoute(
        builder:
            (bcontext) => AddressListWidget(
              addresses: Storage.addressModel ?? UserAddressModel(),
              isBuyAgain: widget.isBuyAgain ?? false,
              orderDetail: orderDetails,
              orderId: orderId,
            ),
      ),
    );

    if (data != null) {
      model = data;
      Storage.addressModel = data;

      setState(() {});
    }
  }

  onGetCartItem() async {
    final data = await Repository.getCartItem(
      Storage.cartModelGlobal?.cartId ?? 0,
    );

    listCartItem = data;
    streamListCartItem.set(listCartItem ?? []);
  }

  onCreateOrder(BuildContext bContext) async {
    final data = await Repository.onCreateOrder(CreateOrderRequestModel(usePoints:isUseReward? reward:0));
    if (data != null) {
      await showCustomDialog(
        bContext,
        AppSize.width(),
        AppSize.width(),
        'Thông báo',
        Text('Đặt hàng thành công', style: TextStyle(color: Colors.white)),
        true,
        false,
        Icon(
          Icons.check_box_outline_blank_rounded,
          color: toHexToColor(primaryColorGreen),
        ),
      );
      Navigator.pop(bContext);
    } else {
      showCustomDialog(
        bContext,
        AppSize.width(),
        AppSize.width(),
        'Thông báo',
        Text(
          'Đặt hàng thấy bại, vui lòng thử lại sau !!!',
          style: TextStyle(color: Colors.white),
        ),
        true,
        false,
        Icon(
          Icons.warning_amber_rounded,
          color: toHexToColor(primaryColorGreen),
        ),
      );
    }
  }

  onCaculate(CartItemModel model, bool isMinus) {
    if ((model.quantity ?? 1) == 1 && isMinus == true) return;
    final quantity =
        isMinus ? (model.quantity ?? 1) - 1 : (model.quantity ?? 1) + 1;
    final index = listCartItem?.indexWhere(
      (e) => e.cartItemId == model.cartItemId,
    );
    if (index != null && index >= 0) {
      listCartItem![index].quantity = quantity;
      streamListCartItem.set(listCartItem!);
    }
    _debouncers[model.cartItemId!] ??= Debouncer(milliseconds: 600);
    _debouncers[model.cartItemId!]!.run(() async {
      final response = await Repository.onUpdateQuantity(
        branchId: Storage.branchModelGlobal?.branchId ?? 3,
        cartItemId: model.cartItemId,
        quantity: quantity,
        select: model.selected ?? true,
      );
      if (widget.isBuyAgain == true) {
        await onPreviewOrderAgain();
      } else {
        await onGetPreviewOrder();
      }
      // if (response) {
      //   await onGetCartItem();
      // }
    });
  }

  onGetPreviewOrder() async {
    List<String> promo = [];
    listPromocode?.forEach((element) {
      if (element.selected == true) {
        promo.add(element.code ?? '');
      }
    });
    final data = await Repository.onGetPreviewOrder(promoCode: promo, usePoints: isUseReward? reward:0);
    if (data != null) {
      previewOrder = data;
      streamPreviewOrder.set(previewOrder ?? OrderPreviewModel());
      return true;
    } else {
      return false;
    }
  }

  onSelectdPaymentMethod(PaymentMethod model) {
    paymentMethods?.forEach((element) {
      if (element.id == model.id) {
        model.selected = true;
      } else {
        element.selected = false;
      }
    });
    streamPaymentMethod.set(paymentMethods);
  }

  onGetPromoCode() async {
    try {
      List<PromoCodeModel>? promoCode =
          await Repository.getActivePromoCodesForCart(
            Storage.cartModelGlobal?.cartId ?? 0,
          );

      streamPromoCodeModels.add(promoCode ?? []);
      listPromocode = promoCode;
    } catch (e) {
      streamPromoCodeModels.add([]);
    }
  }

  onSelectPromoCode(PromoCodeModel model, {BuildContext? bContext}) async {
    listPromocode?.forEach((element) {
      if (element.promoCodeId == model.promoCodeId) {
        element.selected = !(model.selected ?? false);
      }
    });
    final bool isSuccess;
    if (widget.isBuyAgain == true) {
      isSuccess = await onPreviewOrderAgain();
    } else {
      isSuccess = await onGetPreviewOrder();
    }
    if (isSuccess) {
      streamPromoCodeModels.set(listPromocode!);
    } else {
      listPromocode?.forEach((element) {
        if (element.promoCodeId == model.promoCodeId) {
          element.selected = !(model.selected ?? false);
        }
      });
      showCustomDialog(
        bContext!,
        AppSize.width(),
        AppSize.width(),
        'Thông báo',
        Text('Không thể áp dụng mã giảm giá'),
        true,
        false,
        Icon(Icons.warning, color: toHexToColor(primaryButtonColorRed)),
      );
    }

    setState(() {});
  }

  onBuy(BuildContext bContext) async {
    showFullScreenLoading(bContext);
    final CreateOrderRequestModel model = CreateOrderRequestModel(
      usePoints: isUseReward? reward:0,
      branchId: Storage.branchModelGlobal?.branchId,
      cartId: Storage.cartModelGlobal?.cartId,
      grandTotal: previewOrder?.grandTotal,
      // orderDate: formatDateTime(DateTime.now()),
      orderDate: formatDateTime(DateTime.now().add(Duration(days: 1))),

      promoCodes:
          listPromocode
              ?.where((e) => e.selected == true)
              .map((e) => e.code ?? '')
              .toList(),
      status: OrderStatus.pending,
      userAddressId: Storage.addressModel?.userAddressId,
      userId: Storage.userModelGlobal?.userId,
    );

    final OrderCreateResponseModel? data = await Repository.onCreateOrder(
      model,
    );

    if (data == null) return;

    final shipmentResponse = await Repository.createShipment(
      addressId: Storage.addressModel?.userAddressId,
      orderId: data.orderId,
      rateId: previewOrder?.rates?.id,
    );

    final typePayment =
        paymentMethods?.firstWhere((element) => element.selected == true).id;

    if (typePayment == 'cod') {
      await _handleCodPayment(bContext, model, data.orderId);
    } else if (typePayment == 'vnpay' || typePayment == 'zalopay') {
      await _handleOnlinePayment(bContext, model, data.orderId);
    }
  }

  onPreviewOrderAgain() async {
    List<String> promo = [];
    listPromocode?.forEach((element) {
      if (element.selected == true) {
        promo.add(element.code ?? '');
      }
    });
    final data = await Repository.onGetPreviewOrderAgain(
      promoCodes: promo,
      orderOldId: widget.orderId ?? 0,
      userAddressId: Storage.addressModel?.userAddressId ?? 0,
      usePoints: isUseReward? reward:0
    );

    if (data != null) {
      previewOrder = data;
      streamPreviewOrder.set(previewOrder ?? OrderPreviewModel());
      return true;
    } else {
      return false;
    }
  }

  onBuyAgain(BuildContext bContext) async {
    showFullScreenLoading(bContext);
    final CreateOrderRequestModel model = CreateOrderRequestModel(
      branchId: Storage.branchModelGlobal?.branchId,
      cartId: Storage.cartModelGlobal?.cartId,
      grandTotal: previewOrder?.grandTotal,
      // orderDate: formatDateTime(DateTime.now()),
      orderDate: formatDateTime(DateTime.now().add(Duration(days: 1))),

      promoCodes:
          listPromocode
              ?.where((e) => e.selected == true)
              .map((e) => e.code ?? '')
              .toList(),
      status: OrderStatus.pending,
      userAddressId: Storage.addressModel?.userAddressId,
      userId: Storage.userModelGlobal?.userId,
    );

    final data = await Repository.buyAgain(
      usePoints: isUseReward? reward:0,
      orderId: widget.orderId ?? 0,
      userAddressId: Storage.addressModel?.userAddressId ?? 0,
      promoCodes:
          listPromocode
              ?.where((e) => e.selected == true)
              .map((e) => e.code ?? '')
              .toList(),
    );

    if (data == null) return;

    final shipmentResponse = await Repository.createShipment(
      addressId: Storage.addressModel?.userAddressId,
      orderId: data.orderId,
      rateId: previewOrder?.rates?.id,
    );

    final typePayment =
        paymentMethods?.firstWhere((element) => element.selected == true).id;

    if (typePayment == 'cod') {
      await _handleCodPayment(bContext, model, data.orderId);
    } else if (typePayment == 'vnpay' || typePayment == 'zalopay') {
      await _handleOnlinePayment(bContext, model, data.orderId);
    }
  }

  onSetCanBuy(bool value) {
    streamCanBuy.set(value);
  }

  _handleCodPayment(
    BuildContext context,
    CreateOrderRequestModel model,
    int? orderId,
  ) async {
    model.status = OrderStatus.pending;

    final orderUpdate = await Repository.onUpdateOrderAPI(model, orderId);

    if (orderUpdate?.orderId != null) {
      hideFullScreenLoading(context);
      await _showSuccessDialog(context, 'Đặt hàng thành công');
      // Navigator.pop(context);
    } else {
      hideFullScreenLoading(context);
      await _showErrorDialog(context, 'Đặt hàng không thành công');
    }
  }

  _handleOnlinePayment(
    BuildContext context,
    CreateOrderRequestModel model,
    int? orderId,
  ) async {
    model.status = OrderStatus.paying;
    await Repository.onUpdateOrderAPI(model, orderId);

    final paymentUrl = await Repository.onGetVnpayUrl(orderId);

    hideFullScreenLoading(context);
    Navigator.push(
      context,
      MaterialPageRoute(
        builder:
            (_) => VnPayWebViewScreen(
              paymentUrl: paymentUrl,
              onPaymentResult: (success) async {
                if (success) {
                  model.status = OrderStatus.paid;
                  await Repository.onUpdateOrderAPI(model, orderId);
                  await _showSuccessDialog(context, 'Đặt hàng thành công');
                } else {
                  _showErrorDialog(context, 'Đặt hàng không thành công');
                }
              },
            ),
      ),
    );

    model.status = OrderStatus.paid;
    final updatePaying = await Repository.onUpdateOrderAPI(model, orderId);

    if (updatePaying != null) {
      await _showSuccessDialog(context, 'Đặt hàng thành công');
      Navigator.pop(context);
    } else {
      await _showErrorDialog(context, 'Đặt hàng không thành công');
    }
  }

  _showSuccessDialog(BuildContext context, String message) async {
    await showCustomDialog(
      context,
      AppSize.width(),
      AppSize.width(),
      'Thông báo',
      Text(message, style: const TextStyle(color: Colors.black)),
      true,
      false,
      Icon(
        Icons.check_circle_outline_sharp,
        size: 24,
        color: toHexToColor(primaryColorGreen),
      ),
      onClose: () {
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => HomeScreen()),
        );
      },
    );
  }

  _showErrorDialog(BuildContext context, String message) async {
    await showCustomDialog(
      context,
      AppSize.width(),
      AppSize.width(),
      'Thông báo',
      Text(message, style: const TextStyle(color: Colors.black)),
      true,
      false,
      onClose: () {
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => HomeScreen()),
        );
      },
      Icon(Icons.error_outline, size: 24, color: Colors.red),
    );
  }

  openUrlInChrome(String url) {
    if (Platform.isAndroid) {
      final intent = AndroidIntent(
        action: 'action_view',
        data: url,
        package: 'com.android.chrome',
      );
      intent.launch();
    }
  }

  openVNPayUrlWithChrome(String url) async {
    if (url.contains('vnp_Locale=&')) {
      url = url.replaceAll('vnp_Locale=&', 'vnp_Locale=vn&');
    }

    final uri = Uri.parse(url);

    // ✅ Mở bằng Chrome nếu có
    final chromePackage = 'com.android.chrome';
    final canLaunchWithChrome = await canLaunchUrl(
      Uri(
        scheme: 'googlechrome',
        host: uri.host,
        path: uri.path,
        query: uri.query,
      ),
    );

    if (canLaunchWithChrome) {
      final chromeUri = Uri(
        scheme: 'googlechrome',
        host: uri.host,
        path: uri.path,
        query: uri.query,
      );

      await launchUrl(chromeUri);
    } else {
      if (await canLaunchUrl(uri)) {
        await launchUrl(uri, mode: LaunchMode.externalApplication);
      } else {
        throw 'Không thể mở liên kết: $url';
      }
    }
  }

  onRefresh() {
    setState(() {});
  }

  onGetReward() async {
    final response = await Repository.getReward();
    reward = response;
    streamReward.set(response);
  }

  onChangeReward(bool value) {
    isUseReward = value;
    setState(() {
      
    });
  }
}

class PaymentMethod {
  final String? id;
  final String? iconUrl;
  bool? selected;
  Color? color;
  final String? name;
  PaymentMethod({
    this.id,
    this.iconUrl,
    this.selected = false,
    this.color,
    this.name,
  });
}
