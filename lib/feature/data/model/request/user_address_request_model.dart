class UserAddressRequest {
  final String? city;
  final String? district;
  final String? street;
  final String? ward;
  final String? cityCode;
  final String? districtCode;
  final String? wardCode;
  final bool? isPrimary;
  final int? userId;

  UserAddressRequest({
    this.city,
    this.district,
    this.street,
    this.ward,
    this.cityCode,
    this.districtCode,
    this.wardCode,
    this.isPrimary,
    this.userId,
  });

  factory UserAddressRequest.fromJson(Map<String, dynamic> json) {
    return UserAddressRequest(
      city: json['city'],
      district: json['district'],
      street: json['street'],
      ward: json['ward'],
      cityCode: json['cityCode'],
      districtCode: json['districtCode'],
      wardCode: json['wardCode'],
      isPrimary: json['isPrimary'],
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
      'isPrimary': isPrimary,
      'userId': userId,
    };
  }
}
