# CustomerStatementReport
Input Multiormat file processed using Spring Batch.
Spring Batch applcation reads file using reader, validates using processor and end report is collected using writer.
The application gets input from src/main/resources as records.csv, records.xml
Perform validation for endbalance, find duplicate records and add the comment fields.
After validation completion, failed records are written in project location as output.txt for records.csv file and 
XMLoutput.txt for records.xml file.
The application runs in port 8080 and uses http://localhost:8080/customerFailRecordsGeneration to view the batch completion status.
