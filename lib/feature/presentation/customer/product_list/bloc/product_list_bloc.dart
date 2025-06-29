import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:rxdart/subjects.dart';

import '../ui/product_list_screen.dart';

class ProductListBloc extends BaseBloc<ProductListScreen> {
  final BehaviorSubject<ProductFilterResult> streamProducts =
      BehaviorSubject<ProductFilterResult>.seeded(ProductFilterResult());

      
  ProductFilterResult? listProducts;
  final streamProductModels = BehaviorSubject<ProductFilterResult>();

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
  void onReady()async {
    if(widget.category!=null){
      await onGetProduct();
    }
  }

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

    onGetProduct() async {
    try {
      ProductFilterRequest filter = ProductFilterRequest(
        page: 1,
        pageSize: 10,
        branchId: Storage.branchModelGlobal?.branchId,
      );

      ProductFilterResult product = await Repository.onFilterProducts(filter);
      listProducts = product;
      streamProductModels.add(product);
    } catch (e, stack) {
      print('❌ Lỗi khi lấy danh sách sản phẩm: $e');
      print('📛 Stacktrace: $stack');
      streamProductModels.add(ProductFilterResult());
    }
    setState(() {});
  }
}
