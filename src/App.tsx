// // src/App.tsx
// import React from 'react';
// import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// // Import các hằng số route
// import { routeConstants } from './constants/routeConstants';

// // Import LocalStorageManager
// import { LocalStorageManager } from './utils/app_storage';

// // Auth Pages
// import LoginPage from './pages/auth/LoginPage';
// import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';
// import SignupPage from './pages/auth/SignupPage';

// // Dashboard Components
// import DashboardLayout from './components/common/Dashboard/Layout'; // Đảm bảo đúng đường dẫn
// import DashboardHome from './pages/Dashboard/DashboardHome';
// import OrdersPage from './pages/Dashboard/OrdersPage';
// import UsersPage from './pages/Dashboard/UsersPage';
// import RevenueStatsPage from './pages/Dashboard/RevenueStatsPage';
// import ProductsPage from './pages/Dashboard/ProductsPage';
// import UserDetailPage from './pages/Dashboard/UserDetailPage';
// import { InventoryListPage } from './pages/Dashboard/InventoryPage';
// import ProductDetailPage from './pages/Dashboard/ProductDetailPage';
// import BranchManagementPage from './pages/Dashboard/BranchManagementPage';
// import BranchDetailPage from './pages/Dashboard/BranchDetailPage';
// import NotificationScreen from './components/NotificationScreen'; // Import NotificationScreen
// import SettingsPage from './pages/Dashboard/SettingsPage';
// import ProductAddPage from './pages/Dashboard/ProductUpsertPage';
// import BranchUpsertPage from './pages/Dashboard/BranchUpsertPage';
// import ProductUpsertPage from './pages/Dashboard/ProductUpsertPage';
// import CategoryManagementPage from './pages/Dashboard/CategoryManagementPage';
// import SupplierManagementPage from './pages/Dashboard/SupplierManagementPage';
// import { InventoryProductListPage } from './pages/Dashboard/InventoryProductListPage';

// LocalStorageManager.init();

// function App() {
//   const currentUser = LocalStorageManager.getUser();
//   const isLoggedIn = !!LocalStorageManager.getAccessToken();

//   const mockLoggedInUser = {
//     name: currentUser ? currentUser.Fullname : 'Guest',
//     avatar: '../../assets/images/default-avatar.png',
//   };

//   const handleLogout = () => {
//     LocalStorageManager.clearAllData();
//     window.location.href = routeConstants.login;
//   };

//   return (
//     <Router>
//       <Routes>
//         <Route path={routeConstants.login} element={<LoginPage />} />
//         <Route path={routeConstants.forgotPassword} element={<ForgotPasswordPage />} />
//         <Route path={routeConstants.signup} element={<SignupPage />} />
//         <Route
//           path={routeConstants.dashboard}
//           element={
//             // !isLoggedIn ? ( // <-- Đã đổi thành 'isLoggedIn ?'
//               <DashboardLayout
//                 userName={mockLoggedInUser.name}
//                 userAvatar={mockLoggedInUser.avatar}
//                 onLogout={handleLogout}
                
//               />
//             // ) : (
//               // Nếu KHÔNG có token, chuyển hướng về trang đăng nhập
//               // <Navigate to={routeConstants.login} replace />
//             // )
//           }
//         >
//           {/* Các trang con (nested routes) của Dashboard */}
//           <Route index element={<DashboardHome />} />
//           <Route path={routeConstants.orders} element={<OrdersPage />} />
//           <Route path={routeConstants.users} element={<UsersPage />} />
//           <Route path={routeConstants.revenue} element={<RevenueStatsPage />} />
//           <Route path={routeConstants.inventory} element={<InventoryListPage />} />
//           <Route path={routeConstants.products} element={<ProductsPage />} />
//           <Route path={routeConstants.userDetails} element={<UserDetailPage />} />
//           <Route path={routeConstants.productDetails} element={<ProductDetailPage />} />
//           <Route path={routeConstants.branches} element={<BranchManagementPage />} />
//           <Route path={routeConstants.branchDetails} element={<BranchDetailPage />} />
//           {/* Thêm route cho màn hình thông báo là một route con của dashboard */}
//           <Route path={routeConstants.notification} element={<NotificationScreen />} />
//           <Route path={routeConstants.settings} element={<SettingsPage />} />
//           <Route path={routeConstants.addProduct} element={<ProductAddPage />} />
//           <Route path={routeConstants.branchbranchAdd} element={<BranchUpsertPage  />} />
//           <Route path={routeConstants.branchUpdate} element={<BranchUpsertPage  />} />

