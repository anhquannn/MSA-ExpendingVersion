// // export default App;
// src/App.tsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// Import các hằng số route
import { routeConstants } from './constants/routeConstants';

// Import LocalStorageManager
import { LocalStorageManager } from './utils/app_storage';

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
import ProductsPage from './pages/Dashboard/ProductsPage';
import CombosPage from './pages/Dashboard/CombosPage';
import UserDetailPage from './pages/Dashboard/UserDetailPage';
import ProductDetailPage from './pages/Dashboard/ProductDetailPage';
import BranchManagementPage from './pages/Dashboard/BranchManagementPage';
import BranchDetailPage from './pages/Dashboard/BranchDetailPage';
import NotificationScreen from './components/NotificationScreen';
import SettingsPage from './pages/Dashboard/SettingsPage';
import ProductUpsertPage from './pages/Dashboard/ProductUpsertPage';
import ComboAddPage from './pages/Dashboard/ComboAddPage';
import BranchUpsertPage from './pages/Dashboard/BranchUpsertPage';
import CategoryManagementPage from './pages/Dashboard/CategoryManagementPage';
import SupplierManagementPage from './pages/Dashboard/SupplierManagementPage';
import CampaignManagementPage from './pages/Dashboard/CampaignManagementPage';
import PromoCodeManagementPage from './pages/Dashboard/PromoCodeManagementPage';
import TransferRequestListPage from './pages/Dashboard/TransferRequestListPage';
import InventoryCheckListPage from './pages/Dashboard/InventoryCheckListPage';

// ====================================================================
// SỬA LỖI 1: Import đúng component từ đúng file
// ====================================================================C:\Users\minhq\OneDrive\Desktop\msa-webapp\src\pages\Dashboard\InventoryPage.tsx
import { InventoryListPage } from './pages/Dashboard/InventoryPage';
import { InventoryProductListPage } from './pages/Dashboard/InventoryProductListPage';
import DashboardNewPage from './pages/Dashboard/DashboardHome.new';
import TransferRequestDetailPage from './pages/Dashboard/TransferRequestDetailPage';
import { CheckedHistoryPage } from './pages/Dashboard/CheckedHistoryPage';

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
        {/* Auth Routes */}
        <Route path={routeConstants.login} element={<LoginPage />} />
        <Route path={routeConstants.forgotPassword} element={<ForgotPasswordPage />} />
        <Route path={routeConstants.signup} element={<SignupPage />} />

        {/* Dashboard Layout */}
        <Route
          path="/dashboard" // Sử dụng path tĩnh cho layout cha
          element={
            <DashboardLayout
              userName={mockLoggedInUser.name}
              userAvatar={mockLoggedInUser.avatar}
              onLogout={handleLogout}
            />
          }
        >
          {/* Dashboard Nested Routes */}
          <Route index element={<DashboardNewPage branchId={1} />} />
          <Route path="orders" element={<OrdersPage />} />
          <Route path="users" element={<UsersPage />} />
          <Route path="products" element={<ProductsPage />} />
          <Route path="combos" element={<CombosPage />} />
          <Route path="products/add" element={<ProductUpsertPage />} />
          <Route path="combos/add" element={<ComboAddPage />} />
          <Route path="products/edit/:productId" element={<ProductUpsertPage />} />
          <Route path="users/:userId" element={<UserDetailPage />} />
          <Route path="branches" element={<BranchManagementPage />} />
          <Route path="branches/add" element={<BranchUpsertPage />} />
          <Route path="branches/edit/:branchId" element={<BranchUpsertPage />} />
          <Route path="categories" element={<CategoryManagementPage />} />
          <Route path="suppliers" element={<SupplierManagementPage />} />
          <Route path="settings" element={<SettingsPage />} />
          <Route path="notifications" element={<NotificationScreen />} />
          <Route path="campaigns" element={<CampaignManagementPage />} />
          <Route path="campaigns/:campaignId/promocodes" element={<PromoCodeManagementPage />} />
          <Route path="transfer-requests" element={<TransferRequestListPage />} />
          {/* Danh sách lịch kiểm kho */}
          <Route path="inventory-checks" element={<InventoryCheckListPage />} />
          <Route path="transfer-requests/:id" element={<TransferRequestDetailPage />} />
          <Route path="inventories/:id/history" element={<CheckedHistoryPage />} />

          {/* ==================================================================== */}
          {/* SỬA LỖI 2: Cấu hình Route đúng cho List và Detail */}
          {/* ==================================================================== */}

          {/* Route để hiển thị DANH SÁCH CÁC KHO HÀNG */}
          {/* URL sẽ là: /dashboard/inventories */}
          <Route 
            path="/dashboard/inventory" 
            element={<InventoryListPage />} 
          />

          {/* Route để hiển thị SẢN PHẨM CỦA MỘT KHO CỤ THỂ */}
          {/* URL sẽ là: /dashboard/inventories/1, /dashboard/inventories/2, ... */}
          {/* Component sẽ dùng `useParams` để lấy `inventoryId` */}
         <Route 
            path="inventories/:inventoryId" 
            element={<InventoryProductListPage />} 
          />

        </Route>

        {/* Redirect trang gốc về dashboard */}
        <Route path="/" element={<Navigate to="/dashboard" />} />
        
        {/* Có thể thêm Route cho trang 404 Not Found ở đây */}
        {/* <Route path="*" element={<NotFoundPage />} /> */}
      </Routes>
    </Router>
  );
}

export default App;