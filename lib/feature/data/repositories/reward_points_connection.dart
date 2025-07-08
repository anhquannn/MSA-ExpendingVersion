// rp/user/7/balance
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';

class RewardPointsConnection {
static Future<double?> getReward() async {
  final response = await HttpConnection.get(
    'rp/user/${Storage.userModelGlobal?.userId}/balance',
    fromJsonT: (json) => json,
  );

  if (response.result is double) {
    return response.result;
  }

  if (response.result is String) {
    return double.tryParse(response.result);
  }

  if (response.result is int) {
    return (response.result as int).toDouble();
  }

  return null;
}

}
