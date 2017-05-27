SELECT * FROM shopping_cart;
SELECT * FROM products_in_cart;

SELECT p.id,s.state_name FROM person p JOIN state s ON p.state_id = s.id;

SELECT p.person_name,pr.product_name
FROM ((person p JOIN shopping_cart sc ON sc.person_id = p.id) JOIN products_in_cart pc ON pc.cart_id = sc.id), product pr
WHERE pc.product_id = pr.id;

SELECT p,id,s.state_name FROM (person p JOIN state s ON p.state_id = s.id) JOIN () ON ;

-- Alphabetical ordering of states --
SELECT s.state_name,pr.product_name, SUM(pr.price) AS price
FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = sc.person_id
GROUP BY s.state_name, pr.product_name
ORDER BY s.state_name;

SELECT s.state_name,pr.product_name, SUM(pr.price) AS price
FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = sc.person_id
GROUP BY s.state_name, pr.product_name
ORDER BY s.state_name;

-- Alphabetical and full sum of products per state --
SELECT s.state_name, SUM(pr.price) AS price
FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = sc.person_id
GROUP BY s.state_name
ORDER BY s.state_name;