// src/pages/Dashboard/CampaignManagementPage.tsx

import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { Campaign, CampaignFilter, campaignService } from '../../services/campaignService';
import CampaignForm from '../../components/Campaign/CampaignForm';
import Modal from '../../components/common/Modal';
import { useNavigate } from 'react-router-dom';

function useDebounce(value: string, delay: number) {
  const [debouncedValue, setDebouncedValue] = useState(value);
  useEffect(() => {
    const handler = setTimeout(() => setDebouncedValue(value), delay);
    return () => clearTimeout(handler);
  }, [value, delay]);
  return debouncedValue;
}

const CampaignManagementPage: React.FC = () => {
  const queryClient = useQueryClient();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCampaign, setEditingCampaign] = useState<Campaign | null>(null);
  const [filters, setFilters] = useState<CampaignFilter>({
    keyword: '',
    page: 1,
    pageSize: 10,
    sortBy: 'startDate',
    sortDirection: 'DESC',
  });

  const debouncedKeyword = useDebounce(filters.keyword || '', 500);

  const navigate = useNavigate();

  const { data: pagedData, isLoading, isError, error } = useQuery({
    queryKey: ['campaigns', { ...filters, keyword: debouncedKeyword }],
    queryFn: () => campaignService.getCampaignsWithPaging({ ...filters, keyword: debouncedKeyword }),
    placeholderData: keepPreviousData,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => campaignService.deleteCampaign(id),
    onSuccess: () => {
      alert('Xóa chiến dịch thành công!');
      queryClient.invalidateQueries({ queryKey: ['campaigns'] });
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilters(prev => ({ ...prev, keyword: e.target.value, page: 1 }));
  };

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
  };

  const handleSuccess = () => {
    setIsModalOpen(false);
    setEditingCampaign(null);
  };

  const handleOpenAddModal = () => {
    setEditingCampaign(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (campaign: Campaign) => {
    setEditingCampaign(campaign);
    setIsModalOpen(true);
  };

  const handleDelete = (campaign: Campaign) => {
    if (window.confirm(`Bạn có chắc muốn xóa chiến dịch "${campaign.name}"?`)) {
      deleteMutation.mutate(campaign.campaignId!);
    }
  };

  const campaigns = pagedData?.content || [];
  const totalPages = pagedData?.totalPages || 1;

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-semibold">Quản lý Chiến dịch</h2>
        <button onClick={handleOpenAddModal} className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
          Thêm mới
        </button>
      </div>

      <div className="mb-4">
        <input
          type="text"
          placeholder="Tìm theo tên chiến dịch..."
          value={filters.keyword}
          onChange={handleFilterChange}
          className="p-2 border rounded-md w-full md:w-1/3"
        />
      </div>

      {isLoading && <p className="text-center py-4">Đang tải...</p>}
      {isError && <p className="text-center py-4 text-red-500">Lỗi: {(error as Error).message}</p>}

      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border">
          <thead className="bg-gray-100">
            <tr>
              <th className="py-3 px-6 text-left">ID</th>
              <th className="py-3 px-6 text-left">Tên</th>
              <th className="py-3 px-6 text-left">Trạng thái</th>
              <th className="py-3 px-6 text-left">Ngày bắt đầu</th>
              <th className="py-3 px-6 text-left">Ngày kết thúc</th>
              <th className="py-3 px-6 text-center">Hành động</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm">
            {campaigns.map(c => (
              <tr key={c.campaignId} className="border-b hover:bg-gray-50">
                <td className="py-3 px-6">{c.campaignId}</td>
                <td className="py-3 px-6 font-medium">
                    <button
                        onClick={() => navigate(`/dashboard/campaigns/${c.campaignId}/promocodes`)}
                        className="text-blue-600 hover:underline"
                    >
                        {c.name}
                    </button>
                </td>
                <td className="py-3 px-6">{c.status}</td>
                <td className="py-3 px-6">{c.startDate}</td>
                <td className="py-3 px-6">{c.endDate}</td>
                <td className="py-3 px-6 text-center">
                  <button onClick={() => handleOpenEditModal(c)} className="text-yellow-600 hover:underline mr-4">Sửa</button>
                  <button onClick={() => handleDelete(c)} disabled={deleteMutation.isPending} className="text-red-600 hover:underline disabled:text-gray-400">
                    {deleteMutation.isPending ? '...' : 'Xóa'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="flex justify-between items-center mt-6">
        <p className="text-sm">Trang {pagedData?.number ? pagedData.number + 1 : 1} trên {totalPages}</p>
        <div className="flex space-x-2">
          <button
            onClick={() => handlePageChange(filters.page! - 1)}
            disabled={pagedData?.number === 0 || isLoading}
            className="px-4 py-2 border rounded disabled:opacity-50"
          >
            Trước
          </button>
          <button
            onClick={() => handlePageChange(filters.page! + 1)}
            disabled={(pagedData?.number ?? 0) + 1 >= totalPages || isLoading}
            className="px-4 py-2 border rounded disabled:opacity-50"
          >
            Sau
          </button>
        </div>
      </div>

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingCampaign ? 'Chỉnh sửa chiến dịch' : 'Thêm mới chiến dịch'}
      >
        <CampaignForm
          initialData={editingCampaign}
          onSuccess={handleSuccess}
        />
      </Modal>
    </div>
  );
};

export default CampaignManagementPage;
