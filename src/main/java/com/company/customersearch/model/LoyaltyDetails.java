package com.company.customersearch.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoyaltyDetails {
    private String loyaltyProgramName;
    private String memberId;
    private String tier;
    private Integer points;
    private String joinDate;
    private String expiryDate;
}
