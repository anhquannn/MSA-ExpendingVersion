import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:mime/mime.dart';
import 'package:path/path.dart' as path;

class HttpConnection {
  String urlConnection = '';
  String token = '';
  final String baseUrlSupabase = 'https://lmtqwglnnbgsrxhelpxz.supabase.co';
  final String tokenSupabase =
      'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxtdHF3Z2xubmJnc3J4aGVscHh6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU3MTI1NzIsImV4cCI6MjA2MTI4ODU3Mn0.5D6-g10oFKgB5eJw7jbJPGtOsr2BmrYnm5pTpfjA_J0';

  Map<String, String> _configHeader({
    Map<String, String>? extraHeaders,
    bool isToken = false,
  }) {
    final headers = <String, String>{'Content-Type': 'application/json'};
    if (isToken) headers['Authorization'] = token;
    if (extraHeaders != null) headers.addAll(extraHeaders);
    return headers;
  }

  Future<ResponseData> uploadFile(File file) async {
    final responseData = ResponseData();
    final bucketName = 'msa';
    final fileName = path.basename(file.path);
    final url = Uri.parse(
      '$baseUrlSupabase/storage/v1/object/$bucketName/$fileName',
    );

    try {
      final mimeType = lookupMimeType(file.path) ?? 'application/octet-stream';
      final request =
          http.MultipartRequest('POST', url)
            ..headers.addAll({
              'Authorization': 'Bearer $token',
              'x-upsert': 'false',
            })
            ..files.add(
              await http.MultipartFile.fromPath(
                'file',
                file.path,
                contentType: MediaType.parse(mimeType),
              ),
            );

      final streamedResponse = await request.send();
      final response = await http.Response.fromStream(streamedResponse);

      // In debug log nếu cần
      _logRequest(
        method: 'UPLOAD',
        url: url,
        headers: request.headers,
        body: {'file': file.path},
        response: response,
      );

      final body = jsonDecode(response.body);

      responseData
        ..isSuccess = response.statusCode >= 200 && response.statusCode < 300
        ..data = responseData.isSuccess ? body : null;

      return responseData;
    } catch (e) {
      throw Exception('Lỗi upload file: $e');
    }
  }

  Future<ResponseData> post(
    String path, {
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    bool isToken = false,
  }) => _sendRequest(
    method: 'POST',
    path: path,
    body: body,
    headers: headers,
    isToken: isToken,
  );

  Future<ResponseData> get(
    String path, {
    Map<String, String>? headers,
    bool isToken = false,
  }) => _sendRequest(
    method: 'GET',
    path: path,
    headers: headers,
    isToken: isToken,
  );

  Future<ResponseData> put(
    String path, {
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    bool isToken = false,
  }) => _sendRequest(
    method: 'PUT',
    path: path,
    body: body,
    headers: headers,
    isToken: isToken,
  );

  Future<ResponseData> delete(
    String path, {
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    bool isToken = false,
  }) => _sendRequest(
    method: 'DELETE',
    path: path,
    body: body,
    headers: headers,
    isToken: isToken,
  );

  Future<ResponseData> _sendRequest({
    required String method,
    required String path,
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    bool isToken = false,
  }) async {
    final url = Uri.parse('$urlConnection$path');
    final requestHeaders = _configHeader(
      extraHeaders: headers,
      isToken: isToken,
    );
    final responseData = ResponseData();

    try {
      http.Response response;

      switch (method) {
        case 'POST':
          response = await http.post(
            url,
            headers: requestHeaders,
            body: jsonEncode(body ?? {}),
          );
          break;
        case 'GET':
          response = await http.get(url, headers: requestHeaders);
          break;
        case 'PUT':
          response = await http.put(
            url,
            headers: requestHeaders,
            body: jsonEncode(body ?? {}),
          );
          break;
        case 'DELETE':
          response = await http.delete(
            url,
            headers: requestHeaders,
            body: jsonEncode(body ?? {}),
          );
          break;
        default:
          throw Exception('Unsupported HTTP method: $method');
      }

      _logRequest(
        method: method,
        url: url,
        headers: requestHeaders,
        body: body,
        response: response,
      );

      final responseBody = jsonDecode(response.body);

      if (response.statusCode >= 200 && response.statusCode < 300) {
        responseData
          ..isSuccess = responseBody['error'] == 0
          ..data = responseBody['data'];
      } else {
        responseData.isSuccess = false;
      }

      return responseData;
    } catch (e) {
      throw Exception('Lỗi $method: $e');
    }
  }

  void _logRequest({
    required String method,
    required Uri url,
    required Map<String, String> headers,
    Map<String, dynamic>? body,
    required http.Response response,
  }) {
    if (kDebugMode) {
      print('''
###################### [$method REQUEST] ######################
URL: $url
Headers: $headers
Body: $body
Response: ${response.body}
#################################################################
''');
    }
  }
}

class ResponseData {
  bool isSuccess = false;
  dynamic data;
}
