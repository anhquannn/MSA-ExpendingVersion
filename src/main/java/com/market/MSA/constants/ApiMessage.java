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
  SUPPLIER_CREATED("Supplier created successfully"),
  SUPPLIER_UPDATED("Supplier updated successfully"),
  SUPPLIER_DELETED("Supplier deleted successfully"),
  SUPPLIER_RETRIEVED("Supplier retrieved successfully"),
  ALL_SUPPLIERS_RETRIEVED("All suppliers retrieved successfully"),

  // Checked History messages
  CHECKED_HISTORY_CREATED("Checked history created successfully"),
  CHECKED_HISTORY_UPDATED("Checked history updated successfully"),
  CHECKED_HISTORY_DELETED("Checked history deleted successfully"),
  CHECKED_HISTORY_RETRIEVED("Checked history retrieved successfully"),
  ALL_CHECKED_HISTORIES_RETRIEVED("All checked histories retrieved successfully"),

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
  PRODUCT_SALES_STATISTICS_RETRIEVED("Product sales statistics retrieved successfully"),

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

  CAMPAIGN_TARGET_CREATED("Campaign target created successfully"),
  CAMPAIGN_TARGET_UPDATED("Campaign target updated successfully"),
  CAMPAIGN_TARGET_DELETED("Campaign target deleted successfully"),
  CAMPAIGN_TARGET_RETRIEVED("Campaign target retrieved successfully"),
  ALL_CAMPAIGN_TARGETS_RETRIEVED("All campaign targets retrieved successfully"),

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

  // Product Attribute Value messages
  PRODUCT_ATTRIBUTE_VALUE_CREATED("Product attribute value created successfully"),
  PRODUCT_ATTRIBUTE_VALUE_UPDATED("Product attribute value updated successfully"),
  PRODUCT_ATTRIBUTE_VALUE_DELETED("Product attribute value deleted successfully"),
  PRODUCT_ATTRIBUTE_VALUE_RETRIEVED("Product attribute value retrieved successfully"),
  ALL_PRODUCT_ATTRIBUTE_VALUES_RETRIEVED("All product attribute values retrieved successfully"),

  // Product Image messages
  PRODUCT_IMAGE_CREATED("Product image created successfully"),
  PRODUCT_IMAGE_UPDATED("Product image updated successfully"),
  PRODUCT_IMAGE_DELETED("Product image deleted successfully"),
  PRODUCT_IMAGE_RETRIEVED("Product image retrieved successfully"),
  ALL_PRODUCT_IMAGES_RETRIEVED("All product images retrieved successfully"),

  // Product Attribute messages
  PRODUCT_ATTRIBUTE_CREATED("Product attribute created successfully"),
  PRODUCT_ATTRIBUTE_UPDATED("Product attribute updated successfully"),
  PRODUCT_ATTRIBUTE_DELETED("Product attribute deleted successfully"),
  PRODUCT_ATTRIBUTE_RETRIEVED("Product attribute retrieved successfully"),
  ALL_PRODUCT_ATTRIBUTES_RETRIEVED("All product attributes retrieved successfully"),

  // Promo Code Usage messages
  PROMO_CODE_USAGE_CREATED("Promo code usage created successfully"),
  PROMO_CODE_USAGE_UPDATED("Promo code usage updated successfully"),
  PROMO_CODE_USAGE_DELETED("Promo code usage deleted successfully"),
  PROMO_CODE_USAGE_RETRIEVED("Promo code usage retrieved successfully"),
  ALL_PROMO_CODE_USAGES_RETRIEVED("All promo code usages retrieved successfully"),

  // Address messages
  ADDRESS_CREATED("Address created successfully"),
  ADDRESS_UPDATED("Address updated successfully"),
  ADDRESS_DELETED("Address deleted successfully"),
  ADDRESS_RETRIEVED("Address retrieved successfully"),
  ALL_ADDRESSES_RETRIEVED("All addresses retrieved successfully"),

  // Inbound Transfer messages
  INBOUND_CREATED("Inbound transfer created successfully"),
  INBOUND_UPDATED("Inbound transfer updated successfully"),
  INBOUND_DELETED("Inbound transfer deleted successfully"),
  INBOUND_RETRIEVED("Inbound transfer retrieved successfully"),
  ALL_INBOUNDS_RETRIEVED("All inbound transfers retrieved successfully"),

  // Outbound Transfer messages
  OUTBOUND_CREATED("Outbound transfer created successfully"),
  OUTBOUND_UPDATED("Outbound transfer updated successfully"),
  OUTBOUND_DELETED("Outbound transfer deleted successfully"),
  OUTBOUND_RETRIEVED("Outbound transfer retrieved successfully"),
  ALL_OUTBOUNDS_RETRIEVED("All outbound transfers retrieved successfully"),

  // Inventory Check Request messages
  INVENTORY_CHECK_REQUEST_CREATED("Inventory check request created successfully"),
  INVENTORY_CHECK_REQUEST_STATUS_UPDATED("Inventory check request status updated successfully"),
  INVENTORY_CHECK_REQUEST_UPDATED("Inventory check request updated successfully"),
  INVENTORY_CHECK_REQUEST_DELETED("Inventory check request deleted successfully"),
  INVENTORY_CHECK_REQUEST_RETRIEVED("Inventory check request retrieved successfully"),
  ALL_INVENTORY_CHECK_REQUESTS_RETRIEVED("All inventory check requests retrieved successfully"),

  // Product Combination messages
  PRODUCT_COMBINATION_CREATED("Product combination created successfully"),
  PRODUCT_COMBINATION_UPDATED("Product combination updated successfully"),
  PRODUCT_COMBINATION_DELETED("Product combination deleted successfully"),
  PRODUCT_COMBINATION_RETRIEVED("Product combination retrieved successfully"),
  ALL_PRODUCT_COMBINATIONS_RETRIEVED("All product combinations retrieved successfully"),

  // Promotion messages
  PROMOTION_CREATED("Promotion created successfully"),
  PROMOTION_UPDATED("Promotion updated successfully"),
  PROMOTION_DELETED("Promotion deleted successfully"),
  PROMOTION_RETRIEVED("Promotion retrieved successfully"),
  ALL_PROMOTIONS_RETRIEVED("All promotions retrieved successfully"),
  PROMOTION_FILTERED_LIST_RETRIEVED("Filtered promotions list retrieved successfully"),
  PROMOTION_PAGING_RETRIEVED("Promotions paging retrieved successfully"),
  PROMOTION_APPLIED("Promotions applied successfully");

  private final String message;

  ApiMessage(String message) {
    this.message = message;
  }
}
