package com.wrkspot.assessment.service;

import com.wrkspot.assessment.model.Customer;
import com.wrkspot.assessment.model.CustomerFilters;
import com.wrkspot.assessment.model.CustomerResponse;
import com.wrkspot.assessment.util.CustomPageableRequest;

import java.util.List;

public interface CustomerService {

    List<Customer> createCustomer(List<Customer> customer);

    CustomerResponse searchCustomer(CustomerFilters customerFilters, CustomPageableRequest customPageableRequest);

}
