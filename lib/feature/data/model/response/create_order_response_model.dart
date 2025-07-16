import 'package:msa/feature/domain/entities/order_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';

class OrderCreateResponseModel {
  final int? orderId;
  final String? orderDate;
  final double? grandTotal;
  final String? status;
  final BranchModel? branch;
  final CartModel? cart;
  final UserModel? user;
  final DeliveryInfoModel? deliveryInfo;
  final List<PaymentModel>? payments;
  final List<PromoCodeModel>? promoCodes;
  final dynamic? rewardPointTransactions;
  final dynamic? promoCodeUsages;

  OrderCreateResponseModel({
    this.orderId,
    this.orderDate,
    this.grandTotal,
    this.status,
    this.branch,
    this.cart,
    this.user,
    this.deliveryInfo,
    this.payments,
    this.promoCodes,
    this.rewardPointTransactions,
    this.promoCodeUsages,
  });

  factory OrderCreateResponseModel.fromJson(Map<String, dynamic> json) {
    return OrderCreateResponseModel(
      orderId: json['orderId'],
      orderDate: json['orderDate'],
      grandTotal: (json['grandTotal'] as num?)?.toDouble(),
      status: json['status'],
      branch:
          json['branch'] != null ? BranchModel.fromJson(json['branch']) : null,
      cart: json['cart'] != null ? CartModel.fromJson(json['cart']) : null,
      user: json['user'] != null ? UserModel.fromJson(json['user']) : null,
      deliveryInfo:
          json['deliveryInfo'] != null
              ? DeliveryInfoModel.fromJson(json['deliveryInfo'])
              : null,
      payments:
          json['payments'] != null
              ? (json['payments'] as List)
                  .map((e) => PaymentModel.fromJson(e))
                  .toList()
              : null,
      promoCodes:
          json['promoCodes'] != null
              ? (json['promoCodes'] as List)
                  .map((e) => PromoCodeModel.fromJson(e))
                  .toList()
              : null,
      rewardPointTransactions: json['rewardPointTransactions'], // tương tự
      promoCodeUsages: json['promoCodeUsages'], // tương tự
    );
  }

  @override
  String toString() {
    return '''
🧾 OrderCreateResponseModel:
📦 orderId: $orderId
📅 orderDate: $orderDate
💵 grandTotal: $grandTotal
📌 status: $status
🏬 branch: ${branch?.toJson()}
🛒 cart: ${cart?.toJson()}
👤 user: ${user?.toJson()}
🚚 deliveryInfo: ${deliveryInfo}
💳 payments: ${payments?.map((e) => e.toJson()).toList()}
🏷️ promoCodes: ${promoCodes?.map((e) => e.toJson()).toList()}
🎁 rewardPointTransactions: $rewardPointTransactions
🏷️ promoCodeUsages: $promoCodeUsages
''';
  }
}
