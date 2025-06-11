import 'package:msa/feature/domain/entities/user_model.dart';

enum CartModelStatus { active, inactive }

class CartModel {
  final int? cartId;
  final String status;
  final UserModel? userModel;

  CartModel({this.cartId, required this.status, this.userModel});

  factory CartModel.fromJson(Map<String, dynamic> json) {
    return CartModel(
      cartId: int.tryParse(json['cartId'].toString()),
      status: json['status'] ?? '',
      userModel:
          json['userModel'] != null
              ? UserModel.fromJson(json['userModel'] as Map<String, dynamic>)
              : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'cartId': cartId,
      'status': status,
      'userModel': userModel?.toJson(),
    };
  }
}
