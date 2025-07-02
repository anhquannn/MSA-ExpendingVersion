class FeedbackFilterRequest {
  final int? productId;
  final int? userId;
  final int? minRating;
  final int? maxRating;
  final String? fromDate; // ISO-8601 string
  final String? toDate;   // ISO-8601 string
  final String sortBy;
  final String sortDirection;
  final int page;
  final int pageSize;

  FeedbackFilterRequest({
    this.productId,
    this.userId,
    this.minRating,
    this.maxRating,
    this.fromDate,
    this.toDate,
    this.sortBy = 'createdAt',
    this.sortDirection = 'DESC',
    this.page = 1,
    this.pageSize = 10,
  });

  factory FeedbackFilterRequest.fromJson(Map<String, dynamic> json) {
    return FeedbackFilterRequest(
      productId: json['productId'],
      userId: json['userId'],
      minRating: json['minRating'],
      maxRating: json['maxRating'],
      fromDate: json['fromDate'],
      toDate: json['toDate'],
      sortBy: json['sortBy'] ?? 'createdAt',
      sortDirection: json['sortDirection'] ?? 'DESC',
      page: json['page'] ?? 1,
      pageSize: json['pageSize'] ?? 10,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'productId': productId,
      'userId': userId,
      'minRating': minRating,
      'maxRating': maxRating,
      'fromDate': fromDate,
      'toDate': toDate,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
      'page': page,
      'pageSize': pageSize,
    };
  }
}
