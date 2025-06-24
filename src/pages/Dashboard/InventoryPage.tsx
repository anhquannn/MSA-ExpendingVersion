// // src/pages/Dashboard/InventoryPage.tsx

// import React, { useState, useEffect, useMemo } from 'react';
// import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
// import { Link, useNavigate } from 'react-router-dom';
// // --- Import các service và types thật ---
// import {
//   inventoryService,
//   Inventory,
//   InventoryListParams,
//   InventoryCreateParams
// } from '../../services/inventoryService';
// import { branchService, Branch } from '../../services/branchService';

// // --- Custom Hook để Debounce ---
// function useDebounce(value: string, delay: number) {
//   const [debouncedValue, setDebouncedValue] = useState(value);
//   useEffect(() => {
//     const handler = setTimeout(() => {
//       setDebouncedValue(value);
//     }, delay);
//     return () => clearTimeout(handler);
//   }, [value, delay]);
//   return debouncedValue;
// }

// const InventoryModal = ({ isOpen, onClose, onSave, initialData }: {
//   isOpen: boolean;
//   onClose: () => void;
//   onSave: (data: InventoryCreateParams) => void;
//   initialData: Partial<InventoryCreateParams> | null;
// }) => {
//   const [formData, setFormData] = useState<Partial<InventoryCreateParams>>({});

//   // Lấy danh sách chi nhánh cho dropdown
//   const { data: branches = [] } = useQuery({
//     queryKey: ['allBranchesForSelect'],
//     queryFn: () => branchService.getAllBranchesWithPaging({ pageSize: 999 }).then(res => res.content),
//   });

//   useEffect(() => {
//     // Khi mở modal, điền dữ liệu có sẵn (cho việc sửa) hoặc reset form
//     setFormData(initialData || { name: '', address: '', contact: '', branchId: undefined });
//   }, [initialData, isOpen]);

//   if (!isOpen) return null;

//   const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
//     const { name, value } = e.target;
//     setFormData(prev => ({ ...prev, [name]: name === 'branchId' ? Number(value) : value }));
//   };

//   const handleSubmit = (e: React.FormEvent) => {
//     e.preventDefault();
//     onSave(formData as InventoryCreateParams);
//   };

//   return (
//     <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
//       <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-xl w-full max-w-lg space-y-4">
//         <h2 className="text-xl font-bold">{initialData?.name ? 'Sửa Kho Hàng' : 'Thêm Kho Hàng Mới'}</h2>
//         <input name="name" value={formData.name || ''} onChange={handleChange} placeholder="Tên kho (*)" required className="w-full p-2 border rounded-md" />
//         <select name="branchId" value={formData.branchId || ''} onChange={handleChange} required className="w-full p-2 border rounded-md">
//           <option value="">Chọn chi nhánh (*)</option>
//           {branches.map(b => <option key={b.branchId} value={b.branchId}>{b.name}</option>)}
//         </select>
//         <input name="address" value={formData.address || ''} onChange={handleChange} placeholder="Địa chỉ" className="w-full p-2 border rounded-md" />
//         <input name="contact" value={formData.contact || ''} onChange={handleChange} placeholder="Thông tin liên hệ" className="w-full p-2 border rounded-md" />
//         <div className="flex justify-end space-x-2">
//           <button type="button" onClick={onClose} className="px-4 py-2 bg-gray-300 rounded-md">Hủy</button>
//           <button type="submit" className="px-4 py-2 bg-blue-500 text-white rounded-md">Lưu</button>
//         </div>
//       </form>
//     </div>
//   );
// };


// const InventoryManagementPage: React.FC = () => {
//   const queryClient = useQueryClient();

//   // --- STATE CHO BỘ LỌC VÀ UI ---
//   const [filters, setFilters] = useState<InventoryListParams>({
//     keyword: '',
//     branchId: undefined,
//     sortBy: 'inventoryId',
//     sortDirection: 'ASC',
//   });
//   const [isModalOpen, setIsModalOpen] = useState(false);
//   const [editingInventory, setEditingInventory] = useState<Partial<InventoryCreateParams> | null>(null);

//   const debouncedKeyword = useDebounce(filters.keyword || '', 500);

//   // --- API CALLS VỚI REACT QUERY ---

//   // Lấy danh sách kho hàng (đã được lọc)
//   const { data: inventories = [], isLoading, isError, error } = useQuery({
//     queryKey: ['inventories', { ...filters, keyword: debouncedKeyword }],
//     queryFn: () => inventoryService.getAllInventories({ ...filters, keyword: debouncedKeyword }),
//   });

//   // Lấy danh sách chi nhánh cho dropdown bộ lọc
//   const { data: branchesForFilter = [] } = useQuery({
//     queryKey: ['allBranchesForFilter'],
//     queryFn: () => branchService.getAllBranchesWithPaging({ pageSize: 999 }).then(res => res.content),
//   });

