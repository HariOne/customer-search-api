package com.company.customersearch.service.impl;

import com.company.customersearch.client.ThirdPartyCustomerClient;
import com.company.customersearch.enums.Brand;
import com.company.customersearch.model.Customer;
import com.company.customersearch.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final ThirdPartyCustomerClient thirdPartyCustomerClient;

    public CustomerServiceImpl(ThirdPartyCustomerClient thirdPartyCustomerClient) {
        this.thirdPartyCustomerClient = thirdPartyCustomerClient;
    }

    @Override
    public Object searchCustomers(Brand brand, Map<String, String> queryParams) {
        log.info("Searching customers for brand: {}", brand.getDisplayName());

        Map<String, String> filteredParams = filterAndBuildQueryParams(queryParams);
        log.debug("Filtered query parameters: {}", filteredParams);

        List<Customer> customers = thirdPartyCustomerClient.searchCustomers(filteredParams);
        log.info("Received {} customer records from external API", customers.size());

        if (customers.isEmpty()) {
            log.info("No customers found for brand: {}", brand.getDisplayName());
            return Map.of("message", "No customer records found");
        }

        if (customers.size() == 1) {
            log.info("Single customer found, returning complete details");
            return customers.get(0);
        }

        log.info("Multiple customers found ({}), returning minimal details", customers.size());
        return customers.stream()
                .map(customer -> Map.of(
                        "customerId", customer.getCustomerId(),
                        "firstName", customer.getFirstName(),
                        "lastName", customer.getLastName(),
                        "phoneNumber", customer.getPhoneNumber()
                ))
                .toList();
    }

    private Map<String, String> filterAndBuildQueryParams(Map<String, String> queryParams) {
        Map<String, String> filtered = new HashMap<>();

        if (queryParams != null) {
            if (queryParams.containsKey("first_name") && isNotEmpty(queryParams.get("first_name"))) {
                filtered.put("first_name", queryParams.get("first_name"));
            }
            if (queryParams.containsKey("last_name") && isNotEmpty(queryParams.get("last_name"))) {
                filtered.put("last_name", queryParams.get("last_name"));
            }
            if (queryParams.containsKey("loyalty_id") && isNotEmpty(queryParams.get("loyalty_id"))) {
                filtered.put("loyalty_id", queryParams.get("loyalty_id"));
            }
            if (queryParams.containsKey("postal_code") && isNotEmpty(queryParams.get("postal_code"))) {
                filtered.put("postal_code", queryParams.get("postal_code"));
            }
            if (queryParams.containsKey("affiliation") && isNotEmpty(queryParams.get("affiliation"))) {
                filtered.put("affiliation", queryParams.get("affiliation"));
            }
            if (queryParams.containsKey("date_of_birth") && isNotEmpty(queryParams.get("date_of_birth"))) {
                filtered.put("date_of_birth", queryParams.get("date_of_birth"));
            }
            if (queryParams.containsKey("email") && isNotEmpty(queryParams.get("email"))) {
                filtered.put("email", queryParams.get("email"));
            }
            if (queryParams.containsKey("phone_number") && isNotEmpty(queryParams.get("phone_number"))) {
                filtered.put("phone_number", queryParams.get("phone_number"));
            }
        }

        return filtered;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
