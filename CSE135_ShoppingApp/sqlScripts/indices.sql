/* Indexing */

-- INDEX 1 -- 
CREATE INDEX state_table_name ON state(state_name)
DROP INDEX state_table_name

-- INDEX 2 -- 
CREATE INDEX person_table_state_id ON person(state_id)
DROP INDEX person_table_state_id

-- INDEX 3 -- 
CREATE INDEX product_in_cart_prod_id ON products_in_cart(product_id)
DROP INDEX product_in_cart_prod_id

-- INDEX 4 -- 
CREATE INDEX prod_in_cart_cart_id ON products_in_cart(cart_id)
DROP INDEX prod_in_cart_cart_id

-- INDEX 5 -- 
CREATE INDEX shopping_cart_person_id ON shopping_cart(person_id)
DROP INDEX shopping_cart_person_id

-- INDEX 6 -- 
CREATE INDEX product_table_cat_id ON product(category_id)
DROP INDEX product_table_cat_id


