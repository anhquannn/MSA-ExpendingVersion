import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/data/model/request/user_login_request.dart';
import 'package:msa/feature/data/model/request/user_register_request.dart';
import 'package:msa/feature/data/model/request/user_update_request.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import '../../../core/config/constant.dart';
import '../../domain/repositories/user_repository.dart';

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
      HttpConnection.otp = response.data;
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
      HttpConnection.token = response.data;
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
      HttpConnection.otp = response.data;
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
    final response = await HttpConnection.get('$getUserByEmail${HttpConnection.email}');
    if (response.isSuccess) {
      HttpConnection.userModelGlobal = UserModel.fromJson(response.data);
      return HttpConnection.userModelGlobal;
    }
    return null;
  }
}
