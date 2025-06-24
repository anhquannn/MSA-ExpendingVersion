// SỬA: user_repository_impl.dart

import 'package:msa/feature/data/model/response/auth_response.dart';
import 'package:msa/feature/data/model/response/user_login_response.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/data/model/request/user_login_request.dart';
import 'package:msa/feature/data/model/request/user_register_request.dart';
import 'package:msa/feature/data/model/request/user_update_request.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/domain/repositories/user_repository.dart';
import 'package:msa/feature/data/datasources/local/starage.dart'; // Sửa lại tên file nếu là storage.dart

class UserRepositoryImpl implements IUserRepository {
  @override
  Future<UserModel?> registerUser(UserRegisterRequest request) async {
    final response = await HttpConnection.post<UserModel>(
      register,
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
      await Storage.saveToken(data.accessToken);
      await Storage.saveRefreshToken(data.refreshToken);
      return true;
    }
    return false;
  }

  @override
  Future<bool> resetPassword(String email) async {
    // Giả sử API trả về 1 object chứa otp
    final response = await HttpConnection.post<Map<String, dynamic>>(
      resetPass,
      body: {'email': email},
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
      Storage.saveUserModel(
        response.result!,
      ); 
    }
    return response.result;
  }

  static Future<bool> onRefreshToken(String refreshToken) async {
    final response = await HttpConnection.post<AuthResponseModelRequest>(
      refreshTokenUrl,
      body: {'token': refreshToken}, 
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
}
