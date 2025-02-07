package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.FeedbackMapper;
import com.market.MSA.models.Feedback;
import com.market.MSA.repositories.FeedbackRepository;
import com.market.MSA.requests.FeedbackRequest;
import com.market.MSA.responses.FeedbackResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackService {
  FeedbackRepository feedbackRepository;
  FeedbackMapper feedbackMapper;

  // Create Feedback
  public FeedbackResponse createFeedback(FeedbackRequest request) {
    Feedback feedback = feedbackMapper.toFeedback(request);
    Feedback savedFeedback = feedbackRepository.save(feedback);
    return feedbackMapper.toFeedbackResponse(savedFeedback);
  }

  // Update Feedback
  public FeedbackResponse updateFeedback(long feedbackId, FeedbackRequest request) {
    Optional<Feedback> existingFeedbackOpt = feedbackRepository.findById(feedbackId);
    if (existingFeedbackOpt.isPresent()) {
      Feedback existingFeedback = existingFeedbackOpt.get();
      feedbackMapper.updateFeedbackFromRequest(request, existingFeedback);
      Feedback updatedFeedback = feedbackRepository.save(existingFeedback);
      return feedbackMapper.toFeedbackResponse(updatedFeedback);
    }
    throw new AppException(ErrorCode.FEEDBACK_NOT_FOUND); // Or throw an exception if not found
  }

  // Delete Feedback
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
