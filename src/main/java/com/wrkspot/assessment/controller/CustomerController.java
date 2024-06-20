package com.wrkspot.assessment.controller;

import com.wrkspot.assessment.model.Customer;
import com.wrkspot.assessment.model.CustomerFilters;
import com.wrkspot.assessment.service.CustomerService;
import com.wrkspot.assessment.util.CustomPageableRequest;
import com.wrkspot.assessment.util.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {


    @Autowired
    private CustomerService customerService;


    @PostMapping("/customer")
    public ResponseEntity<?> createCustomer(@RequestBody List<Customer> customerList) {
        RequestValidator.validateCreateCustomerRequest(customerList);
        return new ResponseEntity<>(customerService.createCustomer(customerList), HttpStatus.CREATED);
    }


    @GetMapping("/customer")
    public ResponseEntity<?> searchCustomers(CustomerFilters customerFilters, Pageable pageable) {
        CustomPageableRequest customPageableRequest = new CustomPageableRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return new ResponseEntity<>(customerService.searchCustomer(customerFilters, customPageableRequest), HttpStatus.OK);

    }}
