package com.rabobank.customer.statement;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class CustomerStatementReportApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CustomerStatementReportApplication.class, args);
	}
}
