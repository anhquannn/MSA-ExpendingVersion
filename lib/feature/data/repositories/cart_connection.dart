import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/repositories/cart_repository.dart';

class CartRepositoryImpl extends ICartRepository {
  @override
  Future<CartModel?> onGetOrCreateCartForUser(int userId) async {
    final String path = '$getOrCreateCart$userId';
    final data = await HttpConnection.get(path);
    if (data.isSuccess) {
      return CartModel.fromJson(data.data);
    }
    messageError = data.message;
    return null;
  }
}
