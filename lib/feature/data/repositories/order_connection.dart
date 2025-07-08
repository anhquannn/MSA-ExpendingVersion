// SỬA: order_repository_impl.dart

import 'dart:convert';

import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/create_order_model_request.dart';
import 'package:msa/feature/data/model/request/order_paging_request_model.dart';
import 'package:msa/feature/data/model/response/create_order_response_model.dart';
import 'package:msa/feature/data/model/response/create_shipment_response_model.dart';
import 'package:msa/feature/data/model/response/get_order_response_model.dart';
import 'package:msa/feature/data/model/response/order_detail_response_model.dart';
import 'package:msa/feature/data/model/response/revenue_order_response.dart';
import 'package:msa/feature/domain/entities/order_model.dart';
import 'package:msa/feature/domain/entities/order_preview_model.dart';
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
      fromJsonT:
          (json) => (json as List).map((i) => OrderModel.fromJson(i)).toList(),
    );
    if (!response.isSuccess) messageError = response.message;
    return response.result ?? [];
  }

  @override
  Future<List<OrderModel>> onSearchOrdersByPhoneNumber(
    String phoneNumber,
    int page,
    int pageSize,
  ) async {
    final url = HttpConnection.buildUrlWithQueryParams(
      '$searchOrdersByPhoneNumber$phoneNumber',
      {'page': page, 'pageSize': pageSize},
    );
    return _parseOrderList(url);
  }

  @override
  Future<List<OrderModel>> onGetOrdersByUserIdAndStatus(
    int userId,
    String status,
    int page,
    int pageSize,
  ) async {
    final url = HttpConnection.buildUrlWithQueryParams(
      '$getOrdersByUserIdAndStatus$userId/status/$status',
      {'page': page, 'pageSize': pageSize},
    );
    return _parseOrderList(url);
  }

  @override
  Future<List<OrderModel>> onGetAllOrders({
    int page = 0,
    int size = 10,
    String sortBy = 'orderDate',
    String sortDirection = 'desc',
  }) async {
    final url = HttpConnection.buildUrlWithQueryParams(getAllOrders, {
      'page': page,
      'size': size,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
    });
    return _parseOrderList(url);
  }

  @override
  Future<RevenueOrderResponse?> onGetRevenueStatistics(
    int year,
    int month, {
    int? branchId,
    int? userId,
  }) async {
    final url = HttpConnection.buildUrlWithQueryParams(getRevenueStatistics, {
      'year': year,
      'month': month,
      'branchId': branchId,
      'userId': userId,
    });
    final response = await HttpConnection.get<RevenueOrderResponse>(
      url,
      fromJsonT: (json) => RevenueOrderResponse.fromJson(json),
    );
    if (!response.isSuccess) messageError = response.message;
    return response.result;
  }

  @override
  Future<List<OrderModel>> onGetOrdersByBranchId(
    int branchId, {
    int page = 0,
    int size = 10,
    String sortBy = 'orderDate',
    String sortDirection = 'desc',
  }) async {
    final url = HttpConnection.buildUrlWithQueryParams(
      '$getOrdersByBranchId$branchId',
      {
        'page': page,
        'size': size,
        'sortBy': sortBy,
        'sortDirection': sortDirection,
      },
    );
    return _parseOrderList(url);
  }

  @override
  Future<OrderModel?> onUpdateOrder(
    int orderId,
    Map<String, dynamic> updatedData,
  ) async {
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

  static Future<OrderCreateResponseModel?> createOrderAPI(
    CreateOrderRequestModel orderData,
  ) async {
    print('📦 [CREATE ORDER BODY]: ${orderData.toJson()}');

    final response = await HttpConnection.post<OrderCreateResponseModel>(
      createOrder,
      body: orderData.toJson(),
      fromJsonT: (json) => OrderCreateResponseModel.fromJson(json),
    );

    if (!response.isSuccess) {
      print('❌ [CREATE ORDER ERROR]: ${response.message}');
      messageError = response.message;
    } else {
      print('✅ [CREATE ORDER SUCCESS]: ${response.result}');
    }
    final result = response.result;
    if (result != null) {
      print('📦 orderId: ${result.orderId}');
      print('📅 orderDate: ${result.orderDate}');
      print('💵 grandTotal: ${result.grandTotal}');
      print('📌 status: ${result.status}');
      print('🏬 branch: ${jsonEncode(result.branch?.toJson())}');
      print('🛒 cart: ${jsonEncode(result.cart?.toJson())}');
      print('👤 user: ${jsonEncode(result.user?.toJson())}');
      print('🚚 deliveryInfo: ${jsonEncode(result.deliveryInfo)}');
      print(
        '💳 payments: ${jsonEncode(result.payments?.map((e) => e.toJson()).toList())}',
      );
      print(
        '🏷️ promoCodes: ${jsonEncode(result.promoCodes?.map((e) => e.toJson()).toList())}',
      );
      print(
        '🎁 rewardPointTransactions: ${jsonEncode(result.rewardPointTransactions)}',
      );
      print('🏷️ promoCodeUsages: ${jsonEncode(result.promoCodeUsages)}');
    }

    return response.result;
  }

  static Future<OrderPreviewModel?> onGetPreviewOrder({
    List<String>? promoCodes,
    double? usePoints,
  }) async {
    String path =
        '$previewOrder'
        '?branchId=${Storage.branchModelGlobal?.branchId}'
        '&userAddressId=${Storage.addressModel?.userAddressId}'
        '&userId=${Storage.userModelGlobal?.userId}'
        '&cartId=${Storage.cartModelGlobal?.cartId}'
        '&usePoints=${usePoints.toString()}';

    if (promoCodes != null && promoCodes.isNotEmpty) {
      for (var code in promoCodes) {
        path += '&promoCodes=$code';
      }
    }

    // Gọi API
    final response = await HttpConnection.get<OrderPreviewModel>(
      path,
      fromJsonT: (json) => OrderPreviewModel.fromJson(json),
    );

    if (!response.isSuccess) messageError = response.message;
    return response.result;
  }

  static Future<CreateShipmentResponse?> createShipment({
    int? orderId,
    int? addressId,
    String? rateId,
  }) async {
    String path = 'shipment/order/$orderId/address/$addressId/rate/$rateId';
    final response = await HttpConnection.post<CreateShipmentResponse>(
      path,
      fromJsonT: (json) => CreateShipmentResponse.fromJson(json),
    );
    if (!response.isSuccess) messageError = response.message;
    return response.result;
  }

  static Future<OrderCreateResponseModel?> onUpdateOrderAPI(
    CreateOrderRequestModel model,
    int? orderId,
  ) async {
    final response = await HttpConnection.put<OrderCreateResponseModel>(
      '$updateOrder$orderId',
      body: model.toJson(),
      fromJsonT: (json) => OrderCreateResponseModel.fromJson(json),
    );
    if (!response.isSuccess) messageError = response.message;
    return response.result;
  }

  static Future<String?> onGetVnpayUrl(int? orderId) async {
    if (orderId == null) return null;

    final response = await HttpConnection.get<String>(
      '$getUrlVnPay$orderId',
      fromJsonT: (json) => json.toString(),
    );

    if (!response.isSuccess) {
      print('❌ Lỗi tạo VNPay URL: ${response.message}');
      messageError = response.message;
      return null;
    }

    print('✅ VNPay URL: ${response.result}');
    return response.result;
  }

  static Future<List<OrderResponse>> onGetListOrder(
    OrderFilterRequest model,
  ) async {
    final response = await HttpConnection.post<PaginatedResult<OrderResponse>>(
      getListOrder,
      body: model.toJson(),
      fromJsonT:
          (json) => PaginatedResult.fromJson(
            json,
            (itemJson) => OrderResponse.fromJson(itemJson),
          ),
    );

    return response.result?.content ?? [];
  }

  static Future<List<OrderDetailResponse>> onGetOrderDetail({
    int? orderId,
  }) async {
    final data = await HttpConnection.get<List<OrderDetailResponse>>(
      '$orderDetail$orderId',
      fromJsonT: (json) {
        return (json as List)
            .map((e) => OrderDetailResponse.fromJson(e))
            .toList();
      },
    );

    if (data.isSuccess) {
      return data.result ?? [];
    }
    return [];
  }

  static buyAgain({
    required int orderId,
    required int userAddressId,
    List<String>? promoCodes,
    double? usePoints
  }) async {
    // Tạo query string
    final queryParams = <String>[
      'userAddressId=$userAddressId',
        'usePoints=${usePoints.toString()}',
      if (promoCodes != null && promoCodes.isNotEmpty)
        ...promoCodes.map((code) => 'promoCodes=$code'),
    ];

    final queryString = queryParams.join('&');
    final String path = 'order/$orderId/buy-again?$queryString';

    final response = await HttpConnection.post(
      path,
      fromJsonT: (json) => OrderModel.fromJson(json),
    );

    if (response.isSuccess) {
      return response.result ?? OrderModel();
    }
    return null;
  }

  static Future<OrderPreviewModel?> onGetPreviewOrderAgain({
    required int orderOldId,
    required int userAddressId,
    List<String>? promoCodes,
    double? usePoints
  }) async {
    final queryParameters = <String, dynamic>{
      'userAddressId': userAddressId.toString(),
      'usePoints': usePoints.toString(),
    };
    if (promoCodes != null && promoCodes.isNotEmpty) {
      queryParameters.addAll({
        for (var code in promoCodes) 'promoCodes': code,
      });
    }

    final uri = Uri(
      path: 'order/$orderOldId/preview-buy-again',
      queryParameters: queryParameters,
    );

    final response = await HttpConnection.get<OrderPreviewModel>(
      uri.toString(),
      fromJsonT: (json) => OrderPreviewModel.fromJson(json),
    );

    if (!response.isSuccess) messageError = response.message;
    return response.result;
  }
}
