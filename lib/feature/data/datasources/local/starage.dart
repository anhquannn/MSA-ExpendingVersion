import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:path/path.dart' as path;
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

class Storage {
  static String otp = '';
  static String token = '';
  static String deviceId = '';
  static String? messageError;
  static String? refreshToken = '';

  static UserModel? userModelGlobal;
  static CartModel? cartModelGlobal;
  static BranchModel? branchModelGlobal;
  static String email = 'nguyenanhquan20102003@gmail.com';

  // ===================== ĐỌC DỮ LIỆU =====================
  static Future<void> readFromLocalStorage() async {
    final prefs = await SharedPreferences.getInstance();

    otp = prefs.getString('otp') ?? '';
    token = prefs.getString('token') ?? '';
    deviceId = prefs.getString('deviceId') ?? '';
    email = prefs.getString('email') ?? 'nguyenanhquan20102003@gmail.com';

    final userJson = prefs.getString('userModel');
    if (userJson != null) {
      userModelGlobal = UserModel.fromJson(jsonDecode(userJson));
    }

    final cartJson = prefs.getString('cartModel');
    if (cartJson != null) {
      cartModelGlobal = CartModel.fromJson(jsonDecode(cartJson));
    }

    final branchJson = prefs.getString('branchModel');
    if (branchJson != null) {
      branchModelGlobal = BranchModel.fromJson(jsonDecode(branchJson));
    }
  }

  // ===================== GHI DỮ LIỆU =====================

  static Future<void> saveOtp(String value) async {
    otp = value;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('otp', value);
  }

  static Future<void> saveToken(String value) async {
    token = value;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('token', value);
  }

    static Future<void> saveRefreshToken(String value) async {
    token = value;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('refreshToken', value);
  }

  static Future<void> saveDeviceId(String value) async {
    deviceId = value;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('deviceId', value);
  }

  static Future<void> saveEmail(String value) async {
    email = value;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('email', value);
  }

  static Future<void> saveUserModel(UserModel user) async {
    userModelGlobal = user;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('userModel', jsonEncode(user.toJson()));
  }

  static Future<void> saveCartModel(CartModel cart) async {
    cartModelGlobal = cart;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('cartModel', jsonEncode(cart.toJson()));
  }

  static Future<void> saveBranchModel(BranchModel branch) async {
    branchModelGlobal = branch;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('branchModel', jsonEncode(branch.toJson()));
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
    await prefs.remove('otp');
    await prefs.remove('token');
    await prefs.remove('deviceId');
    await prefs.remove('email');
    await prefs.remove('userModel');
    await prefs.remove('cartModel');
    await prefs.remove('branchModel');
  }
}
