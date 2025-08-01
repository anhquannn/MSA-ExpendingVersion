import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/cancel_order_requesr_model.dart';
import 'package:msa/feature/data/model/request/return_order_filter_request.dart';
import 'package:msa/feature/data/model/response/cancel_order_response_model.dart';
import 'package:msa/feature/domain/entities/order_model.dart';

import '../model/request/return_order_request.dart';
import '../model/request/return_order_response_model.dart';
import '../model/response/return_order_response_model.dart'
    show ReturnOrderResult;

class ReturnOrderConnection {
  static createCanceledOrder(CancelOrderRequest request) async {
    final data = await HttpConnection.post(
      cancelOrder,
      body: request.toJson(),
      fromJsonT: (json) => ResponseCancelOrder.fromJson(json),
    );
    return data.isSuccess;
  }

  static createReturnOrder(ReturnOrderRequest model) async {
    final data = await HttpConnection.post(
      returnOrder,
      body: model.toJson(),
      fromJsonT: (json) => ReturnOrderModel.fromJson(json),
    );
    if (data.isSuccess) {
      return data;
    }
    return null;
  }

  static Future<List<ReturnOrderModel>> getAllReturnOrder(
    ReturnOrderFilterRequest model,
  ) async {
    final data = await HttpConnection.post<PaginatedResult<ReturnOrderModel>>(
      listReturnOrder,
      body: model.toJson(),
      fromJsonT:
          (json) => PaginatedResult.fromJson(
            json,
            (itemJson) => ReturnOrderModel.fromJson(itemJson),
          ),
      // (json) {
      //   return (json as List).map((e) => ReturnOrderModel.fromJson(e)).toList();
      // },
    );
    if (data.isSuccess) {
      return data.result?.content ?? [];
    }
    return [];
  }
}
