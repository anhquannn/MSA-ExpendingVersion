import '../../../domain/entities/order_model.dart';
import '../../../domain/entities/user_model.dart';


class ReturnOrderResult {
  final int returnOrderId;
  final OrderModel order;
  final UserModel user;
  final double refundAmount;
  final String status;
  final String? shippingCode;
  final String? shippingStatus;
  final String reason;
  final String createdAt;
  final String updatedAt;
  final List<ReturnOrderItem> returnOrderItems;

  ReturnOrderResult({
    required this.returnOrderId,
    required this.order,
    required this.user,
    required this.refundAmount,
    required this.status,
    required this.reason,
    required this.createdAt,
    required this.updatedAt,
    required this.returnOrderItems,
    this.shippingCode,
    this.shippingStatus,
  });

  factory ReturnOrderResult.fromJson(Map<String, dynamic> json) {
    return ReturnOrderResult(
      returnOrderId: json['returnOrderId'],
      order: OrderModel.fromJson(json['order']),
      user: UserModel.fromJson(json['user']),
      refundAmount: (json['refundAmount'] as num).toDouble(),
      status: json['status'],
      reason: json['reason'],
      createdAt: json['createdAt'],
      updatedAt: json['updatedAt'],
      shippingCode: json['shippingCode'],
      shippingStatus: json['shippingStatus'],
      returnOrderItems: (json['returnOrderItems'] as List<dynamic>)
          .map((e) => ReturnOrderItem.fromJson(e))
          .toList(),
    );
  }
}
class ReturnOrderItem {
  final int itemId;
  final OrderDetailModel orderDetail;
  final int quantity;
  final String? conditionNote;
  final String reason;
  final List<ReturnOrderImage> images;

  ReturnOrderItem({
    required this.itemId,
    required this.orderDetail,
    required this.quantity,
    this.conditionNote,
    required this.reason,
    required this.images,
  });

  factory ReturnOrderItem.fromJson(Map<String, dynamic> json) {
    return ReturnOrderItem(
      itemId: json['itemId'],
      orderDetail: OrderDetailModel.fromJson(json['orderDetail']),
      quantity: json['quantity'],
      conditionNote: json['conditionNote'],
      reason: json['reason'],
      images: (json['images'] as List)
          .map((e) => ReturnOrderImage.fromJson(e))
          .toList(),
    );
  }
}

class ReturnOrderImage {
  final int imageId;
  final String imageUrl;
  final String createdAt;

  ReturnOrderImage({
    required this.imageId,
    required this.imageUrl,
    required this.createdAt,
  });

  factory ReturnOrderImage.fromJson(Map<String, dynamic> json) {
    return ReturnOrderImage(
      imageId: json['imageId'],
      imageUrl: json['imageUrl'],
      createdAt: json['createdAt'],
    );
  }
}
