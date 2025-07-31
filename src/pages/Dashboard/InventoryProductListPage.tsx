// File: src/pages/inventory/InventoryProductListPage.tsx
import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useDebounce } from 'use-debounce';
import { inventoryProductService, InventoryProductFilterParams, InventoryProductUpdatePayload, InventoryProduct, InventoryProductCreatePayload } from '../../services/inventoryProductService';
import { AlertTriangle, Edit, LoaderCircle, Search, X, Plus, Trash } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import { Truck } from 'lucide-react';
import Pagination from '../../components/common/Pagination';
import { productService } from '../../services/productService';

// UI-Only interface, should ideally import these instead
interface ProductImageResponse {
  imageUrl: string;
  primary: boolean;
}
interface Product {
  productId: number;
  name: string;
  price: number;
  unit: string;
  productImageResponses: ProductImageResponse[] | null;
  category: { name: string };
}
interface Inventory {
  inventoryId: number;
  name: string;
}

export function InventoryProductListPage() {
  const { inventoryId } = useParams<{ inventoryId: string }>();
  const numericInventoryId = inventoryId ? parseInt(inventoryId, 10) : 0;
  const queryClient = useQueryClient();

  const [filters, setFilters] = useState<Omit<InventoryProductFilterParams, 'inventoryId'>>({
    page: 1,
    pageSize: 10,
    sortBy: 'stockNumber',
    sortDirection: 'DESC'
  });
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearchTerm] = useDebounce(searchTerm, 500);
  const [editingProduct, setEditingProduct] = useState<InventoryProduct | null>(null);
  const [newStockValue, setNewStockValue] = useState<number>(0);
  const [newMinThreshold, setNewMinThreshold] = useState<number | null | undefined>(undefined);
  const [newMaxThreshold, setNewMaxThreshold] = useState<number | null | undefined>(undefined);

  // === Add Product Modal State ===
  const [isAddModalOpen, setAddModalOpen] = useState(false);
  const [addForm, setAddForm] = useState({
    productId: 0,
    stockNumber: 0,
    stockLevel: 'medium',
    minThreshold: 0,
    maxThreshold: 0,
    expDate: '',
    batchNumber: '',
    discounted: false,
  });

  // Fetch products for dropdown
  const { data: allProductsData } = useQuery({
    queryKey: ['allProducts'],
    queryFn: () => productService.getProducts({ page: 1, pageSize: 100 }),
  });
  // "productsPage" comes from API shape defined in productService
  // Cast to avoid type mismatch between UI-only Product and API Product
  const productOptions: Product[] = (allProductsData?.productsPage?.content ?? []) as unknown as Product[];

  const queryParams: InventoryProductFilterParams = {
    ...filters,
    inventoryId: numericInventoryId
  };

  const {
  data: pagedData,
  isLoading,
  isError,
  error,
} = useQuery({
  queryKey: ['inventoryProducts', queryParams],
  queryFn: () => inventoryProductService.getInventoryProductList(queryParams),
  placeholderData: (previousData) => previousData,
});
const productList = pagedData?.content ?? [];
  const totalPages = pagedData?.totalPages ?? 1;

  // === Mutations ===
  const createMutation = useMutation({
    mutationFn: (payload: InventoryProductCreatePayload) => inventoryProductService.createInventoryProduct(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventoryProducts'] });
      setAddModalOpen(false);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => inventoryProductService.deleteInventoryProduct(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['inventoryProducts'] }),
  });

  const updateStockMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: InventoryProductUpdatePayload }) =>
      inventoryProductService.updateInventoryProduct(id, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventoryProducts'] });
      handleCloseModal();
    },
    onError: (err) => {
      console.error('Lỗi khi cập nhật tồn kho:', err);
    },
  });

  useEffect(() => {
    if (editingProduct) {
      setNewStockValue(editingProduct.stockNumber);
    }
  }, [editingProduct]);

  const handleUpdateFilters = (newFilters: Partial<typeof filters>) => {
    setFilters((prev) => ({ ...prev, ...newFilters, page: 1 }));
  };

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
  };

  const handleEditClick = (product: InventoryProduct) => {
    setEditingProduct(product);
    setNewStockValue(product.stockNumber);
    setNewMinThreshold(product.minThreshold);
    setNewMaxThreshold(product.maxThreshold);
  };
  const handleCloseModal = () => setEditingProduct(null);

  const handleEditFormSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingProduct) return;
    // --- Validate new stock value ---
    if (newStockValue === undefined || newStockValue === null || Number(newStockValue) <= 0) {
      alert('Số lượng phải lớn hơn 0.');
      return;
    }
    const payload: InventoryProductUpdatePayload = {
      stockNumber: newStockValue,
      inventoryId: editingProduct.inventory.inventoryId,
      productId: editingProduct.product.productId,
      stockLevel: 'medium', // TODO: optionally recalculate stock level
    minThreshold: newMinThreshold ?? undefined,
    maxThreshold: newMaxThreshold ?? undefined,
    };
    updateStockMutation.mutate({ id: editingProduct.inventoryProductId, payload });
  };

    // === Add form submit ===
  const handleAddFormSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!addForm.productId) {
      alert('Vui lòng chọn sản phẩm.');
      return;
    }
    if (addForm.stockNumber === undefined || addForm.stockNumber === null || Number(addForm.stockNumber) <= 0) {
      alert('Số lượng phải lớn hơn 0.');
      return;
    }
    const payload: InventoryProductCreatePayload = {
      stockNumber: addForm.stockNumber,
      inventoryId: numericInventoryId,
      productId: addForm.productId,
      stockLevel: addForm.stockLevel as any,
      minThreshold: addForm.minThreshold || undefined,
      maxThreshold: addForm.maxThreshold || undefined,
      expDate: addForm.expDate ? `${addForm.expDate.replace('T', ' ')}:00` : undefined,
      batchNumber: addForm.batchNumber || undefined,
      discounted: addForm.discounted,
    };
    createMutation.mutate(payload);
  };

  const handleDeleteClick = (id: number) => {
    if (window.confirm('Bạn có chắc muốn xóa mục này?')) deleteMutation.mutate(id);
  };

  const formatCurrency = (value: number) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);

  // Safely derive Vietnamese label for stock level (có thể null)
  const getStockLevelText = (level?: string | null) => {
    const l = level?.toLowerCase?.() ?? '';
    if (l === 'low') return 'Thấp';
    if (l === 'high') return 'Cao';
    if (l === 'medium') return 'Trung bình';
    return 'Không xác định';
  };

  // Trả về class màu theo stock level (có thể null)
  const getStockLevelClass = (level?: string | null) => {
    const l = level?.toLowerCase?.() ?? '';
    if (l === 'low') return 'bg-red-100 text-red-800';
    if (l === 'high') return 'bg-green-100 text-green-800';
    if (l === 'medium') return 'bg-yellow-100 text-yellow-800';
    return 'bg-gray-100 text-gray-800';
  };

  return (
    <div className="p-4 md:p-6 lg:p-8 bg-gray-50 min-h-screen font-sans">
      <header className="mb-6 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">Quản lý Tồn kho - Chi nhánh #{inventoryId}</h1>
          <p className="text-gray-500 mt-1">Xem, tìm kiếm, và quản lý sản phẩm trong kho.</p>
        </div>
        <div className="flex flex-col gap-2">
          <Link
          to="/dashboard/transfer-requests"
          className="inline-flex items-center gap-2 bg-blue-600 text-white font-medium px-5 py-2 rounded-lg hover:bg-blue-700 transition-all duration-200 shadow-md"
        >
          <Truck className="w-5 h-5" />
          Yêu cầu vận kho
          </Link>
          <button
            onClick={() => setAddModalOpen(true)}
            className="inline-flex items-center gap-2 bg-green-600 text-white font-medium px-5 py-2 rounded-lg hover:bg-green-700 transition-all duration-200 shadow-md"
          >
            <Plus className="w-5 h-5" />
            Thêm vào kho
          </button>
        </div>
      </header>
      <div className="bg-white p-4 rounded-lg shadow-md mb-6">
        <div className="flex flex-col md:flex-row gap-4 items-center">
          <div className="relative w-full md:w-1/3">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Tìm theo tên sản phẩm..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>

          <div className="flex items-center gap-2 w-full md:w-auto">
            <label htmlFor="sortBy" className="text-gray-600 text-sm">
              Sắp xếp:
            </label>
            <select
              id="sortBy"
              value={filters.sortBy}
              onChange={(e) => handleUpdateFilters({ sortBy: e.target.value })}
              className="border rounded-lg px-2 py-2 text-sm"
            >
              <option value="product.name">Tên sản phẩm</option>
              <option value="stockNumber">Số lượng tồn</option>
              <option value="currentPrice">Giá bán</option>
            </select>
            <select
              value={filters.sortDirection}
              onChange={(e) => handleUpdateFilters({ sortDirection: e.target.value as 'ASC' | 'DESC' })}
              className="border rounded-lg px-2 py-2 text-sm"
            >
              <option value="ASC">Tăng dần</option>
              <option value="DESC">Giảm dần</option>
            </select>
          </div>
        </div>
      </div>

      <main className="bg-white rounded-lg shadow-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Sản phẩm</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Danh mục</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Giá bán</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Tồn kho</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Đã kiểm</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Chênh lệch</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Ngưỡng min</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Ngưỡng max</th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Hạn dùng</th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Mã lô</th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Giảm giá</th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Hành động</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {isLoading ? (
                <tr>
                  <td colSpan={9} className="text-center p-8">
                    <div className="flex justify-center items-center">
                      <LoaderCircle className="w-6 h-6 animate-spin text-blue-500" />
                      <p className="ml-2">Đang tải...</p>
                    </div>
                  </td>
                </tr>
              ) : isError ? (
                <tr>
                  <td colSpan={9} className="text-center p-8 text-red-600">
                    <div className="flex justify-center items-center">
                      <AlertTriangle className="w-6 h-6 mr-2" />
                      <p>Lỗi: {error.message}</p>
                    </div>
                  </td>
                </tr>
                ) : productList.length === 0 ? (

                <tr>
                  <td colSpan={9} className="text-center p-8 text-gray-500">
                    Không tìm thấy sản phẩm nào.
                  </td>
                </tr>
              ) : 
                ( productList.map((item) => {
                  const imageUrl = item.product.productImageResponses?.[0]?.imageUrl || '/placeholder.png';
                  return (
                    <tr key={item.inventoryProductId} className="hover:bg-gray-50">
                      {/* Product */}
                      <td className="px-6 py-4">
                        <div className="flex items-center">
                          <img className="h-12 w-12 rounded-md object-cover" src={imageUrl} alt={item.product.name} />
                          <div className="ml-4">
                            <div className="text-sm font-medium text-gray-900">{item.product.name}</div>
                            <div className="text-sm text-gray-500">ID: {item.product.productId}</div>
                          </div>
                        </div>
                      </td>
                      {/* Category */}
                      <td className="px-6 py-4 text-sm text-gray-500">{item.product.category.name}</td>
                      {/* Price */}
                      <td className="px-6 py-4 text-right text-sm text-gray-900">{formatCurrency(item.currentPrice)}</td>
                      {/* Stock */}
                      <td className="px-6 py-4 text-right text-sm font-semibold text-gray-900">
                        {item.stockNumber} {item.product.unit}
                      </td>
                      {/* Checked */}
                      <td className="px-6 py-4 text-right text-sm text-gray-900">
                        {item.stockNumberChecked ?? '--'}
                      </td>
                      {/* Different */}
                      <td className="px-6 py-4 text-right text-sm text-gray-900">
                         {item.stockNumberDifferent ?? 0}
                       </td>
                       {/* Min Threshold */}
                       <td className="px-6 py-4 text-right text-sm text-gray-900">
                         {item.minThreshold ?? '--'}
                       </td>
                       {/* Max Threshold */}
                       <td className="px-6 py-4 text-right text-sm text-gray-900">
                         {item.maxThreshold ?? '--'}
                       </td>
                      {/* Status */}
                      <td className="px-6 py-4 text-center text-sm">
                        <span
                          className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStockLevelClass(
                            item.stockLevel
                          )}`}
                        >
                          {getStockLevelText(item.stockLevel)}
                        </span>
                      </td>
                      {/* Expiry Date */}
                      <td className="px-6 py-4 text-center text-sm text-gray-900">
                        {item.expDate ? new Date(item.expDate).toLocaleDateString('vi-VN') : '--'}
                      </td>
                      {/* Batch Number */}
                      <td className="px-6 py-4 text-center text-sm text-gray-900">{item.batchNumber || '--'}</td>
                      {/* Discounted */}
                      <td className="px-6 py-4 text-center text-sm">
                        {item.discounted ? 'Có' : 'Không'}
                      </td>
                      {/* Actions */}
                      <td className="px-6 py-4 text-center text-sm whitespace-nowrap">
                        <button
                          onClick={() => handleEditClick(item)}
                          className="text-blue-600 hover:text-blue-900 inline-flex items-center mr-3"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDeleteClick(item.inventoryProductId)}
                          className="text-red-600 hover:text-red-900 inline-flex items-center"
                        >
                          <Trash className="w-4 h-4" />
                        </button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </main>

      {/* Pagination */}
      <div className="flex justify-center mt-6">
        <Pagination currentPage={filters.page ?? 1} totalPages={totalPages} onPageChange={handlePageChange} />
      </div>

      {/* Modal */}
      {editingProduct && (
        <div className="fixed inset-0 bg-black bg-opacity-60 z-50 flex justify-center items-center">
          <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md relative">
            <button onClick={handleCloseModal} className="absolute top-3 right-3 text-gray-400 hover:text-gray-600">
              <X className="w-6 h-6" />
            </button>
            <h2 className="text-2xl font-bold mb-4 text-gray-800">Cập nhật Tồn kho</h2>
            <p className="mb-1 text-gray-700">
              Sản phẩm: <span className="font-semibold">{editingProduct.product.name}</span>
            </p>
            <p className="mb-4 text-gray-500">Tồn kho hiện tại: {editingProduct.stockNumber}</p>
            <form onSubmit={handleEditFormSubmit}>
              <label htmlFor="stockNumber" className="block text-sm font-medium text-gray-700 mb-1">
                Số lượng tồn mới
              </label>
              <input
                id="stockNumber"
                type="number"
                value={newStockValue}
                onChange={(e) => setNewStockValue(Number(e.target.value))}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />

              {/* Min Threshold */}
              <label htmlFor="minThreshold" className="block text-sm font-medium text-gray-700 mt-4 mb-1">
                Ngưỡng tồn kho tối thiểu
              </label>
              <input
                id="minThreshold"
                type="number"
                value={newMinThreshold ?? ''}
                onChange={(e) =>
                  setNewMinThreshold(e.target.value === '' ? undefined : Number(e.target.value))
                }
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />

              {/* Max Threshold */}
              <label htmlFor="maxThreshold" className="block text-sm font-medium text-gray-700 mt-4 mb-1">
                Ngưỡng tồn kho tối đa
              </label>
              <input
                id="maxThreshold"
                type="number"
                value={newMaxThreshold ?? ''}
                onChange={(e) =>
                  setNewMaxThreshold(e.target.value === '' ? undefined : Number(e.target.value))
                }
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <div className="mt-6 flex justify-end gap-3">
                <button type="button" onClick={handleCloseModal} className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300">
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={updateStockMutation.isPending}
                  className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-blue-400 disabled:cursor-not-allowed flex items-center"
                >
                  {updateStockMutation.isPending && <LoaderCircle className="w-4 h-4 mr-2 animate-spin" />}
                  {updateStockMutation.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {isAddModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-60 z-50 flex justify-center items-center">
          <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md relative">
            <button onClick={() => setAddModalOpen(false)} className="absolute top-3 right-3 text-gray-400 hover:text-gray-600">
              <X className="w-6 h-6" />
            </button>
            <h2 className="text-2xl font-bold mb-4 text-gray-800">Thêm sản phẩm vào kho</h2>
            <form onSubmit={handleAddFormSubmit}>
              <label className="block text-sm font-medium text-gray-700 mb-1">Sản phẩm</label>
              <select
                required
                value={addForm.productId}
                onChange={(e) => setAddForm({ ...addForm, productId: Number(e.target.value) })}
                className="w-full border rounded-md px-3 py-2 mb-3"
              >
                <option value={0}>--Chọn sản phẩm--</option>
                {productOptions.map((p) => (
                  <option key={p.productId} value={p.productId}>
                    {p.name}
                  </option>
                ))}
              </select>
              <label className="block text-sm font-medium text-gray-700 mb-1">Số lượng</label>
              <input
                type="number"
                required
                value={addForm.stockNumber}
                onChange={(e) => setAddForm({ ...addForm, stockNumber: Number(e.target.value) })}
                className="w-full border rounded-md px-3 py-2 mb-3"
              />
              <label className="block text-sm font-medium text-gray-700 mb-1">Ngưỡng tối thiểu</label>
              <input type="number" value={addForm.minThreshold} onChange={e=>setAddForm({...addForm,minThreshold:Number(e.target.value)})} className="w-full border rounded-md px-3 py-2 mb-3" />
              <label className="block text-sm font-medium text-gray-700 mb-1">Ngưỡng tối đa</label>
              <input type="number" value={addForm.maxThreshold} onChange={e=>setAddForm({...addForm,maxThreshold:Number(e.target.value)})} className="w-full border rounded-md px-3 py-2 mb-3" />
              <label className="block text-sm font-medium text-gray-700 mb-1">Ngày hết hạn</label>
              <input
                type="datetime-local"
                value={addForm.expDate}
                onChange={(e) => setAddForm({ ...addForm, expDate: e.target.value })}
                className="w-full border rounded-md px-3 py-2 mb-3"
              />
              <label className="block text-sm font-medium text-gray-700 mb-1">Mã lô (Batch)</label>
              <input
                type="text"
                value={addForm.batchNumber}
                onChange={(e) => setAddForm({ ...addForm, batchNumber: e.target.value })}
                className="w-full border rounded-md px-3 py-2 mb-3"
              />
              <label className="inline-flex items-center mb-4">
                <input
                  type="checkbox"
                  checked={addForm.discounted}
                  onChange={(e) => setAddForm({ ...addForm, discounted: e.target.checked })}
                  className="mr-2"
                />
                Giảm giá
              </label>
              <div className="mt-6 flex justify-end gap-3">
                <button type="button" onClick={() => setAddModalOpen(false)} className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300">
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={createMutation.isPending}
                  className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:bg-green-400 flex items-center"
                >
                  {createMutation.isPending && <LoaderCircle className="w-4 h-4 mr-2 animate-spin" />}
                  {createMutation.isPending ? 'Đang lưu...' : 'Thêm'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
