package com.company.customersearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyDetails {

    @JsonProperty("loyaltyId")
    private String loyaltyId;

    @JsonProperty("tier")
    private String tier;

    @JsonProperty("status")
    private String status;
}
