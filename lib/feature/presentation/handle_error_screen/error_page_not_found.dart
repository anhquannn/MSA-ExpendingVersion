import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/utils/prarse_color.dart';
import '../../../core/config/constant.dart';

class PageNotFoundScreen extends StatelessWidget {
  const PageNotFoundScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: toHexToColor(actionColor),
        leading: InkWell(
          onTap: () {
            Navigator.pop(context);
          },
          child: Icon(Icons.arrow_back_ios, color: Colors.white),
        ),
      ),
      backgroundColor: toHexToColor(actionColor),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Center(
            child: SizedBox(
              width: AppSize.w(0.85),
              child: Image.asset(imgError404),
            ),
          ),
          SizedBox(height: 20),
          Center(
            child: AutoSizeText(
              minFontSize: 18,
              maxLines: 24,
              'Không tìm thấy thông tin.',
              style: TextStyle(color: Colors.white),
            ),
          ),
          Center(
            child: AutoSizeText(
              minFontSize: 18,
              maxLines: 24,
              'Vui lòng thử lại sau.',
              style: TextStyle(color: Colors.white),
            ),
          ),
          SizedBox(height: AppSize.h(0.2)),
        ],
      ),
    );
  }
}
