class UpdateNotificationRequest {
  final int? notificationId;
  final String? notificationType;
  final String? deviceTokens;
  final String? deviceIds;
  final DateTime? notificationDate;
  final bool? isRead;
  final String? message;
  final int? userId;
  final int? productId;
  final int? orderId;
  final int? inventoryId;

  UpdateNotificationRequest({
    this.notificationId,
    this.notificationType,
    this.deviceTokens,
    this.deviceIds,
    this.notificationDate,
    this.isRead,
    this.message,
    this.userId,
    this.productId,
    this.orderId,
    this.inventoryId,
  });

  Map<String, dynamic> toJson() {
    return {
      if (notificationId != null) 'notificationId': notificationId,
      if (notificationType != null) 'notificationType': notificationType,
      if (deviceTokens != null) 'deviceTokens': deviceTokens,
      if (deviceIds != null) 'deviceIds': deviceIds,
      if (notificationDate != null)
        'notificationDate': notificationDate!.toIso8601String(),
      if (isRead != null) 'read': isRead,
      if (message != null) 'message': message,
      if (userId != null) 'userId': userId,
      if (productId != null) 'productId': productId,
      if (orderId != null) 'orderId': orderId,
      if (inventoryId != null) 'inventoryId': inventoryId,
    };
  }
}
