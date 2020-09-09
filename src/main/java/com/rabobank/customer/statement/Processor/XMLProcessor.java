package com.rabobank.customer.statement.Processor;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.rabobank.customer.statement.DTO.CustomerStatementDTO;

public class XMLProcessor implements ItemProcessor<CustomerStatementDTO, CustomerStatementDTO> {
	private static final Logger log = LoggerFactory.getLogger(XMLProcessor.class);

	@Override
	public CustomerStatementDTO process(CustomerStatementDTO item) throws Exception {
		String comments = "";
		final double startBalance = item.getStartBalance();
		final double mutation = item.getMutation();
		final BigDecimal x = new BigDecimal(startBalance);
		final BigDecimal y = new BigDecimal(mutation);
		final double actualEndBal = x.add(y).setScale(2, RoundingMode.HALF_UP).doubleValue();
		if (actualEndBal != item.getEndBalance()) {
			comments = "End Balance is incorrect";
		}
		final CustomerStatementDTO itemsWithComments = new CustomerStatementDTO(item.getReference(),
				item.getAccountNumber(), item.getDescription(), item.getStartBalance(), item.getMutation(),
				item.getEndBalance(), comments);
		log.info("Converting (" + item + ") into (" + itemsWithComments + ")");
		return itemsWithComments;
	}

}
