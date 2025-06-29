import 'package:msa/core/utils/utility.dart';

class CreateOrderRequestModel {
  String? orderDate;
  double? grandTotal;
  OrderStatus? status;
  int? branchId;
  int? cartId;
  int? userId;
  int? userAddressId;
  List<String>? promoCodes;
  DeliveryInfoRequestModel? deliveryInfo;

  CreateOrderRequestModel({
    this.orderDate,
    this.grandTotal,
    this.status,
    this.branchId,
    this.cartId,
    this.userId,
    this.userAddressId,
    this.promoCodes,
    this.deliveryInfo,
  });

  factory CreateOrderRequestModel.fromJson(Map<String, dynamic> json) {
    return CreateOrderRequestModel(
      orderDate: json['orderDate'],
      grandTotal: json['grandTotal']?.toDouble() ?? 0.0,
      status: OrderStatusExtension.fromString(json['status'])!,
      branchId: json['branchId'],
      cartId: json['cartId'],
      userId: json['userId'],
      userAddressId: json['userAddressId'],
      promoCodes: List<String>.from(json['promoCodes'] ?? []),
      deliveryInfo: DeliveryInfoRequestModel.fromJson(json['deliveryInfo']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'orderDate': orderDate,
      'grandTotal': grandTotal,
      'status': status?.name,
      'branchId': branchId,
      'cartId': cartId,
      'userId': userId,
      'userAddressId': userAddressId,
      'promoCodes': promoCodes,
      'deliveryInfo': deliveryInfo?.toJson(),
    };
  }
}

class DeliveryInfoRequestModel {
  String deliveryDate;
  String street;
  String ward;
  String district;
  String city;
  bool cod;
  double length;
  double width;
  double height;
  double weight;
  String metadata;
  String status;

  DeliveryInfoRequestModel({
    required this.deliveryDate,
    required this.street,
    required this.ward,
    required this.district,
    required this.city,
    required this.cod,
    required this.length,
    required this.width,
    required this.height,
    required this.weight,
    required this.metadata,
    required this.status,
  });

  factory DeliveryInfoRequestModel.fromJson(Map<String, dynamic> json) {
    return DeliveryInfoRequestModel(
      deliveryDate:json['deliveryDate'],
      street: json['street'],
      ward: json['ward'],
      district: json['district'],
      city: json['city'],
      cod: json['cod'] == true || json['cod'] == 1,
      length: json['length']?.toDouble() ?? 0.0,
      width: json['width']?.toDouble() ?? 0.0,
      height: json['height']?.toDouble() ?? 0.0,
      weight: json['weight']?.toDouble() ?? 0.0,
      metadata: json['metadata'],
      status: json['status'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'deliveryDate': deliveryDate,
      'street': street,
      'ward': ward,
      'district': district,
      'city': city,
      'cod': cod,
      'length': length,
      'width': width,
      'height': height,
      'weight': weight,
      'metadata': metadata,
      'status': status,
    };
  }
}
