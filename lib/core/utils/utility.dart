import 'dart:async';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

enum SortDirection { asc, desc }

extension SortDirectionExtension on SortDirection {
  String get value {
    switch (this) {
      case SortDirection.asc:
        return 'ASC';
      case SortDirection.desc:
        return 'DESC';
    }
  }

  static SortDirection fromString(String value) {
    switch (value.toUpperCase()) {
      case 'ASC':
        return SortDirection.asc;
      case 'DESC':
        return SortDirection.desc;
      default:
        throw ArgumentError('Unknown sort direction: $value');
    }
  }
}

List<String> stringToList(String? input) {
  if (input == null || input.trim().isEmpty) return [];
  return input.split(',').map((e) => e.trim()).toList();
}

String formatCurrency(double amount) {
  final formatter = NumberFormat.currency(
    locale: 'vi_VN',
    symbol: '₫',
    decimalDigits: 0,
  );
  return formatter.format(amount);
}

void showLoginError(String message, BuildContext context) {
  if (context != null) {
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(SnackBar(content: Text(message)));
  }
}

String formatDateTime(DateTime dateTime) {
  return "${dateTime.year.toString().padLeft(4, '0')}-"
      "${dateTime.month.toString().padLeft(2, '0')}-"
      "${dateTime.day.toString().padLeft(2, '0')} "
      "${dateTime.hour.toString().padLeft(2, '0')}:"
      "${dateTime.minute.toString().padLeft(2, '0')}:"
      "${dateTime.second.toString().padLeft(2, '0')}";
}

List<Color> generatePastelGradientForWhiteText() {
  final random = Random();

  Color pastelWithGoodContrast() {
    final h = random.nextDouble() * 360;
    final s = random.nextDouble() * 0.4 + 0.3; // Saturation: 30%–70%
    final l = random.nextDouble() * 0.3 + 0.3; // Lightness: 30%–60%

    return HSLColor.fromAHSL(1.0, h, s, l).toColor();
  }

  return [pastelWithGoodContrast(), pastelWithGoodContrast()];
}

String formatCurrencyVN(double amount) {
  final formatter = NumberFormat.currency(locale: 'vi_VN', symbol: '₫');
  return formatter.format(amount);
}
class Debouncer {
  final int milliseconds;
  Timer? _timer;

  Debouncer({this.milliseconds = 500});

  void run(VoidCallback action) {
    _timer?.cancel();
    _timer = Timer(Duration(milliseconds: milliseconds), action);
  }

  void dispose() {
    _timer?.cancel();
  }
}

String formatDateString(String dateString) {
  try {
    final dateTime = DateTime.parse(dateString);
    return DateFormat('dd/MM/yyyy').format(dateTime);
  } catch (e) {
    print('❌ Lỗi định dạng ngày: $e');
    return '';
  }
}
customPrint(String data){
   print('[✅ DEBUG] ${data.toString()}');
}
enum OrderStatus {
  pending,
  paying,
  paid,
  delivering,
  shipped,
  cancelling,
  cancelled,
  completed,
  failed,
}
extension OrderStatusExtension on OrderStatus {
  String get name => toString().split('.').last.toUpperCase();

  String get description {
    switch (this) {
      case OrderStatus.pending:
        return 'Chờ xử lý';
      case OrderStatus.paying:
        return 'Đang thanh toán';
      case OrderStatus.paid:
        return 'Đã thanh toán';
      case OrderStatus.delivering:
        return 'Đang vận chuyển';
      case OrderStatus.shipped:
        return 'Giao hàng thành công';
      case OrderStatus.cancelling:
        return 'Đang hủy đơn hàng';
      case OrderStatus.cancelled:
        return 'Đã hủy';
      case OrderStatus.completed:
        return 'Hoàn thành';
      case OrderStatus.failed:
        return 'Thất bại';
    }
  }

  static OrderStatus? fromString(String status) {
    try {
      return OrderStatus.values.firstWhere(
        (e) => e.name == status.toUpperCase(),
      );
    } catch (_) {
      return null;
    }
  }
}
enum PromoCodeStatusEnum {
  active,
  inactive,
  expired,
}

extension PromoCodeStatusExtension on PromoCodeStatusEnum {
  String get name => toString().split('.').last.toUpperCase();

  String get description {
    switch (this) {
      case PromoCodeStatusEnum.active:
        return 'Kích hoạt';
      case PromoCodeStatusEnum.inactive:
        return 'Chưa kích hoạt';
      case PromoCodeStatusEnum.expired:
        return 'Đã hết hạn';
    }
  }

  static PromoCodeStatusEnum? fromString(String status) {
    try {
      return PromoCodeStatusEnum.values.firstWhere(
        (e) => e.name == status.toUpperCase(),
      );
    } catch (_) {
      return null;
    }
  }
}
