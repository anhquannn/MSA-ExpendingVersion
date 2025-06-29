class ProductCombinationFilterRequest {
  final int? productId1;
  final int? productId2;
  final String sortBy;
  final String sortDirection;
  final int page;
  final int pageSize;

  ProductCombinationFilterRequest({
    this.productId1,
    this.productId2,
    this.sortBy = "combinationId",
    this.sortDirection = "ASC",
    this.page = 1,
    this.pageSize = 10,
  });

  Map<String, dynamic> toJson() {
    return {
      if (productId1 != null) 'productId1': productId1,
      if (productId2 != null) 'productId2': productId2,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
      'page': page,
      'pageSize': pageSize,
    };
  }
}
