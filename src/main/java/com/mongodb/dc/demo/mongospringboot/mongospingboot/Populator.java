package com.mongodb.dc.demo.mongospringboot.mongospingboot;

import java.time.LocalDateTime;

import com.github.javafaker.Faker;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Account;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Transaction;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Account.AccountType;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.repository.AccountRepository;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.repository.TransactionRepository;

import org.bson.types.Decimal128;

public class Populator {

    // Singleton implementation
    private static Populator instance;
    private static Faker faker;
    private Populator(){}

    public static Populator instance(){
        if(instance==null){
            instance = new Populator();
            faker = new Faker();
        }
        return instance;
    }

    public Account[] populateAccounts(AccountRepository accountRepo, int numAccounts){
        Account[] accounts = new Account[numAccounts];

        System.out.println("-------------CREATING " + numAccounts + " ACCOUNTS-------------------------------\n");
        for(int i=0; i<numAccounts; i++){
            Account a = new Account(getRandomAccountType(), 
                                    faker.random().nextInt(100, 129).intValue(), 
                                    new Decimal128(faker.number().numberBetween(1L, 99999999L)),
                                    Long.toString(faker.number().numberBetween(100000L, 999999L)), 
                                    faker.name().firstName(), 
                                    faker.name().lastName());
            accounts[i] = accountRepo.save(a);
        }
        System.out.println("-------------" + numAccounts + " ACCOUNTS CREATED-------------------------------\n");

        return accounts;
    }

    private AccountType getRandomAccountType(){
        int n = faker.random().nextInt(100) % 6;
        switch(n){
            case 1:
                return AccountType.CREDIT;
            case 2:
                return AccountType.CURRENT;
            case 3:
                return AccountType.DEBIT;
            case 4:
                return AccountType.INVESTMENT;
            case 5:
                return AccountType.LOAN;
            default:
                return AccountType.SAVINGS;
        }
    }

    public void populateTransactions(TransactionRepository transactionRepo, Account[] accounts, int numTransactions){
        System.out.println("-------------CREATING " + numTransactions + " TRANSACTIONS-------------------------------\n");
        for(int i=0; i<numTransactions; i++){
            int from = faker.random().nextInt(0, accounts.length-1);
            int to = faker.random().nextInt(0, accounts.length-1);
            Transaction t = new Transaction(
                                    accounts[from].getCustomer().getId(), 
                                    accounts[from].getCustomer().getFirstName() + " " + accounts[from].getCustomer().getLastName(), 
                                    accounts[to].getCustomer().getId(), 
                                    accounts[to].getCustomer().getFirstName() + " " + accounts[to].getCustomer().getLastName(), 
                                    new Decimal128(faker.number().numberBetween(1L, 9999L)), 
                                    faker.currency().code(),
                                    LocalDateTime.now().minusDays(faker.number().numberBetween(0L, 9L)));
            transactionRepo.save(t);
        }
        System.out.println("-------------" + numTransactions + " TRANSACTIONS CREATED-------------------------------\n");
    }
}
