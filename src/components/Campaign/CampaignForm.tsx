import React, { useEffect, useState } from 'react';
import { Campaign, CampaignPayload, campaignService, getTodayStartDate, getTodayEndDate } from '../../services/campaignService';
import { useMutation, useQueryClient } from '@tanstack/react-query';

interface CampaignFormProps {
  initialData?: Campaign | null;
  onSuccess: () => void;
}

const CampaignForm: React.FC<CampaignFormProps> = ({ initialData, onSuccess }) => {
  const queryClient = useQueryClient();

  const [formData, setFormData] = useState<Partial<CampaignPayload>>({
    name: '',
    description: '',
    status: 'ACTIVE',
    startDate: getTodayStartDate().split(' ')[0], // lấy yyyy-MM-dd
    endDate: getTodayEndDate().split(' ')[0],
  });

  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name,
        description: initialData.description,
        status: initialData.status,
        startDate: initialData.startDate.split(' ')[0],
        endDate: initialData.endDate.split(' ')[0],
      });
    }
  }, [initialData]);

  const mutation = useMutation({
    mutationFn: (payload: CampaignPayload) => {
      if (initialData?.campaignId) {
        return campaignService.updateCampaign(initialData.campaignId, payload);
      }
      return campaignService.createCampaign(payload);
    },
    onSuccess: async () => {
      alert(initialData ? 'Cập nhật chiến dịch thành công!' : 'Thêm chiến dịch thành công!');
      // Invalidate all queries that start with 'campaigns' to ensure all related queries are refetched
      await queryClient.invalidateQueries({ 
        queryKey: ['campaigns'],
        refetchType: 'all'
      });
      onSuccess();
    },
    onError: (err: Error) => {
      console.error('Lỗi khi lưu chiến dịch:', err);
      alert(`Lỗi: ${err.message}`);
    },
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.name?.trim()) {
      alert('Tên chiến dịch là bắt buộc.');
      return;
    }
    if (!formData.startDate) {
      alert('Vui lòng chọn ngày bắt đầu.');
      return;
    }
    if (!formData.endDate) {
      alert('Vui lòng chọn ngày kết thúc.');
      return;
    }

    mutation.mutate(formData as CampaignPayload);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium">Tên chiến dịch *</label>
        <input
          name="name"
          type="text"
          value={formData.name}
          onChange={handleChange}
          required
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>

      <div>
        <label className="block text-sm font-medium">Mô tả</label>
        <input
          name="description"
          type="text"
          value={formData.description}
          onChange={handleChange}
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>

      <div>
        <label className="block text-sm font-medium">Trạng thái</label>
        <select
          name="status"
          value={formData.status}
          onChange={handleChange}
          className="mt-1 w-full p-2 border rounded-md"
        >
          <option value="ACTIVE">ACTIVE</option>
          <option value="INACTIVE">INACTIVE</option>
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium">Ngày bắt đầu *</label>
        <input
          name="startDate"
          type="date"
          value={formData.startDate}
          onChange={handleChange}
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>

      <div>
        <label className="block text-sm font-medium">Ngày kết thúc *</label>
        <input
          name="endDate"
          type="date"
          value={formData.endDate}
          onChange={handleChange}
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>

      <div className="flex justify-end pt-2">
        <button
          type="submit"
          disabled={mutation.isPending}
          className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-400"
        >
          {mutation.isPending ? 'Đang lưu...' : initialData ? 'Cập nhật' : 'Thêm chiến dịch'}
        </button>
      </div>
    </form>
  );
};

export default CampaignForm;
