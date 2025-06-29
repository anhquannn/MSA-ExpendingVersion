import 'package:msa/core/utils/utility.dart';

class OrderFilterRequest {
  final int? branchId;
  final int? userId;
  final OrderStatus? status; 
  final String? fromDate;
  final String? toDate;
  final String? phoneNumber;

  final String sortBy;
  final String sortDirection;

  final int page;
  final int pageSize;

  OrderFilterRequest({
    this.branchId,
    this.userId,
    this.status,
    this.fromDate,
    this.toDate,
    this.phoneNumber,
    this.sortBy = "orderDate",
    this.sortDirection = "DESC",
    this.page = 1,
    this.pageSize = 20,
  });

  Map<String, dynamic> toJson() {
    return {
      'branchId': branchId,
      'userId': userId,
      'status': status?.name,
      'fromDate': fromDate,
      'toDate': toDate,
      'phoneNumber': phoneNumber,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
      'page': page,
      'pageSize': pageSize,
    };
  }
}
