import 'package:msa/feature/domain/entities/product_model.dart';

class FilterProductResponse {
  final List<SupplierModel> suppliers;
  final List<String> netWeights;
  final List<String> units;

  FilterProductResponse({
    required this.suppliers,
    required this.netWeights,
    required this.units,
  });

  factory FilterProductResponse.fromJson(Map<String, dynamic> json) {
    return FilterProductResponse(
      suppliers: (json['suppliers'] as List)
          .map((e) => SupplierModel.fromJson(e))
          .toList(),
      netWeights: List<String>.from(json['netWeights']),
      units: List<String>.from(json['units']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'suppliers': suppliers.map((e) => e.toJson()).toList(),
      'netWeights': netWeights,
      'units': units,
    };
  }
}
