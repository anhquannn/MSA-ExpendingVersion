class FeedbackRequest {
  final int? rating;
  final String? comments;
  final String? createAt;
  final int? userId;
  final int? productId;

  FeedbackRequest({
    this.rating,
    this.comments,
    this.createAt,
    this.userId,
    this.productId,
  });

  factory FeedbackRequest.fromJson(Map<String, dynamic> json) {
    return FeedbackRequest(
      rating: json['rating'],
      comments: json['comments'],
      createAt:json['createAt'],
      userId: json['userId'],
      productId: json['productId'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'rating': rating,
      'comments': comments,
      'createAt': createAt,
      'userId': userId,
      'productId': productId,
    };
  }
}
