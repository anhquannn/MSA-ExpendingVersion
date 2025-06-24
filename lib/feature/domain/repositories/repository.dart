import 'package:msa/feature/data/repositories/user_connection.dart';

class Repository {
  static onRefresh(String accessToken) =>
      UserRepositoryImpl.onRefreshToken(accessToken);
}
