import '../../data/model/request/create_order_model_request.dart';
import '../../data/model/response/preview_order_response.dart';
import '../../data/model/response/revenue_order_response.dart';
import '../entities/order_model.dart';
import '../repositories/order_repository.dart';

class OrderUseCases {
  final CreateOrderUseCase createOrder;
  final GetOrderByIdUseCase getOrderById;
  final SearchOrdersByPhoneNumberUseCase searchOrdersByPhoneNumber;
  final GetOrdersByUserIdAndStatusUseCase getOrdersByUserIdAndStatus;
  final PreviewOrderUseCase previewOrder;
  final GetAllOrdersUseCase getAllOrders;
  final GetRevenueStatisticsUseCase getRevenueStatistics;
  final GetOrdersByBranchIdUseCase getOrdersByBranchId;
  final UpdateOrderUseCase updateOrder;
  final DeleteOrderUseCase deleteOrder;

  OrderUseCases({
    required this.createOrder,
    required this.getOrderById,
    required this.searchOrdersByPhoneNumber,
    required this.getOrdersByUserIdAndStatus,
    required this.previewOrder,
    required this.getAllOrders,
    required this.getRevenueStatistics,
    required this.getOrdersByBranchId,
    required this.updateOrder,
    required this.deleteOrder,
  });
}

class CreateOrderUseCase {
  final IOrderRepository repository;

  CreateOrderUseCase(this.repository);

  Future<OrderModel?> call(CreateOrderRequestModel request) {
    return repository.onCreateOrder(request);
  }
}

class GetOrderByIdUseCase {
  final IOrderRepository repository;

  GetOrderByIdUseCase(this.repository);

  Future<OrderModel?> call(int orderId) {
    return repository.onGetOrderById(orderId);
  }
}

class SearchOrdersByPhoneNumberUseCase {
  final IOrderRepository repository;

  SearchOrdersByPhoneNumberUseCase(this.repository);

  Future<List<OrderModel>?> call(String phoneNumber, int page, int pageSize) {
    return repository.onSearchOrdersByPhoneNumber(phoneNumber, page, pageSize);
  }
}

class GetOrdersByUserIdAndStatusUseCase {
  final IOrderRepository repository;

  GetOrdersByUserIdAndStatusUseCase(this.repository);

  Future<List<OrderModel>?> call(
    int userId,
    String status,
    int page,
    int pageSize,
  ) {
    return repository.onGetOrdersByUserIdAndStatus(
      userId,
      status,
      page,
      pageSize,
    );
  }
}

class PreviewOrderUseCase {
  final IOrderRepository repository;

  PreviewOrderUseCase(this.repository);

  Future<PreviewOrderResponse?> call(
    int userId,
    int cartId,
    List<String>? promoCodes,
  ) {
    return repository.onPreviewOrder(userId, cartId, promoCodes);
  }
}

class GetAllOrdersUseCase {
  final IOrderRepository repository;

  GetAllOrdersUseCase(this.repository);

  Future<List<OrderModel>?> call({
    int page = 0,
    int size = 10,
    String sortBy = 'orderDate',
    String sortDirection = 'desc',
  }) {
    return repository.onGetAllOrders(
      page: page,
      size: size,
      sortBy: sortBy,
      sortDirection: sortDirection,
    );
  }
}

class GetRevenueStatisticsUseCase {
  final IOrderRepository repository;

  GetRevenueStatisticsUseCase(this.repository);

  Future<RevenueOrderResponse?> call(
    int year,
    int month, {
    int? branchId,
    int? userId,
  }) {
    return repository.onGetRevenueStatistics(
      year,
      month,
      branchId: branchId,
      userId: userId,
    );
  }
}

class GetOrdersByBranchIdUseCase {
  final IOrderRepository repository;

  GetOrdersByBranchIdUseCase(this.repository);

  Future<List<OrderModel>?> call(
    int branchId, {
    int page = 0,
    int size = 10,
    String sortBy = 'orderDate',
    String sortDirection = 'desc',
  }) {
    return repository.onGetOrdersByBranchId(
      branchId,
      page: page,
      size: size,
      sortBy: sortBy,
      sortDirection: sortDirection,
    );
  }
}

class UpdateOrderUseCase {
  final IOrderRepository repository;

  UpdateOrderUseCase(this.repository);

  Future<OrderModel?> call(int orderId, Map<String, dynamic> updatedData) {
    return repository.onUpdateOrder(orderId, updatedData);
  }
}

class DeleteOrderUseCase {
  final IOrderRepository repository;

  DeleteOrderUseCase(this.repository);

  Future<bool> call(int orderId) {
    return repository.onDeleteOrder(orderId);
  }
}
