package com.wrkspot.assessment.repository;

import com.wrkspot.assessment.entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<CustomerEntity, String>, JpaRepository<CustomerEntity, String>, JpaSpecificationExecutor<CustomerEntity> {


}