import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../ui/dashboard_screen.dart';

class DashBoardBloc extends BaseBloc<DashBoardScreen> {
  final ScrollController _scrollController = ScrollController();

  @override
  void onInit() {}

  @override
  void onDispose() {
    _scrollController.dispose();
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
