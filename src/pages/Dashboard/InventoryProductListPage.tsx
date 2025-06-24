// // File: src/pages/Dashboard/InventoryProductListPage.tsx

// import React, { useState, useEffect } from 'react';
// import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
// import { useParams, Link } from 'react-router-dom';

// // Services & Types
// import { inventoryProductService, InventoryProduct, InventoryProductFilterParams, InventoryProductUpdatePayload } from '../../services/inventoryProductService';

// // Icons
// import { AlertTriangle, Edit, LoaderCircle, Search, X, ArrowLeft } from 'lucide-react';
// import { ApiResponse } from '../../models';

// // Custom Hook để Debounce (giảm số lần gọi API khi search)
// function useDebounce<T>(value: T, delay: number): T {
//     const [debouncedValue, setDebouncedValue] = useState<T>(value);
//     useEffect(() => {
//         const handler = setTimeout(() => { setDebouncedValue(value); }, delay);
//         return () => { clearTimeout(handler); };
//     }, [value, delay]);
//     return debouncedValue;
// }

// // === COMPONENT CHÍNH ===
// export function InventoryProductListPage() {
//     // 1. Lấy inventoryId từ URL, đây là tham số đầu vào quan trọng nhất
//     const { inventoryId } = useParams<{ inventoryId: string }>();
//     const numericInventoryId = inventoryId ? parseInt(inventoryId, 10) : 0;

//     // 2. Các state quản lý UI: bộ lọc, tìm kiếm, modal...
//     const queryClient = useQueryClient();
//     const [filters, setFilters] = useState<Omit<InventoryProductFilterParams, 'inventoryId'>>({
//         page: 1,
//         pageSize: 10,
//         sortBy: 'stockNumber',
//         sortDirection: 'DESC',
//     });
//     const [searchTerm, setSearchTerm] = useState('');
//     const debouncedSearchTerm = useDebounce(searchTerm, 500);
//     const [editingProduct, setEditingProduct] = useState<InventoryProduct | null>(null);
//     const [newStockValue, setNewStockValue] = useState<number>(0);

//     // 3. Gọi API lấy danh sách sản phẩm bằng useQuery
//     const queryParams: InventoryProductFilterParams = { 
//         ...filters, 
//         inventoryId: numericInventoryId, 
//         keyword: debouncedSearchTerm 
//     };
    
//     // const { data: products, isLoading, isError, error } = useQuery({
//     //     queryKey: ['inventoryProducts', queryParams], // queryKey phải chứa tất cả tham số để caching hoạt động đúng
//     //     queryFn: () => inventoryProductService.getInventoryProductList(queryParams),
//     //     enabled: !!numericInventoryId, // Chỉ gọi API khi có inventoryId hợp lệ (khác 0)
//     // });
// const { data: response, isLoading, isError, error } = useQuery<ApiResponse<InventoryProduct[]>, Error>({
//         queryKey: ['inventoryProducts', queryParams],
//         queryFn: () => inventoryProductService.getInventoryProductList(queryParams),
//         enabled: !!numericInventoryId,
//     });
//      const products: InventoryProduct[] = response?.result || [];
//     // 4. Mutation để cập nhật sản phẩm
//     const updateStockMutation = useMutation({
//         mutationFn: ({ id, payload }: { id: number, payload: InventoryProductUpdatePayload }) => 
//             inventoryProductService.updateInventoryProduct(id, payload),
//         onSuccess: () => {
//             queryClient.invalidateQueries({ queryKey: ['inventoryProducts', { inventoryId: numericInventoryId }] });
//             handleCloseModal();
//         },
//         onError: (err) => console.error("Lỗi khi cập nhật:", err)
//     });

//     // 5. Các hàm xử lý sự kiện
//     useEffect(() => {
//         if (editingProduct) setNewStockValue(editingProduct.stockNumber);
//     }, [editingProduct]);

//     const handleUpdateFilters = (newFilters: Partial<typeof filters>) => {
//         setFilters(prev => ({ ...prev, ...newFilters, page: 1 }));
//     };
//     const handleEditClick = (product: InventoryProduct) => setEditingProduct(product);
//     const handleCloseModal = () => setEditingProduct(null);
//    const handleEditFormSubmit = (e: React.FormEvent) => {
//         e.preventDefault();
//         if (!editingProduct) return;
        
//         const payload: InventoryProductUpdatePayload = {
//             stockNumber: newStockValue,
//             inventoryId: editingProduct.inventory.inventoryId,
//             productId: editingProduct.product.productId,
//             stockLevel: editingProduct.stockLevel, 
//         };
//         updateStockMutation.mutate({ id: editingProduct.inventoryProductId, payload });
//     };

//     // 6. Các hàm tiện ích
//     const formatCurrency = (value: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
//     const getStockLevelClass = (level: string) => { /* ... */ };

