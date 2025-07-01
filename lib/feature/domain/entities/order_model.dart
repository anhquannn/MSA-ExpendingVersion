import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/promo_code_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';

class OrderModel {
  int? orderId;
  String? orderDate;
  double? grandTotal;
  double? totalCost;
  String? status;
  double? discount;
  BranchModel? branch;
  CartModel? cart;
  UserModel? user;
  DeliveryInfoModel? deliveryInfo;
  List<ReturnOrderModel>? returnOrders;
  List<PaymentModel>? payments;
  List<PromoCodeModel>? promoCodes;
  List<OrderDetailModel>? orderDetails;

  OrderModel({
    this.orderId,
    this.orderDate,
    this.grandTotal,
    this.totalCost,
    this.status,
    this.discount,
    this.branch,
    this.cart,
    this.user,
    this.deliveryInfo,
    this.returnOrders,
    this.payments,
    this.promoCodes,
    this.orderDetails,
  });

  factory OrderModel.fromJson(Map<String, dynamic> json) => OrderModel(
    orderId: json['orderId'] as int?,
    orderDate: json['orderDate'],
    grandTotal: (json['grandTotal'] as num?)?.toDouble(),
    totalCost: (json['totalCost'] as num?)?.toDouble(),
    status: json['status'] as String?,
    discount: (json['discount'] as num?)?.toDouble(),
    branch:
        json['branch'] != null ? BranchModel.fromJson(json['branch']) : null,
    cart: json['cart'] != null ? CartModel.fromJson(json['cart']) : null,
    user: json['user'] != null ? UserModel.fromJson(json['user']) : null,
    deliveryInfo:
        json['deliveryInfo'] != null
            ? DeliveryInfoModel.fromJson(json['deliveryInfo'])
            : null,
    returnOrders:
        json['returnOrders'] != null
            ? List<ReturnOrderModel>.from(
              (json['returnOrders'] as List).map(
                (x) => ReturnOrderModel.fromJson(x),
              ),
            )
            : null,
    payments:
        json['payments'] != null
            ? List<PaymentModel>.from(
              (json['payments'] as List).map((x) => PaymentModel.fromJson(x)),
            )
            : null,
    promoCodes:
        json['promoCodes'] != null
            ? (json['promoCodes'] as List)
                .map((e) => PromoCodeModel.fromJson(e))
                .toList()
            : null,

    orderDetails:
        json['orderDetails'] != null
            ? List<OrderDetailModel>.from(
              (json['orderDetails'] as List).map(
                (x) => OrderDetailModel.fromJson(x),
              ),
            )
            : null,
  );

  Map<String, dynamic> toJson() => {
    'orderId': orderId,
    'orderDate': orderDate,
    'grandTotal': grandTotal,
    'totalCost': totalCost,
    'status': status,
    'discount': discount,
    'branch': branch?.toJson(),
    'cart': cart?.toJson(),
    'user': user?.toJson(),
    'deliveryInfo': deliveryInfo?.toJson(),
    'returnOrders': returnOrders?.map((x) => x.toJson()).toList(),
    'payments': payments?.map((x) => x.toJson()).toList(),
    'promoCodes': promoCodes,
    'orderDetails': orderDetails?.map((x) => x.toJson()).toList(),
  };
}

class BranchModel {
  int? branchId;
  String? name;
  String? phone;
  String? street;
  String? ward;
  String? district;
  String? city;
  dynamic inventory; // null hoặc dynamic

  BranchModel({
    this.branchId,
    this.name,
    this.phone,
    this.street,
    this.ward,
    this.district,
    this.city,
    this.inventory,
  });

  factory BranchModel.fromJson(Map<String, dynamic> json) => BranchModel(
    branchId: json['branchId'] as int?,
    name: json['name'] as String?,
    phone: json['phone'] as String?,
    street: json['street'] as String?,
    ward: json['ward'] as String?,
    district: json['district'] as String?,
    city: json['city'] as String?,
    inventory: json['inventory'],
  );

  Map<String, dynamic> toJson() => {
    'branchId': branchId,
    'name': name,
    'phone': phone,
    'street': street,
    'ward': ward,
    'district': district,
    'city': city,
    'inventory': inventory,
  };
}

class CartModel {
  int? cartId;
  String? status;
  UserModel? user;

  CartModel({this.cartId, this.status, this.user});

  factory CartModel.fromJson(Map<String, dynamic> json) => CartModel(
    cartId: json['cartId'] as int?,
    status: json['status'] as String?,
    user: json['user'] != null ? UserModel.fromJson(json['user']) : null,
  );

