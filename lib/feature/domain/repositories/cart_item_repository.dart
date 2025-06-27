import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';

abstract class ICartItemRepository {
  Future<CartItemModel?> onGetCartItem(int cartId, int productId);

  Future<CartItemModel?> onGetCartItemById(int cartItemId);

  Future<List<CartItemModel>?> onGetCartItemsByCartId(int cartId);

  Future<bool> onAddToCart(AddToCartRequest request);

  Future<CartItemModel?> onCreateCartItem(CartItemModel item);

  Future<bool> onUpdateCartItemsSelection(
    List<int> cartItemIds,
    bool isSelected,
  );

  Future<bool> onDeleteCartItem(int cartItemId);

  Future<bool> onClearCart(int cartId);
}
