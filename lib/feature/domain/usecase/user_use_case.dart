
import '../../data/model/request/user_login_request.dart';
import '../../data/model/request/user_register_request.dart';
import '../../data/model/request/user_update_request.dart';
import '../entities/user_model.dart';
import '../repositories/user_repository.dart';

class UserUseCases {
  final LoginUserUseCase login;
  final RegisterUserUseCase register;
  final VerifyOtpUserUseCase verifyOtp;
  final ResetPasswordUseCase resetPassword;
  final ResetPasswordWithoutOtpUseCase resetPasswordWithoutOtp;
  final UpdateUserUseCase update;
  final GetUserByEmailUseCase getUserByEmail;


  UserUseCases({
    required this.login,
    required this.register,
    required this.verifyOtp,
    required this.resetPassword,
    required this.resetPasswordWithoutOtp,
    required this.update,
    required this.getUserByEmail,
  });
}
class LoginUserUseCase {
  final IUserRepository repository;

  LoginUserUseCase(this.repository);

  Future<bool> call(UserLoginRequest request) {
    return repository.loginUser(request);
  }
}

class RegisterUserUseCase {
  final IUserRepository repository;

  RegisterUserUseCase(this.repository);

  Future<UserModel?> call(UserRegisterRequest request) {
    return repository.registerUser(request);
  }
}

class ResetPasswordUseCase {
  final IUserRepository repository;

  ResetPasswordUseCase(this.repository);

  Future<bool> call(String email) {
    return repository.resetPassword(email);
  }
}

class ResetPasswordWithoutOtpUseCase {
  final IUserRepository repository;

  ResetPasswordWithoutOtpUseCase(this.repository);

  Future<bool> call(String email) {
    return repository.resetPasswordWithoutOtp(email);
  }
}

class UpdateUserUseCase {
  final IUserRepository repository;

  UpdateUserUseCase(this.repository);

  Future<UserModel?> call(UserUpdateRequest request) {
    return repository.onupdateUser(request);
  }
}

class VerifyOtpUserUseCase {
  final IUserRepository repository;

  VerifyOtpUserUseCase(this.repository);

  Future<bool> call(String otp) {
    return repository.verifyOtpUser(otp);
  }
}

class GetUserByEmailUseCase {
  final IUserRepository repository;

  GetUserByEmailUseCase(this.repository);

  Future<UserModel?> call() {
    return repository.onGetUserByEmail();
  }
}

