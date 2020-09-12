package com.rabobank.customer.statement.Writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import com.rabobank.customer.statement.DTO.CustomerStatementDTO;

public class XMLWriter implements ItemStreamWriter<CustomerStatementDTO> {

	private FileOutputStream fos;

	private BufferedWriter bw;

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		// TODO Auto-generated method stub

		try {
			File file = new File("XMLoutput.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws ItemStreamException {
		if (bw != null) {
			try {
				bw.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void write(List<? extends CustomerStatementDTO> items) throws Exception {
		for (CustomerStatementDTO csr : items) {
			if (!csr.getComments().isEmpty()) {
				try {
					bw.write("Transaction Reference: " + csr.getReference() + " " + "Description "
							+ csr.getDescription() + " Comments: " + csr.getComments());

					bw.newLine();

				} catch (IOException e) {
					System.err.println("Cannot write message");
				}
			}
		}

		Map<Integer, Integer> map = new HashMap<>();

		for (CustomerStatementDTO csr : items) {
			Integer value = map.get(csr.getReference());
			if (map.get(csr.getReference()) == null) {
				map.put(csr.getReference(), 1);
			} else {
				value++;
				map.put(csr.getReference(), value);

			}
		}
		Set<Map.Entry<Integer, Integer>> entrySet = map.entrySet();
		for (Map.Entry<Integer, Integer> entry : entrySet) {
			if (entry.getValue() > 1) {
				System.out.println(entry.getKey() + "=>" + entry.getValue());
				for (CustomerStatementDTO csr : items) {
					if (csr.getReference() == entry.getKey()) {
						try {
							bw.write("Transaction Reference: " + entry.getKey() + " " + "Description "
									+ csr.getDescription() + " Comments: " + "Duplicate Records");
							bw.newLine();

						} catch (IOException e) {
							System.err.println("Cannot write message");
						}
					}

				}

			}
		}

	}

}