//   // --- MUTATIONS CHO CÁC HÀNH ĐỘNG ---

//   const createInventoryMutation = useMutation({
//     mutationFn: (payload: InventoryCreateParams) => inventoryService.createInventory(payload),
//     onSuccess: () => {
//       alert('Thêm kho thành công!');
//       queryClient.invalidateQueries({ queryKey: ['inventories'] });
//       setIsModalOpen(false);
//     },
//     onError: (err: Error) => alert(`Lỗi: ${err.message}`),
//   });

//   const updateInventoryMutation = useMutation({
//     mutationFn: ({ id, payload }: { id: number, payload: InventoryCreateParams }) => inventoryService.updateInventory(id, payload),
//     onSuccess: () => {
//       alert('Cập nhật kho thành công!');
//       queryClient.invalidateQueries({ queryKey: ['inventories'] });
//       setIsModalOpen(false);
//     },
//     onError: (err: Error) => alert(`Lỗi: ${err.message}`),
//   });

//   const deleteInventoryMutation = useMutation({
//     mutationFn: (id: number) => inventoryService.deleteInventory(id),
//     onSuccess: () => {
//       alert('Xóa kho thành công!');
//       queryClient.invalidateQueries({ queryKey: ['inventories'] });
//     },
//     onError: (err: Error) => alert(`Lỗi: ${err.message}`),
//   });

//   // --- EVENT HANDLERS ---
//   const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
//     const { name, value } = e.target;
//     setFilters(prev => ({ ...prev, [name]: value === '' ? undefined : value }));
//   };

//   const handleOpenAddModal = () => {
//     setEditingInventory(null);
//     setIsModalOpen(true);
//   };

//   const handleOpenEditModal = (inventory: Inventory) => {
//     setEditingInventory({
//       name: inventory.name,
//       address: inventory.address,
//       contact: inventory.contact,
//     });
//     setIsModalOpen(true);
//   };

//   const handleSave = (formData: InventoryCreateParams) => {
//     if (editingInventory && (editingInventory as any).inventoryId) {
//       updateInventoryMutation.mutate({ id: (editingInventory as any).inventoryId, payload: formData });
//     } else {
//       formData.totalRevenue = 1;
//       createInventoryMutation.mutate(formData);
//     }
//   };

//   const handleDelete = (inventory: Inventory) => {
//     if (window.confirm(`Bạn có chắc muốn xóa kho "${inventory.name}"?`)) {
//       deleteInventoryMutation.mutate(inventory.inventoryId);
//     }
//   };
//   const branchMap = useMemo(() => {
//     const map = new Map<number, string>();
//     branchesForFilter.forEach(branch => {
//       map.set(branch.branchId, branch.name);
//     });
//     return map;
//   }, [branchesForFilter]);
//   return (
//     <div className="bg-white p-6 rounded-lg shadow-md">
//       <h2 className="text-2xl font-semibold text-gray-700 mb-4">Quản lý Kho Hàng</h2>

//       <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
//         <input type="text" name="keyword" placeholder="Tìm theo tên kho..." value={filters.keyword} onChange={handleFilterChange} className="p-2 border rounded-md" />
//         <select name="branchId" value={filters.branchId || ''} onChange={handleFilterChange} className="p-2 border rounded-md">
//           <option value="">Tất cả chi nhánh</option>
//           {branchesForFilter.map(b => <option key={b.branchId} value={b.branchId}>{b.name}</option>)}
//         </select>
//         <button onClick={handleOpenAddModal} className="bg-green-600 text-white font-bold py-2 px-4 rounded-md hover:bg-green-700 transition h-full">
//           + Thêm Kho Hàng
//         </button>
//       </div>

//       {isLoading && <p>Đang tải dữ liệu...</p>}
//       {isError && <p className="text-red-500">Lỗi: {error.message}</p>}

