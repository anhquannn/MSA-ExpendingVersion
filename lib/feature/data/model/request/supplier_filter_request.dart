class SupplierFilterRequest {
  final String? keyword;

  // Sorting
  final String sortBy;
  final String sortDirection;

  // Pagination
  final int page;
  final int pageSize;

  SupplierFilterRequest({
    this.keyword,
    this.sortBy = "supplierId",
    this.sortDirection = "ASC",
    this.page = 1,
    this.pageSize = 10,
  });

  Map<String, dynamic> toJson() {
    return {
      'keyword': keyword,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
      'page': page,
      'pageSize': pageSize,
    };
  }

  factory SupplierFilterRequest.fromJson(Map<String, dynamic> json) {
    return SupplierFilterRequest(
      keyword: json['keyword'],
      sortBy: json['sortBy'] ?? "supplierId",
      sortDirection: json['sortDirection'] ?? "ASC",
      page: json['page'] ?? 1,
      pageSize: json['pageSize'] ?? 10,
    );
  }
}
