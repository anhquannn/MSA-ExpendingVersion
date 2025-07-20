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
  categoryIds?: number[];
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

export interface ProductSalesStatistics {
  monthlySales: { monthLabel: string; quantity: number }[];
  stockNumber: number;
  stockLevel: string;
  earliestExpDate?: string;
}

export const productService = {
  getProducts: async (params: ProductFilterParams): Promise<FilteredProductsResult> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: FilteredProductsResult;
    };
    const payload = {
      ...params,
      categoryIds: params.categoryIds ?? (params.categoryId ? [params.categoryId] : undefined),
    } as Omit<ProductFilterParams, 'categoryId'> & { categoryIds?: number[] };
    const { categoryId, ...rest } = payload as any; // ensure no stale field
    const fullResponse = await api.post<FullApiResponse>('product/filter', rest);
    return fullResponse.result;
  },
  createProduct: async (
    productData: ProductCreatePayload & { combinationProductIds?: number[] },
    imageUrls: string[],
    initialInventoryData: Omit<InventoryProductCreatePayload, 'productId'>,
    sendNotificationToAll: boolean = false
  ): Promise<Product> => {
    
    console.log("Service: Bước 1 - Đang tạo sản phẩm...");
    const { combinationProductIds, ...baseProductData } = productData as any;
    const endpoint = `product${sendNotificationToAll ? '?sendNotificationToAll=true' : ''}`;
    const productResponse = await api.post<{ result: Product }>(endpoint, baseProductData);
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
          primary: index === 0,
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
  
  // Lấy danh sách combinations (ProductCombinationResponse) dưới dạng phân trang
  getProductCombinations: async (
    productId: number,
    params?: { page?: number; pageSize?: number; sortBy?: string; sortDirection?: 'ASC' | 'DESC' }
  ): Promise<PagedResponse<ProductCombinationResponse>> => {
    type FullApiResponse = { result: PagedResponse<ProductCombinationResponse> };
    const payload = {
      productId1: productId,
      page: params?.page ?? 1,
      pageSize: params?.pageSize ?? 100,
      sortBy: params?.sortBy ?? 'combinationId',
      sortDirection: params?.sortDirection ?? 'ASC',
    };
    const response = await api.post<FullApiResponse>('product-combinations/list', payload);
    return response.result;
  },

  // Tạo product combination
  createProductCombination: async (payload: ProductCombinationCreatePayload): Promise<ProductCombinationResponse> => {
    type ApiResponse = { result: ProductCombinationResponse };
    const response = await api.post<ApiResponse>('product-combinations', payload);
    return response.result;
  },

  // Cập nhật product combination
  updateProductCombination: async (id: number, payload: ProductCombinationCreatePayload): Promise<ProductCombinationResponse> => {
    type ApiResponse = { result: ProductCombinationResponse };
    const response = await api.put<ApiResponse>(`product-combinations/${id}`, payload);
    return response.result;
  },

  // Xóa product combination
  deleteProductCombination: async (id: number): Promise<boolean> => {
    type ApiResponse = { result: boolean };
    const response = await api.delete<ApiResponse>(`product-combinations/${id}`);
    return response.result;
  },

  // Lấy product combination theo ID
  getProductCombinationById: async (id: number): Promise<ProductCombinationResponse> => {
    type ApiResponse = { result: ProductCombinationResponse };
    const response = await api.get<ApiResponse>(`product-combinations/${id}`);
    return response.result;
  },
  // Lấy danh sách ID sản phẩm đính kèm (legacy)
  getRelatedProductIds: async (productId: number): Promise<number[]> => {
    type ListResp = { result: ProductCombinationResponse[] };
    const filterPayload = { productId1: productId, page: 1, pageSize: 100 };
    const resp = await api.post<ListResp>('product-combinations/list', filterPayload);
    const combos = resp.result || [];
    return combos.map(c => (c.productId1 === productId ? c.productId2 : c.productId1));
  },

  // Lấy danh sách sản phẩm đính kèm (ProductCombination) dưới dạng phân trang
  getAttachedProductsPaged: async (
    productId: number,
    params?: { page?: number; pageSize?: number; sortBy?: string; sortDirection?: 'ASC' | 'DESC' }
  ): Promise<PagedResponse<Product>> => {
    type FullApiResponse = { result: PagedResponse<Product> };
    const payload = {
      productId1: productId,
      page: params?.page ?? 1,
      pageSize: params?.pageSize ?? 100,
      sortBy: params?.sortBy ?? 'combinationId',
      sortDirection: params?.sortDirection ?? 'ASC',
    };
    const response = await api.post<FullApiResponse>('product-combinations/paging-products', payload);
    return response.result;
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

  getProductSalesStatistics: async (productId: number, params?: {branchId?:number; months?:number;}): Promise<ProductSalesStatistics> => {
    type RawStatistic = {
      sales: { year: number; month: number; quantity: number }[];
      stockNumber: number;
      stockLevel: string;
      expDate?: string;
    };
    type ApiResp = { result: RawStatistic; message: string; code:number };
    const res = await api.get<ApiResp>(`product/${productId}/sales-statistics`, {
      branchId: params?.branchId,
      months: params?.months ?? 6,
    });

    const raw = res.result;
    const monthlySales = (raw.sales || []).map((s) => ({
      ...s,
      monthLabel: `${s.month}/${String(s.year).slice(2)}`,
    }));

    return {
      monthlySales,
      stockNumber: raw.stockNumber,
      stockLevel: raw.stockLevel,
      earliestExpDate: raw.expDate,
    };
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
  addProductImages: async (productId: number, imageUrls: string[]): Promise<void> => {
    if (!imageUrls || imageUrls.length === 0) {
      return;
    }
    const imageAddPromises = imageUrls.map((url, index) => {
      const imagePayload = {
        imageUrl: url,
        sortOrder: index + 1,
        primary: index === 0,
        productId,
      };
      return api.post('image', imagePayload);
    });
    await Promise.all(imageAddPromises);
  },
};