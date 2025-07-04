import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import 'package:msa/locator/locator.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:webview_flutter/webview_flutter.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Supabase.initialize(url: urlSupabase, anonKey: anonKey);
  await Firebase.initializeApp();
  await getFcmToken();
  if (defaultTargetPlatform == TargetPlatform.android) {
    WebViewPlatform.instance = WebViewPlatform.instance;
  }

  setupLocator();
  await Storage.readFromLocalStorage();
  final isLoggedIn = await Storage.checkLoginStatus();
  // final isLoggedIn = true;
  print('[main] isLoggedIn = $isLoggedIn');

  runApp(MyApp(isLoggedIn: isLoggedIn));
}

final supabase = Supabase.instance.client;

class MyApp extends StatelessWidget {
  final bool isLoggedIn;

  const MyApp({super.key, required this.isLoggedIn});

  @override
  Widget build(BuildContext context) {
    init(context);
    HttpConnection.context = context;

    return MaterialApp(
      color: Colors.white,
      debugShowCheckedModeBanner: false,
      home: isLoggedIn ? HomeScreen() : LoginScreen(),
      // home: HomeScreen(),
    );
  }
}

Future<void> getFcmToken() async {
  try {
    final fcmToken = await FirebaseMessaging.instance.getToken();

    if (fcmToken != null) {
      print('[getFcmToken] ✅ FCM Token: $fcmToken');
      Storage.deviceId = fcmToken;
      await Storage.saveDeviceId(fcmToken);

      print('[getFcmToken] ✅ FCM Token2: ${Storage.deviceId}');
    } else {
      print('[getFcmToken] ❌ FCM Token is null');
    }
  } catch (e, stack) {
    print('[getFcmToken] ❌ Exception: $e');
    print(stack);
  }
}
//Notification, Sửa orderDetail, API cho mua lại Order
