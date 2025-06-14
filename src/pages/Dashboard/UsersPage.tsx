import React, { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';

// Định nghĩa kiểu dữ liệu cho người dùng (đã có)
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

// Dữ liệu người dùng giả (đã có Manager và các trường thông tin mới)
const initialUsers: User[] = [
  // --- Khách hàng ---
  { id: 'U001', name: 'Nguyễn Thanh Tùng', email: 'tung.nt@example.com', phone: '0901234567', address: '123 Đường ABC, Quận 1, TP.HCM', role: 'Khách hàng', status: 'Active', registeredDate: '2023-01-15' },
  { id: 'U002', name: 'Phạm Thị Lan', email: 'lan.pt@example.com', phone: '0912345678', address: '456 Đường XYZ, Quận 3, TP.HCM', role: 'Khách hàng', status: 'Active', registeredDate: '2023-03-20' },
  { id: 'U003', name: 'Đỗ Văn Khoa', email: 'khoa.dv@example.com', phone: '0987654321', address: '789 Đường KLN, Quận Bình Thạnh, TP.HCM', role: 'Khách hàng', status: 'Inactive', registeredDate: '2023-05-10' },
  { id: 'U004', name: 'Trần Thị Thu', email: 'thu.tt@example.com', phone: '0976543210', address: '101 Đường PQR, Quận 5, TP.HCM', role: 'Khách hàng', status: 'Active', registeredDate: '2023-07-01' },
  { id: 'U005', name: 'Lê Hoàng Minh', email: 'minh.lh@example.com', phone: '0965432109', address: '202 Đường STU, Quận 7, TP.HCM', role: 'Khách hàng', status: 'Active', registeredDate: '2023-09-05' },
  { id: 'U006', name: 'Ngô Thị Diệu', email: 'dieu.nt@example.com', phone: '0954321098', address: '303 Đường VWX, TP.Thủ Đức, TP.HCM', role: 'Khách hàng', status: 'Inactive', registeredDate: '2023-11-12' },
  { id: 'U007', name: 'Trần Văn Mạnh', email: 'manh.tv@example.com', phone: '0900112233', address: '404 Đường YZA, Quận 10, TP.HCM', role: 'Khách hàng', status: 'Active', registeredDate: '2024-01-22' },
  { id: 'U008', name: 'Phan Thị Hoa', email: 'hoa.pt@example.com', phone: '0911223344', address: '505 Đường BCD, Quận Gò Vấp, TP.HCM', role: 'Khách hàng', status: 'Active', registeredDate: '2024-03-08' },
  { id: 'U009', name: 'Đặng Quốc Huy', email: 'huy.dq@example.com', phone: '0922334455', address: '606 Đường EFG, Quận Tân Bình, TP.HCM', role: 'Khách hàng', status: 'Inactive', registeredDate: '2024-05-18' },
  { id: 'U010', name: 'Vũ Thị Kim', email: 'kim.vt@example.com', phone: '0933445566', address: '707 Đường HIJ, Quận Phú Nhuận, TP.HCM', role: 'Khách hàng', status: 'Active', registeredDate: '2024-07-25' },
  // --- Managers ---
  { id: 'M001', name: 'Nguyễn Văn Quản Lý 1', email: 'manager1@example.com', phone: '0910000001', address: '888 Đường Đinh Bộ Lĩnh, Quận Bình Thạnh, TP.HCM', role: 'Manager', status: 'Active', branch: 'Chi nhánh 1', registeredDate: '2022-01-01' },
  { id: 'M002', name: 'Trần Thị Quản Lý 2', email: 'manager2@example.com', phone: '0910000002', address: '999 Đường Cách Mạng Tháng 8, Quận 3, TP.HCM', role: 'Manager', status: 'Active', branch: 'Chi nhánh 2', registeredDate: '2022-03-10' },
  { id: 'M003', name: 'Lê Văn Quản Lý 3', email: 'manager3@example.com', phone: '0910000003', address: '111 Đường Lê Lợi, Quận 1, TP.HCM', role: 'Manager', status: 'Inactive', branch: 'Chi nhánh 3', registeredDate: '2022-05-20' },
  { id: 'M004', name: 'Phan Văn Quản Lý 4', email: 'manager4@example.com', phone: '0910000004', address: '222 Đường Trần Hưng Đạo, Quận 5, TP.HCM', role: 'Manager', status: 'Active', branch: 'Chi nhánh 1', registeredDate: '2022-07-15' },
];

const UsersPage: React.FC = () => {
  const [users, setUsers] = useState<User[]>(initialUsers); 
  const [activeTab, setActiveTab] = useState<'customers' | 'managers'>('customers'); 
  const [editingManagerId, setEditingManagerId] = useState<string | null>(null); 
  const [editFormData, setEditFormData] = useState<Partial<User>>({}); 
  const navigate = useNavigate();

  // --- State cho tìm kiếm và lọc ---
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [filterStatus, setFilterStatus] = useState<string>('all'); // 'all', 'Active', 'Inactive'
  const [filterBranch, setFilterBranch] = useState<string>('all'); // 'all' hoặc tên chi nhánh

  // Lấy danh sách các chi nhánh duy nhất từ dữ liệu manager
  const uniqueBranches = useMemo(() => {
    const managerUsers = users.filter(user => user.role === 'Manager');
    const branches = new Set<string>();
    managerUsers.forEach(manager => {
      if (manager.branch) {
        branches.add(manager.branch);
      }
    });
    return Array.from(branches);
  }, [users]);


  // Lọc người dùng theo tab hiện tại VÀ theo tiêu chí tìm kiếm/lọc
  const filteredAndSearchedUsers = useMemo(() => {
    let tempUsers = users.filter(user => 
      activeTab === 'customers' ? user.role === 'Khách hàng' : user.role === 'Manager'
    );

    // Áp dụng tìm kiếm theo ID hoặc Tên
    if (searchTerm) {
      const lowerCaseSearchTerm = searchTerm.toLowerCase();
      tempUsers = tempUsers.filter(user =>
        user.id.toLowerCase().includes(lowerCaseSearchTerm) ||
        user.name.toLowerCase().includes(lowerCaseSearchTerm)
      );
    }

    // Lọc theo trạng thái
    if (filterStatus !== 'all') {
      tempUsers = tempUsers.filter(user => user.status === filterStatus);
    }

    // Lọc theo chi nhánh (chỉ áp dụng cho tab managers)
    if (activeTab === 'managers' && filterBranch !== 'all') {
      tempUsers = tempUsers.filter(user => user.branch === filterBranch);
    }

    return tempUsers;
  }, [users, activeTab, searchTerm, filterStatus, filterBranch]);


  // --- State cho phân trang ---
  const [currentPage, setCurrentPage] = useState(1);
  const [usersPerPage, setUsersPerPage] = useState(10); 

  // Tính toán tổng số trang dựa trên filteredAndSearchedUsers
  const totalPages = Math.ceil(filteredAndSearchedUsers.length / usersPerPage);

  // Lấy người dùng cho trang hiện tại từ filteredAndSearchedUsers
  const currentUsers = useMemo(() => {
    const indexOfLastUser = currentPage * usersPerPage;
    const indexOfFirstUser = indexOfLastUser - usersPerPage;
    return filteredAndSearchedUsers.slice(indexOfFirstUser, indexOfLastUser);
  }, [currentPage, usersPerPage, filteredAndSearchedUsers]);

  const getStatusClasses = (status: string) => {
    return status === 'Active' ? 'bg-green-200 text-green-800' : 'bg-red-200 text-red-800';
  };

  const handleToggleStatus = (userId: string) => {
    setUsers(prevUsers => {
      return prevUsers.map(user => {
        if (user.id === userId) {
          const newStatus = user.status === 'Active' ? 'Inactive' : 'Active';
          console.log(`Updating user ${user.id} status to ${newStatus}`);
          // Trong thực tế, bạn sẽ gửi yêu cầu API
          return { ...user, status: newStatus };
        }
        return user;
      });
    });
  };

  const handleViewDetails = (userId: string) => {
    navigate(`/dashboard/users/${userId}`);
  };

  const handleEditManager = (manager: User) => {
    setEditingManagerId(manager.id);
    setEditFormData({ ...manager }); 
  };

  const handleEditFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setEditFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSaveManager = () => {
    setUsers(prevUsers => {
      return prevUsers.map(user => {
        if (user.id === editingManagerId) {
          console.log(`Saving manager ${user.id} with new data`, editFormData);
          // Trong thực tế, bạn sẽ gửi yêu cầu API để cập nhật
          return { ...user, ...editFormData as User }; // Cast to User
        }
        return user;
      });
    });
    setEditingManagerId(null); 
    setEditFormData({});
  };

  const handleCancelEdit = () => {
    setEditingManagerId(null);
    setEditFormData({});
  };

  // Hàm xử lý chuyển trang
  const paginate = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  // Tạo mảng số trang để render nút
  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  // Reset về trang 1 khi tab hoặc tiêu chí lọc/tìm kiếm thay đổi
  React.useEffect(() => {
    setCurrentPage(1);
  }, [activeTab, searchTerm, filterStatus, filterBranch]); // Thêm các dependencies lọc mới


  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold text-gray-700 mb-4">Quản Lý Người Dùng</h2>
      <p className="text-gray-600 mb-6">Danh sách các tài khoản trong hệ thống.</p>

      {/* Tab Navigation */}
      <div className="flex border-b border-gray-200 mb-6">
        <button
          onClick={() => { setActiveTab('customers'); setCurrentPage(1); setSearchTerm(''); setFilterStatus('all'); setFilterBranch('all'); }} // Reset all filters
          className={`px-4 py-2 text-sm font-medium focus:outline-none 
            ${activeTab === 'customers' ? 'border-b-2 border-green-500 text-green-600' : 'text-gray-500 hover:text-gray-700'}`}
        >
          Khách hàng
        </button>
        <button
          onClick={() => { setActiveTab('managers'); setCurrentPage(1); setSearchTerm(''); setFilterStatus('all'); setFilterBranch('all'); }} // Reset all filters
          className={`px-4 py-2 text-sm font-medium focus:outline-none 
            ${activeTab === 'managers' ? 'border-b-2 border-green-500 text-green-600' : 'text-gray-500 hover:text-gray-700'}`}
        >
          Quản lý
        </button>
      </div>

      {/* Phần Tìm kiếm và Lọc */}
      <div className="mb-4 p-4 border border-gray-200 rounded-lg bg-gray-50">
        <h3 className="text-lg font-semibold text-gray-700 mb-3">Tìm kiếm & Lọc</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4"> {/* Tăng số cột cho desktop */}
          {/* Tìm kiếm theo ID hoặc Tên người dùng */}
          <div>
            <label htmlFor="search-user" className="block text-sm font-medium text-gray-700 mb-1">Tìm kiếm:</label>
            <input
              type="text"
              id="search-user"
              placeholder="ID hoặc Tên người dùng"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            />
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

          {/* Lọc theo chi nhánh (chỉ cho tab managers) */}
          {activeTab === 'managers' && (
            <div>
              <label htmlFor="filter-branch" className="block text-sm font-medium text-gray-700 mb-1">Chi nhánh:</label>
              <select
                id="filter-branch"
                value={filterBranch}
                onChange={(e) => setFilterBranch(e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              >
                <option value="all">Tất cả chi nhánh</option>
                {uniqueBranches.map(branch => (
                  <option key={branch} value={branch}>{branch}</option>
                ))}
              </select>
            </div>
          )}
        </div>
        {/* Nút reset filter */}
        <div className="mt-4 flex justify-end">
          <button
            onClick={() => {
              setSearchTerm('');
              setFilterStatus('all');
              setFilterBranch('all');
            }}
            className="px-4 py-2 bg-gray-400 text-white rounded-md hover:bg-gray-500 transition duration-200"
          >
            Reset Lọc
          </button>
        </div>
      </div>

      {/* Tùy chọn số lượng người dùng trên mỗi trang */}
      <div className="mb-4 flex justify-end items-center">
        <label htmlFor="users-per-page" className="text-gray-700 mr-2">Người dùng mỗi trang:</label>
        <select
          id="users-per-page"
          value={usersPerPage}
          onChange={(e) => {
            setUsersPerPage(Number(e.target.value));
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

      {/* Bảng người dùng */}
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200">
          <thead>
            <tr className="bg-gray-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-left">ID</th>
              <th className="py-3 px-6 text-left">Họ và Tên</th>
              <th className="py-3 px-6 text-left">Email</th>
              {activeTab === 'managers' && <th className="py-3 px-6 text-left">Chi nhánh</th>} 
              <th className="py-3 px-6 text-left">Trạng Thái</th>
              <th className="py-3 px-6 text-center">Hành Động</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm font-light">
            {filteredAndSearchedUsers.length === 0 ? ( // Sửa lỗi hiển thị "Không có người dùng"
              <tr>
                <td colSpan={activeTab === 'managers' ? 6 : 5} className="py-4 text-center text-gray-500">
                  Không có người dùng nào khớp với tiêu chí lọc/tìm kiếm.
                </td>
              </tr>
            ) : (
              currentUsers.map((user) => (
                <tr key={user.id} className="border-b border-gray-200 hover:bg-gray-50">
                  <td className="py-3 px-6 text-left whitespace-nowrap">{user.id}</td>
                  <td className="py-3 px-6 text-left">{user.name}</td>
                  <td className="py-3 px-6 text-left">{user.email}</td>
                  {activeTab === 'managers' && (
                    <td className="py-3 px-6 text-left">{user.branch || 'N/A'}</td>
                  )}
                  <td className="py-3 px-6 text-left">
                    <span 
                      className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusClasses(user.status)}`}
                    >
                      {user.status}
                    </span>
                  </td>
                  <td className="py-3 px-6 text-center whitespace-nowrap">
                    {activeTab === 'managers' && editingManagerId === user.id ? (
                      // Nút Save/Cancel chỉ hiển thị trong popup, không cần ở đây
                      <div className="flex items-center justify-center space-x-2">
                         {/* Các nút này sẽ được quản lý bởi popup form */}
                      </div>
                    ) : (
                      // Nút hành động bình thường
                      <div className="flex items-center justify-center space-x-2">
                        <button
                          onClick={() => handleToggleStatus(user.id)}
                          className={`px-3 py-1 rounded-md text-xs transition duration-200 
                            ${user.status === 'Active' 
                               ? 'bg-red-500 text-white hover:bg-red-600' 
                               : 'bg-green-500 text-white hover:bg-green-600'
                            }`}
                        >
                          {user.status === 'Active' ? 'Vô hiệu hóa' : 'Kích hoạt'}
                        </button>
                        {activeTab === 'managers' && ( // Chỉ hiện nút chỉnh sửa cho manager
                          <button
                            onClick={() => handleEditManager(user)}
                            className="bg-yellow-500 text-white px-3 py-1 rounded-md text-xs hover:bg-yellow-600 transition duration-200"
                          >
                            Sửa
                          </button>
                        )}
                        <button
                          onClick={() => handleViewDetails(user.id)}
                          className="bg-blue-500 text-white px-3 py-1 rounded-md text-xs hover:bg-blue-600 transition duration-200"
                        >
                          Chi tiết
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Form chỉnh sửa manager (Popup) */}
      {editingManagerId && activeTab === 'managers' && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-md">
            <h3 className="text-xl font-semibold mb-4 text-gray-800">Chỉnh sửa thông tin Quản lý</h3>
            <div className="space-y-4">
              <div>
                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="edit-name">Tên:</label>
                <input
                  type="text"
                  id="edit-name"
                  name="name"
                  value={editFormData.name || ''}
                  onChange={handleEditFormChange}
                  className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                />
              </div>
              <div>
                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="edit-email">Email:</label>
                <input
                  type="email"
                  id="edit-email"
                  name="email"
                  value={editFormData.email || ''}
                  onChange={handleEditFormChange}
                  className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                />
              </div>
              <div>
                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="edit-phone">Điện thoại:</label>
                <input
                  type="text"
                  id="edit-phone"
                  name="phone"
                  value={editFormData.phone || ''}
                  onChange={handleEditFormChange}
                  className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                />
              </div>
              <div>
                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="edit-branch">Chi nhánh:</label>
                <input
                  type="text"
                  id="edit-branch"
                  name="branch"
                  value={editFormData.branch || ''}
                  onChange={handleEditFormChange}
                  className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                />
              </div>
            </div>
            <div className="flex justify-end mt-6 space-x-3">
              <button
                onClick={handleSaveManager}
                className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition duration-200"
              >
                Lưu thay đổi
              </button>
              <button
                onClick={handleCancelEdit}
                className="bg-gray-400 text-gray-800 px-4 py-2 rounded-md hover:bg-gray-500 transition duration-200"
              >
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}


      {/* --- Phần phân trang --- */}
      <div className="mt-6 flex justify-between items-center flex-wrap">
        <div className="text-sm text-gray-600 mb-2 md:mb-0">
          Hiển thị {Math.min((currentPage - 1) * usersPerPage + 1, filteredAndSearchedUsers.length)} - {Math.min(currentPage * usersPerPage, filteredAndSearchedUsers.length)} trên tổng số {filteredAndSearchedUsers.length} người dùng
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
    </div>
  );
};

export default UsersPage;