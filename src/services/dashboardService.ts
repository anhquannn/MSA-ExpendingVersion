import { api } from './apiService';

export interface RevenueStatisticsResponse {
  totalMonthlyRevenue: number;
  revenueChangePercent: number;
  totalOrders: number;
  orderCountChangePercent: number;
  totalYearlyRevenue: number;
  userMonthlyRevenue: number;
  userYearlyRevenue: number;
  branchMonthlyRevenue: number;
  branchYearlyRevenue: number;
  branchUserMonthlyRevenue: number;
  branchUserYearlyRevenue: number;
}

export interface RevenueStatisticsFilter {
  year: number;
  month: number;
  branchId?: number;
}

export const dashboardService = {
  getRevenueStatistics: async (
  filter: RevenueStatisticsFilter
    ): Promise<RevenueStatisticsResponse> => {
    type FullApiResponse = {
        result: RevenueStatisticsResponse;
        message: string;
        code: number;
    };

    const response = await api.get<FullApiResponse>('order/revenue/statistics', {
      year: filter.year,
      month: filter.month,
      branchId: filter.branchId,
    });

    return response.result;
    }
};