//           <Route path="products/edit/:productId" element={<ProductUpsertPage />} />
//           <Route path={routeConstants.category} element={<CategoryManagementPage />} />
//           <Route path={routeConstants.supply} element={<SupplierManagementPage />} />
// {/* <Route path="inventories/:inventoryId" element={<InventoryManagementSingleFile branchId={0} />} /> */}
//                     <Route path={routeConstants.inventoryProduct} element={<InventoryProductListPage branchId={0} />} />

//           {/* Thêm các route cho các trang dashboard khác nếu có */}
//         </Route>

//         {/* --- Default Redirect
//           - không thể xóa kho hàng
//           - không thể sửa kho hàng
//           - danh sách sản phẩm trả về có thêm danh sách hình ảnh
//           - sửa lại hàm lấy danh sách manager 
//           - Cần thêm người dùng role quản lí không
//           - trả về avt người dùng

//           -- viết thêm hàm cập nhật thông tin người dùng (manager admin) -> admin sử dụng
//           - lỗi sửa đơn hàng
//           - lỗi lấy đơn hàng theo id
//           - danh sách đơn hàng
//           - họp về thông báo (các loại thông báo, xử lí thông báo,...)

//           - đơn hàng - thông báo - dashboard - mã giảm giá - phân quyền - loại sản phẩm - nhà sản xuất 

//         --- */}
//         {/* Chuyển hướng người dùng về trang đăng nhập nếu truy cập một route không tồn tại */}
//          <Route index element={<DashboardHome />} />
//       </Routes>
//     </Router>
//   );
// }

// export default App;

// // src/App.tsx

// // import React from 'react';
// // import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// // // Import các hằng số route
// // import { routeConstants } from './constants/routeConstants';

// // // Import LocalStorageManager
// // import { LocalStorageManager } from './utils/app_storage';

// // // --- Auth Pages ---
// // import LoginPage from './pages/auth/LoginPage';
// // import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';
// // import SignupPage from './pages/auth/SignupPage';

// // // --- Dashboard Components ---
// // import DashboardLayout from './components/common/Dashboard/Layout';
// // import DashboardHome from './pages/Dashboard/DashboardHome';
// // import OrdersPage from './pages/Dashboard/OrdersPage';
// // import UsersPage from './pages/Dashboard/UsersPage';
// // import RevenueStatsPage from './pages/Dashboard/RevenueStatsPage';
// // import InventoryPage from './pages/Dashboard/InventoryPage'; // Trang danh sách kho
// // import ProductsPage from './pages/Dashboard/ProductsPage';
// // import UserDetailPage from './pages/Dashboard/UserDetailPage';
// // import ProductDetailPage from './pages/Dashboard/ProductDetailPage';
// // import BranchManagementPage from './pages/Dashboard/BranchManagementPage';
// // import BranchDetailPage from './pages/Dashboard/BranchDetailPage';
// // import NotificationScreen from './components/NotificationScreen';
// // import SettingsPage from './pages/Dashboard/SettingsPage';
// // import ProductAddPage from './pages/Dashboard/ProductUpsertPage';
// // import BranchUpsertPage from './pages/Dashboard/BranchUpsertPage';
// // import ProductUpsertPage from './pages/Dashboard/ProductUpsertPage';
// // import CategoryManagementPage from './pages/Dashboard/CategoryManagementPage';
// // import SupplierManagementPage from './pages/Dashboard/SupplierManagementPage';
// // import { InventoryManagementSingleFile } from './pages/Dashboard/InventoryManagementPage';
// // import { InventoryProductListPage } from './pages/Dashboard/InventoryProductListPage';

// // LocalStorageManager.init();

// // function App() {
// //   const currentUser = LocalStorageManager.getUser();
// //   const isLoggedIn = !!LocalStorageManager.getAccessToken();

// //   const mockLoggedInUser = {
// //     name: currentUser ? currentUser.Fullname : 'Guest',
// //     avatar: '../../assets/images/default-avatar.png',
// //   };

// //   const handleLogout = () => {
// //     LocalStorageManager.clearAllData();
// //     window.location.href = routeConstants.login;
// //   };

// //   return (
// //     <Router>
// //       <Routes>
// //         <Route path={routeConstants.login} element={<LoginPage />} />
// //         <Route path={routeConstants.forgotPassword} element={<ForgotPasswordPage />} />
// //         <Route path={routeConstants.signup} element={<SignupPage />} />
        
