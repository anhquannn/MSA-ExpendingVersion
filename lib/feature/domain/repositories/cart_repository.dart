import 'package:msa/feature/domain/entities/cart_model.dart';

abstract class ICartRepository {
  Future<CartModel?> onGetOrCreateCartForUser(int userId);
}
