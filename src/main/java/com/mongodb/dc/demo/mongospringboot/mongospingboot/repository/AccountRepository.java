package com.mongodb.dc.demo.mongospringboot.mongospingboot.repository;

import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Account;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AccountRepository extends MongoRepository<Account, String> {
    public Account findByAccountNo(String accountNo);

    @Query("{'customer._id': '?0'}")
    public Account[] findAccountsByCustomerId(String customerId);
}