//       <div className="overflow-x-auto">
//         <table className="min-w-full bg-white border">
//           <thead className="bg-gray-100">
//             <tr>
//               <th className="py-3 px-6 text-left">ID Kho</th>
//               <th className="py-3 px-6 text-left">Tên Kho</th>
//               {/* <th className="py-3 px-6 text-left">Chi Nhánh</th> */}
//               <th className="py-3 px-6 text-left">Địa chỉ</th>
//               <th className="py-3 px-6 text-center">Hành Động</th>
//             </tr>
//           </thead>
//           <tbody className="text-gray-600 text-sm">
//             {inventories.map((inv) => (
//               <tr key={inv.inventoryId} className="border-b hover:bg-gray-50">
//                 <td className="py-3 px-6">{inv.inventoryId}</td>
//                 {/* <td className="py-3 px-6 font-medium">{inv.name}</td> */}
//                 <Link
//                   to={`/inventories/${inv.inventoryId}`}
//                   className="text-blue-600 hover:text-blue-800 hover:underline"
//                   title={`Xem sản phẩm trong kho ${inv.name}`}
//                 >
//                   {inv.name}
//                 </Link>
//                 {/* <td className="py-3 px-6">{inv.branch?.name || 'Chưa có'}</td> */}
//                 {/* <td className="py-3 px-6">{branchMap.get(inv.branchId) || <span className="text-gray-400">Chưa có</span>}</td> */}
//                 <td className="py-3 px-6">{inv.address}</td>
//                 <td className="py-3 px-6 text-center">
//                   <button onClick={() => handleOpenEditModal(inv)} className="text-yellow-600 hover:underline mr-4">Sửa</button>
//                   {/* <button onClick={() => handleDelete(inv)} disabled={deleteInventoryMutation.isPending} className="text-red-600 hover:underline disabled:text-gray-400">Xóa</button> */}
//                 </td>
//               </tr>
//             ))}
//             {inventories.length === 0 && !isLoading && (
//               <tr><td colSpan={5} className="text-center py-4">Không có dữ liệu.</td></tr>
//             )}
//           </tbody>
//         </table>
//       </div>
//       <InventoryModal
//         isOpen={isModalOpen}
//         onClose={() => setIsModalOpen(false)}
//         onSave={handleSave}
//         initialData={editingInventory}
//       />
//     </div>
//   );
// };

// export default InventoryManagementPage;

// src/pages/Dashboard/InventoryPage.tsx

import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';

// Import các service và types thật
import { 
  inventoryService, 
  Inventory, 
  InventoryListParams,
  InventoryCreateParams
} from '../../services/inventoryService';
import { branchService, Branch } from '../../services/branchService';

// Custom Hook để Debounce
function useDebounce(value: string, delay: number) {
  const [debouncedValue, setDebouncedValue] = useState(value);
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);
    return () => clearTimeout(handler);
  }, [value, delay]);
  return debouncedValue;
}

// Modal để Thêm/Sửa Kho
const InventoryModal = ({ isOpen, onClose, onSave, initialData }: {
    isOpen: boolean;
    onClose: () => void;
    onSave: (data: InventoryCreateParams) => void;
    initialData: Partial<Inventory> | null;
}) => {
    const [formData, setFormData] = useState<Partial<InventoryCreateParams>>({});
    const { data: branches = [] } = useQuery<Branch[]>({
        queryKey: ['allBranchesForSelect'],
        queryFn: () => branchService.getAllBranchesWithPaging({ pageSize: 999 }).then(res => res.content),
    });

    useEffect(() => {
        // Điền dữ liệu cho việc sửa, bao gồm cả branchId nếu có
        setFormData(initialData || { name: '', address: '', contact: '', branchId: undefined });
    }, [initialData, isOpen]);

    if (!isOpen) return null;

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: name === 'branchId' ? Number(value) : value }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSave(formData as InventoryCreateParams);
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
            <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-xl w-full max-w-lg space-y-4">
                <h2 className="text-xl font-bold">{initialData?.inventoryId ? 'Sửa Kho Hàng' : 'Thêm Kho Hàng Mới'}</h2>
                <input name="name" value={formData.name || ''} onChange={handleChange} placeholder="Tên kho (*)" required className="w-full p-2 border rounded-md" />
                <select name="branchId" value={formData.branchId || ''} onChange={handleChange} required className="w-full p-2 border rounded-md">
                    <option value="">Chọn chi nhánh (*)</option>
                    {branches.map(b => <option key={b.branchId} value={b.branchId}>{b.name}</option>)}
                </select>
                <input name="address" value={formData.address || ''} onChange={handleChange} placeholder="Địa chỉ" className="w-full p-2 border rounded-md" />
                <input name="contact" value={formData.contact || ''} onChange={handleChange} placeholder="Thông tin liên hệ" className="w-full p-2 border rounded-md" />
                <div className="flex justify-end space-x-2">
                    <button type="button" onClick={onClose} className="px-4 py-2 bg-gray-300 rounded-md">Hủy</button>
                    <button type="submit" className="px-4 py-2 bg-blue-500 text-white rounded-md">Lưu</button>
                </div>
            </form>
        </div>
    );
};


