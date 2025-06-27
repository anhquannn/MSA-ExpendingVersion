// File: src/services/productService.ts

import {api} from './apiService';
import { PagedResponse } from './categoryService'; 
import { Category } from './categoryService'; 
import { InventoryProductCreatePayload, inventoryProductService } from './inventoryProductService';
import { Supplier } from './supplierService'; 

export interface ProductImage {
  productImageId: number;
  imageUrl: string;
  sortOrder: number;
  isPrimary: boolean;
}

export interface Product {
  productId: number;
  name: string;
  price: number;
  discountPercentage?: number;
  discountTriggerDays?: number;
  unit: string;
  netWeight?: string;
  specification: string;
  description: string;
  totalRevenue: number;
  supplier: Supplier;
  category: Category;
  productImageResponses: ProductImage[] | null; 
}

export interface ProductCreatePayload {
  name: string;
  price: number;
  discountPercentage?: number;
  discountTriggerDays?: number;
  unit: string;
  netWeight?: string;
  specification: string;
  description: string;
  totalRevenue?: number;
  categoryId: number;
  supplierId: number;
}

export interface ProductUpdatePayload {
  name: string;
  price: number;
  discountPercentage?: number;
  discountTriggerDays?: number;
  unit: string;
  netWeight?: string;
  specification: string;
  description: string;
  totalRevenue?: number;
  categoryId: number;
  supplierId: number;
}

export interface ProductFilterParams {
  branchId?: number;
  categoryId?: number;
  supplierId?: number;
  unit?: string;
  fromDate?: string; // "YYYY-MM-DD HH:mm:ss"
  toDate?: string;   // "YYYY-MM-DD HH:mm:ss"
  netWeight?: string;
  minPrice?: number;
  maxPrice?: number;
  keyword?: string;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
  page?: number;
  pageSize?: number;
}

export interface FilteredProductsResult {
    productsPage: PagedResponse<Product>;
    discountedProductsPage: PagedResponse<Product>;
}

export interface ProductCombinationCreatePayload {
  productId1: number;
  productId2: number;
}

export interface ProductCombinationResponse {
  combinationId: number;
  productId1: number;
  productId2: number;
}

export const productService = {
  getProducts: async (params: ProductFilterParams): Promise<FilteredProductsResult> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: FilteredProductsResult;
    };
    const fullResponse = await api.post<FullApiResponse>('product/filter', params);
    return fullResponse.result;
  },
  createProduct: async (
    productData: ProductCreatePayload & { combinationProductIds?: number[] },
    imageUrls: string[],
    initialInventoryData: Omit<InventoryProductCreatePayload, 'productId'>
  ): Promise<Product> => {
    
    console.log("Service: Bước 1 - Đang tạo sản phẩm...");
    const { combinationProductIds, ...baseProductData } = productData as any;
    const productResponse = await api.post<{ result: Product }>('product', baseProductData);
    const newProduct = productResponse.result;

    if (!newProduct || !newProduct.productId) {
      throw new Error('Tạo sản phẩm thất bại hoặc không nhận được ID sản phẩm.');
    }
    console.log(`Service: Tạo sản phẩm thành công với ID: ${newProduct.productId}`);

    if (imageUrls && imageUrls.length > 0) {
      console.log(`Service: Bước 2 - Đang thêm ${imageUrls.length} ảnh...`);
      const imageAddPromises = imageUrls.map((url, index) => {
        const imagePayload = {
          imageUrl: url,
          sortOrder: index + 1,
          productId: newProduct.productId,
        };
        return api.post('image', imagePayload);
      });
      await Promise.all(imageAddPromises);
      console.log("Service: Thêm tất cả ảnh thành công.");
    }
    console.log(`Service: Bước 3 - Đang thêm vào kho ID: ${initialInventoryData.inventoryId}...`);
    await inventoryProductService.createInventoryProduct({
      productId: newProduct.productId, 
      inventoryId: initialInventoryData.inventoryId,
      stockNumber: initialInventoryData.stockNumber,
      stockLevel: initialInventoryData.stockLevel,
    });
    // create product combinations if any
    if (productData.combinationProductIds && productData.combinationProductIds.length > 0) {
      const comboPromises = productData.combinationProductIds.map((id) =>
        api.post('product-combinations', {
          productId1: newProduct.productId,
          productId2: id,
        })
      );
      await Promise.all(comboPromises);
    }
    return newProduct;
  },
  getRelatedProductIds: async (productId: number): Promise<number[]> => {
    type ListResp = { result: ProductCombinationResponse[] };
    const filterPayload = { productId1: productId, page: 1, pageSize: 100 };
    const resp = await api.post<ListResp>('product-combinations/list', filterPayload);
    const combos = resp.result || [];
    return combos.map(c => (c.productId1 === productId ? c.productId2 : c.productId1));
  },

  updateProduct: async (productId: number, payload: ProductUpdatePayload & { combinationProductIds?: number[] }): Promise<Product> => {
    type UpdateApiResponse = { result: Product };
    const { combinationProductIds, ...basePayload } = payload as any;
    const response = await api.put<UpdateApiResponse>(`product/${productId}`, basePayload);
    return response.result;
  },

  deleteProduct: (productId: number): Promise<void> => {
    return api.delete<void>(`product/${productId}`);
  },

  getProductById: async (productId: number): Promise<Product> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: Product; 
    };
    const response = await api.get<FullApiResponse>(`product/${productId}`);
    return response.result;
  },
};