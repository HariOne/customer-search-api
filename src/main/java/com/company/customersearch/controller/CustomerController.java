package com.company.customersearch.controller;

import com.company.customersearch.enums.Brand;
import com.company.customersearch.exception.InvalidBrandException;
import com.company.customersearch.service.CustomerService;
import com.company.customersearch.util.CorrelationIdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Customer Search", description = "Customer search operations")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Search customers by brand",
            description = "Search customers across multiple brands with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid brand"),
            @ApiResponse(responseCode = "502", description = "External API error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{brand}/customers")
    public ResponseEntity<Object> searchCustomers(
            @PathVariable("brand")
            @Parameter(description = "Brand name (Zoomcar, Revv, MyChoize, Myles)", required = true)
            String brandStr,

            @RequestParam(value = "first_name", required = false)
            @Parameter(description = "Customer first name", required = false)
            String firstName,

            @RequestParam(value = "last_name", required = false)
            @Parameter(description = "Customer last name", required = false)
            String lastName,

            @RequestParam(value = "loyalty_id", required = false)
            @Parameter(description = "Customer loyalty ID", required = false)
            String loyaltyId,

            @RequestParam(value = "postal_code", required = false)
            @Parameter(description = "Customer postal code", required = false)
            String postalCode,

            @RequestParam(value = "affiliation", required = false)
            @Parameter(description = "Customer affiliation", required = false)
            String affiliation,

            @RequestParam(value = "date_of_birth", required = false)
            @Parameter(description = "Customer date of birth", required = false)
            String dateOfBirth,

            @RequestParam(value = "email", required = false)
            @Parameter(description = "Customer email", required = false)
            String email,

            @RequestParam(value = "phone_number", required = false)
            @Parameter(description = "Customer phone number", required = false)
            String phoneNumber) {

        String correlationId = CorrelationIdUtil.getCorrelationId();
        log.info("Received request to search customers for brand: {}. Correlation ID: {}",
                brandStr, correlationId);

        Brand brand = Brand.fromString(brandStr);

        Map<String, String> queryParams = new LinkedHashMap<>();
        if (firstName != null && !firstName.trim().isEmpty()) queryParams.put("first_name", firstName);
        if (lastName != null && !lastName.trim().isEmpty()) queryParams.put("last_name", lastName);
        if (loyaltyId != null && !loyaltyId.trim().isEmpty()) queryParams.put("loyalty_id", loyaltyId);
        if (postalCode != null && !postalCode.trim().isEmpty()) queryParams.put("postal_code", postalCode);
        if (affiliation != null && !affiliation.trim().isEmpty()) queryParams.put("affiliation", affiliation);
        if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) queryParams.put("date_of_birth", dateOfBirth);
        if (email != null && !email.trim().isEmpty()) queryParams.put("email", email);
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) queryParams.put("phone_number", phoneNumber);

        Object result = customerService.searchCustomers(brand, queryParams);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Correlation-Id", correlationId);

        log.info("Successfully processed customer search request. Correlation ID: {}", correlationId);
        return ResponseEntity.ok().headers(headers).body(result);
    }
}
