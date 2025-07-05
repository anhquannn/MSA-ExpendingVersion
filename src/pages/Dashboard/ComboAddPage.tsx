// src/pages/Dashboard/ComboAddPage.tsx
// Trang tạo mới combo sản phẩm

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import Select, { MultiValue } from 'react-select';

import { productService, Product } from '../../services/productService';
import { productComboService, ProductComboCreatePayload } from '../../services/productComboService';

interface OptionType { value: number; label: string; }

const ComboAddPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // === FORM STATE ===
  const [comboForm, setComboForm] = useState<{ name: string; finalPrice?: number; discountPercent?: number }>({
    name: '',
    finalPrice: undefined,
    discountPercent: undefined,
  });
  const [selectedProducts, setSelectedProducts] = useState<MultiValue<OptionType>>([]);

  // === FETCH PRODUCTS FOR SELECT ===
  const { data: productResp } = useQuery({
    queryKey: ['allProductsForCombo'],
    queryFn: () => productService.getProducts({ page: 1, pageSize: 200 }),
  });
  const productOptions: OptionType[] = (productResp?.productsPage?.content ?? []).map((p: Product) => ({
    value: p.productId,
    label: p.name,
  }));

  // === MUTATION ===
  const createComboMutation = useMutation({
    mutationFn: (payload: ProductComboCreatePayload) => productComboService.createCombo(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['allCombos'] });
      navigate('/dashboard/products');
    },
  });

  // === HANDLERS ===
  const handleFormChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setComboForm(prev => ({ ...prev, [name]: name === 'finalPrice' || name === 'discountPercent' ? Number(value) : value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedProducts.length === 0) {
      alert('Vui lòng chọn ít nhất 1 sản phẩm cho combo.');
      return;
    }
    const payload: ProductComboCreatePayload = {
      name: comboForm.name,
      finalPrice: comboForm.finalPrice,
      discountPercent: comboForm.discountPercent,
      productIds: selectedProducts.map(opt => opt.value),
    };
    createComboMutation.mutate(payload);
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold mb-6 text-gray-800">Tạo Combo Sản phẩm</h1>
      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label htmlFor="name" className="block font-medium text-gray-700 mb-1">Tên Combo *</label>
          <input
            id="name"
            name="name"
            type="text"
            required
            value={comboForm.name}
            onChange={handleFormChange}
            className="w-full border rounded-md p-2"
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label htmlFor="finalPrice" className="block font-medium text-gray-700 mb-1">Giá cuối (tuỳ chọn)</label>
            <input
              id="finalPrice"
              name="finalPrice"
              type="number"
              value={comboForm.finalPrice ?? ''}
              onChange={handleFormChange}
              className="w-full border rounded-md p-2"
            />
          </div>
          <div>
            <label htmlFor="discountPercent" className="block font-medium text-gray-700 mb-1">% Giảm giá (tuỳ chọn)</label>
            <input
              id="discountPercent"
              name="discountPercent"
              type="number"
              value={comboForm.discountPercent ?? ''}
              onChange={handleFormChange}
              className="w-full border rounded-md p-2"
            />
          </div>
        </div>

        <div>
          <label className="block font-medium text-gray-700 mb-1">Chọn sản phẩm *</label>
          <Select
            options={productOptions}
            isMulti
            value={selectedProducts}
            onChange={setSelectedProducts}
            className="react-select-container"
            classNamePrefix="react-select"
          />
        </div>

        <div className="flex justify-end space-x-4 pt-4">
          <button type="button" onClick={() => navigate('/dashboard/products')} className="bg-gray-200 text-gray-800 font-bold py-2 px-6 rounded-md hover:bg-gray-300">
            Hủy
          </button>
          <button type="submit" disabled={createComboMutation.isPending} className="bg-indigo-600 text-white font-bold py-2 px-6 rounded-md hover:bg-indigo-700 disabled:bg-gray-400">
            {createComboMutation.isPending ? 'Đang lưu...' : 'Lưu Combo'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ComboAddPage;
