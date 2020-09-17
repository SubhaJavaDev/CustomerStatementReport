package com.rabobank.customer.statement.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.rabobank.customer.statement.DTO.CustomerStatementRecords;
import com.rabobank.customer.statement.Listener.StepListener;
import com.rabobank.customer.statement.Processor.CSVProcessor;
import com.rabobank.customer.statement.Writer.CSVWriter;

@Configuration
@EnableBatchProcessing
public class CSVJobConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	private static final Logger log = LoggerFactory.getLogger(CSVProcessor.class);
	

	@Bean
	public Job csvFileHandlerJob() {
		log.info("CSVFileHandlerJob Execution Begins");
		return jobBuilderFactory.get("csvFileHandlerJob").incrementer(new RunIdIncrementer()).listener(listener())
				.flow(csvStep()).end().build();
	}

//CSV for processing csv file and generating the failure reports as output.txt
	@Bean("csvStep")
	public Step csvStep() {
		log.info("CSVFileHandler Step Execution Begins");
		return stepBuilderFactory.get("csvStep").<CustomerStatementRecords, CustomerStatementRecords>chunk(1000)
				.reader(csvReader(null)).processor(csvProcessor()).writer(csvWriter())
				.listener(getCSVStepListener())
				.build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<CustomerStatementRecords> csvReader(@Value("#{jobParameters[csvFilename]}") String filename) {
		log.info("CSVFileHandler Step entered ItemReader");
		return new FlatFileItemReaderBuilder<CustomerStatementRecords>().name("customerStatementRecordsReader")
				.resource(new ClassPathResource(filename)).linesToSkip(1).delimited().names(new String[] { "reference",
						"accountNumber", "description", "startBalance", "mutation", "endBalance" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<CustomerStatementRecords>() {
					{
						setTargetType(CustomerStatementRecords.class);
					}
				}).build();

	}

	@Bean("CSVStepListener")
	public StepListener getCSVStepListener() {
		return new StepListener("records.csv");
	}

	@Bean
	public CSVProcessor csvProcessor() {
		return new CSVProcessor();
	}

	@Bean
	public ItemWriter<CustomerStatementRecords> csvWriter() {
		return new CSVWriter();
	}

	@Bean("csvListener")
	public JobExecutionListener listener() {
		return new SpringBatchJobCompletionListener();
	}
}
