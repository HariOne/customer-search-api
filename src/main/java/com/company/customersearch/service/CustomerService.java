package com.company.customersearch.service;

import com.company.customersearch.enums.Brand;
import com.company.customersearch.model.CustomerSearchResponse;

import java.util.Map;

public interface CustomerService {
    CustomerSearchResponse searchCustomers(Brand brand, Map<String, String> filters);
}
