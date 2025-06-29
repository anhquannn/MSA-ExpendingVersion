import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/domain/entities/address_model.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

class Storage {
  static String otp = '';
  // static String token = '';
  static  String token =
      'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtd2FuZzM4MjAzQGdtYWlsLmNvbSIsInNjb3BlIjoiUk9MRV9DVVNUT01FUiIsImlzcyI6ImNvbS5tc2EiLCJleHAiOjE3NjQwNjYwODQsInRva2VuX3R5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3NTExMDYwODQsImp0aSI6IjY5YjQ1MGUyLTkwNDktNDdlMi04MWYwLTFiMDgwODljMDdlOSJ9.bH_NS5KyZWffuKRc1gpB7-WORDopL0BI709h7dHCp9-OPAvAIeF8IL5nVRZnLr_plhj4EMhmcMxkmViNrWSXSQ';
  static String deviceId = '';
  static String? messageError;
  static String? refreshToken = '';

  static UserAddressModel? addressModel;
  static UserModel? userModelGlobal;
  static CartModel? cartModelGlobal;
  static BranchModel? branchModelGlobal;
  static String email = 'mwang38203@gmail.com';

  // ===================== ĐỌC DỮ LIỆU =====================
  static Future<void> readFromLocalStorage() async {
    final prefs = await SharedPreferences.getInstance();

    token = prefs.getString(accessTokenKey) ?? '';
    deviceId = prefs.getString(deviceIdKey) ?? '';
    email = prefs.getString(emailKey) ?? 'mwang38203@gmail.com';
    // refreshToken=prefs.getString(refreshTokenKey) ?? '';
    refreshToken = '';

    final userJson = prefs.getString(userModelKey);
    if (userJson != null) {
      userModelGlobal = UserModel.fromJson(jsonDecode(userJson));
    }

    final cartJson = prefs.getString(cartModelKey);
    if (cartJson != null) {
      cartModelGlobal = CartModel.fromJson(jsonDecode(cartJson));
    }

    final branchJson = prefs.getString(branchModelKey);
    if (branchJson != null) {
      branchModelGlobal = BranchModel.fromJson(jsonDecode(branchJson));
    }

    final addressJson = prefs.getString(addressKey);
    if (addressJson != null) {
      addressModel = UserAddressModel.fromJson(jsonDecode(addressJson));
    }
  }

  static Future<void> saveToken(String value) async {
    token = value;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(accessTokenKey, value);
  }

  static Future<void> saveRefreshToken(String value) async {
    refreshToken = value;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(refreshTokenKey, value);
  }

  static Future<void> saveDeviceId(String value) async {
    deviceId = value;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(deviceIdKey, value);
  }

  static Future<void> saveEmail(String value) async {
    email = value;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(emailKey, value);
  }

  static Future<void> saveAddress(UserAddressModel model) async {
    addressModel = model;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(addressKey, jsonEncode(model.toJson()));
  }

  static Future<void> saveUserModel(UserModel user) async {
    userModelGlobal = user;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(userModelKey, jsonEncode(user.toJson()));
  }

  static Future<void> saveCartModel(CartModel cart) async {
    cartModelGlobal = cart;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(cartModelKey, jsonEncode(cart.toJson()));
  }

  static Future<void> saveBranchModel(BranchModel branch) async {
    branchModelGlobal = branch;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(branchModelKey, jsonEncode(branch.toJson()));
  }

  // ===================== ĐĂNG XUẤT =====================

  static Future<void> onLogout() async {
    otp = '';
    token = '';
    deviceId = '';
    email = '';
    userModelGlobal = null;
    cartModelGlobal = null;
    branchModelGlobal = null;

    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(accessTokenKey);
    await prefs.remove(refreshTokenKey);
    await prefs.remove(deviceIdKey);
    await prefs.remove(emailKey);
    await prefs.remove(userModelKey);
    await prefs.remove(cartModelKey);
    await prefs.remove(branchModelKey);
    await prefs.remove(addressKey);
  }

  static Future<bool> checkLoginStatus() async {
    bool isLogin = false;
    if (refreshToken != null) {
      isLogin = true;
    } else {
      final prefs = await SharedPreferences.getInstance();
      final tokenKey = refreshTokenKey;
      final storedRefreshToken = prefs.getString(tokenKey);
      if (storedRefreshToken == null || storedRefreshToken.isEmpty) {
        return false;
      }
    }
    isLogin = await Repository.onRefresh(Storage.refreshToken ?? '');
    return isLogin;
  }
}
