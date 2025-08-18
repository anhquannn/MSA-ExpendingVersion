package com.market.MSA.services.order;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.CampaignTargetMapper;
import com.market.MSA.models.order.Campaign;
import com.market.MSA.models.order.CampaignTarget;
import com.market.MSA.repositories.order.CampaignRepository;
import com.market.MSA.repositories.order.CampaignTargetRepository;
import com.market.MSA.requests.filters.CampaignTargetFilterRequest;
import com.market.MSA.requests.order.CampaignTargetRequest;
import com.market.MSA.responses.order.CampaignTargetResponse;
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
public class CampaignTargetService {
  final CampaignTargetRepository campaignTargetRepository;
  final CampaignTargetMapper campaignTargetMapper;
  final CampaignRepository campaignRepository;

  public CampaignTargetResponse createCampaignTarget(CampaignTargetRequest request) {
    Campaign campaign =
        campaignRepository
            .findById(request.getCampaignId())
            .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_FOUND));

    // Kiểm tra xem campaign này đã có mục trong CampaignTarget chưa
    Long existingCount = campaignTargetRepository.countByCampaignId(request.getCampaignId());
    if (existingCount > 0) {
      throw new AppException(ErrorCode.DUPLICATE_CAMPAIGN_TARGET);
    }

    CampaignTarget entity = campaignTargetMapper.toCampaignTarget(request);
    entity.setCampaign(campaign);

    CampaignTarget saved = campaignTargetRepository.save(entity);
    return campaignTargetMapper.toCampaignTargetResponse(saved);
  }

  public CampaignTargetResponse updateCampaignTarget(Long id, CampaignTargetRequest request) {
    CampaignTarget target =
        campaignTargetRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_TARGET_NOT_FOUND));

    campaignTargetMapper.updateCampaignTarget(request, target);

    // nếu CampaignId có thay đổi
    if (!target.getCampaign().getCampaignId().equals(request.getCampaignId())) {
      Campaign campaign =
          campaignRepository
              .findById(request.getCampaignId())
              .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_FOUND));
      target.setCampaign(campaign);
    }

    CampaignTarget updated = campaignTargetRepository.save(target);
    return campaignTargetMapper.toCampaignTargetResponse(updated);
  }

  public boolean deleteCampaignTarget(Long id) {
    if (!campaignTargetRepository.existsById(id)) {
      throw new AppException(ErrorCode.CAMPAIGN_TARGET_NOT_FOUND);
    }
    campaignTargetRepository.deleteById(id);
    return true;
  }

  public CampaignTargetResponse getById(Long id) {
    return campaignTargetRepository
        .findById(id)
        .map(campaignTargetMapper::toCampaignTargetResponse)
        .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_TARGET_NOT_FOUND));
  }

  public List<CampaignTargetResponse> getAll() {
    return campaignTargetRepository.findAll().stream()
        .map(campaignTargetMapper::toCampaignTargetResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Cacheable("campaign_target_list")
  public List<CampaignTargetResponse> getCampaignTargets(CampaignTargetFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    return campaignTargetRepository
        .filter(request.getCampaignId(), request.getTargetType(), request.getTargetId(), sort)
        .stream()
        .map(campaignTargetMapper::toCampaignTargetResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Cacheable("campaign_target_paging")
  public Page<CampaignTargetResponse> getCampaignTargetsWithPaging(
      CampaignTargetFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return campaignTargetRepository
        .filterWithPaging(
            request.getCampaignId(), request.getTargetType(), request.getTargetId(), pageable)
        .map(campaignTargetMapper::toCampaignTargetResponse);
  }
}
