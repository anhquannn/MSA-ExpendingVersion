class BranchModel {
  int? branchId;
  String? name;
  String? phone;
  String? street;
  String? ward;
  String? district;
  String? city;
  String? cityCode;
  String? districtCode;
  String? wardCode;
  InventoryModel? inventory;
  bool? isSelect;

  BranchModel({
    this.branchId,
    this.name,
    this.phone,
    this.street,
    this.ward,
    this.district,
    this.city,
    this.cityCode,
    this.districtCode,
    this.wardCode,
    this.inventory,
    this.isSelect,
  });

  factory BranchModel.fromJson(Map<String, dynamic> json) {
    return BranchModel(
      branchId: json['branchId'] as int?,
      name: json['name'] as String?,
      phone: json['phone'] as String?,
      street: json['street'] as String?,
      ward: json['ward'] as String?,
      district: json['district'] as String?,
      city: json['city'] as String?,
      cityCode: json['cityCode'] as String?,
      districtCode: json['districtCode'] as String?,
      wardCode: json['wardCode'] as String?,
      inventory: json['inventory'] != null
          ? InventoryModel.fromJson(json['inventory'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'branchId': branchId,
      'name': name,
      'phone': phone,
      'street': street,
      'ward': ward,
      'district': district,
      'city': city,
      'cityCode': cityCode,
      'districtCode': districtCode,
      'wardCode': wardCode,
      'inventory': inventory?.toJson(),
    };
  }
   @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is BranchModel && runtimeType == other.runtimeType && branchId == other.branchId;

  @override
  int get hashCode => branchId.hashCode;
}


class InventoryModel {
  String? name;
  String? address;
  String? contact;
  double? totalRevenue;
  int? branchId;

  InventoryModel({
    this.name,
    this.address,
    this.contact,
    this.totalRevenue,
    this.branchId,
  });

  factory InventoryModel.fromJson(Map<String, dynamic> json) {
    return InventoryModel(
      name: json['name'] as String?,
      address: json['address'] as String?,
      contact: json['contact'] as String?,
      totalRevenue: (json['totalRevenue'] as num?)?.toDouble(),
      branchId: json['branchId'] as int?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'address': address,
      'contact': contact,
      'totalRevenue': totalRevenue,
      'branchId': branchId,
    };
  }
}
