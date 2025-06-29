class CreateShipmentResponse {
  final int? code;
  final String? status;
  final List<dynamic>? data;
  final String? id;
  final int? shipmentStatus;
  final String? shipmentStatusTxt;
  final double? cod;
  final double? fee;
  final String? trackingNumber;
  final String? carrier;
  final String? carrierShortName;
  final String? sortingCode;
  final String? createdAt;

  CreateShipmentResponse({
    this.code,
    this.status,
    this.data,
    this.id,
    this.shipmentStatus,
    this.shipmentStatusTxt,
    this.cod,
    this.fee,
    this.trackingNumber,
    this.carrier,
    this.carrierShortName,
    this.sortingCode,
    this.createdAt,
  });

  factory CreateShipmentResponse.fromJson(Map<String, dynamic> json) {
    return CreateShipmentResponse(
      code: json['code'],
      status: json['status'],
      data: json['data'],
      id: json['id'],
      shipmentStatus: json['shipment_status'],
      shipmentStatusTxt: json['shipment_status_txt'],
      cod: (json['cod'] as num?)?.toDouble(),
      fee: (json['fee'] as num?)?.toDouble(),
      trackingNumber: json['tracking_number'],
      carrier: json['carrier'],
      carrierShortName: json['carrier_short_name'],
      sortingCode: json['sorting_code'],
      createdAt: json['created_at'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'code': code,
      'status': status,
      'data': data,
      'id': id,
      'shipment_status': shipmentStatus,
      'shipment_status_txt': shipmentStatusTxt,
      'cod': cod,
      'fee': fee,
      'tracking_number': trackingNumber,
      'carrier': carrier,
      'carrier_short_name': carrierShortName,
      'sorting_code': sortingCode,
      'created_at': createdAt,
    };
  }

  @override
  String toString() {
    return '''
📦 ShipmentResponse:
🔢 code: $code
📈 status: $status
🆔 id: $id
🚚 shipmentStatus: $shipmentStatus ($shipmentStatusTxt)
💰 COD: $cod
💸 Fee: $fee
📦 Tracking: $trackingNumber
🏢 Carrier: $carrier ($carrierShortName)
🏷️ SortingCode: $sortingCode
🕒 CreatedAt: $createdAt
''';
  }
}
