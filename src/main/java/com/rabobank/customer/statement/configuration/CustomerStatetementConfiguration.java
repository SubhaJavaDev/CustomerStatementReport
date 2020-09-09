package com.rabobank.customer.statement.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.rabobank.customer.statement.DTO.CustomerStatementDTO;
import com.rabobank.customer.statement.DTO.CustomerStatementRecords;
import com.rabobank.customer.statement.Processor.CSVProcessor;
import com.rabobank.customer.statement.Processor.XMLProcessor;
import com.rabobank.customer.statement.Writer.CSVWriter;
import com.rabobank.customer.statement.Writer.XMLWriter;

@Configuration
public class CustomerStatetementConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job statementReportJob() {
		return jobBuilderFactory.get("statementReportJob").incrementer(new RunIdIncrementer()).listener(listener())
				.flow(step1()).next(step2()).end().build();
	}

	private int getLines(String fileFormat) {
		int lines = 0;
		try {
			if (fileFormat.equals("csv")) {
				lines = (int) Files.lines(Paths.get("src/main/resources/records.csv")).parallel().count();
			} else {
				lines = (int) Files.lines(Paths.get("src/main/resources/records.xml")).parallel().count();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

//step1 for processing csv file and generating the failure reports as output.txt
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<CustomerStatementRecords, CustomerStatementRecords>chunk(getLines("csv")).reader(csvReader())
				.processor(csvProcessor()).writer(csvWriter()).build();
	}

	@Bean
	public FlatFileItemReader<CustomerStatementRecords> csvReader() {
		return new FlatFileItemReaderBuilder<CustomerStatementRecords>().name("customerStatementRecordsReader")
				.resource(new ClassPathResource("records.csv")).linesToSkip(1).delimited().names(new String[] {
						"reference", "accountNumber", "description", "startBalance", "mutation", "endBalance" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<CustomerStatementRecords>() {
					{
						setTargetType(CustomerStatementRecords.class);
					}
				}).build();
	}

	@Bean
	public CSVProcessor csvProcessor() {
		return new CSVProcessor();
	}

	@Bean
	public ItemWriter<CustomerStatementRecords> csvWriter() {
		return new CSVWriter();
	}

	// step2 for processing xml file and generating the failure reports as
	// XMLoutput.txt
	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").<CustomerStatementDTO, CustomerStatementDTO>chunk(getLines("xml"))
				.reader(xmlReader()).processor(xmlProcessor()).writer(xmlWriter()).build();
	}

	@Bean
	public ItemWriter<CustomerStatementDTO> xmlWriter() {
		return new XMLWriter();
	}

	@Bean
	public XMLProcessor xmlProcessor() {
		return new XMLProcessor();
	}

	@Bean
	public ItemReader<CustomerStatementDTO> xmlReader() {
		StaxEventItemReader<CustomerStatementDTO> xmlFileReader = new StaxEventItemReader<>();
		xmlFileReader.setResource(new ClassPathResource("records.xml"));
		xmlFileReader.setFragmentRootElementName("record");

		Jaxb2Marshaller customerJaxb2Marshaller = new Jaxb2Marshaller();
		customerJaxb2Marshaller.setClassesToBeBound(CustomerStatementDTO.class);
		xmlFileReader.setUnmarshaller(customerJaxb2Marshaller);
		return xmlFileReader;
	}

	@Bean
	public JobExecutionListener listener() {
		return new SpringBatchJobCompletionListener();
	}
}
