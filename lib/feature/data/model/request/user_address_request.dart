class UserAddressFilterRequest {
  final int userId;
  final String sortBy;
  final String sortDirection;
  final int page;
  final int pageSize;

  UserAddressFilterRequest({
    required this.userId,
    this.sortBy = "createdAt",
    this.sortDirection = "DESC",
    this.page = 1,
    this.pageSize = 10,
  });

  factory UserAddressFilterRequest.fromJson(Map<String, dynamic> json) {
    return UserAddressFilterRequest(
      userId: json['userId'] ?? 0,
      sortBy: json['sortBy'] ?? 'createdAt',
      sortDirection: json['sortDirection'] ?? 'DESC',
      page: json['page'] ?? 1,
      pageSize: json['pageSize'] ?? 10,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
      'page': page,
      'pageSize': pageSize,
    };
  }
}
