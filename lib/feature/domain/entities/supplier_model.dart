class SupplierModel {
  final int supplierId;
  final String name;
  final String address;
  final String contact;
  final String image;

  SupplierModel({
    required this.supplierId,
    required this.name,
    required this.address,
    required this.contact,
    required this.image,
  });

  factory SupplierModel.fromJson(Map<String, dynamic> json) {
    return SupplierModel(
      supplierId: json['supplierId'] ?? 0,
      name: json['name'] ?? '',
      address: json['address'] ?? '',
      contact: json['contact'] ?? '',
      image: json['image'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'supplierId': supplierId,
      'name': name,
      'address': address,
      'contact': contact,
      'image': image,
    };
  }
}
