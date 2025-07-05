import { api } from './apiService';

export interface TopSellingProduct {
  productId: number;
  name: string;
  totalQuantity: number;
}

export interface MonthlyRevenueData {
  year: number;
  month: number;
  revenue: number;
}

export interface ExpiringProduct {
  productId: number;
  name: string;
  quantity: number;
  expDate: string;
}

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
  topSellingProducts: TopSellingProduct[];
  revenues: MonthlyRevenueData[];
  expiringLowStockProducts: ExpiringProduct[];
}

export interface BranchRevenue {
  branchId: number;
  name: string;
  totalRevenue: number;
}

export interface TopCustomer {
  customerId: number;
  name: string;
  totalSpent: number;
}

export interface RevenueStatisticsFilter {
  year: number;
  month: number;
  branchId?: number;
  topPeriod?: number;
  topLimit?: number;
  expiringDays?: number;
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
      topPeriod: filter.topPeriod || 3,
      topLimit: filter.topLimit || 5,
      expiringDays: filter.expiringDays || 30
    });

    return response.result;
    },
    getBranchRevenues: async (params: {year:number; month:number; periodMonths?:number}): Promise<BranchRevenue[]> => {
      type BranchApiResponse = { result: BranchRevenue[]; message: string; code: number };
      const res = await api.get<BranchApiResponse>('order/revenue/branches', {
        year: params.year,
        month: params.month,
        periodMonths: params.periodMonths ?? 6
      });
      return res.result;
    },
    getTopCustomers: async (params: {year:number; month:number; branchId?:number; topPeriod?:number; topLimit?:number}): Promise<TopCustomer[]> => {
      type ApiResp = { result: TopCustomer[]; message: string; code:number };
      const res = await api.get<ApiResp>('order/revenue/top-customers', {
        year: params.year,
        month: params.month,
        branchId: params.branchId,
        topPeriod: params.topPeriod ?? 6,
        topLimit: params.topLimit ?? 10,
      });
      return res.result;
    }
};
