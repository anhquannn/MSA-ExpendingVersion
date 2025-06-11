import 'package:msa/feature/data/model/request/create_order_model_request.dart';
import 'package:msa/feature/data/model/response/preview_order_response.dart';
import 'package:msa/feature/data/model/response/revenue_order_response.dart';
import 'package:msa/feature/domain/entities/order_model.dart';

abstract class IOrderRepository {
  Future<OrderModel?> onCreateOrder(CreateOrderRequestModel orderData);
  Future<OrderModel?> onGetOrderById(int orderId);
  Future<List<OrderModel>?> onSearchOrdersByPhoneNumber(
    String phoneNumber,
    int page,
    int pageSize,
  );
  Future<List<OrderModel>?> onGetOrdersByUserIdAndStatus(
    int userId,
    String status,
    int page,
    int pageSize,
  );
  Future<PreviewOrderResponse?> onPreviewOrder(
    int userId,
    int cartId,
    List<String>? promoCodes,
  );
  Future<List<OrderModel>?> onGetAllOrders({
    int page,
    int size,
    String sortBy,
    String sortDirection,
  });
  Future<RevenueOrderResponse?> onGetRevenueStatistics(
    int year,
    int month, {
    int? branchId,
    int? userId,
  });
  Future<List<OrderModel>?> onGetOrdersByBranchId(
    int branchId, {
    int page,
    int size,
    String sortBy,
    String sortDirection,
  });
  Future<OrderModel?> onUpdateOrder(int orderId, Map<String, dynamic> updatedData);
  Future<bool> onDeleteOrder(int orderId);
}
