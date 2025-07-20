// File: src/components/Inventory/AddInventoryForm.tsx

import React, { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { inventoryService, InventoryCreateParams } from '../../services/inventoryService';
import { branchService, Branch } from '../../services/branchService';

type AddInventoryFormProps = {
  onSuccess: () => void;
};

const AddInventoryForm: React.FC<AddInventoryFormProps> = ({ onSuccess }) => {
  const [formData, setFormData] = useState<Partial<InventoryCreateParams>>({
    name: '',
    address: '',
    contact: '',
    branchId: undefined,
    totalRevenue:1
  });

  // Lấy danh sách chi nhánh để điền vào dropdown
  const { data: branches = [], isLoading: isLoadingBranches } = useQuery({
    queryKey: ['allBranchesForSelect'],
    queryFn: () => branchService.getAllBranchesWithPaging({ pageSize: 999 }).then(res => res.content),
  });

  const createInventoryMutation = useMutation({
    mutationFn: (payload: InventoryCreateParams) => inventoryService.createInventory(payload),
    onSuccess: () => {
      alert('Thêm kho hàng thành công!');
      onSuccess();
    },
    onError: (err: Error) => {
      alert(`Lỗi: ${err.message}`);
    }
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: name === 'branchId' ? Number(value) : value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name?.trim() || !formData.address?.trim() || !formData.contact?.trim() || !formData.branchId) {
      alert('Vui lòng nhập đầy đủ Tên kho, Địa chỉ, Số liên hệ và chọn Chi nhánh.');
      return;
    }
    createInventoryMutation.mutate(formData as InventoryCreateParams);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="inventory-name" className="block text-sm font-medium">Tên kho (*)</label>
        <input type="text" id="inventory-name" name="name" value={formData.name} onChange={handleChange} required className="mt-1 w-full p-2 border rounded-md" />
      </div>
      <div>
        <label htmlFor="inventory-branch" className="block text-sm font-medium">Thuộc chi nhánh (*)</label>
        <select id="inventory-branch" name="branchId" value={formData.branchId || ''} onChange={handleChange} required disabled={isLoadingBranches} className="mt-1 w-full p-2 border rounded-md">
            <option value="">{isLoadingBranches ? 'Đang tải chi nhánh...' : 'Chọn chi nhánh'}</option>
            {branches.map((branch: Branch) => (
                <option key={branch.branchId} value={branch.branchId}>{branch.name}</option>
            ))}
        </select>
      </div>
      <div>
        <label htmlFor="inventory-address" className="block text-sm font-medium">Địa chỉ</label>
        <input type="text" id="inventory-address" name="address" value={formData.address} onChange={handleChange} className="mt-1 w-full p-2 border rounded-md" />
      </div>
      <div>
        <label htmlFor="inventory-contact" className="block text-sm font-medium">Thông tin liên hệ</label>
        <input type="text" id="inventory-contact" name="contact" value={formData.contact} onChange={handleChange} className="mt-1 w-full p-2 border rounded-md" />
      </div>
      <div className="flex justify-end">
        <button type="submit" disabled={createInventoryMutation.isPending} className="bg-green-600 text-white font-bold py-2 px-4 rounded-md hover:bg-green-700 disabled:bg-gray-400">
          {createInventoryMutation.isPending ? 'Đang lưu...' : 'Lưu Kho Hàng'}
        </button>
      </div>
    </form>
  );
};

export default AddInventoryForm;