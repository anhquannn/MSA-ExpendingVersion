import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/repositories/promo_code_repository.dart';

class PromoCodeRepositoryImpl extends IPromoCodeRepository {
  @override
  Future<PromoCodeModel?> addPromoCode(PromoCodeModel promoCode) async {
    final response = await HttpConnection.post<PromoCodeModel>(
      createPromoCode,
      body: promoCode.toJson(),
      isToken: true,
      fromJsonT: (json) => PromoCodeModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<bool> deletePromoCode(String id) async {
    final response = await HttpConnection.delete<dynamic>(
      '$deletePromoCode$id',
      isToken: true,
      fromJsonT: (json) => json,
    );
    return response.isSuccess;
  }

  @override
  Future<List<PromoCodeModel>> getAllPromoCodes({int page = 1, int pageSize = 10}) async {
    final url = HttpConnection.buildUrlWithQueryParams(
      '$getAllPromoCode${userModelGlobal?.userId}',
      {'page': page, 'pageSize': pageSize},
    );
    final response = await HttpConnection.get<List<PromoCodeModel>>(
      url,
      isToken: true,
      fromJsonT: (json) => (json as List).map((i) => PromoCodeModel.fromJson(i)).toList(),
    );
    return response.result ?? [];
  }

  @override
  Future<PromoCodeModel?> getPromoCodeById(String id) async {
    final response = await HttpConnection.get<PromoCodeModel>(
      '$getPromoCodeById$id',
      isToken: true,
      fromJsonT: (json) => PromoCodeModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<PromoCodeModel?> updatePromoCode(PromoCodeModel promoCode, int someInt) async {
    // SỬA: Thay đổi kiểu trả về để an toàn hơn khi request thất bại
    final response = await HttpConnection.put<PromoCodeModel>(
      '$updatePromoCode$someInt',
      body: promoCode.toJson(),
      isToken: true,
      fromJsonT: (json) => PromoCodeModel.fromJson(json),
    );
    // Trả về null nếu thất bại, tầng trên sẽ không bị crash bởi exception
    return response.result;
  }
}