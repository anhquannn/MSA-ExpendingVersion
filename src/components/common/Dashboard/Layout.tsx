// src/components/common/Dashboard/Layout.tsx
import React, { useState, useEffect, useContext } from 'react';
import { Outlet } from 'react-router-dom';
import AppBar from './AppBar';
import { userService } from '../../../services/userService';
import { LocalStorageManager } from '../../../utils/app_storage';
import Sidebar from './Sidebar'; // Vẫn import Sidebar
import { NotificationCtx, NotificationContextType } from '../../../contexts/NotificationContext';

interface DashboardLayoutProps {
  userName?: string;
  userAvatar?: string;
  onLogout: () => void;
}

const DashboardLayout: React.FC<DashboardLayoutProps> = ({ userName: initialUserName, userAvatar, onLogout }) => {
  // --- User name state ---
  const getLatestUserName = () => {
    const stored = LocalStorageManager.getUser();
    return (
      (stored as any)?.Fullname ||
      (stored as any)?.fullName ||
      (stored as any)?.fullname ||
      (stored as any)?.name ||
      (stored as any)?.username ||
      stored?.Email ||
      initialUserName ||
      'Guest'
    );
  };

  const [userName, setUserName] = useState<string>(getLatestUserName());
  const [isSidebarOpen, setIsSidebarOpen] = useState(true); 
  const [isMobile, setIsMobile] = useState(false); 
  const { unread: unreadCount } = useContext(NotificationCtx) as NotificationContextType; 

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleMouseEnterSidebar = () => {
    if (!isMobile && !isSidebarOpen) { 
      setIsSidebarOpen(true);
    }
  };

  const handleMouseLeaveSidebar = () => {
    if (!isMobile && isSidebarOpen) { 
      setIsSidebarOpen(false);
    }
  };
  // Lắng nghe sự thay đổi user trong LocalStorage (CustomEvent được bắn từ LocalStorageManager)
  useEffect(() => {
    const handleUserUpdated = () => {
      setUserName(getLatestUserName());
    };
    window.addEventListener('app_storage_user_updated', handleUserUpdated as EventListener);
    return () => window.removeEventListener('app_storage_user_updated', handleUserUpdated as EventListener);
  }, []);

  useEffect(() => {
    const handleResize = () => {
      const mobile = window.innerWidth < 768; 
      setIsMobile(mobile);
      setIsSidebarOpen(!mobile); 
    };

    window.addEventListener('resize', handleResize);
    handleResize(); 

    return () => window.removeEventListener('resize', handleResize);
  }, []); 

  const sidebarWidthClass = isSidebarOpen ? 'w-64' : 'w-24'; 
  const mainContentMarginClass = isSidebarOpen ? 'ml-8' : 'ml-8'; 

  return (
    <div className="flex min-h-screen bg-gray-100">
      {!isMobile && (
        <Sidebar 
          isSidebarOpen={isSidebarOpen} 
          onToggleSidebar={toggleSidebar} 
          onMouseEnterSidebar={handleMouseEnterSidebar} 
          onMouseLeaveSidebar={handleMouseLeaveSidebar} 
        />
      )}

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
          isSidebarOpen={isSidebarOpen} 
          isMobile={isMobile} 
        />

        {isSidebarOpen && isMobile && ( 
          <div 
            className="fixed inset-0 bg-black bg-opacity-50 z-20" 
            onClick={toggleSidebar}
          ></div>
        )}

        {isMobile && (
          <Sidebar 
            isSidebarOpen={isSidebarOpen} 
            onToggleSidebar={toggleSidebar} 
            onMouseEnterSidebar={() => {}} 
            onMouseLeaveSidebar={() => {}} 
            isMobileOverlay={true} 
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