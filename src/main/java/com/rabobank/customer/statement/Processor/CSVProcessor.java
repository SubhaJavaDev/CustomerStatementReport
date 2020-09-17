package com.rabobank.customer.statement.Processor;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.rabobank.customer.exception.FileHandlerException;
import com.rabobank.customer.statement.DTO.CustomerStatementRecords;

public class CSVProcessor implements ItemProcessor<CustomerStatementRecords, CustomerStatementRecords> {

	private static final Logger log = LoggerFactory.getLogger(CSVProcessor.class);

	@Override
	public CustomerStatementRecords process(final CustomerStatementRecords customerRecords)
			throws FileHandlerException {
		try {
			log.info("CSVFileHandler Step enters ItemProcessor");
			String comments = "";
			final double startBalance = customerRecords.getStartBalance();
			final double mutation = customerRecords.getMutation();
			final BigDecimal x = new BigDecimal(startBalance);
			final BigDecimal y = new BigDecimal(mutation);
			final double actualEndBal = x.add(y).setScale(2, RoundingMode.HALF_UP).doubleValue();
			if (actualEndBal != customerRecords.getEndBalance()) {
				comments = "End Balance is incorrect";

			}
			final CustomerStatementRecords recordswithComments = new CustomerStatementRecords(
					customerRecords.getReference(), customerRecords.getAccountNumber(),
					customerRecords.getDescription(), customerRecords.getStartBalance(), customerRecords.getMutation(),
					customerRecords.getEndBalance(), comments);

			log.info("Added comments for cutomerRecords");
			return recordswithComments;
		} catch (Exception e) {
			throw new FileHandlerException("Unable to process csv file ", e);
		}
	}

}
