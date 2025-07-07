import 'dart:convert';

import 'package:flutter/foundation.dart';

import '../models/inventory.dart';
import '../models/product.dart';
import '../services/api_service.dart';
import 'auth_provider.dart';

class InventoryProvider extends ChangeNotifier {
  InventoryProvider({required ApiService api, required AuthProvider auth})
      : _api = api,
        _auth = auth;

  final ApiService _api;
  final AuthProvider _auth;

  // Pagination state for inventories
  int _inventoryPage = 1;
  bool _inventoryHasMore = true;
  String _keyword = '';

  // Pagination state for products
  int _productPage = 1;
  bool _productHasMore = true;

  List<Inventory> _inventories = [];
  List<Inventory> get inventories => _inventories;
  bool get hasMoreInventories => _inventoryHasMore;

  List<Product> _products = [];
  List<Product> get products => _products;
  bool get hasMoreProducts => _productHasMore;

  Future<void> fetchInventories({bool refresh = false, String keyword = ''}) async {
    if (refresh || keyword != _keyword) {
      _keyword = keyword;
      _inventories = [];
      _inventoryPage = 1;
      _inventoryHasMore = true;
    }
    if (!_inventoryHasMore) return;
    final res = await _api.post(
      '/inventory/paging',
      token: _auth.token,
      body: {
        'page': _inventoryPage,
        'pageSize': 10,
        'keyword': _keyword,
      },
    );
    if (res.statusCode == 200) {
      final data = jsonDecode(res.body) as Map<String, dynamic>;
      final list = data['result']['content'] as List<dynamic>;
      final fetched = list.map((e) => Inventory.fromJson(e)).toList();
      _inventories.addAll(fetched);
      if (fetched.length < 10) _inventoryHasMore = false;
      _inventoryPage += 1;
      notifyListeners();
    }
  }

  Future<bool> updateProductChecked({
    required int inventoryProductId,
    required int checked,
    required int inventoryId,
    required int productId,
    required int stockNumber,
  }) async {
    final body = {
      'stockNumberChecked': checked,
      'inventoryId': inventoryId,
      'productId': productId,
      'stockNumber': stockNumber,
    };
    final res = await _api.put(
      '/inventory-product/$inventoryProductId',
      token: _auth.token,
      body: body,
    );
    if (res.statusCode == 200) {
      final idx = _products.indexWhere((p) => p.id == inventoryProductId);
      if (idx != -1) {
        _products[idx].stockNumberChecked = checked;
      }
      notifyListeners();
      return true;
    }
    return false;
  }

  Future<bool> createInventoryCheckRequest({required int inventoryId, required String note, required DateTime requestedDate}) async {
    if (_auth.userId == null || _auth.token == null) {
      debugPrint('[InventoryCheck] Missing auth info: userId=${_auth.userId}, token=${_auth.token}');
      return false;
    }

    final inv = _inventories.firstWhere(
      (e) => e.id == inventoryId,
      orElse: () {
        debugPrint('[InventoryCheck] Inventory ID $inventoryId not found locally, using fallback');
        return Inventory(id: inventoryId, name: '', address: '', totalRevenue: 0, managerId: 0);
      },
    );

    int managerId = inv.managerId;
    if (managerId == 0) {
      // Try fetch via API, else fallback to admin (id = 1)
      final fetched = await _api.fetchFirstManagerId(
          inventoryId: inventoryId, token: _auth.token!);
      managerId = fetched ?? 1;
      if (managerId == 1) {
        debugPrint('[InventoryCheck] Fallback to admin userId=1 for inventoryId=$inventoryId');
      }
    }

    final res = await _api.createInventoryCheckRequest(
      inventoryId: inventoryId,
      surveyorId: _auth.userId!,
      userId: managerId,
      note: note,
      requestedDate: requestedDate,
      token: _auth.token!,
    );

    if (res.statusCode == 200) {
      return true;
    } else {
      debugPrint('[InventoryCheck] API failed: status=${res.statusCode}, body=${res.body}');
      return false;
    }
  }

  Future<bool> saveCheckedHistory({
    required int inventoryId,
    required String note,
  }) async {
    if (_auth.userId == null || _auth.token == null) return false;
    try {
      final res = await _api.createCheckedHistory(
        inventoryId: inventoryId,
        userId: _auth.userId!,
        note: note,
        token: _auth.token!,
      );
      return res.statusCode == 200;
    } catch (e) {
      return false;
    }
  }

  Future<void> fetchProducts(int inventoryId, {bool refresh = false}) async {
    if (refresh) {
      _products = [];
      _productPage = 1;
      _productHasMore = true;
    }
    if (!_productHasMore) return;

    final res = await _api.post(
      '/inventory-product/paging',
      token: _auth.token,
      body: {
        'inventoryId': inventoryId,
        'page': _productPage,
        'active': true,
        'pageSize': 10,
      },
    );
    if (res.statusCode == 200) {
      final data = jsonDecode(res.body) as Map<String, dynamic>;
      final list = data['result']['content'] as List<dynamic>;
      final fetched = list.map((e) => Product.fromJson(e)).toList();
      if (_productPage == 1) {
        _products = fetched;
      } else {
        _products.addAll(fetched);
      }
      if (fetched.length < 10) _productHasMore = false;
      _productPage += 1;
      notifyListeners();
    }
  }
}
