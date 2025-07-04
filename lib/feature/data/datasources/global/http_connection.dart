import 'dart:convert';
import 'dart:io';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:mime/mime.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:path/path.dart' as path;

import '../../../../core/config/constant.dart';

class HttpConnection {
  //A0:86:1F:F4:87:FF:1F:3C:1D:28:B2:77:A6:7C:00:18:12:61:9B:AF
  static final String _urlConnection = urlConnection;
  final String baseUrlSupabase = 'https://lmtqwglnnbgsrxhelpxz.supabase.co';
  final String tokenSupabase =
      'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxtdHF3Z2xubmJnc3J4aGVscHh6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU3MTI1NzIsImV4cCI6MjA2MTI4ODU3Mn0.5D6-g10oFKgB5eJw7jbJPGtOsr2BmrYnm5pTpfjA_J0';
  static BuildContext? context;
  static String tk = Storage.token;
  // 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtd2FuZzM4MjAzQGdtYWlsLmNvbSIsInNjb3BlIjoiUk9MRV9DVVNUT01FUiIsImlzcyI6ImNvbS5tc2EiLCJleHAiOjE3NjQwNjYwODQsInRva2VuX3R5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3NTExMDYwODQsImp0aSI6IjY5YjQ1MGUyLTkwNDktNDdlMi04MWYwLTFiMDgwODljMDdlOSJ9.bH_NS5KyZWffuKRc1gpB7-WORDopL0BI709h7dHCp9-OPAvAIeF8IL5nVRZnLr_plhj4EMhmcMxkmViNrWSXSQ';
  static List<City> cityGlobal = [];
  static List<Ward> wardGlobal = [];
  static List<District> districtGlobal = [];

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
    bool isToken = true,
  }) {
    final headers = <String, String>{
      'Content-Type': 'application/json; charset=UTF-8',
    };
    // if (isToken) headers['Authorization'] = 'Bearer ${Storage.token}';
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

  static Future<ApiResponse<T>> post<T>(
    String path, {
    required T Function(dynamic json) fromJsonT, // Bắt buộc cung cấp hàm parse
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    bool isToken = true,
    BuildContext? context,
  }) => sendRequest<T>(
    method: 'POST',
    path: path,
    fromJsonT: fromJsonT,
    body: body,
    headers: headers,
    isToken: isToken,
    context: context,
  );

  static Future<ApiResponse<T>> get<T>(
    String path, {
    required T Function(dynamic json) fromJsonT,
    Map<String, String>? headers,
    bool isToken = true,
    BuildContext? context,
  }) => sendRequest<T>(
    method: 'GET',
    path: path,
    fromJsonT: fromJsonT,
    headers: headers,
    isToken: isToken,
    context: context,
  );

  static Future<ApiResponse<T>> put<T>(
    String path, {
    required T Function(dynamic json) fromJsonT,
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    BuildContext? context,
    bool isToken = true, // Sửa: PUT thường cần token
  }) => sendRequest<T>(
    method: 'PUT',
    path: path,
    fromJsonT: fromJsonT,
    body: body,
    headers: headers,
    isToken: isToken,
    context: context,
  );

  static Future<ApiResponse<T>> delete<T>(
    String path, {
    required T Function(dynamic json) fromJsonT,
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    BuildContext? context,
    bool isToken = true, // Sửa: DELETE thường cần token
  }) => sendRequest<T>(
    method: 'DELETE',
    path: path,
    fromJsonT: fromJsonT,
    body: body,
    headers: headers,
    isToken: isToken,
    context: context,
  );

  static Future<ApiResponse<T>> sendRequest<T>({
    required String method,
    required String path,
    required T Function(dynamic json) fromJsonT,
    Map<String, dynamic>? body,
    Map<String, String>? headers,
    bool isToken = true,
    BuildContext? context,
  }) async {
    Future<http.Response> doRequest(
      Uri url,
      Map<String, String> requestHeaders,
    ) async {
      switch (method.toUpperCase()) {
        case 'POST':
          return await http.post(
            url,
            headers: requestHeaders,
            body: jsonEncode(body ?? {}),
          );
        case 'GET':
          return await http.get(url, headers: requestHeaders);
        case 'PUT':
          return await http.put(
            url,
            headers: requestHeaders,
            body: jsonEncode(body ?? {}),
          );
        case 'DELETE':
          return await http.delete(
            url,
            headers: requestHeaders,
            body: jsonEncode(body ?? {}),
          );
        default:
          throw Exception('Unsupported HTTP method: $method');
      }
    }

    final url = Uri.parse('$_urlConnection$path');
    Map<String, String> requestHeaders = _configHeader(
      extraHeaders: headers,
      isToken: isToken,
    );

    try {
      http.Response response = await doRequest(url, requestHeaders);

      // Nếu token hết hạn
      if (response.statusCode == 401) {
        print('Token hết hạn, đang gọi refreshToken...');
        if (context != null && Storage.refreshToken == null) {
          showCustomDialog(
            context,
            100,
            100,
            'Thông báo',
            Text('Token đã hết hạn, vui lòng đăng nhập lại.'),
            true,
            true,
            null,
          );
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => LoginScreen()),
          );
        }
        final refreshed = await Repository.onRefresh(
          Storage.refreshToken ?? '',
          // 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtd2FuZzM4MjAzQGdtYWlsLmNvbSIsInNjb3BlIjoiUk9MRV9DVVNUT01FUiIsImlzcyI6ImNvbS5tc2EiLCJleHAiOjE4ODA3MDYwODQsInRva2VuX3R5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNzUxMTA2MDg0LCJqdGkiOiI1NTRjMzg1ZC1lYjgwLTRiNmYtODJmMC1mNDhhYTZkMWM0NTEifQ.r47-F54ytvnUjBXsoIMGPaOaGVNGo8q2-M25gf7MOWmuTUc7iXsVOwZB8mSQPbL-Fl_VLK11j8U02Sbr2w9Bbg',
        );

        if (refreshed) {
          requestHeaders = _configHeader(
            extraHeaders: headers,
            isToken: isToken,
          );
          response = await doRequest(url, requestHeaders); // Gọi lại request cũ
        } else {
          return ApiResponse<T>(
            code: 401,
            message: 'Token expired, refresh failed',
          );
        }
      }

      _logRequest(
        method: method,
        url: url,
        headers: requestHeaders,
        body: body,
        response: response,
        statusCode: response.statusCode,
      );

      final responseBodyString = utf8.decode(response.bodyBytes);
      final responseBody = jsonDecode(responseBodyString);

      return ApiResponse.fromJson(responseBody, fromJsonT);
    } catch (e) {
      print('Lỗi $method tại $path: $e');
      return ApiResponse<T>(code: 500, message: 'Lỗi client: $e');
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
      // customPrint(jsonDecode(response.body.toString()));
    }
  }
}

