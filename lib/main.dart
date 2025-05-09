import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/presentation/logins/change_password/ui/change_password_screen.dart';
import 'package:msa/feature/presentation/logins/register/ui/register_screen.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

import 'feature/presentation/admin/add_category/ui/add_category_screen.dart';
import 'feature/presentation/admin/add_manufacturer/ui/add_manufacturer_screen.dart';
import 'feature/presentation/admin/add_promocode/ui/add_promocode_screen.dart';
import 'feature/presentation/admin/admin_notification/ui/admin_notification_screen.dart';
import 'feature/presentation/admin/create_branch/ui/create_branch_screen.dart';
import 'feature/presentation/admin/dashboard/ui/dashboard_screen.dart';
import 'feature/presentation/admin/inventory_in/ui/inventory_in_screen.dart';
import 'feature/presentation/admin/list_branch/ui/list_branch_screen.dart';
import 'feature/presentation/admin/order/ui/order_screen.dart';
import 'feature/presentation/admin/product/product_detail/ui/product_detail_screen.dart';
import 'feature/presentation/admin/product/ui/product_screen.dart';
import 'feature/presentation/admin/promocode/ui/promocode_screen.dart';
import 'feature/presentation/admin/user/ui/user_screen.dart';
import 'feature/presentation/customer/category_list/ui/category_list_screen.dart';
import 'feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'feature/presentation/customer/product_list/ui/product_list_screen.dart';
import 'feature/presentation/customer/promo_code_list/ui/promo_code_list_screen.dart';
import 'feature/presentation/logins/login/ui/login_screen.dart';
import 'feature/presentation/manager/dashboard_manager/dashboard_screen.dart';
import 'feature/presentation/manager/inventory_manager/inventory_manager.dart';
import 'feature/presentation/manager/notification_manager/notification.dart';
import 'feature/presentation/manager/order_manager/order_screen.dart';
import 'feature/presentation/manager/stock_check/ui/stock_check_ui.dart';
import 'widget/test.dart';
import 'feature/presentation/logins/forgot_pasword/ui/forgot_password_screen.dart';
import 'feature/presentation/logins/verify_otp/ui/verify_otp_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Supabase.initialize(url: urlSupabase, anonKey: anonKey);
  runApp(const MyApp());
}

final supabase = Supabase.instance.client;

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    init(context);
    return MaterialApp(debugShowCheckedModeBanner: false, home: HomeScreen());
  }
}
