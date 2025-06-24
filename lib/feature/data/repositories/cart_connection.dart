// SỬA: cart_repository_impl.dart

import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/repositories/cart_repository.dart';

class CartRepositoryImpl extends ICartRepository {
  @override
  Future<CartModel?> onGetOrCreateCartForUser(int userId) async {
    final String path = '$getOrCreateCart$userId';
    
    // SỬA: Sử dụng HttpConnection.get<T> và cung cấp fromJsonT
    final ApiResponse<CartModel> response = await HttpConnection.get<CartModel>(
      path,
      fromJsonT: (json) => CartModel.fromJson(json),
    );
    
    // Gán message lỗi nếu có
    if (!response.isSuccess) {
      messageError = response.message;
    }

    // Trả về kết quả đã được parse, sẽ là null nếu request thất bại
    return response.result;
  }
}