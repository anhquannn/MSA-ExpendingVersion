import React, { useState, useMemo } from 'react'; // Import useState
import { useParams, useNavigate } from 'react-router-dom';

// Định nghĩa kiểu dữ liệu cho người dùng (tương tự như UsersPage)
interface User {
  id: string;
  name: string;
  email: string;
  phone?: string;
  address?: string;
  role: 'Khách hàng' | 'Manager';
  status: 'Active' | 'Inactive';
  registeredDate: string;
  branch?: string; // Chỉ có ở Manager
}

// Dữ liệu người dùng giả (đã thêm Manager)
const mockUsersData: User[] = [
  // --- Khách hàng ---
  { id: 'U001', name: 'Nguyễn Thanh Tùng', email: 'tung.nt@example.com', phone: '0901234567', address: '123 Đường ABC, Quận 1, TP.HCM', role: 'Khách hàng', status: 'Active', registeredDate: '2023-01-15' },
  { id: 'U002', name: 'Phạm Thị Lan', email: 'lan.pt@example.com', phone: '0912345678', address: '456 Đường XYZ, Quận 3, TP.HCM', role: 'Khách hàng', status: 'Active', registeredDate: '2023-03-20' },
  { id: 'U003', name: 'Đỗ Văn Khoa', email: 'khoa.dv@example.com', phone: '0987654321', address: '789 Đường KLN, Quận Bình Thạnh, TP.HCM', role: 'Khách hàng', status: 'Inactive', registeredDate: '2023-05-10' },
  // --- Managers ---
  { id: 'M001', name: 'Nguyễn Văn Quản Lý 1', email: 'manager1@example.com', phone: '0910000001', address: '888 Đường Đinh Bộ Lĩnh, Quận Bình Thạnh, TP.HCM', role: 'Manager', status: 'Active', branch: 'Chi nhánh 1', registeredDate: '2022-01-01' },
  { id: 'M002', name: 'Trần Thị Quản Lý 2', email: 'manager2@example.com', phone: '0910000002', address: '999 Đường Cách Mạng Tháng 8, Quận 3, TP.HCM', role: 'Manager', status: 'Active', branch: 'Chi nhánh 2', registeredDate: '2022-03-10' },
  { id: 'M003', name: 'Lê Văn Quản Lý 3', email: 'manager3@example.com', phone: '0910000003', address: '111 Đường Lê Lợi, Quận 1, TP.HCM', role: 'Manager', status: 'Inactive', branch: 'Chi nhánh 3', registeredDate: '2022-05-20' },
];

