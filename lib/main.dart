import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/user_update_request.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

import 'feature/domain/usecase/user_use_case.dart';
import 'feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'locator/locator.dart';


void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Supabase.initialize(url: urlSupabase, anonKey: anonKey);
  await Firebase.initializeApp();
  getFcmToken();
  setupLocator();

  runApp(const MyApp());
}

final supabase = Supabase.instance.client;

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    init(context);
    HttpConnection.context = context;
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: HomeScreen(),
    );
  }
}

Future<void> getFcmToken() async {
  final fcmToken = await FirebaseMessaging.instance.getToken();
  if (fcmToken == null) {
  HttpConnection.deviceId = fcmToken??'';
  print('FCM Token: $fcmToken');
  }
}

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Profile Screen")),
      body: Center(child: const Text("Welcome to Profile Screen!")),
    );
  }
}

class TestScreen extends StatelessWidget {
  const TestScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final userUseCases = locator<UserUseCases>();
    return Scaffold(
      body: Center(
        child: InkWell(
          onTap: () {
            userUseCases.update(
              UserUpdateRequest(
                birthday: '',
                password: '123',
                phoneNumber: '0912345678',
                address: 'address',
                roles: [],
                fullName: 'fullName',
                email: 'minhquang03082003@gmail.com',
                userId: '4',
              ),
            );
          },
          child: Container(
            width: 60,
            height: 50,
            color: Colors.blue,
            child: Center(child: Text('test')),
          ),
        ),
      ),
    );
  }
}