// Component chính: Trang Quản lý Kho Hàng
export const InventoryListPage: React.FC = () => {
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState<Omit<InventoryListParams, 'branchId'>>({ keyword: '', sortBy: 'inventoryId', sortDirection: 'ASC' });
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingInventory, setEditingInventory] = useState<Partial<Inventory> | null>(null);
  const debouncedKeyword = useDebounce(filters.keyword || '', 500);

  const { data: inventories = [], isLoading, isError, error } = useQuery<Inventory[]>({
    queryKey: ['inventories', { ...filters, keyword: debouncedKeyword }],
    queryFn: () => inventoryService.getAllInventories({ ...filters, keyword: debouncedKeyword }),
  });
  
  const createInventoryMutation = useMutation({
    mutationFn: (payload: InventoryCreateParams) => inventoryService.createInventory(payload),
    onSuccess: () => {
      alert('Thêm kho thành công!');
      queryClient.invalidateQueries({ queryKey: ['inventories'] });
      setIsModalOpen(false);
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });
  
  const updateInventoryMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number, payload: InventoryCreateParams }) => inventoryService.updateInventory(id, payload),
    onSuccess: () => {
      alert('Cập nhật kho thành công!');
      queryClient.invalidateQueries({ queryKey: ['inventories'] });
      setIsModalOpen(false);
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilters(prev => ({ ...prev, keyword: e.target.value }));
  };
  
  const handleOpenAddModal = () => {
    setEditingInventory(null);
    setIsModalOpen(true);
  };
  const handleOpenEditModal = (inventory: Inventory) => {
    setEditingInventory({
      // inventoryId: inventory.inventoryId,
      name: inventory.name,
      address: inventory.address,
      contact: inventory.contact,
    });
    setIsModalOpen(true);
  };
  
  const handleSave = (formData: InventoryCreateParams) => {
    if (editingInventory && editingInventory.inventoryId) {
      updateInventoryMutation.mutate({ id: editingInventory.inventoryId, payload: formData });
    } else {
      createInventoryMutation.mutate(formData);
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold text-gray-700 mb-4">Quản lý Kho Hàng</h2>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <input type="text" name="keyword" placeholder="Tìm theo tên kho..." value={filters.keyword || ''} onChange={handleFilterChange} className="p-2 border rounded-md md:col-span-2" />
        <button onClick={handleOpenAddModal} className="bg-green-600 text-white font-bold py-2 px-4 rounded-md hover:bg-green-700 transition h-full">
          + Thêm Kho Hàng
        </button>
      </div>

      {isLoading && <p>Đang tải dữ liệu...</p>}
      {isError && <p className="text-red-500">Lỗi: {error?.message}</p>}

      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border">
          <thead className="bg-gray-100">
            <tr>
              <th className="py-3 px-6 text-left">ID Kho</th>
              <th className="py-3 px-6 text-left">Tên Kho</th>
              <th className="py-3 px-6 text-left">Địa chỉ</th>
              <th className="py-3 px-6 text-center">Hành Động</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm">
            {inventories.map((inv) => (
              <tr key={inv.inventoryId} className="border-b hover:bg-gray-50">
                <td className="py-3 px-6">{inv.inventoryId}</td>
                <td className="py-3 px-6 font-medium">
                  <Link 
                    to={`/dashboard/inventories/${inv.inventoryId}`} 
                    className="text-blue-600 hover:text-blue-800 hover:underline"
                    title={`Xem sản phẩm trong kho ${inv.name}`}
                  >
                    {inv.name}
                  </Link>
                </td>
                <td className="py-3 px-6">{inv.address}</td>
                <td className="py-3 px-6 text-center">
                  <button onClick={() => handleOpenEditModal(inv)} className="text-yellow-600 hover:underline mr-4">Sửa</button>
                </td>
              </tr>
            ))}
            {inventories.length === 0 && !isLoading && (
              <tr><td colSpan={4} className="text-center py-4">Không có dữ liệu.</td></tr>
            )}
          </tbody>
        </table>
      </div>
      <InventoryModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onSave={handleSave} initialData={editingInventory} />
    </div>
  );
};

// export default InventoryManagementPage;
// File: src/pages/Dashboard/InventoryProductListPage.tsx

// import React, { useState, useEffect, useCallback } from 'react';
// import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
// import { useParams, Link } from 'react-router-dom';

// // Services & Types
// import { 
//     inventoryProductService, 
//     InventoryProduct, 
//     InventoryProductFilterParams, 
//     InventoryProductUpdatePayload 
// } from '../../services/inventoryProductService';

// // Icons
// import { AlertTriangle, Edit, LoaderCircle, Search, X, ArrowLeft, SlidersHorizontal, RotateCcw } from 'lucide-react';

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
//     // 1. Lấy inventoryId từ URL
//     const { inventoryId } = useParams<{ inventoryId: string }>();
//     const numericInventoryId = inventoryId ? parseInt(inventoryId, 10) : 0;
    
//     // 2. State quản lý: Gộp tất cả bộ lọc vào một object duy nhất
//     const initialFilters: Omit<InventoryProductFilterParams, 'inventoryId'> = {
//         page: 1,
//         pageSize: 10,
//         sortBy: 'stockNumber',
//         sortDirection: 'DESC',
//         keyword: '',
//         minStock: undefined,
//         maxStock: undefined,
//         minPrice: undefined,
//         maxPrice: undefined,
//         active: undefined
//     };
//     const [filters, setFilters] = useState(initialFilters);
//     const [isFilterVisible, setIsFilterVisible] = useState(false);
    
