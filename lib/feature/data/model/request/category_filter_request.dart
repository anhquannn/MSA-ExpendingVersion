class CategoryFilterRequest {
  final String? name;
  final int? parentId;
  final String? sortBy;
  final String? sortDirection;
  final int? page;
  final int? pageSize;

  CategoryFilterRequest({
     this.name,
    this.parentId,
    this.sortBy = "name",
    this.sortDirection,
    this.page = 1,
    this.pageSize = 40,
  });

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'parentId': parentId,
      'sortBy': sortBy,
      'page': page,
      'pageSize': pageSize,
    };
  }

  factory CategoryFilterRequest.fromJson(Map<String, dynamic> json) {
    return CategoryFilterRequest(
      name: json['name'] ?? '',
      parentId: json['parentId'],
      sortBy: json['sortBy'] ?? 'name',
      sortDirection: json['sortDirection'] ?? 'ASC',
      page: json['page'] ?? 1,
      pageSize: json['pageSize'] ?? 10,
    );
  }
}
