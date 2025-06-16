// src/services/inventoryService.ts

import { Product, mockProducts, mockBranches, BranchStock } from '../types/inventory';

// Giả lập ID tự tăng
let nextProductId = mockProducts.length + 1;

// Thời gian chờ để giả lập gọi API
const simulateDelay = (ms: number) => new Promise(res => setTimeout(res, ms));

interface PaginationParams {
  page: number;
  limit: number;
  search?: string;
}

interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
}

// Hàm giả lập API (Mock API)
const inventoryService = {
  // Lấy danh sách sản phẩm có phân trang và tìm kiếm
  async getProducts({ page, limit, search }: PaginationParams): Promise<PaginatedResponse<Product>> {
    await simulateDelay(500); // Giả lập độ trễ

    let filteredProducts = [...mockProducts];

    if (search) {
      const lowercasedSearch = search.toLowerCase();
      filteredProducts = filteredProducts.filter(
        product =>
          product.name.toLowerCase().includes(lowercasedSearch) ||
          product.id.toLowerCase().includes(lowercasedSearch) ||
          product.category.toLowerCase().includes(lowercasedSearch)
      );
    }

    const startIndex = (page - 1) * limit;
    const endIndex = startIndex + limit;
    const paginatedProducts = filteredProducts.slice(startIndex, endIndex);

    return {
      data: paginatedProducts,
      total: filteredProducts.length,
      page,
      limit,
    };
  },

  // Lấy một sản phẩm theo ID
  async getProductById(id: string): Promise<Product | undefined> {
    await simulateDelay(300);
    return mockProducts.find(p => p.id === id);
  },

  // Thêm sản phẩm mới
  async addProduct(product: Omit<Product, 'id' | 'branchStocks'> & { branchStocks: { branchId: string; stock: number; }[] }): Promise<Product> {
    await simulateDelay(500);
    const newProduct: Product = {
      ...product,
      id: `P${String(nextProductId++).padStart(3, '0')}`, // Tạo ID mới
      // Map branchStocks từ input đơn giản sang cấu trúc đầy đủ
      branchStocks: product.branchStocks.map(bs => {
        const branchInfo = mockBranches.find(b => b.id === bs.branchId);
        return {
          branchId: bs.branchId,
          branchName: branchInfo ? branchInfo.name : 'Unknown Branch',
          stock: bs.stock,
        };
      }),
    };
    mockProducts.push(newProduct);
    return newProduct;
  },

  // Cập nhật sản phẩm
  async updateProduct(id: string, updatedFields: Partial<Omit<Product, 'id'> & { branchStocks: { branchId: string; stock: number; }[] }>): Promise<Product> {
    await simulateDelay(500);
    const index = mockProducts.findIndex(p => p.id === id);
    if (index === -1) {
      throw new Error('Product not found');
    }

    const currentProduct = mockProducts[index];
    const newBranchStocks: BranchStock[] = updatedFields.branchStocks
      ? updatedFields.branchStocks.map(bs => {
          const branchInfo = mockBranches.find(b => b.id === bs.branchId);
          return {
            branchId: bs.branchId,
            branchName: branchInfo ? branchInfo.name : 'Unknown Branch',
            stock: bs.stock,
          };
        })
      : currentProduct.branchStocks; // Giữ nguyên nếu không được cập nhật

    const updatedProduct = {
      ...currentProduct,
      ...updatedFields,
      branchStocks: newBranchStocks,
      id: currentProduct.id, // Đảm bảo ID không đổi
    };
    mockProducts[index] = updatedProduct;
    return updatedProduct;
  },

  // Xóa sản phẩm
  async deleteProduct(id: string): Promise<void> {
    await simulateDelay(300);
    const initialLength = mockProducts.length;
    mockProducts.splice(mockProducts.findIndex(p => p.id === id), 1);
    if (mockProducts.length === initialLength) {
      throw new Error('Product not found');
    }
  },

  // Lấy danh sách Categories (loại sản phẩm)
  async getCategories(): Promise<string[]> {
    await simulateDelay(200);
    // Lấy các category duy nhất từ mockProducts
    const categories = Array.from(new Set(mockProducts.map(p => p.category)));
    return ['Tất cả', ...categories].sort(); // Thêm "Tất cả" và sắp xếp
  },

  // Lấy danh sách chi nhánh
  async getBranches(): Promise<{ id: string; name: string }[]> {
    await simulateDelay(200);
    return mockBranches;
  }
};

export default inventoryService;