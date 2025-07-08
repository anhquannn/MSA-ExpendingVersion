import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:flutter/foundation.dart';

/// A very small wrapper around the http package so we only touch
/// low-level networking code in one place. This keeps the rest of the
/// codebase free from repeated boilerplate and makes it easier to mock
/// in unit tests. Feel free to extend this class as your project grows.
class ApiService {
  ApiService({required this.baseUrl});

  final String baseUrl;

  Future<http.Response> post(
    String path, {
    required Map<String, dynamic> body,
    String? token,
  }) async {
    final uri = Uri.parse('$baseUrl$path');
    final headers = <String, String>{
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Bearer $token',
    };
    return http.post(uri, body: jsonEncode(body), headers: headers);
  }

  Future<http.Response> put(
    String path, {
    required Map<String, dynamic> body,
    String? token,
  }) async {
    final uri = Uri.parse('$baseUrl$path');
    final headers = <String, String>{
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Bearer $token',
    };
    return http.put(uri, body: jsonEncode(body), headers: headers);
  }

  Future<http.Response> get(String path, {String? token}) async {
    final uri = Uri.parse('$baseUrl$path');
    final headers = <String, String>{
      if (token != null) 'Authorization': 'Bearer $token',
    };
    return http.get(uri, headers: headers);
  }

  Future<http.Response> delete(String path, {String? token}) async {
    final uri = Uri.parse('$baseUrl$path');
    final headers = <String, String>{
      if (token != null) 'Authorization': 'Bearer $token',
    };
    return http.delete(uri, headers: headers);
  }

  Future<int?> fetchFirstManagerId({required int inventoryId, required String token}) async {
    final path = '/user/inventory/$inventoryId/managers/all';
    if (kDebugMode) {
      print('[API] GET $path');
    }
    final res = await get(path, token: token);
    if (kDebugMode) {
      print('[API] resp status=${res.statusCode}, body=${res.body}');
    }
    if (res.statusCode == 200) {
      try {
        final data = jsonDecode(res.body) as Map<String, dynamic>;
        dynamic content = data['result'];
        if (content is Map<String, dynamic> && content.containsKey('content')) {
          content = content['content'];
        }
        if (content is List && content.isNotEmpty) {
          final first = content.first as Map<String, dynamic>;
          return first['userId'] as int? ?? first['id'] as int?;
        }
      } catch (_) {}
    }
    return null;
  }

  Future<http.Response> createInventoryCheckRequest({
    required int inventoryId,
    required int surveyorId,
    required int userId,
    required String note,
    required DateTime requestedDate,
    required String token,
  }) async {
    if (kDebugMode) {
      print('[API] POST /inventory-check-requests');
    }
    return post(
      '/inventory-check-requests',
      body: {
        'inventoryId': inventoryId,
        'surveyorId': surveyorId,
        'userId': userId,
        'requestedDate': '${requestedDate.year.toString().padLeft(4,'0')}-${requestedDate.month.toString().padLeft(2,'0')}-${requestedDate.day.toString().padLeft(2,'0')} ${requestedDate.hour.toString().padLeft(2,'0')}:${requestedDate.minute.toString().padLeft(2,'0')}:${requestedDate.second.toString().padLeft(2,'0')}',
        'note': note,
      },
      token: token,
    );
  }

  Future<http.Response> createCheckedHistory({
    required int inventoryId,
    required int userId,
    required String note,
    required String token,
  }) async {
    if (kDebugMode) {
      print('[API] POST /checked-history');
    }
    final res = await post(
      '/checked-history',
      body: {
        'inventoryId': inventoryId,
        'userId': userId,
        'note': note,
      },
      token: token,
    );
    return res;
  }
}
