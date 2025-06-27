// src/pages/Dashboard/InventoryPage.tsx

import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { PagedResponse } from '../../services/categoryService';

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

  const { data, isLoading, isError, error } = useQuery<PagedResponse<Inventory>, Error>({
    queryKey: ['inventories', { ...filters, keyword: debouncedKeyword }],
    queryFn: () =>
      inventoryService.getInventoryWithPaging({
        ...filters,
        keyword: debouncedKeyword,
        page: filters.page || 1,
        pageSize: filters.pageSize || 10,
      }),
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
            {isLoading ? (
              <tr>
                <td colSpan={4} className="text-center py-4">Đang tải dữ liệu...</td>
              </tr>
            ) : data && data.content ? (
              data.content.length > 0 ? (
                data.content.map((inv: Inventory) => (
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
                      <Link to={`/dashboard/inventories/${inv.inventoryId}/history`} className="text-blue-600 hover:underline">Kiểm kho</Link>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={4} className="text-center py-4">Không có dữ liệu.</td>
                </tr>
              )
            ) : (
              <tr>
                <td colSpan={4} className="text-center py-4">Không thể tải dữ liệu kho hàng.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      <InventoryModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onSave={handleSave} initialData={editingInventory} />
    </div>
  );
};
