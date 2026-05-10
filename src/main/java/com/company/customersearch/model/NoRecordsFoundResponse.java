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
public class NoRecordsFoundResponse {

    @JsonProperty("message")
    private String message;

    public static NoRecordsFoundResponse of() {
        return NoRecordsFoundResponse.builder()
                .message("No customer records found")
                .build();
    }
}
