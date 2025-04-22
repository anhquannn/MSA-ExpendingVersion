package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.FeedbackMapper;
import com.market.MSA.models.product.Feedback;
import com.market.MSA.repositories.product.FeedbackRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.product.FeedbackRequest;
import com.market.MSA.responses.product.FeedbackResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

  final FeedbackMapper feedbackMapper;

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

    Feedback savedFeedback = feedbackRepository.save(feedback);
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
    Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
    return feedbackOpt
        .map(feedbackMapper::toFeedbackResponse)
        .orElseThrow(
            () -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND)); // Or throw an exception if not
    // found
  }

  // Get all Feedbacks by Product ID
  public List<FeedbackResponse> getAllFeedbacksByProductId(long productId) {
    List<Feedback> feedbacks = feedbackRepository.findByProduct_ProductId(productId);
    return feedbacks.stream().map(feedbackMapper::toFeedbackResponse).collect(Collectors.toList());
  }
}
