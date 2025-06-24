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
