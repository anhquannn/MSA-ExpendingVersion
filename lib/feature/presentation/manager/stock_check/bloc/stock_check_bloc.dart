import 'package:flutter/cupertino.dart';

import '../../../../../core/config/base_bloc.dart';
import '../ui/stock_check_ui.dart';

class StockCheckBloc extends BaseBloc<StockCheckScreen> {
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
