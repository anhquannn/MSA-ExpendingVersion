class PromoCodeRequestModel {
  final int userId;
  final int? campaignId;
  final int page;
  final int pageSize;

  PromoCodeRequestModel({
    required this.userId,
     this.campaignId,
     this.page=1,
     this.pageSize=20,
  });
  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'campaignId': campaignId,
      'page': page,
      'pageSize': pageSize,
    };
  }
}