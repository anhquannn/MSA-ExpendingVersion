class AuthenticationResponse {
  final String accessToken;
  final String refreshToken;
  final String tokenType;
  final int? expiresIn;
  final bool authenticated;

  AuthenticationResponse({
    required this.accessToken,
    required this.refreshToken,
    required this.tokenType,
    required this.expiresIn,
    required this.authenticated,
  });

  factory AuthenticationResponse.fromJson(Map<String, dynamic> json) {
    return AuthenticationResponse(
      accessToken: json['access_token'] ?? '',
      refreshToken: json['refresh_token'] ?? '',
      tokenType: json['token_type'] ?? 'Bearer',
      expiresIn: json['expires_in'],
      authenticated: json['authenticated'] ?? false,
    );
  }
}
