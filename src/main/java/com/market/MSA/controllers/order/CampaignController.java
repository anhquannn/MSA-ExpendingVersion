package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.order.CampaignRequest;
import com.market.MSA.responses.order.CampaignResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.CampaignService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campaign")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignController {
  CampaignService campaignService;

  @PostMapping
  ApiResponse<CampaignResponse> createCampaign(@RequestBody @Valid CampaignRequest request) {
    return ApiResponse.<CampaignResponse>builder()
        .result(campaignService.createCampaign(request))
        .message(ApiMessage.CAMPAIGN_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{campaignId}")
  ApiResponse<CampaignResponse> updateCampaign(
      @PathVariable Long campaignId, @RequestBody @Valid CampaignRequest request) {
    return ApiResponse.<CampaignResponse>builder()
        .result(campaignService.updateCampaign(campaignId, request))
        .message(ApiMessage.CAMPAIGN_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{campaignId}")
  ApiResponse<Boolean> deleteCampaign(@PathVariable Long campaignId) {
    return ApiResponse.<Boolean>builder()
        .result(campaignService.deleteCampaign(campaignId))
        .message(ApiMessage.CAMPAIGN_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{campaignId}")
  ApiResponse<CampaignResponse> getCampaignById(@PathVariable Long campaignId) {
    return ApiResponse.<CampaignResponse>builder()
        .result(campaignService.getCampaignById(campaignId))
        .message(ApiMessage.CAMPAIGN_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/search")
  ApiResponse<List<CampaignResponse>> getCampaignByName(
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDirection) {
    return ApiResponse.<List<CampaignResponse>>builder()
        .result(
            campaignService.getCampaignByCampaignName(name, page, pageSize, sortBy, sortDirection))
        .message(ApiMessage.ALL_CAMPAIGNS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  ApiResponse<List<CampaignResponse>> getAllCampaigns(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<CampaignResponse>>builder()
        .result(campaignService.getAllCampaigns(page, pageSize))
        .message(ApiMessage.ALL_CAMPAIGNS_RETRIEVED.getMessage())
        .build();
  }
}
