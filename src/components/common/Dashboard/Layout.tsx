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
  const [unreadCount, setUnreadCount] = useState(3); 

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleMouseEnterSidebar = () => {
    if (!isMobile) { 
      setIsSidebarOpen(true);
    }
  };

  const handleMouseLeaveSidebar = () => {
    if (!isMobile) { 
      setIsSidebarOpen(false);
    }
  };

  const handleNotificationsClick = () => {
    alert(`Bạn có ${unreadCount} thông báo mới!`);
    setUnreadCount(0); 
  };

  useEffect(() => {
    const handleResize = () => {
      const mobile = window.innerWidth < 768; 
      setIsMobile(mobile);
      // Trên desktop, Sidebar ban đầu mở. Trên mobile, Sidebar ban đầu đóng.
      setIsSidebarOpen(!mobile); 
    };

    window.addEventListener('resize', handleResize);
    handleResize(); 

    return () => window.removeEventListener('resize', handleResize);
  }, []); 

  return (
    <div className="flex min-h-screen bg-gray-100">
      {/* 1. Sidebar cho Desktop */}
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
          ${isMobile ? 
            '' : // Trên mobile, Sidebar là fixed overlay, không ảnh hưởng margin
            (isSidebarOpen ? 'ml-2' : 'ml-2') // Trên desktop, điều chỉnh margin-left
          }`} 
      >
        <AppBar 
          userName={userName} 
          userAvatar={userAvatar} 
          onLogout={onLogout} 
          onAvatarClick={toggleSidebar} 
          unreadNotifications={unreadCount} 
          onNotificationsClick={handleNotificationsClick} 
          isSidebarOpen={isSidebarOpen} 
          isMobile={isMobile} // Truyền isMobile xuống AppBar
        />

        {/* Backdrop cho mobile khi sidebar mở */}
        {isSidebarOpen && isMobile && ( 
          <div 
            className="fixed inset-0 bg-black bg-opacity-50 z-20" 
            onClick={toggleSidebar}
          ></div>
        )}

        {/* 2. Sidebar Overlay cho Mobile (hiển thị có điều kiện) */}
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