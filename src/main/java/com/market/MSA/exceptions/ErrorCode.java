package com.market.MSA.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
  UNCATEGORIZED_EXCEPTION(99, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

  USER_EXISTED(101, "User existed", HttpStatus.BAD_REQUEST),
  USER_NOT_EXISTED(102, "User not exist", HttpStatus.NOT_FOUND),
  USER_UNAUTHENTICATED(103, "Unauthenticated", HttpStatus.UNAUTHORIZED),
  USER_INVALID(104, "Invalid request", HttpStatus.BAD_REQUEST),

  INVALID_TOKEN(105, "Invalid token", HttpStatus.BAD_REQUEST),
  UNAUTHORIZED(106, "You do not have permission", HttpStatus.FORBIDDEN),
  GENERATE_TOKEN_FALSE(131, "Generate token false", HttpStatus.INTERNAL_SERVER_ERROR),
  LOGOUT_FALSE(132, "Logout false", HttpStatus.INTERNAL_SERVER_ERROR),
  ROLE_NOT_FOUND(107, "Role not found", HttpStatus.NOT_FOUND),
  INVALID_DOB(108, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),

  INVALID_EMAIL(109, "Invalid email", HttpStatus.NOT_FOUND),
  INVALID_CREDENTIALS(110, "Wrong password", HttpStatus.BAD_REQUEST),
  INVALID_GOOGLE_TOKEN(111, "Getting google token failed!", HttpStatus.BAD_REQUEST),
  PARSE_GOOGLE_RESPONSE_ERROR(112, "Error parsing google response!", HttpStatus.FORBIDDEN),

  ORDER_NOT_FOUND(113, "Order not found!", HttpStatus.NOT_FOUND),
  CART_NOT_FOUND(114, "Cart not found!", HttpStatus.NOT_FOUND),
  BRANCH_NOT_FOUND(115, "Branch not found!", HttpStatus.NOT_FOUND),
  PRODUCT_NOT_FOUND(116, "Product not found!", HttpStatus.NOT_FOUND),
  CART_ITEM_NOT_FOUND(117, "Cart item not found!", HttpStatus.NOT_FOUND),
  PARENT_CATEGORY_NOT_FOUND(118, "Parent category not found!", HttpStatus.NOT_FOUND),
  CATEGORY_NOT_FOUND(119, "Category not found!", HttpStatus.NOT_FOUND),
  DELIVERY_DETAIL_NOT_FOUND(120, "Delivery detail not found!", HttpStatus.NOT_FOUND),
  DELIVERY_INFO_NOT_FOUND(121, "Delivery info not found!", HttpStatus.NOT_FOUND),
  PARSE_SHIPPO_RESPONSE_ERROR(122, "Error parsing shipment response!", HttpStatus.FORBIDDEN),
  MANUFACTURER_NOT_FOUND(123, "Manufacturer not found!", HttpStatus.NOT_FOUND),
  ORDER_DETAIL_NOT_FOUND(124, "Order detail not found!", HttpStatus.NOT_FOUND),
  PAYMENT_NOT_FOUND(125, "Payment not found!", HttpStatus.NOT_FOUND),
  PROMO_CODE_NOT_FOUND(126, "Promo code not found!", HttpStatus.NOT_FOUND),
  CANCEL_ORDER_NOT_FOUND(127, "Cancel order not found!", HttpStatus.NOT_FOUND),
  FEEDBACK_NOT_FOUND(128, "Feedback not found!", HttpStatus.NOT_FOUND),

  TRENDING_PRODUCT_NOT_FOUND(129, "Trending product not found!", HttpStatus.NOT_FOUND),
  USER_BEHAVIOR_NOT_FOUND(130, "User behavior not found!", HttpStatus.NOT_FOUND),
  PROMO_CODE_NOT_YET_ACTIVE(132, "Promo code not yet active!", HttpStatus.BAD_REQUEST),
  PROMO_CODE_EXPIRED(133, "Promo code expired!", HttpStatus.BAD_REQUEST),
  WRONG_PROMO_CODE(134, "Wrong promo code!", HttpStatus.BAD_REQUEST),
  CANNOT_SEND_EMAIL(135, "Can not send email!", HttpStatus.BAD_REQUEST),
  PERMISSION_NOT_FOUND(136, "Permission not found!", HttpStatus.NOT_FOUND),

  INVENTORY_NOT_FOUND(137, "Inventory not found!", HttpStatus.NOT_FOUND),
  INVENTORY_PRODUCT_NOT_FOUND(138, "InventoryProduct not found!", HttpStatus.NOT_FOUND),
  INSUFFICIENT_STOCK(139, "Insufficient stock!", HttpStatus.BAD_REQUEST),
  INVALID_SORT_FIELD(140, "Invalid sort field!", HttpStatus.BAD_REQUEST),

  NOTIFICATION_NOT_FOUND(141, "Notification not found!", HttpStatus.NOT_FOUND),
  INVALID_INPUT(142, "Invalid input!", HttpStatus.BAD_REQUEST),

  CAMPAIGN_NOT_FOUND(143, "Campaign not found!", HttpStatus.NOT_FOUND),
  PROMO_CODE_OUTSIDE_CAMPAIGN_DATES(
      144, "PromoCode dates must be within Campaign date range!", HttpStatus.BAD_REQUEST),

  TRANSFER_REQUEST_ALREADY_PROCESSED(
      145, "Transfer request has already been processed", HttpStatus.BAD_REQUEST),
  TRANSFER_REQUEST_NOT_FOUND(146, "Transfer request not found", HttpStatus.NOT_FOUND),
  TRANSFER_REQUEST_ITEM_NOT_FOUND(147, "Transfer request item not found", HttpStatus.NOT_FOUND),
  REWARD_POINT_NOT_FOUND(146, "Reward point not found", HttpStatus.NOT_FOUND),
  REWARD_POINT_TRANSACTION_NOT_FOUND(
      147, "Reward point transaction not found", HttpStatus.NOT_FOUND),

  INSUFFICIENT_POINTS(150, "Insufficient points", HttpStatus.BAD_REQUEST),
  PAYMENT_ERROR(151, "Payment error", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final int code;
  private final String message;
  private final HttpStatusCode statusCode;

  ErrorCode(int code, String message, HttpStatusCode statusCode) {
    this.code = code;
    this.message = message;
    this.statusCode = statusCode;
  }
}
