DROP TABLE Products_Precomputed;
CREATE TABLE State_Precomputed (
state_id INTEGER NOT NULL,
state_name TEXT NOT NULL,
price REAL NOT NULL CHECK(price >= 0.0)
);
INSERT INTO State_Precomputed(SELECT tot.state_id, tot.state_name, SUM(tot.price) AS price
			FROM (SELECT s.id AS state_id, s.state_name, COALESCE((pr.price * quantity), 0) AS price
			FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN
			((products_in_cart pc JOIN product pr ON pc.product_id = pr.id)
			JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot
            GROUP BY tot.state_id, tot.state_name ORDER BY price DESC LIMIT 50);

ALTER TABLE State_Precomputed add column id SERIAL PRIMARY KEY;

SELECT * FROM State_Precomputed

CREATE TABLE Products_Precomputed (
product_id INTEGER NOT NULL,
product_name TEXT NOT NULL,
price REAL NOT NULL CHECK(price >= 0.0)
);
INSERT INTO Products_Precomputed(SELECT p.id, product_name, COALESCE(SUM(pr.price*quantity), 0) as price
			FROM product p LEFT OUTER JOIN products_in_cart pr ON p.id = pr.product_id
			GROUP BY p.id, product_name, pr.price
			ORDER BY price DESC LIMIT 50);

ALTER TABLE Products_Precomputed add column id SERIAL PRIMARY KEY;
SELECT * FROM Products_Precomputed

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
            ORDER BY tot.state_name);

ALTER TABLE States_Products_Precomputed add column id SERIAL PRIMARY KEY;

Select * from States_Products_Precomputed;
