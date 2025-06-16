import 'package:msa/feature/data/model/response/user_login_response.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/data/model/request/user_login_request.dart';
import 'package:msa/feature/data/model/request/user_register_request.dart';
import 'package:msa/feature/data/model/request/user_update_request.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import '../../../core/config/constant.dart';
import '../../domain/repositories/user_repository.dart';
import '../datasources/local/starage.dart';

class UserRepositoryImpl implements IUserRepository {
  @override
  Future<UserModel?> registerUser(UserRegisterRequest request) async {
    final data = await HttpConnection.post(register, body: request.toJson());
    return data.isSuccess ? UserModel.fromJson(data.data) : null;
  }

  @override
  Future<bool> loginUser(UserLoginRequest request) async {
    final response = await HttpConnection.post(login, body: request.toJson());
    if (response.isSuccess) {
      final data = UserModel.fromJson(response.data);
      Storage.userModelGlobal = data;
      Storage.saveUserModel(data);
      return true;
    }
    return false;
  }

  @override
  Future<bool> verifyOtpUser(String otpRequest) async {
    final response = await HttpConnection.post(
      verifyOtp,
      body: {'otp': otpRequest},
    );
    if (response.isSuccess) {
      final data = AccessTokenResponse.fromJson(response.data);

      Storage.refreshToken = data.refreshToken;
      Storage.token = data.accessToken;
      Storage.saveToken(data.accessToken);
      Storage.saveRefreshToken(data.refreshToken);
      return true;
    }
    return false;
  }

  @override
  Future<bool> resetPassword(String email) async {
    final response = await HttpConnection.post(
      resetPass,
      body: {'email': email},
    );
    if (response.isSuccess) {
      Storage.otp = response.data;
      return true;
    }
    return false;
  }

  @override
  Future<bool> resetPasswordWithoutOtp(String email) async {
    final response = await HttpConnection.post('$resetPassWithoutOtp$email');
    return response.isSuccess;
  }

  @override
  Future<UserModel?> onupdateUser(UserUpdateRequest request) async {
    final response = await HttpConnection.put(
      '$updateUser${request.userId}',
      body: request.toJson(),
    );
    return response.isSuccess ? UserModel.fromJson(response.data) : null;
  }

  @override
  Future<UserModel?> onGetUserByEmail() async {
    // final response = await HttpConnection.get('$getUserByEmail${HttpConnection.email}');
    final response = await HttpConnection.get(
      'user/email/nguyenanhquan20102003@gmail.com',
    );
    if (response.isSuccess) {
      Storage.userModelGlobal = UserModel.fromJson(response.data);
      return Storage.userModelGlobal;
    }
    return null;
  }
}
