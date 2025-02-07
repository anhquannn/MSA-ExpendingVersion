package com.market.MSA.controllers;

import com.market.MSA.requests.FeedbackRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.FeedbackResponse;
import com.market.MSA.services.FeedbackService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    FeedbackResponse createdFeedback = feedbackService.createFeedback(request);
    return ApiResponse.<FeedbackResponse>builder().result(createdFeedback).build();
  }

  @PutMapping("/{id}")
  public ApiResponse<FeedbackResponse> updateFeedback(
      @PathVariable Long id, @RequestBody @Valid FeedbackRequest request) {
    FeedbackResponse updatedFeedback = feedbackService.updateFeedback(id, request);
    return ApiResponse.<FeedbackResponse>builder().result(updatedFeedback).build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteFeedback(@PathVariable Long id) {
    feedbackService.deleteFeedback(id);
    return ApiResponse.<Void>builder().result(null).build();
  }

  @GetMapping("/{id}")
  public ApiResponse<FeedbackResponse> getFeedbackById(@PathVariable Long id) {
    FeedbackResponse feedbackResponse = feedbackService.getFeedbackById(id);
    return ApiResponse.<FeedbackResponse>builder().result(feedbackResponse).build();
  }

  @GetMapping("/product/{productId}")
  public ApiResponse<List<FeedbackResponse>> getAllFeedbacksByProductId(
      @PathVariable Long productId) {
    List<FeedbackResponse> feedbackResponses =
        feedbackService.getAllFeedbacksByProductId(productId);
    return ApiResponse.<List<FeedbackResponse>>builder().result(feedbackResponses).build();
  }
}
