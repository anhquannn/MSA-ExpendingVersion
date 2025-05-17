import 'dart:math';

import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';


Widget buildIconWrapWithLabel({
  required List<Widget> icons,
  required List<String> labels,
}) {
  // assert(icons.length == labels!.length, 'Icons and labels must be same length');
  return Wrap(
    spacing: 10,
    runSpacing: 10,
    children: List.generate(icons.length, (index) {
      return Container(
        width: min(70, AppSize.w(0.2)),
        decoration: BoxDecoration(borderRadius: BorderRadius.circular(10)),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                border: Border.all(color: Colors.black),
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
              ),
              child: icons[index],
            ),
            const SizedBox(height: 4),
            Text(
              maxLines: 1,
              // softWrap: true,
              overflow: TextOverflow.ellipsis,
              labels[index],
              style: const TextStyle(fontSize: 12),
            ),
          ],
        ),
      );
    }),
  );
}

Widget itemIconFunction(IconData icon, VoidCallback onTap) {
  return InkWell(onTap: onTap, child: Icon(icon, color: Colors.black));
}
