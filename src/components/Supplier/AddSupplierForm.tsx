// File: src/components/Supplier/AddSupplierForm.tsx

import React, { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { supplierService, SupplierPayload } from '../../services/supplierService';

type AddSupplierFormProps = {
  onSuccess: () => void;
};

const AddSupplierForm: React.FC<AddSupplierFormProps> = ({ onSuccess }) => {
  const [formData, setFormData] = useState<Partial<SupplierPayload>>({
    name: '',
    address: '',
    contact: '',
  });

  const createSupplierMutation = useMutation({
    mutationFn: (payload: SupplierPayload) => supplierService.createSupplier(payload),
    onSuccess: () => {
      alert('Thêm nhà cung cấp thành công!');
      onSuccess(); // Báo cho component cha (để đóng modal và làm mới)
    },
    onError: (err: Error) => {
      alert(`Lỗi: ${err.message}`);
    }
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.contact) {
      alert('Vui lòng nhập Tên và Thông tin liên hệ.');
      return;
    }
    createSupplierMutation.mutate(formData as SupplierPayload);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="supplier-name" className="block text-sm font-medium">Tên nhà cung cấp (*)</label>
        <input
          type="text"
          id="supplier-name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          className="mt-1 w-full p-2 border rounded-md"
          required
        />
      </div>
       <div>
        <label htmlFor="supplier-contact" className="block text-sm font-medium">Thông tin liên hệ (*)</label>
        <input
          type="text"
          id="supplier-contact"
          name="contact"
          value={formData.contact}
          onChange={handleChange}
          className="mt-1 w-full p-2 border rounded-md"
          required
        />
      </div>
      <div>
        <label htmlFor="supplier-address" className="block text-sm font-medium">Địa chỉ</label>
        <input
          type="text"
          id="supplier-address"
          name="address"
          value={formData.address}
          onChange={handleChange}
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>
      <div className="flex justify-end">
        <button
          type="submit"
          disabled={createSupplierMutation.isPending}
          className="bg-green-600 text-white font-bold py-2 px-4 rounded-md hover:bg-green-700 disabled:bg-gray-400"
        >
          {createSupplierMutation.isPending ? 'Đang lưu...' : 'Lưu Nhà Cung Cấp'}
        </button>
      </div>
    </form>
  );
};

export default AddSupplierForm;