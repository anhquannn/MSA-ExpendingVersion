package com.market.MSA.constants;

import lombok.Getter;

@Getter
public enum ApiMessage {
  // Authentication messages
  USER_REGISTERED("User registered successfully"),
  USER_LOGGED_IN("User logged in successfully"),
  USER_LOGGED_OUT("User logged out successfully"),
  TOKEN_REFRESHED("Token refreshed successfully"),
  PASSWORD_RESET_EMAIL_SENT("Password reset email sent successfully"),
  PASSWORD_RESET("Password reset successfully"),
  EMAIL_VERIFIED("Email verified successfully"),
  VERIFICATION_EMAIL_SENT("Verification email sent successfully"),
  GOOGLE_LOGIN_SUCCESSFUL("Google login successful"),

  // User messages
  USER_CREATED("User created successfully"),
  USER_UPDATED("User updated successfully"),
  USER_DELETED("User deleted successfully"),
  USER_RETRIEVED("User retrieved successfully"),
  ALL_USERS_RETRIEVED("All users retrieved successfully"),

  // Category messages
  CATEGORY_CREATED("Category created successfully"),
  CATEGORY_UPDATED("Category updated successfully"),
  CATEGORY_DELETED("Category deleted successfully"),
  CATEGORY_RETRIEVED("Category retrieved successfully"),
  ALL_CATEGORIES_RETRIEVED("All categories retrieved successfully"),

  // Product messages
  PRODUCT_CREATED("Product created successfully"),
  PRODUCT_UPDATED("Product updated successfully"),
  PRODUCT_DELETED("Product deleted successfully"),
  PRODUCT_RETRIEVED("Product retrieved successfully"),
  ALL_PRODUCTS_RETRIEVED("All products retrieved successfully"),

  // Transfer request messages
  TRANSFER_REQUEST_CREATED("Transfer request created successfully"),
  TRANSFER_REQUEST_UPDATED("Transfer request updated successfully"),

  TRANSFER_REQUEST_APPROVED("Transfer request approved"),
  TRANSFER_REQUEST_REJECTED("Transfer request rejected"),
  TRANSFER_REQUEST_DELETED("Transfer request deleted successfully"),
  TRANSFER_REQUEST_RETRIEVED("Transfer request retrieved successfully"),
  ALL_TRANSFER_REQUESTS_RETRIEVED("All Transfer requests retrieved successfully"),

  // Transfer request item messages
  TRANSFER_REQUEST_ITEM_CREATED("Transfer request item created successfully"),
  TRANSFER_REQUEST_ITEM_UPDATED("Transfer request item updated successfully"),
  TRANSFER_REQUEST_ITEM_DELETED("Transfer request item deleted successfully"),
  TRANSFER_REQUEST_ITEM_RETRIEVED("Transfer request item retrieved successfully"),
  ALL_TRANSFER_REQUEST_ITEMS_RETRIEVED("All Transfer request items retrieved successfully"),

  // Cart messages
  CART_CREATED("Cart created successfully"),
  CART_UPDATED("Cart updated successfully"),
  CART_DELETED("Cart deleted successfully"),
  CART_RETRIEVED("Cart retrieved successfully"),
  ALL_CARTS_RETRIEVED("All carts retrieved successfully"),

  // Cart Item messages
  CART_ITEM_CREATED("Cart item created successfully"),
  CART_ITEM_UPDATED("Cart item updated successfully"),
  CART_ITEM_DELETED("Cart item deleted successfully"),
  CART_ITEM_RETRIEVED("Cart item retrieved successfully"),
  ALL_CART_ITEMS_RETRIEVED("All cart items retrieved successfully"),
  CART_ITEMS_SELECTION_UPDATED("Cart items selection updated"),
  CART_CLEARED("Cart cleared"),
  CART_TOTAL_CALCULATED("Cart total calculated successfully"),

  ADD_CART_ITEM("Add cart item successfully"),

  // Branch messages
  BRANCH_CREATED("Branch created successfully"),
  BRANCH_UPDATED("Branch updated successfully"),
  BRANCH_DELETED("Branch deleted successfully"),
  BRANCH_RETRIEVED("Branch retrieved successfully"),
  ALL_BRANCHES_RETRIEVED("All branches retrieved successfully"),

