package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.FeedbackMapper;
import com.market.MSA.models.order.OrderDetail;
import com.market.MSA.models.product.Feedback;
import com.market.MSA.repositories.product.FeedbackRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.FeedbackFilterRequest;
import com.market.MSA.requests.product.FeedbackRequest;
import com.market.MSA.responses.product.FeedbackResponse;
import com.market.MSA.services.others.EntityFinderService;
import com.market.MSA.services.user.RewardPointService;
import java.util.List;
import java.util.Optional;
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
public class FeedbackService {
  final EntityFinderService entityFinderService;
  final FeedbackRepository feedbackRepository;
  final UserRepository userRepository;
  final ProductRepository productRepository;
  final com.market.MSA.repositories.order.OrderDetailRepository orderDetailRepository;

  final FeedbackMapper feedbackMapper;
  final RewardPointService rewardPointService;

  // Create Feedback
  @Transactional
  public FeedbackResponse createFeedback(FeedbackRequest request) {
    Feedback feedback = feedbackMapper.toFeedback(request);
    feedback.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    feedback.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));
    feedback.setComments(request.getComments());

    // Link to order detail and mark rated
    if (request.getOrderDetailId() != null) {
      OrderDetail od =
          entityFinderService.findByIdOrThrow(
              orderDetailRepository, request.getOrderDetailId(), ErrorCode.ORDER_DETAIL_NOT_FOUND);

      // Kiểm tra xem order detail này đã được đánh giá chưa
      if (od.isRated()) {
        throw new AppException(ErrorCode.ORDER_DETAIL_ALREADY_RATED);
      }

      od.setRated(true);
      feedback.setOrderDetail(od);
    }

    Feedback savedFeedback = feedbackRepository.save(feedback);

    // Tích điểm cho feedback - chỉ tích điểm khi feedback được tạo từ order detail
    if (feedback.getOrderDetail() != null) {
      // Tính điểm thưởng cho feedback (100 điểm cho mỗi feedback)
      double points = 100.0;

      try {
        // Gọi RewardPointService để vừa tạo transaction vừa cập nhật bảng reward_point
        rewardPointService.earnPoints(
            feedback.getUser().getUserId(),
            feedback.getOrderDetail().getOrder().getOrderId(),
            points);

        log.info(
            "Successfully awarded {} points to user {} for feedback {}",
            points,
            feedback.getUser().getUserId(),
            savedFeedback.getFeedbackId());
      } catch (Exception e) {
        log.error(
            "Failed to award points for feedback {}: {}",
            savedFeedback.getFeedbackId(),
            e.getMessage());
        // Không throw exception để không làm rollback việc tạo feedback
        // Có thể implement retry mechanism hoặc queue để xử lý sau
      }
    }

    return feedbackMapper.toFeedbackResponse(savedFeedback);
  }

  // Update Feedback
  @Transactional
  public FeedbackResponse updateFeedback(long feedbackId, FeedbackRequest request) {
    Optional<Feedback> existingFeedbackOpt = feedbackRepository.findById(feedbackId);
    if (existingFeedbackOpt.isPresent()) {
      Feedback existingFeedback = existingFeedbackOpt.get();
      feedbackMapper.updateFeedbackFromRequest(request, existingFeedback);
      existingFeedback.setProduct(
          entityFinderService.findByIdOrThrow(
              productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
      existingFeedback.setUser(
          entityFinderService.findByIdOrThrow(
              userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));

      Feedback updatedFeedback = feedbackRepository.save(existingFeedback);
      return feedbackMapper.toFeedbackResponse(updatedFeedback);
    }
    throw new AppException(ErrorCode.FEEDBACK_NOT_FOUND); // Or throw an exception if not found
  }

  // Delete Feedback
  @Transactional
  public boolean deleteFeedback(long feedbackId) {
    Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
    if (feedbackOpt.isPresent()) {
      feedbackRepository.delete(feedbackOpt.get());
      return true;
    }

    throw new AppException(ErrorCode.FEEDBACK_NOT_FOUND); // Or throw an exception if not found
  }

  // Get Feedback by ID
  public FeedbackResponse getFeedbackById(long feedbackId) {
    return feedbackRepository
        .findById(feedbackId)
        .map(feedbackMapper::toFeedbackResponse)
        .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
  }

  @Cacheable("all_feedbacks")
  public List<FeedbackResponse> getAll() {
    return feedbackRepository.findAll().stream()
        .map(feedbackMapper::toFeedbackResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("feedbacks_list")
  public List<FeedbackResponse> getAllFeedbacks(FeedbackFilterRequest request) {
    return feedbackRepository
        .filter(
            request.getProductId(),
            request.getUserId(),
            request.getMinRating(),
            request.getMaxRating(),
            request.getFromDate(),
            request.getToDate())
        .stream()
        .map(feedbackMapper::toFeedbackResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("feedbacks_paging")
  public Page<FeedbackResponse> getAllFeedbacksWithPaging(FeedbackFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return feedbackRepository
        .filterWithPaging(
            request.getProductId(),
            request.getUserId(),
            request.getMinRating(),
            request.getMaxRating(),
            request.getFromDate(),
            request.getToDate(),
            pageable)
        .map(feedbackMapper::toFeedbackResponse);
  }
}
