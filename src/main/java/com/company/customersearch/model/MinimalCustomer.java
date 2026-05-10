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
public class MinimalCustomer {

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    public static MinimalCustomer fromCustomer(Customer customer) {
        return MinimalCustomer.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
