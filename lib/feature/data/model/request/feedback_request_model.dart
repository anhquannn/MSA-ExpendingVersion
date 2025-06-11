class FeedbackRequest {
  final int rating;
  final String? comments;
  final String createAt;
  final int userId;
  final int productId;

  FeedbackRequest({
    required this.rating,
    this.comments,
    required this.createAt,
    required this.userId,
    required this.productId,
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
