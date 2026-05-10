package com.company.customersearch.service;

import com.company.customersearch.enums.Brand;
import java.util.Map;

public interface CustomerService {

    Object searchCustomers(Brand brand, Map<String, String> queryParams);
}
