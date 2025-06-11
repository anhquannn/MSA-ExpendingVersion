class AddToCartRequest {
  final int userId;
  final int productId;
  final int branchId;
  final int quantity;

  AddToCartRequest({
    required this.userId,
    required this.productId,
    required this.branchId,
    required this.quantity,
  });

  factory AddToCartRequest.fromJson(Map<String, dynamic> json) {
    return AddToCartRequest(
      userId: json['userId'],
      productId: json['productId'],
      branchId: json['branchId'],
      quantity: json['quantity'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'productId': productId,
      'branchId': branchId,
      'quantity': quantity,
    };
  }
}
