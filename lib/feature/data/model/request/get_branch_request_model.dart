import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';

class BranchFilterRequest {
  final String? keyword;
  final int? userId;
  final String? sortBy;
  final String? sortDirection;
  final int page;
  final int pageSize;

  BranchFilterRequest({
    this.keyword,
    this.userId,
    this.sortBy = 'branchId',
    this.sortDirection = 'ASC',
    this.page = 1,
    this.pageSize = 20,
  });

  factory BranchFilterRequest.fromJson(Map<String, dynamic> json) {
    return BranchFilterRequest(
      keyword: json['keyword'] ?? '',
      userId: json['userId'],
      sortBy: json['sortBy'] ?? 'branchId',
      sortDirection: json['sortDirection'] ?? 'ASC',
      page: json['page'] ?? 1,
      pageSize: json['pageSize'] ?? 10,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'keyword': keyword,
      'userId': userId,
      'sortBy': sortBy,
      'sortDirection': sortDirection,
      'page': page,
      'pageSize': pageSize,
    };
  }
}

class BranchFilterResponse {
  final PaginatedResult<BranchModel> paginatedResult;

  BranchFilterResponse({required this.paginatedResult});

  factory BranchFilterResponse.fromJson(Map<String, dynamic> json) {
    return BranchFilterResponse(
      paginatedResult: PaginatedResult<BranchModel>.fromJson(
        json,
        (item) => BranchModel.fromJson(item),
      ),
    );
  }
}
