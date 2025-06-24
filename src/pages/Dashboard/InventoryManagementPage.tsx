// File: src/pages/inventory/InventoryManagementSingleFile.tsx
// (Đã cập nhật để khớp với API response mới)

import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useDebounce } from 'use-debounce';
import { inventoryProductService, InventoryProductFilterParams, InventoryProductUpdatePayload ,InventoryProduct} from '../../services/inventoryProductService';
import { AlertTriangle, Edit, LoaderCircle, Search, X } from 'lucide-react';
import { ApiResponse } from '../../models';

// =================================================================
// INTERFACES (Cập nhật để khớp với API response mới)
// =================================================================

interface ProductImageResponse {
  productImageId: number;
  imageUrl: string;
  sortOrder: number;
  primary: boolean;
}

interface Category {
  categoryId: number;
  name: string;
  description: string;
  parentCategory: Category | null;
}

interface Supplier {
    supplierId: number;
    name: string;
    address: string;
    contact: string;
    image: string | null;
}

interface Product {
  productId: number;
  name: string;
  price: number;
  unit: string;
  productImageResponses: ProductImageResponse[] | null;
  category: Category;
  supplier: Supplier;
  // Các trường khác không dùng đến trong UI có thể bỏ qua để interface gọn hơn
}

interface Inventory {
  inventoryId: number;
  name: string;
}

interface Props {
  branchId: number;
}
export function InventoryManagementSingleFile({ branchId }: Props) {
  // --- STATE MANAGEMENT (Không thay đổi) ---
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState<Omit<InventoryProductFilterParams, 'inventoryId'>>({
    page: 1,
    pageSize: 10,
    sortBy: 'stockNumber',
    sortDirection: 'DESC',
    keyword: '',
  });
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearchTerm] = useDebounce(searchTerm, 500);
  const [editingProduct, setEditingProduct] = useState<InventoryProduct | null>(null);
  const [newStockValue, setNewStockValue] = useState<number>(0);

  // --- DATA FETCHING & MUTATION (Không thay đổi logic) ---
  const queryParams: InventoryProductFilterParams = { ...filters, inventoryId: branchId, keyword: debouncedSearchTerm };
  
  // const { data: products, isLoading, isError, error } = useQuery({
  //   queryKey: ['inventoryProducts', queryParams],
  //   queryFn: () => inventoryProductService.getInventoryProductList(queryParams) as Promise<InventoryProduct[]>, // Ép kiểu để an toàn hơn
  //   placeholderData: (previousData) => previousData,
  // });
const { 
    data: products, // `products` giờ đây sẽ là kiểu InventoryProduct[]
    isLoading, 
    isError, 
    error 
} = useQuery<
    ApiResponse<InventoryProduct[]>, // 1. Kiểu dữ liệu mà queryFn trả về
    Error,                         // 2. Kiểu dữ liệu của lỗi
    InventoryProduct[]             // 3. Kiểu dữ liệu cuối cùng mà `data` sẽ nhận (SAU KHI SELECT)
