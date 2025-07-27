import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';

import '../core/config/config.dart';
import '../core/config/constant.dart';

class CustomCustomerLead extends StatelessWidget {
  final String? text;
  const CustomCustomerLead({super.key, this.text = 'chức năng này'});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        spacing: 8,
        children: [
          Image.asset(loginRequest, height: 80, fit: BoxFit.contain),
          Text('Vui lòng đăng nhập để xem $text'),
          InkWell(
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const LoginScreen()),
              );
            },
            child: Text(
              'Đăng nhập',
              style: TextStyle(
                fontStyle: FontStyle.italic,
                decoration: TextDecoration.underline,
                color: Colors.blueAccent,
                fontSize: 15,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

checkLogin(BuildContext context) {
  if (Storage.isLogin == false) {
    showCustomDialog(
      context,
      AppSize.width(),
      AppSize.width(),
      'Thông báo',
      Text('Bạn chưa đăng nhập, vui lòng đăng nhập để tiếp tục thao tác'),
      true,
      true,
      Icon(Icons.warning, color: Colors.yellow),
      onClose: () {
        Navigator.pop(context);
      },
      onSubmit: () {
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const LoginScreen()),
        );
      },
    );
    return false;
  }
  return true;
}
