package com.market.MSA.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShippoApiResponse {
    @JsonProperty("results")
    List<ShippoResponse> results;

    @JsonProperty("next")
    String next;

    @JsonProperty("previous")
    String previous;
}