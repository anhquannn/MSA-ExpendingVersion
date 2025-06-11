import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';

class CartItemModel {
  int? cartItemId;
  double? price;
  int? quantity;
  ProductModel? product;
  CartModel? cart;
  bool? selected;

  CartItemModel({
    this.cartItemId,
    this.price,
    this.quantity,
    this.product,
    this.cart,
    this.selected,
  });

  factory CartItemModel.fromJson(Map<String, dynamic> json) {
    return CartItemModel(
      cartItemId: json['cartItemId'],
      price: (json['price'] as num?)?.toDouble(),
      quantity: json['quantity'],
      selected: json['selected'],
      product: json['product'] != null ? ProductModel.fromJson(json['product']) : null,
      cart: json['cart'] != null ? CartModel.fromJson(json['cart']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'cartItemId': cartItemId,
      'price': price,
      'quantity': quantity,
      'selected': selected,
      'product': product?.toJson(),
      'cart': cart?.toJson(),
    };
  }
}
