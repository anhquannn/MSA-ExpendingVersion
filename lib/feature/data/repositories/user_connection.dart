// SỬA: user_repository_impl.dart

import 'package:flutter/widgets.dart';
import 'package:msa/feature/data/model/request/change_password_request_model.dart';
import 'package:msa/feature/data/model/request/login_request_model.dart';
import 'package:msa/feature/data/model/request/user_address_request.dart';
import 'package:msa/feature/data/model/response/auth_response.dart';
import 'package:msa/feature/data/model/response/authentication_response.dart';
import 'package:msa/feature/data/model/response/user_login_response.dart';
import 'package:msa/feature/domain/entities/address_model.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/data/model/request/user_login_request.dart';
import 'package:msa/feature/data/model/request/user_register_request.dart';
import 'package:msa/feature/data/model/request/user_update_request.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/domain/repositories/user_repository.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:supabase_flutter/supabase_flutter.dart'; // Sửa lại tên file nếu là storage.dart

class UserRepositoryImpl implements IUserRepository {
  @override
  Future<UserModel?> registerUser(UserRegisterRequest request) async {
    final response = await HttpConnection.post<UserModel>(
      register,
      isToken: false,
      body: request.toJson(),
      fromJsonT: (json) => UserModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<bool> loginUser(UserLoginRequest request) async {
    final response = await HttpConnection.post<UserModel>(
      login,
      body: request.toJson(),
      isToken: false,
      fromJsonT: (json) => UserModel.fromJson(json),
    );
    if (response.isSuccess && response.result != null) {
      final data = response.result!;
      Storage.userModelGlobal = data;
      Storage.saveUserModel(data);
      Storage.saveEmail(data.email ?? '');
      return true;
    }
    return false;
  }

  @override
  Future<bool> verifyOtpUser(String otpRequest) async {
    final response = await HttpConnection.post<AccessTokenResponse>(
      verifyOtp,
      body: {'otp': otpRequest},
      isToken: false,
      fromJsonT: (json) => AccessTokenResponse.fromJson(json),
    );

    if (response.isSuccess && response.result != null) {
      final data = response.result!;

      // Log access token và refresh token để kiểm tra
      debugPrint('Access Token: ${data.accessToken}');
      debugPrint('Refresh Token: ${data.refreshToken}');

      Storage.token = data.accessToken;
      Storage.refreshToken = data.refreshToken;
      await Storage.saveToken(data.accessToken);
      await Storage.saveRefreshToken(data.refreshToken);
      return true;
    }

    debugPrint('OTP Verification failed. Response: ${response.message}');
    return false;
  }

  @override
  Future<bool> resetPassword(String email) async {
    // Giả sử API trả về 1 object chứa otp
    final response = await HttpConnection.post<Map<String, dynamic>>(
      resetPass,
      body: {'email': email},
      isToken: false,
      fromJsonT: (json) => json,
    );
    if (response.isSuccess && response.result != null) {
      Storage.otp = response.result!['otp'] ?? ''; // Lấy otp từ response
      return true;
    }
    return false;
  }

  @override
  Future<bool> resetPasswordWithoutOtp(String email) async {
    final response = await HttpConnection.post<dynamic>(
      '$resetPassWithoutOtp$email',
      fromJsonT: (json) => json,
    );
    return response.isSuccess;
  }

  @override
  Future<UserModel?> onupdateUser(UserUpdateRequest request) async {
    final response = await HttpConnection.put<UserModel>(
      '$updateUser${request.userId}',
      body: request.toJson(),
      fromJsonT: (json) => UserModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<UserModel?> onGetUserByEmail() async {
    final email = Storage.email;
    if (email.isEmpty) return null;

    final response = await HttpConnection.get<UserModel>(
      '$getUserByEmail$email',
      fromJsonT: (json) => UserModel.fromJson(json),
    );
    if (response.isSuccess && response.result != null) {
      Storage.userModelGlobal = response.result!;
      Storage.saveUserModel(response.result!);
    }
    return response.result;
  }

  static Future<bool> onRefreshToken(
    String refreshToken, {
    BuildContext? context,
  }) async {
    // String tk =
    //     'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtd2FuZzM4MjAzQGdtYWlsLmNvbSIsInNjb3BlIjoiUk9MRV9DVVNUT01FUiIsImlzcyI6ImNvbS5tc2EiLCJleHAiOjE4ODA3MDYwODQsInRva2VuX3R5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNzUxMTA2MDg0LCJqdGkiOiI1NTRjMzg1ZC1lYjgwLTRiNmYtODJmMC1mNDhhYTZkMWM0NTEifQ.r47-F54ytvnUjBXsoIMGPaOaGVNGo8q2-M25gf7MOWmuTUc7iXsVOwZB8mSQPbL-Fl_VLK11j8U02Sbr2w9Bbg';
    final response = await HttpConnection.post<AuthResponseModelRequest>(
      refreshTokenUrl,
      body: {'token': Storage.refreshToken},
      // body: {'token': tk},
      isToken: false,
      fromJsonT: (json) => AuthResponseModelRequest.fromJson(json),
    );
    if (response.isSuccess && response.result != null) {
      final data = response.result!;
      await Storage.saveToken(data.accessToken);
      await Storage.saveRefreshToken(data.refreshToken);
      return true;
    }
    return false;
  }

  static Future<bool> onAddAddress(UserAddressRequest model) async {
    final response = await HttpConnection.post(
      'address',
      isToken: false,
      body: model.toJson(),
      fromJsonT: (json) => UserModelResponseAddress.fromJson(json),
    );
    if (response.isSuccess) {
      return true;
    }
    return false;
  }

  static Future<bool> onUpdateDeviceId() async {
    String path =
        '$updateDeviceId${Storage.deviceId}${Storage.userModelGlobal?.userId}';
    final response = await HttpConnection.put(
      isToken: false,
      path,
      fromJsonT: (json) => UserModel.fromJson(json),
    );
    return response.isSuccess;
  }

  static onUpdateInfo(UserUpdateRequest request) async {
    String path = '$updateInfo${Storage.userModelGlobal?.userId}';
    final response = await HttpConnection.put(
      path,
      body: request.toJson(),
      fromJsonT: (json) => UserModel.fromJson(json),
    );
    return response.isSuccess;
  }

  static onResendOtp(UserLoginRequest request) async {
    final response = await HttpConnection.post(
      resentOtp,
      isToken: false,
      body: request.toJson(),
      fromJsonT: (json) => UserModel.fromJson(json),
    );
    return response.isSuccess;
  }

  static Future<List<UserAddressModel>> getUserAddresses() async {
    final requestBody = {
      'userId': Storage.userModelGlobal?.userId,
      'page': 1,
      'pageSize': 10,
    };

    print('📤 Gửi yêu cầu lấy danh sách địa chỉ với body: $requestBody');

    final response = await HttpConnection.post<UserAddressPaginatedResult>(
      getAddress,
      body: requestBody,
      isToken: true,
      fromJsonT: (json) => UserAddressPaginatedResult.fromJson(json),
    );

    if (response.result != null) {
      final addresses = response.result!.content;
      print('✅ Đã nhận ${addresses.length} địa chỉ:');

      for (final addr in addresses) {
        print(
          '📍 ID: ${addr.userAddressId}, ${addr.street}, ${addr.ward}, ${addr.district}, ${addr.city} - Primary: ${addr.primary}',
        );
      }

      return addresses;
    } else {
      print('❌ Lỗi khi lấy danh sách địa chỉ: ${response.message}');
      throw Exception('Lỗi khi lấy danh sách địa chỉ: ${response.message}');
    }
  }

  static onChangePassword(UpdatePasswordRequest request, userId) async {
    final response = await HttpConnection.put(
      '$changePassword$userId',
      body: request.toJson(),
      fromJsonT: (json) => UserModel.fromJson(json),
    );
    return response.isSuccess;
  }

  static onUpdateUserAddress(
    int userAddressId,
    UserAddressUpdateRequest model,
  ) async {
    final response = await HttpConnection.put(
      '$updateUserAddress$userAddressId',
      body: model.toJson(),
      fromJsonT: (json) => UserAddressModel.fromJson(json),
    );
    if (response.isSuccess) {
      Storage.addressModel = response.result;
    }
    return response.isSuccess;
  }

  static onLoginFCM(LoginRequest request) async {
    final response = await HttpConnection.post<UserModel>(
      login,
      body: request.toJson(),
      isToken: false,
      fromJsonT: (json) => UserModel.fromJson(json),
    );
    if (response.isSuccess && response.result != null) {
      final data = response.result!;
      Storage.userModelGlobal = data;
      Storage.saveUserModel(data);
      Storage.saveEmail(data.email ?? '');
      return true;
    }
    return false;
  }

  static loginWithGoogleToken(String accessToken) async {
    try {
      final response = await HttpConnection.post(
        '$loginGoogle?accessToken=$accessToken',
        isToken: false,
        fromJsonT: (json) => AuthenticationResponse.fromJson(json),
      );

      if (response.isSuccess) {
        Storage.refreshToken = response.result?.refreshToken;
        Storage.refreshToken = response.result?.accessToken;
        Storage.saveRefreshToken(response.result?.refreshToken ?? '');
        Storage.saveToken(response.result?.accessToken ?? '');
      }

      return response.isSuccess;
    } catch (e, stack) {
      print('❌ Lỗi gọi API loginWithGoogle: $e');
      print('📛 Stacktrace: $stack');
      return false;
    }
  }

  static onGetUserInfo() async {
    final response = await HttpConnection.get<UserModel>(
      info,
      fromJsonT: (json) => UserModel.fromJson(json),
    );
    final data = response.result!;
    Storage.userModelGlobal = data;
    Storage.saveUserModel(data);
    Storage.saveEmail(data.email ?? '');
    if (response.isSuccess && response.result != null) {
      Storage.userModelGlobal = response.result!;
      Storage.saveUserModel(response.result!);
    }
    return response.result;
  }
}
