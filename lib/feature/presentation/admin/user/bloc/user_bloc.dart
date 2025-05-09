import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../ui/user_screen.dart';

class UserBloc extends BaseBloc<UserScreen> {
  final TextEditingController searchController = TextEditingController();
  final FocusNode searchFocusNode = FocusNode();

  final List<String> mockItems = ['Admin', 'User', 'Customer'];
  String? selectedValue;

  @override
  void onInit() {}

  @override
  void onDispose() {
    searchController.dispose();
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
  void onUpdateProduct() {}
  void onDeleteProduct() {}
  void onDropdownChanged(String? value) {
    selectedValue = value;
    setState(() {});
  }
}
