class NotificationFilterRequest {
  int? userId;
  String? type;
  bool? isRead;
  int? productId;
  int? orderId;
  int? inventoryId;
  DateTime? fromDate;
  DateTime? toDate;

  String sortBy;
  String sortDirection;

  int page;
  int pageSize;

  NotificationFilterRequest({
    this.userId,
    this.type,
    this.isRead,
    this.productId,
    this.orderId,
    this.inventoryId,
    this.fromDate,
    this.toDate,
    this.sortBy = 'notificationDate',
    this.sortDirection = 'DESC',
    this.page = 1,
    this.pageSize = 10,
  });

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'type': type,
      'isRead': isRead,
      'productId': productId,
      'orderId': orderId,
      'inventoryId': inventoryId,
      'fromDate': fromDate?.toIso8601String(),
      'toDate': toDate?.toIso8601String(),
      'sortBy': sortBy,
      'sortDirection': sortDirection,
      'page': page,
      'pageSize': pageSize,
    };
  }

  factory NotificationFilterRequest.fromJson(Map<String, dynamic> json) {
    return NotificationFilterRequest(
      userId: json['userId'] as int?,
      type: json['type'] as String?,
      isRead: json['isRead'] as bool?,
      productId: json['productId'] as int?,
      orderId: json['orderId'] as int?,
      inventoryId: json['inventoryId'] as int?,
      fromDate: json['fromDate'] != null ? DateTime.parse(json['fromDate']) : null,
      toDate: json['toDate'] != null ? DateTime.parse(json['toDate']) : null,
      sortBy: json['sortBy'] ?? 'notificationDate',
      sortDirection: json['sortDirection'] ?? 'DESC',
      page: json['page'] ?? 1,
      pageSize: json['pageSize'] ?? 10,
    );
  }
}
