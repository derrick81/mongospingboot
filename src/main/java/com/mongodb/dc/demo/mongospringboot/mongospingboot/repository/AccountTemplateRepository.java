package com.mongodb.dc.demo.mongospringboot.mongospingboot.repository;

import java.util.List;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Account;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.BalanceStatsAggregate;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Account.TransactionBrief;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class AccountTemplateRepository {
    
    @Autowired
    MongoTemplate mongoTemplate;
    
    // Array update
    public boolean addTransactionToHistory(String accountNo, TransactionBrief transactionBrief){
        Query query = new Query(Criteria.where("accountNo").is(accountNo));
        Update update = new Update();
        update.push("lastTenTransactions").atPosition(0).slice(10).each(transactionBrief);

        UpdateResult result = mongoTemplate.updateFirst(query, update, Account.class);
        if(result != null && result.getMatchedCount() == 1 && result.getModifiedCount() == 1){
            return true;
        }
        return false;
    }

    //Average balance by account type
    public List<BalanceStatsAggregate> getBalanceStatsByAccountType(){
        /*
            [{$group: {
                _id: '$type',
                averageBalance: {
                    $avg: '$balance'
                },
                maxBalance: {
                    $max: '$balance'
                },
                minBalance: {
                    $min: '$balance'
                },
                stdDeviation: {
                    $stdDevPop: '$balance'
                }
            }}]
        */
        GroupOperation groupOperation = new GroupOperation(Fields.fields("type"))
                                            .avg("balance").as("average")
                                            .min("balance").as("minimum")
                                            .max("balance").as("maximum")
                                            .stdDevPop("balance").as("stdDeviation");
        Aggregation aggregation = Aggregation.newAggregation(groupOperation);        
        return mongoTemplate.aggregate(aggregation, Account.class, BalanceStatsAggregate.class).getMappedResults();
    }
}
