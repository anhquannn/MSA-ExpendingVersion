// File: src/components/Category/AddCategoryForm.tsx

import React, { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { categoryService, CreateCategoryPayload, Category } from '../../services/categoryService';

type AddCategoryFormProps = {
  onSuccess: () => void; // Callback sẽ được gọi khi thêm thành công
};

const AddCategoryForm: React.FC<AddCategoryFormProps> = ({ onSuccess }) => {
  // --- STATE CHO CÁC TRƯỜNG INPUT ---
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [parentCategoryId, setParentCategoryId] = useState<string>(''); // State cho dropdown

  // --- LẤY DỮ LIỆU ĐỂ ĐIỀN VÀO DROPDOWN ---
  const { data: categories = [], isLoading: isLoadingCategories } = useQuery({
    queryKey: ['allCategoriesForParentSelect'],
    queryFn: () => categoryService.getCategories({ pageSize: 999 }).then(res => res.content),
  });

  // --- MUTATION ĐỂ TẠO CATEGORY ---
  const createCategoryMutation = useMutation({
    mutationFn: (payload: CreateCategoryPayload) => categoryService.createCategory(payload),
    onSuccess: () => {
      alert('Thêm danh mục thành công!');
      onSuccess(); // Báo cho component cha để đóng modal và làm mới danh sách
    },
    onError: (err: Error) => {
      alert(`Lỗi: ${err.message}`);
    }
  });

  // --- HÀM SUBMIT FORM ---
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name) {
      alert('Vui lòng nhập tên danh mục.');
      return;
    }
    const payload: CreateCategoryPayload = { name, description };
    if (parentCategoryId) {
      payload.parentCategoryId = Number(parentCategoryId);
    }
    createCategoryMutation.mutate(payload);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {/* Input Tên danh mục */}
      <div>
        <label htmlFor="category-name" className="block text-sm font-medium text-gray-700">Tên danh mục (*)</label>
        <input
          type="text"
          id="category-name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="mt-1 w-full p-2 border rounded-md"
          required
        />
      </div>
      
      {/* === DROPDOWN DANH MỤC CHA (PHẦN BỊ THIẾU) === */}
      <div>
        <label htmlFor="parent-category" className="block text-sm font-medium text-gray-700">Danh mục cha</label>
        <select
          id="parent-category"
          value={parentCategoryId}
          onChange={(e) => setParentCategoryId(e.target.value)}
          disabled={isLoadingCategories}
          className="mt-1 w-full p-2 border rounded-md"
        >
          <option value="">
            {isLoadingCategories ? 'Đang tải...' : '-- Không có danh mục cha --'}
          </option>
          {categories.map((cat: Category) => (
            <option key={cat.categoryId} value={cat.categoryId}>
              {cat.name}
            </option>
          ))}
        </select>
      </div>

      {/* Textarea Mô tả */}
      <div>
        <label htmlFor="category-description" className="block text-sm font-medium text-gray-700">Mô tả</label>
        <textarea
          id="category-description"
          rows={3}
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className="mt-1 w-full p-2 border rounded-md"
        />
      </div>

      {/* Nút bấm */}
      <div className="flex justify-end">
        <button
          type="submit"
          disabled={createCategoryMutation.isPending}
          className="bg-green-600 text-white font-bold py-2 px-4 rounded-md hover:bg-green-700 disabled:bg-gray-400"
        >
          {createCategoryMutation.isPending ? 'Đang lưu...' : 'Lưu Danh Mục'}
        </button>
      </div>
    </form>
  );
};

export default AddCategoryForm;