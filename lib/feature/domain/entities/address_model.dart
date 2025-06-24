class UserAddressRequest {
  final String city;
  final String district;
  final String street;
  final String ward;
  final String cityCode;
  final String districtCode;
  final String wardCode;
  final bool isPrimary;
  final String? createdAt;
  final int userId;

  UserAddressRequest({
    required this.city,
    required this.district,
    required this.street,
    required this.ward,
    required this.cityCode,
    required this.districtCode,
    required this.wardCode,
    required this.isPrimary,
    this.createdAt,
    required this.userId,
  });

  factory UserAddressRequest.fromJson(Map<String, dynamic> json) {
    return UserAddressRequest(
      city: json['city'] ?? '',
      district: json['district'] ?? '',
      street: json['street'] ?? '',
      ward: json['ward'] ?? '',
      cityCode: json['cityCode'] ?? '',
      districtCode: json['districtCode'] ?? '',
      wardCode: json['wardCode'] ?? '',
      isPrimary: json['isPrimary'] ?? false, // đúng key JSON
      createdAt: json['createdAt'] ?? '',
      userId: json['userId'] ?? 0,
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
      'primary': isPrimary,
      'createdAt': createdAt,
      'userId': userId,
    };
  }
}

class RoleModel {
  final int roleId;
  final String name;
  final String description;
  final List<PermissionModel> permissions;

  RoleModel({
    required this.roleId,
    required this.name,
    required this.description,
    required this.permissions,
  });

  factory RoleModel.fromJson(Map<String, dynamic> json) {
    return RoleModel(
      roleId: json['roleId'] ?? 0, // đảm bảo không null
      name: json['name'] ?? '',
      description: json['description'] ?? '',
      permissions:
          (json['permissions'] as List<dynamic>?)
              ?.map((e) => PermissionModel.fromJson(e))
              .toList() ??
          [], // nếu null thì trả về list rỗng
    );
  }
}

class PermissionModel {
  final int permissionId;
  final String name;
  final String description;

  PermissionModel({
    required this.permissionId,
    required this.name,
    required this.description,
  });

  factory PermissionModel.fromJson(Map<String, dynamic> json) {
    return PermissionModel(
      permissionId: json['permissionId'] ?? '',
      name: json['name'] ?? '',
      description: json['description'] ?? '',
    );
  }
}

class UserModelResponseAddress {
  final int userId;
  final String fullName;
  final String email;
  final String phoneNumber;
  final String birthday;
  final String password;
  final String? image;
  final String? deviceId;
  final String? googleId;
  final List<RoleModel> roles;
  final List<dynamic> branches; // nếu có model branch, có thể thay đổi
  final dynamic userAddresses;

  UserModelResponseAddress({
    required this.userId,
    required this.fullName,
    required this.email,
    required this.phoneNumber,
    required this.birthday,
    required this.password,
    this.image,
    this.deviceId,
    this.googleId,
    required this.roles,
    required this.branches,
    this.userAddresses,
  });

  factory UserModelResponseAddress.fromJson(Map<String, dynamic> json) {
    return UserModelResponseAddress(
      userId: json['userId'] ?? 0, // sửa ở đây
      fullName: json['fullName'] ?? '',
      email: json['email'] ?? '',
      phoneNumber: json['phoneNumber'] ?? '',
      birthday: json['birthday'] ?? '',
      password: json['password'] ?? '',
      image: json['image'],
      deviceId: json['deviceId'],
      googleId: json['googleId'],
      roles:
          json['roles'] != null
              ? (json['roles'] as List<dynamic>)
                  .map((e) => RoleModel.fromJson(e))
                  .toList()
              : [],
      branches: json['branches'] ?? [],
      userAddresses: json['userAddresses'],
    );
  }
}
