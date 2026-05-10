package com.company.customersearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @JsonProperty("fullAddress")
    private Address fullAddress;

    @JsonProperty("loyaltyDetails")
    private LoyaltyDetails loyaltyDetails;
}
