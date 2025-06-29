class UpdatePasswordRequest {
  final String oldPassword;
  final String newPassword;

  UpdatePasswordRequest({
    required this.oldPassword,
    required this.newPassword,
  });

  factory UpdatePasswordRequest.fromJson(Map<String, dynamic> json) {
    return UpdatePasswordRequest(
      oldPassword: json['oldPassword'] as String,
      newPassword: json['newPassword'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'oldPassword': oldPassword,
      'newPassword': newPassword,
    };
  }
}
