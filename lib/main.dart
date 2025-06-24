import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/user_update_request.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import 'package:msa/feature/presentation/logins/register/ui/register_screen.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

import 'feature/data/datasources/local/starage.dart';
import 'feature/domain/usecase/user_use_case.dart';
import 'feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'locator/locator.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Supabase.initialize(url: urlSupabase, anonKey: anonKey);
  await Firebase.initializeApp();
  getFcmToken();
  setupLocator();
  await Storage.readFromLocalStorage();
  final isLoggedIn = await Storage.checkLoginStatus();
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
      debugShowCheckedModeBanner: false,
      home: isLoggedIn ? HomeScreen() : LoginScreen(),
      // home:RegisterScreen()
    );
  }
}

Future<void> getFcmToken() async {
  final fcmToken = await FirebaseMessaging.instance.getToken();
  if (fcmToken == null) {
    Storage.deviceId = fcmToken ?? '';
    Storage.saveDeviceId(fcmToken ?? '');
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

class ApiTestScreen extends StatefulWidget {
  const ApiTestScreen({super.key});

  @override
  State<ApiTestScreen> createState() => _ApiTestScreenState();
}

class _ApiTestScreenState extends State<ApiTestScreen> {
  final userUseCases = locator<UserUseCases>();

  String _result = '';

  Future<void> _testUpdateUser() async {
    setState(() {
      _result = 'Đang gửi request...';
    });

    try {
      final response = await userUseCases.update(
        UserUpdateRequest(
          userId: '4',
          fullName: 'Nguyen Van A',
          email: 'minhquang03082003@gmail.com',
          phoneNumber: '0909090909',
          address: 'HCM',
          password: '123456',
          birthday: '',
          roles: [],
        ),
      );

      setState(() {
        _result = '✅ Thành công: $response';
      });
    } catch (e) {
      print('########### $e');
      setState(() {
        _result = '❌ Lỗi: $e';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Test Update API')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            ElevatedButton(
              onPressed: _testUpdateUser,
              child: const Text('Gửi request'),
            ),
            const SizedBox(height: 20),
            Text(
              _result,
              style: TextStyle(
                color: _result.startsWith('✅') ? Colors.green : Colors.red,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
