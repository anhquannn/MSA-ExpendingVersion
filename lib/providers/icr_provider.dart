import 'dart:convert';

import 'package:flutter/foundation.dart';

import '../models/inventory_check_request.dart';
import '../services/api_service.dart';
import 'auth_provider.dart';

class IcrProvider extends ChangeNotifier {
  IcrProvider({required ApiService api, required AuthProvider auth})
      : _api = api,
        _auth = auth;

  final ApiService _api;
  final AuthProvider _auth;

  // Pagination
  int _page = 1;
  bool _hasMore = true;
  List<InventoryCheckRequest> _items = [];

  // Current filters
  String _keyword = '';
  int? _inventoryId;
  String? _status;

  List<InventoryCheckRequest> get items => _items;
  bool get hasMore => _hasMore;

  Future<void> fetch({
    bool refresh = false,
    String keyword = '',
    int? inventoryId,
    String? status,
  }) async {
    if (refresh || keyword != _keyword || inventoryId != _inventoryId || status != _status) {
      _keyword = keyword;
      _inventoryId = inventoryId;
      _status = status;
      _items = [];
      _page = 1;
      _hasMore = true;
    }
    if (!_hasMore) return;

    final body = {
      'page': _page,
      'pageSize': 10,
      'keyword': _keyword,
      if (_inventoryId != null) 'inventoryId': _inventoryId,
      if (_status != null && _status!.isNotEmpty) 'status': _status,
      'sortBy': 'icrId',
      'sortDirection': 'DESC',
    };
    final res = await _api.post('/inventory-check-requests/paging', token: _auth.token, body: body);
    if (res.statusCode == 200) {
      final data = jsonDecode(res.body) as Map<String, dynamic>;
      final list = data['result']['content'] as List<dynamic>;
      final fetched = list.map((e) => InventoryCheckRequest.fromJson(e)).toList();
      _items.addAll(fetched);
      if (fetched.length < 10) _hasMore = false;
      _page += 1;
      notifyListeners();
    }
  }

  Future<bool> add(InventoryCheckRequest req) async {
    int managerId = req.userId;
    if (managerId == 0 && _auth.token != null) {
      final fetched = await _api.fetchFirstManagerId(inventoryId: req.inventoryId, token: _auth.token!);
      managerId = fetched ?? 1; // fallback admin
    }
    final res = await _api.post('/inventory-check-requests', token: _auth.token, body: {
      'inventoryId': req.inventoryId,
      'surveyorId': req.surveyorId,
      'userId': managerId,
      'note': req.note,
      'requestedDate': '${req.requestedDate.year.toString().padLeft(4,'0')}-${req.requestedDate.month.toString().padLeft(2,'0')}-${req.requestedDate.day.toString().padLeft(2,'0')} ${req.requestedDate.hour.toString().padLeft(2,'0')}:${req.requestedDate.minute.toString().padLeft(2,'0')}:${req.requestedDate.second.toString().padLeft(2,'0')}',
      'status': req.status,
    });
    if (res.statusCode == 200) {
      final created = InventoryCheckRequest.fromJson(jsonDecode(res.body)['result']);
      _items.insert(0, created);
      notifyListeners();
      return true;
    }
    return false;
  }

  Future<bool> update(int id, Map<String, dynamic> payload) async {
    final res = await _api.put('/inventory-check-requests/$id', token: _auth.token, body: payload);
    if (res.statusCode == 200) {
      final idx = _items.indexWhere((e) => e.id == id);
      if (idx != -1) {
        _items[idx] = InventoryCheckRequest.fromJson(jsonDecode(res.body)['result']);
        notifyListeners();
      }
      return true;
    }
    return false;
  }

  Future<bool> delete(int id) async {
    final res = await _api.delete('/inventory-check-requests/$id', token: _auth.token);
    if (res.statusCode == 200) {
      _items.removeWhere((e) => e.id == id);
      notifyListeners();
      return true;
    }
    return false;
  }
}
