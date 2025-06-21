// File: src/types/product.ts

// Wang Code Web: Đảm bảo không có dòng `export default` nào ở đây.
// Chúng ta sử dụng `export` cho từng interface/type để có thể import theo tên.

export interface Supplier {
  supplierId: number;
  name: string;
  address: string;
  contact: string;
  image: string | null;
}

export interface Category {
  categoryId: number;
  name: string;
  description: string;
  parentCategory: Category | null;
}

export interface Product {
  productId: number;
  name: string;
  price: number;
  discountPercentage: number;
  discountTriggerDays: number;
  unit: string;
  netWeight: number | null;
  specification: string;
  description: string;
  createdAt: string | null;
  totalRevenue: number;
  supplier: Supplier;
  category: Category;
  inventoryProductResponses: any[] | null;
  orderDetails: any[] | null;
  feedbackResponses: any[] | null;
  productImageResponses: any[] | null;
  userBehaviorResponses: any[] | null;
  notificationResponses: any[] | null;
  trendingProductResponses: any[] | null;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  result: T;
}

export type ProductCreatePayload = {
  name: string;
  price: number;
  unit: string;
  specification: string;
  description: string;
  totalRevenue: number;
  categoryId: number;
  supplierId: number;
};

export type ProductImagePayload = {
  imageUrl: String[];
  sortOrder: number;
  productId: number;
};

export type CategoryPayload = {
  name: string;
  description?: string;
  parentCategoryId?: number | null; 
};

export type SupplierPayload = {
  name: string;
  address: string;
  contact: string;
  image?: string | null;
};

