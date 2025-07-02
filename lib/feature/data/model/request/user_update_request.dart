/// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation
library;

import 'dart:convert';

UserUpdateRequest userUpdateRequestFromJson(String str) => UserUpdateRequest.fromJson(json.decode(str));

String userUpdateRequestToJson(UserUpdateRequest data) => json.encode(data.toJson());

class UserUpdateRequest {
    UserUpdateRequest({
         this.birthday,
         this.password,
         this.phoneNumber,
         this.roles,
         this.fullName,
         this.email,
       this.userId
    });

    int? userId;
    String? birthday;
    String? password;
    String? phoneNumber;
    String? address;
    List<dynamic>? roles;
    String? fullName;
    String? email;

    factory UserUpdateRequest.fromJson(Map<dynamic, dynamic> json) => UserUpdateRequest(
        birthday:json["birthday"],
        password: json["password"],
        phoneNumber: json["phoneNumber"],
        roles: List<dynamic>.from(json["roles"].map((x) => x)),
        fullName: json["fullName"],
        email: json["email"],
    );

    Map<String, dynamic> toJson() => {
        "birthday": birthday,
        "password": password,
        "phoneNumber": phoneNumber,
        "address": address,
        "fullName": fullName,
        "email": email,
    };
}
