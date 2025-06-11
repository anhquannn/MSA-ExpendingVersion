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
    final response = await HttpConnection.post(path);
    return response.isSuccess;
  }

  @override
  Future<String> onCalculateCartTotal(int cartId) async {
    final String path = '$calculateCartTotal$cartId';
    final response = await HttpConnection.get(path);
    return response.data.toString();
  }

  @override
  Future<bool> onClearCart(int cartId) async {
    final String path = "$clearCart$cartId";
    final response = await HttpConnection.delete(path);
    return response.isSuccess;
  }

  @override
  Future<CartItemModel?> onCreateCartItem(CartItemModel item) async {
    final response = await HttpConnection.post(
      createCartItem,
      body: item.toJson(),
    );
    if (response.isSuccess) return CartItemModel.fromJson(response.data);
    return null;
  }

  @override
  Future<bool> onDeleteCartItem(int cartItemId) async {
    final String path = '$deleteCartItem$cartItemId';
    final response = await HttpConnection.delete(path);
    return response.isSuccess;
  }

  @override
  Future<CartItemModel?> onGetCartItem(int cartId, int productId) async {
    final String path = '$getCartItem$cartId/$productId';
    final response = await HttpConnection.get(path);
    if (response.isSuccess) return CartItemModel.fromJson(response.data);
    return null;
  }

  @override
  Future<CartItemModel?> onGetCartItemById(int cartItemId) async {
    final String path = '$getCartItemById$cartItemId';
    final response = await HttpConnection.get(path);
    if (response.isSuccess) return CartItemModel.fromJson(response.data);
    return null;
  }

  @override
  Future<List<CartItemModel>> onGetCartItemsByCartId(int cartId) async {
    final String path = '$getCartItemsByCartId$cartId';
    final response = await HttpConnection.get(path);
    if (response.isSuccess) {
      final List<dynamic> jsonList = response.data;
      return jsonList.map((json) => CartItemModel.fromJson(json)).toList();
    }
    return [];
  }

  @override
  Future<bool> onUpdateCartItemsSelection(
    List<int> cartItemIds,
    bool isSelected,
  ) async {
    final String path =
        '$updateCartItemsSelection${isSelected == true ? 'true' : 'false'}';
    final response = await HttpConnection.put(
      path,
      body: {"cartItemIds": cartItemIds},
    );
    return response.isSuccess;
  }
}
