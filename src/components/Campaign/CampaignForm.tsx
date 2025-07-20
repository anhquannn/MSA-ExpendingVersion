import React, { useEffect, useState } from 'react';
import { Campaign, CampaignTarget, CampaignPayload, campaignService, getTodayStartDate, getTodayEndDate } from '../../services/campaignService';
import { categoryService } from '../../services/categoryService';
import { supplierService } from '../../services/supplierService';
import { Category } from '../../services/categoryService';
import { Supplier } from '../../services/supplierService';
import { useMutation, useQueryClient } from '@tanstack/react-query';

interface CampaignFormProps {
  initialData?: Campaign | null;
  onSuccess: () => void;
}

interface CampaignTargetForm {
  targetType: 'CATEGORY' | 'SUPPLIER';
  targetId: number | null;
}

const CampaignForm: React.FC<CampaignFormProps> = ({ initialData, onSuccess }) => {
  const queryClient = useQueryClient();

  const [formData, setFormData] = useState<Partial<CampaignPayload> & { campaignId?: number }>({
    name: '',
    description: '',
    status: 'ACTIVE',
    scopeType: 'ALL',
    minOrderValue: 0,
    startDate: getTodayStartDate().split(' ')[0], // lấy yyyy-MM-dd
    endDate: getTodayEndDate().split(' ')[0],
  });

  const [targets, setTargets] = useState<CampaignTarget[]>([]);
  const [targetForm, setTargetForm] = useState<CampaignTarget | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await categoryService.getCategories({ page: 1, pageSize: 500 });
        setCategories(response.content);
      } catch (error) {
        console.error('Lỗi khi lấy danh sách danh mục:', error);
      }
    };

    const fetchSuppliers = async () => {
      try {
        const response = await supplierService.getSuppliers({ page: 1, pageSize: 500 });
        setSuppliers(response.content);
      } catch (error) {
        console.error('Lỗi khi lấy danh sách nhà cung cấp:', error);
      }
    };

    fetchCategories();
    fetchSuppliers();
  }, []);

  useEffect(() => {
    const loadForEdit = async () => {
      if (!initialData) return;
      // Populate form fields
      setFormData({
        name: initialData.name,
        description: initialData.description,
        status: initialData.status,
        scopeType: initialData.scopeType,
        minOrderValue: initialData.minOrderValue,
        startDate: initialData.startDate.split(' ')[0],
        endDate: initialData.endDate.split(' ')[0],
      });

      // If targets are already provided in initialData, use them; otherwise fetch from server
      if (initialData.targets && initialData.targets.length > 0) {
        setTargets(initialData.targets);
      } else {
        try {
          const remoteTargets = await campaignService.getCampaignTargets(initialData.campaignId!);
          setTargets(remoteTargets);
        } catch (err) {
          console.error('Error fetching campaign targets:', err);
        }
      }
    };

    loadForEdit();
  }, [initialData]);

  // Use a single mutation for both campaign and targets
  const handleSaveTarget = () => {
    if (!targetForm || !formData.scopeType) return;
    
    // Ensure we don't create a target with scopeType 'ALL'
    if (formData.scopeType === 'ALL') {
      console.warn('Cannot create target with scopeType ALL');
      return;
    }

    const newTarget: CampaignTarget = {
      campaignId: formData.campaignId || 0,
      targetId: targetForm.targetId,
      targetType: formData.scopeType as 'CATEGORY' | 'SUPPLIER'
    };

    setTargets(prev => [...prev, newTarget]);
    setTargetForm(null);
  };

  const campaignMutation = useMutation({
    mutationFn: async (payload: { campaign: CampaignPayload, targets?: CampaignTarget[] }) => {
      try {
        // First create/update the campaign
        const campaign = initialData?.campaignId 
          ? await campaignService.updateCampaign(initialData.campaignId, payload.campaign)
          : await campaignService.createCampaign(payload.campaign);

        // If there are targets and scopeType is not ALL
        if (payload.targets && payload.targets.length > 0 && payload.campaign.scopeType !== 'ALL') {
          // Synchronize targets
          for (const target of payload.targets) {
            if (!campaign.campaignId) {
              throw new Error('campaignId is undefined');
            }

            if (initialData) {
              // Editing mode: update existing targets or create new targets without campaignTargetId
              if (target.campaignTargetId) {
                await campaignService.updateCampaignTarget(target.campaignTargetId, {
                  ...target,
                  campaignId: campaign.campaignId,
                });
              } else if (typeof target.targetId === 'number') {
                await campaignService.createCampaignTarget({
                  ...target,
                  campaignId: campaign.campaignId,
                  campaignTargetId: undefined,
                });
              }
            } else {
              // Creating new campaign: create all targets
              if (target.campaignTargetId) {
                await campaignService.updateCampaignTarget(target.campaignTargetId, {
                  ...target,
                  campaignId: campaign.campaignId,
                });
              } else if (typeof target.targetId === 'number') {
                await campaignService.createCampaignTarget({
                  ...target,
                  campaignId: campaign.campaignId,
                  campaignTargetId: undefined,
                });
              }
            }
          }
        }
        return campaign;
      } catch (error) {
        throw error;
      }
    },
    onSuccess: async (campaign) => {
      alert(`${initialData ? 'Cập nhật' : 'Thêm'} chiến dịch và mục tiêu thành công!`);
      // Invalidate all queries that start with 'campaigns' to ensure all related queries are refetched
      await queryClient.invalidateQueries({ 
        queryKey: ['campaigns'],
        refetchType: 'all'
      });
      onSuccess();
    },
    onError: (err: Error) => {
      console.error('Lỗi khi lưu chiến dịch và mục tiêu:', err);
      alert(`Lỗi: ${err.message}`);
    },
  });

  const deleteTargetMutation = useMutation({
    mutationFn: async (campaignTargetId: number) => {
      await campaignService.deleteCampaignTarget(campaignTargetId);
      return campaignTargetId;
    },
    onSuccess: (campaignTargetId: number) => {
      // Refresh the targets list after deletion
      setTargets(prev => prev.filter(t => t.campaignTargetId !== campaignTargetId));
    },
    onError: (err: Error) => {
      console.error('Lỗi khi xóa mục tiêu:', err);
      alert(`Lỗi: ${err.message}`);
    }
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'minOrderValue' ? Number(value) : value,
    }));
  };

  const handleTargetChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    if (targetForm) {
      setTargetForm(prev => prev && {
        ...prev,
        [name]: name === 'targetId' ? Number(value) : value,
        campaignTargetId: undefined,
        campaignId: prev?.campaignId || 0
      });
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
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
    if (formData.minOrderValue! < 0) {
      alert('Giá trị đơn hàng tối thiểu không hợp lệ');
      return;
    }

    try {
      // Nếu người dùng đang chọn target mà chưa ấn Lưu, tự động thêm target hiện tại vào danh sách
      let preparedTargets = targets;
      if (targetForm && targetForm.targetId !== null && targetForm.targetId !== 0) {
        preparedTargets = [
          ...targets,
          {
            ...targetForm,
            targetType: (formData.scopeType as 'CATEGORY' | 'SUPPLIER'),
            campaignTargetId: undefined,
            campaignId: formData.campaignId || 0,
          },
        ];
      }

      // Nếu phạm vi không phải ALL thì phải có ít nhất một target
      if (formData.scopeType !== 'ALL' && preparedTargets.length === 0) {
        alert('Vui lòng chọn ít nhất một mục tiêu');
        return;
      }

      // Build payload
      const payload = {
        campaign: formData as CampaignPayload,
        targets: preparedTargets,
      };

      // Call the mutation with both campaign and targets
      await campaignMutation.mutateAsync(payload);
    } catch (error) {
      console.error('Lỗi khi lưu chiến dịch và mục tiêu:', error);
      alert(`Lỗi: ${error instanceof Error ? error.message : 'Đã xảy ra lỗi'}`);
    }
  };

  const handleAddTarget = () => {
    // Đặt loại mục tiêu trùng với phạm vi
    const targetType = formData.scopeType === 'CATEGORY' ? 'CATEGORY' : 'SUPPLIER';
    setTargetForm({
      targetType,
      targetId: 0,
      campaignTargetId: undefined,
      campaignId: initialData?.campaignId || 0
    });
  };

  const handleDeleteTarget = (target: CampaignTarget) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa mục tiêu này không?')) return;
    setTargets(prev => prev.filter(t => t.campaignTargetId !== target.campaignTargetId));
  };

  return (
    <div className="max-w-7xl mx-auto p-6">
      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Hàng 1: Tên chiến dịch, Trạng thái, Phạm vi */}
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          {/* Tên chiến dịch */}
          <div className="space-y-2">
            <label className="block text-sm font-semibold text-gray-700">
              Tên chiến dịch *
            </label>
            <input
              name="name"
              type="text"
              value={formData.name}
              onChange={handleChange}
              className="w-full px-4 py-3 text-base border-2 border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
              placeholder="Nhập tên chiến dịch"
              required
            />
          </div>

          {/* Trạng thái */}
          <div className="space-y-2">
            <label className="block text-sm font-semibold text-gray-700">
              Trạng thái *
            </label>
            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="w-full px-4 py-3 text-base border-2 border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
              required
            >
              <option value="ACTIVE">Hoạt động</option>
              <option value="INACTIVE">Không hoạt động</option>
            </select>
          </div>

          {/* Phạm vi */}
          <div className="space-y-2">
            <label className="block text-sm font-semibold text-gray-700">
              Phạm vi *
            </label>
            <select
              name="scopeType"
              value={formData.scopeType}
              onChange={handleChange}
              className="w-full px-4 py-3 text-base border-2 border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
              required
            >
              <option value="ALL">Toàn bộ</option>
              <option value="CATEGORY">Danh mục</option>
              <option value="SUPPLIER">Nhà cung cấp</option>
            </select>
          </div>
        </div>

        {/* Hàng 2: Giá trị đơn hàng, Ngày bắt đầu, Ngày kết thúc */}
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          {/* Giá trị đơn hàng tối thiểu */}
          <div className="space-y-2">
            <label className="block text-sm font-semibold text-gray-700">
              Giá trị đơn hàng tối thiểu *
            </label>
            <input
              name="minOrderValue"
              type="number"
              value={formData.minOrderValue}
              onChange={handleChange}
              className="w-full px-4 py-3 text-base border-2 border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
              placeholder="0"
              min="0"
            />
          </div>

          {/* Ngày bắt đầu */}
          <div className="space-y-2">
            <label className="block text-sm font-semibold text-gray-700">
              Ngày bắt đầu *
            </label>
            <input
              name="startDate"
              type="date"
              value={formData.startDate}
              onChange={handleChange}
              className="w-full px-4 py-3 text-base border-2 border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
              required
            />
          </div>

          {/* Ngày kết thúc */}
          <div className="space-y-2">
            <label className="block text-sm font-semibold text-gray-700">
              Ngày kết thúc *
            </label>
            <input
              name="endDate"
              type="date"
              value={formData.endDate}
              onChange={handleChange}
              className="w-full px-4 py-3 text-base border-2 border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
              required
            />
          </div>
        </div>

        {/* Mô tả */}
        <div className="space-y-2">
          <label className="block text-sm font-semibold text-gray-700">
            Mô tả
          </label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            className="w-full px-4 py-3 text-base border-2 border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
            rows={3}
            placeholder="Nhập mô tả chiến dịch..."
          />
        </div>

        {/* Danh sách mục tiêu */}
        {formData.scopeType !== 'ALL' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold text-gray-800">
                Danh sách mục tiêu
              </h3>
              <button
                type="button"
                onClick={handleAddTarget}
                className="px-4 py-2 bg-green-600 text-white font-medium rounded-lg hover:bg-green-700 transition-colors duration-200"
              >
                Thêm mục tiêu
              </button>
            </div>

            {targetForm && (
              <div className="p-4 bg-gray-50 border-2 border-gray-200 rounded-lg shadow-sm">
                <h4 className="text-base font-medium text-gray-700 mb-3">
                  Thêm mục tiêu mới
                </h4>
                
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {/* Loại mục tiêu - Tự động đồng bộ với phạm vi */}
                  <div className="space-y-2">
                    <label className="block text-sm font-semibold text-gray-700">
                      Loại mục tiêu *
                    </label>
                    <select
                      name="targetType"
                      value={formData.scopeType === 'CATEGORY' ? 'CATEGORY' : 'SUPPLIER'}
                      onChange={handleTargetChange}
                      className="w-full px-4 py-3 text-base border-2 border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
                      disabled
                      required
                    >
                      <option value="CATEGORY">Danh mục</option>
                      <option value="SUPPLIER">Nhà cung cấp</option>
                    </select>
                    <p className="text-xs text-gray-500">
                      Tự động đồng bộ với phạm vi
                    </p>
                  </div>

                  {/* Mục tiêu */}
                  <div className="space-y-2">
                    <label className="block text-sm font-semibold text-gray-700">
                      Mục tiêu *
                    </label>
                    <select
                      name="targetId"
                      value={targetForm?.targetId || ''}
                      onChange={handleTargetChange}
                      className="w-full px-4 py-3 text-base border-2 border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
                      required
                    >
                      <option value="">Chọn mục tiêu...</option>
                      {formData.scopeType === 'CATEGORY' ? (
                        categories.map((category) => (
                          <option key={category.categoryId} value={category.categoryId}>
                            {category.name}
                          </option>
                        ))
                      ) : (
                        suppliers.map((supplier) => (
                          <option key={supplier.supplierId} value={supplier.supplierId}>
                            {supplier.name}
                          </option>
                        ))
                      )}
                    </select>
                  </div>
                  </div>
                </div>
            )}

            {targets.length > 0 && (
              <div className="overflow-hidden bg-white border-2 border-gray-200 rounded-lg shadow-sm">
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-4 text-left text-sm font-semibold text-gray-700 uppercase tracking-wider">
                          Loại
                        </th>
                        <th className="px-6 py-4 text-left text-sm font-semibold text-gray-700 uppercase tracking-wider">
                          Mã mục tiêu
                        </th>
                        <th className="px-6 py-4 text-left text-sm font-semibold text-gray-700 uppercase tracking-wider">
                          Tên
                        </th>
                        <th className="px-6 py-4 text-left text-sm font-semibold text-gray-700 uppercase tracking-wider">
                          Hành động
                        </th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {targets.map((target) => {
                        const targetName = target.targetType === 'CATEGORY' 
                          ? categories.find(c => c.categoryId === target.targetId)?.name || 'Không xác định'
                          : suppliers.find(s => s.supplierId === target.targetId)?.name || 'Không xác định';
                        
                        return (
                          <tr key={target.campaignTargetId} className="hover:bg-gray-50">
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                              <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full ${
                                target.targetType === 'CATEGORY' 
                                  ? 'bg-blue-100 text-blue-800' 
                                  : 'bg-green-100 text-green-800'
                              }`}>
                                {target.targetType === 'CATEGORY' ? 'Danh mục' : 'Nhà cung cấp'}
                              </span>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                              {target.targetId || 'Chưa chọn'}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                              {targetName}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                              <button
                                type="button"
                                onClick={() => handleDeleteTarget(target)}
                                className="px-4 py-2 bg-red-600 text-white font-medium rounded-lg hover:bg-red-700 transition-colors duration-200"
                              >
                                Xóa
                              </button>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Nút submit */}
        <div className="flex justify-end pt-4 border-t border-gray-200">
          <button
            type="submit"
            disabled={campaignMutation.isPending}
            className="px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors duration-200"
          >
            {campaignMutation.isPending ? 'Đang lưu...' : initialData ? 'Cập nhật chiến dịch' : 'Thêm chiến dịch'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CampaignForm;