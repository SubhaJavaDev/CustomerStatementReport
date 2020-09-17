package com.rabobank.customer.statement.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabobank.customer.exception.FileHandlerException;

@RestController
public class CustomerStatementReportController {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job csvFileHandlerJob;

	@Autowired
	Job xmlFileHandlerJob;
	
	@Value("${CSVFile}")
	String csvFilename;
	

	private static final Logger log = LoggerFactory.getLogger(CustomerStatementReportController.class);

	@GetMapping("/csvFileGeneration")
	public String generateCSVReport() {
		String status = "";
		try {
			JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
					.addString("csvFilename", csvFilename).toJobParameters();
			JobExecution je = jobLauncher.run(csvFileHandlerJob, jobParameters);
			log.info("CsvFileHandlerJob Begins");
			List<Throwable> list = je.getAllFailureExceptions();
			if(list.size()>0)
			{
			for (Throwable throwable : list) {
				if (throwable.getLocalizedMessage().contains("File not found in specified location"))
					return "<h1>File not found in specified location, Please place the file to get the report</h1>";
				else if (throwable.getLocalizedMessage().contains("Parsing error"))
					return "<h1>Incorrect data in input file</h1> ";
			}
			}
			String batchStatus = je.getStatus().toString();
			if (batchStatus == BatchStatus.FAILED.toString())
				return "<h1>Error while reading CSV file</h1>";

			else if (batchStatus == BatchStatus.COMPLETED.toString())
				return "<h1>Batch job has been invoked for csv records and report generated at project path</h1>";
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new FileHandlerException("CSV File Handling Error: ", e);

		}
		return status;

	}

	@GetMapping("/xmlFileGeneration")
	public String generateXMLReport() throws Exception {
		String status = "";
		try {
			JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
					.toJobParameters();
			JobExecution je = jobLauncher.run(xmlFileHandlerJob, jobParameters);
			log.info("XmlFileHandlerJob Begins");
			List<Throwable> list = je.getAllFailureExceptions();
			if(list.size()>0)
			{
			for (Throwable throwable : list) {
				if (throwable.getLocalizedMessage().contains("File not found in specified location"))
					return "<h1>File not found in specified location, Please place the file to get the report</h1>";
				else if (throwable.getLocalizedMessage().contains("Parsing error"))
					return "<h1>Incorrect data in input file</h1> ";
				else if(throwable.getLocalizedMessage().contains("File is Empty"))
					return "<h1>File is empty</h1> ";
					
			}
			}
			String batchStatus = je.getStatus().toString();
			if (batchStatus == BatchStatus.FAILED.toString())
				return "Error while processing XML file";

			else if (batchStatus == BatchStatus.COMPLETED.toString())
				return "Batch job has been invoked for xml records and report generated at project path";
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new FileHandlerException("XML File Handling Error: ", e);
		}
		return status;

	}

}
