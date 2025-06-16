// src/App.tsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// Import các hằng số route
import { routeConstants } from './constants/routeConstants';

// Import LocalStorageManager
import { LocalStorageManager } from './utils/app_storage';

// Auth Pages
import LoginPage from './pages/auth/LoginPage';
import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';
import SignupPage from './pages/auth/SignupPage';

// Dashboard Components
import DashboardLayout from './components/common/Dashboard/Layout'; // Đảm bảo đúng đường dẫn
import DashboardHome from './pages/Dashboard/DashboardHome';
import OrdersPage from './pages/Dashboard/OrdersPage';
import UsersPage from './pages/Dashboard/UsersPage';
import RevenueStatsPage from './pages/Dashboard/RevenueStatsPage';
import InventoryPage from './pages/Dashboard/InventoryPage';
import ProductsPage from './pages/Dashboard/ProductsPage';
import UserDetailPage from './pages/Dashboard/UserDetailPage';
import ProductDetailPage from './pages/Dashboard/ProductDetailPage';
import BranchManagementPage from './pages/Dashboard/BranchManagementPage';
import BranchDetailPage from './pages/Dashboard/BranchDetailPage';
import NotificationScreen from './components/NotificationScreen'; // Import NotificationScreen
import SettingsPage from './pages/Dashboard/SettingsPage';

LocalStorageManager.init();

function App() {
  const currentUser = LocalStorageManager.getUser();
  const isLoggedIn = !!LocalStorageManager.getAccessToken();

  const mockLoggedInUser = {
    name: currentUser ? currentUser.Fullname : 'Guest',
    avatar: '../../assets/images/default-avatar.png',
  };

  const handleLogout = () => {
    LocalStorageManager.clearAllData();
    window.location.href = routeConstants.login;
  };

  return (
    <Router>
      <Routes>
        <Route path={routeConstants.login} element={<LoginPage />} />
        <Route path={routeConstants.forgotPassword} element={<ForgotPasswordPage />} />
        <Route path={routeConstants.signup} element={<SignupPage />} />

        {/* Protected Dashboard Routes */}
        {/*
          Thay vì kiểm tra isLoggedIn ở đây và render DashboardLayout hoặc Navigate,
          chúng ta sẽ luôn render DashboardLayout cho đường dẫn /dashboard,
          và để DashboardLayout (hoặc một component wrapper) xử lý việc bảo vệ.
          Hoặc, cách đơn giản hơn là kiểm tra trực tiếp ở đây:
        */}
        <Route
          path={routeConstants.dashboard}
          element={
            // !isLoggedIn ? ( // <-- Đã đổi thành 'isLoggedIn ?'
              <DashboardLayout
                userName={mockLoggedInUser.name}
                userAvatar={mockLoggedInUser.avatar}
                onLogout={handleLogout}
                
              />
            // ) : (
              // Nếu KHÔNG có token, chuyển hướng về trang đăng nhập
              // <Navigate to={routeConstants.login} replace />
            // )
          }
        >
          {/* Các trang con (nested routes) của Dashboard */}
          <Route index element={<DashboardHome />} />
          <Route path={routeConstants.orders} element={<OrdersPage />} />
          <Route path={routeConstants.users} element={<UsersPage />} />
          <Route path={routeConstants.revenue} element={<RevenueStatsPage />} />
          <Route path={routeConstants.inventory} element={<InventoryPage />} />
          <Route path={routeConstants.products} element={<ProductsPage />} />
          <Route path={routeConstants.userDetails} element={<UserDetailPage />} />
          <Route path={routeConstants.productDetails} element={<ProductDetailPage />} />
          <Route path={routeConstants.branches} element={<BranchManagementPage />} />
          <Route path={routeConstants.branchDetails} element={<BranchDetailPage />} />
          {/* Thêm route cho màn hình thông báo là một route con của dashboard */}
          <Route path={routeConstants.notification} element={<NotificationScreen />} />
          <Route path={routeConstants.settings} element={<SettingsPage />} />
          {/* Thêm các route cho các trang dashboard khác nếu có */}
        </Route>

        {/* --- Default Redirect --- */}
        {/* Chuyển hướng người dùng về trang đăng nhập nếu truy cập một route không tồn tại */}
        <Route path="*" element={<Navigate to={routeConstants.login} replace />} />
      </Routes>
    </Router>
  );
}

export default App;