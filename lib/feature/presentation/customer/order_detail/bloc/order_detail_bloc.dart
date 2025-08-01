import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/cancel_order_requesr_model.dart';
import 'package:msa/feature/data/model/request/feedback_request_model.dart';
import 'package:msa/feature/data/model/response/get_order_response_model.dart';
import 'package:msa/feature/data/model/response/order_detail_response_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/feature/presentation/customer/order_detail/ui/order_detail_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/custom_loading.dart';
import 'package:msa/widget/custom_rating.dart';
import 'package:rxdart/subjects.dart';

import '../ui/return_product.dart';

class OrderDetailBloc extends BaseBloc<OrderDetailScreen> {
  List<OrderDetailResponse>? orderDetail;
  final streamOrderDetail = BehaviorSubject<List<OrderDetailResponse>>();

  List<ProductModel> listProduct = [];
  final streamListProduct = BehaviorSubject<List<ProductModel>>();

  List<PromoCodeModel> listPromoCode = [];
  final streamListPromoCode = BehaviorSubject<List<PromoCodeModel>>();

  OrderResponse? model;

  TextEditingController feedBackContoller = TextEditingController();

  int _rating = 5;
  String _comment = '';

  @override
  String get contextKey => 'OrderDetailScreen';

  @override
  Widget build(BuildContext viewContext) => widget.build(viewContext);
  @override
  void onDispose() {
    // TODO: implement onDispose
  }

  @override
  void onInit() {
    model = widget.order;
  }

  @override
  void onReady() {
    Future.wait<void>([onGetOrderDetail()]);
  }

  @override
  void onResumed() {
    // TODO: implement onResumed
  }

  onGetOrderDetail() async {
    final List<OrderDetailResponse> response =
        await Repository.onGetOrderDetail(orderId: widget.order?.orderId);
    if (response != null) {
      orderDetail = response;
      streamOrderDetail.set(response);

      for (OrderDetailResponse i in response) {
        if (i.product != null) {
          listProduct.add(i.product ?? ProductModel());
        }
      }
      streamListProduct.set(listProduct);

      for (OrderDetailResponse i in response) {
        if (i.order?.promoCodes != null) {
          i.order?.promoCodes?.forEach((element) {
            listPromoCode.add(element);
          });
        }
      }
      streamListPromoCode.set(listPromoCode);
    }
  }

  onBuyAgain(BuildContext bContext) async {
    Navigator.push(
      bContext,
      MaterialPageRoute(
        builder:
            (bContext) => CreateOrderScreen(
              isBuyAgain: true,
              orderDetail: orderDetail,
              orderId: widget.order?.orderId,
            ),
      ),
    );
  }

  getTotalProduct() {
    double data = 0;
    orderDetail?.forEach((element) {
      data += element.totalPrice ?? 0;
    });
    return data;
  }

  getTotalShip() {
    return (model?.grandTotal ?? 0) - getTotalProduct();
  }

  onCheckStatus(OrderStatus status) {
    switch (status) {
      case OrderStatus.completed:
        return true;
      case OrderStatus.pending:
        return true;
      case OrderStatus.paying:
        return true;
      case OrderStatus.paid:
        return false;
      case OrderStatus.delivering:
        return false;
      case OrderStatus.shipped:
        return true;
      case OrderStatus.cancelling:
        return false;
      case OrderStatus.cancelled:
        return false;
      case OrderStatus.failed:
        return false;
      case OrderStatus.returnOrder:
        return true;
    }
  }

