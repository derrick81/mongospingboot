package com.mongodb.dc.demo.mongospringboot.mongospingboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableMongoRepositories //https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo-repo-usage
public class MongospingbootApplication {
	public static void main(String[] args) {
		SpringApplication.run(MongospingbootApplication.class, args);
	}
}
