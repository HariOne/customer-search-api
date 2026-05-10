package com.company.customersearch.controller;

import com.company.customersearch.enums.Brand;
import com.company.customersearch.model.CustomerSearchResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Customer", description = "Customer Search API")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{brand}/customers")
    @Operation(summary = "Search customers by brand",
            description = "Search for customers in a specific brand using various filter criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerSearchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid brand provided"),
            @ApiResponse(responseCode = "502", description = "External API error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerSearchResponse> searchCustomers(
            @PathVariable
            @Parameter(description = "Brand name (Zoomcar, Revv, MyChoize, Myles)")
            String brand,
            @RequestParam(required = false)
            @Parameter(description = "Customer first name")
            String first_name,
            @RequestParam(required = false)
            @Parameter(description = "Customer last name")
            String last_name,
            @RequestParam(required = false)
            @Parameter(description = "Customer loyalty ID")
            String loyalty_id,
            @RequestParam(required = false)
            @Parameter(description = "Customer postal code")
            String postal_code,
            @RequestParam(required = false)
            @Parameter(description = "Customer affiliation")
            String affiliation,
            @RequestParam(required = false)
            @Parameter(description = "Customer date of birth (YYYY-MM-DD)")
            String date_of_birth,
            @RequestParam(required = false)
            @Parameter(description = "Customer email")
            String email,
            @RequestParam(required = false)
            @Parameter(description = "Customer phone number")
            String phone_number,
            @RequestHeader(value = "Correlation-Id", required = false)
            String correlationId) {

        String correlationIdValue = CorrelationIdUtil.getCorrelationId();
        log.info("Received search request for brand: {} with correlationId: {}", brand, correlationIdValue);

        Brand brandEnum = Brand.fromString(brand);

        Map<String, String> filters = new HashMap<>();
        if (first_name != null && !first_name.isEmpty()) filters.put("first_name", first_name);
        if (last_name != null && !last_name.isEmpty()) filters.put("last_name", last_name);
        if (loyalty_id != null && !loyalty_id.isEmpty()) filters.put("loyalty_id", loyalty_id);
        if (postal_code != null && !postal_code.isEmpty()) filters.put("postal_code", postal_code);
        if (affiliation != null && !affiliation.isEmpty()) filters.put("affiliation", affiliation);
        if (date_of_birth != null && !date_of_birth.isEmpty()) filters.put("date_of_birth", date_of_birth);
        if (email != null && !email.isEmpty()) filters.put("email", email);
        if (phone_number != null && !phone_number.isEmpty()) filters.put("phone_number", phone_number);

        CustomerSearchResponse response = customerService.searchCustomers(brandEnum, filters);
        response.setCorrelationId(correlationIdValue);

        return ResponseEntity.ok(response);
    }
}
