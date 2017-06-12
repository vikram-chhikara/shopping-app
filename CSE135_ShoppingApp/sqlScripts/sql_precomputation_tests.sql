DROP TABLE State_Precomputed;
CREATE TABLE State_Precomputed (
state_id INTEGER NOT NULL,
state_name TEXT NOT NULL,
category_id INTEGER,
price REAL NOT NULL CHECK(price >= 0.0)
);
INSERT INTO State_Precomputed(SELECT state_id, tot.state_name, tot.cat_id, SUM(tot.price) AS price 
			FROM (SELECT s.id as state_id, s.state_name, pr.category_id as cat_id, COALESCE((pr.price * quantity), 0) AS price 
			FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN 
			((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) 
			JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot
			GROUP BY state_id, tot.cat_id, tot.state_name ORDER BY price DESC);

SELECT * FROM State_Precomputed


DROP TABLE Products_Precomputed;
CREATE TABLE Products_Precomputed (
product_id INTEGER NOT NULL,
product_name TEXT NOT NULL,
category_id INTEGER NOT NULL,
price DOUBLE PRECISION NOT NULL CHECK(price >= 0.0)
);
INSERT INTO Products_Precomputed(SELECT p.id, product_name, p.category_id, COALESCE(SUM(pr.price*quantity), 0) as price
			FROM product p LEFT OUTER JOIN products_in_cart pr ON p.id = pr.product_id
			GROUP BY p.id, product_name, pr.price
			ORDER BY price DESC);

SELECT * FROM Products_Precomputed


DROP TABLE States_Products_Precomputed;
CREATE TABLE States_Products_Precomputed (
state_id INTEGER NOT NULL,
state_name TEXT NOT NULL,
product_id INTEGER NOT NULL,
product_name TEXT NOT NULL,
price REAL NOT NULL CHECK(price >= 0.0)
);

INSERT INTO States_Products_Precomputed(SELECT tot.s_id, tot.state_name,tot.pr_id, product_name, SUM(tot.price) as price
			FROM (SELECT s.id as s_id, s.state_name, pr.id as pr_id, pr.product_name, (pr.price * quantity) AS price
			FROM (state s JOIN person p ON p.state_id = s.id) JOIN
			((products_in_cart pc JOIN product pr ON pc.product_id = pr.id)
			JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot
			GROUP BY tot.s_id, tot.state_name,tot.pr_id, product_name
            ORDER BY s_id);

Select * from States_Products_Precomputed;