const UserDetailPage: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();

  // Tìm người dùng trong dữ liệu giả
  const initialUser = useMemo(() => {
    return mockUsersData.find(u => u.id === userId);
  }, [userId]);

  const [user, setUser] = useState<User | undefined>(initialUser); // State để có thể chỉnh sửa user
  const [isEditing, setIsEditing] = useState(false); // State để bật/tắt chế độ chỉnh sửa
  const [editFormData, setEditFormData] = useState<Partial<User>>({}); // Dữ liệu form chỉnh sửa

  // Cập nhật state user khi initialUser (từ useParams) thay đổi (ví dụ: khi chuyển giữa các trang chi tiết user)
  React.useEffect(() => {
    setUser(initialUser);
    setIsEditing(false); // Thoát chế độ chỉnh sửa khi user thay đổi
    setEditFormData({});
  }, [initialUser]);


  if (!user) {
    return (
      <div className="bg-white p-6 rounded-lg shadow-md text-center text-red-600">
        <h2 className="text-2xl font-semibold mb-4">Không tìm thấy người dùng</h2>
        <p className="mb-4">ID người dùng "{userId}" không tồn tại.</p>
        <button 
          onClick={() => navigate('/dashboard/users')}
          className="bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 transition duration-200"
        >
          Quay lại danh sách người dùng
        </button>
      </div>
    );
  }

  const getStatusClasses = (status: string) => {
    return status === 'Active' ? 'bg-green-200 text-green-800' : 'bg-red-200 text-red-800';
  };

  const handleEdit = () => {
    setIsEditing(true);
    setEditFormData({ ...user }); // Đổ dữ liệu hiện tại vào form
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    setEditFormData({});
  };

  const handleSave = () => {
    // Simulate API call to save changes
    // console.log("Saving changes for user:", editingManagerId, editFormData);
    // Trong thực tế:
    // try {
    //   await axios.put(`/api/users/${user.id}`, editFormData);
    //   setUser(prev => ({ ...prev, ...editFormData } as User)); // Cập nhật UI sau khi lưu thành công
    //   setIsEditing(false);
    //   setEditFormData({});
    // } catch (error) {
    //   console.error("Failed to save user data", error);
    //   // Xử lý lỗi
    // }
    
    // Đối với dữ liệu giả:
    setUser(prev => ({ ...prev!, ...editFormData })); // Cập nhật state user
    setIsEditing(false); // Thoát chế độ chỉnh sửa
    setEditFormData({});
    alert("Thông tin đã được lưu (chỉ là giả lập)!");
  };

  const handleFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setEditFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleToggleStatus = () => {
    const newStatus = user.status === 'Active' ? 'Inactive' : 'Active';
    // Simulate API call
    console.log(`Updating user ${user.id} status to ${newStatus}`);
    // Trong thực tế:
    // try {
    //   await axios.put(`/api/users/${user.id}/status`, { status: newStatus });
    //   setUser(prev => ({ ...prev!, status: newStatus }));
    // } catch (error) {
    //   console.error("Failed to toggle status", error);
    // }
    setUser(prev => ({ ...prev!, status: newStatus })); // Cập nhật state user
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-semibold text-gray-700">Chi Tiết Người Dùng: {user.name}</h2>
        <div className="flex space-x-3">
          <button 
            onClick={() => navigate(-1)} // Quay lại trang trước
            className="bg-gray-300 text-gray-800 px-4 py-2 rounded-md hover:bg-gray-400 transition duration-200"
          >
            Quay lại
          </button>
          {user.role === 'Manager' && ( // Nút sửa chỉ cho Manager
            isEditing ? (
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
            )
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4 text-gray-700">
        <div className="mb-2">
          <span className="font-semibold">ID Người Dùng:</span> {user.id}
        </div>
        <div className="mb-2">
          <span className="font-semibold">Email:</span> 
          {isEditing && user.role === 'Manager' ? (
            <input 
              type="email" 
              name="email" 
              value={editFormData.email || ''} 
              onChange={handleFormChange} 
              className="ml-2 border rounded px-2 py-1"
            />
          ) : (
            <span className="ml-2">{user.email}</span>
          )}
        </div>
        <div className="mb-2">
          <span className="font-semibold">Số Điện Thoại:</span> 
          {isEditing && user.role === 'Manager' ? (
            <input 
              type="text" 
              name="phone" 
              value={editFormData.phone || ''} 
              onChange={handleFormChange} 
              className="ml-2 border rounded px-2 py-1"
            />
          ) : (
            <span className="ml-2">{user.phone || 'Chưa cập nhật'}</span>
          )}
        </div>
        <div className="mb-2 col-span-1 md:col-span-2">
          <span className="font-semibold">Địa Chỉ:</span> 
          {isEditing && user.role === 'Manager' ? (
            <input 
              type="text" 
              name="address" 
              value={editFormData.address || ''} 
              onChange={handleFormChange} 
              className="ml-2 border rounded px-2 py-1 w-full"
            />
          ) : (
            <span className="ml-2">{user.address || 'Chưa cập nhật'}</span>
          )}
        </div>
        <div className="mb-2">
          <span className="font-semibold">Vai Trò:</span> {user.role}
        </div>
        {user.role === 'Manager' && ( // Chỉ hiển thị chi nhánh cho Manager
          <div className="mb-2">
            <span className="font-semibold">Chi nhánh:</span>
            {isEditing ? (
              <input 
                type="text" 
                name="branch" 
                value={editFormData.branch || ''} 
                onChange={handleFormChange} 
                className="ml-2 border rounded px-2 py-1"
              />
            ) : (
              <span className="ml-2">{user.branch || 'Chưa cập nhật'}</span>
            )}
          </div>
        )}
        <div className="mb-2">
          <span className="font-semibold">Ngày Đăng Ký:</span> {user.registeredDate}
        </div>
        <div className="mb-2">
          <span className="font-semibold">Trạng Thái:</span> 
          <span className={`ml-2 px-3 py-1 rounded-full text-sm font-semibold ${getStatusClasses(user.status)}`}>
            {user.status}
          </span>
          {user.role === 'Manager' && ( // Nút toggle status chỉ cho Manager
            <button
              onClick={handleToggleStatus}
              className={`ml-3 px-3 py-1 rounded-md text-xs transition duration-200 
                ${user.status === 'Active' 
                   ? 'bg-red-500 text-white hover:bg-red-600' 
                   : 'bg-green-500 text-white hover:bg-green-600'
                }`}
            >
              {user.status === 'Active' ? 'Vô hiệu hóa' : 'Kích hoạt'}
            </button>
          )}
        </div>
      </div>

      {/* Lịch sử đơn hàng chỉ hiển thị cho Khách hàng */}
      {user.role === 'Khách hàng' && (
        <div className="mt-8 pt-6 border-t border-gray-200">
          <h3 className="text-xl font-semibold text-gray-700 mb-4">Lịch Sử Đơn Hàng (Chưa có dữ liệu)</h3>
          <p className="text-gray-500">Thông tin lịch sử đơn hàng của người dùng sẽ được hiển thị tại đây.</p>
        </div>
      )}
    </div>
  );
};

export default UserDetailPage;