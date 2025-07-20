// File: src/services/inventoryService.ts

import {api} from './apiService';
import { Branch } from './branchService'; 
import { PagedResponse } from './categoryService';

export interface Inventory {
  inventoryId: number;
  name: string;
  address: string;
  contact: string;
  totalRevenue: number;
  branch: Branch;
  inventoryProductResponses: any[] | null;
}
export interface InventoryCreateParams {
  name: string;
  address: string;
  contact: string;
  totalRevenue?: number; 
  branchId: number;
}


export type InventoryUpdateParams = Partial<InventoryCreateParams>;
export interface InventoryListParams {
  keyword?: string;
  branchId?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page?: number;   
  pageSize?: number; 
}

export interface CheckedHistory {
  checkedHistoryId: number;
  checkedDate: string; // ISO string
  note: string;
  user: { id: number; fullName: string } | null;
}

export const inventoryService = {
  getAllInventories: async (params: InventoryListParams): Promise<Inventory[]> => {
    type FullApiResponse = { result: Inventory[] };
    const response = await api.post<FullApiResponse>('inventory/list', params);
    return response.result;
  },

  getInventoryWithPaging: async (
    params: InventoryListParams
  ): Promise<PagedResponse<Inventory>> => {
    type FullApiResponse = { result: PagedResponse<Inventory> };
    const response = await api.post<FullApiResponse>('inventory/paging', params);
    return response.result;
  },
  
  getInventoryById: async (inventoryId: number): Promise<Inventory> => {
    type FullApiResponse = { result: Inventory };
    const response = await api.get<FullApiResponse>(`inventory/${inventoryId}`);
    return response.result;
  },
  createInventory: async (payload: InventoryCreateParams): Promise<Inventory> => {
    type FullApiResponse = { result: Inventory };
    const response = await api.post<FullApiResponse>('inventory', payload);
    return response.result;
  },
  // Accept partial fields when updating an inventory record
  updateInventory: async (
    inventoryId: number,
    payload: InventoryUpdateParams
  ): Promise<Inventory> => {
    type FullApiResponse = { result: Inventory };
    const response = await api.put<FullApiResponse>(`inventory/${inventoryId}`, payload);
    return response.result;
  },
  deleteInventory: (inventoryId: number): Promise<void> => {
    return api.delete<void>(`inventory/${inventoryId}`);
  },
  getCheckedHistoriesPaging: async (params: {
    inventoryId: number;
    keyword?: string;
    page?: number;
    pageSize?: number;
    sortBy?: string;
    sortDirection?: 'ASC' | 'DESC';
  }): Promise<PagedResponse<CheckedHistory>> => {
    type FullApiResponse = { result: PagedResponse<CheckedHistory> };
    const response = await api.post<FullApiResponse>('checked-history/paging', {
      keyword: params.keyword ?? '',
      sortBy: params.sortBy || 'checkedDate',
      sortDirection: params.sortDirection || 'DESC',
      page: params.page || 1,
      pageSize: params.pageSize || 10,
      inventoryId: params.inventoryId,
    });
    return response.result;
  },
};