//     // Debounce chỉ áp dụng cho trường 'keyword' để tối ưu UX khi gõ
//     const debouncedKeyword = useDebounce(filters.keyword, 500);

//     // 3. Quản lý Modal chỉnh sửa
//     const [editingProduct, setEditingProduct] = useState<InventoryProduct | null>(null);
//     const [newStockValue, setNewStockValue] = useState<number>(0);

//     // 4. React Query: Lấy và cập nhật dữ liệu
//     const queryClient = useQueryClient();
    
//     const queryParams: InventoryProductFilterParams = { 
//         ...filters, 
//         inventoryId: numericInventoryId, 
//         keyword: debouncedKeyword, // Sử dụng keyword đã được debounce
//     };
    
//     const { data: response, isLoading, isError, error } = useQuery({
//         queryKey: ['inventoryProducts', queryParams], 
//         queryFn: () => inventoryProductService.getInventoryProductList(queryParams),
//         enabled: !!numericInventoryId, 
//         placeholderData: (previousData) => previousData, // Giữ lại data cũ khi loading
//     });
    
//     const products = response?.content || [];
//     const totalPages = response?.totalPages || 0;

//     const updateStockMutation = useMutation({
//         mutationFn: ({ id, payload }: { id: number, payload: InventoryProductUpdatePayload }) => 
//             inventoryProductService.updateInventoryProduct(id, payload),
//         onSuccess: () => {
//             queryClient.invalidateQueries({ queryKey: ['inventoryProducts'] });
//             handleCloseModal();
//         },
//         onError: (err) => console.error("Lỗi khi cập nhật:", err)
//     });

//     // 5. Hàm xử lý sự kiện
//     const handleFilterChange = useCallback((e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
//         const { name, value, type } = e.target;
        
//         let processedValue: string | number | boolean | undefined = value;
//         if (type === 'number') {
//             processedValue = value === '' ? undefined : Number(value);
//         }
//         if (name === 'active') {
//             processedValue = value === '' ? undefined : value === 'true';
//         }

//         setFilters(prev => ({ ...prev, [name]: processedValue, page: 1 }));
//     }, []);

//     const resetFilters = useCallback(() => {
//         setFilters(initialFilters);
//     }, []);

//     const handleEditClick = (product: InventoryProduct) => {
//         setEditingProduct(product);
//         setNewStockValue(product.stockNumber);
//     };
    
//     const handleCloseModal = () => setEditingProduct(null);

//     const handleEditFormSubmit = (e: React.FormEvent) => {
//         e.preventDefault();
//         if (!editingProduct) return;
//         const payload: InventoryProductUpdatePayload = {
//             stockNumber: newStockValue,
//             inventoryId: editingProduct.inventory.inventoryId,
//             productId: editingProduct.product.productId,
//         };
//         updateStockMutation.mutate({ id: editingProduct.inventoryProductId, payload });
//     };

//     // 6. Hàm tiện ích
//     const formatCurrency = (value: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    
//     // 7. Xử lý trường hợp không có ID
//     if (!numericInventoryId) {
//         return (
//             <div className="p-8 text-center">
//                 <h2 className="text-2xl font-bold text-gray-700">ID Kho không hợp lệ</h2>
//                 <Link to="/dashboard/inventories" className="mt-4 inline-flex items-center px-6 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700">
//                     <ArrowLeft className="w-4 h-4 mr-2" />
//                     Quay lại danh sách kho
//                 </Link>
//             </div>
//         );
//     }
    
//     // 8. Render giao diện
//     return (
//         <div className="p-4 md:p-6 lg:p-8 bg-gray-50 min-h-screen">
//             {/* Header */}
//             <header className="mb-6">
//                 <Link to="/dashboard/inventories" className="inline-flex items-center text-blue-600 hover:underline mb-2">
//                     <ArrowLeft className="w-4 h-4 mr-1" />
//                     Quay lại danh sách kho
//                 </Link>
//                 <h1 className="text-3xl font-bold text-gray-800">Sản phẩm trong Kho #{numericInventoryId}</h1>
//                 <p className="text-gray-500 mt-1">Danh sách chi tiết các sản phẩm và số lượng tồn kho.</p>
//             </header>
            
//             {/* Thanh Filter */}
//             <div className="bg-white p-4 rounded-lg shadow-md mb-6">
//                 <div className="flex flex-wrap items-center justify-between gap-4">
//                     <div className="relative flex-grow max-w-lg">
//                         <input
//                             type="text"
//                             name="keyword"
//                             value={filters.keyword}
//                             onChange={handleFilterChange}
//                             placeholder="Tìm kiếm theo tên, SKU sản phẩm..."
//                             className="w-full p-2 pl-10 border rounded-md focus:ring-2 focus:ring-blue-500"
//                         />
//                         <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
//                     </div>
//                     <button 
//                         onClick={() => setIsFilterVisible(!isFilterVisible)} 
//                         className="flex items-center gap-2 p-2 px-4 bg-gray-100 rounded-md hover:bg-gray-200"
//                     >
//                         <SlidersHorizontal className="w-5 h-5" />
//                         <span>Bộ lọc</span>
//                     </button>
//                 </div>

