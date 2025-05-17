import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';

import '../ui/promo_code_list_screen.dart';

class PromoCodeListBloc extends BaseBloc<PromoCodeListScreen> {
  @override
  String get contextKey => 'PromoCodeListBloc';

  @override
  void onInit() {}

  @override
  void onDispose() {
  }

  @override
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  void onCreatePromoCode() {}
}
