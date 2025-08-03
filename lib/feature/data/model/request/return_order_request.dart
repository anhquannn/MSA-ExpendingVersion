enum ReturnStatus {
  PENDING,
  APPROVED,
  REJECTED,
  SHIPPED,
  RECEIVED,
  REFUNDED,
  EXCHANGED,
}


class ReturnOrderRequest {
  final int? orderId;
  final int? userId;
  final String? reason;
  final List<ReturnOrderItem>? returnOrderItems;

  ReturnOrderRequest({
    this.orderId,
    this.userId,
    this.reason,
    this.returnOrderItems,
  });

  factory ReturnOrderRequest.fromJson(Map<String, dynamic> json) {
    return ReturnOrderRequest(
      orderId: json['orderId'],
      userId: json['userId'],
      reason: json['reason'],
      returnOrderItems: (json['returnOrderItems'] as List<dynamic>)
          .map((e) => ReturnOrderItem.fromJson(e))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() => {
        'orderId': orderId,
        'userId': userId,
        'reason': reason,
        'returnOrderItems': returnOrderItems?.map((e) => e.toJson()).toList(),
      };
}

class ReturnOrderItem {
  final int? orderDetailId;
  final int? quantity;
  final String? reason;
  final List<ImageModel>? images;

  ReturnOrderItem({
    this.orderDetailId,
    this.quantity,
    this.reason,
    this.images,
  });

  factory ReturnOrderItem.fromJson(Map<String, dynamic> json) {
    return ReturnOrderItem(
      orderDetailId: json['orderDetailId'],
      quantity: json['quantity'],
      reason: json['reason'],
      images: (json['images'] as List<dynamic>)
          .map((e) => ImageModel.fromJson(e))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() => {
        'orderDetailId': orderDetailId,
        'quantity': quantity,
        'reason': reason,
        'images': images?.map((e) => e.toJson()).toList(),
      };
}

class ImageModel {
  final String? imageUrl;

  ImageModel({this.imageUrl});

  factory ImageModel.fromJson(Map<String, dynamic> json) {
    return ImageModel(imageUrl: json['imageUrl']);
  }

  Map<String, dynamic> toJson() => {
        'imageUrl': imageUrl,
      };
}