//                 {isFilterVisible && (
//                     <div className="mt-4 pt-4 border-t border-gray-200">
//                         <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
//                             {/* Filter by Stock */}
//                             <div>
//                                 <label className="block text-sm font-medium text-gray-700 mb-1">Tồn kho</label>
//                                 <div className="flex items-center gap-2">
//                                     <input type="number" name="minStock" value={filters.minStock ?? ''} onChange={handleFilterChange} placeholder="Min" className="w-full p-2 border rounded-md"/>
//                                     <input type="number" name="maxStock" value={filters.maxStock ?? ''} onChange={handleFilterChange} placeholder="Max" className="w-full p-2 border rounded-md"/>
//                                 </div>
//                             </div>
//                             {/* Filter by Price */}
//                             <div>
//                                 <label className="block text-sm font-medium text-gray-700 mb-1">Giá bán</label>
//                                 <div className="flex items-center gap-2">
//                                     <input type="number" name="minPrice" value={filters.minPrice ?? ''} onChange={handleFilterChange} placeholder="Min" className="w-full p-2 border rounded-md"/>
//                                     <input type="number" name="maxPrice" value={filters.maxPrice ?? ''} onChange={handleFilterChange} placeholder="Max" className="w-full p-2 border rounded-md"/>
//                                 </div>
//                             </div>
//                              {/* Filter by Active Status */}
//                             <div>
//                                 <label htmlFor="active" className="block text-sm font-medium text-gray-700 mb-1">Trạng thái</label>
//                                 <select id="active" name="active" value={filters.active === undefined ? '' : String(filters.active)} onChange={handleFilterChange} className="w-full p-2 border rounded-md bg-white">
//                                     <option value="">Tất cả</option>
//                                     <option value="true">Đang hoạt động</option>
//                                     <option value="false">Ngừng hoạt động</option>
//                                 </select>
//                             </div>
//                             {/* Action Buttons */}
//                             <div className="flex items-end">
//                                 <button onClick={resetFilters} className="flex items-center gap-2 p-2 px-4 bg-red-500 text-white rounded-md hover:bg-red-600 w-full justify-center">
//                                     <RotateCcw className="w-4 h-4"/>
//                                     <span>Reset</span>
//                                 </button>
//                             </div>
//                         </div>
//                     </div>
//                 )}
//             </div>

//             {/* Bảng Dữ liệu */}
//             <main className="bg-white rounded-lg shadow-md overflow-hidden">
//                 <div className="overflow-x-auto">
//                     <table className="min-w-full divide-y divide-gray-200">
//                         <thead className="bg-gray-50">
//                             {/* ... thead của bạn ... */}
//                         </thead>
//                         <tbody className="bg-white divide-y divide-gray-200">
//                             {isLoading && (<tr><td colSpan={7} className="text-center p-8"><LoaderCircle className="w-8 h-8 mx-auto animate-spin text-blue-500" /></td></tr>)}
//                             {isError && (<tr><td colSpan={7} className="text-center p-8 text-red-600">Lỗi: {error.message}</td></tr>)}
//                             {!isLoading && !isError && products.length === 0 && (<tr><td colSpan={7} className="text-center p-8 text-gray-500">Không tìm thấy sản phẩm nào khớp với bộ lọc.</td></tr>)}
//                             {products.map((item) => (
//                                 <tr key={item.inventoryProductId} className="hover:bg-gray-50">
//                                     {/* ... các td hiển thị dữ liệu sản phẩm ... */}
//                                     <td className="px-6 py-4 whitespace-nowrap text-center">
//                                         <button onClick={() => handleEditClick(item)} className="text-blue-600 hover:text-blue-900 p-2 rounded-full hover:bg-blue-100">
//                                             <Edit className="w-5 h-5" />
//                                         </button>
//                                     </td>
//                                 </tr>
//                             ))}
//                         </tbody>
//                     </table>
//                 </div>
//             </main>
            
//             {/* Modal Edit */}
//             {editingProduct && (
//                  <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center">
//                     {/* ... JSX cho Modal của bạn ... */}
//                  </div>
//             )}
//         </div>
//     );
// }
// // File: src/pages/Dashboard/InventoryProductListPage.tsx

// import React, { useState, useEffect, useCallback } from 'react';
// import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
// import { useParams, Link } from 'react-router-dom';

// // Services & Types (Đã được cập nhật ở Bước 1)
// import {
//   inventoryProductService,
//   InventoryProduct,
//   InventoryProductFilterParams,
//   InventoryProductUpdatePayload,
//   // ApiResponse, // Import type mới
// } from '../../services/inventoryProductService';
// import { ApiResponse } from '../../services/apiService';
// // Icons
// import { Edit, LoaderCircle, Search, ArrowLeft, SlidersHorizontal, RotateCcw } from 'lucide-react';

