import 'package:msa/core/utils/utility.dart';

class PaymentRequestModel {
  final String? paymentMethod;
  final String? paymentDate;
  final OrderStatus? status;
  final double? grandTotal;
  final String? transactionId;
  final String? bankCode;
  final String? bankTranNo;
  final String? responseCode;
  final String? updateDate;
  final int? userId;
  final int? orderId;

  PaymentRequestModel({
    this.paymentMethod,
    this.paymentDate,
    this.status,
    this.grandTotal,
    this.transactionId,
    this.bankCode,
    this.bankTranNo,
    this.responseCode,
    this.updateDate,
    this.userId,
    this.orderId,
  });

  factory PaymentRequestModel.fromJson(Map<String, dynamic> json) {
    return PaymentRequestModel(
      paymentMethod: json['paymentMethod'],
      paymentDate: json['paymentDate'],
      status: OrderStatusExtension.fromString(json['status'])!,
      grandTotal: (json['grandTotal'] ?? 0).toDouble(),
      transactionId: json['transactionId'],
      bankCode: json['bankCode'],
      bankTranNo: json['bankTranNo'],
      responseCode: json['responseCode'],
      updateDate: json['updateDate'],
      userId: json['userId'],
      orderId: json['orderId'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'paymentMethod': paymentMethod,
      'paymentDate': paymentDate,
      'status': status?.name,
      'grandTotal': grandTotal,
      'transactionId': transactionId,
      'bankCode': bankCode,
      'bankTranNo': bankTranNo,
      'responseCode': responseCode,
      'updateDate': updateDate,
      'userId': userId,
      'orderId': orderId,
    };
  }
}
