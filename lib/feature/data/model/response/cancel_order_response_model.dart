import 'package:msa/feature/domain/entities/order_model.dart';

class ResponseCancelOrder {
  final int? cancelOrderId;
  final String? cancelDate;
  final String? status;
  final String? reason;
  final double? refundAmount;
  final OrderModel? orderResponse; // hoặc kiểu cụ thể nếu có

  ResponseCancelOrder({
    this.cancelOrderId,
    this.cancelDate,
    this.status,
    this.reason,
    this.refundAmount,
    this.orderResponse,
  });

  factory ResponseCancelOrder.fromJson(Map<String, dynamic> json) {
    return ResponseCancelOrder(
      cancelOrderId: json['cancelOrderId'],
      cancelDate: json['cancelDate'],
      status: json['status'],
      reason: json['reason'],
      refundAmount:
          json['refundAmount'] != null
              ? (json['refundAmount'] as num).toDouble()
              : null,
      orderResponse:
          json['orderResponse'] != null
              ? OrderModel.fromJson(json['orderResponse'])
              : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'cancelOrderId': cancelOrderId,
      'cancelDate': cancelDate,
      'status': status,
      'reason': reason,
      'refundAmount': refundAmount,
      'orderResponse': orderResponse,
    };
  }
}
