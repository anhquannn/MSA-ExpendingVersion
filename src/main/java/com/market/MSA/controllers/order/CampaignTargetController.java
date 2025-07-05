package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.CampaignTargetFilterRequest;
import com.market.MSA.requests.order.CampaignTargetRequest;
import com.market.MSA.responses.order.CampaignTargetResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.CampaignTargetService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campaign-target")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignTargetController {
  final CampaignTargetService campaignTargetService;

  @PostMapping
  public ApiResponse<CampaignTargetResponse> createCampaignTarget(
      @RequestBody @Valid CampaignTargetRequest request) {
    return ApiResponse.<CampaignTargetResponse>builder()
        .result(campaignTargetService.createCampaignTarget(request))
        .message(
            ApiMessage.CAMPAIGN_TARGET_CREATED
                .getMessage())
        .build();
  }

  @PutMapping("/{campaignTargetId}")
  public ApiResponse<CampaignTargetResponse> updateCampaignTarget(
      @PathVariable Long campaignTargetId, @RequestBody CampaignTargetRequest request) {
    return ApiResponse.<CampaignTargetResponse>builder()
        .result(campaignTargetService.updateCampaignTarget(campaignTargetId, request))
        .message(
            ApiMessage.CAMPAIGN_TARGET_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{campaignTargetId}")
  public ApiResponse<Boolean> deleteCampaignTarget(@PathVariable Long campaignTargetId) {
    return ApiResponse.<Boolean>builder()
        .result(campaignTargetService.deleteCampaignTarget(campaignTargetId))
        .message(
            ApiMessage.CAMPAIGN_TARGET_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{campaignTargetId}")
  public ApiResponse<CampaignTargetResponse> getCampaignTargetById(
      @PathVariable Long campaignTargetId) {
    return ApiResponse.<CampaignTargetResponse>builder()
        .result(campaignTargetService.getById(campaignTargetId))
        .message(
            ApiMessage.CAMPAIGN_TARGET_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<CampaignTargetResponse>> getAllCampaignTargets() {
    return ApiResponse.<List<CampaignTargetResponse>>builder()
        .result(campaignTargetService.getAll())
        .message(ApiMessage.ALL_CAMPAIGN_TARGETS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<CampaignTargetResponse>> getFilteredCampaignTargets(
      @RequestBody @Valid CampaignTargetFilterRequest request) {
    return ApiResponse.<List<CampaignTargetResponse>>builder()
        .result(campaignTargetService.getCampaignTargets(request))
        .message(
            ApiMessage.ALL_CAMPAIGN_TARGETS_RETRIEVED
                .getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<CampaignTargetResponse>> getFilteredCampaignTargetsWithPaging(
      @RequestBody @Valid CampaignTargetFilterRequest request) {
    return ApiResponse.<Page<CampaignTargetResponse>>builder()
        .result(campaignTargetService.getCampaignTargetsWithPaging(request))
        .message(
            ApiMessage.ALL_CAMPAIGN_TARGETS_RETRIEVED
                .getMessage())
        .build();
  }
}
