import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:rxdart/subjects.dart';

import '../ui/product_list_screen.dart';

class ProductListBloc extends BaseBloc<ProductListScreen> {
  final BehaviorSubject<ProductFilterResult> streamProducts =
      BehaviorSubject<ProductFilterResult>.seeded(ProductFilterResult());

  @override
  String get contextKey => 'ProductListScreen';
  @override
  void onInit() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      streamProducts.add(ProductFilterResult());
    });
  }

  @override
  void onDispose() {
    streamProducts.close();
  }

  @override
  void onReady() {}

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);
}
