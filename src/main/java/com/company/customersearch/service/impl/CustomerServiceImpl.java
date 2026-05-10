package com.company.customersearch.service.impl;

import com.company.customersearch.client.ThirdPartyCustomerClient;
import com.company.customersearch.enums.Brand;
import com.company.customersearch.model.Customer;
import com.company.customersearch.model.CustomerSearchResponse;
import com.company.customersearch.model.CustomerSummary;
import com.company.customersearch.service.CustomerService;
import com.company.customersearch.util.CorrelationIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final ThirdPartyCustomerClient thirdPartyCustomerClient;

    public CustomerServiceImpl(ThirdPartyCustomerClient thirdPartyCustomerClient) {
        this.thirdPartyCustomerClient = thirdPartyCustomerClient;
    }

    @Override
    public CustomerSearchResponse searchCustomers(Brand brand, Map<String, String> filters) {
        String correlationId = CorrelationIdUtil.getCorrelationId();
        log.info("Searching customers for brand: {} with filters: {}. Correlation ID: {}", 
                brand.getDisplayName(), filters, correlationId);

        List<Customer> customers = thirdPartyCustomerClient.searchCustomers(brand, filters);

        if (customers == null || customers.isEmpty()) {
            log.info("No customers found for brand: {}. Correlation ID: {}", brand.getDisplayName(), correlationId);
            return CustomerSearchResponse.builder()
                    .message("No records found")
                    .correlationId(correlationId)
                    .build();
        }

        if (customers.size() == 1) {
            log.info("Found 1 customer for brand: {}. Correlation ID: {}", brand.getDisplayName(), correlationId);
            return CustomerSearchResponse.builder()
                    .message("Customer found")
                    .customer(customers.get(0))
                    .correlationId(correlationId)
                    .build();
        }

        log.info("Found {} customers for brand: {}. Correlation ID: {}", customers.size(), brand.getDisplayName(), correlationId);
        List<CustomerSummary> summaries = customers.stream()
                .map(c -> CustomerSummary.builder()
                        .customerId(c.getCustomerId())
                        .firstName(c.getFirstName())
                        .lastName(c.getLastName())
                        .email(c.getEmail())
                        .build())
                .collect(Collectors.toList());

        return CustomerSearchResponse.builder()
                .message("Multiple customers found")
                .customers(summaries)
                .correlationId(correlationId)
                .build();
    }
}
