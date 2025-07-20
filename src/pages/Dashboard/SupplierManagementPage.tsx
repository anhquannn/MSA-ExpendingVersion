// File: src/pages/Dashboard/SupplierManagementPage.tsx

import React, { useState, useEffect } from 'react';
import Pagination from '../../components/common/Pagination';
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { supplierService, Supplier, SupplierPagingParams } from '../../services/supplierService';
import Modal from '../../components/common/Modal'; // Tái sử dụng Modal chung
import SupplierForm from '../../components/Supplier/SupplierForm'; // Import form vừa tạo

// Custom Hook để Debounce
function useDebounce(value: string, delay: number) {
  const [debouncedValue, setDebouncedValue] = useState(value);
  useEffect(() => {
    const handler = setTimeout(() => { setDebouncedValue(value); }, delay);
    return () => clearTimeout(handler);
  }, [value, delay]);
  return debouncedValue;
}

const SupplierManagementPage: React.FC = () => {
  const queryClient = useQueryClient();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState<Supplier | null>(null);
  const [filters, setFilters] = useState<SupplierPagingParams>({
    keyword: '',
    page: 1,
    pageSize: 10,
    sortBy: 'supplierId',
    sortDirection: 'ASC',
  });
  const debouncedKeyword = useDebounce(filters.keyword || '', 500);

  const { data: pagedData, isLoading, isError, error } = useQuery({
    queryKey: ['suppliers', { ...filters, keyword: debouncedKeyword }],
    queryFn: () => supplierService.getSuppliers({ ...filters, keyword: debouncedKeyword }),
    placeholderData: keepPreviousData,
  });

  const suppliers = pagedData?.content || [];
  const totalPages = pagedData?.totalPages || 1;

  const deleteMutation = useMutation({
    mutationFn: (id: number) => supplierService.deleteSupplier(id),
    onSuccess: () => {
      alert('Xóa nhà cung cấp thành công!');
      queryClient.invalidateQueries({ queryKey: ['suppliers'] });
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilters(prev => ({ ...prev, keyword: e.target.value, page: 1 }));
  };

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
  };

  const handleOpenAddModal = () => {
    setEditingSupplier(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (supplier: Supplier) => {
    setEditingSupplier(supplier);
    setIsModalOpen(true);
  };

  const handleDelete = (supplier: Supplier) => {
    if (window.confirm(`Bạn có chắc muốn xóa nhà cung cấp "${supplier.name}"?`)) {
      deleteMutation.mutate(supplier.supplierId);
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-semibold">Quản lý Nhà Cung Cấp</h2>
        <button onClick={handleOpenAddModal} className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">Thêm Mới</button>
      </div>

      <div className="mb-4 flex gap-4">
        <input
          type="text"
          placeholder="Tìm theo tên hoặc thông tin liên hệ..."
          value={filters.keyword}
          onChange={handleFilterChange}
          className="p-2 border rounded-md flex-1 md:w-1/3"
        />
        <button
          onClick={() => setFilters(prev => ({ ...prev, keyword: '' }))}
          className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-md"
        >
          Reset
        </button>
      </div>

      {isLoading && <p className="text-center py-4">Đang tải...</p>}
      {isError && <p className="text-center py-4 text-red-500">Lỗi: {(error as Error).message}</p>}
      
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border">
          <thead className="bg-gray-100">
            <tr>
              <th className="py-3 px-6 text-left">ID</th>
              <th className="py-3 px-6 text-left">Hình</th>
              <th className="py-3 px-6 text-left">Tên NCC</th>
              <th className="py-3 px-6 text-left">Liên hệ</th>
              <th className="py-3 px-6 text-left">Địa chỉ</th>
              <th className="py-3 px-6 text-center">Hành Động</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm">
            {suppliers.map(sup => (
              <tr key={sup.supplierId} className="border-b hover:bg-gray-50">
                <td className="py-3 px-6">{sup.supplierId}</td>
                <td className="py-3 px-4">
                  <img
                    src={sup.image ? sup.image : 'https://via.placeholder.com/64'}
                    alt={sup.name}
                    className="h-16 w-16 object-cover rounded-md"
                  />
                </td>
                <td className="py-3 px-6 font-medium">{sup.name}</td>
                <td className="py-3 px-6">{sup.contact}</td>
                <td className="py-3 px-6">{sup.address}</td>
                <td className="py-3 px-6 text-center">
                  <button onClick={() => handleOpenEditModal(sup)} className="text-yellow-600 hover:underline mr-4">Sửa</button>
                  <button onClick={() => handleDelete(sup)} disabled={deleteMutation.isPending} className="text-red-600 hover:underline disabled:text-gray-400">
                    {deleteMutation.isPending ? '...' : 'Xóa'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="flex justify-center mt-6">
        <Pagination currentPage={filters.page ?? 1} totalPages={totalPages} onPageChange={handlePageChange} />
      </div>

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingSupplier ? 'Sửa Nhà Cung Cấp' : 'Thêm Nhà Cung Cấp Mới'}
      >
        <SupplierForm 
          onSuccess={() => setIsModalOpen(false)}
          initialData={editingSupplier}
        />
      </Modal>
    </div>
  );
};

export default SupplierManagementPage;