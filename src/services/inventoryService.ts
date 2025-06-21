// File: src/services/inventoryService.ts

import {api} from './apiService';
import { Branch } from './branchService'; 
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

export type InventoryUpdateParams = InventoryCreateParams;
export interface InventoryListParams {
  keyword?: string;
  branchId?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page?: number;   
  pageSize?: number; 
}

export const inventoryService = {
  getAllInventories: async (params: InventoryListParams): Promise<Inventory[]> => {
    type FullApiResponse = { result: Inventory[] };
    const response = await api.post<FullApiResponse>('inventory/list', params);
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
  updateInventory: async (inventoryId: number, payload: InventoryUpdateParams): Promise<Inventory> => {
    type FullApiResponse = { result: Inventory };
    const response = await api.put<FullApiResponse>(`inventory/${inventoryId}`, payload);
    return response.result;
  },
  deleteInventory: (inventoryId: number): Promise<void> => {
    return api.delete<void>(`inventory/${inventoryId}`);
  },
};