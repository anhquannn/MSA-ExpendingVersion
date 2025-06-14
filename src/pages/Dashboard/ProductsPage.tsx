import React, { useState, useMemo, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

// Định nghĩa kiểu dữ liệu cho Product
interface Product {
  id: string;
  name: string;
  category: string;
  price: string; // Giá có thể là string hoặc number, giữ string cho mock
  stock: number; // Số lượng tồn kho
  unit: string; // Đơn vị tính
  status: 'Active' | 'Inactive'; // Trạng thái hoạt động
  imageUrl: string; // Đường dẫn hình ảnh
  description?: string; // Mô tả sản phẩm
}

// Dữ liệu sản phẩm giả (đã bổ sung thông tin và trạng thái)
const initialProducts: Product[] = [
  { id: 'PROD001', name: 'Táo Gala Mỹ', category: 'Trái cây', price: '45,000 VNĐ', stock: 150, unit: 'kg', status: 'Active', imageUrl: '/product-images/apple.png', description: 'Táo Gala nhập khẩu từ Mỹ, giòn ngọt, giàu vitamin.' },
  { id: 'PROD002', name: 'Cà chua Đà Lạt', category: 'Rau củ', price: '20,000 VNĐ', stock: 80, unit: 'kg', status: 'Active', imageUrl: '/product-images/tomato.png', description: 'Cà chua tươi Đà Lạt, sạch và an toàn.' },
  { id: 'PROD003', name: 'Thịt bò Úc', category: 'Thịt', price: '250,000 VNĐ', stock: 30, unit: 'kg', status: 'Active', imageUrl: '/product-images/beef.png', description: 'Thịt bò tươi ngon nhập khẩu trực tiếp từ Úc.' },
  { id: 'PROD004', name: 'Nước rửa chén Sunlight', category: 'Gia dụng', price: '35,000 VNĐ', stock: 50, unit: 'chai', status: 'Inactive', imageUrl: '/product-images/sunlight.png', description: 'Nước rửa chén diệt khuẩn, siêu sạch.' },
  { id: 'PROD005', name: 'Khoai tây Đà Lạt', category: 'Rau củ', price: '18,000 VNĐ', stock: 120, unit: 'kg', status: 'Active', imageUrl: '/product-images/default-product.png', description: 'Khoai tây tươi Đà Lạt, giàu tinh bột.' },
  { id: 'PROD006', name: 'Cá hồi Na Uy', category: 'Hải sản', price: '320,000 VNĐ', stock: 15, unit: 'kg', status: 'Active', imageUrl: '/product-images/default-product.png', description: 'Cá hồi tươi nhập khẩu từ Na Uy, giàu Omega-3.' },
  { id: 'PROD007', name: 'Kem đánh răng P/S', category: 'Chăm sóc cá nhân', price: '25,000 VNĐ', stock: 100, unit: 'tuýp', status: 'Inactive', imageUrl: '/product-images/default-product.png', description: 'Kem đánh răng bảo vệ răng miệng toàn diện.' },
  { id: 'PROD008', name: 'Bánh gạo One.One', category: 'Bánh kẹo', price: '22,000 VNĐ', stock: 200, unit: 'gói', status: 'Active', imageUrl: '/product-images/default-product.png', description: 'Bánh gạo giòn tan, hương vị thơm ngon.' },
  { id: 'PROD009', name: 'Nước mắm Phú Quốc', category: 'Gia vị', price: '60,000 VNĐ', stock: 70, unit: 'chai', status: 'Active', imageUrl: '/product-images/default-product.png', description: 'Nước mắm truyền thống, đậm đà hương vị.' },
  { id: 'PROD010', name: 'Sữa tươi TH True Milk 1L', category: 'Sữa & SP từ sữa', price: '32,000 VNĐ', stock: 90, unit: 'hộp', status: 'Active', imageUrl: '/product-images/default-product.png', description: 'Sữa tươi nguyên chất, giàu dinh dưỡng.' },
  { id: 'PROD011', name: 'Trứng gà', category: 'Thực phẩm tươi', price: '3,500 VNĐ', stock: 300, unit: 'quả', status: 'Active', imageUrl: '/product-images/default-product.png', description: 'Trứng gà tươi sạch, giàu protein.' },
  { id: 'PROD012', name: 'Bánh quy AFC', category: 'Bánh kẹo', price: '30,000 VNĐ', stock: 110, unit: 'hộp', status: 'Inactive', imageUrl: '/product-images/default-product.png', description: 'Bánh quy giòn, bổ sung chất xơ.' },
];

const ProductsPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>(initialProducts);
  const navigate = useNavigate();

  // --- State cho tìm kiếm và lọc ---
  const [searchTerm, setSearchTerm] = useState<string>(''); // Tìm theo Mã SP, Tên SP
  const [filterStockMin, setFilterStockMin] = useState<string>(''); // Tồn kho từ
  const [filterStockMax, setFilterStockMax] = useState<string>(''); // Tồn kho đến
  const [filterUnit, setFilterUnit] = useState<string>('all'); // Lọc theo đơn vị
  const [filterStatus, setFilterStatus] = useState<string>('all'); // Lọc theo trạng thái
  const [filterCategory, setFilterCategory] = useState<string>('all'); // Lọc theo danh mục

  // Lấy danh sách các đơn vị và danh mục duy nhất
  const uniqueUnits = useMemo(() => {
    const units = new Set<string>();
    initialProducts.forEach(p => units.add(p.unit));
    return Array.from(units);
  }, []);
  const uniqueCategories = useMemo(() => {
    const categories = new Set<string>();
    initialProducts.forEach(p => categories.add(p.category));
    return Array.from(categories);
  }, []);

  // --- State cho phân trang ---
  const [currentPage, setCurrentPage] = useState(1);
  const [productsPerPage, setProductsPerPage] = useState(10); // Số sản phẩm trên mỗi trang

  // --- State cho Modal thêm/sửa sản phẩm ---
  const [showProductModal, setShowProductModal] = useState(false);
  const [productToEdit, setProductToEdit] = useState<Product | null>(null); // Null cho thêm mới, Product object cho chỉnh sửa

  // --- State cho Dialog xác nhận xóa ---
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [productToDelete, setProductToDelete] = useState<Product | null>(null);


  // --- Logic lọc và tìm kiếm ---
  const filteredAndSearchedProducts = useMemo(() => {
    let tempProducts = [...products];

    // 1. Tìm kiếm theo ID hoặc Tên
    if (searchTerm) {
      const lowerCaseSearchTerm = searchTerm.toLowerCase();
      tempProducts = tempProducts.filter(product =>
        product.id.toLowerCase().includes(lowerCaseSearchTerm) ||
        product.name.toLowerCase().includes(lowerCaseSearchTerm)
      );
    }

    // 2. Lọc theo tồn kho
    if (filterStockMin) {
      const minStockNum = parseInt(filterStockMin);
      tempProducts = tempProducts.filter(product => product.stock >= minStockNum);
    }
    if (filterStockMax) {
      const maxStockNum = parseInt(filterStockMax);
      tempProducts = tempProducts.filter(product => product.stock <= maxStockNum);
    }

    // 3. Lọc theo đơn vị
    if (filterUnit !== 'all') {
      tempProducts = tempProducts.filter(product => product.unit === filterUnit);
    }

    // 4. Lọc theo trạng thái
    if (filterStatus !== 'all') {
      tempProducts = tempProducts.filter(product => product.status === filterStatus);
    }

    // 5. Lọc theo danh mục
    if (filterCategory !== 'all') {
      tempProducts = tempProducts.filter(product => product.category === filterCategory);
    }

    return tempProducts;
  }, [products, searchTerm, filterStockMin, filterStockMax, filterUnit, filterStatus, filterCategory]);


  // Tính toán tổng số trang
  const totalPages = Math.ceil(filteredAndSearchedProducts.length / productsPerPage);

  // Lấy sản phẩm cho trang hiện tại
  const currentProducts = useMemo(() => {
    const indexOfLastProduct = currentPage * productsPerPage;
    const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
    return filteredAndSearchedProducts.slice(indexOfFirstProduct, indexOfLastProduct);
  }, [currentPage, productsPerPage, filteredAndSearchedProducts]);


  const getStatusClasses = (status: string) => {
    return status === 'Active' ? 'bg-green-200 text-green-800' : 'bg-red-200 text-red-800';
  };

  const handleToggleStatus = (productId: string) => {
    setProducts(prevProducts => {
      return prevProducts.map(product => {
        if (product.id === productId) {
          const newStatus = product.status === 'Active' ? 'Inactive' : 'Active';
          console.log(`Updating product ${product.id} status to ${newStatus}`);
          // Simulate API call: axios.put(`/api/products/${productId}/status`, { status: newStatus });
          return { ...product, status: newStatus };
        }
        return product;
      });
    });
  };

  const handleViewDetails = (productId: string) => {
    navigate(`/dashboard/products/${productId}`);
  };

  const paginate = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  // Reset về trang 1 khi các tiêu chí lọc/tìm kiếm thay đổi
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, filterStockMin, filterStockMax, filterUnit, filterStatus, filterCategory]);


  // --- Hàm thêm/sửa sản phẩm (Open Modal) ---
  const handleAddProduct = () => {
    setProductToEdit(null); // Không có sản phẩm nào để chỉnh sửa (chế độ thêm mới)
    setShowProductModal(true);
  };

  const handleEditProduct = (product: Product) => {
    setProductToEdit(product); // Đặt sản phẩm cần chỉnh sửa
    setShowProductModal(true);
  };

  const handleSaveProduct = (formData: Product) => {
    if (productToEdit) { // Chế độ chỉnh sửa
      setProducts(prev => prev.map(p => p.id === formData.id ? formData : p));
      console.log('Product updated:', formData);
      // Simulate API call: axios.put(`/api/products/${formData.id}`, formData);
    } else { // Chế độ thêm mới
      // Gán một ID mới (thường API sẽ trả về ID)
      const newId = `PROD${(products.length + 1).toString().padStart(3, '0')}`;
      const newProduct = { ...formData, id: newId };
      setProducts(prev => [...prev, newProduct]);
      console.log('New product added:', newProduct);
      // Simulate API call: axios.post('/api/products', newProduct);
    }
    setShowProductModal(false);
    setProductToEdit(null);
  };

  const handleCloseProductModal = () => {
    setShowProductModal(false);
    setProductToEdit(null);
  };

  // --- Hàm xóa sản phẩm (Open Delete Dialog) ---
  const handleDeleteProductClick = (product: Product) => {
    setProductToDelete(product);
    setShowDeleteDialog(true);
  };

  const [editProductFormData, setEditProductFormData] = useState<Partial<Product>>({}); // <-- ĐÂY LÀ DÒNG FIX LỖI


  const confirmDeleteProduct = () => {
    if (productToDelete) {
      setProducts(prev => prev.filter(p => p.id !== productToDelete.id));
      console.log('Product deleted:', productToDelete.id);
      // Simulate API call: axios.delete(`/api/products/${productToDelete.id}`);
      setShowDeleteDialog(false);
      setProductToDelete(null);
    }
  };

  const closeDeleteDialog = () => {
    setShowDeleteDialog(false);
    setProductToDelete(null);
  };


  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold text-gray-700 mb-4">Quản Lý Sản Phẩm</h2>
      <p className="text-gray-600 mb-6">Danh sách đầy đủ các sản phẩm có sẵn trong cửa hàng.</p>

      {/* Nút Thêm Sản phẩm */}
      <div className="flex justify-end mb-4">
        <button
          onClick={handleAddProduct}
          className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition duration-200"
        >
          Thêm Sản Phẩm Mới
        </button>
      </div>

      {/* --- Phần Tìm kiếm và Lọc --- */}
      <div className="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
        <h3 className="text-lg font-semibold text-gray-700 mb-3">Tìm kiếm & Lọc</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4"> {/* Tăng số cột cho desktop */}
          {/* Tìm kiếm theo ID hoặc Tên sản phẩm */}
          <div>
            <label htmlFor="search-product" className="block text-sm font-medium text-gray-700 mb-1">Tìm kiếm:</label>
            <input
              type="text"
              id="search-product"
              placeholder="ID hoặc Tên sản phẩm"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>

          {/* Lọc theo danh mục */}
          <div>
            <label htmlFor="filter-category" className="block text-sm font-medium text-gray-700 mb-1">Danh mục:</label>
            <select
              id="filter-category"
              value={filterCategory}
              onChange={(e) => setFilterCategory(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="all">Tất cả danh mục</option>
              {uniqueCategories.map(category => (
                <option key={category} value={category}>{category}</option>
              ))}
            </select>
          </div>

          {/* Lọc theo đơn vị */}
          <div>
            <label htmlFor="filter-unit" className="block text-sm font-medium text-gray-700 mb-1">Đơn vị:</label>
            <select
              id="filter-unit"
              value={filterUnit}
              onChange={(e) => setFilterUnit(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="all">Tất cả đơn vị</option>
              {uniqueUnits.map(unit => (
                <option key={unit} value={unit}>{unit}</option>
              ))}
            </select>
          </div>

          {/* Lọc theo trạng thái */}
          <div>
            <label htmlFor="filter-status" className="block text-sm font-medium text-gray-700 mb-1">Trạng thái:</label>
            <select
              id="filter-status"
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="all">Tất cả</option>
              <option value="Active">Hoạt động</option>
              <option value="Inactive">Không hoạt động</option>
            </select>
          </div>

          {/* Lọc theo tồn kho */}
          <div className="grid grid-cols-2 gap-2 col-span-full md:col-span-2 lg:col-span-2"> {/* Chiếm hết chiều rộng cho mobile/tablet */}
            <div>
              <label htmlFor="min-stock" className="block text-sm font-medium text-gray-700 mb-1">Tồn kho từ:</label>
              <input
                type="number"
                id="min-stock"
                placeholder="Tối thiểu"
                value={filterStockMin}
                onChange={(e) => setFilterStockMin(e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
            <div>
              <label htmlFor="max-stock" className="block text-sm font-medium text-gray-700 mb-1">Tồn kho đến:</label>
              <input
                type="number"
                id="max-stock"
                placeholder="Tối đa"
                value={filterStockMax}
                onChange={(e) => setFilterStockMax(e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
          </div>

        </div>
        {/* Nút reset filter */}
        <div className="mt-4 flex justify-end">
          <button
            onClick={() => {
              setSearchTerm('');
              setFilterStockMin('');
              setFilterStockMax('');
              setFilterUnit('all');
              setFilterStatus('all');
              setFilterCategory('all');
            }}
            className="px-4 py-2 bg-gray-400 text-white rounded-md hover:bg-gray-500 transition duration-200"
          >
            Reset Lọc
          </button>
        </div>
      </div>


      {/* Tùy chọn số lượng sản phẩm trên mỗi trang */}
      <div className="mb-4 flex justify-end items-center">
        <label htmlFor="products-per-page" className="text-gray-700 mr-2">Sản phẩm mỗi trang:</label>
        <select
          id="products-per-page"
          value={productsPerPage}
          onChange={(e) => {
            setProductsPerPage(Number(e.target.value));
            setCurrentPage(1);
          }}
          className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
        >
          <option value={5}>5</option>
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={50}>50</option>
        </select>
      </div>

      {/* Bảng sản phẩm */}
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200">
          <thead>
            <tr className="bg-gray-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-left">ID</th>
              <th className="py-3 px-6 text-left">Tên Sản Phẩm</th>
              <th className="py-3 px-6 text-left">Danh Mục</th>
              <th className="py-3 px-6 text-left">Giá</th>
              <th className="py-3 px-6 text-left">Tồn Kho</th>
              <th className="py-3 px-6 text-left">Đơn Vị</th>
              <th className="py-3 px-6 text-left">Trạng Thái</th>
              <th className="py-3 px-6 text-center">Hành Động</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm font-light">
            {currentProducts.length === 0 ? (
              <tr>
                <td colSpan={8} className="py-4 text-center text-gray-500">Không tìm thấy sản phẩm nào khớp với tiêu chí lọc.</td>
              </tr>
            ) : (
              currentProducts.map((product) => (
                <tr key={product.id} className="border-b border-gray-200 hover:bg-gray-50">
                  <td className="py-3 px-6 text-left whitespace-nowrap">{product.id}</td>
                  <td className="py-3 px-6 text-left">{product.name}</td>
                  <td className="py-3 px-6 text-left">{product.category}</td>
                  <td className="py-3 px-6 text-left">{product.price}</td>
                  <td className="py-3 px-6 text-left">{product.stock}</td>
                  <td className="py-3 px-6 text-left">{product.unit}</td>
                  <td className="py-3 px-6 text-left">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusClasses(product.status)}`}
                    >
                      {product.status}
                    </span>
                  </td>
                  <td className="py-3 px-6 text-center whitespace-nowrap">
                    <button
                      onClick={() => handleToggleStatus(product.id)}
                      className={`px-3 py-1 rounded-md text-xs transition duration-200 
                        ${product.status === 'Active'
                          ? 'bg-red-500 text-white hover:bg-red-600'
                          : 'bg-green-500 text-white hover:bg-green-600'
                        }`}
                    >
                      {product.status === 'Active' ? 'Vô hiệu hóa' : 'Kích hoạt'}
                    </button>
                    <button
                      onClick={() => handleEditProduct(product)}
                      className="ml-2 bg-yellow-500 text-white px-3 py-1 rounded-md text-xs hover:bg-yellow-600 transition duration-200"
                    >
                      Sửa
                    </button>
                    <button
                      onClick={() => handleViewDetails(product.id)}
                      className="ml-2 bg-blue-500 text-white px-3 py-1 rounded-md text-xs hover:bg-blue-600 transition duration-200"
                    >
                      Chi tiết
                    </button>
                    <button
                      onClick={() => handleDeleteProductClick(product)}
                      className="ml-2 bg-gray-500 text-white px-3 py-1 rounded-md text-xs hover:bg-gray-600 transition duration-200"
                    >
                      Xóa
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* --- Phần phân trang --- */}
      <div className="mt-6 flex justify-between items-center flex-wrap">
        <div className="text-sm text-gray-600 mb-2 md:mb-0">
          Hiển thị {Math.min((currentPage - 1) * productsPerPage + 1, filteredAndSearchedProducts.length)} - {Math.min(currentPage * productsPerPage, filteredAndSearchedProducts.length)} trên tổng số {filteredAndSearchedProducts.length} sản phẩm
        </div>
        <nav className="flex items-center space-x-1" aria-label="Pagination">
          <button
            onClick={() => paginate(currentPage - 1)}
            disabled={currentPage === 1}
            className="px-3 py-1 rounded-md bg-white text-gray-700 border border-gray-300 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition duration-200"
          >
            Trước
          </button>

          {pageNumbers.map(number => (
            <button
              key={number}
              onClick={() => paginate(number)}
              className={`px-3 py-1 rounded-md transition duration-200
                ${currentPage === number
                  ? 'bg-green-600 text-white shadow-md'
                  : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-100'
                }`}
            >
              {number}
            </button>
          ))}

          <button
            onClick={() => paginate(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="px-3 py-1 rounded-md bg-white text-gray-700 border border-gray-300 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition duration-200"
          >
            Sau
          </button>
        </nav>
      </div>

      {/* --- MODAL THÊM/SỬA SẢN PHẨM --- */}
      {showProductModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-lg">
            <h3 className="text-xl font-semibold mb-4 text-gray-800">{productToEdit ? 'Chỉnh sửa Sản phẩm' : 'Thêm Sản phẩm mới'}</h3>
            <form onSubmit={(e) => { e.preventDefault(); handleSaveProduct(editProductFormData as Product); }} className="space-y-4">
              <div>
                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="product-name">Tên Sản phẩm:</label>
                <input
                  type="text"
                  id="product-name"
                  name="name"
                  value={editProductFormData.name || ''}
                  onChange={e => setEditProductFormData({ ...editProductFormData, name: e.target.value })}
                  required
                  className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                />
              </div>
              <div>
                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="product-category">Danh mục:</label>
                <input
                  type="text"
                  id="product-category"
                  name="category"
                  value={editProductFormData.category || ''}
                  onChange={e => setEditProductFormData({ ...editProductFormData, category: e.target.value })}
                  required
                  className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="product-price">Giá (VNĐ):</label>
                  <input
                    type="text" // Dùng text để giữ định dạng VNĐ, chuyển đổi khi lưu
                    id="product-price"
                    name="price"
                    value={editProductFormData.price || ''}
                    onChange={e => setEditProductFormData({ ...editProductFormData, price: e.target.value })}
                    required
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  />
                </div>
                <div>
                  <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="product-stock">Tồn kho:</label>
                  <input
                    type="number"
                    id="product-stock"
                    name="stock"
                    value={editProductFormData.stock || ''}
                    onChange={e => setEditProductFormData({ ...editProductFormData, stock: Number(e.target.value) })}
                    required
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="product-unit">Đơn vị:</label>
                  <input
                    type="text"
                    id="product-unit"
                    name="unit"
                    value={editProductFormData.unit || ''}
                    onChange={e => setEditProductFormData({ ...editProductFormData, unit: e.target.value })}
                    required
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  />
                </div>
                <div>
                  <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="product-status">Trạng thái:</label>
                  <select
                    id="product-status"
                    name="status"
                    value={editProductFormData.status || 'Active'}
                    onChange={e => setEditProductFormData({ ...editProductFormData, status: e.target.value as 'Active' | 'Inactive' })}
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  >
                    <option value="Active">Hoạt động</option>
                    <option value="Inactive">Không hoạt động</option>
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="product-image">URL Hình ảnh:</label>
                <input
                  type="text"
                  id="product-image"
                  name="imageUrl"
                  value={editProductFormData.imageUrl || ''}
                  onChange={e => setEditProductFormData({ ...editProductFormData, imageUrl: e.target.value })}
                  placeholder="/product-images/default-product.png"
                  className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                />
              </div>
              <div>
                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="product-description">Mô tả:</label>
                <textarea
                  id="product-description"
                  name="description"
                  value={editProductFormData.description || ''}
                  onChange={e => setEditProductFormData({ ...editProductFormData, description: e.target.value })}
                  rows={3}
                  className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                ></textarea>
              </div>

              <div className="flex justify-end space-x-3 mt-6">
                <button
                  type="button"
                  onClick={handleCloseProductModal}
                  className="px-4 py-2 bg-gray-300 text-gray-800 rounded-md hover:bg-gray-400 transition duration-200"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition duration-200"
                >
                  {productToEdit ? 'Cập nhật' : 'Thêm mới'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* --- DIALOG XÁC NHẬN XÓA SẢN PHẨM --- */}
      {showDeleteDialog && productToDelete && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-sm">
            <h3 className="text-xl font-semibold text-gray-800 mb-4">Xác nhận Xóa Sản Phẩm</h3>
            <p className="text-gray-700 mb-4">Bạn có chắc chắn muốn xóa sản phẩm này không?</p>
            <div className="mb-4 text-sm text-gray-600">
              <p><span className="font-semibold">ID Sản Phẩm:</span> {productToDelete.id}</p>
              <p><span className="font-semibold">Tên Sản Phẩm:</span> {productToDelete.name}</p>
            </div>
            <div className="flex justify-end space-x-3">
              <button
                onClick={closeDeleteDialog}
                className="px-4 py-2 bg-gray-300 text-gray-800 rounded-md hover:bg-gray-400 transition duration-200"
              >
                Không
              </button>
              <button
                onClick={confirmDeleteProduct}
                className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition duration-200"
              >
                Xác nhận Xóa
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductsPage;