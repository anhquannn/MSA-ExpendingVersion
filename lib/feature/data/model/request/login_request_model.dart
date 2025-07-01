enum PlatformType {
  ANDROID,
  IOS,
  WEB,
}
extension PlatformTypeExtension on PlatformType {
  String get name => toString().split('.').last.toUpperCase();

  static PlatformType? fromString(String status) {
    try {
      return PlatformType.values.firstWhere(
        (e) => e.name == status.toUpperCase(),
      );
    } catch (_) {
      return null;
    }
  }
}

class LoginRequest {
  final String email;
  final String password;
  final String fcmToken;
  final PlatformType platform;

  LoginRequest({
    required this.email,
    required this.password,
    required this.fcmToken,
    required this.platform,
  });

  Map<String, dynamic> toJson() => {
        'email': email,
        'password': password,
        'fcmToken': fcmToken,
        'platform': platform.name, // hoặc platform.toString().split('.').last nếu không dùng Dart >= 2.15
      };
}
