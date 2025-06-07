package com.market.MSA.requests.product;

import com.market.MSA.validators.RatingConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackRequest {
  @RatingConstraint int rating;

  String comments;
  LocalDateTime createAt;

  Long userId;
  Long productId;
}
