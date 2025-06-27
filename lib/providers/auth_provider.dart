import 'dart:convert';

import 'package:flutter/foundation.dart';

import '../services/api_service.dart';

class AuthProvider extends ChangeNotifier {
  AuthProvider({required ApiService api}) : _api = api;

  final ApiService _api;

  String? _accessToken;
  int? _userId;
  bool get authenticated => _accessToken != null;
  String? get token => _accessToken;
  int? get userId => _userId;

 Future<bool> login({required String email, required String password}) async {
    try {
      final res = await _api.post('/user/admin/login', body: {
        'email': email,
        'password': password,
      });

      if (res.statusCode == 200) {
        final data = jsonDecode(res.body) as Map<String, dynamic>;
        final token = data['result']['access_token'] as String?;

        if (token == null) {
          debugPrint('[AuthProvider] Login failed: Token null');
          return false;
        }

        if (!_hasSurveyorRole(token)) {
          debugPrint('[AuthProvider] Login failed: User has no ROLE_SURVEYOR');
          return false;
        }

        _accessToken = token;
        await _loadUserInfo();
        notifyListeners();
        debugPrint('[AuthProvider] Login successful');
        return true;
      } else {
        debugPrint(
          '[AuthProvider] Login failed: status ${res.statusCode}, body: ${res.body}',
        );
        return false;
      }
    } catch (e, stack) {
      debugPrint('[AuthProvider] Login exception: $e');
      debugPrint(stack.toString());
      return false;
    }
  }
  
  bool _hasSurveyorRole(String token) {
    try {
      final parts = token.split('.');
      if (parts.length != 3) return false;

      final payload =
          utf8.decode(base64Url.decode(base64Url.normalize(parts[1])));
      final json = jsonDecode(payload) as Map<String, dynamic>;
      final scope = json['scope'] as String? ?? '';

      // Chỉ cần có ROLE_SURVEYOR là pass
      return scope.contains('ROLE_SURVEYOR');
    } catch (_) {
      return false;
    }
  }

  Future<void> _loadUserInfo() async {
    if (_accessToken == null) return;
    final res = await _api.get('/user/myinfo', token: _accessToken);
    if (res.statusCode == 200) {
      final data = jsonDecode(res.body) as Map<String, dynamic>;
      _userId = data['result']['id'] as int? ?? data['result']['userId'] as int?;
    }
  }

  Future<void> logout() async {
    if (_accessToken == null) return;
    try {
      await _api.post(
        '/user/logout',
        body: {'token': _accessToken},
        token: _accessToken,
      );
    } catch (_) {
      // ignore errors, proceed to clear cred locally
    }
    _accessToken = null;
    _userId = null;
    notifyListeners();
  }
}
