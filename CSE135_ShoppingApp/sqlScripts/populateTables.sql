
INSERT INTO person(person_name,role_id, state_id, age) VALUES('blah', 1, 7, 8);


INSERT INTO category(category_name,description, created_by) VALUES('shoes','description for shoes', 'Storm');
INSERT INTO category(category_name,description, created_by) VALUES('tees','description for tees', 'Storm');
INSERT INTO category(category_name,description, created_by) VALUES('accessories','description for accessories', 'Storm');
INSERT INTO category(category_name,description, created_by) VALUES('food','description for food', 'Storm');
INSERT INTO category(category_name,description, created_by) VALUES('technology','description for technology', 'Storm');
INSERT INTO category(category_name,description, created_by) VALUES('misc','description for misc', 'Storm');


-- INSERT INTO product(sku_id,product_name, price, category_id, created_by) VALUES();


-- SELECT * FROM role; --
-- SELECT * FROM state; --
SELECT * FROM person;
SELECT * FROM category;
SELECT * FROM product;
SELECT * FROM shopping_cart;
SELECT * FROM products_in_cart;
