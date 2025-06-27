class UpdateUserRequest {
  String fullName;
  String email;
  String phoneNumber;
  String birthday; // hoặc DateTime nếu bạn muốn dùng kiểu DateTime
  String password;
  String address;
  String googleId;
  List<int> roles;

  UpdateUserRequest({
    required this.fullName,
    required this.email,
    required this.phoneNumber,
    required this.birthday,
    required this.password,
    required this.address,
    required this.googleId,
    required this.roles,
  });

  factory UpdateUserRequest.fromJson(Map<String, dynamic> json) {
    return UpdateUserRequest(
      fullName: json['fullName'] ?? '',
      email: json['email'] ?? '',
      phoneNumber: json['phoneNumber'] ?? '',
      birthday: json['birthday'] ?? '',
      password: json['password'] ?? '',
      address: json['address'] ?? '',
      googleId: json['googleId'] ?? '',
      roles: List<int>.from(json['roles'] ?? []),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'fullName': fullName,
      'email': email,
      'phoneNumber': phoneNumber,
      'birthday': birthday,
      'password': password,
      'address': address,
      'googleId': googleId,
      'roles': roles,
    };
  }
}
