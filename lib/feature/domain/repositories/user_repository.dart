import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/data/model/request/user_login_request.dart';
import 'package:msa/feature/data/model/request/user_register_request.dart';
import 'package:msa/feature/data/model/request/user_update_request.dart';

abstract class IUserRepository {
  Future<UserModel?> registerUser(UserRegisterRequest request);
  Future<bool> loginUser(UserLoginRequest request);
  Future<bool> verifyOtpUser(String otpRequest);
  Future<bool> resetPassword(String email);
  Future<bool> resetPasswordWithoutOtp(String email);
  Future<UserModel?> onupdateUser(UserUpdateRequest request);
  Future<UserModel?> onGetUserByEmail();
}