  // Role messages
  ROLE_CREATED("Role created successfully"),
  ROLE_UPDATED("Role updated successfully"),
  ROLE_DELETED("Role deleted successfully"),
  ROLE_RETRIEVED("Role retrieved successfully"),
  ALL_ROLES_RETRIEVED("All roles retrieved successfully"),

  // Inventory messages
  INVENTORY_CREATED("Inventory created successfully"),
  INVENTORY_UPDATED("Inventory updated successfully"),
  INVENTORY_DELETED("Inventory deleted successfully"),
  INVENTORY_RETRIEVED("Inventory retrieved successfully"),
  ALL_INVENTORIES_RETRIEVED("All inventories retrieved successfully"),

  // Inventory Product messages
  INVENTORY_PRODUCT_CREATED("Inventory product created successfully"),
  INVENTORY_PRODUCT_UPDATED("Inventory product updated successfully"),
  INVENTORY_PRODUCT_DELETED("Inventory product deleted successfully"),
  INVENTORY_PRODUCT_RETRIEVED("Inventory product retrieved successfully"),
  INVENTORY_PRODUCT_STATISTICS_CREATED("Inventory product statistics created successfully"),
  INVENTORY_PRODUCT_TOTAL_STOCK_CREATED("Inventory product total stock created successfully"),
  ALL_INVENTORY_PRODUCTS_RETRIEVED("All inventory products retrieved successfully"),

  // Payment messages
  PAYMENT_CREATED("Payment created successfully"),
  PAYMENT_UPDATED("Payment updated successfully"),
  PAYMENT_DELETED("Payment deleted successfully"),
  PAYMENT_RETRIEVED("Payment retrieved successfully"),
  ALL_PAYMENTS_RETRIEVED("All payments retrieved successfully"),
  VNPAY_PAYMENT_URL_CREATED("VNPay payment url created successfully"),
  VNPAY_CALLBACK_HANDLED("VNPay callback handled successfully"),

  // Manufacturer messages
  MANUFACTURER_CREATED("Manufacturer created successfully"),
  MANUFACTURER_UPDATED("Manufacturer updated successfully"),
  MANUFACTURER_DELETED("Manufacturer deleted successfully"),
  MANUFACTURER_RETRIEVED("Manufacturer retrieved successfully"),
  ALL_MANUFACTURERS_RETRIEVED("All manufacturers retrieved successfully"),

  // Permission messages
  PERMISSION_CREATED("Permission created successfully"),
  PERMISSION_UPDATED("Permission updated successfully"),
  PERMISSION_DELETED("Permission deleted successfully"),
  PERMISSION_RETRIEVED("Permission retrieved successfully"),
  ALL_PERMISSIONS_RETRIEVED("All permissions retrieved successfully"),

  // Notification messages
  NOTIFICATION_CREATED("Notification created successfully"),
  NOTIFICATION_UPDATED("Notification updated successfully"),
  NOTIFICATION_DELETED("Notification deleted successfully"),
  NOTIFICATION_RETRIEVED("Notification retrieved successfully"),
  ALL_NOTIFICATIONS_RETRIEVED("All notifications retrieved successfully"),

  // Feedback messages
  FEEDBACK_CREATED("Feedback created successfully"),
  FEEDBACK_UPDATED("Feedback updated successfully"),
  FEEDBACK_DELETED("Feedback deleted successfully"),
  FEEDBACK_RETRIEVED("Feedback retrieved successfully"),
  ALL_FEEDBACKS_RETRIEVED("All feedbacks retrieved successfully"),

  // Order messages
  ORDER_CREATED("Order created successfully"),
  ORDER_UPDATED("Order updated successfully"),
  ORDER_DELETED("Order deleted successfully"),
  ORDER_RETRIEVED("Order retrieved successfully"),
  ALL_ORDERS_RETRIEVED("All orders retrieved successfully"),
  ORDER_CANCELLED("Order cancelled successfully"),
  ORDER_SUMMARY_RETRIEVED("Order summary retrieved successfully"),

  REVENUE_STATISTICS_RETRIEVED("Revenue statistics retrieved successfully"),

  // Order Detail messages
  ORDER_DETAIL_CREATED("Order detail created successfully"),
  ORDER_DETAIL_UPDATED("Order detail updated successfully"),
  ORDER_DETAIL_DELETED("Order detail deleted successfully"),
  ORDER_DETAIL_RETRIEVED("Order detail retrieved successfully"),
  ALL_ORDER_DETAILS_RETRIEVED("All order details retrieved successfully"),

