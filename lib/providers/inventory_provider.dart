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

  List<Inventory> _inventories = [];
  List<Inventory> get inventories => _inventories;

  List<Product> _products = [];
  List<Product> get products => _products;

  Future<void> fetchInventories() async {
    final res = await _api.post(
      '/inventory/paging',
      token: _auth.token,
      body: {
        'page': 1,
        'pageSize': 10,
      },
    );
    if (res.statusCode == 200) {
      final data = jsonDecode(res.body) as Map<String, dynamic>;
      final list = data['result']['content'] as List<dynamic>;
      _inventories = list.map((e) => Inventory.fromJson(e)).toList();
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
        notifyListeners();
      }
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
    // fetch via api
    final fetched = await _api.fetchFirstManagerId(inventoryId: inventoryId, token: _auth.token!);
    if (fetched == null) {
      debugPrint('[InventoryCheck] Cannot fetch managerId for inventoryId=$inventoryId');
      return false;
    }
    managerId = fetched;
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

  Future<void> fetchProducts(int inventoryId) async {
    _products = [];
    notifyListeners();

    final res = await _api.post(
      '/inventory-product/paging',
      token: _auth.token,
      body: {
        'inventoryId': inventoryId,
        'page': 1,
        'active': true,
        'pageSize': 10,
      },
    );
    if (res.statusCode == 200) {
      final data = jsonDecode(res.body) as Map<String, dynamic>;
      final list = data['result']['content'] as List<dynamic>;
      _products = list.map((e) => Product.fromJson(e)).toList();
      notifyListeners();
    }
  }
}
