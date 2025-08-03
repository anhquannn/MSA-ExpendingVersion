import 'dart:io';

import 'package:device_info_plus/device_info_plus.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import 'package:msa/locator/locator.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import 'package:webview_flutter/webview_flutter.dart';

final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
    FlutterLocalNotificationsPlugin();

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  // await Supabase.initialize(url: urlSupabase, anonKey: anonKey);
  await requestNotificationPermission();
  await Firebase.initializeApp();
  await getFcmToken();
    await Supabase.initialize(
    url: 'https://lmtqwglnnbgsrxhelpxz.supabase.co',
    anonKey: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxtdHF3Z2xubmJnc3J4aGVscHh6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU3MTI1NzIsImV4cCI6MjA2MTI4ODU3Mn0.5D6-g10oFKgB5eJw7jbJPGtOsr2BmrYnm5pTpfjA_J0',
  );
  await initializeNotifications();
  FirebaseMessaging.instance.setForegroundNotificationPresentationOptions(
    alert: true,
    badge: true,
    sound: true,
  );

  if (defaultTargetPlatform == TargetPlatform.android) {
    WebViewPlatform.instance = WebViewPlatform.instance;
  }

  setupLocator();
  await Storage.readFromLocalStorage();
  final isLoggedIn = await Storage.checkLoginStatus();
  print('[main] isLoggedIn = $isLoggedIn');

  FirebaseMessaging.onMessage.listen((RemoteMessage message) {
     print('🔔 Nhận thông báo!');
  print('Full Message: ${message.toMap()}');
    print('🔔 Nhận thông báo!');
    print('Title: ${message.notification?.title}');
    print('Body: ${message.notification?.body}');
    showNotification(message);
  });
  FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);

  runApp(MyApp(isLoggedIn: isLoggedIn));
}

@pragma('vm:entry-point')
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await Firebase.initializeApp();
  print('🔕 [Background] Thông báo: ${message.notification?.title}');
  await showNotification(message);
}

Future<void> requestNotificationPermission() async {
  print('[Permission] 🟡 Hàm requestNotificationPermission được gọi');
  // if (Platform.isAndroid) {
    final androidInfo = await DeviceInfoPlugin().androidInfo;

    if (androidInfo.version.sdkInt >= 33) {
      var status = await Permission.notification.status;

      if (status.isDenied) {
        print('🔔 Đang yêu cầu quyền thông báo...');
        status = await Permission.notification.request();

        if (status.isGranted) {
          print('✅ Người dùng đã đồng ý quyền thông báo');
        } else if (status.isPermanentlyDenied) {
          print('❌ Người dùng từ chối vĩnh viễn quyền, mở settings...');
          await openAppSettings();
        } else {
          print('❌ Người dùng từ chối quyền thông báo');
        }
      } else if (status.isGranted) {
        print('✅ Quyền thông báo đã được cấp trước đó');
      }
    } else {
      print('Android < 13, không cần xin quyền thông báo');
    }
  // }
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
    );
  }
}

/// ✅ Hàm lấy FCM Token
Future<void> getFcmToken() async {
  try {
    final fcmToken = await FirebaseMessaging.instance.getToken();

    if (fcmToken != null) {
      print('[getFcmToken] ✅ FCM Token: $fcmToken');
      Storage.deviceId = fcmToken;
      await Storage.saveDeviceId(fcmToken);
    } else {
      print('[getFcmToken] ❌ FCM Token is null');
    }
  } catch (e, stack) {
    print('[getFcmToken] ❌ Exception: $e');
    print(stack);
  }
}

Future<void> initializeNotifications() async {
  const androidSettings = AndroidInitializationSettings('@mipmap/ic_launcher');

  const settings = InitializationSettings(android: androidSettings);

  await flutterLocalNotificationsPlugin.initialize(
    settings,
    onDidReceiveNotificationResponse: (NotificationResponse response) async {
      final payload = response.payload;
      print('🔗 Người dùng đã nhấn vào thông báo: $payload');
    },
    onDidReceiveBackgroundNotificationResponse:
        notificationTapBackground, // cần cho Android background
  );

  await FirebaseMessaging.instance.requestPermission();
}

@pragma('vm:entry-point') // Bắt buộc với background handler
void notificationTapBackground(NotificationResponse response) {
  final payload = response.payload;
  print('📦 [Background] Người dùng đã nhấn vào thông báo: $payload');
}

Future<void> showNotification(RemoteMessage message) async {
  final title = message.notification?.title ?? 'Thông báo';
  final body = message.notification?.body ?? '';
  final data = message.data;
  final type = data['type'] ?? '';

  final androidDetails = AndroidNotificationDetails(
    'default_channel_id1',
    'Thông báo',
    channelDescription: 'Kênh mặc định để hiển thị thông báo',
    importance: Importance.max,
    priority: Priority.high,
    // icon: iconName,
     icon: '@mipmap/ic_launcher',
    showWhen: true,
  );

  final notificationDetails = NotificationDetails(android: androidDetails);

  await flutterLocalNotificationsPlugin.show(
    message.hashCode,
    title,
    body,
    notificationDetails,
    payload: data.isNotEmpty ? data.toString() : null,
  );
}
