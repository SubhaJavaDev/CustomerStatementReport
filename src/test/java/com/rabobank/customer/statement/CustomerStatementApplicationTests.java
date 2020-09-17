package com.rabobank.customer.statement;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rabobank.customer.statement.configuration.CSVJobConfiguration;

@SpringBatchTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes = { CSVJobConfiguration.class })
class CustomerStatementApplicationTests {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;


	@After
	public void cleanUp() {
		jobRepositoryTestUtils.removeJobExecutions();
	}


	private JobParameters defaultJobParameters() {
		JobParametersBuilder paramsBuilder = new JobParametersBuilder();

		paramsBuilder.addLong("time", System.currentTimeMillis());
		paramsBuilder.addString("csvFilename", "records.csv");
		return paramsBuilder.toJobParameters();
	}


	@Test
	public void EndToEndTest() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
		JobInstance actualJobInstance = jobExecution.getJobInstance();
		ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

		assertEquals(actualJobInstance.getJobName(), "csvFileHandlerJob");
		assertEquals(actualJobExitStatus.getExitCode(), "COMPLETED");
	}

	@Test
	public void stepTest() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("csvStep");
		assertNotNull(jobExecution);
	}

	@Test
	public void stepExecutionTest() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("csvStep", defaultJobParameters());
		Collection<StepExecution> actualStepExecutionSize = jobExecution.getStepExecutions();
		ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

		assertEquals(actualStepExecutionSize.size(), 1);
		assertEquals(actualJobExitStatus.getExitCode(), "COMPLETED");

	}

}
