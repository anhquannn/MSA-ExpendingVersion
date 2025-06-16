// src/pages/Dashboard/SettingsPage.tsx
import React, { useState, useEffect } from 'react';
import { LocalStorageManager } from '../../utils/app_storage'; // Đảm bảo đúng đường dẫn

// Sử dụng kiểu User từ models để đồng bộ với LocalStorageManager
import { User } from '../../models/user.model';

const SettingsPage: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState<User | null>(null);
  const [saveMessage, setSaveMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  // --- Các hàm gọi API Placeholder ---

  /**
   * Hàm giả lập gọi API để lấy thông tin người dùng
   * Trong thực tế, bạn sẽ thay thế bằng fetch/axios call đến endpoint /api/user/profile
   */
  const fetchUserProfile = async (): Promise<User | null> => {
    try {
      // Giả lập độ trễ API
      await new Promise(resolve => setTimeout(resolve, 500)); 
      
      const storedUser = LocalStorageManager.getUser();
      if (storedUser) {
        // Trong thực tế: const response = await fetch('/api/user/profile', { headers: { Authorization: `Bearer ${LocalStorageManager.getAccessToken()}` } });
        // const data = await response.json();
        // return data.user; // Hoặc cấu trúc dữ liệu tương ứng từ API của bạn
        
        return storedUser; // Trả về dữ liệu từ local storage cho mục đích demo
      }
      return null;
    } catch (error) {
      console.error("Lỗi khi tải thông tin người dùng:", error);
      setErrorMessage("Không thể tải thông tin người dùng.");
      return null;
    }
  };

  /**
   * Hàm giả lập gọi API để cập nhật thông tin người dùng
   * Trong thực tế, bạn sẽ thay thế bằng fetch/axios call đến endpoint /api/user/profile (PUT/PATCH)
   */
  const updateUserProfile = async (updatedData: User): Promise<boolean> => {
    try {
      // Giả lập độ trễ API
      await new Promise(resolve => setTimeout(resolve, 1000));

      // Trong thực tế:
      // const response = await fetch('/api/user/profile', {
      //   method: 'PUT', // Hoặc PATCH
      //   headers: {
      //     'Content-Type': 'application/json',
      //     'Authorization': `Bearer ${LocalStorageManager.getAccessToken()}`,
      //   },
      //   body: JSON.stringify(updatedData),
      // });

      // if (!response.ok) {
      //   const errorData = await response.json();
      //   throw new Error(errorData.message || "Cập nhật thất bại.");
      // }

      // const result = await response.json();
      // Cập nhật LocalStorage sau khi API trả về thành công
      LocalStorageManager.saveUser(updatedData); 
      return true;
    } catch (error: any) {
      console.error("Lỗi khi cập nhật thông tin người dùng:", error);
      setErrorMessage(error.message || "Cập nhật thông tin thất bại.");
      return false;
    }
  };

  // --- Logic Component ---

  useEffect(() => {
    const loadUser = async () => {
      setIsLoading(true);
      const currentUser = await fetchUserProfile();
      if (currentUser) {
        setUser(currentUser);
        setFormData(currentUser);
      }
      setIsLoading(false);
    };

    loadUser();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prevData => (prevData ? { ...prevData, [name]: value } : null));
    setErrorMessage(null); // Xóa thông báo lỗi khi người dùng bắt đầu chỉnh sửa lại
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (formData && !isLoading) {
      setIsLoading(true);
      setSaveMessage(null);
      setErrorMessage(null);

      const success = await updateUserProfile(formData);

      if (success) {
        setUser(formData); // Cập nhật state user sau khi lưu thành công
        setEditMode(false);
        setSaveMessage('Lưu thông tin thành công!');
        setTimeout(() => setSaveMessage(null), 3000);
      } else {
        // errorMessage đã được set trong updateUserProfile
      }
      setIsLoading(false);
    }
  };

  const handleCancel = () => {
    setEditMode(false);
    setFormData(user); // Khôi phục dữ liệu ban đầu
    setErrorMessage(null);
    setSaveMessage(null);
  };

  if (isLoading && !user) { // Hiển thị loading khi mới tải lần đầu
    return (
      <div className="p-6 text-center text-gray-600">
        Đang tải thông tin người dùng...
      </div>
    );
  }

  if (!user && !isLoading) { // Nếu không có user và không đang tải
    return (
      <div className="p-6 text-center text-red-600">
        Không thể tải thông tin người dùng. Vui lòng thử lại.
      </div>
    );
  }

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-3xl mx-auto bg-white rounded-lg shadow-lg p-8">
        <h2 className="text-3xl font-bold text-gray-800 mb-6 border-b pb-4">
          Cài đặt tài khoản
        </h2>

        {saveMessage && (
          <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4" role="alert">
            <strong className="font-bold">Thành công!</strong>
            <span className="block sm:inline"> {saveMessage}</span>
          </div>
        )}

        {errorMessage && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
            <strong className="font-bold">Lỗi!</strong>
            <span className="block sm:inline"> {errorMessage}</span>
          </div>
        )}

        <form onSubmit={handleSave}>
          <div className="space-y-6">
            <div>
              <label htmlFor="Fullname" className="block text-sm font-medium text-gray-700 mb-1">
                Tên đầy đủ
              </label>
              <input
                type="text"
                id="Fullname"
                name="Fullname"
                value={formData?.Fullname || ''}
                onChange={handleChange}
                disabled={!editMode || isLoading}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500 sm:text-sm disabled:bg-gray-100 disabled:text-gray-500"
              />
            </div>
            
            <div>
              <label htmlFor="Email" className="block text-sm font-medium text-gray-700 mb-1">
                Email
              </label>
              <input
                type="email"
                id="Email"
                name="Email"
                value={formData?.Email || ''}
                onChange={handleChange}
                disabled={!editMode || isLoading}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500 sm:text-sm disabled:bg-gray-100 disabled:text-gray-500"
              />
            </div>

            <div>
              <label htmlFor="PhoneNumber" className="block text-sm font-medium text-gray-700 mb-1">
                Số điện thoại
              </label>
              <input
                type="tel"
                id="PhoneNumber"
                name="PhoneNumber"
                value={formData?.PhoneNumber || ''}
                onChange={handleChange}
                disabled={!editMode || isLoading}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500 sm:text-sm disabled:bg-gray-100 disabled:text-gray-500"
              />
            </div>

            <div>
              <label htmlFor="Address" className="block text-sm font-medium text-gray-700 mb-1">
                Địa chỉ
              </label>
              <textarea
                id="Address"
                name="Address"
                value={formData?.Address || ''}
                onChange={handleChange}
                disabled={!editMode || isLoading}
                rows={3}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500 sm:text-sm disabled:bg-gray-100 disabled:text-gray-500"
              />
            </div>

            <div>
              <label htmlFor="Role" className="block text-sm font-medium text-gray-700 mb-1">
                Vai trò
              </label>
              <input
                type="text"
                id="Role"
                name="Role"
                value={formData?.Role || ''}
                disabled={true} // Vai trò thường không cho phép chỉnh sửa trực tiếp
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm bg-gray-100 text-gray-500 sm:text-sm cursor-not-allowed"
              />
            </div>
          </div>

          <div className="mt-8 flex justify-end space-x-3">
            {!editMode ? (
              <button
                type="button"
                onClick={() => setEditMode(true)}
                disabled={isLoading}
                className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Chỉnh sửa
              </button>
            ) : (
              <>
                <button
                  type="button"
                  onClick={handleCancel}
                  disabled={isLoading}
                  className="inline-flex justify-center py-2 px-4 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={isLoading}
                  className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {isLoading ? 'Đang lưu...' : 'Lưu thay đổi'}
                </button>
              </>
            )}
          </div>
        </form>
      </div>
    </div>
  );
};

export default SettingsPage;