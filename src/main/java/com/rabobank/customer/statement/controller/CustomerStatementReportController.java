package com.rabobank.customer.statement.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerStatementReportController {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job statementReportJob;

	@GetMapping("/customerFailRecordsGeneration")
	public String generateReport() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(statementReportJob, jobParameters);
		return "Batch job has been invoked and check the generated reports at target/test-outputs path";
	}

}
