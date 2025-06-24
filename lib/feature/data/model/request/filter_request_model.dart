class PromoCodeFilterRequest {
  final String name;
  final String code;
  final String type;
  final String status;
  final String fromDate; 
  final String toDate;
  final int campaignId;

  final String sortBy;
  final String sortDirection;

  final int page;
  final int pageSize;

  PromoCodeFilterRequest({
    required this.name,
    required this.code,
    required this.type,
    required this.status,
    required this.fromDate,
    required this.toDate,
    required this.campaignId,
    this.sortBy = 'createdAt',
    this.sortDirection = 'DESC',
    this.page = 1,
    this.pageSize = 10,
  });

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'code': code,
      'type': type,
      'status': status,
      'fromDate': fromDate,
      'toDate': toDate,
      'campaignId': campaignId,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
      'page': page,
      'pageSize': pageSize,
    };
  }
}