  onCreateRatesForProducts({required BuildContext context}) async {
    final List<Map<String, dynamic>> products =
        orderDetail
            ?.map(
              (element) => {
                'productId': element.product?.productId,
                'productName': element.product?.name,
                'orderDetailId': element.orderDetailId,
              },
            )
            .toList() ??
        [];

    final userId = Storage.userModelGlobal?.userId;

    await showRatingDialog(context, products: products, userId: userId);
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

  showRatingDialog(
    BuildContext context, {
    required List<Map<String, dynamic>> products,
    required int? userId,
  }) async {
    final width = MediaQuery.of(context).size.width * 0.9;
    final height = width;
    final now = DateTime.now();
    final GlobalKey<RatingContentState> contentKey = GlobalKey();

    await showCustomDialog(
      context,
      width,
      height,
      'Đánh giá sản phẩm',
      RatingContent(
        key: contentKey,
        onSubmit: (rating, comment) async {
          Navigator.of(context).pop();

          final List<Future<void>> requests = [];

          for (var product in products) {
            final productId = product['productId'];
            final productName = product['productName'];
            final orderDetailId = product['orderDetailId'];

            final model = FeedbackRequest(
              comments: comment,
              createAt: formatDateTime(now),
              rating: rating,
              productId: productId,
              userId: userId,
              orderDetailId: orderDetailId,
            );

            final request = Repository.createFeedbackAPI(model).then((
              response,
            ) {
              if (response == null) {
                showCustomDialog(
                  context,
                  AppSize.width(),
                  AppSize.width(),
                  'Thông báo',
                  Text('Không thể tạo đánh giá cho sản phẩm "$productName"'),
                  true,
                  false,
                  Icon(
                    Icons.warning,
                    color: toHexToColor(primaryButtonColorRed),
                  ),
                );
              } else {
                _showSuccessDialog(context, 'Tạo đánh giá thành công');
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => HomeScreen()),
                );
              }
            });

            requests.add(request);
          }

          await Future.wait(requests); // Gọi tất cả API
        },
      ),
      false,
      true,
      null,
      onSubmit: () {
        contentKey.currentState?.callSubmit();
      },
      onClose: () {
        Navigator.of(context).pop();
      },
    );
  }

  onCancelProducts({required BuildContext context}) async {
    final List<Map<String, dynamic>> products =
        orderDetail
            ?.map(
              (element) => {
                'productId': element.product?.productId,
                'productName': element.product?.name,
              },
            )
            .toList() ??
        [];

    await showReturnDialog(context, products: products);
  }

  onReturnProducts({required BuildContext context}) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ReturnProductScreen(orderId: model?.orderId),
      ),
    );
  }

  showReturnDialog(
    BuildContext context, {
    required List<Map<String, dynamic>> products,
  }) async {
    final width = MediaQuery.of(context).size.width * 0.9;
    final height = width;
    final GlobalKey<ReturnContentState> contentKey = GlobalKey();

    await showCustomDialog(
      context,
      width,
      height,
      'Yêu cầu trả hàng',
      ReturnContent(
        key: contentKey,
        onSubmit: (reason) async {
          Navigator.of(context).pop();

          int quantity = 0;
          for (OrderDetailResponse i in orderDetail ?? []) {
            quantity += i.quantity ?? 0;
          }
          final model = CancelOrderRequest(
            cancelDate: formatDateTime(DateTime.now()),
            orderId: widget.order?.orderId,
            status: OrderStatus.cancelled.name,
            reason: reason,
            refundAmount: quantity,
          );

          final response = await Repository.createCanceledOrder(model);

          if (response == false) {
            showCustomDialog(
              context,
              AppSize.width(),
              AppSize.width(),
              'Lỗi trả hàng',
              const Text('Không thể gửi yêu cầu trả hàng.'),
              true,
              false,
              Icon(Icons.error, color: toHexToColor(primaryButtonColorRed)),
            );
          } else {
            showCustomDialog(
              context,
              AppSize.width(),
              AppSize.width(),
              'Thành công',
              const Text('Yêu cầu trả hàng đã được gửi.'),
              true,
              false,
              const Icon(Icons.check_circle, color: Colors.green),
              onClose: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => HomeScreen()),
                );
              },
            );
          }
        },
      ),
      false,
      true,
      null,
      onSubmit: () {
        contentKey.currentState?.callSubmit();
      },
      onClose: () {
        Navigator.of(context).pop();
      },
    );
  }
}
