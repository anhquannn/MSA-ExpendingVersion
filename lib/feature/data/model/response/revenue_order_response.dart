class RevenueOrderResponse {
  final double? branchYearlyRevenue;
  final double? branchUserYearlyRevenue;
  final double? userYearlyRevenue;
  final double? branchMonthlyRevenue;
  final double? userMonthlyRevenue;
  final double? totalMonthlyRevenue;
  final double? branchUserMonthlyRevenue;
  final double? totalYearlyRevenue;

  RevenueOrderResponse({
    this.branchYearlyRevenue,
    this.branchUserYearlyRevenue,
    this.userYearlyRevenue,
    this.branchMonthlyRevenue,
    this.userMonthlyRevenue,
    this.totalMonthlyRevenue,
    this.branchUserMonthlyRevenue,
    this.totalYearlyRevenue,
  });

  factory RevenueOrderResponse.fromJson(Map<String, dynamic> json) {
    return RevenueOrderResponse(
      branchYearlyRevenue: (json['branchYearlyRevenue'] as num?)?.toDouble(),
      branchUserYearlyRevenue:
          (json['branchUserYearlyRevenue'] as num?)?.toDouble(),
      userYearlyRevenue: (json['userYearlyRevenue'] as num?)?.toDouble(),
      branchMonthlyRevenue: (json['branchMonthlyRevenue'] as num?)?.toDouble(),
      userMonthlyRevenue: (json['userMonthlyRevenue'] as num?)?.toDouble(),
      totalMonthlyRevenue: (json['totalMonthlyRevenue'] as num?)?.toDouble(),
      branchUserMonthlyRevenue:
          (json['branchUserMonthlyRevenue'] as num?)?.toDouble(),
      totalYearlyRevenue: (json['totalYearlyRevenue'] as num?)?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'branchYearlyRevenue': branchYearlyRevenue,
      'branchUserYearlyRevenue': branchUserYearlyRevenue,
      'userYearlyRevenue': userYearlyRevenue,
      'branchMonthlyRevenue': branchMonthlyRevenue,
      'userMonthlyRevenue': userMonthlyRevenue,
      'totalMonthlyRevenue': totalMonthlyRevenue,
      'branchUserMonthlyRevenue': branchUserMonthlyRevenue,
      'totalYearlyRevenue': totalYearlyRevenue,
    };
  }
}
