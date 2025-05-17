import 'package:msa/feature/domain/entities/promo_code_model.dart';

abstract class IPromoCodeRepository {
  Future<List<PromoCodeModel>?> getAllPromoCodes({
    int page = 1,
    int pageSize = 10,
  });
  Future<PromoCodeModel?> getPromoCodeById(String id);
  Future<PromoCodeModel?> addPromoCode(PromoCodeModel promoCode);
  Future<PromoCodeModel?> updatePromoCode(PromoCodeModel promoCode, int id);
  Future<bool> deletePromoCode(String id);
}
