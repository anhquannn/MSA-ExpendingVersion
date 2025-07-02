import 'package:msa/feature/data/model/response/product_filter_response.dart';

// Add an empty ProductImage factory if not already present

class ProductModel {
  final int? productId;
  final String? name;
  final String? image; // ảnh chính
  final double? price;
  final double? currentPrice;
  final String? unit;
  final String? color;
  final String? specification;
  final String? description;
  final int? expiry;
  final double? totalRevenue;

  final String? netWeight;
  final double? discountPercentage;
  final int? discountTriggerDays;
  final String? createdAt;

  final SupplierModel? supplier;
  final CategoryModel? category;
  final List<ProductImage>? productImages;

  final dynamic inventoryProductResponses;
  final dynamic orderDetails;
  final dynamic feedbackResponses;
  final dynamic userBehaviorResponses;
  final dynamic notificationResponses;
  final dynamic trendingProductResponses;

  ProductModel({
    this.productId,
    this.name,
    this.image,
    this.price,
    this.currentPrice,
    this.unit,
    this.color,
    this.specification,
    this.description,
    this.expiry,
    this.totalRevenue,
    this.netWeight,
    this.discountPercentage,
    this.discountTriggerDays,
    this.createdAt,
    this.supplier,
    this.category,
    this.productImages,
    this.inventoryProductResponses,
    this.orderDetails,
    this.feedbackResponses,
    this.userBehaviorResponses,
    this.notificationResponses,
    this.trendingProductResponses,
  });

  factory ProductModel.fromJson(Map<String, dynamic> json) {
    final imagesJson = json['productImageResponses'] as List<dynamic>?;
    final images = imagesJson?.map((e) => ProductImage.fromJson(e)).toList();

    // Lấy ảnh chính hoặc ảnh đầu tiên
    final mainImage = images?.firstWhere(
      (img) => img.isPrimary ?? false,
      orElse: () => images.isNotEmpty ? images.first : images[0],
    );

    return ProductModel(
      productId: int.tryParse(json['productId'].toString()),
      name: json['name'] ?? '',
      image: mainImage?.imageUrl,
      price:
          json['price'] is num ? double.tryParse(json['price'].toString()) : 0,
      currentPrice: (json['currentPrice'] as num?)?.toDouble(),
      unit: json['unit'] ?? '',
      color: json['color'] ?? '',
      specification: json['specification'] ?? '',
      description: json['description'] ?? '',
      expiry:
          json['expiry'] != null
              ? int.tryParse(json['expiry'].toString())
              : null,
      totalRevenue: (json['totalRevenue'] as num?)?.toDouble(),
      netWeight: json['netWeight'],
      discountPercentage: (json['discountPercentage'] as num?)?.toDouble(),
      discountTriggerDays: json['discountTriggerDays'],
      createdAt: json['createdAt'],
      supplier:
          json['supplier'] != null
              ? SupplierModel.fromJson(json['supplier'])
              : null,
      category:
          json['category'] != null
              ? CategoryModel.fromJson(json['category'])
              : null,
      productImages: images,
      inventoryProductResponses: json['inventoryProductResponses'],
      orderDetails: json['orderDetails'],
      feedbackResponses: json['feedbackResponses'],
      userBehaviorResponses: json['userBehaviorResponses'],
      notificationResponses: json['notificationResponses'],
      trendingProductResponses: json['trendingProductResponses'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'productId': productId,
      'name': name,
      'image': image,
      'price': price,
      'currentPrice': currentPrice,
      'unit': unit,
      'color': color,
      'specification': specification,
      'description': description,
      'expiry': expiry,
      'totalRevenue': totalRevenue,
      'netWeight': netWeight,
      'discountPercentage': discountPercentage,
      'discountTriggerDays': discountTriggerDays,
      'createdAt': createdAt,
      'supplier': supplier?.toJson(),
      'category': category?.toJson(),
      'productImageResponses': productImages?.map((e) => e.toJson()).toList(),
      'inventoryProductResponses': inventoryProductResponses,
      'orderDetails': orderDetails,
      'feedbackResponses': feedbackResponses,
      'userBehaviorResponses': userBehaviorResponses,
      'notificationResponses': notificationResponses,
      'trendingProductResponses': trendingProductResponses,
    };
  }
}

class ManufacturerModel {
  final int manufacturerId;
  final String name;
  final String address;
  final String contact;

