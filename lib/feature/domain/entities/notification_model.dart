import 'package:msa/feature/domain/entities/order_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';

class NotificationModel {
  final int? notificationId;
  final String? notificationType;
  final String? notificationDate;
  final String? message;
  final UserModel? user;
  final ProductModel? product;
  final OrderModel? order;
  final dynamic? inventory;
  final bool? read;

  NotificationModel({
     this.notificationId,
     this.notificationType,
     this.notificationDate,
     this.message,
     this.user,
    this.product,
    this.order,
    this.inventory,
     this.read,
  });

factory NotificationModel.fromJson(Map<String, dynamic> json) {
  return NotificationModel(
    notificationId: json['notificationId'],
    notificationType: json['notificationType'],
    notificationDate: json['notificationDate'],
    message: json['message'],
    user: json['user'] != null ? UserModel.fromJson(json['user']) : null,
    product: json['product'] != null ? ProductModel.fromJson(json['product']) : null,
    order: json['order'] != null ? OrderModel.fromJson(json['order']) : null,
    inventory: json['inventory'],
    read: json['read'],
  );
}


  Map<String, dynamic> toJson() {
    return {
      'notificationId': notificationId,
      'notificationType': notificationType,
      'notificationDate': notificationDate,
      'message': message,
      'user': user?.toJson(),
      'product': product,
      'order': order?.toJson(),
      'inventory': inventory,
      'read': read,
    };
  }
}

class InventoryModel {
  final int inventoryId;
  final String name;
  final String address;
  final String contact;
  final double totalRevenue;
  final BranchModel branch;

  InventoryModel({
    required this.inventoryId,
    required this.name,
    required this.address,
    required this.contact,
    required this.totalRevenue,
    required this.branch,
  });

  factory InventoryModel.fromJson(Map<String, dynamic> json) {
    return InventoryModel(
      inventoryId: json['inventoryId'],
      name: json['name'],
      address: json['address'],
      contact: json['contact'],
      totalRevenue: json['totalRevenue'],
      branch: BranchModel.fromJson(json['branch']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'inventoryId': inventoryId,
      'name': name,
      'address': address,
      'contact': contact,
      'totalRevenue': totalRevenue,
      'branch': branch.toJson(),
    };
  }
}