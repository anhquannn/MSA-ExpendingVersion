import 'package:flutter/material.dart';

late BuildContext context;

void init(contextApp) {
  context = contextApp;
}

class AppSize {
  // --- Kích thước toàn màn hình ---
  static double width() => MediaQuery.of(context).size.width;
  static double height() => MediaQuery.of(context).size.height;

  // --- Tính theo phần trăm ---
  static double w(double percent) => width() * percent;

  static double h(double percent) => height() * percent;

  static double sp(context, double percent) => width() * percent;

  static double radius(context, double percent) => width() * percent;

  // --- Padding định mức ---
  static EdgeInsets paddingMini() => EdgeInsets.all(w(0.02));
  static EdgeInsets paddingMinimum() => EdgeInsets.all(w(0.04));
  static EdgeInsets paddingMaximum() => EdgeInsets.all(w(0.06));

  static EdgeInsets paddingHorizontalMini(context) =>
      EdgeInsets.symmetric(horizontal: w(0.02));

  static EdgeInsets paddingHorizontalMinimum(context) =>
      EdgeInsets.symmetric(horizontal: w(0.04));

  static EdgeInsets paddingHorizontalMaximum(context) =>
      EdgeInsets.symmetric(horizontal: w(0.06));

  static EdgeInsets paddingVerticalMini(context) =>
      EdgeInsets.symmetric(vertical: h(0.01));

  static EdgeInsets paddingVerticalMinimum(context) =>
      EdgeInsets.symmetric(vertical: h(0.02));

  static EdgeInsets paddingVerticalMaximum(context) =>
      EdgeInsets.symmetric(vertical: h(0.04));

  // --- Padding tùy chỉnh ---
  static EdgeInsets paddingOnly(
    context, {
    double left = 0,
    double top = 0,
    double right = 0,
    double bottom = 0,
  }) => EdgeInsets.only(
    left: w(left),
    top: h(top),
    right: w(right),
    bottom: h(bottom),
  );

  // --- Border Radius mặc định ---
  static const double radiusMini = 5;
  static const double radiusMinimum = 10;
  static const double radiusMaximum = 15;
  static const double radiusAppBar = 30;
  static const double radiusBottomBar = 30;

  // --- SafeArea Padding ---
  static EdgeInsets safePadding(context) => MediaQuery.of(context).padding;

  static double safePaddingTop(context) => safePadding(context).top;

  static double safePaddingBottom(context) => safePadding(context).bottom;

  static double appBarHeight(context) =>
      safePaddingTop(context) + kToolbarHeight;
}

List<Color> listColor = [
  Colors.red,
  Colors.green,
  Colors.blue,
  Colors.orange,
  Colors.purple,
  Colors.yellow,
  Colors.black,
  Colors.white,
  Colors.brown,
  Colors.cyan,
  Colors.teal,
  Colors.indigo,
  Colors.amber,
  Colors.lime,
  Colors.pink,
  Colors.deepOrange,
  Colors.deepPurple,
  Colors.lightBlue,
  Colors.lightGreen,
  Colors.blueGrey,
  Colors.grey,
  Colors.redAccent,
  Colors.greenAccent,
  Colors.blueAccent,
  Colors.orangeAccent,
  Colors.purpleAccent,
  Colors.yellowAccent,
  Colors.cyanAccent,
  Colors.tealAccent,
  Colors.indigoAccent,
  Colors.amberAccent,
  Colors.limeAccent,
  Colors.pinkAccent,
  Color(0xFF64B5F6), // Custom blue
  Color(0xFFFFC107), // Custom amber
  Color(0xFF2196F3), // Custom blue
  Color(0xFFFF5722), // Custom deep orange
  Color(0xFF9C27B0), // Custom purple
  Color(0xFF4CAF50), // Custom green
  Color(0xFF03A9F4), // Custom light blue
  Color(0xFFCDDC39), // Custom lime
  Color(0xFF009688), // Custom teal
  Color(0xFF3F51B5), // Custom indigo
  Color(0xFFE91E63), // Custom pink
  Color(0xFF8BC34A), // Custom light green
  Color(0xFF607D8B), // Custom blue-grey
  Color(0xFF795548), // Custom brown
  Color(0xFF607D8B), // Custom grey
];
