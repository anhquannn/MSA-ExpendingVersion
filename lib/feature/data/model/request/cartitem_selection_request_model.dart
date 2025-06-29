class CartItemSelectionRequest {
  final int? cartId;
  final List<int>? cartItemIds;

  CartItemSelectionRequest({
     this.cartId,
     this.cartItemIds,
  });

  Map<String, dynamic> toJson() {
    return {
      'cartId': cartId,
      'cartItemIds': cartItemIds,
    };
  }

  factory CartItemSelectionRequest.fromJson(Map<String, dynamic> json) {
    return CartItemSelectionRequest(
      cartId: json['cartId'],
      cartItemIds: List<int>.from(json['cartItemIds'] ?? []),
    );
  }
}
