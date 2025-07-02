package com.market.MSA.responses.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response DTO for revenue statistics */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatisticsResponse {
  /** Total revenue of the current month */
  private Double totalMonthlyRevenue;

  /** Percentage change in revenue compared to previous month */
  private Double revenueChangePercent;

  /** Total number of orders in the current month */
  private Long totalOrders;

  /** Percentage change in order count compared to previous month */
  private Double orderCountChangePercent;

  /** Total yearly revenue */
  private Double totalYearlyRevenue;

  /** User's monthly revenue (if userId is provided) */
  private Double userMonthlyRevenue;

  /** User's yearly revenue (if userId is provided) */
  private Double userYearlyRevenue;

  /** Branch's monthly revenue (if branchId is provided) */
  private Double branchMonthlyRevenue;

  /** Branch's yearly revenue (if branchId is provided) */
  private Double branchYearlyRevenue;

  /** Combined branch and user monthly revenue (if both branchId and userId are provided) */
  private Double branchUserMonthlyRevenue;

  /** Combined branch and user yearly revenue (if both branchId and userId are provided) */
  private Double branchUserYearlyRevenue;

  /** Top selling products list */
  private java.util.List<TopSellingProductResponse> topSellingProducts;

  /** List of monthly revenue data (within selected period) */
  private java.util.List<MonthlyRevenueDataResponse> revenues;

  /** Expiring and low stock products list */
  private java.util.List<ExpiringProductResponse> expiringLowStockProducts;
}