// //         {/* Layout chung cho Dashboard */}
// //         <Route
// //           path={routeConstants.dashboard}
// //           element={
// //              isLoggedIn ? (
// //               <DashboardLayout
// //                 userName={mockLoggedInUser.name}
// //                 userAvatar={mockLoggedInUser.avatar}
// //                 onLogout={handleLogout}
// //               />
// //             ) : (
// //               // Nếu KHÔNG có token, chuyển hướng về trang đăng nhập
// //               <Navigate to={routeConstants.login} replace />
// //             )
// //           }
// //         >
// //           {/* Các trang con (nested routes) của Dashboard */}
// //           <Route index element={<DashboardHome />} />
// //           <Route path={routeConstants.orders} element={<OrdersPage />} />
// //           <Route path={routeConstants.users} element={<UsersPage />} />
// //           <Route path={routeConstants.revenue} element={<RevenueStatsPage />} />
// //           <Route path={routeConstants.products} element={<ProductsPage />} />
// //           <Route path={routeConstants.userDetails} element={<UserDetailPage />} />
// //           <Route path={routeConstants.productDetails} element={<ProductDetailPage />} />
// //           <Route path={routeConstants.branches} element={<BranchManagementPage />} />
// //           <Route path={routeConstants.branchDetails} element={<BranchDetailPage />} />
// //           <Route path={routeConstants.notification} element={<NotificationScreen />} />
// //           <Route path={routeConstants.settings} element={<SettingsPage />} />
// //           <Route path={routeConstants.addProduct} element={<ProductAddPage />} />
// //           <Route path={routeConstants.branchbranchAdd} element={<BranchUpsertPage />} />
// //           <Route path={routeConstants.branchUpdate} element={<BranchUpsertPage />} />
// //           <Route path="products/edit/:productId" element={<ProductUpsertPage />} />
// //           <Route path={routeConstants.category} element={<CategoryManagementPage />} />
// //           <Route path={routeConstants.supply} element={<SupplierManagementPage />} />
          
// //           {/* --- CẶP ROUTE CHO QUẢN LÝ KHO --- */}
// //           {/* 1. Route cho trang danh sách các kho hàng */}
// //           <Route path={routeConstants.inventory} element={<InventoryPage />} />

// //           {/* 2. Route cho trang chi tiết sản phẩm trong một kho cụ thể */}
// //           {/* <Route path="inventories/:inventoryId" element={<InventoryManagementSingleFile />} /> */}
// //                     <Route path="inventories/:inventoryId" element={<InventoryProductListPage />} />


// //         </Route>

// //         {/* --- Default Redirect --- */}
// //         {/* Chuyển hướng về trang dashboard nếu người dùng vào trang gốc */}
// //         <Route path="/" element={<Navigate to={routeConstants.dashboard} />} />
        
// //         {/* Optional: Một route bắt các đường dẫn không khớp để hiển thị trang 404 Not Found */}
// //         {/* <Route path="*" element={<NotFoundPage />} /> */}
// //       </Routes>
// //     </Router>
// //   );
// // }

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
import UserDetailPage from './pages/Dashboard/UserDetailPage';
import ProductDetailPage from './pages/Dashboard/ProductDetailPage';
import BranchManagementPage from './pages/Dashboard/BranchManagementPage';
import BranchDetailPage from './pages/Dashboard/BranchDetailPage';
import NotificationScreen from './components/NotificationScreen';
import SettingsPage from './pages/Dashboard/SettingsPage';
import ProductUpsertPage from './pages/Dashboard/ProductUpsertPage';
import BranchUpsertPage from './pages/Dashboard/BranchUpsertPage';
import CategoryManagementPage from './pages/Dashboard/CategoryManagementPage';
import SupplierManagementPage from './pages/Dashboard/SupplierManagementPage';

// ====================================================================
// SỬA LỖI 1: Import đúng component từ đúng file
// ====================================================================C:\Users\minhq\OneDrive\Desktop\msa-webapp\src\pages\Dashboard\InventoryPage.tsx
import { InventoryListPage } from './pages/Dashboard/InventoryPage';
import { InventoryProductListPage } from './pages/Dashboard/InventoryProductListPage';


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
          <Route index element={<DashboardHome />} />
          <Route path="orders" element={<OrdersPage />} />
          <Route path="users" element={<UsersPage />} />
          <Route path="products" element={<ProductsPage />} />
          <Route path="products/add" element={<ProductUpsertPage />} />
          <Route path="products/edit/:productId" element={<ProductUpsertPage />} />
          <Route path="users/:userId" element={<UserDetailPage />} />
          <Route path="branches" element={<BranchManagementPage />} />
          <Route path="branches/add" element={<BranchUpsertPage />} />
          <Route path="branches/edit/:branchId" element={<BranchUpsertPage />} />
          <Route path="categories" element={<CategoryManagementPage />} />
          <Route path="suppliers" element={<SupplierManagementPage />} />
          <Route path="settings" element={<SettingsPage />} />
          <Route path="notifications" element={<NotificationScreen />} />
          
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