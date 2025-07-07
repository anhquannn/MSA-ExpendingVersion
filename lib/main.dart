// ========== main.dart sau khi dọn ==========
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'providers/auth_provider.dart';
import 'providers/inventory_provider.dart';
import 'screens/login_screen.dart';
import 'screens/inventories_screen.dart';
import 'screens/products_screen.dart';
import 'services/api_service.dart';
import 'theme/app_theme.dart';

void main() {
  final api = ApiService(baseUrl: 'http://192.168.2.48:1081/msa/api');
  runApp(MyApp(api: api));
}

class MyApp extends StatelessWidget {
  const MyApp({super.key, required this.api});
  final ApiService api;

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthProvider(api: api)),
        ChangeNotifierProxyProvider<AuthProvider, InventoryProvider>(
          create: (context) =>
              InventoryProvider(api: api, auth: context.read<AuthProvider>()),
          update: (_, auth, __) => InventoryProvider(api: api, auth: auth),
        ),
      ],
      child: MaterialApp(
        title: 'MSA Checker',
        debugShowCheckedModeBanner: false,
        theme: AppTheme.light(),
        darkTheme: AppTheme.dark(),
        initialRoute: '/',
        routes: {
          '/': (_) => const LoginScreen(),
          '/inventories': (_) => const InventoriesScreen(),
          '/products': (_) => const ProductsScreen(),
        },
      ),
    );
  }
}