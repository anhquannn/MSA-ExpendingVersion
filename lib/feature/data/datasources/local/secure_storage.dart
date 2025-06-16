import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SecureStorageService {
  
  final _storage = const FlutterSecureStorage();

  /// Lưu dữ liệu vào secure storage
  Future<bool> writeSecureData(String key, String value) async {
    try {
      await _storage.write(key: key, value: value);
      return true;
    } catch (e) {
      print('Error saving secure data: $e');
      return false;
    }
  }

  /// Đọc dữ liệu từ secure storage
  Future<String?> readSecureData(String key) async {
    try {
      final value = await _storage.read(key: key);
      return value;
    } catch (e) {
      print('Error reading secure data: $e');
      return null;
    }
  }
}
