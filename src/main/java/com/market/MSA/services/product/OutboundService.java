package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.OutboundMapper;
import com.market.MSA.models.product.OutboundTransfer;
import com.market.MSA.repositories.product.OutboundRepository;
import com.market.MSA.requests.filters.OutboundFilterRequest;
import com.market.MSA.requests.product.OutboundRequest;
import com.market.MSA.responses.product.OutboundResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutboundService {
  OutboundRepository outboundTransferRepository;
  OutboundMapper outboundMapper;

  @Transactional
  public OutboundResponse createOutboundTransfer(OutboundRequest request) {
    OutboundTransfer outboundTransfer = outboundMapper.toOutboundTransfer(request);
    outboundTransfer = outboundTransferRepository.save(outboundTransfer);
    return outboundMapper.toOutboundResponse(outboundTransfer);
  }

  @Transactional
  public OutboundResponse updateOutboundTransfer(Long outboundTransferId, OutboundRequest request) {
    OutboundTransfer outboundTransfer =
        outboundTransferRepository
            .findById(outboundTransferId)
            .orElseThrow(() -> new AppException(ErrorCode.OUTBOUND_TRANSFER_NOT_FOUND));

    outboundMapper.updateOutbound(request, outboundTransfer);

    outboundTransfer = outboundTransferRepository.save(outboundTransfer);
    return outboundMapper.toOutboundResponse(outboundTransfer);
  }

  @Transactional
  public boolean deleteOutboundTransfer(Long outboundTransferId) {
    if (!outboundTransferRepository.existsById(outboundTransferId)) {
      throw new AppException(ErrorCode.OUTBOUND_TRANSFER_NOT_FOUND);
    }
    outboundTransferRepository.deleteById(outboundTransferId);
    return true;
  }

  public OutboundResponse getOutboundTransferById(Long outboundTransferId) {
    OutboundTransfer outboundTransfer =
        outboundTransferRepository
            .findById(outboundTransferId)
            .orElseThrow(() -> new AppException(ErrorCode.OUTBOUND_TRANSFER_NOT_FOUND));
    return outboundMapper.toOutboundResponse(outboundTransfer);
  }

  @Cacheable("all_outbound_transfers")
  public List<OutboundResponse> getAll() {
    return outboundTransferRepository.findAll().stream()
        .map(outboundMapper::toOutboundResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("outbound_transfers_paging")
  public Page<OutboundResponse> getAllWithPaging(OutboundFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return outboundTransferRepository
        .filterWithPaging(
            request.getStatus(),
            request.getFromDate(),
            request.getToDate(),
            request.getUserId(),
            request.getInventoryId(),
            request.getTransferId(),
            pageable)
        .map(outboundMapper::toOutboundResponse);
  }
}
