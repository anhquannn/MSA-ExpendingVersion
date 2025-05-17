enum PromoCodeStatus { unavailable, available }

class PromoCodeModel {
  final int? promoCodeId;
  final String? name;
  final String? code;
  final String? description;
  final String? startDate;
  final String? endDate;
  final String? status;
  final String? discountType;
  final double? discountPercentage;
  final double? minimumOrderValue;
  final dynamic campaignResponse;

  PromoCodeModel({
    this.promoCodeId,
    this.name,
    this.code,
    this.description,
    this.startDate,
    this.endDate,
    this.status,
    this.discountType,
    this.discountPercentage,
    this.minimumOrderValue,
    this.campaignResponse,
  });

factory PromoCodeModel.fromJson(Map<String, dynamic> json) {
  return PromoCodeModel(
    promoCodeId: json['promoCodeId'] is int
        ? json['promoCodeId']
        : int.tryParse(json['promoCodeId'].toString()),
    name: json['name']?.toString(),
    code: json['code']?.toString(),
    description: json['description']?.toString(),
    startDate: json['startDate']??'',
    endDate: json['endDate']??'',
    status: json['status']??'',
    discountType: json['discountType']?.toString(),
    discountPercentage: json['discountPercentage'] is double
        ? json['discountPercentage']
        : double.tryParse(json['discountPercentage'].toString()),
    minimumOrderValue: json['minimumOrderValue'] is double
        ? json['minimumOrderValue']
        : double.tryParse(json['minimumOrderValue'].toString()),
    campaignResponse: json['campaignResponse'],
  );
}


  Map<String, dynamic> toJson() {
    return {
      'promoCodeId': promoCodeId,
      'name': name,
      'code': code,
      'description': description,
      'startDate': startDate,
      'endDate': endDate,
      'status': status,
      'discountType': discountType,
      'discountPercentage': discountPercentage,
      'minimumOrderValue': minimumOrderValue,
      'campaignResponse': campaignResponse,
    };
  }
}
