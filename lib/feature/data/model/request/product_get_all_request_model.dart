class ProductGetAllRequest {
  final int? categoryId;
  final double? minPrice;
  final double? maxPrice;
  final String? keyword;
  final String? sortBy;
  final String? sortDirection;
  int? page = 1;
  int? pageSize = 10;
  int? branchId;

  ProductGetAllRequest({
    this.categoryId,
    this.minPrice,
    this.maxPrice,
    this.keyword,
    this.sortBy,
    this.sortDirection,
    this.page,
    this.pageSize,
    this.branchId
  });


  factory ProductGetAllRequest.fromJson(Map<String, dynamic> json) {
    return ProductGetAllRequest(
      categoryId: json['categoryId'],
      minPrice: (json['minPrice'] as num?)?.toDouble(),
      maxPrice: (json['maxPrice'] as num?)?.toDouble(),
      keyword: json['keyword'],
      sortBy: json['sortBy'],
      sortDirection: json['sortDirection'],
      page: json['page'],
      pageSize: json['pageSize'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'categoryId': categoryId,
      'minPrice': minPrice,
      'maxPrice': maxPrice,
      'keyword': keyword,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
      'page': page,
      'pageSize': pageSize,
      'branchId':branchId
    };
  }
}
