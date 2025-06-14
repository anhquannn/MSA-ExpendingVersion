import React from 'react';
import { useNavigate } from 'react-router-dom';
import defaultAvatar from '../../../assets/images/default-avatar.png'; 

interface AppBarProps {
  userName: string;
  userAvatar?: string; 
  onLogout: () => void;
  onAvatarClick?: () => void; 
  unreadNotifications?: number; 
  onNotificationsClick?: () => void; 
  isSidebarOpen: boolean; 
  isMobile: boolean; // NEW PROP: Để AppBar biết là mobile hay desktop
}

const AppBar: React.FC<AppBarProps> = ({ 
  userName, 
  userAvatar, 
  onLogout, 
  onAvatarClick,
  unreadNotifications = 0, 
  onNotificationsClick,
  isSidebarOpen,
  isMobile // NEW PROP
}) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('authToken'); 
    navigate('/login'); 
  };

  return (
    <div className="flex items-center justify-between px-6 py-3 bg-white shadow-md z-20 relative">
      {/* Container cho các phần tử bên trái: Nút Hamburger (Mobile) & Nút Toggle Sidebar (Desktop) & Tiêu đề */}
      <div className="flex items-center space-x-3">
        {/* Nút Hamburger để mở Sidebar trên Mobile (chỉ hiện khi isMobile) */}
        {isMobile && (
          <button 
            onClick={onAvatarClick} // onAvatarClick sẽ là toggleSidebar từ DashboardLayout
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
              onClick={onAvatarClick} // Cũng sử dụng onAvatarClick để toggle Sidebar
              className="p-2 text-gray-600 hover:text-gray-900 focus:outline-none"
              aria-label={isSidebarOpen ? "Ẩn Sidebar" : "Hiện Sidebar"}
          >
              {isSidebarOpen ? (
                  // Icon để ẩn (ví dụ: mũi tên trái)
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7"></path></svg>
              ) : (
                  // Icon để hiện (ví dụ: mũi tên phải)
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 5l7 7-7 7M5 5l7 7-7 7"></path></svg>
              )}
          </button>
        )}

        {/* Tiêu đề Dashboard */}
        <h1 className="text-2xl font-bold text-gray-800">Dashboard</h1> 
      </div>
      
      <div className="flex items-center space-x-4 ml-auto">
        {/* Icon Thông báo */}
        <button
          onClick={onNotificationsClick}
          className="relative p-2 rounded-full hover:bg-gray-100 transition duration-300 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-50"
          aria-label="Thông báo"
        >
          <svg className="w-6 h-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
          </svg>
          {unreadNotifications > 0 && (
            <span className="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-red-100 bg-red-600 rounded-full transform translate-x-1/2 -translate-y-1/2">
              {unreadNotifications}
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