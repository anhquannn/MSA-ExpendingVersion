import 'package:msa/core/utils/utility.dart';

class PromoCodeRequestModel {
  final int userId;
  final int? campaignId;
  final int page;
  final int pageSize;
  final PromoCodeStatusEnum? status;

  PromoCodeRequestModel({
    required this.userId,
    this.campaignId,
    this.page = 1,
    this.pageSize = 20,
    this.status
  });
  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'campaignId': campaignId,
      'page': page,
      'pageSize': pageSize,
      'status':status?.name
    };
  }
}