>({
    queryKey: ['inventoryProducts', queryParams],
    queryFn: () => inventoryProductService.getInventoryProductList(queryParams), // 4. Bỏ ép kiểu 'as' nguy hiểm
    
    // 5. Thêm option `select` để trích xuất và biến đổi dữ liệu
    select: (response) => {
        // `response` ở đây là object ApiResponse đầy đủ
        // Chúng ta chỉ trả về mảng `result` mà component cần
        return response.result; 
    },

    placeholderData: (previousData) => previousData,
    // ... các options khác nếu có
});
  const updateStockMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number, payload: InventoryProductUpdatePayload }) =>
      inventoryProductService.updateInventoryProduct(id, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventoryProducts'] });
      handleCloseModal();
    },
    onError: (err) => {
      console.error("Lỗi khi cập nhật tồn kho:", err);
    }
  });

  // --- EFFECTS & HANDLERS (Không thay đổi logic) ---
  useEffect(() => {
    handleUpdateFilters({ keyword: debouncedSearchTerm });
  }, [debouncedSearchTerm]);
  
  useEffect(() => {
    if (editingProduct) {
      setNewStockValue(editingProduct.stockNumber);
    }
  }, [editingProduct]);

  const handleUpdateFilters = (newFilters: Partial<typeof filters>) => {
    setFilters(prev => ({ ...prev, ...newFilters, page: 1 }));
  };
  const handleEditClick = (product: InventoryProduct) => setEditingProduct(product);
  const handleCloseModal = () => setEditingProduct(null);

  const handleEditFormSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingProduct) return;
    const payload: InventoryProductUpdatePayload = {
      stockNumber: newStockValue,
      inventoryId: editingProduct.inventory.inventoryId,
      productId: editingProduct.product.productId,
      stockLevel: 'medium'
    };
    updateStockMutation.mutate({ id: editingProduct.inventoryProductId, payload });
  };

  // --- HELPERS (Không thay đổi) ---
  const formatCurrency = (value: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  const getStockLevelClass = (level: string) => {
    if (level === 'low') return 'bg-red-100 text-red-800';
    if (level === 'high') return 'bg-green-100 text-green-800';
    return 'bg-yellow-100 text-yellow-800';
  };

  // =================================================================
  // RENDER JSX
  // =================================================================
  return (
    <div className="p-4 md:p-6 lg:p-8 bg-gray-50 min-h-screen font-sans">
      <header className="mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Quản lý Tồn kho - Chi nhánh #{branchId}</h1>
        <p className="text-gray-500 mt-1">Xem, tìm kiếm, và quản lý sản phẩm trong kho.</p>
      </header>
      
      {/* SECTION: FILTERS (Không thay đổi) */}
      <div className="bg-white p-4 rounded-lg shadow-md mb-6">
        {/* ... JSX của Filters giữ nguyên ... */}
        <div className="flex flex-col md:flex-row gap-4 items-center">
            <div className="relative w-full md:w-1/3">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input type="text" placeholder="Tìm theo tên sản phẩm..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none" />
            </div>
            <div className="flex items-center gap-2 w-full md:w-auto">
                <label htmlFor="sortBy" className="text-gray-600 text-sm">Sắp xếp:</label>
                <select id="sortBy" value={filters.sortBy} onChange={(e) => handleUpdateFilters({ sortBy: e.target.value })} className="border rounded-lg px-2 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none">
                    <option value="product.name">Tên sản phẩm</option>
                    <option value="stockNumber">Số lượng tồn</option>
                    <option value="currentPrice">Giá bán</option>
                </select>
                <select value={filters.sortDirection} onChange={(e) => handleUpdateFilters({ sortDirection: e.target.value as 'ASC' | 'DESC' })} className="border rounded-lg px-2 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none">
                    <option value="ASC">Tăng dần</option>
                    <option value="DESC">Giảm dần</option>
                </select>
            </div>
        </div>
      </div>

      {/* SECTION: DATA TABLE */}
      <main className="bg-white rounded-lg shadow-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Sản phẩm</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Danh mục</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Giá bán</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Tồn kho</th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Hành động</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {isLoading ? (
                <tr><td colSpan={6} className="text-center p-8"><div className="flex justify-center items-center"><LoaderCircle className="w-6 h-6 animate-spin text-blue-500" /><p className="ml-2">Đang tải...</p></div></td></tr>
              ) : isError ? (
                <tr><td colSpan={6} className="text-center p-8"><div className="flex justify-center items-center text-red-600"><AlertTriangle className="w-6 h-6 mr-2" /><p>Lỗi: {error.message}</p></div></td></tr>
              ) : products?.length === 0 ? (
                <tr><td colSpan={6} className="text-center p-8 text-gray-500">Không tìm thấy sản phẩm nào.</td></tr>
              ) : (
                products?.map((item) => {
                  {/* --- THAY ĐỔI LOGIC LẤY HÌNH ẢNH Ở ĐÂY --- */}
                  const imageUrl = item.product.productImageResponses?.[0]?.imageUrl || '/placeholder.png';

                  return (
                    <tr key={item.inventoryProductId} className="hover:bg-gray-50">
                      <td className="px-6 py-4">
                        <div className="flex items-center">
                          <div className="flex-shrink-0 h-12 w-12">
                            <img className="h-12 w-12 rounded-md object-cover" src={imageUrl} alt={item.product.name} />
                          </div>
                          <div className="ml-4">
                            <div className="text-sm font-medium text-gray-900">{item.product.name}</div>
                            <div className="text-sm text-gray-500">ID: {item.product.productId}</div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{item.product.category.name}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-900">{formatCurrency(item.currentPrice)}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-semibold text-gray-900">{item.stockNumber} {item.product.unit}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-center"><span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStockLevelClass(item.stockLevel)}`}>{item.stockLevel}</span></td>
                      <td className="px-6 py-4 whitespace-nowrap text-center text-sm font-medium"><button onClick={() => handleEditClick(item)} className="text-blue-600 hover:text-blue-900 p-2 rounded-full hover:bg-blue-100 transition-colors"><Edit className="w-5 h-5" /></button></td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        </div>
      </main>

      {/* SECTION: EDIT MODAL (Không thay đổi) */}
      {editingProduct && (
        <div className="fixed inset-0 bg-black bg-opacity-60 z-50 flex justify-center items-center transition-opacity">
            {/* ... JSX của Modal giữ nguyên ... */}
            <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md relative transform transition-all" role="dialog" aria-modal="true">
                <button onClick={handleCloseModal} className="absolute top-3 right-3 text-gray-400 hover:text-gray-600"><X className="w-6 h-6" /></button>
                <h2 className="text-2xl font-bold mb-4 text-gray-800">Cập nhật Tồn kho</h2>
                <p className="mb-1 text-gray-700">Sản phẩm: <span className="font-semibold">{editingProduct.product.name}</span></p>
                <p className="mb-4 text-gray-500">Tồn kho hiện tại: {editingProduct.stockNumber}</p>
                <form onSubmit={handleEditFormSubmit}>
                    <label htmlFor="stockNumber" className="block text-sm font-medium text-gray-700 mb-1">Số lượng tồn mới</label>
                    <input id="stockNumber" type="number" value={newStockValue} onChange={(e) => setNewStockValue(Number(e.target.value))} className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500" required />
                    <div className="mt-6 flex justify-end gap-3">
                        <button type="button" onClick={handleCloseModal} className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors">Hủy</button>
                        <button type="submit" disabled={updateStockMutation.isPending} className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-blue-400 disabled:cursor-not-allowed flex items-center transition-colors">
                            {updateStockMutation.isPending && <LoaderCircle className="w-4 h-4 mr-2 animate-spin" />}
                            {updateStockMutation.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
      )}
    </div>
  );
}

