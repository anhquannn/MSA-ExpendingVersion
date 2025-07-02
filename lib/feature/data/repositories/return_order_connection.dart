import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/cancel_order_requesr_model.dart';
import 'package:msa/feature/data/model/response/cancel_order_response_model.dart';

class ReturnOrderConnection {
  static createCanceledOrder(CancelOrderRequest request) async {
    final data = await HttpConnection.post(
      cancelOrder,
      body: request.toJson(),
      fromJsonT: (json) => ResponseCancelOrder.fromJson(json),
    );
    return data.isSuccess;
  }
}
