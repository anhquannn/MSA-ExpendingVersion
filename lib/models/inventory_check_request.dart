class InventoryCheckRequest {
  InventoryCheckRequest({
    required this.id,
    required this.note,
    required this.requestedDate,
    required this.status,
    required this.inventoryId,
    required this.inventoryName,
    required this.surveyorId,
    required this.userId,
  });

  factory InventoryCheckRequest.fromJson(Map<String, dynamic> json) {
    // The backend field names may vary; we try a few sensible fallbacks to keep the parsing resilient.
    final inventory = json['inventory'] as Map<String, dynamic>?;
    return InventoryCheckRequest(
      id: json['icrId'] as int? ?? json['inventoryCheckRequestId'] as int? ?? 0,
      note: json['note'] as String? ?? '',
      requestedDate: DateTime.tryParse(json['requestedDate'] as String? ?? '') ?? DateTime.now(),
      status: (json['status'] as String?) ?? '',
      inventoryId: json['inventoryId'] as int? ?? inventory?['inventoryId'] as int? ?? 0,
      inventoryName: inventory?['name'] as String? ?? '',
      surveyorId: json['surveyorId'] as int? ?? 0,
      userId: json['userId'] as int? ?? 0,
    );
  }

  final int id;
  final String note;
  final DateTime requestedDate;
  final String status;
  final int inventoryId;
  final String inventoryName;
  final int surveyorId;
  final int userId;
}
