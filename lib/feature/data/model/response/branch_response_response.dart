import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';

class BranchResponseModel {
  final List<BranchModel> content;
  final PageableModel pageable;
  final bool last;
  final int totalElements;
  final int totalPages;
  final int size;
  final int number;
  final SortModel sort;
  final int numberOfElements;
  final bool first;
  final bool empty;

  BranchResponseModel({
    required this.content,
    required this.pageable,
    required this.last,
    required this.totalElements,
    required this.totalPages,
    required this.size,
    required this.number,
    required this.sort,
    required this.numberOfElements,
    required this.first,
    required this.empty,
  });

  factory BranchResponseModel.fromJson(Map<String, dynamic> json) {
    return BranchResponseModel(
      content: (json['content'] as List)
          .map((e) => BranchModel.fromJson(e))
          .toList(),
      pageable: PageableModel.fromJson(json['pageable'] ?? {}),
      last: json['last'] ?? false,
      totalElements: json['totalElements'] ?? 0,
      totalPages: json['totalPages'] ?? 0,
      size: json['size'] ?? 0,
      number: json['number'] ?? 0,
      sort: SortModel.fromJson(json['sort'] ?? {}),
      numberOfElements: json['numberOfElements'] ?? 0,
      first: json['first'] ?? false,
      empty: json['empty'] ?? false,
    );
  }

  Map<String, dynamic> toJson() => {
        'content': content.map((e) => e.toJson()).toList(),
        'pageable': pageable.toJson(),
        'last': last,
        'totalElements': totalElements,
        'totalPages': totalPages,
        'size': size,
        'number': number,
        'sort': sort.toJson(),
        'numberOfElements': numberOfElements,
        'first': first,
        'empty': empty,
      };
}
