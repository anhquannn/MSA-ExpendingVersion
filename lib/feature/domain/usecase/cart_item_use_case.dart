import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/repositories/cart_item_repository.dart';

class CartItemUseCase {
  final GetCartItemUseCase getCartItem;
  final CalculateCartTotalUseCase calculateCartTotal;
  final GetCartItemByIdUseCase getCartItemById;
  final GetCartItemsByCartIdUseCase getCartItemsByCartId;
  final AddToCartUseCase addToCart;
  final CreateCartItemUseCase createCartItem;
  final UpdateCartItemsSelectionUseCase updateCartItemsSelection;
  final DeleteCartItemUseCase deleteCartItem;
  final ClearCartUseCase clearCart;

  CartItemUseCase({
    required this.getCartItem,
    required this.calculateCartTotal,
    required this.getCartItemById,
    required this.getCartItemsByCartId,
    required this.addToCart,
    required this.createCartItem,
    required this.updateCartItemsSelection,
    required this.deleteCartItem,
    required this.clearCart,
  });
}

// Get Cart Item
class GetCartItemUseCase {
  final ICartItemRepository repository;
  GetCartItemUseCase(this.repository);

  Future<CartItemModel?> call(int cartId, int productId) {
    return repository.onGetCartItem(cartId, productId);
  }
}

// Calculate Cart Total
class CalculateCartTotalUseCase {
  final ICartItemRepository repository;
  CalculateCartTotalUseCase(this.repository);

  Future<String> call(int cartId) {
    return repository.onCalculateCartTotal(cartId);
  }
}

// Get Cart Item By ID
class GetCartItemByIdUseCase {
  final ICartItemRepository repository;
  GetCartItemByIdUseCase(this.repository);

  Future<CartItemModel?> call(int cartItemId) {
    return repository.onGetCartItemById(cartItemId);
  }
}

// Get All Cart Items By Cart ID
class GetCartItemsByCartIdUseCase {
  final ICartItemRepository repository;
  GetCartItemsByCartIdUseCase(this.repository);

  Future<List<CartItemModel>?> call(int cartId) {
    return repository.onGetCartItemsByCartId(cartId);
  }
}

// Add To Cart
class AddToCartUseCase {
  final ICartItemRepository repository;
  AddToCartUseCase(this.repository);

  Future<bool> call(AddToCartRequest request) {
    return repository.onAddToCart(request);
  }
}

// Create Cart Item
class CreateCartItemUseCase {
  final ICartItemRepository repository;
  CreateCartItemUseCase(this.repository);

  Future<CartItemModel?> call(CartItemModel item) {
    return repository.onCreateCartItem(item);
  }
}

// Update Selection
class UpdateCartItemsSelectionUseCase {
  final ICartItemRepository repository;
  UpdateCartItemsSelectionUseCase(this.repository);

  Future<bool> call(List<int> cartItemIds, bool isSelected) {
    return repository.onUpdateCartItemsSelection(cartItemIds, isSelected);
  }
}

// Delete Cart Item
class DeleteCartItemUseCase {
  final ICartItemRepository repository;
  DeleteCartItemUseCase(this.repository);

  Future<bool> call(int cartItemId) {
    return repository.onDeleteCartItem(cartItemId);
  }
}

// Clear Cart
class ClearCartUseCase {
  final ICartItemRepository repository;
  ClearCartUseCase(this.repository);

  Future<bool> call(int cartId) {
    return repository.onClearCart(cartId);
  }
}
