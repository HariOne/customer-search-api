package com.company.customersearch.service.impl;

import com.company.customersearch.client.ThirdPartyCustomerClient;
import com.company.customersearch.enums.Brand;
import com.company.customersearch.model.Customer;
import com.company.customersearch.model.NoCustomerFoundResponse;
import com.company.customersearch.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    public Object searchCustomers(Brand brand, Map<String, String> queryParams) {
        log.info("Searching customers for brand: {}", brand.getDisplayName());

        Map<String, String> filteredParams = filterEmptyParams(queryParams);
        List<Customer> customers = thirdPartyCustomerClient.searchCustomers(filteredParams);

        if (customers == null || customers.isEmpty()) {
            log.info("No customers found for brand: {}", brand.getDisplayName());
            return new NoCustomerFoundResponse("No customer records found");
        }

        if (customers.size() == 1) {
            log.info("Single customer found for brand: {}. Customer ID: {}",
                    brand.getDisplayName(), customers.get(0).getCustomerId());
            return customers.get(0);
        }

        log.info("Multiple customers found for brand: {}. Count: {}",
                brand.getDisplayName(), customers.size());
        return customers.stream()
                .map(this::toMinimalCustomer)
                .collect(Collectors.toList());
    }

    private Map<String, String> filterEmptyParams(Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return new HashMap<>();
        }

        return queryParams.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().trim().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Customer toMinimalCustomer(Customer customer) {
        return Customer.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
