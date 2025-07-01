import 'package:flutter/src/widgets/framework.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/model/response/get_order_response_model.dart';
import 'package:msa/feature/data/model/response/order_detail_response_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/customer/order_detail/ui/order_detail_screen.dart';
import 'package:rxdart/subjects.dart';

class OrderDetailBloc extends BaseBloc<OrderDetailScreen> {
  List<OrderDetailResponse>? orderDetail;
  final streamOrderDetail = BehaviorSubject<List<OrderDetailResponse>>();

  List<ProductModel> listProduct = [];
  final streamListProduct = BehaviorSubject<List<ProductModel>>();

  List<PromoCodeModel> listPromoCode = [];
  final streamListPromoCode = BehaviorSubject<List<PromoCodeModel>>();

  OrderResponse? model;

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
      case OrderStatus.completed:
        return true;
      case OrderStatus.failed:
        return false;
    }
  }

  onCreateRate() {}

  onRefund() {}
}
