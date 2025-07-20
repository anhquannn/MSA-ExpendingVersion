// src/pages/Dashboard/PromoCodeManagementPage.tsx

import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { promoCodeService, PromoCode, PromoCodeFilter } from '../../services/promoCodeService';
import PromoCodeForm from '../../components/PromoCode/PromoCodeForm';
import Modal from '../../components/common/Modal';
import Pagination from '../../components/common/Pagination';
import { useParams } from 'react-router-dom';

function useDebounce(value: string, delay: number) {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const handler = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(handler);
  }, [value, delay]);
  return debounced;
}

const PromoCodeManagementPage: React.FC = () => {
  const { campaignId } = useParams<{ campaignId?: string }>();
  const userId = 1; // Giả sử đã đăng nhập
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editing, setEditing] = useState<PromoCode | null>(null);
  const [filters, setFilters] = useState<PromoCodeFilter>({
        page: 1,
        pageSize: 10,
        sortBy: 'startDate',
        sortDirection: 'DESC',
        keyword: '',
        campaignId: campaignId ? Number(campaignId) : undefined,
    });

  const debouncedKeyword = useDebounce(filters.keyword || '', 500);

  const { data: pagedData, isLoading, isError, error } = useQuery({
    queryKey: ['promocodes', { ...filters, keyword: debouncedKeyword }],
    queryFn: () => promoCodeService.getPromoCodesWithPaging({ ...filters, keyword: debouncedKeyword }),
    placeholderData: keepPreviousData,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => promoCodeService.deletePromoCode(id),
    onSuccess: () => {
      alert('Xóa mã giảm giá thành công!');
      queryClient.invalidateQueries({ queryKey: ['promocodes'] });
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilters(prev => ({ ...prev, keyword: e.target.value, page: 1 }));
  };

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
  };

  const openAddModal = () => {
    setEditing(null);
    setIsModalOpen(true);
  };

  const openEditModal = (item: PromoCode) => {
    setEditing(item);
    setIsModalOpen(true);
  };

  const handleDelete = (item: PromoCode) => {
    if (window.confirm(`Bạn có chắc muốn xóa mã "${item.code}" không?`)) {
      deleteMutation.mutate(item.promoCodeId!);
    }
  };

  const promoCodes = pagedData?.content || [];
  const totalPages = pagedData?.totalPages || 1;

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-semibold">Quản lý Mã Giảm Giá</h2>
        <button onClick={openAddModal} className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
          Thêm Mới
        </button>
      </div>

      <div className="mb-4 flex gap-3 items-center">
        <input
          type="text"
          placeholder="Tìm theo mã hoặc tên..."
          value={filters.keyword}
          onChange={handleFilterChange}
          className="p-2 border rounded-md w-full md:w-1/3"
        />
        <button
          onClick={() => setFilters(prev => ({ ...prev, keyword: '', page: 1 }))}
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
              <th className="py-3 px-6 text-left">Mã</th>
              <th className="py-3 px-6 text-left">Tên</th>
              <th className="py-3 px-6 text-left">Giảm (%)</th>
              <th className="py-3 px-6 text-left">Phạm vi</th>
            <th className="py-3 px-6 text-left">Giá trị tối thiểu</th>
            <th className="py-3 px-6 text-left">Bắt đầu</th>
            <th className="py-3 px-6 text-left">Kết thúc</th>
            <th className="py-3 px-6 text-left">Trạng thái</th>
            <th className="py-3 px-6 text-left">Mô tả</th>
              <th className="py-3 px-6 text-center">Hành động</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm">
            {promoCodes.map(pc => (
              <tr key={pc.promoCodeId} className="border-b hover:bg-gray-50">
                <td className="py-3 px-6">{pc.promoCodeId}</td>
                <td className="py-3 px-6 font-medium">{pc.code}</td>
                <td className="py-3 px-6">{pc.name}</td>
                <td className="py-3 px-6">{pc.discountPercentage}</td>
                <td className="py-3 px-6">{pc.campaignResponse?.scopeType || '---'}</td>
            <td className="py-3 px-6">{pc.campaignResponse?.minOrderValue || '---'}</td>
                <td className="py-3 px-6">{pc.startDate}</td>
                <td className="py-3 px-6">{pc.endDate}</td>
                <td className="py-3 px-6">{pc.status || '---'}</td>
                <td className="py-3 px-6">{pc.description}</td>
                <td className="py-3 px-6 text-center">
                  <button onClick={() => openEditModal(pc)} className="text-yellow-600 hover:underline mr-4">Sửa</button>
                  <button onClick={() => handleDelete(pc)} disabled={deleteMutation.isPending} className="text-red-600 hover:underline disabled:text-gray-400">
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
        title={editing ? 'Sửa Mã Giảm Giá' : 'Thêm Mã Giảm Giá'}
      >
        <PromoCodeForm
          initialData={editing}
          onSuccess={() => setIsModalOpen(false)}
          userId={userId}
        />
      </Modal>
    </div>
  );
};

export default PromoCodeManagementPage;
