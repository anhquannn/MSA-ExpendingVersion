import { api, PagedResponse } from './apiService';

export interface LowStockItem {
  productId: number;
  productName: string;
  stockNumber: number;
  stockLevel: string;
}

const HEAD_INVENTORY_ID = 1;

export const lowStockService = {
  /**
   * Lấy tổng số sản phẩm tồn kho thấp ở HEAD.
   */
  async getLowStockCount(inventoryId: number = HEAD_INVENTORY_ID): Promise<number> {
    const params = {
      inventoryId,
      isLowStock: true,
      page: 1,
      pageSize: 1,
    } as Record<string, any>;

    const res = await api.get<PagedResponse<LowStockItem>>('inventory-product/paging', params);
    return res.totalElements;
  },

  /**
   * Lấy danh sách sản phẩm tồn kho thấp ở HEAD.
   */
  async getLowStockList(inventoryId: number = HEAD_INVENTORY_ID, page = 1, pageSize = 1000): Promise<LowStockItem[]> {
    const params = {
      inventoryId,
      isLowStock: true,
      page,
      pageSize,
      sortBy: 'stockNumber',
      sortDirection: 'ASC',
    } as Record<string, any>;

    const res = await api.get<PagedResponse<LowStockItem>>('inventory-product/paging', params);
    return res.content;
  },
};
