import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';

class FeedbackModel {
  final int feedbackId;
  final int rating;
  final String? comments;
  final String? createAt;
  final UserModel user;
  final ProductModel product;

  FeedbackModel({
    required this.feedbackId,
    required this.rating,
    this.comments,
    required this.createAt,
    required this.user,
    required this.product,
  });

  factory FeedbackModel.fromJson(Map<String, dynamic> json) {
    return FeedbackModel(
      feedbackId: json['feedbackId'],
      rating: json['rating'],
      comments: json['comments'],
      createAt: json['createAt'],
      user: UserModel.fromJson(json['user']),
      product: ProductModel.fromJson(json['product']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'feedbackId': feedbackId,
      'rating': rating,
      'comments': comments,
      'createAt': createAt,
      'user': user.toJson(),
      'product': product.toJson(),
    };
  }
}
