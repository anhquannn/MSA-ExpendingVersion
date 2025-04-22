package com.market.MSA.responses.product;

import com.market.MSA.responses.user.UserResponse;
import java.util.Date;
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
public class FeedbackResponse {
  Long feedbackId;

  int rating;
  String comments;
  Date createAt;

  UserResponse user;
  ProductResponse product;
}
