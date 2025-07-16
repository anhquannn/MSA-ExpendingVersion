// file: models/request/product_filter_request.dart

import 'package:intl/intl.dart'; // Thêm vào pubspec.yaml: intl: ^0.19.0

class ProductFilterRequest {
  final int? branchId;
  final List<int?>? categoryId;
  final int? supplierId;
  final String? unit;
  final DateTime? fromDate;
  final DateTime? toDate;
  final String? netWeight;
  final double? minPrice;
  final double? maxPrice;
  final String? keyword;
  final String sortBy;
  final String? sortDirection;
  final int page;
  final int pageSize;

  ProductFilterRequest({
    this.branchId,
    this.categoryId,
    this.supplierId,
    this.unit,
    this.fromDate,
    this.toDate,
    this.netWeight,
    this.minPrice,
    this.maxPrice,
    this.keyword,
    this.sortBy = 'price',
    this.sortDirection ,
    this.page =
        0, // Backend thường xử lý trang từ 0, trong khi cURL gửi 1. Cần xác nhận lại.
    this.pageSize = 20,
  });

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = {};

    // Chỉ thêm các trường không null vào JSON
    if (branchId != null) data['branchId'] = branchId;
    if (categoryId != null && categoryId != [])
      data['categoryIds'] = categoryId;
    if (supplierId != null) data['supplierId'] = supplierId;
    if (unit != null) data['unit'] = unit;
    if (fromDate != null)
      data['fromDate'] = DateFormat('yyyy-MM-dd HH:mm:ss').format(fromDate!);
    if (toDate != null)
      data['toDate'] = DateFormat('yyyy-MM-dd HH:mm:ss').format(toDate!);
    if (netWeight != null) data['netWeight'] = netWeight;
    if (minPrice != null) data['minPrice'] = minPrice;
    if (maxPrice != null) data['maxPrice'] = maxPrice;
    if (keyword != null && keyword!.isNotEmpty) data['keyword'] = keyword;

    data['sortBy'] = sortBy;
    data['sortDirection'] = sortDirection;
    data['page'] = page;
    data['pageSize'] = pageSize;

    return data;
  }
}
