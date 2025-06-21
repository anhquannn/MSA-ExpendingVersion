
import {api} from './apiService'; 

export interface Category {
  categoryId: number;
  name: string;
  description: string;
  parentCategory: Category | null; 
}

export interface PagedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; 

}

export interface CategoryPagingParams {
  name?: string;
  parentId?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page?: number;
  pageSize?: number;
}

export interface CreateCategoryPayload {
  name: string;
  description: string;
  parentCategoryId?: number; 
}

export interface UpdateCategoryPayload {
  name: string;
  description: string;
}

export const categoryService = {

   getCategories: async (params: CategoryPagingParams): Promise<PagedResponse<Category>> => {
    // 1. Định nghĩa kiểu cho toàn bộ response mà API thực sự trả về
    type FullApiResponse = {
      code: number;
      message: string;
      result: PagedResponse<Category>; // Dữ liệu chúng ta cần nằm trong `result`
    };

    // 2. Gọi API và nhận về response đầy đủ, lồng nhau
    const fullResponse = await api.post<FullApiResponse>('category/paging', params);

    // 3. "Mở gói" và chỉ trả về phần `result`, đúng với kiểu dữ liệu PagedResponse<Category>
    return fullResponse.result;
  },

  // createCategory: (payload: CreateCategoryPayload): Promise<{ result: Category }> => {
  //   return api.post<{ result: Category }>('category', payload);
  // },
  createCategory: async (payload: CreateCategoryPayload): Promise<Category> => {
    // API trả về { result: Category }, ta sẽ "mở gói" nó
    const response = await api.post<{ result: Category }>('category', payload);
    return response.result;
  },

  // updateCategory: (categoryId: number, payload: UpdateCategoryPayload): Promise<Category> => {
  //   // Sử dụng template literal để tạo URL động
  //   return api.put<Category>(`category/${categoryId}`, payload);
  // },

    updateCategory: async (categoryId: number, payload: UpdateCategoryPayload): Promise<Category> => {
    // Giả định API update cũng trả về { result: Category } để nhất quán
    // Nếu nó trả về thẳng Category, bạn chỉ cần return api.put(...)
    const response = await api.put<{ result: Category }>(`category/${categoryId}`, payload);
    return response.result;
  },

  deleteCategory: (categoryId: number): Promise<void> => {
    return api.delete<void>(`category/${categoryId}`);
  },
};