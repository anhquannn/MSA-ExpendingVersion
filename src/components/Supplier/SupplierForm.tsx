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
  // File ảnh người dùng chọn
  const [imageFile, setImageFile] = useState<File | null>(null);

  // Điền dữ liệu vào form khi ở chế độ sửa
  useEffect(() => {
    if (initialData) {
      const supabaseBaseUrl = process.env.REACT_APP_SUPABASE_URL;
      const processedImage = initialData.image && !initialData.image.startsWith('http') && supabaseBaseUrl
        ? `${supabaseBaseUrl}/storage/v1/object/public/msa/${initialData.image}`
        : initialData.image || '';
      setFormData({
        name: initialData.name,
        address: initialData.address,
        contact: initialData.contact,
        image: processedImage,
      });
    }
  }, [initialData]);

  const mutation = useMutation({
    mutationFn: (payload: SupplierPayload) => {
      if (initialData?.supplierId) {
        return supplierService.updateSupplierWithImage(initialData.supplierId, payload, imageFile || undefined);
      }
      return supplierService.createSupplierWithImage(payload, imageFile || undefined);
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

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setImageFile(file);
      // Hiển thị preview tạm thời
      const previewUrl = URL.createObjectURL(file);
      setFormData(prev => ({ ...prev, image: previewUrl }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.contact) {
      alert('Vui lòng điền Tên và Thông tin liên hệ.');
      return;
    }

    try {
      const payload: SupplierPayload = {
        name: formData.name!,
        contact: formData.contact!,
        address: formData.address || '',
        image: formData.image as string | null ?? null,
      };
      mutation.mutate(payload);
    } catch (err) {
      console.error('Lỗi upload ảnh:', err);
      alert('Lỗi khi tải ảnh lên. Vui lòng thử lại.');
    }
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
        <label className="block text-sm font-medium">Ảnh đại diện</label>
        <input type="file" accept="image/*" onChange={handleFileChange} className="mt-1 w-full" />
        {formData.image && (
          <img src={formData.image as string} alt="preview" className="h-16 mt-2 object-cover rounded" />
        )}
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