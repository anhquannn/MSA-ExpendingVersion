import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/repositories/promo_code_repository.dart';

class PromoCodeRepositoryImpl extends IPromoCodeRepository {
  @override
  Future<PromoCodeModel?> addPromoCode(PromoCodeModel promoCode) async {
    final data = await HttpConnection.post(
      isToken: true,
      createPromoCode,
      body: promoCode.toJson(),
    );
    if (data.isSuccess) {
      return PromoCodeModel.fromJson(data.data);
    }
    return null;
  }

  @override
  Future<bool> deletePromoCode(String id) async {
    final data = await HttpConnection.delete(
      isToken: true,
      '$deletePromoCode$id',
    );
    return data.isSuccess;
  }

  @override
  Future<List<PromoCodeModel>> getAllPromoCodes({
    int page = 1,
    int pageSize = 10,
  }) async {
    final queryParameters = {'page': page.toString(), 'pageSize': pageSize.toString()};
    final url = HttpConnection.buildUrlWithQueryParams(
      getAllPromoCode,
      queryParameters,
    );
    final data = await HttpConnection.get(isToken: true, url);
    if (data.isSuccess) {
      final List<PromoCodeModel> promoCodes = [];
      for (var item in data.data) {
        promoCodes.add(PromoCodeModel.fromJson(item));
      }
      return promoCodes;
    }
    return [];
  }

  @override
  Future<PromoCodeModel?> getPromoCodeById(String id) async {
    final data = await HttpConnection.get(
      isToken: true,
      '$getPromoCodeById$id',
    );
    if (data.isSuccess) {
      return PromoCodeModel.fromJson(data.data);
    }
    return null;
  }

  @override
  Future<PromoCodeModel> updatePromoCode(
    PromoCodeModel promoCode,
    int someInt,
  ) async {
    final data = await HttpConnection.put(
      isToken: true,
      '$updatePromoCode$someInt',
      body: promoCode.toJson(),
    );
    if (data.isSuccess) {
      return PromoCodeModel.fromJson(data.data);
    }
    throw Exception('Failed to update promo code');
  }
}
