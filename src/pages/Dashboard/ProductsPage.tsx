// export default ProductsPage;

// src/pages/ProductsPage.tsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
// --- Import các service và types ---
import { productService, Product, ProductFilterParams, ProductUpdatePayload } from '../../services/productService';
import { categoryService, Category } from '../../services/categoryService';
import { supplierService, Supplier } from '../../services/supplierService';

// --- Custom Hook để Debounce (trì hoãn) việc gọi API khi người dùng gõ tìm kiếm ---
function useDebounce(value: string, delay: number) {
  const [debouncedValue, setDebouncedValue] = useState(value);
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);
    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);
  return debouncedValue;
}

// Component hiển thị danh sách tên sản phẩm đính kèm
const AttachedProductsCell: React.FC<{ productId: number }> = ({ productId }) => {
  const { data, isLoading } = useQuery({
    queryKey: ['attachedProducts', productId],
    queryFn: () => productService.getAttachedProductsPaged(productId, { page: 1, pageSize: 100 }),
  });

  if (isLoading) return <span>...</span>;

  const names = (data?.content || []).map((p) => p.name).join(', ');
  return <span>{names || '-'}</span>;
};


const ProductsPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [filters, setFilters] = useState<ProductFilterParams>({
    keyword: '',
    categoryId: undefined,
    supplierId: undefined,
    minPrice: undefined,
    maxPrice: undefined,
    page: 1,
    pageSize: 100,
  });

  const debouncedSearchTerm = useDebounce(filters.keyword || '', 500);
  const {
    data: productsResponse,
    isLoading: isLoadingProducts,
    isError: isErrorProducts,
  } = useQuery({
    queryKey: ['products', { ...filters, keyword: debouncedSearchTerm }],
    queryFn: () => productService.getProducts({ ...filters, keyword: debouncedSearchTerm }),
    placeholderData: keepPreviousData,
  });

  const { data: categoriesResponse } = useQuery({
    queryKey: ['allCategories'],
    queryFn: () => categoryService.getCategories({ page: 1, pageSize: 999 }),
  });
  const { data: suppliersResponse } = useQuery({
    queryKey: ['allSuppliers'],
    queryFn: () => supplierService.getSuppliers({}),
  });

  const products = productsResponse?.productsPage?.content || [];
  const totalPages = productsResponse?.productsPage?.totalPages || 1;
  const categories = categoriesResponse?.content || [];
  const suppliers = suppliersResponse?.content || [];

  const deleteProductMutation = useMutation({
    mutationFn: (productId: number) => productService.deleteProduct(productId),
    onSuccess: () => {
      console.log('Xóa sản phẩm thành công!');
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
    onError: (error) => {
      console.error('Lỗi khi xóa sản phẩm:', error);
      alert(`Lỗi: ${error instanceof Error ? error.message : 'Lỗi không xác định'}`);
    }
  });

  const updateProductMutation = useMutation({
    mutationFn: ({ productId, payload }: { productId: number; payload: ProductUpdatePayload }) =>
      productService.updateProduct(productId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
      console.log("Cập nhật thành công!");
    }
  });

  // --- EVENT HANDLERS ---

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value === '' ? undefined : value,
      page: 1,
    }));
  };

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({
      ...prev,
      page: newPage,
    }));
  };

  const handleDeleteClick = (product: Product) => {
    if (window.confirm(`Bạn có chắc muốn xóa sản phẩm "${product.name}"?`)) {
      deleteProductMutation.mutate(product.productId);
    }
  };
  if (isLoadingProducts) {
    return <div className="p-6 text-center">Đang tải dữ liệu sản phẩm...</div>;
  }
  if (isErrorProducts) {
    return <div className="p-6 text-center text-red-500">Lỗi khi tải dữ liệu sản phẩm.</div>;
  }

  return (
    <div className="bg-white p-6 rounded-lg shadow-md space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Quản lý Sản phẩm</h1>

      {/* --- KHU VỰC BỘ LỌC --- */}
      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4 p-4 border rounded-md">
        <input
          type="text"
          name="keyword"
          placeholder="Tìm theo tên hoặc ID..."
          value={filters.keyword}
          onChange={handleFilterChange}
          className="w-full p-2 border rounded-md"
        />
        <select name="categoryId" value={filters.categoryId || ''} onChange={handleFilterChange} className="w-full p-2 border rounded-md">
          <option value="">Tất cả danh mục</option>
          {categories.map(cat => <option key={cat.categoryId} value={cat.categoryId}>{cat.name}</option>)}
        </select>
        <select name="supplierId" value={filters.supplierId || ''} onChange={handleFilterChange} className="w-full p-2 border rounded-md">
          <option value="">Tất cả nhà cung cấp</option>
          {suppliers.map(sup => <option key={sup.supplierId} value={sup.supplierId}>{sup.name}</option>)}
        </select>
        <input
          type="number"
          name="minPrice"
          placeholder="Giá từ..."
          value={filters.minPrice || ''}
          onChange={handleFilterChange}
          className="w-full p-2 border rounded-md"
        />
        <input
          type="number"
          name="maxPrice"
          placeholder="Giá đến..."
          value={filters.maxPrice || ''}
          onChange={handleFilterChange}
          className="w-full p-2 border rounded-md"
        />
      </div>

      {/* --- CÁC NÚT THÊM --- */}
      <div className="flex flex-wrap gap-2 justify-end">
        <button
          onClick={() => navigate('/dashboard/products/add')}
          className="bg-green-600 text-white font-bold py-2 px-4 rounded-md hover:bg-green-700 transition-colors"
        >
          + Thêm Sản Phẩm
        </button>
      </div>

      {/* --- BẢNG HIỂN THỊ SẢN PHẨM --- */}
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white">
          <thead className="bg-gray-100">
            <tr>
              <th className="py-3 px-4 text-left">ID</th>
              <th className="py-3 px-4 text-left">Ảnh</th>
              <th className="py-3 px-4 text-left">Tên sản phẩm</th>
              <th className="py-3 px-4 text-left">Giá</th>
              <th className="py-3 px-4 text-left">Danh mục</th>
              <th className="py-3 px-4 text-left">Nhà cung cấp</th>
              <th className="py-3 px-4 text-left">SP đính kèm</th>
              <th className="py-3 px-4 text-left">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {products.map(product => (
              <tr key={product.productId} className="border-b hover:bg-gray-50">
                <td className="py-3 px-4">{product.productId}</td>
                <td className="py-3 px-4">
                  <img
                    src={product.productImageResponses?.[0]?.imageUrl || 'https://via.placeholder.com/64'}
                    alt={product.name}
                    className="h-16 w-16 object-cover rounded-md"
                  />
                </td>
                <td className="py-3 px-4 font-medium">{product.name}</td>
                <td className="py-3 px-4">{new Intl.NumberFormat('vi-VN').format(product.price)}đ</td>
                <td className="py-3 px-4">{product.category.name}</td>
                <td className="py-3 px-4">{product.supplier.name}</td>
                <td className="py-3 px-4"><AttachedProductsCell productId={product.productId} /></td>
                <td className="py-3 px-4 space-x-2">
                  <button
                    onClick={() => navigate(`/dashboard/products/edit/${product.productId}`)}
                    className="text-blue-600 hover:underline"
                  >
                    Sửa
                  </button>
                  <button onClick={() => handleDeleteClick(product)} disabled={deleteProductMutation.isPending} className="text-red-600 hover:underline disabled:text-gray-400">Xóa</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* --- KHU VỰC PHÂN TRANG --- */}
      <div className="flex justify-between items-center mt-6">
        <p>Trang {filters.page} trên {totalPages}</p>
        <div className="space-x-2">
          <button
            onClick={() => handlePageChange(filters.page! - 1)}
            disabled={filters.page === 1}
            className="px-4 py-2 border rounded-md disabled:opacity-50"
          >
            Trước
          </button>
          <button
            onClick={() => handlePageChange(filters.page! + 1)}
            disabled={filters.page === totalPages}
            className="px-4 py-2 border rounded-md disabled:opacity-50"
          >
            Sau
          </button>
        </div>
      </div>

    </div>
  );
};

export default ProductsPage;