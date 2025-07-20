// src/components/Branch/BranchModal.tsx

import React, { useState, useEffect } from 'react';
import { BranchFormData } from '../../types/branch'; // Import BranchFormData

interface BranchModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (formData: BranchFormData) => void;
  initialData: BranchFormData | null;
  isEditing: boolean;
}

const BranchModal: React.FC<BranchModalProps> = ({
  isOpen,
  onClose,
  onSave,
  initialData,
  isEditing,
}) => {
  const [formData, setFormData] = useState<BranchFormData>(initialData || {
    name: '',
    email: '',
    phone: '',
    city: '',
    cityCode: '',
    district: '',
    districtCode: '',
    street: '',
    ward: '',
    wardCode: '',
    status: 'active', // Mặc định khi thêm mới
  });

  useEffect(() => {
    if (isOpen) {
      setFormData(initialData || {
        name: '',
        email: '',
        phone: '',
        city: '',
        cityCode: '',
        district: '',
        districtCode: '',
        street: '',
        ward: '',
        wardCode: '',
        status: 'active',
      });
    }
  }, [isOpen, initialData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.cityCode || !formData.districtCode || !formData.wardCode) {
      alert('Vui lòng nhập đầy đủ mã Thành phố, Quận/Huyện và Phường/Xã.');
      return;
    }
    e.preventDefault();
    onSave(formData);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center z-50">
      <div className="bg-white p-8 rounded-lg shadow-xl w-full max-w-lg overflow-y-auto max-h-[90vh]">
        <h3 className="text-2xl font-bold mb-6 text-gray-800">
          {isEditing ? 'Chỉnh Sửa Chi Nhánh' : 'Thêm Chi Nhánh Mới'}
        </h3>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 gap-4 mb-4">
            <div>
              <label htmlFor="name" className="block text-gray-700 text-sm font-bold mb-2">
                Tên Chi Nhánh:
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              />
            </div>
            <div>
              <label htmlFor="email" className="block text-gray-700 text-sm font-bold mb-2">
                Email:
              </label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              />
            </div>
            <div>
              <label htmlFor="phone" className="block text-gray-700 text-sm font-bold mb-2">
                Số Điện Thoại:
              </label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              />
            </div>
            <div>
              <label htmlFor="city" className="block text-gray-700 text-sm font-bold mb-2">
                Thành Phố:
              </label>
              <input
                type="text"
                id="city"
                name="city"
                value={formData.city}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              />
            </div>
            <div>
              <label htmlFor="cityCode" className="block text-gray-700 text-sm font-bold mb-2">
                Mã Thành phố:
              </label>
              <input
                type="text"
                id="cityCode"
                name="cityCode"
                value={formData.cityCode || ''}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              />
            </div>
            <div>
              <label htmlFor="district" className="block text-gray-700 text-sm font-bold mb-2">
                Quận/Huyện:
              </label>
              <input
                type="text"
                id="district"
                name="district"
                value={formData.district}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              />
            </div>
            <div>
              <label htmlFor="districtCode" className="block text-gray-700 text-sm font-bold mb-2">
                Mã Quận/Huyện:
              </label>
              <input
                type="text"
                id="districtCode"
                name="districtCode"
                value={formData.districtCode || ''}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              />
            </div>
            <div>
              <label htmlFor="street" className="block text-gray-700 text-sm font-bold mb-2">
                Đường:
              </label>
              <input
                type="text"
                id="street"
                name="street"
                value={formData.street}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              />
            </div>
            <div>
              <label htmlFor="ward" className="block text-gray-700 text-sm font-bold mb-2">
                Phường/Xã (Tùy chọn):
              </label>
              <input
                type="text"
                id="ward"
                name="ward"
                value={formData.ward || ''}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              />
            </div>
            <div>
              <label htmlFor="wardCode" className="block text-gray-700 text-sm font-bold mb-2">
                Mã Phường/Xã:
              </label>
              <input
                type="text"
                id="wardCode"
                name="wardCode"
                value={formData.wardCode || ''}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              />
            </div>
            {isEditing && ( // Chỉ hiển thị trạng thái khi chỉnh sửa
              <div>
                <label htmlFor="status" className="block text-gray-700 text-sm font-bold mb-2">
                  Trạng Thái:
                </label>
                <select
                  id="status"
                  name="status"
                  value={formData.status}
                  onChange={handleChange}
                  className="shadow border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  required
                >
                  <option value="active">Đang Hoạt Động</option>
                  <option value="inactive">Ngừng Hoạt Động</option>
                </select>
              </div>
            )}
          </div>
          <div className="flex justify-end gap-4 mt-6">
            <button
              type="button"
              onClick={onClose}
              className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-md transition duration-300"
            >
              Hủy
            </button>
            <button
              type="submit"
              className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-md transition duration-300"
            >
              Lưu
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default BranchModal;