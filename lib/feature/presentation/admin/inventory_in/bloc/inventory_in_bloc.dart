import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../ui/inventory_in_screen.dart';

class InventoryInBloc extends BaseBloc<InventoryInScreen> {
  String selectItem = '123 Cho Lon';
  TextEditingController controller = TextEditingController();

  @override
  void onInit() {}

  @override
  void onDispose() {}

  @override
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  void onSelect(String select) {
    selectItem = select;
    setState(() {});
  }
}
