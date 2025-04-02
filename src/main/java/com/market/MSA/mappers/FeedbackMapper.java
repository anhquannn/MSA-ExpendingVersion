package com.market.MSA.mappers;

import com.market.MSA.models.Feedback;
import com.market.MSA.requests.FeedbackRequest;
import com.market.MSA.responses.FeedbackResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface FeedbackMapper {
  Feedback toFeedback(FeedbackRequest request);

  FeedbackResponse toFeedbackResponse(Feedback feedback);

  @Mapping(target = "feedbackId", ignore = true)
  void updateFeedbackFromRequest(FeedbackRequest request, @MappingTarget Feedback feedback);
}
