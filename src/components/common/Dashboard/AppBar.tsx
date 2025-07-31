// src/components/AppBar.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import defaultAvatar from '../../../assets/images/default-avatar.png';
import NotificationDropdown from '../../NotificationDropdown';

interface AppBarProps {
  userName: string;
  userAvatar?: string;
  onLogout: () => void;
  onAvatarClick?: () => void;
  unreadNotifications?: number;
  onNotificationsClick?: () => void; // Giữ nguyên prop này nhưng chúng ta sẽ điều chỉnh logic bên trong
  isSidebarOpen: boolean;
  isMobile: boolean;
}

const AppBar: React.FC<AppBarProps> = ({
  userName,
  userAvatar,
  onLogout,
  onAvatarClick,
  unreadNotifications = 0,
  onNotificationsClick, // Chúng ta sẽ không sử dụng trực tiếp prop này nữa, mà thay bằng handleNotificationsRedirect
  isSidebarOpen,
  isMobile
}) => {
  const navigate = useNavigate();

  // Sử dụng hàm onLogout do DashboardLayout truyền xuống để đảm bảo xóa toàn bộ session / cache chính xác
  const handleLogout = () => {
    if (onLogout) {
      onLogout();
    } else {
      // Fallback (hiếm khi xảy ra)
      navigate('/login');
    }
  };

  // Hàm mới để điều hướng đến trang thông báo
  const handleNotificationsRedirect = () => {
    navigate('notification');
    // Nếu bạn vẫn muốn chạy một hàm callback nào đó từ DashboardLayout, bạn có thể gọi onNotificationsClick ở đây
    // if (onNotificationsClick) {
    //   onNotificationsClick();
    // }
  };

  return (
    <div className="flex items-center justify-between px-6 py-3 bg-white shadow-md z-20 relative">
      {/* Container cho các phần tử bên trái: Nút Hamburger (Mobile) & Nút Toggle Sidebar (Desktop) & Tiêu đề */}
      <div className="flex items-center space-x-3">
        {/* Nút Hamburger để mở Sidebar trên Mobile (chỉ hiện khi isMobile) */}
        {isMobile && (
          <button
            onClick={onAvatarClick}
            className="p-2 text-gray-600 hover:text-gray-900 focus:outline-none"
            aria-label="Mở Sidebar"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h16M4 18h16"></path>
            </svg>
          </button>
        )}

        {/* Nút Toggle Sidebar cho Desktop (chỉ hiện khi không phải mobile) */}
        {!isMobile && (
          <button
            onClick={onAvatarClick}
            className="p-2 text-gray-600 hover:text-gray-900 focus:outline-none"
            aria-label={isSidebarOpen ? "Ẩn Sidebar" : "Hiện Sidebar"}
          >
            {isSidebarOpen ? (
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7"></path></svg>
            ) : (
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 5l7 7-7 7M5 5l7 7-7 7"></path></svg>
            )}
          </button>
        )}

        <h1 className="text-2xl font-bold text-gray-800">Dashboard</h1>
      </div>

      <div className="flex items-center space-x-4 ml-auto">
        {/* Icon Thông báo */}
        <NotificationDropdown />

        {/* Avatar và Tên người dùng */}
        <div
          className="flex items-center space-x-2 cursor-pointer"
          onClick={onAvatarClick}
        >
          <img
            src={defaultAvatar}
            alt="User Avatar"
            className="w-10 h-10 rounded-full object-cover border-2 border-green-400"
          />
          <span className="font-medium text-gray-700 hidden md:block">Admin</span>
        </div>

        {/* Nút Đăng xuất */}
        <button
          onClick={handleLogout}
          className="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 transition duration-300"
        >
          Đăng Xuất
        </button>
      </div>
    </div>
  );
};

export default AppBar;