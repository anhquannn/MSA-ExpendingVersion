import 'dart:convert';
import 'dart:io';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:mime/mime.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:path/path.dart' as path;

import '../../../../core/config/constant.dart';

class HttpConnection {
  static final String _urlConnection = urlConnection;
  final String baseUrlSupabase = 'https://lmtqwglnnbgsrxhelpxz.supabase.co';
  final String tokenSupabase =
      'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxtdHF3Z2xubmJnc3J4aGVscHh6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU3MTI1NzIsImV4cCI6MjA2MTI4ODU3Mn0.5D6-g10oFKgB5eJw7jbJPGtOsr2BmrYnm5pTpfjA_J0';
  static BuildContext? context;


  static List<City> cityGlobal=[];
  static List<Ward> wardGlobal=[];
  static List<District> districtGlobal=[];


  static String buildUrlWithQueryParams(
    String baseUrl,
    Map<String, dynamic> queryParams,
  ) {
    if (queryParams.isEmpty) return baseUrl;

    final queryString = queryParams.entries
        .where((e) => e.value != null)
        .map(
          (e) =>
              '${Uri.encodeQueryComponent(e.key)}=${Uri.encodeQueryComponent(e.value.toString())}',
        )
        .join('&');

    return '$baseUrl?$queryString';
  }

  static Map<String, String> _configHeader({
    Map<String, String>? extraHeaders,
    bool isToken = false,
  }) {
    final headers = <String, String>{
      'Content-Type': 'application/json; charset=UTF-8',
    };
    if (isToken) headers['Authorization'] = 'Bearer ${Storage.token}';
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
              'Authorization': 'Bearer ${Storage.token}',
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
        statusCode: response.statusCode,
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

  static Future<ResponseData> post(
    String path, {
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    bool isToken = false,
  }) => sendRequest(
    method: 'POST',
    path: path,
    body: body,
    headers: headers,
    isToken: isToken,
  );

  static Future<ResponseData> get(
    String path, {
    Map<String, String>? headers,
    bool isToken = false,
  }) => sendRequest(
    method: 'GET',
    path: path,
    headers: headers,
    isToken: isToken,
  );

  static Future<ResponseData> put(
    String path, {
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    bool isToken = false,
  }) => sendRequest(
    method: 'PUT',
    path: path,
    body: body,
    headers: headers,
    isToken: isToken,
  );

  static Future<ResponseData> delete(
    String path, {
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    bool isToken = false,
  }) => sendRequest(
    method: 'DELETE',
    path: path,
    body: body,
    headers: headers,
    isToken: isToken,
  );

  static Future<ResponseData> sendRequest({
    required String method,
    required String path,
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    bool isToken = false,
  }) async {
    final url = Uri.parse('$_urlConnection$path');
    final requestHeaders = _configHeader(
      extraHeaders: headers,
      isToken: isToken,
    );
    final responseData = ResponseData();

    try {
      http.Response response;

      switch (method.toUpperCase()) {
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
        statusCode: response.statusCode,
      );

      // Decode response body bytes tùy theo header Content-Type charset
      String responseBodyString;

      final contentType = response.headers['content-type'] ?? '';

      if (contentType.toLowerCase().contains('charset=latin1') ||
          contentType.toLowerCase().contains('charset=iso-8859-1')) {
        // decode latin1
        responseBodyString = latin1.decode(response.bodyBytes);
      } else {
        // mặc định utf8
        responseBodyString = utf8.decode(response.bodyBytes);
      }

      final responseBody = jsonDecode(responseBodyString);

      if (response.statusCode >= 200 && response.statusCode < 300) {
        responseData
          ..isSuccess = responseBody['code'] == 200
          ..data = responseBody['result']
          ..message = responseBody['message'];
      } else {
       responseData
          ..isSuccess = false
          ..data = ''
          ..message = {};
      }

      return responseData;
    } catch (e) {
      throw Exception('Lỗi $method: $e');
    }
  }

  static void _logRequest({
    required String method,
    required Uri url,
    required Map<String, String> headers,
    Map<String, dynamic>? body,
    required http.Response response,
    required int statusCode,
  }) {
    if (kDebugMode) {
      print('''\n
############################## [$method REQUEST] ##############################
\t URL: $url
\t Headers: $headers
\t Body: $body
\t Response: ${response.body}
########################### [STATUS CODE $statusCode] ##########################\n
''');
    }
  }
}

class ResponseData {
  bool isSuccess = false;
  dynamic data;
  dynamic message;
}
