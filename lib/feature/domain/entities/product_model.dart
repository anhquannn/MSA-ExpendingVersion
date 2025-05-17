class ProductModel {
  final int? productId;
  final String? name;
  final String? images;
  final double? price;
  final double? currentPrice;
  final String? unit;
  final String? color;
  final String? specification;
  final String? description;
  final int? expiry;
  final int? totalRevenue;
  final ManufacturerModel? manufacturer;
  final CategoryModel? category;
  final dynamic inventoryProductResponses;
  final dynamic orderDetails;

  ProductModel({
     this.productId,
     this.name,
     this.images,
     this.price,
     this.currentPrice,
     this.unit,
     this.color,
     this.specification,
     this.description,
     this.expiry,
     this.totalRevenue,
     this.manufacturer,
     this.category,
    this.inventoryProductResponses,
    this.orderDetails,
  });

  factory ProductModel.fromJson(Map<String, dynamic> json) {
    return ProductModel(
      productId: int.tryParse(json['productId'].toString()),
      name: json['name']??'',
      images: json['images']??'',
      price: double.tryParse(json['price'].toString()),
      currentPrice: double.tryParse(json['currentPrice'].toString()),
      unit: json['unit']??'',
      color: json['color']??'',
      specification: json['specification']??'',
      description: json['description']??'',
      expiry: json['expiry']!=null?int.tryParse(json['expiry'].toString()):0,
      totalRevenue: json['totalRevenue']??'',
      manufacturer: json['manufacturer'] != null ? ManufacturerModel.fromJson(json['manufacturer']) : null,
      category: json['category']!=null ?CategoryModel.fromJson(json['category']):null,
      inventoryProductResponses: json['inventoryProductResponses'],
      orderDetails: json['orderDetails'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'productId': productId,
      'name': name,
      'images': images,
      'price': price,
      'currentPrice': currentPrice,
      'unit': unit,
      'color': color,
      'specification': specification,
      'description': description,
      'expiry': expiry,
      'totalRevenue': totalRevenue,
      'manufacturer': manufacturer?.toJson(),
      'category': category?.toJson(),
      'inventoryProductResponses': inventoryProductResponses,
      'orderDetails': orderDetails,
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

class CategoryModel {
  final int? categoryId;
  final String? name;
  final String? description;
  final int? parentCategory;

  CategoryModel({
     this.categoryId,
     this.name,
     this.description,
    this.parentCategory,
  });

  factory CategoryModel.fromJson(Map<String, dynamic> json) {
    return CategoryModel(
      categoryId: int.tryParse(json['categoryId'].toString()),
      name: json['name'],
      description: json['description'],
      parentCategory: int.tryParse(json['parentCategory'].toString()),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'categoryId': categoryId,
      'name': name,
      'description': description,
      'parentCategory': parentCategory,
    };
  }
}
