class Product {
  Product({
    required this.id,
    required this.code,
    required this.currentPrice,
    required this.stockNumber,
    required this.inventoryId,
    required this.productId,
    this.stockNumberChecked,
  });

  factory Product.fromJson(Map<String, dynamic> json) => Product(
        id: json['inventoryProductId'] as int,
        code: (json['product']?['name'] as String?) ?? '',
        currentPrice: (json['currentPrice'] as num?)?.toDouble() ?? 0,
        stockNumber: json['stockNumber'] as int? ?? 0,
        stockNumberChecked: json['stockNumberChecked'] as int?,
        inventoryId: (json['inventoryId'] as int?) ?? (json['inventory']?['inventoryId'] as int?) ?? 0,
        productId: (json['productId'] as int?) ?? (json['product']?['productId'] as int?) ?? 0,
      );

  final int id;
  final String code;
  final double currentPrice;
  final int stockNumber;
  int? stockNumberChecked;
  final int inventoryId;
  final int productId;
}
