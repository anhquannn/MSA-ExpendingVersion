// src/pages/Dashboard/CampaignManagementPage.tsx

import React, { useState, useEffect } from 'react';
import { categoryService } from '../../services/categoryService';
import { supplierService } from '../../services/supplierService';
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

  // Lưu map id -> name để hiển thị tên target nhanh chóng
  const [categoryMap, setCategoryMap] = useState<Record<number, string>>({});
  const [supplierMap, setSupplierMap] = useState<Record<number, string>>({});
  const [targetNameMap, setTargetNameMap] = useState<Record<number, string>>({}); // campaignId -> target names

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

  // Fetch category & supplier data once on mount to build lookup maps
  useEffect(() => {
    const fetchLookupData = async () => {
      try {
        const [catRes, supRes] = await Promise.all([
          categoryService.getCategories({ page: 1, pageSize: 500 }),
          supplierService.getSuppliers({ page: 1, pageSize: 500 }),
        ]);
        const catMap: Record<number, string> = {};
        catRes.content.forEach(c => (catMap[c.categoryId] = c.name));
        setCategoryMap(catMap);
        const supMap: Record<number, string> = {};
        supRes.content.forEach(s => (supMap[s.supplierId] = s.name));
        setSupplierMap(supMap);
      } catch (err) {
        console.error('Lỗi khi tải danh mục/nhà cung cấp:', err);
      }
    };
    fetchLookupData();
  }, []);

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

  // Khi campaigns thay đổi, lấy target name cho từng campaign (chỉ khi cần)
  useEffect(() => {
    const fetchTargetsForCampaigns = async () => {
      const promises = campaigns
        .filter(c => c.scopeType !== 'ALL' && !(c.campaignId! in targetNameMap))
        .map(async c => {
          try {
            const targets = await campaignService.getCampaignTargets(c.campaignId!);
            const names = targets.map(t => {
              if (t.targetType === 'CATEGORY') return categoryMap[t.targetId] || `Danh mục #${t.targetId}`;
              if (t.targetType === 'SUPPLIER') return supplierMap[t.targetId] || `NCC #${t.targetId}`;
              return `${t.targetType} #${t.targetId}`;
            });
            return { campaignId: c.campaignId!, names: names.join(', ') };
          } catch (err) {
            console.error('Lỗi khi lấy target của campaign', c.campaignId, err);
            return { campaignId: c.campaignId!, names: '' };
          }
        });

      const results = await Promise.all(promises);
      setTargetNameMap(prev => {
        const updated = { ...prev };
        results.forEach(r => {
          updated[r.campaignId] = r.names;
        });
        return updated;
      });
    };

    if (campaigns.length > 0) {
      fetchTargetsForCampaigns();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [campaigns, categoryMap, supplierMap]);
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
              <th className="py-3 px-6 text-left">Phạm vi</th>
              <th className="py-3 px-6 text-left">Tên mục tiêu</th>
              <th className="py-3 px-6 text-left">Giá trị tối thiểu</th>
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
                <td className="py-3 px-6">{c.scopeType}</td>
                <td className="py-3 px-6">
                  {c.scopeType === 'ALL' ? '—' : (targetNameMap[c.campaignId!] || 'Đang tải...')}
                </td>
                <td className="py-3 px-6">{c.minOrderValue}</td>
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
