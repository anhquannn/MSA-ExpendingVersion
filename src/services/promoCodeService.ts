// File: src/services/promoCodeService.ts

import { api } from './apiService';
import { PagedResponse } from './categoryService';

export interface PromoCode {
  promoCodeId?: number;
  name: string;
  code: string;
  description: string;
  startDate: string;
  endDate: string;
  status: string;
  discountPercentage: number;
  minimumOrderValue: number;
  campaignId: number;
  campaign?: {
    campaignId: number;
    name: string;
  };
  createdAt?: string;
  updatedAt?: string;
}

export interface PromoCodeFilter {
  keyword?: string;
  status?: string;
  fromDate?: string;
  toDate?: string;
  campaignId?: number;
  userId?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page: number;
  pageSize: number;
}

export interface PromoCodePayload {
  name: string;
  code: string;
  description: string;
  startDate: string;
  endDate: string;
  status: string;
  discountPercentage: number;
  minimumOrderValue: number;
  campaignId: number;
}

export const promoCodeService = {
  createPromoCode: async (payload: PromoCodePayload): Promise<PromoCode> => {
    type FullApiResponse = {
      result: PromoCode;
      message: string;
      success: boolean;
    };

    const response = await api.post<FullApiResponse>('promo-code', payload);
    return response.result;
  },

  updatePromoCode: async (promoCodeId: number, payload: PromoCodePayload): Promise<PromoCode> => {
    type FullApiResponse = {
      result: PromoCode;
      message: string;
      success: boolean;
    };

    const response = await api.put<FullApiResponse>(`promo-code/${promoCodeId}`, payload);
    return response.result;
  },

  deletePromoCode: async (promoCodeId: number): Promise<void> => {
    await api.delete<void>(`promo-code/${promoCodeId}`);
  },

  getPromoCodeById: async (promoCodeId: number): Promise<PromoCode> => {
    type FullApiResponse = {
      result: PromoCode;
      message: string;
      success: boolean;
    };

    const response = await api.get<FullApiResponse>(`promo-code/${promoCodeId}`);
    return response.result;
  },

  getPromoCodeByCode: async (code: string, userId: number): Promise<PromoCode> => {
    type FullApiResponse = {
      result: PromoCode;
      message: string;
      success: boolean;
    };

    const response = await api.get<FullApiResponse>(`promo-code/user/${userId}/code/${code}`);
    return response.result;
  },

  getAllPromoCodes: async (
    filter: Omit<PromoCodeFilter, 'page' | 'pageSize'>
  ): Promise<PromoCode[]> => {
    type FullApiResponse = {
      result: PromoCode[];
      message: string;
      success: boolean;
    };

    const response = await api.post<FullApiResponse>(
      `promo-code/list`,
      filter
    );
    return response.result;
  },

  getPromoCodesWithPaging: async (
    filter: PromoCodeFilter
  ): Promise<PagedResponse<PromoCode>> => {
    type FullApiResponse = {
      result: PagedResponse<PromoCode>;
      message: string;
      success: boolean;
    };

    const response = await api.post<FullApiResponse>(
      `promo-code/paging`,
      {
        ...filter,
        sortBy: filter.sortBy || 'startDate',
        sortDirection: filter.sortDirection || 'DESC',
      }
    );
    return response.result;
  },
};
