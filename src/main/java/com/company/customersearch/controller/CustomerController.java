package com.company.customersearch.controller;

import com.company.customersearch.enums.Brand;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Customer Search", description = "Endpoints for searching customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{brand}/customers")
    @Operation(
            summary = "Search customers by brand",
            description = "Search for customers across multiple brands with optional filters")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customers found",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid brand or validation failure"),
            @ApiResponse(
                    responseCode = "502",
                    description = "External API error"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    public ResponseEntity<Object> searchCustomers(
            @PathVariable
            @Parameter(description = "Brand name. Allowed values: Zoomcar, Revv, MyChoize, Myles")
            String brand,

            @RequestParam(required = false)
            @Parameter(description = "First name of the customer")
            String first_name,

            @RequestParam(required = false)
            @Parameter(description = "Last name of the customer")
            String last_name,

            @RequestParam(required = false)
            @Parameter(description = "Loyalty ID of the customer")
            String loyalty_id,

            @RequestParam(required = false)
            @Parameter(description = "Postal code of the customer")
            String postal_code,

            @RequestParam(required = false)
            @Parameter(description = "Affiliation of the customer")
            String affiliation,

            @RequestParam(required = false)
            @Parameter(description = "Date of birth of the customer (YYYY-MM-DD)")
            String date_of_birth,

            @RequestParam(required = false)
            @Parameter(description = "Email of the customer")
            String email,

            @RequestParam(required = false)
            @Parameter(description = "Phone number of the customer")
            String phone_number) {

        String correlationId = CorrelationIdUtil.getCorrelationId();
        log.info("Received search request for brand: {}. Correlation ID: {}", brand, correlationId);

        Brand brandEnum = Brand.fromString(brand);
        log.debug("Validated brand: {}", brandEnum.getDisplayName());

        Map<String, String> queryParams = new HashMap<>();
        if (first_name != null) queryParams.put("first_name", first_name);
        if (last_name != null) queryParams.put("last_name", last_name);
        if (loyalty_id != null) queryParams.put("loyalty_id", loyalty_id);
        if (postal_code != null) queryParams.put("postal_code", postal_code);
        if (affiliation != null) queryParams.put("affiliation", affiliation);
        if (date_of_birth != null) queryParams.put("date_of_birth", date_of_birth);
        if (email != null) queryParams.put("email", email);
        if (phone_number != null) queryParams.put("phone_number", phone_number);

        Object result = customerService.searchCustomers(brandEnum, queryParams);
        log.info("Search completed successfully. Correlation ID: {}", correlationId);

        return ResponseEntity.ok(result);
    }
}
