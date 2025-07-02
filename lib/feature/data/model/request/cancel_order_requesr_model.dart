class CancelOrderRequest {
  final String? cancelDate; 
  final String? status;  
  final String? reason;
  final int? refundAmount;
  final int? orderId;

  CancelOrderRequest({
    this.cancelDate,
    this.status,
    this.reason,
    this.refundAmount,
    this.orderId,
  });

  factory CancelOrderRequest.fromJson(Map<String?, dynamic> json) {
    return CancelOrderRequest(
      cancelDate: json['cancelDate'],
      status: json['status'],
      reason: json['reason'],
      refundAmount: json['refundAmount'],
      orderId: json['orderId'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'cancelDate': cancelDate,
      'status': status,
      'reason': reason,
      'refundAmount': refundAmount,
      'orderId': orderId,
    };
  }
}
