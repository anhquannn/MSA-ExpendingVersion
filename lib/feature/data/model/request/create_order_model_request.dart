class CreateOrderRequestModel {
  DateTime orderDate;
  double grandTotal;
  String status;
  int branchId;
  int cartId;
  int userId;
  List<String> promoCodes;
  DeliveryInfoRequestModel deliveryInfo;

  CreateOrderRequestModel({
    required this.orderDate,
    required this.grandTotal,
    required this.status,
    required this.branchId,
    required this.cartId,
    required this.userId,
    required this.promoCodes,
    required this.deliveryInfo,
  });

  factory CreateOrderRequestModel.fromJson(Map<String, dynamic> json) {
    return CreateOrderRequestModel(
      orderDate: DateTime.parse(json['orderDate']),
      grandTotal: json['grandTotal']?.toDouble() ?? 0.0,
      status: json['status'],
      branchId: json['branchId'],
      cartId: json['cartId'],
      userId: json['userId'],
      promoCodes: List<String>.from(json['promoCodes'] ?? []),
      deliveryInfo: DeliveryInfoRequestModel.fromJson(json['deliveryInfo']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'orderDate': orderDate.toIso8601String(),
      'grandTotal': grandTotal,
      'status': status,
      'branchId': branchId,
      'cartId': cartId,
      'userId': userId,
      'promoCodes': promoCodes,
      'deliveryInfo': deliveryInfo.toJson(),
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
