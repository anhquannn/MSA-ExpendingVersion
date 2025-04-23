package com.market.MSA.services.order;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.CampaignMapper;
import com.market.MSA.models.order.Campaign;
import com.market.MSA.repositories.order.CampaignRepository;
import com.market.MSA.requests.order.CampaignRequest;
import com.market.MSA.responses.order.CampaignResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignService {
  final CampaignRepository campaignRepository;
  private final CampaignMapper campaignMapper;

  public CampaignResponse createCampaign(CampaignRequest request) {
    Campaign campaign = campaignMapper.toCampaign(request);
    Campaign savedCampaign = campaignRepository.save(campaign);
    return campaignMapper.toCampaignResponse(savedCampaign);
  }

  public CampaignResponse updateCampaign(Long id, CampaignRequest request) {
    Campaign campaign =
        campaignRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_FOUND));
    campaignMapper.updateCampaign(campaign, request);
    Campaign updatedCampaign = campaignRepository.save(campaign);
    return campaignMapper.toCampaignResponse(updatedCampaign);
  }

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

  public List<CampaignResponse> getCampaignByCampaignName(
      String name, int page, int pageSize, String sortBy, String sortDirection) {
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
    Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

    Page<Campaign> campaigns = campaignRepository.searchByName(name, pageable);

    return campaigns.map(campaignMapper::toCampaignResponse).stream().toList();
  }

  public List<CampaignResponse> getAllCampaigns(int page, int pageSize) {
    return campaignRepository.findAll().stream()
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(campaignMapper::toCampaignResponse)
        .collect(Collectors.toList());
  }
}
