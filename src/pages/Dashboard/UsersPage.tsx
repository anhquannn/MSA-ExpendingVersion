// src/pages/Dashboard/UsersPage.tsx

import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';

// --- Import các service và types thật ---
import { userService, User, PagingParams, UserUpdatePayload, SurveyorCreatePayload } from '../../services/userService';

// --- Component Popup/Modal để sửa thông tin User ---
const EditUserModal = ({ user, onClose, onSave, isSaving }: {
  user: User;
  onClose: () => void;
  onSave: (payload: UserUpdatePayload) => void;
  isSaving: boolean;
}) => {
  const [formData, setFormData] = useState<Partial<UserUpdatePayload>>({
    fullName: user.fullName || '',
    phoneNumber: user.phoneNumber || '',
    email: user.email || '',
    password: '',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-xl w-full max-w-md">
        <h3 className="text-xl font-semibold mb-4 text-gray-800">Chỉnh sửa: {user.fullName}</h3>
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-bold mb-1">Họ tên:</label>
            <input type="text" name="fullName" value={formData.fullName} onChange={handleChange} className="w-full p-2 border rounded-md" />
          </div>
          <div>
            <label className="block text-sm font-bold mb-1">Email:</label>
            <input type="email" name="email" value={formData.email || ''} onChange={handleChange} className="w-full p-2 border rounded-md" />
          </div>
          <div>
            <label className="block text-sm font-bold mb-1">Mật khẩu mới:</label>
            <input type="password" name="password" value={formData.password || ''} onChange={handleChange} className="w-full p-2 border rounded-md" placeholder="Để trống nếu không đổi" />
          </div>
          <div>
            <label className="block text-sm font-bold mb-1">Số điện thoại:</label>
            <input type="tel" name="phoneNumber" value={formData.phoneNumber || ''} onChange={handleChange} className="w-full p-2 border rounded-md" />
          </div>
        </div>
        <div className="flex justify-end mt-6 space-x-3">
          <button type="button" onClick={onClose} disabled={isSaving} className="bg-gray-300 text-gray-800 px-4 py-2 rounded-md hover:bg-gray-400">Hủy</button>
          <button type="submit" disabled={isSaving} className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 disabled:bg-gray-400">
            {isSaving ? 'Đang lưu...' : 'Lưu thay đổi'}
          </button>
        </div>
      </form>
    </div>
  );
};


const UsersPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // --- STATE CHO UI: TAB, BỘ LỌC, VÀ MODAL ---
  const tabList = [
    { key: 'customer', label: 'Khách hàng' },
    { key: 'manager', label: 'Quản lý' }, // sẽ match manager_*
    { key: 'admin', label: 'Admin' },
    { key: 'surveyor', label: 'NV Kiểm kho' },
  ] as const;
  type UserTab = typeof tabList[number]['key'];
  const [activeTab, setActiveTab] = useState<UserTab>('customer');
  const [filters, setFilters] = useState<PagingParams>({ page: 0, size: 10 });
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [creating, setCreating] = useState<boolean>(false);

  // --- DATA FETCHING VỚI useQuery ---
  const {
    data: pagedUsers,
    isLoading,
    isError,
    error
  } = useQuery({
    queryKey: ['users', activeTab, filters],
    queryFn: () => userService.getUsersByRole(activeTab === 'manager' ? 'manager_' : activeTab, filters),
    placeholderData: keepPreviousData,
  });

  const users = pagedUsers?.content || [];
  const totalPages = pagedUsers?.totalPages || 1;

  // --- MUTATIONS CHO CÁC HÀNH ĐỘNG ---
  const deleteUserMutation = useMutation({
    mutationFn: (userId: number) => userService.deleteUser(userId),
    onSuccess: () => {
      alert('Xóa người dùng thành công!');
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
    onError: (err: Error) => alert(`Lỗi khi xóa người dùng: ${err.message}`),
  });

  const updateUserMutation = useMutation({
    mutationFn: ({ userId, payload }: { userId: number; payload: UserUpdatePayload }) =>
      userService.updateUser(userId, payload),
    onSuccess: () => {
      alert('Cập nhật thành công!');
      queryClient.invalidateQueries({ queryKey: ['users'] });
      setEditingUser(null); // Đóng modal sau khi thành công
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  const createSurveyorMutation = useMutation({
    mutationFn: (payload: SurveyorCreatePayload) => userService.createSurveyor(payload),
    onSuccess: () => {
      alert('Tạo người dùng thành công!');
      queryClient.invalidateQueries({ queryKey: ['users'] });
      setCreating(false);
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  // --- EVENT HANDLERS ---
  const handleTabChange = (tab: UserTab) => {
    setActiveTab(tab);
    setFilters({ page: 0, size: 10 }); // Reset phân trang khi chuyển tab
  };

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
  };

  const handleDelete = (user: User) => {
    if (window.confirm(`Bạn có chắc muốn xóa người dùng "${user.fullName}"?`)) {
      deleteUserMutation.mutate(user.userId);
    }
  };

  const handleSaveUser = (payload: UserUpdatePayload) => {
    if (!editingUser) return;
    updateUserMutation.mutate({ userId: editingUser.userId, payload });
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold text-gray-700 mb-4">Quản Lý Người Dùng</h2>

      <div className="flex border-b mb-6 space-x-2">
        {tabList.map(t => (
        <button
          key={t.key}
          onClick={() => handleTabChange(t.key)}
          className={`px-4 py-2 text-sm font-medium ${activeTab === t.key ? 'border-b-2 border-blue-500 text-blue-600' : 'text-gray-500 hover:text-gray-700'}`}
        >
          {t.label}
        </button>
      ))}

      </div>

      {/* TODO: Thêm khu vực Filter/Search ở đây nếu cần */}

      <div className="overflow-x-auto">
        {isLoading && <p className="text-center py-4">Đang tải dữ liệu...</p>}
        {isError && <p className="text-center py-4 text-red-500">Lỗi: {error.message}</p>}

        <table className="min-w-full bg-white border">
          <thead className="bg-gray-100 text-gray-600 uppercase text-sm leading-normal">
            <tr>
              <th className="py-3 px-6 text-left">ID</th>
              <th className="py-3 px-6 text-left">Họ và Tên</th>
              <th className="py-3 px-6 text-left">Email</th>
              <th className="py-3 px-6 text-left">Số điện thoại</th>
              <th className="py-3 px-6 text-left">Vai trò</th>
              <th className="py-3 px-6 text-center">
                Hành Động
                {activeTab === 'surveyor' && (
                  <div className="mt-2 flex justify-center">
                    <button
                      className="px-3 py-1 rounded-md text-xs bg-green-600 text-white hover:bg-green-700"
                      onClick={() => setCreating(true)}
                    >
                      Thêm NV Kiểm kho
                    </button>
                  </div>
                )}
              </th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm font-light">
            {users.map((user: User) => (
              <tr key={user.userId} className="border-b hover:bg-gray-50">
                <td className="py-3 px-6">{user.userId}</td>
                <td className="py-3 px-6 font-medium">
                  <Link to={`/dashboard/users/${user.userId}`} className="text-blue-600 hover:underline">
                    {user.fullName || '(Chưa có tên)'}
                  </Link>
                </td>
                <td className="py-3 px-6">{user.email}</td>
                <td className="py-3 px-6">{user.phoneNumber || 'N/A'}</td>
                <td className="py-3 px-6">
                  <span className="font-semibold">{user.roles?.map(role => role.name).join(', ') || 'N/A'}</span>
                </td>
                <td className="py-3 px-6 text-center whitespace-nowrap">
                  <div className="flex items-center justify-center space-x-2">
                    {(activeTab === 'manager' || activeTab === 'surveyor') && (
                      <button
                        onClick={() => setEditingUser(user)}
                        className="px-3 py-1 rounded-md text-xs bg-yellow-500 text-white hover:bg-yellow-600 transition"
                      >
                        Sửa
                      </button>
                    )}
                    <button onClick={() => navigate(`/dashboard/users/${user.userId}`)} className="px-3 py-1 rounded-md text-xs bg-blue-500 text-white hover:bg-blue-600 transition">Chi tiết</button>
                    <button onClick={() => handleDelete(user)} disabled={deleteUserMutation.isPending} className="px-3 py-1 rounded-md text-xs bg-red-500 text-white hover:bg-red-600 disabled:bg-gray-400 transition">
                      {deleteUserMutation.isPending ? '...' : 'Xóa'}
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {users.length === 0 && !isLoading && (
              <tr><td colSpan={6} className="text-center py-4">Không có người dùng nào.</td></tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Phân trang */}
      <div className="flex justify-between items-center mt-6">
        {/* SỬA Ở ĐÂY: Dùng (filters.page ?? 0) để đảm bảo luôn là số */}
        <p className="text-sm text-gray-600">Trang {(filters.page ?? 0) + 1} trên {totalPages}</p>

        <div className="flex space-x-2">
          {/* SỬA Ở ĐÂY */}
          <button
            onClick={() => handlePageChange((filters.page ?? 0) - 1)}
            disabled={filters.page === 0 || isLoading}
            className="px-4 py-2 border rounded-md disabled:opacity-50"
          >
            Trước
          </button>

          {/* SỬA Ở ĐÂY */}
          <button
            onClick={() => handlePageChange((filters.page ?? 0) + 1)}
            disabled={(filters.page ?? 0) + 1 >= totalPages || isLoading}
            className="px-4 py-2 border rounded-md disabled:opacity-50"
          >
            Sau
          </button>
        </div>
      </div>

      {/* Modal chỉnh sửa sẽ được render ở đây khi `editingUser` có giá trị */}
      {/* Create surveyor modal */}
      {creating && (
        <EditUserModal
          user={{
            userId: 0,
            fullName: '',
            email: '',
            phoneNumber: '',
            birthday: null,
            address: '',
            image: null,
            deviceId: null,
            googleId: null,
            roles: null,
            branches: null,
          } as any}
          onClose={() => setCreating(false)}
          onSave={(payload) => {
            createSurveyorMutation.mutate(payload as any);
          }}
          isSaving={createSurveyorMutation.isPending}
        />
      )}

      {editingUser && (
        <EditUserModal
          user={editingUser}
          onClose={() => setEditingUser(null)}
          onSave={handleSaveUser}
          isSaving={updateUserMutation.isPending}
        />
      )}
    </div>
  );
};

export default UsersPage;