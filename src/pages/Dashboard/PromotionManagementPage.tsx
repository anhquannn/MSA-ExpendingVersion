// File: src/pages/Dashboard/PromotionManagementPage.tsx
import React, { useState, useEffect } from 'react';
import { useDebounce } from 'use-debounce';
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { promotionService, Promotion, PromotionCreatePayload, PromotionFilterRequest } from '../../services/promotionService';
import { productService, Product } from '../../services/productService';
import Pagination from '../../components/common/Pagination';
import Modal from '../../components/common/Modal';

// Simple Form component for create / update
const PromotionForm: React.FC<{ initialData?: Promotion | null; onSuccess: () => void }> = ({ initialData, onSuccess }) => {
  const queryClient = useQueryClient();
  const { data: products = [], isLoading: loadingProducts } = useQuery({
    queryKey: ['products', 'all'],
    queryFn: () => productService.getProducts({ page: 1, pageSize: 200 }).then(res => res.productsPage.content),
    staleTime: 5 * 60 * 1000,
  });
  // helper for default datetime-local format
  const isoNow = new Date().toISOString().slice(0,16);
  const isoNextWeek = new Date(Date.now() + 7*24*60*60*1000).toISOString().slice(0,16);

  const [formData, setFormData] = useState<PromotionCreatePayload>({
    productMainId: initialData?.productMain.productId || 0,
    productFreeId: initialData?.productFree.productId || 0,
    startDate: initialData?.startDate || isoNow,
    endDate: initialData?.endDate || isoNextWeek,
    discountPercentage: initialData?.discountPercentage || 0,
    status: initialData?.status || 'INACTIVE',
  });

  // helper convert 'YYYY-MM-DDTHH:mm' to 'YYYY-MM-DD HH:mm:00'
  const toBackendDate = (val: string) => {
    const date = new Date(val);
    return date.toISOString().slice(0, 19).replace('T', ' ');
  };  

  const mutation = useMutation({
    mutationFn: () => {
      if (initialData) {
        const payload = { ...formData, startDate: toBackendDate(formData.startDate), endDate: toBackendDate(formData.endDate) };
        return promotionService.updatePromotion(initialData.promotionId, payload);
      }
      const payload = { ...formData, startDate: toBackendDate(formData.startDate), endDate: toBackendDate(formData.endDate) };
       return promotionService.createPromotion(payload);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['promotions'] });
      onSuccess();
    },
    onError: (err: any) => alert(err.message || 'Đã có lỗi xảy ra'),
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // --- Simple validation ---
    if (formData.productMainId <= 0 || formData.productFreeId <= 0) {
      alert('Vui lòng nhập ID sản phẩm hợp lệ.');
      return;
    }
    if (!formData.startDate || !formData.endDate) {
      alert('Vui lòng chọn ngày bắt đầu và kết thúc.');
      return;
    }
    if (new Date(formData.startDate) > new Date(formData.endDate)) {
      alert('Ngày kết thúc phải sau ngày bắt đầu.');
      return;
    }
    if (formData.discountPercentage <= 0 || formData.discountPercentage > 100) {
      alert('Phần trăm giảm phải từ 1 đến 100.');
      return;
    }
    mutation.mutate();
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {/* For brevity only inputs for ids and percent. In reality use selects/date pickers */}
      <label className="block text-sm font-medium">Sản phẩm chính
        <select value={formData.productMainId} onChange={e => setFormData(p => ({ ...p, productMainId: Number(e.target.value) }))} className="w-full p-2 border rounded-md mt-1">
          <option value={0}>-- Chọn sản phẩm --</option>
          {products.map((pr: Product) => (
            <option key={pr.productId} value={pr.productId}>{pr.name}</option>
          ))}
        </select>
      </label>


      <label className="block text-sm font-medium">Sản phẩm tặng
        <select value={formData.productFreeId} onChange={e => setFormData(p => ({ ...p, productFreeId: Number(e.target.value) }))} className="w-full p-2 border rounded-md mt-1">
          <option value={0}>-- Chọn sản phẩm --</option>
          {products.map((pr: Product) => (
            <option key={pr.productId} value={pr.productId}>{pr.name}</option>
          ))}
        </select>
      </label>


      <label className="block text-sm font-medium">Ngày bắt đầu
        <input type="datetime-local" value={formData.startDate} onChange={e => setFormData(p => ({ ...p, startDate: e.target.value }))} className="w-full p-2 border rounded-md mt-1" required />
      </label>

      <label className="block text-sm font-medium">Ngày kết thúc
        <input type="datetime-local" value={formData.endDate} onChange={e => setFormData(p => ({ ...p, endDate: e.target.value }))} className="w-full p-2 border rounded-md mt-1" required />
      </label>

      <label className="block text-sm font-medium">Phần trăm giảm (%)
        <input type="number" value={formData.discountPercentage} onChange={e => setFormData(p => ({ ...p, discountPercentage: Number(e.target.value) }))} className="w-full p-2 border rounded-md mt-1" required />
      </label>

      <label className="block text-sm font-medium">Trạng thái
        <select value={formData.status} onChange={e => setFormData(p=>({...p,status:e.target.value as any}))} className="w-full p-2 border rounded-md mt-1">
          <option value="INACTIVE">Chưa kích hoạt</option>
          <option value="ACTIVE">Kích hoạt</option>
        </select>
      </label>
      <button type="submit" className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white font-semibold rounded" disabled={mutation.isPending}>
        {mutation.isPending ? 'Đang lưu...' : 'Lưu'}
      </button>
    </form>
  );
};

