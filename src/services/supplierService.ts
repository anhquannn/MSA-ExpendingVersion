// File: src/services/supplierService.ts

import {api} from './apiService'; 
import { PagedResponse } from './categoryService';

export interface Supplier {
  supplierId: number;
  name: string;
  address: string;
  contact: string;
  image: string | null;
}

export interface SupplierPagingParams {
  keyword?: string;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page?: number;
  pageSize?: number;
}

export interface SupplierPayload {
  name: string;
  address: string;
  contact: string;
  image?: string | null;
}

export const supplierService = {

  getSuppliers: async (params: SupplierPagingParams): Promise<PagedResponse<Supplier>> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: PagedResponse<Supplier>;
    };

    const fullResponse = await api.post<FullApiResponse>('supplier/paging', params);
    return fullResponse.result;
  },

  createSupplier: async (payload: SupplierPayload): Promise<Supplier> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: Supplier;
    };
    const fullResponse = await api.post<FullApiResponse>('supplier', payload);
    return fullResponse.result;
  },

  updateSupplier: async (supplierId: number, payload: SupplierPayload): Promise<Supplier> => {
     type FullApiResponse = {
      code: number;
      message: string;
      result: Supplier;
    };
    const fullResponse = await api.put<FullApiResponse>(`supplier/${supplierId}`, payload);
    return fullResponse.result;
  },

  deleteSupplier: (supplierId: number): Promise<void> => {
    return api.delete<void>(`supplier/${supplierId}`);
  },

  getSupplierById: async (supplierId: number): Promise<Supplier> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: Supplier;
    };
    const fullResponse = await api.get<FullApiResponse>(`supplier/${supplierId}`);
    return fullResponse.result;
  },
};