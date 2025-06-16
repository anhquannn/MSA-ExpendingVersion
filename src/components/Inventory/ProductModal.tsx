// src/components/Inventory/ProductModal.tsx

import React, { useState, useEffect } from 'react';
import { Product, BranchStock, mockBranches } from '../../types/inventory'; // Import mockBranches

interface ProductModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (formData: ProductFormData) => void | Promise<void>;
  initialData: ProductFormData | null;
  isEditing: boolean;
}

interface ProductFormData {
  id?: string;
  name: string;
  category: string;
  unit: string;
  lowStockThreshold: number;
  branchStocks: { branchId: string; stock: number; }[];
}

const ProductModal: React.FC<ProductModalProps> = ({
  isOpen,
  onClose,
  onSave,
  initialData,
  isEditing,
}) => {
  const [formData, setFormData] = useState<ProductFormData>(initialData || {
    name: '',
    category: '',
    unit: '',
    lowStockThreshold: 0,
    branchStocks: mockBranches.map(branch => ({ branchId: branch.id, stock: 0 })),
  });

  const [categories, setCategories] = useState<string[]>([]);
  const [branches, setBranches] = useState<{ id: string; name: string }[]>([]);

  useEffect(() => {
    // Tải danh sách category và branch khi modal mở
    const loadDependencies = async () => {
      // Giả lập API call để lấy categories và branches
      // Bạn có thể import inventoryService và gọi inventoryService.getCategories() và inventoryService.getBranches()
      const mockCategories = ['Trái cây', 'Sữa & Sản phẩm từ sữa', 'Gạo & Ngũ cốc', 'Bánh kẹo', 'Đồ uống', 'Thực phẩm khô'];
      setCategories(mockCategories.sort());
      setBranches(mockBranches); // Sử dụng mockBranches đã import
    };
    if (isOpen) {
      loadDependencies();
      setFormData(initialData || {
        name: '',
        category: '',
        unit: '',
        lowStockThreshold: 0,
        branchStocks: mockBranches.map(branch => ({ branchId: branch.id, stock: 0 })),
      });
    }
  }, [isOpen, initialData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: name === 'lowStockThreshold' ? Number(value) : value }));
  };

  const handleBranchStockChange = (branchId: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      branchStocks: prev.branchStocks.map((bs) =>
        bs.branchId === branchId ? { ...bs, stock: Number(value) } : bs
      ),
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center z-50">
      <div className="bg-white p-8 rounded-lg shadow-xl w-full max-w-lg">
        <h3 className="text-2xl font-bold mb-6 text-gray-800">
          {isEditing ? 'Chỉnh Sửa Sản Phẩm' : 'Thêm Sản Phẩm Mới'}
        </h3>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 gap-4 mb-4">
            <div>
              <label htmlFor="name" className="block text-gray-700 text-sm font-bold mb-2">
                Tên Sản Phẩm:
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
              <label htmlFor="category" className="block text-gray-700 text-sm font-bold mb-2">
                Loại Sản Phẩm:
              </label>
              <select
                id="category"
                name="category"
                value={formData.category}
                onChange={handleChange}
                className="shadow border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              >
                <option value="">Chọn loại</option>
                {categories.map((cat) => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>
            <div>
              <label htmlFor="unit" className="block text-gray-700 text-sm font-bold mb-2">
                Đơn Vị:
              </label>
              <input
                type="text"
                id="unit"
                name="unit"
                value={formData.unit}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              />
            </div>
            <div>
              <label htmlFor="lowStockThreshold" className="block text-gray-700 text-sm font-bold mb-2">
                Ngưỡng Cảnh Báo Hết Hàng:
              </label>
              <input
                type="number"
                id="lowStockThreshold"
                name="lowStockThreshold"
                value={formData.lowStockThreshold}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                required
              />
            </div>

            {/* Tồn kho theo chi nhánh */}
            <h4 className="text-lg font-semibold text-gray-700 mt-4 col-span-full">Tồn Kho Theo Chi Nhánh:</h4>
            {branches.map(branch => (
              <div key={branch.id} className="flex items-center space-x-2">
                <label className="block text-gray-700 text-sm font-bold w-1/2">
                  {branch.name}:
                </label>
                <input
                  type="number"
                  value={formData.branchStocks.find(bs => bs.branchId === branch.id)?.stock || 0}
                  onChange={(e) => handleBranchStockChange(branch.id, e.target.value)}
                  className="shadow appearance-none border rounded w-1/2 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  min="0"
                  required
                />
              </div>
            ))}

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

export default ProductModal;