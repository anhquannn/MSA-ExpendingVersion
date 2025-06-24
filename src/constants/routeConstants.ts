// src/constants/routeConstants.ts
export const routeConstants = {
  login: '/login',
  signup: '/signup',
  forgotPassword: '/forgot-password',
  dashboard: '/dashboard',
  orders: 'orders', // Lưu ý: Đây là relative path cho nested route
  users: 'users',
  revenue: 'revenue',
  inventory: 'inventory',
  products: 'products', 
  userDetails: 'users/:userId',
  branches: 'branches',
  settings: 'settings',
  productDetails: 'products/:productId',
  branchDetails: 'branches/:branchId',
  notification: 'notification',
  addProduct:'products/add',
  branchbranchAdd: '/dashboard/branches/add',
  branchUpdate:'/dashboard/branches/edit/:branchId',
  category:'/dashboard/categories',
  supply:'/dashboard/suppliers',
  inventoryProduct:'/dashboard/inventories/:inventoryId'
};