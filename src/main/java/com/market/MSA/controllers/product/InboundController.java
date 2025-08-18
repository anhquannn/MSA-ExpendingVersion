package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.InboundFilterRequest;
import com.market.MSA.requests.product.InboundRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.InboundResponse;
import com.market.MSA.services.product.InboundService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inbound")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InboundController {
  InboundService inboundService;

  @PostMapping
  public ApiResponse<InboundResponse> createInboundTransfer(@RequestBody InboundRequest request) {
    return ApiResponse.<InboundResponse>builder()
        .result(inboundService.createInboundTransfer(request))
        .message(ApiMessage.INBOUND_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<InboundResponse> updateInboundTransfer(
      @PathVariable Long id, @RequestBody InboundRequest request) {
    return ApiResponse.<InboundResponse>builder()
        .result(inboundService.updateInboundTransfer(id, request))
        .message(ApiMessage.INBOUND_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteInboundTransfer(@PathVariable Long id) {
    Boolean result = inboundService.deleteInboundTransfer(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.INBOUND_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<InboundResponse> getInboundTransferById(@PathVariable Long id) {
    return ApiResponse.<InboundResponse>builder()
        .result(inboundService.getInboundTransferById(id))
        .message(ApiMessage.INBOUND_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<InboundResponse>> getAll() {
    return ApiResponse.<List<InboundResponse>>builder()
        .result(inboundService.getAll())
        .message(ApiMessage.ALL_INBOUNDS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<InboundResponse>> getAllWithPaging(
      @RequestBody @Valid InboundFilterRequest request) {
    return ApiResponse.<Page<InboundResponse>>builder()
        .result(inboundService.getAllWithPaging(request))
        .message(ApiMessage.ALL_INBOUNDS_RETRIEVED.getMessage())
        .build();
  }
}
