import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../core/config/constant.dart';
import '../core/utils/prarse_color.dart';

Widget buildTabSection({
  required List<Widget> tabs,
  required TabBarView tabBarView,
  double tabBarHeight = 48,
  bool isScrollable = true,
}) {
  return DefaultTabController(
    length: tabs.length,
    child: Column(
      children: [
        Material(
          color: toHexToColor(backgroundColor),
          child: SizedBox(
            height: tabBarHeight,
            child: TabBar(
              isScrollable: isScrollable,
              dividerHeight: 0,
              labelPadding: EdgeInsets.symmetric(horizontal: 5),
              labelStyle: TextStyle(
                fontWeight: FontWeight.bold,
                color: Colors.blueGrey,
                fontSize: 14,
              ),
              unselectedLabelStyle: TextStyle(
                color: toHexToColor(secondaryTextColor),
                fontSize: 12,
              ),
              indicatorColor: Colors.blueGrey,
              tabs: tabs,
            ),
          ),
        ),
        Expanded(child: tabBarView),
      ],
    ),
  );
}
