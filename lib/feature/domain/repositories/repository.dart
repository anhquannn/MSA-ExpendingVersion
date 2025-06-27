import 'package:msa/feature/data/datasources/global/http_connection.dart'
    show ApiResponse;
import 'package:msa/feature/data/model/request/category_filter_request.dart';
import 'package:msa/feature/data/model/request/get_branch_request_model.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/request/product_get_all_request_model.dart';
import 'package:msa/feature/data/model/request/promocode_request_model.dart';
import 'package:msa/feature/data/model/request/user_login_request.dart';
import 'package:msa/feature/data/model/request/user_update_request.dart';
import 'package:msa/feature/data/repositories/branch_connection.dart';
import 'package:msa/feature/data/repositories/cart_item_connection.dart';
import 'package:msa/feature/data/repositories/category_connection.dart';
import 'package:msa/feature/data/repositories/product_connection.dart';
import 'package:msa/feature/data/repositories/promo_code_connection.dart';
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
}
