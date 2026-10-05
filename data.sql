DROP database ecommerce;
CREATE database ecommerce;
use  ecommerce;

SELECT name, category, COUNT(*) AS duplicate_count
FROM product
GROUP BY name, category
HAVING COUNT(*) > 1;

SELECT *
# FROM flyway_schema_history;