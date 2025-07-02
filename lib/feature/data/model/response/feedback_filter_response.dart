import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';

class FeedbacFilterkResponse {
  final int? feedbackId;
  final int? rating;
  final String? comments;
  final String? createdAt;
  final UserModel? user;
  final ProductModel? product;

  FeedbacFilterkResponse({
    this.feedbackId,
    this.rating,
    this.comments,
    this.createdAt,
    this.user,
    this.product,
  });

  factory FeedbacFilterkResponse.fromJson(Map<String, dynamic> json) {
    return FeedbacFilterkResponse(
      feedbackId: json['feedbackId'],
      rating: json['rating'],
      comments: json['comments'],
      createdAt: json['createdAt'],
      user: json['user'] != null ? UserModel.fromJson(json['user']) : null,
      product:
          json['product'] != null ? ProductModel.fromJson(json['product']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'feedbackId': feedbackId,
      'rating': rating,
      'comments': comments,
      'createdAt': createdAt,
      'user': user?.toJson(),
      'product': product?.toJson(),
    };
  }
}
