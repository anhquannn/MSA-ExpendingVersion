import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/request/supplier_filter_request.dart';
import 'package:msa/feature/domain/entities/product_model.dart';

class SupplierConnection {

  static getAll(SupplierFilterRequest request) async {
    final response = await HttpConnection.post<PaginatedResult<SupplierModel>>(
      getSupplier,
      body: request.toJson(),
      fromJsonT:
          (json) => PaginatedResult.fromJson(
            json,
            (itemJson) => SupplierModel.fromJson(itemJson),
          ),
    );
    return response.result?.content;
  }
}
