// src/App.tsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// Import các hằng số route (nếu bạn đã định nghĩa)
import { routeConstants } from './constants/routeConstants';

// --- Auth Pages ---
import LoginPage from './pages/auth/LoginPage';
import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';
import SignupPage from './pages/auth/SignupPage';

// --- Dashboard Components ---
import DashboardLayout from './components/common/Dashboard/Layout';

import DashboardHome from './pages/Dashboard/DashboardHome';
import OrdersPage from './pages/Dashboard/OrdersPage';
import UsersPage from './pages/Dashboard/UsersPage';
import RevenueStatsPage from './pages/Dashboard/RevenueStatsPage';
import InventoryPage from './pages/Dashboard/InventoryPage';
import ProductsPage from './pages/Dashboard/ProductsPage';
import UserDetailPage from './pages/Dashboard/UserDetailPage';
import ProductDetailPage from './pages/Dashboard/ProductDetailPage';

function App() {
  // Dữ liệu giả cho người dùng đã đăng nhập
  // Trong ứng dụng thực tế, bạn sẽ lấy thông tin này từ trạng thái xác thực (Context/Redux)
  const mockLoggedInUser = {
    name: 'Manager Chi Nhánh',
    avatar: '../../assets/images/default-avatar.png', // Sử dụng ảnh mặc định
  };

  // Hàm xử lý đăng xuất
  const handleLogout = () => {
    localStorage.removeItem('authToken'); // Xóa token xác thực
    // Có thể thêm các logic dọn dẹp khác ở đây (ví dụ: xóa state người dùng)
    window.location.href = '/login'; // Chuyển hướng cứng để đảm bảo reset state hoàn toàn
  };

  return (
    <Router>
      <Routes>
        {/* --- Authentication Routes --- */}
        <Route path={routeConstants.login} element={<LoginPage />} />
        <Route path={routeConstants.forgotPassword} element={<ForgotPasswordPage />} />
        <Route path={routeConstants.signup} element={<SignupPage />} />

        {/* --- Dashboard Routes --- */}
        {/* Sử dụng DashboardLayout làm bố cục chính cho tất cả các trang dashboard */}
        <Route
          path={routeConstants.dashboard} // Base path cho dashboard
          element={
            // Logic kiểm tra xác thực đơn giản: nếu có authToken trong localStorage
            // localStorage.getItem('authToken') ? (
            <DashboardLayout
              userName={mockLoggedInUser.name}
              userAvatar={mockLoggedInUser.avatar}
              onLogout={handleLogout}
            />
            // ) : (
            // Nếu không có token, chuyển hướng về trang đăng nhập
            // <Navigate to={routeConstants.login} replace /> 
            // )
          }

        >
          {/* Các trang con (nested routes) của Dashboard */}
          {/* Khi người dùng truy cập /dashboard, sẽ render DashboardHome */}
          <Route index element={<DashboardHome />} />
          <Route path={routeConstants.orders} element={<OrdersPage />} />
          <Route path={routeConstants.users} element={<UsersPage />} />
          <Route path={routeConstants.revenue} element={<RevenueStatsPage />} />
          <Route path={routeConstants.inventory} element={<InventoryPage />} />
          <Route path={routeConstants.products} element={<ProductsPage />} />
          <Route path={routeConstants.userDetails} element={<UserDetailPage />} />
           <Route path={routeConstants.productDetails} element={<ProductDetailPage />} /> 
          {/* Thêm các route cho các trang dashboard khác nếu có */}
          {/* <Route path={routeConstants.branches} element={<BranchManagementPage />} /> */}
          {/* <Route path={routeConstants.settings} element={<SettingsPage />} /> */}
        </Route>

        {/* --- Default Redirect --- */}
        {/* Chuyển hướng người dùng về trang đăng nhập nếu truy cập một route không tồn tại */}
        <Route path="*" element={<Navigate to={routeConstants.login} replace />} />
      </Routes>
    </Router>
  );
}

export default App;