  Map<String, dynamic> toJson() => {
    'cartId': cartId,
    'status': status,
    'user': user?.toJson(),
  };
}

class DeliveryInfoModel {
  int? deliveryInfoId;
  String? street;
  String? ward;
  String? district;
  String? city;
  String? cod;
  String? weight;
  String? width;
  String? height;
  String? length;
  String? metadata;
  String? status;
  DateTime? deliveryDate;
  OrderModel? order;

  DeliveryInfoModel({
    this.deliveryInfoId,
    this.street,
    this.ward,
    this.district,
    this.city,
    this.cod,
    this.weight,
    this.width,
    this.height,
    this.length,
    this.metadata,
    this.status,
    this.deliveryDate,
    this.order,
  });

  factory DeliveryInfoModel.fromJson(Map<String, dynamic> json) =>
      DeliveryInfoModel(
        deliveryInfoId: json['deliveryInfoId'] as int?,
        street: json['street'] as String?,
        ward: json['ward'] as String?,
        district: json['district'] as String?,
        city: json['city'] as String?,
        cod: json['cod'] as String?,
        weight: json['weight'] as String?,
        width: json['width'] as String?,
        height: json['height'] as String?,
        length: json['length'] as String?,
        metadata: json['metadata'] as String?,
        status: json['status'] as String?,
        deliveryDate:
            json['deliveryDate'] != null
                ? DateTime.parse(json['deliveryDate'])
                : null,
        order:
            json['order'] != null ? OrderModel.fromJson(json['order']) : null,
      );

  Map<String, dynamic> toJson() => {
    'deliveryInfoId': deliveryInfoId,
    'street': street,
    'ward': ward,
    'district': district,
    'city': city,
    'cod': cod,
    'weight': weight,
    'width': width,
    'height': height,
    'length': length,
    'metadata': metadata,
    'status': status,
    'deliveryDate': deliveryDate?.toIso8601String(),
    'order': order?.toJson(),
  };
}

class PaymentModel {
  int? paymentId;
  String? paymentMethod;
  String? paymentDate;
  String? status;
  double? grandTotal;
  String? transactionId;
  String? bankCode;
  String? bankTranNo;
  String? responseCode;
  DateTime? updateDate;
  UserModel? user;
  OrderModel? order;

  PaymentModel({
    this.paymentId,
    this.paymentMethod,
    this.paymentDate,
    this.status,
    this.grandTotal,
    this.transactionId,
    this.bankCode,
    this.bankTranNo,
    this.responseCode,
    this.updateDate,
    this.user,
    this.order,
  });

  factory PaymentModel.fromJson(Map<String, dynamic> json) => PaymentModel(
    paymentId: json['paymentId'] as int?,
    paymentMethod: json['paymentMethod'] as String?,
    paymentDate: json['paymentDate'] as String?,
    status: json['status'] as String?,
    grandTotal: (json['grandTotal'] as num?)?.toDouble(),
    transactionId: json['transactionId'] as String?,
    bankCode: json['bankCode'] as String?,
    bankTranNo: json['bankTranNo'] as String?,
    responseCode: json['responseCode'] as String?,
    updateDate:
        json['updateDate'] != null ? DateTime.parse(json['updateDate']) : null,
    user: json['user'] != null ? UserModel.fromJson(json['user']) : null,
    order: json['order'] != null ? OrderModel.fromJson(json['order']) : null,
  );

  Map<String, dynamic> toJson() => {
    'paymentId': paymentId,
    'paymentMethod': paymentMethod,
    'paymentDate': paymentDate,
    'status': status,
    'grandTotal': grandTotal,
    'transactionId': transactionId,
    'bankCode': bankCode,
    'bankTranNo': bankTranNo,
    'responseCode': responseCode,
    'updateDate': updateDate?.toIso8601String(),
    'user': user?.toJson(),
    'order': order?.toJson(),
  };
}

class PromoCode {
  int? promoCodeId;
  String? name;
  String? code;
  String? description;
  DateTime? startDate;
  DateTime? endDate;
  String? status;
  String? discountType;
  double? discountPercentage;
  double? minimumOrderValue;
  CampaignModel? campaign;

  PromoCode({
    this.promoCodeId,
    this.name,
    this.code,
    this.description,
    this.startDate,
    this.endDate,
    this.status,
    this.discountType,
    this.discountPercentage,
    this.minimumOrderValue,
    this.campaign,
  });

