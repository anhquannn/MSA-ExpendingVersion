package com.market.MSA.services.order;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.CampaignMapper;
import com.market.MSA.models.order.Campaign;
import com.market.MSA.repositories.order.CampaignRepository;
import com.market.MSA.requests.filters.CampaignFilterRequest;
import com.market.MSA.requests.order.CampaignRequest;
import com.market.MSA.responses.order.CampaignResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignService {
  final CampaignRepository campaignRepository;
  final CampaignMapper campaignMapper;

  @Transactional
  public CampaignResponse createCampaign(CampaignRequest request) {
    Campaign campaign = campaignMapper.toCampaign(request);
    Campaign savedCampaign = campaignRepository.save(campaign);
    return campaignMapper.toCampaignResponse(savedCampaign);
  }

  @Transactional
  public CampaignResponse updateCampaign(Long id, CampaignRequest request) {
    Campaign campaign =
        campaignRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_FOUND));
    campaignMapper.updateCampaign(campaign, request);
    Campaign updatedCampaign = campaignRepository.save(campaign);
    return campaignMapper.toCampaignResponse(updatedCampaign);
  }

  @Transactional
  public boolean deleteCampaign(Long id) {
    if (!campaignRepository.existsById(id)) {
      throw new AppException(ErrorCode.CAMPAIGN_NOT_FOUND);
    }
    campaignRepository.deleteById(id);
    return true;
  }

  public CampaignResponse getCampaignById(Long id) {
    return campaignRepository
        .findById(id)
        .map(campaignMapper::toCampaignResponse)
        .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_FOUND));
  }
  @Cacheable("all_campaigns")
  public List<CampaignResponse> getAll() {
      return campaignRepository.findAll().stream().map(campaignMapper::toCampaignResponse).collect(Collectors.toList());
  }

  @Cacheable("campaigns_list")
  public List<CampaignResponse> getAllCampaigns(CampaignFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    return campaignRepository
        .filter(
            request.getName(),
            request.getStatus(),
            request.getFromDate(),
            request.getToDate(),
            request.getKeyword(),
            sort)
        .stream()
        .map(campaignMapper::toCampaignResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("campaigns_paging")
  public Page<CampaignResponse> getAllCampaignsWithPaging(CampaignFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    Pageable pageable =
        PageRequest.of(
            request.getPage() - 1, // Convert to 0-based index
            request.getPageSize(),
            sort);

    return campaignRepository
        .filterWithPaging(
            request.getName(),
            request.getStatus(),
            request.getFromDate(),
            request.getToDate(),
            request.getKeyword(),
            pageable)
        .map(campaignMapper::toCampaignResponse);
  }
}
