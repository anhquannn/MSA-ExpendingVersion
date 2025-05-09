import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../ui/home_screen.dart';

class HomeScreenBloc extends BaseBloc<HomeScreen> {
  final PageController categoryController = PageController(initialPage: 0);
  final ValueNotifier<int> indexScreen = ValueNotifier(0);
  double currentPage = 0;
  @override
  void onInit() {}

  @override
  void onDispose() {
    categoryController.dispose();
    indexScreen.dispose();
  }

  @override
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  void onSetting() {}
  void onLogout() {}
  void onTapHone() {}
}
