import 'package:msa/core/utils/utility.dart';

class CategoryRequestFilterModel {
  final String? name;
  final int? parentId;
  final String sortBy;
  final SortDirection sortDirection;
  final int page;
  final int pageSize;

  CategoryRequestFilterModel({
    this.name,
    this.parentId,
    this.sortBy = 'name',
    this.sortDirection = SortDirection.asc,
    this.page = 1,
    this.pageSize = 10,
  });

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'parentId': parentId,
      'sortBy': sortBy,
      'sortDirection': sortDirection.value,
      'page': page,
      'pageSize': pageSize,
    };
  }

  factory CategoryRequestFilterModel.fromJson(Map<String, dynamic> json) {
    return CategoryRequestFilterModel(
      name: json['name'],
      parentId: json['parentId'],
      sortBy: json['sortBy'] ?? 'name',
      sortDirection: SortDirectionExtension.fromString(json['sortDirection'] ?? 'ASC'),
      page: json['page'] ?? 1,
      pageSize: json['pageSize'] ?? 10,
    );
  }
}
