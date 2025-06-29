import 'package:msa/feature/domain/entities/user_model.dart';

class UserAddressModel {
  final int userAddressId;
  final String city;
  final String district;
  final String street;
  final String ward;
  final String cityCode;
  final String districtCode;
  final String wardCode;
  final String createdAt;
  final bool primary;
  final UserModel user;

  UserAddressModel({
    required this.userAddressId,
    required this.city,
    required this.district,
    required this.street,
    required this.ward,
    required this.cityCode,
    required this.districtCode,
    required this.wardCode,
    required this.createdAt,
    required this.primary,
    required this.user,
  });

  factory UserAddressModel.fromJson(Map<String, dynamic> json) {
    return UserAddressModel(
      userAddressId: json['userAddressId'],
      city: json['city'],
      district: json['district'],
      street: json['street'],
      ward: json['ward'],
      cityCode: json['cityCode'],
      districtCode: json['districtCode'],
      wardCode: json['wardCode'],
      createdAt: json['createdAt'],
      primary: json['primary'] ?? false,
      user: UserModel.fromJson(json['user']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userAddressId': userAddressId,
      'city': city,
      'district': district,
      'street': street,
      'ward': ward,
      'cityCode': cityCode,
      'districtCode': districtCode,
      'wardCode': wardCode,
      'createdAt': createdAt,
      'primary': primary,
      'user': user.toJson(),
    };
  }
}