class PreviewOrderResponse {
  final double? totalCost;
  final double? discount;
  final double? grandTotal;

  PreviewOrderResponse({this.totalCost, this.discount, this.grandTotal});

  factory PreviewOrderResponse.fromJson(Map<String, dynamic> json) {
    return PreviewOrderResponse(
      totalCost: (json['totalCost'] as num?)?.toDouble(),
      discount: (json['discount'] as num?)?.toDouble(),
      grandTotal: (json['grandTotal'] as num?)?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'totalCost': totalCost,
      'discount': discount,
      'grandTotal': grandTotal,
    };
  }
}