  ManufacturerModel({
    required this.manufacturerId,
    required this.name,
    required this.address,
    required this.contact,
  });

  factory ManufacturerModel.fromJson(Map<String, dynamic> json) {
    return ManufacturerModel(
      manufacturerId: json['manufacturerId'],
      name: json['name'],
      address: json['address'],
      contact: json['contact'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'manufacturerId': manufacturerId,
      'name': name,
      'address': address,
      'contact': contact,
    };
  }
}

class SupplierModel {
  final int? supplierId;
  final String? name;
  final String? address;
  final String? contact;
  final String? image;
  bool? selected;

  SupplierModel({
    this.supplierId,
    this.name,
    this.address,
    this.contact,
    this.image,
    this.selected=false
  });

  factory SupplierModel.fromJson(Map<String, dynamic> json) {
    return SupplierModel(
      supplierId: json['supplierId'],
      name: json['name'],
      address: json['address'],
      contact: json['contact'],
      image: json['image'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'supplierId': supplierId,
      'name': name,
      'address': address,
      'contact': contact,
      'image': image,
    };
  }
}

class CategoryModel {
  final int? categoryId;
  final String? name;
  final String? description;
  final CategoryModel? parentCategory;
  bool selected;

  CategoryModel({
    this.categoryId,
    this.name,
    this.description,
    this.parentCategory,
    this.selected = false,
  });

  factory CategoryModel.fromJson(Map<String, dynamic> json) {
    return CategoryModel(
      categoryId: json['categoryId'],
      name: json['name'],
      description: json['description'],
      parentCategory:
          json['parentCategory'] != null
              ? CategoryModel.fromJson(json['parentCategory'])
              : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'categoryId': categoryId,
      'name': name,
      'description': description,
      'parentCategory': parentCategory?.toJson(),
    };
  }

  List<CategoryModel> parseCategoryList(dynamic jsonList) {
    if (jsonList == null) return [];
    return List<CategoryModel>.from(
      jsonList.map((item) => CategoryModel.fromJson(item)),
    );
  }
}

class ProductPageResponse {
  final List<ProductModel> content;
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

  ProductPageResponse({
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

  factory ProductPageResponse.fromJson(Map<String, dynamic> json) {
    return ProductPageResponse(
      content:
          (json['content'] as List<dynamic>?)
              ?.map((item) => ProductModel.fromJson(item))
              .toList() ??
          [],
      pageable: PageableModel.fromJson(json['pageable'] ?? {}),
      last: json['last'] ?? false,
      totalElements: json['totalElements'] ?? 0,
      totalPages: json['totalPages'] ?? 0,
      size: json['size'] ?? 0,
      number: json['number'] ?? 0,
      sort: SortModel.fromJson(json['sort'] ?? {}),
      numberOfElements: json['numberOfElements'] ?? 0,
      first: json['first'] ?? false,
      empty: json['empty'] ?? true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
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
}

class SortModel {
  final bool empty;
  final bool sorted;
  final bool unsorted;

  SortModel({
    required this.empty,
    required this.sorted,
    required this.unsorted,
  });

  factory SortModel.fromJson(Map<String, dynamic> json) {
    return SortModel(
      empty: json['empty'] ?? false,
      sorted: json['sorted'] ?? false,
      unsorted: json['unsorted'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {'empty': empty, 'sorted': sorted, 'unsorted': unsorted};
  }
}

class PageableModel {
  final int pageNumber;
  final int pageSize;
  final SortModel sort;
  final int offset;
  final bool paged;
  final bool unpaged;

  PageableModel({
    required this.pageNumber,
    required this.pageSize,
    required this.sort,
    required this.offset,
    required this.paged,
    required this.unpaged,
  });

  factory PageableModel.fromJson(Map<String, dynamic> json) {
    return PageableModel(
      pageNumber: json['pageNumber'] ?? 0,
      pageSize: json['pageSize'] ?? 0,
      sort: SortModel.fromJson(json['sort'] ?? {}),
      offset: json['offset'] ?? 0,
      paged: json['paged'] ?? false,
      unpaged: json['unpaged'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'pageNumber': pageNumber,
      'pageSize': pageSize,
      'sort': sort.toJson(),
      'offset': offset,
      'paged': paged,
      'unpaged': unpaged,
    };
  }
}
