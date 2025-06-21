// File: src/components/Supplier/SupplierForm.tsx

import React, { useState, useEffect } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { supplierService, Supplier, SupplierPayload } from '../../services/supplierService';

type SupplierFormProps = {
  onSuccess: () => void;
  initialData?: Supplier | null;
};

const SupplierForm: React.FC<SupplierFormProps> = ({ onSuccess, initialData }) => {
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState<Partial<SupplierPayload>>({
    name: '',
    address: '',
    contact: '',
    image: '',
  });

  // Điền dữ liệu vào form khi ở chế độ sửa
  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name,
        address: initialData.address,
        contact: initialData.contact,
        image: initialData.image || '',
      });
    }
  }, [initialData]);

  const mutation = useMutation({
    mutationFn: (payload: SupplierPayload) => {
      if (initialData?.supplierId) {
        return supplierService.updateSupplier(initialData.supplierId, payload);
      }
      return supplierService.createSupplier(payload);
    },
    onSuccess: () => {
      alert(initialData ? 'Cập nhật nhà cung cấp thành công!' : 'Thêm nhà cung cấp thành công!');
      // Báo cho React Query rằng dữ liệu 'suppliers' đã cũ và cần được làm mới
      queryClient.invalidateQueries({ queryKey: ['suppliers'] });
      onSuccess(); // Gọi callback để đóng modal
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.contact) {
      alert('Vui lòng điền Tên và Thông tin liên hệ.');
      return;
    }
    mutation.mutate(formData as SupplierPayload);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium">Tên nhà cung cấp (*)</label>
        <input name="name" value={formData.name} onChange={handleChange} required className="mt-1 w-full p-2 border rounded-md" />
      </div>
      <div>
        <label className="block text-sm font-medium">Thông tin liên hệ (*)</label>
        <input name="contact" value={formData.contact} onChange={handleChange} required className="mt-1 w-full p-2 border rounded-md" />
      </div>
      <div>
        <label className="block text-sm font-medium">Địa chỉ</label>
        <input name="address" value={formData.address} onChange={handleChange} className="mt-1 w-full p-2 border rounded-md" />
      </div>
      <div>
        <label className="block text-sm font-medium">URL Hình ảnh</label>
        <input name="image" value={formData.image || ''} onChange={handleChange} className="mt-1 w-full p-2 border rounded-md" />
      </div>
      <div className="flex justify-end pt-2">
        <button type="submit" disabled={mutation.isPending} className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:bg-gray-400">
          {mutation.isPending ? 'Đang lưu...' : 'Lưu'}
        </button>
      </div>
    </form>
  );
};

export default SupplierForm;