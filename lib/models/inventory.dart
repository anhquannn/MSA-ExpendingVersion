class Inventory {
  Inventory({
    required this.id,
    required this.name,
    required this.address,
    required this.totalRevenue,
    required this.managerId,
  });

  factory Inventory.fromJson(Map<String, dynamic> json) => Inventory(
        id: json['inventoryId'] as int,
        name: json['name'] as String,
        address: json['address'] as String,
        totalRevenue: (json['totalRevenue'] as num?)?.toDouble() ?? 0,
        managerId: json['userId'] as int? ?? json['managerId'] as int? ?? 0,
      );

  final int id;
  final String name;
  final String address;
  final double totalRevenue;
  final int managerId;
}
