import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

enum SortDirection {
  asc,
  desc,
}

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
  final formatter = NumberFormat.currency(locale: 'vi_VN', symbol: '₫', decimalDigits: 0);
  return formatter.format(amount);
}

   void showLoginError(String message, BuildContext context) {
    if (context != null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text(message)));
    }
  }
