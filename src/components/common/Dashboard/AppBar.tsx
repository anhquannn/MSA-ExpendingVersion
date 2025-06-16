// src/components/AppBar.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import defaultAvatar from '../../../assets/images/default-avatar.png';

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

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    navigate('/login');
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
        <button
          onClick={handleNotificationsRedirect}
          className="relative p-2 rounded-full text-gray-600 hover:text-green-600 hover:bg-gray-100 transition-colors duration-300 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-50"
          aria-label="Thông báo"
        >
          <svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            {/* Biểu tượng chuông SVG */}
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
          </svg>
          {unreadNotifications > 0 && (
            <span className="absolute top-0 right-0 inline-flex items-center justify-center h-5 w-5 rounded-full bg-red-600 text-white text-xs font-bold transform translate-x-1/4 -translate-y-1/4 ring-2 ring-white">
              {unreadNotifications > 99 ? '99+' : unreadNotifications} {/* Giới hạn hiển thị số */}
            </span>
          )}
        </button>

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
          <span className="font-medium text-gray-700 hidden md:block">{userName}</span>
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