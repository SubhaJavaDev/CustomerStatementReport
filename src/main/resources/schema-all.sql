--DROP TABLE person IF EXISTS;
--
--CREATE TABLE person  (
--    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
--    first_name VARCHAR(20),
--    last_name VARCHAR(20)
--);

DROP TABLE customerStatement IF EXISTS;

CREATE TABLE customerStatement  (
    customerStatement_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    
    reference INTEGER ,
    accountNumber VARCHAR(50),
    description VARCHAR(250),
    startBalance DOUBLE,
    endBalance DOUBLE,
    mutation DOUBLE,
    comments VARCHAR(500)
    
);