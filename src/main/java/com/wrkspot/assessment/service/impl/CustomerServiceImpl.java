package com.wrkspot.assessment.service.impl;

import com.wrkspot.assessment.exception.GenericException;
import com.wrkspot.assessment.model.*;
import com.wrkspot.assessment.entities.Address;
import com.wrkspot.assessment.entities.CustomerEntity;
import com.wrkspot.assessment.repository.CustomerRepository;

import com.wrkspot.assessment.service.CustomerService;
import com.wrkspot.assessment.util.CustomPageableRequest;
import com.wrkspot.assessment.util.CustomerSpecificationUtils;
import com.wrkspot.assessment.util.PageModelGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private  KafkaMessagingService kafkaMessagingService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PageModelGenerator<CustomerEntity> pageModelGenerator;

    @Override
    public List<Customer> createCustomer(List<Customer> customer) {
        logger.info("creating customer {}", customer);
        List<Customer> customerList = new ArrayList<>();
        try {

            List<CustomerEntity> customerEntityList = new ArrayList<>();
            customer.forEach(customer1 -> customerEntityList.add(entityFromRequest(customer1)));
            customerRepository.saveAll(customerEntityList).forEach(
                    savedCustomer -> {
                        kafkaMessagingService.sendMessage("NEW_CUSTOMER", savedCustomer);
                        customerList.add(responseFromEntity(savedCustomer));
                    }
            );
        } catch (Exception e) {
            logger.error("Error creating customer", e);
            throw new GenericException();
        }
        return customerList;
    }

    @Override
    public CustomerResponse searchCustomer(CustomerFilters customerFilters, CustomPageableRequest customPageableRequest) {
        logger.info("searching  customers with filters {}", customerFilters);

        CustomerResponse customerResponse = new CustomerResponse();
        PagedModel<CustomerEntity> pagedModel = null;
        List<Customer> customerList = new ArrayList<>();

        try {

            Specification<CustomerEntity> rewardSpecification = CustomerSpecificationUtils.withCustomerFilter(customerFilters);
            Page<CustomerEntity> customersPage = customerRepository.findAll(rewardSpecification, customPageableRequest);
            pagedModel = pageModelGenerator.getPageModel(customersPage);
            customerResponse.setTotal(customersPage.getTotalElements());
            customersPage.forEach(customerEntity -> customerList.add(responseFromEntity(customerEntity)));
            customerResponse.setItems(customerList);

            pagedModel.getLinks().forEach(link -> {
                if (link.getRel().toString().equals("prev")) {
                    customerResponse.setPrev(link.getHref());

                }
                if (link.getRel().toString().equals("first")) {
                    customerResponse.setFirst(link.getHref());

                }
                if (link.getRel().toString().equals("self")) {
                    customerResponse.setSelf(link.getHref());

                }
                if (link.getRel().toString().equals("last")) {
                    customerResponse.setLast(link.getHref());

                }
                if (link.getRel().toString().equals("next")) {
                    customerResponse.setNext(link.getHref());

                }
            });


        } catch (Exception e) {

            logger.error("Error fetching customer", e);
            throw new GenericException();

        }
        return customerResponse;
    }


    private CustomerEntity entityFromRequest(Customer customer) {

        List<Address> addressList = new ArrayList<>();
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setAge(customer.customer.getAge());
        customerEntity.setFirstName(customer.customer.getFirstName());
        customerEntity.setLastName(customer.customer.getLastName());
        customerEntity.setMobileNumber(customer.customer.getMobileNumber());
        customerEntity.setSpendingLimit(customer.customer.getSpendingLimit());

        customer.getCustomer().getAddress().forEach(address -> {
            Address address1 = new Address();
            address1.setAddress1(address.getAddress1());
            address1.setCity(address.getCity());
            address1.setAddress2(address.getAddress2());
            address1.setType(address.getType());
            address1.setZipCode(address.getZipCode());
            address1.setState(address.getState());
            addressList.add(address1);
        });

        customerEntity.setAddress(addressList);

        return customerEntity;
    }


    private Customer responseFromEntity(CustomerEntity customerEntity) {

        Customer customer = new Customer();
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerId(customerEntity.getCustomerId().toString());
        customerDetails.setAge(customerEntity.getAge());
        customerDetails.setMobileNumber(customerEntity.getMobileNumber());
        customerDetails.setSpendingLimit(customerEntity.getSpendingLimit());
        customerDetails.setFirstName(customerEntity.getFirstName());
        customerDetails.setLastName(customerEntity.getLastName());
        List<CustomerAddress> customerAddressList = new ArrayList<>();
        customerEntity.getAddress().forEach(address -> {
            CustomerAddress customerAddress = new CustomerAddress();
            customerAddress.setAddress1(address.getAddress1());
            customerAddress.setType(address.getType());
            customerAddress.setCity(address.getCity());
            customerAddress.setAddress2(address.getAddress2());
            customerAddress.setZipCode(address.getZipCode());
            customerAddress.setState(address.getState());
            customerAddressList.add(customerAddress);
        });

        customerDetails.setAddress(customerAddressList);

        customer.setCustomer(customerDetails);
        return customer;
    }

}
