import { api, ApiResponse, PagedResponse } from './apiService';

export interface LowStockItem {
  inventoryProductId: number;
  stockNumber: number;
  stockNumberChecked: number | null;
  stockNumberDifferent: number;
  currentPrice: number;
  branchCurrentPrice: number;
  expDate: string;
  createdAt: string | null;
  updatedAt: string | null;
  batchNumber: string;
  stockLevel: string;
  minThreshold: number;
  maxThreshold: number;
  product: {
    productId: number;
    name: string;
    price: number;
    branchCurrentPrice: number | null;
    discountPercentage: number;
    discountTriggerDays: number;
    unit: string;
    netWeight: string;
    specification: string;
    description: string;
    createdAt: string;
    updatedAt: string | null;
    lastClassificationDate: string | null;
    abcClassification: string;
    totalRevenue: number;
    supplier: {
      supplierId: number;
      name: string;
      address: string;
      contact: string;
      image: string;
    };
    category: {
      categoryId: number;
      name: string;
      description: string;
      parentCategory: any | null;
    };
  };
  inventory: {
    inventoryId: number;
    name: string;
    address: string;
    contact: string;
    totalRevenue: number;
  };
  active: boolean;
  discounted: boolean;
}

const HEAD_INVENTORY_ID = 1;

export const lowStockService = {
  /**
   * Lấy tổng số sản phẩm tồn kho thấp.
   */
  async getLowStockCount(inventoryId: number = HEAD_INVENTORY_ID): Promise<number> {
    try {
      const params = {
        inventoryId,
        isLowStock: true,
        page: 1,
        pageSize: 1,
      } as Record<string, any>;

      console.log('📤 Fetching low stock count with params:', params);
      const response = await api.get<ApiResponse<PagedResponse<LowStockItem>>>('inventory-product/paging', params);
      console.log('📥 Low stock count response:', response);
      
      return response.result?.totalElements || 0;
    } catch (error) {
      console.error('❌ Error in getLowStockCount:', error);
      return 0;
    }
  },

  /**
   * Lấy danh sách sản phẩm tồn kho thấp.
   */
  async getLowStockList(inventoryId: number = HEAD_INVENTORY_ID, page = 1, pageSize = 1000): Promise<LowStockItem[]> {
    try {
      const params = {
        inventoryId,
        isLowStock: true,
        page,
        pageSize,
        sortBy: 'stockNumber',
        sortDirection: 'ASC',
      } as Record<string, any>;

      console.log('📤 Fetching low stock list with params:', params);
      const response = await api.get<ApiResponse<PagedResponse<LowStockItem>>>('inventory-product/paging', params);
      console.log('📥 Low stock list response:', response);
      
      return response.result?.content || [];
    } catch (error) {
      console.error('❌ Error in getLowStockList:', error);
      return [];
    }
  },
};
