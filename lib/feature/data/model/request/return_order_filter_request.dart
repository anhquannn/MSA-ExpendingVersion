enum ReturnStatus {
  PENDING,
  APPROVED,
  REJECTED,
  SHIPPED,
  RECEIVED,
  REFUNDED,
  EXCHANGED,
}


class ReturnOrderFilterRequest {
  final ReturnStatus? status; 
  final int? userId;
  final int? orderId;
  final int? branchId;
  final DateTime? fromDate;
  final DateTime? toDate;
  final int page;
  final int size;
  final String sortBy;
  final String sortDirection;

  ReturnOrderFilterRequest({
    this.status,
    this.userId,
    this.orderId,
    this.branchId,
    this.fromDate,
    this.toDate,
    this.page = 0,
    this.size = 30,
    this.sortBy = 'createdAt',
    this.sortDirection = 'DESC',
  });

  Map<String, dynamic> toJson() {
    return {
      if (status != null) 'status': status.toString(),
      if (userId != null) 'userId': userId,
      if (orderId != null) 'orderId': orderId,
      if (branchId != null) 'branchId': branchId,
      if (fromDate != null) 'fromDate': _formatDate(fromDate!),
      if (toDate != null) 'toDate': _formatDate(toDate!),
      'page': page,
      'size': size,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
    };
  }

  String _formatDate(DateTime date) {
    return "${date.year.toString().padLeft(4, '0')}-"
           "${date.month.toString().padLeft(2, '0')}-"
           "${date.day.toString().padLeft(2, '0')} "
           "${date.hour.toString().padLeft(2, '0')}:"
           "${date.minute.toString().padLeft(2, '0')}:"
           "${date.second.toString().padLeft(2, '0')}";
  }
}
