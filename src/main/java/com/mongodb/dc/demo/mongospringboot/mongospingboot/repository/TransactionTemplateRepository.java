package com.mongodb.dc.demo.mongospringboot.mongospingboot.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.mongodb.MongoException;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Account;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.DailyCurrencyTotalAggregate;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Transaction;

import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SetWindowFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.SetWindowFieldsOperation.ComputedField;
import org.springframework.data.mongodb.core.aggregation.SetWindowFieldsOperation.WindowOutput;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class TransactionTemplateRepository {
    
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MongoTransactionManager transactionManager;
    
    // Multi-Document Transaction
    public Transaction addTransaction(Transaction transaction) {
        mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        return transactionTemplate.execute(new TransactionCallback<Transaction>() {

            @Override
            public Transaction doInTransaction(TransactionStatus status) {
                Account fromAccount = mongoTemplate.findAndModify(
                    new Query(Criteria.where("accountNo").is(transaction.getFromAccount().getAccountNo())),
                    new Update().inc("balance",
                            new Decimal128(transaction.getAmount().bigDecimalValue().multiply(new BigDecimal("-1")))),
                    Account.class);
                if (fromAccount == null)
                    throw new MongoException("Transaction failed modifying fromAccount");

                Account toAccount = mongoTemplate.findAndModify(
                        new Query(Criteria.where("accountNo").is(transaction.getToAccount().getAccountNo())),
                        new Update().inc("balance", transaction.getAmount()),
                        Account.class);
                if (toAccount == null)
                    throw new MongoException("Transaction failed modifying toAccount");

                transaction.getFromAccount().setCustomerName(fromAccount.getCustomer().getFirstName(),
                                                            fromAccount.getCustomer().getLastName());
                transaction.getToAccount().setCustomerName(toAccount.getCustomer().getFirstName(),
                                                        toAccount.getCustomer().getLastName());
                transaction.setTimestamp(LocalDateTime.now());
                Transaction savedTransaction = mongoTemplate.insert(transaction);
                if (savedTransaction == null)
                    throw new MongoException("Transaction failed inserting transaction");
                return savedTransaction;
            }
            
        });
    }

    public List<DailyCurrencyTotalAggregate> getDailyCurrencyTotal(){
        /*
            [{$setWindowFields: {
                partitionBy: '$currency',
                sortBy: {
                    timestamp: 1
                },
                output: {
                    dailyTotal: {
                        $sum: '$amount',
                        window: {
                            range: ['unbounded','current'],
                            unit: 'day'
                        }
                    }
                }
            }}, {$project: {
                    _id: 0,
                    currency: 1,
                    dailyTotal: 1,
                    date: {
                        $dateTrunc: {
                            date: '$timestamp',
                            unit: 'day'
                        }
                    }
                }}]
        */

        MongoExpression outputPathExpression = 
            MongoExpression.create("{$sum: '$amount', window:{range: ['unbounded', 'current'], unit: 'day'}}");
        //MongoExpression.create("{$sum:'$amount', window:{range:['unbounded','current'], unit:'day'}}");    
        ComputedField computedField = new ComputedField("dailyTotal", AggregationExpression.from(outputPathExpression));
        SetWindowFieldsOperation setWindowFieldsOperation = SetWindowFieldsOperation.builder()
                            .partitionByField("currency")
                            .sortBy("timestamp")
                            .output(new WindowOutput(computedField))
                            .build();

        MongoExpression dateTruncExpression = 
                MongoExpression.create("{$dateTrunc: {date: '$timestamp', unit: 'day'}}");
        ProjectionOperation projectionOperation = new ProjectionOperation()
                .andExclude("_id")
                .andInclude("currency", "dailyTotal")
                .and(AggregationExpression.from(dateTruncExpression)).as("date"); 

        Aggregation aggregation = Aggregation.newAggregation(setWindowFieldsOperation, projectionOperation);
        return mongoTemplate.aggregate(aggregation, Transaction.class, DailyCurrencyTotalAggregate.class).getMappedResults();
    }
}