class ResponseData {
  bool isSuccess = false;
  dynamic data;
  dynamic message;
}
// file: api_response.dart

class ApiResponse<T> {
  final int code;
  final String message;
  final T? result; // Sử dụng generic type T cho trường result

  ApiResponse({required this.code, required this.message, this.result});

  bool get isSuccess => code >= 200 && code < 300;

  factory ApiResponse.fromJson(
    Map<String, dynamic> json,
    T Function(dynamic json) fromJsonT,
  ) {
    return ApiResponse<T>(
      code: json['code'] ?? 500,
      message: json['message'] ?? 'Unknown error',
      // Chỉ parse 'result' nếu nó không null và code thành công
      result:
          json['result'] != null && (json['code'] >= 200 && json['code'] < 300)
              ? fromJsonT(json['result'])
              : null,
    );
  }
}

// file: paginated_result.dart

class PaginatedResult<T> {
  final List<T> content;
  final int totalPages;
  final int totalElements;
  final bool last;
  final bool first;

  PaginatedResult({
    required this.content,
    required this.totalPages,
    required this.totalElements,
    required this.last,
    required this.first,
  });

  factory PaginatedResult.fromJson(
    Map<String, dynamic> json,
    T Function(dynamic itemJson) fromJsonT, // Hàm để parse từng item trong list
  ) {
    return PaginatedResult<T>(
      content:
          (json['content'] as List<dynamic>)
              .map((item) => fromJsonT(item))
              .toList(),
      totalPages: json['totalPages'] ?? 0,
      totalElements: json['totalElements'] ?? 0,
      last: json['last'] ?? true,
      first: json['first'] ?? true,
    );
  }
}
