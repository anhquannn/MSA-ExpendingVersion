import { api } from './apiService';
import { Product } from './productService';
import { Inventory } from './inventoryService';
import { PagedResponse } from './categoryService'; // ✅ đã có sẵn

// === TYPES ===

export interface InventoryStatistics {
  totalProducts: number;
  totalQuantity: number;
  lowStockCount: number;
  highStockCount: number;
}

export interface InventoryProduct {
  inventoryProductId: number;
  stockNumber: number;
  stockNumberChecked: number | null;
  stockNumberDifferent: number;
  currentPrice: number;
  expDate: string | null;
  batchNumber: string | null;
  discounted: boolean;
  stockLevel: 'low' | 'medium' | 'high' | string;
  minThreshold?: number;
  maxThreshold?: number;
  active: boolean;
  product: Product;
  inventory: Inventory;
}

export interface InventoryProductFilterParams {
  inventoryId: number;
  minStock?: number;
  maxStock?: number;
  minPrice?: number;
  maxPrice?: number;
  active?: boolean;
  discounted?: boolean;
  batchNumber?: string;
  productId?: number;
  fromDate?: string;
  toDate?: string;
  sortBy?: string;
  sortDirection?: 'DESC' | 'ASC';
  page?: number;
  pageSize?: number;
}

export interface InventoryProductCreatePayload {
  stockNumber: number;
  inventoryId: number;
  productId: number;
  stockLevel: 'low' | 'medium' | 'high' | string;
  minThreshold?: number;
  maxThreshold?: number;
  expDate?: string; // "YYYY-MM-DD HH:mm:ss"
  batchNumber?: string;
  discounted?: boolean;
}

export interface InventoryProductUpdatePayload {
  stockNumber: number;
  inventoryId: number;
  productId: number;
  stockLevel: 'low' | 'medium' | 'high' | string;
  minThreshold?: number;
  maxThreshold?: number;
  expDate?: string;
  batchNumber?: string;
  discounted?: boolean;
}

// === SERVICE ===

export const inventoryProductService = {
  getInventoryStatistics: async (inventoryId: number): Promise<InventoryStatistics> => {
    type FullApiResponse = {
      result: InventoryStatistics;
      message: string;
      success: boolean;
    };
    const response = await api.get<FullApiResponse>(`inventory-product/statistic/${inventoryId}`);
    return response.result;
  },

  getInventoryProductList: async (
    params: InventoryProductFilterParams
  ): Promise<PagedResponse<InventoryProduct>> => {
    type FullApiResponse = {
      result: PagedResponse<InventoryProduct>;
      message: string;
      success: boolean;
    };

    const finalParams = {
      ...params,
      active: true,
      page: params.page ?? 1,
      pageSize: params.pageSize ?? 10,
    };

    const response = await api.post<FullApiResponse>('inventory-product/paging', finalParams);
    return response.result;
  },

  getInventoryProductById: async (inventoryProductId: number): Promise<InventoryProduct> => {
    type FullApiResponse = {
      result: InventoryProduct;
      message: string;
      success: boolean;
    };
    const response = await api.get<FullApiResponse>(`inventory-product/${inventoryProductId}`);
    return response.result;
  },

  createInventoryProduct: async (payload: InventoryProductCreatePayload): Promise<InventoryProduct> => {
    type FullApiResponse = {
      result: InventoryProduct;
      message: string;
      success: boolean;
    };
    const response = await api.post<FullApiResponse>('inventory-product', payload);
    return response.result;
  },

  updateInventoryProduct: async (
    inventoryProductId: number,
    payload: InventoryProductUpdatePayload
  ): Promise<InventoryProduct> => {
    type FullApiResponse = {
      result: InventoryProduct;
      message: string;
      success: boolean;
    };
    const response = await api.put<FullApiResponse>(`inventory-product/${inventoryProductId}`, payload);
    return response.result;
  },

  deleteInventoryProduct: async (inventoryProductId: number): Promise<void> => {
    await api.delete<void>(`inventory-product/${inventoryProductId}`);
  },
};
