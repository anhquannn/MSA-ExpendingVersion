package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.OutboundFilterRequest;
import com.market.MSA.requests.product.OutboundRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.OutboundResponse;
import com.market.MSA.services.product.OutboundService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outbound")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutboundController {
  OutboundService outboundService;

  @PostMapping
  public ApiResponse<OutboundResponse> createOutboundTransfer(
      @RequestBody OutboundRequest request) {
    return ApiResponse.<OutboundResponse>builder()
        .result(outboundService.createOutboundTransfer(request))
        .message(ApiMessage.OUTBOUND_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<OutboundResponse> updateOutboundTransfer(
      @PathVariable Long id, @RequestBody OutboundRequest request) {
    return ApiResponse.<OutboundResponse>builder()
        .result(outboundService.updateOutboundTransfer(id, request))
        .message(ApiMessage.OUTBOUND_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteOutboundTransfer(@PathVariable Long id) {
    Boolean result = outboundService.deleteOutboundTransfer(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.OUTBOUND_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<OutboundResponse> getOutboundTransferById(@PathVariable Long id) {
    return ApiResponse.<OutboundResponse>builder()
        .result(outboundService.getOutboundTransferById(id))
        .message(ApiMessage.OUTBOUND_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<OutboundResponse>> getAll() {
    return ApiResponse.<List<OutboundResponse>>builder()
        .result(outboundService.getAll())
        .message(ApiMessage.ALL_OUTBOUNDS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<OutboundResponse>> getAllWithPaging(
      @RequestBody @Valid OutboundFilterRequest request) {
    return ApiResponse.<Page<OutboundResponse>>builder()
        .result(outboundService.getAllWithPaging(request))
        .message(ApiMessage.ALL_OUTBOUNDS_RETRIEVED.getMessage())
        .build();
  }
}
