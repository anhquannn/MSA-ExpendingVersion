// src/services/productComboService.ts
// Service dedicated to create / retrieve product combo records in the Admin panel.
// If later you want to extend it (update, delete, pagination...) just follow the same style.

import { api } from './apiService';

export interface ComboProductItem {
  productId: number;
  quantity: number;
}

export interface ProductComboCreatePayload {
  name: string;
  discountPercent?: number; // optional if finalPrice provided
  finalPrice?: number; // optional custom final price
  productIds: number[]; // list of product ids in combo
}

export interface ProductCombo {
  comboId: number;
  name: string;
  discountPercent?: number;
  finalPrice?: number;
  productIds: number[];
}

export const productComboService = {
  /**
   * Create a new product combo.
   * Matches the POSTMAN test: POST /product-combos with JSON body similar to ProductComboCreatePayload.
   */
  createCombo: async (payload: ProductComboCreatePayload): Promise<ProductCombo> => {
    type ApiResp = { result: ProductCombo };
    const resp = await api.post<ApiResp>('combos', payload);
    return resp.result;
  },

  /**
   * Fetch all combos (simple no-filter version). Add params as needed later.
   */
  getAllCombos: async (): Promise<ProductCombo[]> => {
    type ListResp = { result: ProductCombo[] };
    const resp = await api.get<ListResp>('combos');
    return resp.result;
  },
};
