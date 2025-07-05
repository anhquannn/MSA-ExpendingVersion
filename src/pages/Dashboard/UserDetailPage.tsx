// File: src/pages/Dashboard/UserDetailPage.tsx

import React, { useState, useEffect, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

// --- Import các service và types thật ---
import { userService, User, UserUpdatePayload, Role } from '../../services/userService';
import { roleService } from '../../services/roleService';
import { AuthTokenManager } from '../../services/apiService';

const formatDateForInput = (dateString: string | null | undefined): string => {
  if (!dateString) return '';
  const parsableDateString = dateString.replace(' ', 'T');
  try {
    const date = new Date(parsableDateString);
    if (isNaN(date.getTime())) return '';
    return date.toISOString().slice(0, 16);
  } catch (error) {
    return '';
  }
};


const UserDetailPage: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState<Partial<UserUpdatePayload & { password?: string }>>({});

  const {
    data: user,
    isLoading,
    isError,
    error
  } = useQuery({
    queryKey: ['user', userId],
    queryFn: () => userService.getUserById(Number(userId)),
    enabled: !!userId,
  });

  const { data: allRoles = [] } = useQuery({
    queryKey: ['allRoles'],
    queryFn: () => roleService.getAllRoles(),
  });

  useEffect(() => {
    if (user) {
      setEditData({
        fullName: user.fullName || '',
        phoneNumber: user.phoneNumber || '',
        birthday: formatDateForInput(user.birthday),
        roles: user.roles?.[0] ? [user.roles[0].roleId] : [],
      });
    }
  }, [user]);

  const { currentUser } = useMemo(() => ({ currentUser: AuthTokenManager.getCurrentUser() as User | null }), []);
  const canModify = useMemo(() => {
    if (!currentUser) return false;
    const hasPower = currentUser.roles?.some(r => ['ADMIN','MANAGER_2','MANAGER_3','MANAGER_4'].includes(r.name));
    return hasPower;   // cho phép sửa mọi user nếu có quyền
  }, [currentUser]);

  const updateUserMutation = useMutation({
    mutationFn: ({ userId, payload }: { userId: number; payload: UserUpdatePayload }) =>
      userService.updateUser(userId, payload),
    onSuccess: (updatedUser) => {
      alert('Cập nhật thông tin thành công!');
      queryClient.setQueryData(['user', userId], updatedUser);
      queryClient.invalidateQueries({ queryKey: ['users'] });
      setIsEditing(false);
    },
    onError: (err: Error) => alert(`Lỗi khi cập nhật: ${err.message}`),
  });

  const deleteUserMutation = useMutation({
    mutationFn: (id: number) => userService.deleteUser(id),
    onSuccess: () => {
      alert('Xóa người dùng thành công!');
      queryClient.invalidateQueries({ queryKey: ['users'] });
      navigate('/dashboard/users');
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  const handleCancelClick = () => {
    setIsEditing(false);
    if (user) {
      setEditData({
        fullName: user.fullName || '',
        phoneNumber: user.phoneNumber || '',
        email: user.email || '',
        password: '',
        birthday: formatDateForInput(user.birthday),
        roles: user.roles?.map(r => r.roleId) || [],
      });
    }
  };


  const handleEditClick = () => {
    setIsEditing(true);
    if (user) {
      setEditData({
        fullName: user.fullName || '',
        phoneNumber: user.phoneNumber || '',
        birthday: formatDateForInput(user.birthday),
        roles: user.roles?.[0] ? [user.roles[0].roleId] : [],
      });
    }
  };
  const handleFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setEditData(prev => ({
      ...prev,
      [name]: name === 'roleId' ? Number(value) || undefined : value
    }));
  };

  const handleSaveClick = () => {
    if (!user) return;
    const formattedBirthday = editData.birthday
      ? new Date(editData.birthday).toISOString().slice(0, 19).replace('T', ' ')
      : undefined;

    const payload: UserUpdatePayload = {
      fullName: editData.fullName,
      phoneNumber: editData.phoneNumber,
      email: editData.email,
      password: editData.password,
      birthday: formattedBirthday,
      roles: editData.roles || [],
    };

    updateUserMutation.mutate({ userId: user.userId, payload });
  };

  const handleRolesChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value: roleName, checked } = e.target;
    const currentRoles = editData.roles || [];
    if (checked) {
      setEditData(prev => ({
        ...prev,
        roles: [...currentRoles, Number(roleName)],
      }));
    } else {
      setEditData(prev => ({
        ...prev,
        roles: currentRoles.filter(role => role !== Number(roleName)),
      }));
    }
  };
  const handleDelete = () => {
    if (!user) return;
    if (window.confirm(`Bạn có chắc muốn xóa người dùng "${user.fullName}"?`)) {
      deleteUserMutation.mutate(user.userId);
    }
  };

  // --- RENDER ---
  if (isLoading) return <div className="p-8 text-center">Đang tải chi tiết...</div>;
  if (isError) return <div className="p-8 text-center text-red-500">Lỗi: {(error as Error).message}</div>;
  if (!user) return <div className="p-8 text-center">Không tìm thấy người dùng.</div>;

  return (
    <div className="bg-white p-8 rounded-lg shadow-lg max-w-4xl mx-auto mt-10">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-3xl font-bold text-gray-800">{isEditing ? 'Chỉnh Sửa Thông Tin' : `Chi Tiết: ${user.fullName}`}</h2>
        <div className="flex space-x-2">
          {isEditing ? (
            <>
              <button onClick={handleSaveClick} disabled={updateUserMutation.isPending} className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 disabled:bg-gray-400">
                {updateUserMutation.isPending ? 'Đang lưu...' : 'Lưu'}
              </button>
              <button onClick={handleCancelClick} className="bg-gray-300 text-gray-800 px-4 py-2 rounded-md hover:bg-gray-400">Hủy</button>
            </>
          ) : (
            <>
              <button onClick={() => navigate(-1)} className="bg-gray-200 text-gray-800 px-4 py-2 rounded-md hover:bg-gray-300">Quay lại</button>
              {canModify && (
                <>
                  <button onClick={handleEditClick} className="bg-yellow-500 text-white px-4 py-2 rounded-md hover:bg-yellow-600">Chỉnh Sửa</button>
                  <button onClick={handleDelete} disabled={deleteUserMutation.isPending} className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 disabled:bg-gray-400">
                    {deleteUserMutation.isPending ? '...' : 'Xóa'}
                  </button>
                </>
              )}
            </>
          )}
        </div>
      </div>

      <div className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="flex flex-col">
            <label className="text-sm font-semibold text-gray-600 mb-1">Họ và tên</label>
            <input type="text" name="fullName" value={isEditing ? editData.fullName : user.fullName} readOnly={!isEditing} onChange={handleFormChange} className={`p-2 border rounded-md transition-colors ${!isEditing ? 'bg-gray-100 border-transparent' : 'bg-white border-gray-300'}`} />
          </div>
          <div className="flex flex-col">
            <label className="text-sm font-semibold text-gray-600 mb-1">Email</label>
            <input type="email" name="email" value={isEditing ? editData.email || '' : user.email} readOnly={!isEditing} onChange={handleFormChange} className={`p-2 border rounded-md transition-colors ${!isEditing ? 'bg-gray-100 border-transparent' : 'bg-white border-gray-300'}`} />
          </div>
          {isEditing && (
            <div className="flex flex-col">
              <label className="text-sm font-semibold text-gray-600 mb-1">Mật khẩu mới</label>
              <input type="password" name="password" value={editData.password || ''} onChange={handleFormChange} className="p-2 border rounded-md bg-white border-gray-300" placeholder="Để trống nếu không đổi" />
            </div>
          )}
          <div className="flex flex-col">
            <label className="text-sm font-semibold text-gray-600 mb-1">Số điện thoại</label>
            <input type="tel" name="phoneNumber" value={isEditing ? editData.phoneNumber || '' : user.phoneNumber || ''} readOnly={!isEditing} onChange={handleFormChange} className={`p-2 border rounded-md transition-colors ${!isEditing ? 'bg-gray-100 border-transparent' : 'bg-white border-gray-300'}`} />
          </div>
          <div className="flex flex-col">
            <label className="text-sm font-semibold text-gray-600 mb-1">Ngày sinh</label>
            {isEditing ? (
              <input type="datetime-local" name="birthday" value={editData.birthday || ''} onChange={handleFormChange} className="p-2 border rounded-md bg-white border-gray-300" />
            ) : (
              <input type="text" value={user.birthday ? new Date(user.birthday).toLocaleString('vi-VN') : 'Chưa cập nhật'} readOnly className="p-2 border rounded-md bg-gray-100" />
            )}
          </div>

          <div className="md:col-span-2 flex flex-col">
            <label className="text-sm font-semibold text-gray-600 mb-1">Vai trò</label>
            {isEditing ? (
              <select
                name="roleId"
                value={editData.roles?.[0] ?? ''}
                onChange={handleFormChange}
                className="p-2 border rounded-md bg-white border-gray-300"
              >
                <option value="">-- Chọn vai trò --</option>
                {allRoles.map((role: Role) => (
                  <option key={role.roleId} value={role.roleId}>
                    {role.description} ({role.name})
                  </option>
                ))}
              </select>
            ) : (
              <input
                type="text"
                value={user.roles?.map(r => r.name).join(', ') || 'N/A'}
                readOnly
                className="p-2 border rounded-md bg-gray-100 border-transparent"
              />
            )}
          </div>

        </div>
      </div>
    </div>
  );
};

export default UserDetailPage;