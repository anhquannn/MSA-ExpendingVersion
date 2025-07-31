// File: src/services/promotionService.ts
import { api, ApiResponse, PagedResponse } from './apiService';

export interface PromotionProductRef {
  productId: number;
  name: string;
}

export interface Promotion {
  promotionId: number;
  productMain: PromotionProductRef;
  productFree: PromotionProductRef;
  startDate: string; // ISO
  endDate: string;
  discountPercentage: number;
  status: 'ACTIVE' | 'INACTIVE' | 'EXPIRED';
}

export interface PromotionCreatePayload {
  productMainId: number;
  productFreeId: number;
  startDate: string; // ISO 8601
  endDate: string;   // ISO 8601
  discountPercentage: number;
  status?: string;
}

export interface PromotionFilterRequest {
  keyword?: string;
  status?: string;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page?: number;
  pageSize?: number;
}

const root = 'promotion';

export const promotionService = {
  createPromotion: async (dto: PromotionCreatePayload): Promise<Promotion> => {
    const { result } = await api.post<ApiResponse<Promotion>>(root, dto);
    return result;
  },
  updatePromotion: async (id: number, dto: PromotionCreatePayload): Promise<Promotion> => {
    const { result } = await api.put<ApiResponse<Promotion>>(`${root}/${id}`, dto);
    return result;
  },
  deletePromotion: async (id: number): Promise<boolean> => {
    const { result } = await api.delete<ApiResponse<boolean>>(`${root}/${id}`);
    return result;
  },
  getPromotionById: async (id: number): Promise<Promotion> => {
    const { result } = await api.get<ApiResponse<Promotion>>(`${root}/${id}`);
    return result;
  },
  getAllPromotions: async (): Promise<Promotion[]> => {
    const { result } = await api.get<ApiResponse<Promotion[]>>(`${root}/all`);
    return result;
  },
  getListPromotions: async (filter: PromotionFilterRequest): Promise<Promotion[]> => {
    const { result } = await api.post<ApiResponse<Promotion[]>>(`${root}/list`, filter);
    return result;
  },
  getPagingPromotions: async (filter: PromotionFilterRequest): Promise<PagedResponse<Promotion>> => {
    const { result } = await api.post<ApiResponse<PagedResponse<Promotion>>>(`${root}/paging`, filter);
    return result;
  },
  applyPromotionForCart: async (cartId: number): Promise<void> => {
    await api.post(`${root}/apply?cartId=${cartId}`, null);
  },
};
