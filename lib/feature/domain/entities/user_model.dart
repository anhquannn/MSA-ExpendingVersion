library;

import 'dart:convert';

UserModel userResponseModelFromJson(String str) =>
    UserModel.fromJson(json.decode(str));

String userResponseModelToJson(UserModel data) => json.encode(data.toJson());

class UserModel {
  UserModel({
    this.birthday,
    this.password,
    this.phoneNumber,
    this.address,
    this.roles,
    this.fullName,
    this.userId,
    this.email,
    this.image = "",
    this.deviceId = "",
    this.googleId = "",
  });

  String image;
  String deviceId;
  String googleId;
  String? birthday;
  String? password;
  String? phoneNumber;
  String? address;
  List<Role>? roles;
  String? fullName;
  int? userId;
  String? email;

  factory UserModel.fromJson(Map<dynamic, dynamic> json) => UserModel(
    birthday: json["birthday"] ?? '',
    password: json["password"] ?? '',
    phoneNumber: json["phoneNumber"] ?? '',
    address: json["address"] ?? '',
    roles:
        (json["roles"] is List)
            ? List<Role>.from(json["roles"].map((x) => Role.fromJson(x)))
            : [],

    fullName: json["fullName"] ?? '',
    userId: json["userId"] ?? '',
    email: json["email"] ?? '',
    image: json["image"] ?? '',
    deviceId: json["deviceId"] ?? '',
    googleId: json["googleId"] ?? '',
  );

  Map<dynamic, dynamic> toJson() => {
    "birthday": birthday,
    "password": password,
    "phoneNumber": phoneNumber,
    "address": address,
    "roles":
        (roles != null && roles!.isNotEmpty)
            ? (List<dynamic>.from(roles!.map((x) => x.toJson())))
            : null,
    "fullName": fullName,
    "userId": userId,
    "email": email,
    "image": image,
    "deviceId": deviceId,
    "googleId": googleId,
  };
}

class Role {
  Role({this.roleId, this.permissions, this.name});

  int? roleId;
  List<Permission>? permissions;
  String? name;

  factory Role.fromJson(Map<String, dynamic> json) => Role(
    roleId: json["roleId"],
    name: json["name"],
    permissions:
        json["permissions"] != null
            ? List<Permission>.from(
              json["permissions"].map((x) => Permission.fromJson(x)),
            )
            : [],
  );

  Map<String, dynamic> toJson() => {
    "roleId": roleId,
    "name": name,
    "permissions":
        permissions != null
            ? List<dynamic>.from(permissions!.map((x) => x.toJson()))
            : [],
  };
}
class Permission {
  final int? permissionId;
  final String? name;
  final String? description;

  Permission({this.permissionId, this.name, this.description});

  factory Permission.fromJson(Map<String, dynamic> json) => Permission(
        permissionId: json["permissionId"],
        name: json["name"],
        description: json["description"],
      );

  Map<String, dynamic> toJson() => {
        "permissionId": permissionId,
        "name": name,
        "description": description,
      };
}
