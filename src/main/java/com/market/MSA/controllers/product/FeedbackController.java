package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.FeedbackFilterRequest;
import com.market.MSA.requests.product.FeedbackRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.FeedbackResponse;
import com.market.MSA.services.product.FeedbackService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackController {
  FeedbackService feedbackService;

  @PostMapping
  public ApiResponse<FeedbackResponse> createFeedback(@RequestBody @Valid FeedbackRequest request) {
    return ApiResponse.<FeedbackResponse>builder()
        .result(feedbackService.createFeedback(request))
        .message(ApiMessage.FEEDBACK_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<FeedbackResponse> updateFeedback(
      @PathVariable Long id, @RequestBody @Valid FeedbackRequest request) {
    return ApiResponse.<FeedbackResponse>builder()
        .result(feedbackService.updateFeedback(id, request))
        .message(ApiMessage.FEEDBACK_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteFeedback(@PathVariable Long id) {
    Boolean result = feedbackService.deleteFeedback(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.FEEDBACK_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<FeedbackResponse> getFeedbackById(@PathVariable Long id) {
    return ApiResponse.<FeedbackResponse>builder()
        .result(feedbackService.getFeedbackById(id))
        .message(ApiMessage.FEEDBACK_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<FeedbackResponse>> getAll() {
    return ApiResponse.<List<FeedbackResponse>>builder()
        .result(feedbackService.getAll())
        .message(ApiMessage.ALL_FEEDBACKS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<FeedbackResponse>> getAllFeedbacks(
      @RequestBody @Valid FeedbackFilterRequest request) {
    return ApiResponse.<List<FeedbackResponse>>builder()
        .result(feedbackService.getAllFeedbacks(request))
        .message(ApiMessage.ALL_FEEDBACKS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<FeedbackResponse>> getAllFeedbacksWithPaging(
      @RequestBody @Valid FeedbackFilterRequest request) {
    return ApiResponse.<Page<FeedbackResponse>>builder()
        .result(feedbackService.getAllFeedbacksWithPaging(request))
        .message(ApiMessage.ALL_FEEDBACKS_RETRIEVED.getMessage())
        .build();
  }
}