  // Delivery Info messages
  DELIVERY_INFO_CREATED("Delivery info created successfully"),
  DELIVERY_INFO_UPDATED("Delivery info updated successfully"),
  DELIVERY_INFO_DELETED("Delivery info deleted successfully"),
  DELIVERY_INFO_RETRIEVED("Delivery info retrieved successfully"),
  ALL_DELIVERY_INFOS_RETRIEVED("All delivery infos retrieved successfully"),

  // Promo Code messages
  PROMO_CODE_CREATED("Promo code created successfully"),
  PROMO_CODE_UPDATED("Promo code updated successfully"),
  PROMO_CODE_DELETED("Promo code deleted successfully"),
  PROMO_CODE_RETRIEVED("Promo code retrieved successfully"),
  ALL_PROMO_CODES_RETRIEVED("All promo codes retrieved successfully"),

  // Campaign messages
  CAMPAIGN_CREATED("Campaign created successfully"),
  CAMPAIGN_UPDATED("Campaign updated successfully"),
  CAMPAIGN_DELETED("Campaign deleted successfully"),
  CAMPAIGN_RETRIEVED("Campaign retrieved successfully"),
  ALL_CAMPAIGNS_RETRIEVED("All campaigns retrieved successfully"),

  // Return Order messages
  RETURN_ORDER_CREATED("Return order created successfully"),
  RETURN_ORDER_UPDATED("Return order updated successfully"),
  RETURN_ORDER_DELETED("Return order deleted successfully"),
  RETURN_ORDER_RETRIEVED("Return order retrieved successfully"),
  ALL_RETURN_ORDERS_RETRIEVED("All return orders retrieved successfully"),

  // Trending Product messages
  TRENDING_PRODUCT_CREATED("Trending product created successfully"),
  TRENDING_PRODUCT_UPDATED("Trending product updated successfully"),
  TRENDING_PRODUCT_DELETED("Trending product deleted successfully"),
  TRENDING_PRODUCT_RETRIEVED("Trending product retrieved successfully"),
  ALL_TRENDING_PRODUCTS_RETRIEVED("All trending products retrieved successfully"),

  // User Behavior messages
  USER_BEHAVIOR_CREATED("User behavior created successfully"),
  USER_BEHAVIOR_UPDATED("User behavior updated successfully"),
  USER_BEHAVIOR_DELETED("User behavior deleted successfully"),
  USER_BEHAVIOR_RETRIEVED("User behavior retrieved successfully"),
  ALL_USER_BEHAVIORS_RETRIEVED("All user behaviors retrieved successfully"),

  // Shipment messages
  SHIPMENT_CREATED("Shipment created successfully"),
  SHIPMENT_RETRIEVED("Shipment retrieved successfully"),
  ALL_SHIPMENTS_RETRIEVED("All shipments retrieved successfully"),
  CITIES_RETRIEVED("Cities retrieved successfully"),
  DISTRICTS_RETRIEVED("District retrieved successfully"),
  WARDS_RETRIEVED("Ward retrieved successfully"),
  RATES_CREATED("Rates created successfully"),

  // Reward point
  REWARD_POINT_CREATED("Reward point created successfully"),
  REWARD_POINT_UPDATED("Reward point updated successfully"),
  REWARD_POINT_DELETED("Reward point deleted successfully"),
  REWARD_POINT_RETRIEVED("Reward point retrieved successfully"),
  ALL_REWARD_POINTS_RETRIEVED("All reward points retrieved successfully"),
  REWARD_POINT_TRANSACTION_CREATED("Reward point transaction created successfully"),
  REWARD_POINT_TRANSACTION_UPDATED("Reward point transaction updated successfully"),
  REWARD_POINT_TRANSACTION_DELETED("Reward point transaction deleted successfully"),
  REWARD_POINT_TRANSACTION_RETRIEVED("Reward point transaction retrieved successfully"),
  ALL_REWARD_POINT_TRANSACTIONS_RETRIEVED("All reward point transactions retrieved successfully"),
  POINTS_EARNED("Points earned successfully"),
  POINTS_REDEEMED("Points redeemed successfully"),
  POINTS_ADJUSTED("Points adjusted successfully"),
  POINTS_BALANCE_RETRIEVED("Points balance retrieved successfully"),
  ;

  private final String message;

  ApiMessage(String message) {
    this.message = message;
  }
}
