import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class AppTheme {
  static ThemeData light() {
    const seed = Color(0xFF006E6D);
    final scheme = ColorScheme.fromSeed(seedColor: seed, brightness: Brightness.light);

    return ThemeData(
      colorScheme: scheme,
      useMaterial3: true,
      textTheme: GoogleFonts.interTextTheme(),
      inputDecorationTheme: InputDecorationTheme(
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
        ),
        filled: true,
        fillColor: scheme.surfaceVariant,
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 24),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        ),
      ),
      cardTheme: CardTheme(
        elevation: 1,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        surfaceTintColor: scheme.surfaceVariant,
      ),
    );
  }

  static ThemeData dark() {
    const seed = Color(0xFF006E6D);
    final scheme = ColorScheme.fromSeed(seedColor: seed, brightness: Brightness.dark);
    return light().copyWith(colorScheme: scheme);
  }
}
