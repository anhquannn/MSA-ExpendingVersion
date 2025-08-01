import '../../../domain/entities/order_model.dart';
import '../../../domain/entities/user_model.dart';

class ReturnOrderModel {
  final int? returnOrderId;
  final OrderModel? order;
  final UserModel? user;
  final double? refundAmount;
  final String? status;
  final String? shippingCode;
  final String? shippingStatus;
  final String? reason;
  final String? createdAt;
  final String? updatedAt;
  final List<ReturnOrderItemModel>? returnOrderItems;

  ReturnOrderModel({
    this.returnOrderId,
    this.order,
    this.user,
    this.refundAmount,
    this.status,
    this.shippingCode,
    this.shippingStatus,
    this.reason,
    this.createdAt,
    this.updatedAt,
    this.returnOrderItems,
  });

  factory ReturnOrderModel.fromJson(Map<String, dynamic> json) {
    return ReturnOrderModel(
      returnOrderId: json['returnOrderId'],
      order: OrderModel.fromJson(json['order']),
      user: UserModel.fromJson(json['user']),
      refundAmount: json['refundAmount'],
      status: json['status'],
      shippingCode: json['shippingCode'],
      shippingStatus: json['shippingStatus'],
      reason: json['reason'],
      createdAt: json['createdAt'],
      updatedAt: json['updatedAt'],
      returnOrderItems:
          (json['returnOrderItems'] as List)
              .map((e) => ReturnOrderItemModel.fromJson(e))
              .toList(),
    );
  }
}

class ReturnOrderItemModel {
  final int? itemId;
  final OrderDetailModel? orderDetail;
  final int? quantity;
  final String? conditionNote;
  final String? reason;
  final List<ReturnOrderImageModel>? images;

  ReturnOrderItemModel({
    this.itemId,
    this.orderDetail,
    this.quantity,
    this.conditionNote,
    this.reason,
    this.images,
  });

  factory ReturnOrderItemModel.fromJson(Map<String, dynamic> json) {
    return ReturnOrderItemModel(
      itemId: json['itemId'],
      orderDetail: OrderDetailModel.fromJson(json['orderDetail']),
      quantity: json['quantity'],
      conditionNote: json['conditionNote'],
      reason: json['reason'],
      images:
          (json['images'] as List)
              .map((e) => ReturnOrderImageModel.fromJson(e))
              .toList(),
    );
  }
}

class ReturnOrderImageModel {
  final int? imageId;
  final String? imageUrl;
  final String? createdAt;

  ReturnOrderImageModel({this.imageId, this.imageUrl, this.createdAt});

  factory ReturnOrderImageModel.fromJson(Map<String, dynamic> json) {
    return ReturnOrderImageModel(
      imageId: json['imageId'],
      imageUrl: json['imageUrl'],
      createdAt: json['createdAt'],
    );
  }
}
