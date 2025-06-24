// File: src/services/inventoryProductService.ts

import { api, ApiResponse } from './apiService';
import { Product } from './productService'; // Import các type cần thiết
import { Inventory } from './inventoryService';

// === TYPES ===

/**
 * @description Dữ liệu thống kê của một kho hàng cụ thể.
 */
export interface InventoryStatistics {
  totalProducts: number;
  totalQuantity: number;
  lowStockCount: number;
  highStockCount: number;
}

/**
 * @description Đại diện cho một sản phẩm cụ thể nằm trong một kho.
 * Đây là type chi tiết dựa trên JSON response bạn đã cung cấp.
 */
export interface InventoryProduct {
  inventoryProductId: number;
  stockNumber: number;
  currentPrice: number;
  expDate: string | null;
  batchNumber: string | null;
  stockLevel: 'low' | 'medium' | 'high' | string;
  active: boolean;
  product: Product; // Thông tin chi tiết sản phẩm
  inventory: Inventory; // Thông tin kho chứa sản phẩm
}

/**
 * @description Tham số bộ lọc để lấy danh sách sản phẩm trong kho.
 */
export interface InventoryProductFilterParams {
  inventoryId: number;
  keyword?: string;
  minStock?: number;
  maxStock?: number;
  minPrice?: number;
  maxPrice?: number;
  active?: boolean;
  discounted?: boolean;
  batchNumber?: string;
  productId?: number;
  fromDate?: string; // Format: 'YYYY-MM-DD'
  toDate?: string;   // Format: 'YYYY-MM-DD'
  sortBy?: string;
  sortDirection?: 'DESC' | 'ASC';
  page?: number;
  pageSize?: number;
}

/**
 * @description Dữ liệu cần thiết để thêm một sản phẩm vào kho.
 */
export interface InventoryProductCreatePayload {
  stockNumber: number;
  inventoryId: number;
  productId: number;
   stockLevel: 'low' | 'medium' | 'high' | string; 
}
export interface InventoryProductUpdatePayload {
  stockNumber: number;
  inventoryId: number;
  productId: number;
  stockLevel: 'low' | 'medium' | 'high' | string;
}
/**
 * @description Dữ liệu để cập nhật một sản phẩm trong kho (thường là số lượng).
 */
// export type InventoryProductUpdatePayload = Pick<InventoryProductCreatePayload, 'stockNumber'>;


// === SERVICE OBJECT ===

export const inventoryProductService = {
  /**
   * @description Lấy dữ liệu thống kê của một kho hàng.
   * @returns Promise chứa toàn bộ ApiResponse.
   */
  getInventoryStatistics: async (inventoryId: number): Promise<ApiResponse<InventoryStatistics>> => {
    const response = await api.get<ApiResponse<InventoryStatistics>>(`/inventory-product/statistic/${inventoryId}`);
    return response;
  },

  /**
   * @description Lấy danh sách sản phẩm trong kho theo bộ lọc.
   * @returns Promise chứa toàn bộ ApiResponse.
   */
  getInventoryProductList: async (params: InventoryProductFilterParams): Promise<ApiResponse<InventoryProduct[]>> => {
    // Lưu ý: Dùng POST để gửi filter body là một lựa chọn của backend.
    // Về mặt RESTful convention, GET với query params thường được ưa chuộng hơn.
    const response = await api.post<ApiResponse<InventoryProduct[]>>('/inventory-product/list', params);
    return response;
  },

  /**
   * @description Cập nhật một sản phẩm trong kho.
   * @returns Promise chứa toàn bộ ApiResponse.
   */
  updateInventoryProduct: async (inventoryProductId: number, payload: InventoryProductUpdatePayload): Promise<ApiResponse<InventoryProduct>> => {
    const response = await api.put<ApiResponse<InventoryProduct>>(`/inventory-product/${inventoryProductId}`, payload);
    return response;
  },

  /**
   * @description Lấy chi tiết một sản phẩm trong kho bằng ID của nó.
   * @returns Promise chứa toàn bộ ApiResponse.
   */
  getInventoryProductById: async (inventoryProductId: number): Promise<ApiResponse<InventoryProduct>> => {
    const response = await api.get<ApiResponse<InventoryProduct>>(`/inventory-product/${inventoryProductId}`);
    return response;
  },

  /**
   * @description Thêm một sản phẩm mới vào kho.
   * @returns Promise chứa toàn bộ ApiResponse.
   */
  createInventoryProduct: async (payload: InventoryProductCreatePayload): Promise<ApiResponse<InventoryProduct>> => {
    const response = await api.post<ApiResponse<InventoryProduct>>('/inventory-product', payload);
    return response;
  },

  /**
   * @description Xóa một sản phẩm khỏi kho.
   * @returns Promise chứa toàn bộ ApiResponse.
   */
  deleteInventoryProduct: async (inventoryProductId: number): Promise<ApiResponse<null>> => {
    const response = await api.delete<ApiResponse<null>>(`/inventory-product/${inventoryProductId}`);
    return response;
  },
};