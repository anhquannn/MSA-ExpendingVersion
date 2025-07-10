import 'package:msa/feature/domain/entities/order_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';

class OrderDetailResponse {
  int? orderDetailId;
  int? quantity;
  double? unitPrice;
  double? totalPrice;
  String? image;
  String? name;
  String? status;
  ProductModel? product;
  OrderModel? order;
  bool? rated;

  OrderDetailResponse({
    this.orderDetailId,
    this.quantity,
    this.unitPrice,
    this.totalPrice,
    this.image,
    this.name,
    this.status,
    this.product,
    this.order,
    this.rated,
  });

  factory OrderDetailResponse.fromJson(Map<String, dynamic> json) {
    return OrderDetailResponse(
      orderDetailId: json['orderDetailId'],
      quantity: json['quantity'],
      unitPrice: (json['unitPrice'] as num?)?.toDouble(),
      totalPrice: (json['totalPrice'] as num?)?.toDouble(),
      image: json['image'],
      name: json['name'],
      status: json['status'],
      product:
          json['product'] != null
              ? ProductModel.fromJson(json['product'])
              : null,
      order: json['order'] != null ? OrderModel.fromJson(json['order']) : null,
      rated: json['rated'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'orderDetailId': orderDetailId,
      'quantity': quantity,
      'unitPrice': unitPrice,
      'totalPrice': totalPrice,
      'image': image,
      'name': name,
      'status': status,
      'product': product?.toJson(),
      'order': order?.toJson(),
      'rated': rated,
    };
  }
}
