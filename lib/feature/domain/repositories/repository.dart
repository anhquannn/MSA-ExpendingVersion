import 'package:msa/feature/data/datasources/global/http_connection.dart'
    show ApiResponse;
import 'package:msa/feature/data/model/request/category_filter_request.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/request/product_get_all_request_model.dart';
import 'package:msa/feature/data/model/request/promocode_request_model.dart';
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
}
