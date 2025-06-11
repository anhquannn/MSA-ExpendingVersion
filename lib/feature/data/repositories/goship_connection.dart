import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';

import '../datasources/global/http_connection.dart';

class GoshipRepository {
  Future<bool> onGetCities() async {
    final data = await HttpConnection.get(getCities);
    if (data.isSuccess) {
      final response =
          (data.data as List).map((e) => City.fromJson(e)).toList();
      HttpConnection.cityGlobal = response;
      return true;
    }
    return false;
  }

  Future<bool> onGetDistrictsApi(String cityId) async {
    final data = await HttpConnection.get('$getDistricts$cityId');
    if (data.isSuccess) {
      final response =
          (data.data as List).map((e) => District.fromJson(e)).toList();
      HttpConnection.districtGlobal = response;
      return true;
    }
    return false;
  }

  Future<bool> onGetWardsApi(String districtId) async {
    final data = await HttpConnection.get('$getWards$districtId');
    if (data.isSuccess) {
      final response =
          (data.data as List).map((e) => Ward.fromJson(e)).toList();
      HttpConnection.wardGlobal = response;
      return true;
    }
    return false;
  }
}
