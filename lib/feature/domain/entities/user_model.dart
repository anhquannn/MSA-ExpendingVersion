/// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation
library;

import 'dart:convert';

UserModel userResponseModelFromJson(String str) => UserModel.fromJson(json.decode(str));

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
        birthday: json["birthday"]??'',
        password: json["password"]??'',
        phoneNumber: json["phoneNumber"]??'',
        address: json["address"]??'',
        roles: json["roles"] != null
            ? List<Role>.from(json["roles"].map((x) => Role.fromJson(x)))
            : null,
        fullName: json["fullName"]??'',
        userId: json["userId"]??'',
        email: json["email"]??'',
        image: json["image"]??'',
        deviceId: json["deviceId"]??'',
        googleId: json["googleId"]??'',
        
    );

    Map<dynamic, dynamic> toJson() => {
        "birthday":birthday,
        "password": password,
        "phoneNumber": phoneNumber,
        "address": address,
        "roles":(roles!=null&&roles!.isNotEmpty)? (List<dynamic>.from(roles!.map((x) => x.toJson()))):null,
        "fullName": fullName,
        "userId": userId,
        "email": email,
        "image": image,
        "deviceId": deviceId,
        "googleId": googleId,
    };
}

class Role {
    Role({
        this.roleId,
        this.permissions,
        this.name,
    });

    int? roleId;
    List<dynamic>? permissions;
    String? name;

    factory Role.fromJson(Map<dynamic, dynamic> json) => Role(
        roleId: json["roleId"],
        permissions: List<dynamic>.from(json["permissions"].map((x) => x)),
        name: json["name"],
    );

    Map<dynamic, dynamic> toJson() => {
        "roleId": roleId,
        "permissions": permissions!=null?List<dynamic>.from(permissions!.map((x) => x)):null,
        "name": name,
    };
}
