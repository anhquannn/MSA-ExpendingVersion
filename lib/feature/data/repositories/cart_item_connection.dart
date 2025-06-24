// SỬA: cart_item_repository_impl.dart

import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/repositories/cart_item_repository.dart';

class CartItemRepositoryImpl extends ICartItemRepository {
  @override
  Future<bool> onAddToCart(AddToCartRequest request) async {
    String path = HttpConnection.buildUrlWithQueryParams(
      addToCart,
      request.toJson(),
    );

    // SỬA: Đối với các hàm chỉ cần biết thành công hay thất bại,
    // ta có thể dùng <dynamic> và không cần parse chi tiết.
    final response = await HttpConnection.post<dynamic>(
      path,
      fromJsonT: (json) => json, // Không cần parse cụ thể
    );
    return response.isSuccess;
  }

  @override
  Future<String> onCalculateCartTotal(int cartId) async {
    final String path = '$calculateCartTotal$cartId';

    // SỬA: Giả sử API trả về một con số (double hoặc int).
    final response = await HttpConnection.get<double>(
      path,
      fromJsonT: (json) => (json as num).toDouble(),
    );

    // Trả về giá trị đã parse hoặc "0" nếu thất bại.
    return response.result?.toString() ?? "0";
  }

  @override
  Future<bool> onClearCart(int cartId) async {
    final String path = "$clearCart$cartId";
    final response = await HttpConnection.delete<dynamic>(
      path,
      fromJsonT: (json) => json,
    );
    return response.isSuccess;
  }

  @override
  Future<CartItemModel?> onCreateCartItem(CartItemModel item) async {
    final response = await HttpConnection.post<CartItemModel>(
      createCartItem,
      body: item.toJson(),
      fromJsonT: (json) => CartItemModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<bool> onDeleteCartItem(int cartItemId) async {
    final String path = '$deleteCartItem$cartItemId';
    final response = await HttpConnection.delete<dynamic>(
      path,
      fromJsonT: (json) => json,
    );
    return response.isSuccess;
  }

  @override
  Future<CartItemModel?> onGetCartItem(int cartId, int productId) async {
    final String path = '$getCartItem$cartId/$productId';
    final response = await HttpConnection.get<CartItemModel>(
      path,
      fromJsonT: (json) => CartItemModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<CartItemModel?> onGetCartItemById(int cartItemId) async {
    final String path = '$getCartItemById$cartItemId';
    final response = await HttpConnection.get<CartItemModel>(
      path,
      fromJsonT: (json) => CartItemModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<List<CartItemModel>> onGetCartItemsByCartId(int cartId) async {
    final String path = '$getCartItemsByCartId$cartId';

    // SỬA: Xử lý cho kiểu trả về là một List.
    final response = await HttpConnection.get<List<CartItemModel>>(
      path,
      // `fromJsonT` sẽ nhận vào mảng JSON và map nó thành List<CartItemModel>
      fromJsonT: (json) {
        final List<dynamic> jsonList = json as List<dynamic>;
        return jsonList.map((itemJson) => CartItemModel.fromJson(itemJson)).toList();
      },
    );

    // Trả về danh sách, hoặc một danh sách rỗng nếu có lỗi.
    return response.result ?? [];
  }

  @override
  Future<bool> onUpdateCartItemsSelection(
    List<int> cartItemIds,
    bool isSelected,
  ) async {
    // SỬA: Sửa lại cách build path để đúng chuẩn hơn
    final String path = '$updateCartItemsSelection?isSelected=${isSelected.toString()}';

    final response = await HttpConnection.put<dynamic>(
      path,
      body: {"cartItemIds": cartItemIds},
      fromJsonT: (json) => json,
    );
    return response.isSuccess;
  }
}