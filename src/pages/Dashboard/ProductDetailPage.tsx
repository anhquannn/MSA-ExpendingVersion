import React, { useState, useMemo, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

// Định nghĩa kiểu dữ liệu cho Product (tương tự như ProductsPage)
interface Product {
  id: string;
  name: string;
  category: string;
  price: string;
  stock: number;
  unit: string;
  status: 'Active' | 'Inactive';
  imageUrl: string;
  description?: string;
}

// Dữ liệu sản phẩm giả (tương tự như ProductsPage để đảm bảo đồng bộ)
const mockProductsData: Product[] = [
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

const ProductDetailPage: React.FC = () => {
  const { productId } = useParams<{ productId: string }>(); // Lấy productId từ URL
  const navigate = useNavigate();

  // Tìm sản phẩm trong dữ liệu giả
  const initialProduct = useMemo(() => {
    return mockProductsData.find(p => p.id === productId);
  }, [productId]);

  const [product, setProduct] = useState<Product | undefined>(initialProduct); // State để có thể chỉnh sửa product
  const [isEditing, setIsEditing] = useState(false); // State để bật/tắt chế độ chỉnh sửa
  const [editFormData, setEditFormData] = useState<Partial<Product>>({}); // Dữ liệu form chỉnh sửa

  // Cập nhật state product khi initialProduct thay đổi (ví dụ: khi chuyển giữa các trang chi tiết sản phẩm)
  useEffect(() => {
    setProduct(initialProduct);
    setIsEditing(false); // Thoát chế độ chỉnh sửa khi product thay đổi
    setEditFormData({});
  }, [initialProduct]);


  if (!product) {
    return (
      <div className="bg-white p-6 rounded-lg shadow-md text-center text-red-600">
        <h2 className="text-2xl font-semibold mb-4">Không tìm thấy sản phẩm</h2>
        <p className="mb-4">ID sản phẩm "{productId}" không tồn tại.</p>
        <button 
          onClick={() => navigate('/dashboard/products')}
          className="bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 transition duration-200"
        >
          Quay lại danh sách sản phẩm
        </button>
      </div>
    );
  }

  const getStatusClasses = (status: string) => {
    return status === 'Active' ? 'bg-green-200 text-green-800' : 'bg-red-200 text-red-800';
  };

  const handleEdit = () => {
    setIsEditing(true);
    setEditFormData({ ...product }); // Đổ dữ liệu hiện tại vào form
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    setEditFormData({});
  };

  const handleSave = () => {
    // Simulate API call to save changes
    console.log("Saving changes for product:", editFormData);
    // Trong thực tế:
    // try {
    //   await axios.put(`/api/products/${product.id}`, editFormData);
    //   setProduct(prev => ({ ...prev, ...editFormData } as Product)); // Cập nhật UI sau khi lưu thành công
    //   setIsEditing(false);
    //   setEditFormData({});
    // } catch (error) {
    //   console.error("Failed to save product data", error);
    //   // Xử lý lỗi
    // }
    
    // Đối với dữ liệu giả:
    setProduct(prev => ({ ...prev!, ...editFormData })); // Cập nhật state product
    setIsEditing(false); // Thoát chế độ chỉnh sửa
    setEditFormData({});
    alert("Thông tin sản phẩm đã được lưu (chỉ là giả lập)!");
  };

  const handleFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setEditFormData(prev => ({ ...prev, [name]: name === 'stock' ? Number(value) : value })); // Convert stock to number
  };

  const handleToggleStatus = () => {
    const newStatus = product.status === 'Active' ? 'Inactive' : 'Active';
    // Simulate API call
    console.log(`Updating product ${product.id} status to ${newStatus}`);
    // Trong thực tế:
    // try {
    //   await axios.put(`/api/products/${product.id}/status`, { status: newStatus });
    //   setProduct(prev => ({ ...prev!, status: newStatus }));
    // } catch (error) {
    //   console.error("Failed to toggle status", error);
    // }
    setProduct(prev => ({ ...prev!, status: newStatus })); // Cập nhật state product
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-semibold text-gray-700">Chi Tiết Sản Phẩm: {product.name}</h2>
        <div className="flex space-x-3">
          <button 
            onClick={() => navigate(-1)} // Quay lại trang trước
            className="bg-gray-300 text-gray-800 px-4 py-2 rounded-md hover:bg-gray-400 transition duration-200"
          >
            Quay lại
          </button>
          {isEditing ? (
            <>
              <button 
                onClick={handleSave}
                className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition duration-200"
              >
                Lưu
              </button>
              <button 
                onClick={handleCancelEdit}
                className="bg-gray-500 text-white px-4 py-2 rounded-md hover:bg-gray-600 transition duration-200"
              >
                Hủy
              </button>
            </>
          ) : (
            <button 
              onClick={handleEdit}
              className="bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 transition duration-200"
            >
              Chỉnh sửa
            </button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {/* Cột Hình ảnh sản phẩm */}
        <div className="md:col-span-1 flex flex-col items-center justify-center bg-gray-100 p-4 rounded-lg shadow-inner">
          <img 
            src={product.imageUrl || '/product-images/default-product.png'} 
            alt={product.name} 
            className="w-full h-auto object-contain rounded-lg max-h-64"
          />
          {isEditing && (
            <div className="mt-4 w-full">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="product-image-url">URL Hình ảnh:</label>
              <input
                type="text"
                id="product-image-url"
                name="imageUrl"
                value={editFormData.imageUrl || ''}
                onChange={handleFormChange}
                placeholder="/product-images/new-image.png"
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              />
            </div>
          )}
        </div>

        {/* Cột Thông tin sản phẩm */}
        <div className="md:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4 text-gray-700">
          <div className="mb-2">
            <span className="font-semibold">ID Sản Phẩm:</span> {product.id}
          </div>
          <div className="mb-2">
            <span className="font-semibold">Tên Sản Phẩm:</span> 
            {isEditing ? (
              <input 
                type="text" 
                name="name" 
                value={editFormData.name || ''} 
                onChange={handleFormChange} 
                className="ml-2 border rounded px-2 py-1"
              />
            ) : (
              <span className="ml-2">{product.name}</span>
            )}
          </div>
          <div className="mb-2">
            <span className="font-semibold">Danh Mục:</span> 
            {isEditing ? (
              <input 
                type="text" 
                name="category" 
                value={editFormData.category || ''} 
                onChange={handleFormChange} 
                className="ml-2 border rounded px-2 py-1"
              />
            ) : (
              <span className="ml-2">{product.category}</span>
            )}
          </div>
          <div className="mb-2">
            <span className="font-semibold">Giá:</span> 
            {isEditing ? (
              <input 
                type="text" // Giữ là text để cho phép định dạng tiền tệ khi nhập
                name="price" 
                value={editFormData.price || ''} 
                onChange={handleFormChange} 
                className="ml-2 border rounded px-2 py-1"
              />
            ) : (
              <span className="ml-2">{product.price}</span>
            )}
          </div>
          <div className="mb-2">
            <span className="font-semibold">Tồn Kho:</span> 
            {isEditing ? (
              <input 
                type="number" 
                name="stock" 
                value={editFormData.stock || ''} 
                onChange={handleFormChange} 
                className="ml-2 border rounded px-2 py-1"
              />
            ) : (
              <span className="ml-2">{product.stock} {product.unit}</span>
            )}
          </div>
          <div className="mb-2">
            <span className="font-semibold">Đơn Vị:</span> 
            {isEditing ? (
              <input 
                type="text" 
                name="unit" 
                value={editFormData.unit || ''} 
                onChange={handleFormChange} 
                className="ml-2 border rounded px-2 py-1"
              />
            ) : (
              <span className="ml-2">{product.unit}</span>
            )}
          </div>
          <div className="mb-2 col-span-full"> {/* Trạng thái và nút toggle */}
            <span className="font-semibold">Trạng Thái:</span> 
            <span className={`ml-2 px-3 py-1 rounded-full text-sm font-semibold ${getStatusClasses(product.status)}`}>
              {product.status}
            </span>
            <button
              onClick={handleToggleStatus}
              className={`ml-3 px-3 py-1 rounded-md text-xs transition duration-200 
                ${product.status === 'Active' 
                   ? 'bg-red-500 text-white hover:bg-red-600' 
                   : 'bg-green-500 text-white hover:bg-green-600'
                }`}
            >
              {product.status === 'Active' ? 'Vô hiệu hóa' : 'Kích hoạt'}
            </button>
          </div>
          <div className="mb-2 col-span-full"> {/* Mô tả sản phẩm */}
            <span className="font-semibold">Mô tả:</span> 
            {isEditing ? (
              <textarea 
                name="description" 
                value={editFormData.description || ''} 
                onChange={handleFormChange} 
                rows={4}
                className="ml-2 border rounded px-2 py-1 w-full"
              ></textarea>
            ) : (
              <p className="ml-2 mt-1 text-gray-600">{product.description || 'Chưa có mô tả'}</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetailPage;