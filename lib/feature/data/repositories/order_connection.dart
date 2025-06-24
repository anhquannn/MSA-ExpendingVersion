// SỬA: order_repository_impl.dart

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
    final response = await HttpConnection.post<OrderModel>(
      createOrder,
      body: orderData.toJson(),
      fromJsonT: (json) => OrderModel.fromJson(json),
    );
    if (!response.isSuccess) messageError = response.message;
    return response.result;
  }

  @override
  Future<OrderModel?> onGetOrderById(int orderId) async {
    final response = await HttpConnection.get<OrderModel>(
      '$getOrderById$orderId',
      fromJsonT: (json) => OrderModel.fromJson(json),
    );
    if (!response.isSuccess) messageError = response.message;
    return response.result;
  }
  
  // Sửa lại hàm này để dùng PaginatedResult nếu API trả về cấu trúc phân trang
  // Tạm thời vẫn giữ logic cũ nhưng dùng cấu trúc mới
  Future<List<OrderModel>> _parseOrderList(String url) async {
      final response = await HttpConnection.get<List<OrderModel>>(
      url,
      fromJsonT: (json) => (json as List).map((i) => OrderModel.fromJson(i)).toList(),
    );
    if (!response.isSuccess) messageError = response.message;
    return response.result ?? [];
  }

  @override
  Future<List<OrderModel>> onSearchOrdersByPhoneNumber(String phoneNumber, int page, int pageSize) async {
    final url = HttpConnection.buildUrlWithQueryParams(
      '$searchOrdersByPhoneNumber$phoneNumber', 
      {'page': page, 'pageSize': pageSize}
    );
    return _parseOrderList(url);
  }

  @override
  Future<List<OrderModel>> onGetOrdersByUserIdAndStatus(int userId, String status, int page, int pageSize) async {
    final url = HttpConnection.buildUrlWithQueryParams(
      '$getOrdersByUserIdAndStatus$userId/status/$status',
      {'page': page, 'pageSize': pageSize}
    );
    return _parseOrderList(url);
  }

  @override
  Future<PreviewOrderResponse?> onPreviewOrder(int userId, int cartId, List<String>? promoCodes) async {
    final promoCodeParam = promoCodes != null && promoCodes.isNotEmpty ? promoCodes.join(",") : null;
    final url = HttpConnection.buildUrlWithQueryParams(
      previewOrder,
      {'userId': userId, 'cartId': cartId, 'promoCodes': promoCodeParam},
    );

    final response = await HttpConnection.get<PreviewOrderResponse>(
      url,
      fromJsonT: (json) => PreviewOrderResponse.fromJson(json),
    );
    if (!response.isSuccess) messageError = response.message;
    return response.result;
  }

  @override
  Future<List<OrderModel>> onGetAllOrders({int page = 0, int size = 10, String sortBy = 'orderDate', String sortDirection = 'desc'}) async {
     final url = HttpConnection.buildUrlWithQueryParams(
        getAllOrders, 
        {'page': page, 'size': size, 'sortBy': sortBy, 'sortDirection': sortDirection}
      );
    return _parseOrderList(url);
  }

  @override
  Future<RevenueOrderResponse?> onGetRevenueStatistics(int year, int month, {int? branchId, int? userId}) async {
    final url = HttpConnection.buildUrlWithQueryParams(
      getRevenueStatistics,
      {'year': year, 'month': month, 'branchId': branchId, 'userId': userId},
    );
    final response = await HttpConnection.get<RevenueOrderResponse>(
      url,
      fromJsonT: (json) => RevenueOrderResponse.fromJson(json),
    );
    if (!response.isSuccess) messageError = response.message;
    return response.result;
  }

  @override
  Future<List<OrderModel>> onGetOrdersByBranchId(int branchId, {int page = 0, int size = 10, String sortBy = 'orderDate', String sortDirection = 'desc'}) async {
    final url = HttpConnection.buildUrlWithQueryParams(
      '$getOrdersByBranchId$branchId',
      {'page': page, 'size': size, 'sortBy': sortBy, 'sortDirection': sortDirection}
    );
    return _parseOrderList(url);
  }

  @override
  Future<OrderModel?> onUpdateOrder(int orderId, Map<String, dynamic> updatedData) async {
    final response = await HttpConnection.put<OrderModel>(
      '$updateOrder$orderId',
      body: updatedData,
      fromJsonT: (json) => OrderModel.fromJson(json),
    );
    if (!response.isSuccess) messageError = response.message;
    return response.result;
  }

  @override
  Future<bool> onDeleteOrder(int orderId) async {
    final response = await HttpConnection.delete<dynamic>(
      '$deleteOrder$orderId',
      fromJsonT: (json) => json,
    );
    if (!response.isSuccess) messageError = response.message;
    return response.isSuccess;
  }
}