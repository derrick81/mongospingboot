package com.mongodb.dc.demo.mongospringboot.mongospingboot.controller;

import java.util.List;

import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Account;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.BalanceStatsAggregate;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Account.AccountType;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Account.TransactionBrief;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.repository.AccountRepository;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.repository.AccountTemplateRepository;

import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private AccountTemplateRepository accountTemplateRepo;

    // Spring Boot native - CREATE
    @PostMapping("/accounts")
    Account addAccount(@RequestBody Account newAccount){
        return accountRepo.save(newAccount);
    }

    // Spring Boot native - READ
    @GetMapping("/accounts/{accountNo}")
    Account getAccountByAccountNo(@PathVariable String accountNo){
        return accountRepo.findByAccountNo(accountNo);
    }    

    // Spring Boot native - Update
    @PutMapping("/accounts/{accountNo}/branchCode")
    Account udpateAccountBranchcode(@PathVariable String accountNo, 
                                    @RequestParam int currCode, @RequestParam int newCode){
        Account account = accountRepo.findByAccountNo(accountNo);
        if(account != null && account.getBranchCode()==currCode){
            account.setBranchCode(newCode);
            return accountRepo.save(account);
        }
        return null;
    }

    // Spring Boot native - Delete
    @DeleteMapping("/accounts/{accountNo}")
    String deleteAccount(@PathVariable String accountNo){
        Account account = accountRepo.findByAccountNo(accountNo);
        if(account != null){
            accountRepo.delete(account);
            return "Account deleted succcessfully";
        }
        return "Unable to delete account";
    }

    // Mix Spring Boot native and MongoTemplate
    @PostMapping("/accounts/{accountNo}/transactionhistory")
    Account addTransactionToHistory(@PathVariable String accountNo, @RequestBody TransactionBrief transactionBrief){
        if(accountTemplateRepo.addTransactionToHistory(accountNo, transactionBrief))
        {
            return accountRepo.findByAccountNo(accountNo);
        }
        return null;
    }

    // MongoTemplate - Basic aggregation
    @GetMapping("/accounts/balancestats")
    List<BalanceStatsAggregate> getBalanceStats(){
        return accountTemplateRepo.getBalanceStatsByAccountType();
    }

    @GetMapping("/accounts/setupdemo")
    Account[] setupDemoAccounts(){
        //Cleanup
        Account[] old1 = accountRepo.findAccountsByCustomerId("1000");
        Account[] old2 = accountRepo.findAccountsByCustomerId("1001");
        if(old1 !=null && old1.length>0){
            for (Account a : old1) {
                accountRepo.delete(a);
            }
        }
        if(old2 !=null && old2.length>0){
            for (Account a : old2) {
                accountRepo.delete(a);
            }
        }

        //Construct new demo accounts
        Account[] demoAccounts = new Account[2];
        Account a1 = new Account(AccountType.SAVINGS, 99, 
                                 new Decimal128(100000L),
                                "1000", "Sheldon", "Cooper");
        Account a2 = new Account(AccountType.SAVINGS, 99,
                                 new Decimal128(100000L),
                                "1001", "Leonard", "Hofstadter");
        demoAccounts[0] = accountRepo.save(a1);
        demoAccounts[1] = accountRepo.save(a2);

        return demoAccounts;
    }
}
