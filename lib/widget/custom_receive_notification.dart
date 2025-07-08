import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:msa/firebase_options.dart';


@pragma('vm:entry-point')
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await Firebase.initializeApp();
  print('🔔 [Background] Message received: ${message.messageId}');
  print('🔔 Data: ${message.data}');
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);

  FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);

  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String? _token;
  String _message = 'Chưa nhận thông báo';

  @override
  void initState() {
    super.initState();
    initFCM();
  }

  Future<void> initFCM() async {
    // 🔑 Lấy FCM Token để đăng ký với backend
    _token = await FirebaseMessaging.instance.getToken();
    print('🔑 FCM Token: $_token');

    // 🔔 Lắng nghe thông báo khi app đang mở (foreground)
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      print('📲 [Foreground] Title: ${message.notification?.title}');
      print('📲 Body: ${message.notification?.body}');
      print('📲 Data: ${message.data}');
      setState(() {
        _message =
            'Title: ${message.notification?.title}\nBody: ${message.notification?.body}';
      });
    });

    // 🔁 Khi người dùng nhấn vào thông báo (app background)
    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      print('➡️ Opened from Notification');
      print('➡️ Data: ${message.data}');
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'FCM Demo',
      home: Scaffold(
        appBar: AppBar(title: Text('FCM Flutter')),
        body: Center(
          child: Text(
            _message,
            style: TextStyle(fontSize: 18),
            textAlign: TextAlign.center,
          ),
        ),
      ),
    );
  }
}
