import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/create_order_model_request.dart';
import 'package:msa/feature/data/model/response/preview_order_response.dart';
import 'package:msa/feature/data/model/response/revenue_order_response.dart';
import 'package:msa/feature/domain/entities/order_model.dart';
import 'package:msa/feature/domain/repositories/order_repository.dart';

class OrderRepositoryImpl extends IOrderRepository {
  @override
  Future<OrderModel?> onCreateOrder(CreateOrderRequestModel orderData) async {
    final data = await HttpConnection.post(
      createOrder,
      body: orderData.toJson(),
    );
    if (data.isSuccess) {
      return OrderModel.fromJson(data.data);
    }
    messageError = data.message;
    return null;
  }

  @override
  Future<OrderModel?> onGetOrderById(int orderId) async {
    final data = await HttpConnection.get('$getOrderById$orderId');
    if (data.isSuccess) {
      return OrderModel.fromJson(data.data);
    }
    messageError = data.message;
    return null;
  }

  @override
  Future<List<OrderModel>?> onSearchOrdersByPhoneNumber(
    String phoneNumber,
    int page,
    int pageSize,
  ) async {
    final String url =
        '$searchOrdersByPhoneNumber=$phoneNumber&page=$page&pageSize=$pageSize';
    final data = await HttpConnection.get(url);
    if (data.isSuccess) {
      return List<OrderModel>.from(
        data.data.map((json) => OrderModel.fromJson(json)),
      );
    }
    messageError = data.message;
    return [];
  }

  @override
  Future<List<OrderModel>?> onGetOrdersByUserIdAndStatus(
    int userId,
    String status,
    int page,
    int pageSize,
  ) async {
    final String url =
        '$getOrdersByUserIdAndStatus$userId/status/$status?page=$page&pageSize=$pageSize';
    final data = await HttpConnection.get(url);
    if (data.isSuccess) {
      return List<OrderModel>.from(
        data.data.map((json) => OrderModel.fromJson(json)),
      );
    }
    messageError = data.message;
    return [];
  }

  @override
  Future<PreviewOrderResponse?> onPreviewOrder(
    int userId,
    int cartId,
    List<String>? promoCodes,
  ) async {
    final String promoCodeParam =
        promoCodes != null && promoCodes.isNotEmpty
            ? '&promoCodes=${promoCodes.join(",")}'
            : '';
    final String url =
        '$previewOrder=$userId&cartId=$cartId$promoCodeParam';
    final data = await HttpConnection.get(url);
    if (data.isSuccess) {
      return PreviewOrderResponse.fromJson(data.data);
    }
    messageError = data.message;
    return null;
  }

  @override
  Future<List<OrderModel>> onGetAllOrders({
    int page = 0,
    int size = 10,
    String sortBy = 'orderDate',
    String sortDirection = 'desc',
  }) async {
    final String url =
        '$getAllOrders=$page&size=$size&sortBy=$sortBy&sortDirection=$sortDirection';
    final data = await HttpConnection.get(url);
    if (data.isSuccess) {
      return List<OrderModel>.from(
        data.data.map((json) => OrderModel.fromJson(json)),
      );
    }
    messageError = data.message;
    return [];
  }

  @override
  Future<RevenueOrderResponse?> onGetRevenueStatistics(
    int year,
    int month, {
    int? branchId,
    int? userId,
  }) async {
    String url = '$getRevenueStatistics=$year&month=$month';
    if (branchId != null) url += '&branchId=$branchId';
    if (userId != null) url += '&userId=$userId';

    final data = await HttpConnection.get(url);
    if (data.isSuccess) {
      return RevenueOrderResponse.fromJson(data.data);
    }
    messageError = data.message;
    return null;
  }

  @override
  Future<List<OrderModel>> onGetOrdersByBranchId(
    int branchId, {
    int page = 0,
    int size = 10,
    String sortBy = 'orderDate',
    String sortDirection = 'desc',
  }) async {
    final String url =
        '$getOrdersByBranchId$branchId?page=$page&size=$size&sortBy=$sortBy&sortDirection=$sortDirection';
    final data = await HttpConnection.get(url);
    if (data.isSuccess) {
      return List<OrderModel>.from(
        data.data.map((json) => OrderModel.fromJson(json)),
      );
    }
    messageError = data.message;
    return [];
  }

  @override
  Future<OrderModel?> onUpdateOrder(
    int orderId,
    Map<String, dynamic> updatedData,
  ) async {
    final data = await HttpConnection.put(
      '$updateOrder$orderId',
      body: updatedData,
    );
    if (data.isSuccess) {
      return OrderModel.fromJson(data.data);
    }
    messageError = data.message;
    return null;
  }

  @override
  Future<bool> onDeleteOrder(int orderId) async {
    final data = await HttpConnection.delete('$deleteOrder$orderId');
    if (data.isSuccess) return true;
    messageError = data.message;
    return false;
  }
}
