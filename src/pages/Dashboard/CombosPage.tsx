// src/pages/Dashboard/CombosPage.tsx
// Danh sách Combo sản phẩm

import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { productComboService, ProductCombo } from '../../services/productComboService';
import { productService, Product } from '../../services/productService';

const CombosPage: React.FC = () => {
  const navigate = useNavigate();

  const { data: combos, isLoading, isError } = useQuery({
    queryKey: ['allCombos'],
    queryFn: () => productComboService.getAllCombos(),
  });

  // fetch all products once to map ids->name
  const { data: allProducts } = useQuery({
    queryKey: ['allProductsForComboList'],
    queryFn: () => productService.getProducts({ page: 1, pageSize: 500 }),
  });
  const productMap: Record<number, string> = {};
  if (allProducts?.productsPage?.content) {
    (allProducts.productsPage.content as Product[]).forEach(p => { productMap[p.productId] = p.name; });
  }

  if (isLoading) {
    return <div className="p-6 text-center">Đang tải dữ liệu combo...</div>;
  }

  if (isError) {
    return <div className="p-6 text-center text-red-500">Lỗi khi tải dữ liệu combo.</div>;
  }

  return (
    <div className="bg-white p-6 rounded-lg shadow-md space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-800">Quản lý Combo</h1>
        <button
          onClick={() => navigate('/dashboard/combos/add')}
          className="bg-indigo-600 text-white font-bold py-2 px-4 rounded-md hover:bg-indigo-700 transition-colors"
        >
          + Thêm Combo
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full bg-white">
          <thead className="bg-gray-100">
            <tr>
              <th className="py-3 px-4 text-left">ID</th>
              <th className="py-3 px-4 text-left">Tên Combo</th>
              <th className="py-3 px-4 text-left">Giá cuối</th>
              <th className="py-3 px-4 text-left">% Giảm</th>
              <th className="py-3 px-4 text-left">Số SP</th>
              <th className="py-3 px-4 text-left">Sản phẩm</th>
            </tr>
          </thead>
          <tbody>
            {combos && combos.length === 0 && (
              <tr>
                <td colSpan={5} className="p-4 text-center text-gray-500">Chưa có combo nào.</td>
              </tr>
            )}
            {combos?.map((combo: ProductCombo) => (
              <tr key={combo.comboId} className="border-b hover:bg-gray-50">
                <td className="py-3 px-4">{combo.comboId}</td>
                <td className="py-3 px-4 font-medium">{combo.name}</td>
                <td className="py-3 px-4">{combo.finalPrice ? new Intl.NumberFormat('vi-VN').format(combo.finalPrice) : '-'}</td>
                <td className="py-3 px-4">{combo.discountPercent ?? '-'}</td>
                <td className="py-3 px-4">{combo.productIds?.length ?? '-'}</td>
                <td className="py-3 px-4">{combo.productIds?.map(id=>productMap[id]).filter(Boolean).join(', ')}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default CombosPage;