// // Custom Hook để Debounce
// function useDebounce<T>(value: T, delay: number): T {
//     const [debouncedValue, setDebouncedValue] = useState<T>(value);
//     useEffect(() => {
//         const handler = setTimeout(() => { setDebouncedValue(value); }, delay);
//         return () => { clearTimeout(handler); };
//     }, [value, delay]);
//     return debouncedValue;
// }

// // === COMPONENT CHÍNH ===
// export function InventoryPage() {
//     const { inventoryId } = useParams<{ inventoryId: string }>();
//     const numericInventoryId = inventoryId ? parseInt(inventoryId, 10) : 0;
    
//     const initialFilters: Omit<InventoryProductFilterParams, 'inventoryId'> = {
//         page: 1,
//         pageSize: 10,
//         sortBy: 'stockNumber',
//         sortDirection: 'DESC',
//         keyword: '',
//         active: undefined,
//     };
//     const [filters, setFilters] = useState(initialFilters);
//     const [isFilterVisible, setIsFilterVisible] = useState(false);
//     const debouncedKeyword = useDebounce(filters.keyword, 500);

//     const [editingProduct, setEditingProduct] = useState<InventoryProduct | null>(null);
//     const [newStockValue, setNewStockValue] = useState<number>(0);

//     const queryClient = useQueryClient();
    
//     const queryParams: InventoryProductFilterParams = {
//         ...filters,
//         inventoryId: numericInventoryId,
//         keyword: debouncedKeyword,
//     };
    
//     // Sử dụng kiểu `ApiResponse<InventoryProduct[]>` cho useQuery
//     const { data: response, isLoading, isError, error } = useQuery<ApiResponse<InventoryProduct[]>, Error>({
//         queryKey: ['inventoryProducts', queryParams],
//         queryFn: () => inventoryProductService.getInventoryProductList(queryParams),
//         enabled: !!numericInventoryId,
//     });
    
//     // Trích xuất dữ liệu từ `response.result`
//     const products: InventoryProduct[] = response?.result || [];

//     const updateStockMutation = useMutation({
//         mutationFn: ({ id, payload }: { id: number, payload: InventoryProductUpdatePayload }) =>
//             inventoryProductService.updateInventoryProduct(id, payload),
//         onSuccess: () => {
//             queryClient.invalidateQueries({ queryKey: ['inventoryProducts'] });
//             handleCloseModal();
//         },
//         onError: (err) => console.error("Lỗi khi cập nhật:", err),
//     });

//     const handleFilterChange = useCallback((e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
//         const { name, value, type } = e.target;
//         let processedValue: string | number | boolean | undefined = value;
//         if (type === 'number') {
//             processedValue = value === '' ? undefined : Number(value);
//         }
//         if (name === 'active') {
//             processedValue = value === '' ? undefined : value === 'true';
//         }
//         setFilters(prev => ({ ...prev, [name]: processedValue, page: 1 }));
//     }, []);

//     const resetFilters = useCallback(() => setFilters(initialFilters), [initialFilters]);
//     const handleEditClick = (product: InventoryProduct) => {
//         setEditingProduct(product);
//         setNewStockValue(product.stockNumber);
//     };
//     const handleCloseModal = () => setEditingProduct(null);

//     const handleEditFormSubmit = (e: React.FormEvent) => {
//         e.preventDefault();
//         if (!editingProduct) return;
//         const payload: InventoryProductUpdatePayload = {
//           stockNumber: newStockValue,
//           inventoryId: editingProduct.inventory.inventoryId,
//           productId: editingProduct.product.productId,
//           stockLevel: 'medium'
//         };
//         updateStockMutation.mutate({ id: editingProduct.inventoryProductId, payload });
//     };

//     const formatCurrency = (value: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
//         return (
//         <div className="p-4 md:p-6 lg:p-8 bg-gray-50 min-h-screen">
//             {/* Header */}
//             <header className="mb-6">
//                 <Link to="/dashboard/inventories" className="inline-flex items-center text-blue-600 hover:underline mb-2">
//                     <ArrowLeft className="w-4 h-4 mr-1" />
//                     Quay lại danh sách kho
//                 </Link>
//                 <h1 className="text-3xl font-bold text-gray-800">Sản phẩm trong Kho #{numericInventoryId}</h1>
//                 <p className="text-gray-500 mt-1">Danh sách chi tiết các sản phẩm và số lượng tồn kho.</p>
//             </header>
            