//     // 7. Xử lý trường hợp không có ID (edge case)
//     if (!numericInventoryId) {
//         return (
//             <div className="p-8 text-center">
//                 <h2 className="text-2xl font-bold text-gray-700">ID Kho không hợp lệ</h2>
//                 <Link to="/inventories" className="mt-4 inline-flex items-center px-6 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700">
//                     <ArrowLeft className="w-4 h-4 mr-2" />
//                     Quay lại danh sách kho
//                 </Link>
//             </div>
//         );
//     }

//     // 8. Render giao diện
//     return (
//         <div className="p-4 md:p-6 lg:p-8 bg-gray-50 min-h-screen">
//             <header className="mb-6">
//                 <Link to="/inventories" className="inline-flex items-center text-blue-600 hover:underline mb-2">
//                     <ArrowLeft className="w-4 h-4 mr-1" />
//                     Quay lại danh sách kho
//                 </Link>
//                 <h1 className="text-3xl font-bold text-gray-800">Sản phẩm trong Kho #{numericInventoryId}</h1>
//                 <p className="text-gray-500 mt-1">Danh sách các sản phẩm hiện có trong kho này.</p>
//             </header>
            
//             <div className="bg-white p-4 rounded-lg shadow-md mb-6">
//                 {/* JSX cho Filters */}
//             </div>

//             <main className="bg-white rounded-lg shadow-md overflow-hidden">
//                 <div className="overflow-x-auto">
//                     <table className="min-w-full divide-y divide-gray-200">
//                         {/* thead */}
//                         <tbody className="bg-white divide-y divide-gray-200">
//                             {isLoading && (<tr><td colSpan={6} className="text-center p-8"><LoaderCircle className="w-8 h-8 mx-auto animate-spin text-blue-500" /></td></tr>)}
//                             {isError && (<tr><td colSpan={6} className="text-center p-8 text-red-600">Lỗi: {error.message}</td></tr>)}
//                             {!isLoading && !isError && products?.length === 0 && (<tr><td colSpan={6} className="text-center p-8 text-gray-500">Kho này chưa có sản phẩm nào.</td></tr>)}
//                             {!isLoading && products?.map((item) => {
//                                 const imageUrl = item.product.productImageResponses?.[0]?.imageUrl || '/placeholder.png';
//                                 return (
//                                     <tr key={item.inventoryProductId} className="hover:bg-gray-50">
//                                         {/* td cho tên sản phẩm, ảnh, giá... */}
//                                         <td className="px-6 py-4 whitespace-nowrap text-center">
//                                             <button onClick={() => handleEditClick(item)} className="text-blue-600 hover:text-blue-900 p-2 rounded-full hover:bg-blue-100"><Edit className="w-5 h-5" /></button>
//                                         </td>
//                                     </tr>
//                                 )
//                             })}
//                         </tbody>
//                     </table>
//                 </div>
//             </main>

//             {editingProduct && (
//                 <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center">
//                     {/* JSX cho Modal */}
//                 </div>
//             )}
//         </div>
//     );
// }
// File: src/pages/Dashboard/InventoryProductListPage.tsx
// File: src/pages/inventory/InventoryManagementSingleFile.tsx
// (Đã cập nhật để khớp với API response mới)

import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useDebounce } from 'use-debounce';
import { inventoryProductService, InventoryProductFilterParams, InventoryProductUpdatePayload ,InventoryProduct} from '../../services/inventoryProductService';
import { AlertTriangle, Edit, LoaderCircle, Search, X } from 'lucide-react';
import { ApiResponse } from '../../models';
import { useParams, Link } from 'react-router-dom';
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


export function InventoryProductListPage() {
     const { inventoryId } = useParams<{ inventoryId: string }>();
    const numericInventoryId = inventoryId ? parseInt(inventoryId, 10) : 0;
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
  const queryParams: InventoryProductFilterParams = { ...filters, inventoryId: numericInventoryId, keyword: debouncedSearchTerm };

const { 
    data: products, // `products` giờ đây sẽ là kiểu InventoryProduct[]
    isLoading, 
    isError, 
    error 
} = useQuery<
    ApiResponse<InventoryProduct[]>, 
    Error,                         
    InventoryProduct[]            
>({
    queryKey: ['inventoryProducts', queryParams],
    queryFn: () => inventoryProductService.getInventoryProductList(queryParams),
    
    select: (response) => {
        return response.result; 
    },

    placeholderData: (previousData) => previousData,
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

  const formatCurrency = (value: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  const getStockLevelClass = (level: string) => {
    if (level === 'low') return 'bg-red-100 text-red-800';
    if (level === 'high') return 'bg-green-100 text-green-800';
    return 'bg-yellow-100 text-yellow-800';
  };
  return (
    <div className="p-4 md:p-6 lg:p-8 bg-gray-50 min-h-screen font-sans">
      <header className="mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Quản lý Tồn kho - Chi nhánh #{inventoryId}</h1>
        <p className="text-gray-500 mt-1">Xem, tìm kiếm, và quản lý sản phẩm trong kho.</p>
      </header>
      
      <div className="bg-white p-4 rounded-lg shadow-md mb-6">
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

      {editingProduct && (
        <div className="fixed inset-0 bg-black bg-opacity-60 z-50 flex justify-center items-center transition-opacity">
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