  factory PromoCode.fromJson(Map<String, dynamic> json) => PromoCode(
    promoCodeId: json['promoCodeId'] as int?,
    name: json['name'] as String?,
    code: json['code'] as String?,
    description: json['description'] as String?,
    startDate:
        json['startDate'] != null ? DateTime.parse(json['startDate']) : null,
    endDate: json['endDate'] != null ? DateTime.parse(json['endDate']) : null,
    status: json['status'] as String?,
    discountType: json['discountType'] as String?,
    discountPercentage: (json['discountPercentage'] as num?)?.toDouble(),
    minimumOrderValue: (json['minimumOrderValue'] as num?)?.toDouble(),
    campaign:
        json['campaign'] != null
            ? CampaignModel.fromJson(json['campaign'])
            : null,
  );

  Map<String, dynamic> toJson() => {
    'promoCodeId': promoCodeId,
    'name': name,
    'code': code,
    'description': description,
    'startDate': startDate?.toIso8601String(),
    'endDate': endDate?.toIso8601String(),
    'status': status,
    'discountType': discountType,
    'discountPercentage': discountPercentage,
    'minimumOrderValue': minimumOrderValue,
    'campaign': campaign?.toJson(),
  };
}

class OrderDetailModel {
  int? orderDetailId;
  int? quantity;
  double? unitPrice;
  double? totalPrice;
  String? image;
  OrderModel? order;
  ProductModel? product;

  OrderDetailModel({
    this.orderDetailId,
    this.quantity,
    this.unitPrice,
    this.totalPrice,
    this.image,
    this.order,
    this.product,
  });

  factory OrderDetailModel.fromJson(
    Map<String, dynamic> json,
  ) => OrderDetailModel(
    orderDetailId: json['orderDetailId'] as int?,
    quantity: json['quantity'] as int?,
    unitPrice: (json['unitPrice'] as num?)?.toDouble(),
    totalPrice: (json['totalPrice'] as num?)?.toDouble(),
    image: json['image'] as String?,
    order: json['order'] != null ? OrderModel.fromJson(json['order']) : null,
    product:
        json['product'] != null ? ProductModel.fromJson(json['product']) : null,
  );

  Map<String, dynamic> toJson() => {
    'orderDetailId': orderDetailId,
    'quantity': quantity,
    'unitPrice': unitPrice,
    'totalPrice': totalPrice,
    'image': image,
    'order': order?.toJson(),
    'product': product?.toJson(),
  };
}

class ReturnOrderModel {
  int? cancelOrderId;
  DateTime? cancelDate;
  String? status;
  String? reason;
  double? refundAmount;
  OrderModel? order;

  ReturnOrderModel({
    this.cancelOrderId,
    this.cancelDate,
    this.status,
    this.reason,
    this.refundAmount,
    this.order,
  });

  factory ReturnOrderModel.fromJson(
    Map<String, dynamic> json,
  ) => ReturnOrderModel(
    cancelOrderId: json['cancelOrderId'] as int?,
    cancelDate:
        json['cancelDate'] != null ? DateTime.parse(json['cancelDate']) : null,
    status: json['status'] as String?,
    reason: json['reason'] as String?,
    refundAmount: (json['refundAmount'] as num?)?.toDouble(),
    order: json['order'] != null ? OrderModel.fromJson(json['order']) : null,
  );

  Map<String, dynamic> toJson() => {
    'cancelOrderId': cancelOrderId,
    'cancelDate': cancelDate?.toIso8601String(),
    'status': status,
    'reason': reason,
    'refundAmount': refundAmount,
    'order': order?.toJson(),
  };
}

class CampaignModel {
  int? campaignId;
  String? name;
  String? description;
  String? status;
  DateTime? startDate;
  DateTime? endDate;
  List<PromoCode>? promoCodes;

  CampaignModel({
    this.campaignId,
    this.name,
    this.description,
    this.status,
    this.startDate,
    this.endDate,
    this.promoCodes,
  });

  factory CampaignModel.fromJson(Map<String, dynamic> json) => CampaignModel(
    campaignId: json['campaignId'] as int?,
    name: json['name'] as String?,
    description: json['description'] as String?,
    status: json['status'] as String?,
    startDate:
        json['startDate'] != null ? DateTime.parse(json['startDate']) : null,
    endDate: json['endDate'] != null ? DateTime.parse(json['endDate']) : null,
    promoCodes:
        json['promoCodes'] != null
            ? List<PromoCode>.from(
              json['promoCodes'].map((x) => PromoCode.fromJson(x)),
            )
            : null,
  );

  Map<String, dynamic> toJson() => {
    'campaignId': campaignId,
    'name': name,
    'description': description,
    'status': status,
    'startDate': startDate?.toIso8601String(),
    'endDate': endDate?.toIso8601String(),
    'promoCodes': promoCodes?.map((x) => x.toJson()).toList(),
  };
}
