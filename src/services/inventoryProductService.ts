// File: src/services/inventoryProductService.ts

import {api} from './apiService';
import { PagedResponse } from './categoryService'; 
import { Product } from './productService';       
import { Branch } from './branchService';        

export interface InventoryProduct {
  inventoryId: number;
  name: string;
  address: string;
  contact: string;
  totalRevenue: number;
  branch: Branch; // Giả định có interface Branch
}

export interface InventoryProduct {
  inventoryProductId: number;
  stockNumber: number;
  currentPrice: number;
  expDate: string | null;
  createdAt: string | null;
  updatedAt: string | null;
  batchNumber: string | null;
  stockLevel: 'low' | 'medium' | 'high';
  product: Product;
  inventory: InventoryProduct;
  active: boolean;
  discounted: boolean;
}

export interface InventoryProductCreatePayload {
  stockLevel: 'low' | 'medium' | 'high';
  stockNumber: number;
  inventoryId: number;
  productId: number;
}

export interface InventoryProductUpdatePayload extends InventoryProductCreatePayload {}

export interface InventoryStatistics {
  totalProducts: number;
  totalQuantity: number;
  lowStockCount: number;
  highStockCount: number;
}

export interface InventoryProductFilterParams {
  inventoryId?: number;
  productId?: number;
  batchNumber?: string;
  fromDate?: string; // "YYYY-MM-DD HH:mm:ss"
  toDate?: string;
  minStock?: number;
  maxStock?: number;
  minPrice?: number;
  maxPrice?: number;
  sortBy?: string;
  sortDirection?: 'DESC' | 'ASC';
  page?: number;
  pageSize?: number;
}

export const inventoryProductService = {

  filterInventoryProducts: async (params: InventoryProductFilterParams): Promise<PagedResponse<InventoryProduct>> => {
    type FullApiResponse = { result: PagedResponse<InventoryProduct> };
    const response = await api.post<FullApiResponse>('inventory-product/paging', params);
    return response.result;
  },

  createInventoryProduct: async (payload: InventoryProductCreatePayload): Promise<InventoryProduct> => {
    type FullApiResponse = { result: InventoryProduct };
    const response = await api.post<FullApiResponse>('inventory-product', payload);
    return response.result;
  },

  updateInventoryProduct: async (inventoryProductId: number, payload: InventoryProductUpdatePayload): Promise<InventoryProduct> => {
    type FullApiResponse = { result: InventoryProduct };
    const response = await api.put<FullApiResponse>(`inventory-product/${inventoryProductId}`, payload);
    return response.result;
  },

  deleteInventoryProduct: (inventoryProductId: number): Promise<void> => {
    return api.delete<void>(`inventory-product/${inventoryProductId}`);
  },

  getInventoryProductById: async (inventoryProductId: number): Promise<InventoryProduct> => {
    type FullApiResponse = { result: InventoryProduct };
    const response = await api.get<FullApiResponse>(`inventory-product/${inventoryProductId}`);
    return response.result;
  },

  getInventoryStatistics: async (inventoryId: number): Promise<InventoryStatistics> => {
    type FullApiResponse = { result: InventoryStatistics };
    const response = await api.get<FullApiResponse>(`inventory-product/statistic/${inventoryId}`);
    return response.result;
  },

  getTotalStockInBranch: (branchId: number, productId: number): Promise<number> => {
    return api.get<number>(`inventory-product/branch/${branchId}/product/${productId}/stock`);
  },
};