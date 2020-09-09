# CustomerStatementReport
Input Multiormat file processed using Spring Batch.
The application gets input from src/main/resources as records.csv, records.xml
Perform validation for endbalance, find duplicate records and add the comment fields.
After validation completion, failed records are written in file as taget/test-outputs/output.txt for csv file and 
taget/test-outputs/XMLoutput.txt for XML file.
The application runs in port 8080 and uses http://localhost:8080/customerFailRecordsGeneration to view the batch completion status.
