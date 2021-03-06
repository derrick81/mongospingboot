# Mongo Springboot
This demo application is used to show how MongoDB can be used with Springboot.

## Running the application
Under src/main/resources add a new file named application.properties.
application.properties should contain at least 2 settings properties:
1. spring.data.mongodb.uri - this should contain the connection string. Example format would be mongodb+srv://<username>:<password>@<domain_name>/mongosb?retryWrites=true&w=majority. For details on how to create a free Atlas cluster to get this application running and how to connect to your free cluster, please see https://docs.atlas.mongodb.com/tutorial/deploy-free-tier-cluster/.
2. spring.data.mongodb.database - this should contain the name of the database. In this demo, the value "mongosb" is used.

Your application.properties should look minimally like this:
```
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<domain_name>/mongosb?retryWrites=true&w=majority
spring.data.mongodb.database=mongosb
```
## Demo scenarios
A walkthrough of the following demo scenarios can be easily done by using the included MongoSB.postman_collection.json script. You will first need to import this included script into Postman. 

### Using Springboot native CRUD
Run the following scripts:
1. Native_AddAccount
2. Native_AccountByAccountNo
3. Native_UpdateBranchCode
4. Native_DeleteAccount

The above will let you experience how to make use of the native Springboot respository methods like save() and delete() to perform CRUD with MongoDB. Do note that the account number used in scripts 2 to 4 will come from the successful execution of script 1.

### Mixed Springboot native with MongoTemplate
Run the following scripts:
1. Pre_SetupDemoAccounts
2. Mixed_AddTransactionToHistory

First do a setup by running script 1. After that, run script 2 10 times, changing the value in amount from 1 to 10. You will see that the transaction history is continually added to lastTenTransactions array in a reverse chronological order. Subsequently run it one more time using the value 11 to see how the oldest transaction will automatically be removed.

### MongoDB Transactions with Springboot using MongoTemplate
Run the following scripts:
1. Pre_SetupDemoAccounts
2. Template_AddNewTransaction

Use the two accounts created in script 1 to transfer from one account to another. Behind the scenes this transfer is done within a single transaction session to reduce the amount from the payer, increase the amount to the payee and create a transaction record.

### MongoDB Aggregation Framework with Springboot using MongoTemplate
1. Pre_Populate
2. Template_GetBalanceStats
3. Template_DailyCurrencyTotal

Run script 1 to populate the database (which you can first drop to start afresh) with 100 accounts and 1000 transactions all randomly generated. Run script 2 to see how to use aggregation to easily perform some basic analytical computations such as finding the min, max, average and standard deviation. Run script 3 to see a deeper usage of aggregation framework to do time series based analysis.