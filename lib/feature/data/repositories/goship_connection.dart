import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';

import '../datasources/global/http_connection.dart';

class GoshipRepository {
  static Future<List<City>> onGetCities() async {
    final data = await HttpConnection.get(getCities);
    if (data.isSuccess) {
      final response =
          (data.data as List).map((e) => City.fromJson(e)).toList();
      HttpConnection.cityGlobal = response;
      return response;
    }
    return [];
  }

  static Future<List<District>> onGetDistrictsApi(String cityId) async {
    final data = await HttpConnection.get('$getDistricts$cityId');
    if (data.isSuccess) {
      final response =
          (data.data as List).map((e) => District.fromJson(e)).toList();
      HttpConnection.districtGlobal = response;
      return response;
    }
    return [];
  }

  static Future<List<Ward>> onGetWardsApi(String districtId) async {
    final data = await HttpConnection.get('$getWards$districtId');
    if (data.isSuccess) {
      final response =
          (data.data as List).map((e) => Ward.fromJson(e)).toList();
      HttpConnection.wardGlobal = response;
      return response;
    }
    return [];
  }
}
