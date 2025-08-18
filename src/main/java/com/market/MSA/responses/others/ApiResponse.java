package com.market.MSA.responses.others;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collections;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  @Builder.Default private int code = 200;

  String message;

  @Setter private T result;

  @SuppressWarnings("unchecked")
  public T getResult() {
    if (result != null) return result;

    // Nếu T là một List, thì trả về emptyList, ngược lại trả về null
    if (List.class.isAssignableFrom(Object.class)) { // Không có cách rõ ràng để kiểm tra T
      return (T) Collections.emptyList(); // vẫn là unchecked cast
    }
    return null;
  }
}
