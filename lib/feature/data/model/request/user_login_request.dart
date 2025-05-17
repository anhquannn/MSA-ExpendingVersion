/// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation
library;

import 'dart:convert';

UserLoginRequest userLoginRequestFromJson(String str) => UserLoginRequest.fromJson(json.decode(str));

String userLoginRequestToJson(UserLoginRequest data) => json.encode(data.toJson());

class UserLoginRequest {
    UserLoginRequest({
        required this.password,
        required this.email,
    });

    String password;
    String email;

    factory UserLoginRequest.fromJson(Map<dynamic, dynamic> json) => UserLoginRequest(
        password: json["password"],
        email: json["email"],
    );

    Map<String, dynamic>? toJson() => {
        "password": password,
        "email": email,
    };
}
