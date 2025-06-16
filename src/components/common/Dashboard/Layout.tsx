// src/components/common/Dashboard/Layout.tsx
import React, { useState, useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import AppBar from './AppBar';
import Sidebar from './Sidebar'; // Vẫn import Sidebar

interface DashboardLayoutProps {
  userName: string;
  userAvatar?: string;
  onLogout: () => void;
}

const DashboardLayout: React.FC<DashboardLayoutProps> = ({ userName, userAvatar, onLogout }) => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true); 
  const [isMobile, setIsMobile] = useState(false); 
  const [unreadCount, setUnreadCount] = useState(3); // Giữ lại state này nếu bạn muốn hiển thị số thông báo chưa đọc

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleMouseEnterSidebar = () => {
    // Chỉ tự động mở sidebar nếu không phải mobile và sidebar đang đóng
    if (!isMobile && !isSidebarOpen) { 
      setIsSidebarOpen(true);
    }
  };

  const handleMouseLeaveSidebar = () => {
    // Chỉ tự động đóng sidebar nếu không phải mobile và sidebar đang mở
    // Bạn có thể cần một state để biết sidebar có "thực sự" được người dùng đóng hay không
    // Để đơn giản, cứ cho là sẽ đóng khi rời chuột
    if (!isMobile && isSidebarOpen) { 
      setIsSidebarOpen(false);
    }
  };

  // !!! LOẠI BỎ onNotificationsClick Ở ĐÂY HOẶC ĐỂ TRỐNG !!!
  // AppBar đã tự xử lý điều hướng đến /dashboard/notifications
  // const handleNotificationsClick = () => {
  //   alert(`Bạn có ${unreadCount} thông báo mới!`);
  //   setUnreadCount(0); // Đặt lại về 0 sau khi người dùng nhấp (hoặc sau khi họ xem màn hình thông báo)
  // };

  useEffect(() => {
    const handleResize = () => {
      const mobile = window.innerWidth < 768; 
      setIsMobile(mobile);
      // Trên desktop, Sidebar ban đầu mở. Trên mobile, Sidebar ban đầu đóng.
      // Khi chuyển đổi giữa mobile/desktop, điều chỉnh trạng thái sidebar
      setIsSidebarOpen(!mobile); 
    };

    window.addEventListener('resize', handleResize);
    handleResize(); 

    return () => window.removeEventListener('resize', handleResize);
  }, []); 

  // Xác định chiều rộng sidebar cho class Tailwind
  const sidebarWidthClass = isSidebarOpen ? 'w-64' : 'w-24'; // Ví dụ: 64 = 16rem, 20 = 5rem
  const mainContentMarginClass = isSidebarOpen ? 'ml-8' : 'ml-8'; 

  return (
    <div className="flex min-h-screen bg-gray-100">
      {/* Sidebar cho Desktop (fixed, không ảnh hưởng đến main content margin) */}
      {!isMobile && (
        <Sidebar 
          isSidebarOpen={isSidebarOpen} 
          onToggleSidebar={toggleSidebar} 
          onMouseEnterSidebar={handleMouseEnterSidebar} 
          onMouseLeaveSidebar={handleMouseLeaveSidebar} 
        />
      )}

      {/* Main content area */}
      <div 
        className={`flex-1 flex flex-col transition-all duration-300 ease-in-out 
          ${!isMobile ? mainContentMarginClass : ''} // Chỉ áp dụng margin trên desktop
        `} 
      >
        <AppBar 
          userName={userName} 
          userAvatar={userAvatar} 
          onLogout={onLogout} 
          onAvatarClick={toggleSidebar} 
          unreadNotifications={unreadCount} 
          // --- LOẠI BỎ onNotificationsClick HOẶC ĐỂ TRỐNG ---
          // onNotificationsClick={handleNotificationsClick} // Dòng này không cần thiết nữa
          isSidebarOpen={isSidebarOpen} 
          isMobile={isMobile} 
        />

        {/* Backdrop cho mobile khi sidebar mở */}
        {isSidebarOpen && isMobile && ( 
          <div 
            className="fixed inset-0 bg-black bg-opacity-50 z-20" 
            onClick={toggleSidebar}
          ></div>
        )}

        {/* Sidebar Overlay cho Mobile (hiển thị có điều kiện) */}
        {isMobile && (
          <Sidebar 
            isSidebarOpen={isSidebarOpen} 
            onToggleSidebar={toggleSidebar} 
            onMouseEnterSidebar={() => {}} // Không cần hover trên mobile
            onMouseLeaveSidebar={() => {}} // Không cần hover trên mobile
            isMobileOverlay={true} // Báo hiệu đây là phiên bản mobile overlay
          />
        )}

        <main className="flex-1 overflow-auto"> 
          <Outlet /> 
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;