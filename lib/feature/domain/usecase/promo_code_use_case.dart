import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/repositories/promo_code_repository.dart';

class PromoCodeUseCase {
  final CreatePromoCodeUseCase create;
  final UpdatePromoCodeUseCase update;
  final DeletePromoCodeUseCase delete;
  final GetAllPromoCodesUseCase getAll;
  final GetPromoCodeByIdUseCase getById;

  PromoCodeUseCase({
    required this.create,
    required this.update,
    required this.delete,
    required this.getAll,
    required this.getById,
  });
}

class CreatePromoCodeUseCase {
  final IPromoCodeRepository promoCodeRepository;

  CreatePromoCodeUseCase(this.promoCodeRepository);

  Future<void> call(PromoCodeModel promoCode) async {
    await promoCodeRepository.addPromoCode(promoCode);
  }
}

class UpdatePromoCodeUseCase {
  final IPromoCodeRepository promoCodeRepository;

  UpdatePromoCodeUseCase(this.promoCodeRepository);

  Future<void> call(PromoCodeModel promoCode, int id) async {
    await promoCodeRepository.updatePromoCode(promoCode, id);
  }
}

class DeletePromoCodeUseCase {
  final IPromoCodeRepository promoCodeRepository;

  DeletePromoCodeUseCase(this.promoCodeRepository);

  Future<void> call(String id) async {
    await promoCodeRepository.deletePromoCode(id);
  }
}

class GetAllPromoCodesUseCase {
  final IPromoCodeRepository promoCodeRepository;

  GetAllPromoCodesUseCase(this.promoCodeRepository);

  Future<List<PromoCodeModel>?> call({int page = 1, int pageSize = 10}) async {
    return promoCodeRepository.getAllPromoCodes(page: page, pageSize: pageSize);
  }
}

class GetPromoCodeByIdUseCase {
  final IPromoCodeRepository promoCodeRepository;

  GetPromoCodeByIdUseCase(this.promoCodeRepository);

  Future<PromoCodeModel?> call(String id) async {
    return promoCodeRepository.getPromoCodeById(id);
  }
}
