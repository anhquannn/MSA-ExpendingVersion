class UserAddressFilterRequest {
  final int userId;
  final String sortBy;
  final String sortDirection;
  final int page;
  final int pageSize;

  UserAddressFilterRequest({
    required this.userId,
    this.sortBy = "createdAt",
    this.sortDirection = "DESC",
    this.page = 1,
    this.pageSize = 10,
  });

  factory UserAddressFilterRequest.fromJson(Map<String, dynamic> json) {
    return UserAddressFilterRequest(
      userId: json['userId'] ?? 0,
      sortBy: json['sortBy'] ?? 'createdAt',
      sortDirection: json['sortDirection'] ?? 'DESC',
      page: json['page'] ?? 1,
      pageSize: json['pageSize'] ?? 10,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
      'page': page,
      'pageSize': pageSize,
    };
  }
}

class UserAddressUpdateRequest {
  final String? city;
  final String? district;
  final String? street;
  final String? ward;
  final String? cityCode;
  final String? districtCode;
  final String? wardCode;
  bool? primary;
  final int? userId;

  UserAddressUpdateRequest({
    this.city,
    this.district,
    this.street,
    this.ward,
    this.cityCode,
    this.districtCode,
    this.wardCode,
    this.primary=true,
    this.userId,
  });

  factory UserAddressUpdateRequest.fromJson(Map<String, dynamic> json) {
    return UserAddressUpdateRequest(
      city: json['city'],
      district: json['district'],
      street: json['street'],
      ward: json['ward'],
      cityCode: json['cityCode'],
      districtCode: json['districtCode'],
      wardCode: json['wardCode'],
      primary: json['primary'] ?? false,
      userId: json['userId'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'city': city,
      'district': district,
      'street': street,
      'ward': ward,
      'cityCode': cityCode,
      'districtCode': districtCode,
      'wardCode': wardCode,
      'primary': primary,
      'userId': userId,
    };
  }
}
