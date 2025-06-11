// src/constants/apiConstants.ts

export const API_ENDPOINTS = {
  // USER
  REGISTER: 'user/register',
  LOGIN: 'user/login',
  VERIFY_OTP: 'user/verify-otp',
  RESET_PASS: 'user/reset-password',
  UPDATE_USER: (userId: number | string) => `user/${userId}`,
  GET_USER_BY_EMAIL: (email: string) => `user/email/${email}`,

  // PRODUCT
  GET_ALL_PRODUCTS: 'product',
  SEARCH_PRODUCTS: 'product/search',
  GET_PRODUCT_BY_ID: (productId: number | string) => `product/${productId}`,
  GET_ALL_PRODUCTS_IN_BRANCH: (branchId: number | string) => `product/branch/${branchId}`,
  
  // CATEGORY
  GET_ALL_CATEGORIES: 'category',
  
  // PROMOCODE
  GET_ALL_PROMO_CODES: 'promo-code',
  GET_PROMO_CODE_BY_ID: (codeId: number | string) => `promo-code/${codeId}`,
  
  // CART
  GET_OR_CREATE_CART: (userId: number | string) => `cart/user/${userId}`,
  ADD_TO_CART: 'cart-item/add',
  GET_CART_ITEMS: (cartId: number | string) => `cart-item/by-cart/${cartId}`,
  CLEAR_CART: (cartId: number | string) => `cart-item/clear/${cartId}`,
  
  // ORDER
  CREATE_ORDER: 'order',
  PREVIEW_ORDER: 'order/preview',
  GET_ORDER_BY_ID: (orderId: number | string) => `order/${orderId}`,
  GET_ORDERS_BY_USER_STATUS: (userId: number | string, status: string) => `order/user/${userId}/status/${status}`,
  
  // GOSHIP
  GET_CITIES: 'shipment/cities',
  GET_DISTRICTS: (cityId: string) => `shipment/districts/${cityId}`,
  GET_WARDS: (districtId: string) => `shipment/wards/${districtId}`,
  
  // FEEDBACK
  GET_FEEDBACKS_BY_PRODUCT: (productId: number | string) => `feedback/product/${productId}`,
  CREATE_FEEDBACK: 'feedback',
};