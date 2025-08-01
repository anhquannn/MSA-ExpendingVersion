package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.ReturnOrderFilterRequest;
import com.market.MSA.requests.order.ReturnOrderRequest;
import com.market.MSA.requests.order.ReturnOrderStatusUpdateRequest;
import com.market.MSA.responses.order.ReturnOrderResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.ReturnOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/return-order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnOrderController {

  ReturnOrderService returnOrderService;

  /** Tạo yêu cầu trả hàng mới Endpoint: POST /return-order */
  @PostMapping
  public ApiResponse<ReturnOrderResponse> createReturnOrder(
      @RequestBody @Valid ReturnOrderRequest request) {

    ReturnOrderResponse response = returnOrderService.createReturnOrder(request);

    return ApiResponse.<ReturnOrderResponse>builder()
        .result(response)
        .message(ApiMessage.RETURN_ORDER_CREATED.getMessage())
        .build();
  }

  /**
   * Cập nhật trạng thái return order (Admin/Manager only) Endpoint: PUT
   * /return-order/{returnOrderId}/status
   */
  @PutMapping("/{returnOrderId}/status")
  @PreAuthorize("@customSecurity.isAdminOrManager()")
  public ApiResponse<ReturnOrderResponse> updateReturnOrderStatus(
      @PathVariable @NotNull(message = "Return Order ID is required") Long returnOrderId,
      @RequestBody @Valid ReturnOrderStatusUpdateRequest request) {

    ReturnOrderResponse response =
        returnOrderService.updateReturnOrderStatus(returnOrderId, request);

    return ApiResponse.<ReturnOrderResponse>builder()
        .result(response)
        .message(ApiMessage.RETURN_ORDER_STATUS_UPDATED.getMessage())
        .build();
  }

  /** Lấy thông tin return order theo ID Endpoint: GET /return-order/{returnOrderId} */
  @GetMapping("/{returnOrderId}")
  public ApiResponse<ReturnOrderResponse> getReturnOrderById(@PathVariable Long returnOrderId) {

    ReturnOrderResponse response = returnOrderService.getReturnOrderById(returnOrderId);

    return ApiResponse.<ReturnOrderResponse>builder()
        .result(response)
        .message(ApiMessage.RETURN_ORDER_RETRIEVED.getMessage())
        .build();
  }

  /** Lấy danh sách return orders theo user Endpoint: GET /return-order/user/{userId} */
  @GetMapping("/user/{userId}")
  public ApiResponse<java.util.List<ReturnOrderResponse>> getReturnOrdersByUser(
      @PathVariable Long userId) {

    java.util.List<ReturnOrderResponse> response = returnOrderService.getReturnOrdersByUser(userId);

    return ApiResponse.<java.util.List<ReturnOrderResponse>>builder()
        .result(response)
        .message(ApiMessage.USER_RETURN_ORDERS_RETRIEVED.getMessage())
        .build();
  }

  /**
   * Lấy danh sách return orders theo branch (Admin/Manager only) Endpoint: GET
   * /return-order/branch/{branchId}
   */
  @GetMapping("/branch/{branchId}")
  @PreAuthorize("@customSecurity.isAdminOrManager()")
  public ApiResponse<java.util.List<ReturnOrderResponse>> getReturnOrdersByBranch(
      @PathVariable Long branchId, @RequestParam(required = false) String status) {

    java.util.List<ReturnOrderResponse> response =
        returnOrderService.getReturnOrdersByBranch(branchId, status);

    return ApiResponse.<java.util.List<ReturnOrderResponse>>builder()
        .result(response)
        .message(ApiMessage.BRANCH_RETURN_ORDERS_RETRIEVED.getMessage())
        .build();
  }

  /**
   * Approve return order và tự động tạo inbound transfer (Admin/Manager only) Endpoint: PUT
   * /return-order/{returnOrderId}/approve
   */
  @PutMapping("/{returnOrderId}/approve")
  @PreAuthorize("@customSecurity.isAdminOrManager()")
  public ApiResponse<ReturnOrderResponse> approveReturnOrder(
      @PathVariable Long returnOrderId, @RequestParam(required = false) String reason) {

    ReturnOrderResponse response = returnOrderService.approveReturnOrder(returnOrderId, reason);

    return ApiResponse.<ReturnOrderResponse>builder()
        .result(response)
        .message(ApiMessage.RETURN_ORDER_APPROVED.getMessage())
        .build();
  }

  /** Reject return order (Admin/Manager only) Endpoint: PUT /return-order/{returnOrderId}/reject */
  @PutMapping("/{returnOrderId}/reject")
  @PreAuthorize("@customSecurity.isAdminOrManager()")
  public ApiResponse<ReturnOrderResponse> rejectReturnOrder(
      @PathVariable Long returnOrderId, @RequestParam String reason) {

    ReturnOrderResponse response = returnOrderService.rejectReturnOrder(returnOrderId, reason);

    return ApiResponse.<ReturnOrderResponse>builder()
        .result(response)
        .message(ApiMessage.RETURN_ORDER_REJECTED.getMessage())
        .build();
  }

  /** Lọc return orders với phân trang Endpoint: POST /return-order/filter/paging */
  @PostMapping("/filter/paging")
  @PreAuthorize("@customSecurity.isAdminOrManager()")
  public ApiResponse<Page<ReturnOrderResponse>> filterReturnOrdersWithPaging(
      @RequestBody @Valid ReturnOrderFilterRequest request) {

    Page<ReturnOrderResponse> response = returnOrderService.filterReturnOrdersWithPaging(request);

    return ApiResponse.<Page<ReturnOrderResponse>>builder()
        .result(response)
        .message(ApiMessage.RETURN_ORDERS_FILTERED_PAGING.getMessage())
        .build();
  }

  /** Lọc return orders không phân trang Endpoint: POST /return-order/filter */
  @PostMapping("/filter")
  @PreAuthorize("@customSecurity.isAdminOrManager()")
  public ApiResponse<java.util.List<ReturnOrderResponse>> filterReturnOrders(
      @RequestBody @Valid ReturnOrderFilterRequest request) {

    java.util.List<ReturnOrderResponse> response = returnOrderService.filterReturnOrders(request);

    return ApiResponse.<java.util.List<ReturnOrderResponse>>builder()
        .result(response)
        .message(ApiMessage.RETURN_ORDERS_FILTERED.getMessage())
        .build();
  }
}
