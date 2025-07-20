import React, { useEffect, useState } from 'react';
import { PromoCode, PromoCodePayload, promoCodeService } from '../../services/promoCodeService';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { campaignService, Campaign } from '../../services/campaignService';

interface PromoCodeFormProps {
  initialData?: PromoCode | null;
  onSuccess: () => void;
  userId: number;
}

const PromoCodeForm: React.FC<PromoCodeFormProps> = ({ initialData, onSuccess, userId }) => {
  const queryClient = useQueryClient();
  const today = new Date().toISOString().split('T')[0];
  const [campaigns, setCampaigns] = useState<Campaign[]>([]);
  const [formData, setFormData] = useState<Partial<PromoCodePayload>>({
    name: '',
    code: '',
    description: '',
    startDate: today,
    endDate: today,
    status: 'ACTIVE',
    discountPercentage: 0,
    campaignId: 0,
  });

  useEffect(() => {
  const fetchCampaigns = async () => {
    try {
      const result = await campaignService.getCampaigns();
      setCampaigns(result);
    } catch (err) {
      console.error('Lỗi khi tải danh sách chiến dịch:', err);
      alert('Không thể tải danh sách chiến dịch.');
    }
  };
  fetchCampaigns();
}, []);

  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name,
        code: initialData.code,
        description: initialData.description,
        startDate: initialData.startDate.split(/T| /)[0],
        endDate: initialData.endDate.split(/T| /)[0],
        status: initialData.status,
        discountPercentage: initialData.discountPercentage,
        campaignId: initialData.campaignId,
      });
    }
  }, [initialData]);

  const mutation = useMutation({
    mutationFn: (payload: PromoCodePayload) => {
      if (initialData?.promoCodeId) {
        return promoCodeService.updatePromoCode(initialData.promoCodeId, payload);
      }
      return promoCodeService.createPromoCode(payload);
    },
    onSuccess: () => {
      alert(initialData ? 'Cập nhật mã giảm giá thành công!' : 'Thêm mã giảm giá thành công!');
      queryClient.invalidateQueries({ queryKey: ['promocodes'] });
      onSuccess();
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: ['discountPercentage', 'campaignId'].includes(name)
        ? Number(value)
        : value,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
  e.preventDefault();

  const {
    name, code, startDate, endDate, discountPercentage, campaignId, description, status
  } = formData;

  if (!name || !code || !startDate || !endDate || !campaignId) {
    alert('Vui lòng nhập đầy đủ các trường bắt buộc.');
    return;
  }

  if (new Date(startDate) > new Date(endDate)) {
    alert('Ngày bắt đầu không được sau ngày kết thúc.');
    return;
  }

  if (discountPercentage! < 0 || discountPercentage! > 100) {
    alert('Giá trị giảm phải nằm trong khoảng 0 đến 100%.');
    return;
  }

  // Chuyển đổi sang định dạng ISO 8601: yyyy-MM-ddTHH:mm:ss
  const formattedStartDate = `${startDate} 00:00:00`;
  const formattedEndDate = `${endDate} 23:59:59`; // optional: bao trùm hết ngày kết thúc

  const payload: PromoCodePayload = {
    name,
    code,
    description: description || '',
    startDate: formattedStartDate,
    endDate: formattedEndDate,
    status: status || 'ACTIVE',
    discountPercentage: discountPercentage || 0,
    campaignId,
  };

  mutation.mutate(payload);
};

  return (
    <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div>
        <label className="block text-sm font-medium">Tên mã giảm giá (*)</label>
        <input
          name="name"
          value={formData.name}
          onChange={handleChange}
          required
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>

      <div>
        <label className="block text-sm font-medium">Mã giảm giá (*)</label>
        <input
          name="code"
          value={formData.code}
          onChange={handleChange}
          required
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>

      <div className="md:col-span-2">
        <label className="block text-sm font-medium">Mô tả</label>
        <input
          name="description"
          value={formData.description}
          onChange={handleChange}
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>

      <div>
        <label className="block text-sm font-medium">Ngày bắt đầu (*)</label>
        <input
          name="startDate"
          type="date"
          value={formData.startDate}
          onChange={handleChange}
          required
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>

      <div>
        <label className="block text-sm font-medium">Ngày kết thúc (*)</label>
        <input
          name="endDate"
          type="date"
          value={formData.endDate}
          onChange={handleChange}
          required
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
        <label className="block text-sm font-medium">Giảm giá (%)</label>
        <input
          name="discountPercentage"
          type="number"
          min={0}
          max={100}
          value={formData.discountPercentage}
          onChange={handleChange}
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>

      <div className="md:col-span-2">
        <label className="block text-sm font-medium">Chiến dịch</label>
        <select
          name="campaignId"
          value={formData.campaignId}
          onChange={handleChange}
          className="mt-1 w-full p-2 border rounded-md"
          required
        >
          <option value="">-- Chọn chiến dịch --</option>
          {campaigns.map((campaign) => (
            <option key={campaign.campaignId} value={campaign.campaignId}>
              {campaign.name}
              {campaign.scopeType !== 'ALL' &&
                ` (${campaign.scopeType === 'CATEGORY' ? 'Danh mục' : 'Nhà cung cấp'})`}
            </option>
          ))}
        </select>
      </div>

      {formData.campaignId && formData.campaignId > 0 && (
        <div className="md:col-span-2">
          <label className="block text-sm font-medium">Chi tiết chiến dịch</label>
          <div className="mt-1 p-4 bg-gray-50 rounded-md">
            <div className="flex justify-between text-sm">
              <div>
                <span className="font-medium">Phạm vi áp dụng:</span>
                <span className="ml-2">
                  {campaigns.find(c => c.campaignId === formData.campaignId)?.scopeType === 'ALL' ? 'Toàn bộ' : 
                  (campaigns.find(c => c.campaignId === formData.campaignId)?.scopeType === 'CATEGORY' ? 'Danh mục' : 'Nhà cung cấp')}
                </span>
              </div>
              <div>
                <span className="font-medium">Giá trị tối thiểu:</span>
                <span className="ml-2">
                  {campaigns.find(c => c.campaignId === formData.campaignId)?.minOrderValue} đ
                </span>
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="md:col-span-2 flex justify-end pt-4">
        <button
          type="submit"
          disabled={mutation.isPending}
          className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-400"
        >
          {mutation.isPending ? 'Đang lưu...' : (initialData ? 'Cập nhật' : 'Thêm mã giảm giá')}
        </button>
      </div>
    </form>
  );
};

export default PromoCodeForm;
