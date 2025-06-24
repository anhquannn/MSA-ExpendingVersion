// SỬA: goship_repository.dart

import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';

class GoshipRepository {
  static Future<List<City>> onGetCities() async {
    final response = await HttpConnection.get<List<City>>(
      getCities,
      fromJsonT: (json) => (json as List).map((e) => City.fromJson(e)).toList(),
    );
    
    final cities = response.result ?? [];
    if (response.isSuccess) {
      HttpConnection.cityGlobal = cities;
    }
    return cities;
  }

  static Future<List<District>> onGetDistrictsApi(String cityId) async {
    final response = await HttpConnection.get<List<District>>(
      '$getDistricts$cityId',
      fromJsonT: (json) => (json as List).map((e) => District.fromJson(e)).toList(),
    );
    
    final districts = response.result ?? [];
    if (response.isSuccess) {
      HttpConnection.districtGlobal = districts;
    }
    return districts;
  }

  static Future<List<Ward>> onGetWardsApi(String districtId) async {
    final response = await HttpConnection.get<List<Ward>>(
      '$getWards$districtId',
      fromJsonT: (json) => (json as List).map((e) => Ward.fromJson(e)).toList(),
    );
    
    final wards = response.result ?? [];
    if (response.isSuccess) {
      HttpConnection.wardGlobal = wards;
    }
    return wards;
  }
}