//             {/* Thanh Filter */}
//             <div className="bg-white p-4 rounded-lg shadow-md mb-6">
//                 <div className="flex flex-wrap items-center justify-between gap-4">
//                     <div className="relative flex-grow max-w-lg">
//                         <input
//                             type="text"
//                             name="keyword"
//                             value={filters.keyword}
//                             onChange={handleFilterChange}
//                             placeholder="Tìm kiếm theo tên, SKU sản phẩm..."
//                             className="w-full p-2 pl-10 border rounded-md focus:ring-2 focus:ring-blue-500"
//                         />
//                         <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
//                     </div>
//                     <button 
//                         onClick={() => setIsFilterVisible(!isFilterVisible)} 
//                         className="flex items-center gap-2 p-2 px-4 bg-gray-100 rounded-md hover:bg-gray-200"
//                     >
//                         <SlidersHorizontal className="w-5 h-5" />
//                         <span>Bộ lọc</span>
//                     </button>
//                 </div>

//                 {isFilterVisible && (
//                     <div className="mt-4 pt-4 border-t border-gray-200">
//                         <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
//                             {/* Filter by Stock */}
//                             <div>
//                                 <label className="block text-sm font-medium text-gray-700 mb-1">Tồn kho</label>
//                                 <div className="flex items-center gap-2">
//                                     <input type="number" name="minStock" value={filters.minStock ?? ''} onChange={handleFilterChange} placeholder="Min" className="w-full p-2 border rounded-md"/>
//                                     <input type="number" name="maxStock" value={filters.maxStock ?? ''} onChange={handleFilterChange} placeholder="Max" className="w-full p-2 border rounded-md"/>
//                                 </div>
//                             </div>
//                             {/* Filter by Price */}
//                             <div>
//                                 <label className="block text-sm font-medium text-gray-700 mb-1">Giá bán</label>
//                                 <div className="flex items-center gap-2">
//                                     <input type="number" name="minPrice" value={filters.minPrice ?? ''} onChange={handleFilterChange} placeholder="Min" className="w-full p-2 border rounded-md"/>
//                                     <input type="number" name="maxPrice" value={filters.maxPrice ?? ''} onChange={handleFilterChange} placeholder="Max" className="w-full p-2 border rounded-md"/>
//                                 </div>
//                             </div>
//                              {/* Filter by Active Status */}
//                             <div>
//                                 <label htmlFor="active" className="block text-sm font-medium text-gray-700 mb-1">Trạng thái</label>
//                                 <select id="active" name="active" value={filters.active === undefined ? '' : String(filters.active)} onChange={handleFilterChange} className="w-full p-2 border rounded-md bg-white">
//                                     <option value="">Tất cả</option>
//                                     <option value="true">Đang hoạt động</option>
//                                     <option value="false">Ngừng hoạt động</option>
//                                 </select>
//                             </div>
//                             {/* Action Buttons */}
//                             <div className="flex items-end">
//                                 <button onClick={resetFilters} className="flex items-center gap-2 p-2 px-4 bg-red-500 text-white rounded-md hover:bg-red-600 w-full justify-center">
//                                     <RotateCcw className="w-4 h-4"/>
//                                     <span>Reset</span>
//                                 </button>
//                             </div>
//                         </div>
//                     </div>
//                 )}
//             </div>

//             {/* Bảng Dữ liệu */}
//             <main className="bg-white rounded-lg shadow-md overflow-hidden">
//                 <div className="overflow-x-auto">
//                     <table className="min-w-full divide-y divide-gray-200">
//                         <thead className="bg-gray-50">
//                             {/* ... thead của bạn ... */}
//                         </thead>
//                         <tbody className="bg-white divide-y divide-gray-200">
//                             {isLoading && (<tr><td colSpan={7} className="text-center p-8"><LoaderCircle className="w-8 h-8 mx-auto animate-spin text-blue-500" /></td></tr>)}
//                             {isError && (<tr><td colSpan={7} className="text-center p-8 text-red-600">Lỗi: {error.message}</td></tr>)}
//                             {!isLoading && !isError && products.length === 0 && (<tr><td colSpan={7} className="text-center p-8 text-gray-500">Không tìm thấy sản phẩm nào khớp với bộ lọc.</td></tr>)}
//                             {products.map((item) => (
//                                 <tr key={item.inventoryProductId} className="hover:bg-gray-50">
//                                     {/* ... các td hiển thị dữ liệu sản phẩm ... */}
//                                     <td className="px-6 py-4 whitespace-nowrap text-center">
//                                         <button onClick={() => handleEditClick(item)} className="text-blue-600 hover:text-blue-900 p-2 rounded-full hover:bg-blue-100">
//                                             <Edit className="w-5 h-5" />
//                                         </button>
//                                     </td>
//                                 </tr>
//                             ))}
//                         </tbody>
//                     </table>
//                 </div>
//             </main>
            
//             {/* Modal Edit */}
//             {editingProduct && (
//                  <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center">
//                     {/* ... JSX cho Modal của bạn ... */}
//                  </div>
//             )}
//         </div>
//     );
// }
