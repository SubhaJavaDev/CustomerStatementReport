package com.rabobank.customer.statement.Listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.core.io.ClassPathResource;

import com.rabobank.customer.exception.FileHandlerException;
import com.rabobank.customer.exception.ResourceNotFoundException;

public class StepListener implements StepExecutionListener {
	private String filename;
	public StepListener(String filename) {
		this.filename = filename;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		try {
			
			ClassPathResource sp = new ClassPathResource(filename);

			if (!sp.exists())
				throw new ResourceNotFoundException("Resource not found in the specified location");
		} catch (Exception e) {
			throw new FileHandlerException("File not found in specified location", e);
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		if(stepExecution.getReadCount()==0)
		{
			stepExecution.addFailureException(new Throwable("File is Empty"));
			return ExitStatus.NOOP;
		}
		return null;
	}

}
