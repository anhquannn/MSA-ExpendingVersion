package com.market.MSA.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShippoParcel {
    @JsonProperty("object_owner")
     String objectOwner;

    @JsonProperty("mass_unit")
     String massUnit;

    @JsonProperty("distance_unit")
     String distanceUnit;

    @JsonProperty("length")
     String length;

    @JsonProperty("width")
     String width;

    @JsonProperty("height")
     String height;

    @JsonProperty("weight")
     String weight;
}
