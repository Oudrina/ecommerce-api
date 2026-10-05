ALTER TABLE product
    ADD CONSTRAINT uk_product_name_category UNIQUE (name, category);
SHOW CREATE TABLE product;
SHOW TABLES LIKE 'flyway_schema_history';

# SELECT * FROM  ;
SHOW TABLES;