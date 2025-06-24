/// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation
library;

import 'dart:convert';

UserRegisterRequest userRegisterRequestFromJson(String str) =>
    UserRegisterRequest.fromJson(json.decode(str));

String userRegisterRequestToJson(UserRegisterRequest data) =>
    json.encode(data.toJson());

class UserRegisterRequest {
  UserRegisterRequest({
    this.image,
    this.deviceId,
    this.googleId,
    required this.birthday,
    required this.password,
    required this.phoneNumber,
    // required this.address,
    required this.fullName,
    required this.email,
  });

  String birthday;
  String password;
  String phoneNumber;
  // String address;
  String fullName;
  String email;
  String? image;
  String? deviceId;
  String? googleId;

  factory UserRegisterRequest.fromJson(Map<dynamic, dynamic> json) =>
      UserRegisterRequest(
        birthday: json["birthday"],
        password: json["password"],
        phoneNumber: json["phoneNumber"],
        // address: json["address"],
        fullName: json["fullName"],
        email: json["email"],
        deviceId: json['deviceId'],
        googleId: json['googleId'],
        image: json['image']
      );

  Map<String, dynamic>? toJson() => {
    "birthday": birthday,
    "password": password,
    "phoneNumber": phoneNumber,
    // "address": address,
    "fullName": fullName,
    "email": email,
    "deviceId":deviceId,
    "googleId":googleId,
    "image":image
  };
}
