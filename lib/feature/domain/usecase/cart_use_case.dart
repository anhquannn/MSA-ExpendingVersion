import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/repositories/cart_repository.dart';

class CartUseCase {
  final GetOrCreateCartForUserUseCase create;
  CartUseCase({required this.create});
}

class GetOrCreateCartForUserUseCase {
  final ICartRepository repository;
  GetOrCreateCartForUserUseCase(this.repository);
  Future<CartModel?> call(int userId) {
    return repository.onGetOrCreateCartForUser(userId);
  }
}
