import React, { useState, useEffect, useCallback, useMemo } from 'react';
import inventoryService from '../../services/inventoryService';
import { Product, BranchStock, mockBranches } from '../../types/inventory';
import ProductModal from '../../components/Inventory/ProductModal';
import Button from '../../components/common/Button';
import { Link, useNavigate } from 'react-router-dom';

// Định nghĩa kiểu dữ liệu cho form sản phẩm
interface ProductFormData {
  id?: string;
  name: string;
  category: string;
  unit: string;
  lowStockThreshold: number;
  branchStocks: { branchId: string; stock: number; }[];
}

const InventoryPage: React.FC = () => {
    const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [inputSearchValue, setInputSearchValue] = useState(''); // State mới cho giá trị input khi gõ
  const [searchTerm, setSearchTerm] = useState(''); // State chính thức cho từ khóa tìm kiếm (khi nhấn Enter)
  const [currentPage, setCurrentPage] = useState(1);
  const [productsPerPage] = useState(10);
  const [totalProducts, setTotalProducts] = useState(0);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentProduct, setCurrentProduct] = useState<ProductFormData | null>(null);
  const [isEditing, setIsEditing] = useState(false);

  // Hàm để fetch dữ liệu sản phẩm
  const fetchProducts = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await inventoryService.getProducts({
        page: currentPage,
        limit: productsPerPage,
        search: searchTerm,
      });
      setProducts(response.data);
      setTotalProducts(response.total);
    } catch (err: any) {
      setError('Lỗi khi tải dữ liệu tồn kho: ' + err.message);
    } finally {
      setLoading(false);
    }
  }, [currentPage, productsPerPage, searchTerm]); // `searchTerm` ở đây để kích hoạt khi Enter
  const handleViewDetails = (productId: string) => {
    navigate(`/dashboard/products/${productId}`);
  };
  // Gọi API khi component mount, khi phân trang thay đổi, hoặc khi searchTerm chính thức thay đổi
  useEffect(() => {
    // Chỉ fetch dữ liệu khi trang tải lần đầu hoặc khi phân trang/tìm kiếm thay đổi
    // Tránh fetch ngay lập tức khi `inputSearchValue` thay đổi
    fetchProducts();
  }, [fetchProducts]);

  // --- Hàm xử lý thay đổi input khi người dùng gõ ---
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputSearchValue(e.target.value); // Chỉ cập nhật giá trị hiển thị trong input
  };

  // --- Hàm xử lý khi nhấn phím (để bắt Enter) ---
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      // Khi nhấn Enter, cập nhật searchTerm chính thức và reset trang về 1
      setSearchTerm(inputSearchValue);
      setCurrentPage(1);
      // fetchProducts() sẽ được gọi thông qua useEffect vì `searchTerm` đã thay đổi
    }
  };

  // --- Hàm mở/đóng Modal ---
  const openAddModal = () => {
    setCurrentProduct({
      name: '',
      category: '',
      unit: '',
      lowStockThreshold: 0,
      branchStocks: mockBranches.map(branch => ({ branchId: branch.id, stock: 0 })),
    });
    setIsEditing(false);
    setIsModalOpen(true);
  };

  const openEditModal = (product: Product) => {
    setCurrentProduct({
      id: product.id,
      name: product.name,
      category: product.category,
      unit: product.unit,
      lowStockThreshold: product.lowStockThreshold,
      branchStocks: product.branchStocks.map(bs => ({ branchId: bs.branchId, stock: bs.stock })),
    });
    setIsEditing(true);
    setIsModalOpen(true);
  };

  const closeProductModal = () => {
    setIsModalOpen(false);
    setCurrentProduct(null);
    setIsEditing(false);
  };

  // --- Hàm xử lý thêm/chỉnh sửa sản phẩm ---
  const handleSaveProduct = async (formData: ProductFormData) => {
    setLoading(true);
    try {
      if (isEditing && formData.id) {
        await inventoryService.updateProduct(formData.id, {
          ...formData,
          branchStocks: formData.branchStocks.map(bs => {
            const branch = mockBranches.find(b => b.id === bs.branchId);
            return {
              branchId: bs.branchId,
              branchName: branch ? branch.name : '',
              stock: bs.stock,
            };
          }),
        });
        console.log(`Đã cập nhật sản phẩm: ${formData.name}`);
      } else {
        await inventoryService.addProduct(formData);
        console.log(`Đã thêm sản phẩm mới: ${formData.name}`);
      }
      closeProductModal();
      setSearchTerm(''); // Xóa từ khóa tìm kiếm sau khi thêm/sửa
      setInputSearchValue(''); // Xóa giá trị trong ô input
      setCurrentPage(1); // Reset về trang 1
      fetchProducts(); // Tải lại danh sách sản phẩm sau khi thay đổi
    } catch (err: any) {
      setError('Lỗi khi lưu sản phẩm: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- Hàm xử lý xóa sản phẩm ---
  const handleDeleteProduct = async (productId: string) => {
    if (window.confirm(`Bạn có chắc chắn muốn xóa sản phẩm ${productId} không?`)) {
      setLoading(true);
      try {
        await inventoryService.deleteProduct(productId);
        console.log(`Đã xóa sản phẩm: ${productId}`);
        setSearchTerm(''); // Xóa từ khóa tìm kiếm sau khi xóa
        setInputSearchValue(''); // Xóa giá trị trong ô input
        setCurrentPage(1); // Reset về trang 1
        fetchProducts(); // Tải lại danh sách sản phẩm
      } catch (err: any) {
        setError('Lỗi khi xóa sản phẩm: ' + err.message);
      } finally {
        setLoading(false);
      }
    }
  };

  // --- Phân trang ---
  const totalPages = useMemo(() => Math.ceil(totalProducts / productsPerPage), [totalProducts, productsPerPage]);

  const goToPage = (page: number) => {
    if (page > 0 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const getStockStatus = (totalStock: number, lowStockThreshold: number) => {
    if (totalStock === 0) return { text: 'Hết Hàng', class: 'bg-red-200 text-red-800' };
    if (totalStock <= lowStockThreshold) return { text: 'Sắp Hết', class: 'bg-yellow-200 text-yellow-800' };
    return { text: 'Đủ Hàng', class: 'bg-green-200 text-green-800' };
  };

  if (loading) {
    return <div className="text-center py-8">Đang tải dữ liệu tồn kho...</div>;
  }

  if (error) {
    return <div className="text-center py-8 text-red-600">Lỗi: {error}</div>;
  }

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold text-gray-700 mb-4">Quản lý Kho Hàng</h2>

      {/* Thanh tìm kiếm và nút Thêm Sản Phẩm */}
      <div className="flex justify-between items-center mb-6">
        <input
          type="text"
          placeholder="Tìm kiếm theo tên, mã, loại sản phẩm..."
          className="p-2 border border-gray-300 rounded-md w-1/3"
          value={inputSearchValue} // Sử dụng inputSearchValue cho input
          onChange={handleInputChange} // Cập nhật inputSearchValue khi gõ
          onKeyDown={handleKeyDown} // Bắt sự kiện nhấn phím
        />
        <button
          onClick={openAddModal}
          className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-md transition duration-300"
        >
          Thêm Sản Phẩm Mới
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200">
          <thead>
            <tr className="bg-gray-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-left">Mã SP</th>
              <th className="py-3 px-6 text-left">Tên Sản Phẩm</th>
              <th className="py-3 px-6 text-left">Loại</th>
              <th className="py-3 px-6 text-left">Đơn Vị</th>
              {mockBranches.map(branch => (
                <th key={branch.id} className="py-3 px-6 text-left">Tồn Kho {branch.name}</th>
              ))}
              <th className="py-3 px-6 text-left">Tổng Tồn Kho</th>
              <th className="py-3 px-6 text-left">Trạng Thái</th>
              <th className="py-3 px-6 text-center">Hành Động</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm font-light">
            {products.length === 0 ? (
              <tr>
                <td colSpan={6 + mockBranches.length} className="py-6 text-center">Không tìm thấy sản phẩm nào.</td>
              </tr>
            ) : (
              products.map((product) => {
                const totalStock = product.branchStocks.reduce((sum, bs) => sum + bs.stock, 0);
                const status = getStockStatus(totalStock, product.lowStockThreshold);

                function handleCopyProductId(id: string, e: React.MouseEvent<HTMLButtonElement, MouseEvent>): void {
                  throw new Error('Function not implemented.');
                }

                return (
                  <tr key={product.id} className="border-b border-gray-200 hover:bg-gray-50">
                       <td className="py-3 px-6 text-left whitespace-nowrap">
                      <Link
                        to={`/dashboard/products/${product.id}`} // Đường dẫn đến trang chi tiết
                        className="text-blue-600 hover:text-blue-800 hover:underline flex items-center"
                      >
                        {product.id}
                      </Link>
                    </td>
                     <td className="py-3 px-6 text-left font-bold">{product.name}</td>
                      <td className="py-3 px-6 text-left font-bold">{product.category}</td>
                       <td className="py-3 px-6 text-left font-bold">{product.unit}</td>
                     {mockBranches.map(branch => {
                      const branchStock = product.branchStocks.find(bs => bs.branchId === branch.id);
                      return (
                        <td key={`${product.id}-${branch.id}`} className="py-3 px-6 text-left">
                          {branchStock ? branchStock.stock : 0}
                        </td>
                      );
                    })}
                    <td className="py-3 px-6 text-left font-bold">{totalStock}</td>
                    <td className="py-3 px-6 text-left">
                      <span className={`px-3 py-1 rounded-full text-xs font-semibold ${status.class}`}>
                        {status.text}
                      </span>
                    </td>
                    <td className="py-3 px-6 text-center">
                      <div className="flex item-center justify-center">
                        <button
                          onClick={() => openEditModal(product)}
                          className="w-8 h-8 rounded-full bg-yellow-100 text-yellow-700 flex items-center justify-center mr-2 hover:bg-yellow-200"
                          title="Chỉnh sửa"
                        >
                          <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                            <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z" />
                            <path fillRule="evenodd" d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z" clipRule="evenodd" />
                          </svg>
                        </button>
                        <button
                          onClick={() => handleDeleteProduct(product.id)}
                          className="w-8 h-8 rounded-full bg-red-100 text-red-700 flex items-center justify-center hover:bg-red-200"
                          title="Xóa"
                        >
                          <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                            <path fillRule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clipRule="evenodd" />
                          </svg>
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {/* Phân trang */}
      {totalPages > 1 && (
        <div className="flex justify-center mt-6">
          <button
            onClick={() => goToPage(currentPage - 1)}
            disabled={currentPage === 1}
            className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-l disabled:opacity-50"
          >
            Trước
          </button>
          {[...Array(totalPages)].map((_, index) => (
            <button
              key={index}
              onClick={() => goToPage(index + 1)}
              className={`bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 mx-1 ${
                currentPage === index + 1 ? 'bg-blue-500 text-white' : ''
              }`}
            >
              {index + 1}
            </button>
          ))}
          <button
            onClick={() => goToPage(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-r disabled:opacity-50"
          >
            Tiếp
          </button>
        </div>
      )}

      {/* Modal Thêm/Chỉnh sửa Sản Phẩm */}
      {isModalOpen && (
        <ProductModal
          isOpen={isModalOpen}
          onClose={closeProductModal}
          onSave={handleSaveProduct}
          initialData={currentProduct}
          isEditing={isEditing}
        />
      )}
    </div>
  );
};

export default InventoryPage;