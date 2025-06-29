class ReportModel {
  final double? successPercent;
  final double? returnPercent;
  final double? avgTimeDelivery;
  final double? avgTimeDeliveryFormat;
  final double? scorePercent;

  ReportModel({
    this.successPercent,
    this.returnPercent,
    this.avgTimeDelivery,
    this.avgTimeDeliveryFormat,
    this.scorePercent,
  });

  factory ReportModel.fromJson(Map<String, dynamic> json) {
    return ReportModel(
      successPercent: (json['success_percent'] as num?)?.toDouble() ?? 0,
      returnPercent: (json['return_percent'] as num?)?.toDouble() ?? 0,
      avgTimeDelivery: (json['avg_time_delivery'] as num?)?.toDouble() ?? 0,
      avgTimeDeliveryFormat:
          (json['avg_time_delivery_format'] as num?)?.toDouble() ?? 0,
      scorePercent: (json['score_percent'] as num?)?.toDouble() ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'success_percent': successPercent,
      'return_percent': returnPercent,
      'avg_time_delivery': avgTimeDelivery,
      'avg_time_delivery_format': avgTimeDeliveryFormat,
      'score_percent': scorePercent,
    };
  }
}

class RateModel {
  final String? id;
  final String? carrierName;
  final String? carrierLogo;
  final String? carrierShortName;
  final String? service;
  final String? expected;
  final bool? isApplyOnly;
  final double? promotionId;
  final double? discount;
  final double? weightFee;
  final double? locationFirstFee;
  final double? locationStepFee;
  final double? remoteAreaFee;
  final double? oilFee;
  final double? locationFee;
  final double? codFee;
  final double? serviceFee;
  final double? totalFee;
  final double? totalAmount;
  final double? totalAmountCarrier;
  final double? totalAmountShop;
  final double? priceTableId;
  final double? insurranceFee;
  final int? returnFee;
  final ReportModel? report;

  RateModel({
    this.id,
    this.carrierName,
    this.carrierLogo,
    this.carrierShortName,
    this.service,
    this.expected,
    this.isApplyOnly,
    this.promotionId,
    this.discount,
    this.weightFee,
    this.locationFirstFee,
    this.locationStepFee,
    this.remoteAreaFee,
    this.oilFee,
    this.locationFee,
    this.codFee,
    this.serviceFee,
    this.totalFee,
    this.totalAmount,
    this.totalAmountCarrier,
    this.totalAmountShop,
    this.priceTableId,
    this.insurranceFee,
    this.returnFee,
    this.report,
  });

  factory RateModel.fromJson(Map<String, dynamic> json) {
    return RateModel(
      id: json['id'] ?? '',
      carrierName: json['carrier_name'] ?? '',
      carrierLogo: json['carrier_logo'] ?? '',
      carrierShortName: json['carrier_short_name'] ?? '',
      service: json['service'] ?? '',
      expected: json['expected'] ?? '',
      isApplyOnly: json['is_apply_only'] ?? false,
      promotionId: (json['promotion_id'] as num?)?.toDouble() ?? 0,
      discount: (json['discount'] as num?)?.toDouble() ?? 0,
      weightFee: (json['weight_fee'] as num?)?.toDouble() ?? 0,
      locationFirstFee: (json['location_first_fee'] as num?)?.toDouble() ?? 0,
      locationStepFee: (json['location_step_fee'] as num?)?.toDouble() ?? 0,
      remoteAreaFee: (json['remote_area_fee'] as num?)?.toDouble() ?? 0,
      oilFee: (json['oil_fee'] as num?)?.toDouble() ?? 0,
      locationFee: (json['location_fee'] as num?)?.toDouble() ?? 0,
      codFee: (json['cod_fee'] as num?)?.toDouble() ?? 0,
      serviceFee: (json['service_fee'] as num?)?.toDouble() ?? 0,
      totalFee: (json['total_fee'] as num?)?.toDouble() ?? 0,
      totalAmount: (json['total_amount'] as num?)?.toDouble() ?? 0,
      totalAmountCarrier:
          (json['total_amount_carrier'] as num?)?.toDouble() ?? 0,
      totalAmountShop: (json['total_amount_shop'] as num?)?.toDouble() ?? 0,
      priceTableId: (json['price_table_id'] as num?)?.toDouble() ?? 0,
      insurranceFee: (json['insurrance_fee'] as num?)?.toDouble() ?? 0,
      returnFee: json['return_fee'] ?? 0,
      report: ReportModel.fromJson(json['report']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'carrier_name': carrierName,
      'carrier_logo': carrierLogo,
      'carrier_short_name': carrierShortName,
      'service': service,
      'expected': expected,
      'is_apply_only': isApplyOnly,
      'promotion_id': promotionId,
      'discount': discount,
      'weight_fee': weightFee,
      'location_first_fee': locationFirstFee,
      'location_step_fee': locationStepFee,
      'remote_area_fee': remoteAreaFee,
      'oil_fee': oilFee,
      'location_fee': locationFee,
      'cod_fee': codFee,
      'service_fee': serviceFee,
      'total_fee': totalFee,
      'total_amount': totalAmount,
      'total_amount_carrier': totalAmountCarrier,
      'total_amount_shop': totalAmountShop,
      'price_table_id': priceTableId,
      'insurrance_fee': insurranceFee,
      'return_fee': returnFee,
      'report': report?.toJson(),
    };
  }
}

class OrderPreviewModel {
  final double? totalCost;
  final double? discount;
  final double? grandTotal;
  final RateModel? rates;

  OrderPreviewModel({
    this.totalCost,
    this.discount,
    this.grandTotal,
    this.rates,
  });

  factory OrderPreviewModel.fromJson(Map<String, dynamic> json) {
    return OrderPreviewModel(
      totalCost: (json['totalCost'] as num?)?.toDouble() ?? 0,
      discount: (json['discount'] as num?)?.toDouble() ?? 0,
      grandTotal: (json['grandTotal'] as num?)?.toDouble() ?? 0,
      rates: RateModel.fromJson(json['rates']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'totalCost': totalCost,
      'discount': discount,
      'grandTotal': grandTotal,
      'rates': rates?.toJson(),
    };
  }
}
