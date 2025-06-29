import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';

class OrderResponse {
  final int? orderId;
  final String? orderDate;
  final double? grandTotal;
  final String? status;
  final BranchModel? branch;
  final CartModel? cart;
  final UserModel? user;

  OrderResponse({
    this.orderId,
    this.orderDate,
    this.grandTotal,
    this.status,
    this.branch,
    this.cart,
    this.user,
  });

  factory OrderResponse.fromJson(Map<String, dynamic> json) {
    return OrderResponse(
      orderId: json['orderId'],
      orderDate: json['orderDate'],
      grandTotal: json['grandTotal'],
      status: json['status'],
      branch: json['branch'] != null ? BranchModel.fromJson(json['branch']) : null,
      cart: json['cart'] != null ? CartModel.fromJson(json['cart']) : null,
      user: json['user'] != null ? UserModel.fromJson(json['user']) : null,
    );
  }

  Map<String, dynamic> toJson() => {
        'orderId': orderId,
        'orderDate': orderDate,
        'grandTotal': grandTotal,
        'status': status,
        'branch': branch?.toJson(),
        'cart': cart?.toJson(),
        'user': user?.toJson(),
      };
}