const PromotionManagementPage: React.FC = () => {
  // --- UI state ---
  const [keyword, setKeyword] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [debouncedKeyword] = useDebounce(keyword, 500);
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState<PromotionFilterRequest>({ page: 1, pageSize: 10 });
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPromotion, setEditingPromotion] = useState<Promotion | null>(null);

  const { data: pagedData, isLoading } = useQuery({
    queryKey: ['promotions', filters],
    queryFn: () => promotionService.getPagingPromotions(filters),
    placeholderData: keepPreviousData,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => promotionService.deletePromotion(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['promotions'] });
    },
  });

  // Sync filters whenever debounce or status change
  useEffect(() => {
    setFilters(prev => ({
      ...prev,
      keyword: debouncedKeyword || undefined,
      status: statusFilter || undefined,
      page: 1,
    }));
  }, [debouncedKeyword, statusFilter]);

  const promotions = pagedData?.content || [];
  const totalPages = pagedData?.totalPages || 1;

  function renderStatusBadge(status: string): JSX.Element {
    const normalized = status?.toUpperCase();
    switch (normalized) {
      case 'ACTIVE':
        return (
          <span className="bg-green-100 text-green-800 text-sm font-semibold px-3 py-1 rounded-full shadow-sm">
            Kích hoạt
          </span>
        );
      case 'INACTIVE':
        return (
          <span className="bg-gray-200 text-gray-800 text-sm font-semibold px-3 py-1 rounded-full shadow-sm">
            Chưa kích hoạt
          </span>
        );
      case 'EXPIRED':
        return (
          <span className="bg-red-100 text-red-700 text-sm font-semibold px-3 py-1 rounded-full shadow-sm">
            Hết hạn
          </span>
        );
      default:
        return (
          <span className="bg-yellow-100 text-yellow-800 text-sm font-semibold px-3 py-1 rounded-full shadow-sm">
            {status || 'Không xác định'}
          </span>
        );
    }
  }

  return (
    <div className="p-6 space-y-6">
      {/* --- TIÊU ĐỀ --- */}
      <h1 className="text-2xl font-bold mb-4">Quản lý Khuyến mãi</h1>

      {/* --- BỘ LỌC --- */}
      <div className="p-4 border rounded-md grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 bg-gray-50 mb-4">
        <input
          type="text"
          placeholder="Tìm theo tên hoặc ID..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className="w-full p-2 border rounded-md"
        />
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="w-full p-2 border rounded-md"
        >
          <option value="">Tất cả trạng thái</option>
          <option value="INACTIVE">Chưa kích hoạt</option>
          <option value="ACTIVE">Kích hoạt</option>
        </select>
        <button
          type="button"
          onClick={() => {
            setKeyword('');
            setStatusFilter('');
          }}
          className="w-full sm:w-auto bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold px-4 py-2 rounded-md"
        >
          Đặt lại
        </button>
        <div />
      </div>

      {/* --- NÚT THÊM --- */}
      <div className="flex justify-end mb-4">
        <button
          onClick={() => {
            setEditingPromotion(null);
            setIsModalOpen(true);
          }}
          className="bg-green-600 hover:bg-green-700 text-white font-semibold px-4 py-2 rounded-md whitespace-nowrap"
        >
          + Thêm Khuyến mãi
        </button>
      </div>

      {/* List */}
      <div className="overflow-x-auto bg-white shadow rounded">
        <table className="min-w-full">
          <thead>
            <tr className="bg-gray-100 text-left">
              <th className="p-3">ID</th>
              <th className="p-3">SP chính</th>
              <th className="p-3">SP tặng</th>
              <th className="p-3">Phần trăm</th>
              <th className="p-3">Bắt đầu</th>
              <th className="p-3">Kết thúc</th>
              <th className="p-3">Trạng thái</th>
              <th className="p-3">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {promotions.map(p => (
              <tr key={p.promotionId} className="border-t">
                <td className="p-3">{p.promotionId}</td>
                <td className="p-3">{p.productMain.name}</td>
                <td className="p-3">{p.productFree.name}</td>
                <td className="p-3">{p.discountPercentage}%</td>
                <td className="p-3 whitespace-nowrap">{new Date(p.startDate).toLocaleString('vi-VN')}</td>
                <td className="p-3 whitespace-nowrap">{new Date(p.endDate).toLocaleString('vi-VN')}</td>
                <td className="p-3">{renderStatusBadge(p.status)}</td>
                <td className="p-3 space-x-2">
                  <button className="text-blue-600" onClick={() => { setEditingPromotion(p); setIsModalOpen(true); }}>Sửa</button>
                  <button className="text-red-600" onClick={() => { if (window.confirm('Xoá?')) deleteMutation.mutate(p.promotionId); }}>Xoá</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination currentPage={filters.page ?? 1} totalPages={totalPages} onPageChange={page => setFilters(f => ({ ...f, page }))} />

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={editingPromotion ? 'Cập nhật KM' : 'Thêm KM'}>
        <PromotionForm initialData={editingPromotion} onSuccess={() => { setIsModalOpen(false); }} />
      </Modal>
    </div>
  );
};

export default PromotionManagementPage;
