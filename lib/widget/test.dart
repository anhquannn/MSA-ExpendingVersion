import 'package:auto_size_text/auto_size_text.dart';
import 'package:curved_labeled_navigation_bar/curved_navigation_bar.dart';
import 'package:curved_labeled_navigation_bar/curved_navigation_bar_item.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';

import 'custom_widget.dart';
import 'reuseable_screen_hide_appbar.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Custom Scaffold Demo',
      theme: ThemeData(primarySwatch: Colors.green),
      home: const HomeScreens(),
      debugShowCheckedModeBanner: false,
    );
  }
}

class HomeScreens extends StatelessWidget {
  const HomeScreens({super.key});

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      title: Text('Đăng ký'),
      appBarLeading: const Icon(Icons.menu),
      appBarActions: [
        IconButton(onPressed: () {}, icon: const Icon(Icons.search)),
        IconButton(onPressed: () {}, icon: const Icon(Icons.notifications)),
      ],
      appBarGradient: true,
      bottomBarGradient: true,
      hideBottomBarOnScroll: true,
      // bottomBarItems: [
      //   BottomBarItem(
      //     icon: Icon(Icons.home),
      //     label: 'Trang chủTrang chủ',
      //     onTap: () => debugPrint(' Trang chủ'),
      //   ),
      //   BottomBarItem(
      //     icon: Icon(Icons.home),
      //     label: 'Trang chủ Trang chủ',
      //     onTap: () => debugPrint(' Trang chủ'),
      //   ),
      //   BottomBarItem(
      //     icon: Icon(Icons.home),
      //     label: 'Giỏ hàngTrang chủ',
      //     onTap: () => debugPrint(' Trang chủ'),
      //   ),
      //   BottomBarItem(
      //     icon: Icon(Icons.home),
      //     label: 'Tài khoảnTrang chủ',
      //     onTap: () => debugPrint(' Trang chủ'),
      //   ),
      // ],
      // bottomBarItemsCustom: [
      //   BottomBarItem(
      //     icon: Icon(Icons.home, color: Colors.white),
      //     label: 'Trang chủ',
      //     onTap: () {},
      //   ),
      //   BottomBarItem(
      //     icon: Icon(Icons.home, color: Colors.white),
      //     label: 'Trang chủ',
      //     onTap: () {},
      //   ),
      //   BottomBarItem(
      //     icon: Icon(Icons.home, color: Colors.white),
      //     label: 'Trang chủ',
      //     onTap: () {},
      //   ),
      //   BottomBarItem(
      //     icon: Icon(Icons.home, color: Colors.white),
      //     label: 'Trang chủ',
      //     onTap: () {},
      //   ),
      //   BottomBarItem(
      //     icon: Icon(Icons.home, color: Colors.white),
      //     label: 'Trang chủ',
      //     onTap: () {},
      //   ),
      //   BottomBarItem(
      //     icon: Icon(Icons.home, color: Colors.white),
      //     label: 'Trang chủ',
      //     // onTap: () {},
      //   ),
      // ],
      bodyBuilder: (controller) {
        return ListView.builder(
          controller: controller,
          itemCount: 50,
          padding: const EdgeInsets.all(16),
          itemBuilder: (context, index) {
            return Card(
              margin: const EdgeInsets.symmetric(vertical: 8),
              child: ListTile(
                leading: const Icon(Icons.shopping_bag),
                title: Text('Sản phẩm #$index'),
                subtitle: const Text('Mô tả sản phẩm'),
              ),
            );
          },
        );
      },
    );
  }
}
//
// void main() {
//   runApp(MyApp());
// }
//
// class MyApp extends StatelessWidget {
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       title: 'Curved Bottom Navigation Bar',
//       theme: ThemeData(primarySwatch: Colors.blue),
//       home: SimpleScreen(),
//     );
//   }
// }

class SimpleScreen extends StatefulWidget {
  @override
  _SimpleScreenState createState() => _SimpleScreenState();
}

class _SimpleScreenState extends State<SimpleScreen> {
  int _selectedIndex = 0;

  final List<Widget> _pages = [
    Center(
      child: Text(
        'Home Page',
        style: TextStyle(fontSize: 24, color: Colors.black),
      ),
    ),
    Center(child: Text('Search Page', style: TextStyle(fontSize: 24))),
    Center(child: Text('Profile Page', style: TextStyle(fontSize: 24))),
    Center(child: Text('Settings Page', style: TextStyle(fontSize: 24))),
    Center(child: Text('Notifications Page', style: TextStyle(fontSize: 24))),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Curved Bottom Navigation Bar')),
      body: _pages[_selectedIndex],
      bottomNavigationBar: CurvedNavigationBar(
        color: toHexToColor(appBarColor),
        backgroundColor: Colors.transparent,
        items: [
          CurvedNavigationBarItem(
            child: Icon(Icons.home_outlined, color: Colors.white),
            label: 'Trang chủ',
            labelStyle: TextStyle(color: Colors.white),
          ),
          CurvedNavigationBarItem(
            child: Icon(Icons.search, color: Colors.white),
          ),
          CurvedNavigationBarItem(child: Icon(Icons.chat_bubble_outline)),
          CurvedNavigationBarItem(child: Icon(Icons.newspaper)),
          CurvedNavigationBarItem(child: Icon(Icons.perm_identity)),
        ],
      ),
    );
  }
}
