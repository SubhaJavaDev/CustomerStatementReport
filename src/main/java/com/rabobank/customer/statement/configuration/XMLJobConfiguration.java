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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.rabobank.customer.statement.DTO.CustomerStatementDTO;
import com.rabobank.customer.statement.Listener.StepListener;
import com.rabobank.customer.statement.Processor.XMLProcessor;
import com.rabobank.customer.statement.Writer.XMLWriter;

@Configuration
@EnableBatchProcessing
public class XMLJobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	@Value("${XMLFile}")
	String xmlFilename;
	private static final Logger log = LoggerFactory.getLogger(XMLJobConfiguration.class);

	@Bean
	public Job xmlFileHandlerJob() {
		log.info("XMLFileHandlerJob Execution Begins");
		return jobBuilderFactory.get("xmlFileHandlerJob").incrementer(new RunIdIncrementer()).listener(listener())
				.flow(xmlStep()).end().build();
	}

// xmlStep for processing xml file and generating the failure reports as XMLoutput.txt

	@Bean("xmlStep")
	public Step xmlStep() {
		log.info("XMLFileHandler Step Execution Begins");
		return stepBuilderFactory.get("xmlStep").<CustomerStatementDTO, CustomerStatementDTO>chunk(1000)
				.reader(xmlReader()).processor(xmlProcessor()).writer(xmlWriter()).listener(getXMLStepListener())
				.build();

	}

	@Bean("XMLStepListener")
	public StepListener getXMLStepListener() {
		return new StepListener(xmlFilename);
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
		log.info("XMLFileHandler Step entered ItemReader");
		StaxEventItemReader<CustomerStatementDTO> xmlFileReader = new StaxEventItemReader<>();
		xmlFileReader.setResource(new ClassPathResource(xmlFilename));
		xmlFileReader.setFragmentRootElementName("record");

		Jaxb2Marshaller customerJaxb2Marshaller = new Jaxb2Marshaller();
		customerJaxb2Marshaller.setClassesToBeBound(CustomerStatementDTO.class);
		xmlFileReader.setUnmarshaller(customerJaxb2Marshaller);
		return xmlFileReader;
	}

	@Bean("xmlListener")
	public JobExecutionListener listener() {
		return new SpringBatchJobCompletionListener();
	}
}
