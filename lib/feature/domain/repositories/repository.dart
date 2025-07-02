import 'package:msa/feature/data/datasources/global/http_connection.dart'
    show ApiResponse;
import 'package:msa/feature/data/model/request/cancel_order_requesr_model.dart';
import 'package:msa/feature/data/model/request/cartitem_selection_request_model.dart';
import 'package:msa/feature/data/model/request/category_filter_request.dart';
import 'package:msa/feature/data/model/request/change_password_request_model.dart';
import 'package:msa/feature/data/model/request/create_order_model_request.dart';
import 'package:msa/feature/data/model/request/feedback_filter_request_model.dart';
import 'package:msa/feature/data/model/request/feedback_request_model.dart';
import 'package:msa/feature/data/model/request/get_branch_request_model.dart';
import 'package:msa/feature/data/model/request/login_request_model.dart';
import 'package:msa/feature/data/model/request/order_paging_request_model.dart';
import 'package:msa/feature/data/model/request/product_conbine_model_request.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/request/product_get_all_request_model.dart';
import 'package:msa/feature/data/model/request/promocode_request_model.dart';
import 'package:msa/feature/data/model/request/supplier_filter_request.dart';
import 'package:msa/feature/data/model/request/user_address_request.dart';
import 'package:msa/feature/data/model/request/user_login_request.dart';
import 'package:msa/feature/data/model/request/user_update_request.dart';
import 'package:msa/feature/data/model/response/feedback_filter_response.dart';
import 'package:msa/feature/data/repositories/branch_connection.dart';
import 'package:msa/feature/data/repositories/cart_item_connection.dart';
import 'package:msa/feature/data/repositories/category_connection.dart';
import 'package:msa/feature/data/repositories/feedback_connection.dart';
import 'package:msa/feature/data/repositories/order_connection.dart';
import 'package:msa/feature/data/repositories/product_connection.dart';
import 'package:msa/feature/data/repositories/promo_code_connection.dart';
import 'package:msa/feature/data/repositories/return_order_connection.dart';
import 'package:msa/feature/data/repositories/supplier_connection.dart';
import 'package:msa/feature/data/repositories/user_connection.dart';
import 'package:msa/feature/domain/entities/address_model.dart';

class Repository {
  static onRefresh(String accessToken) =>
      UserRepositoryImpl.onRefreshToken(accessToken);

  static onCreateAddress(UserAddressRequest model) =>
      UserRepositoryImpl.onAddAddress(model);

  static onGetAllCategory(CategoryFilterRequest model) =>
      CategoryRepositoryImpl.getAllCategory(model);

  static onGetAllPromoCode(PromoCodeRequestModel model) =>
      PromoCodeRepositoryImpl.getAllPromoCode(model);

  static onFilterProducts(ProductFilterRequest model) =>
      ProductRepositoryImpl.onFilterProducts(model);

  static onCaculateOrder(int cartId) =>
      CartItemRepositoryImpl.onCalculateCartTotal(cartId);

  static onUpdateQuantity({
    int? cartItemId,
    int? branchId,
    int? quantity,
    bool select = false,
  }) => CartItemRepositoryImpl.onUpdateQuantity(
    branchId: branchId,
    cartItemId: cartItemId,
    quantity: quantity,
    select: select,
  );

  static onGetAllBranch(BranchFilterRequest request) =>
      BranchRepositoryImpl.getBranchPaging(request);

  static onUpdateDeviceId() => UserRepositoryImpl.onUpdateDeviceId();

  static onUpdateInfo(UserUpdateRequest request) =>
      UserRepositoryImpl.onUpdateInfo(request);

  static onResendOtp(UserLoginRequest request) =>
      UserRepositoryImpl.onResendOtp(request);

  static getUserAddresses() => UserRepositoryImpl.getUserAddresses();

  static getCartItem(int cartId) =>
      CartItemRepositoryImpl.onGetCartItemsCartId(cartId);

  static onChangePassword(UpdatePasswordRequest request, int userId) =>
      UserRepositoryImpl.onChangePassword(request, userId);

  static onUpdateUserAddress(
    int userAdressId,
    UserAddressUpdateRequest model,
  ) => UserRepositoryImpl.onUpdateUserAddress(userAdressId, model);

  static onCreateOrder(CreateOrderRequestModel model) =>
      OrderRepositoryImpl.createOrderAPI(model);

  static onGetPreviewOrder({List<String>? promoCode}) =>
      OrderRepositoryImpl.onGetPreviewOrder(promoCodes: promoCode);

  static createShipment({int? orderId, int? addressId, String? rateId}) =>
      OrderRepositoryImpl.createShipment(
        addressId: addressId,
        orderId: orderId,
        rateId: rateId,
      );

  static onUpdateOrderAPI(CreateOrderRequestModel model, int? orderId) =>
      OrderRepositoryImpl.onUpdateOrderAPI(model, orderId);

  static onGetVnpayUrl(int? orderId) =>
      OrderRepositoryImpl.onGetVnpayUrl(orderId);

  static onGetListOrder(OrderFilterRequest model) =>
      OrderRepositoryImpl.onGetListOrder(model);

  static onCreateFeedBack(FeedbackRequest request) =>
      FeedbackRepositoryImpl.createFeedbackApi(request);

  static onGetOrderDetail({int? orderId}) =>
      OrderRepositoryImpl.onGetOrderDetail(orderId: orderId);

  static onGetProductById(int productId) =>
      ProductRepositoryImpl.getProductByIdApi(productId);

  static onGetConbineProduct(ProductCombinationFilterRequest request) =>
      ProductRepositoryImpl.getProductCombine(request);

  static onUpdateCartItemsSelectionAPI(
    CartItemSelectionRequest request,
    bool isSelected,
  ) =>
      CartItemRepositoryImpl.onUpdateCartItemsSelectionAPI(request, isSelected);

  static onLoginFCM(LoginRequest request) =>
      UserRepositoryImpl.onLoginFCM(request);

  static loginWithGoogleToken(String accessToken) =>
      UserRepositoryImpl.loginWithGoogleToken(accessToken);

  static onGetUserInfo() => UserRepositoryImpl.onGetUserInfo();

  static createFeedbackAPI(FeedbackRequest request) =>
      FeedbackRepositoryImpl.createFeedbackAPI(request);

  static createCanceledOrder(CancelOrderRequest request) =>
      ReturnOrderConnection.createCanceledOrder(request);

  static Future<List<FeedbacFilterkResponse>> getFeedback(
    FeedbackFilterRequest request,
  ) => FeedbackRepositoryImpl.getFeedback(request);

  static getAllSupply(SupplierFilterRequest request) =>
      SupplierConnection.getAll(request);
}
