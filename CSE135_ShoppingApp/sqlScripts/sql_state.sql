-- Alphabetical and full sum of products per state INCLUDING quantity --
SELECT tot.state_name, SUM(tot.price) AS price 
FROM (SELECT s.state_name, COALESCE((pr.price * quantity), 0) AS price 
FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) 
JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot 
GROUP BY tot.state_name ORDER BY tot.state_name
-- LIMIT ? OFFSET ?
-- ORDER BY price DESC

-- Products per state and full pricing --
SELECT tot.state_name, product_name, SUM(tot.price) as price
FROM (SELECT s.state_name, pr.product_name, (pr.price * quantity) AS price
FROM (state s JOIN person p ON p.state_id = s.id) JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id)
JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot
GROUP BY tot.state_name, product_name ORDER BY tot.state_name

-- Top-K full sum of products per state sorted by category --
SELECT tot.state_name, SUM(tot.price) AS price 
FROM (SELECT s.state_name, COALESCE((pr.price * quantity), 0) AS price 
FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) 
JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id 
WHERE category_id = ?) as tot 
GROUP BY tot.state_name ORDER BY price DESC

-- Sum of products per state limited by category --
SELECT tot.state_name, SUM(tot.price) AS price 
FROM (SELECT s.state_name, COALESCE((pr.price * quantity), 0) AS price 
FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) 
JOIN shopping_cart sc ON pc.cart_id = sc.id AND category_id = ?) ON p.id = person_id) as tot 
GROUP BY tot.state_name ORDER BY price DESC;

-- Products Listed by Top Price --
SELECT product_name, COALESCE(SUM(pr.price*quantity), 0) as price 
FROM product p LEFT OUTER JOIN products_in_cart pr ON p.id = pr.product_id 
GROUP BY product_name, pr.price 
ORDER BY price DESC

-- Products listed by top price and only counting category --
SELECT product_name, SUM(pr.price*quantity) as price 
FROM product p JOIN products_in_cart pr ON p.id = pr.product_id AND category_id = 8
GROUP BY product_name, pr.price 
ORDER BY price DESC

-- Just in case, the above query but with blanks --
SELECT product_name, COALESCE(SUM(pr.price*quantity), 0) as price 
FROM product p LEFT OUTER JOIN products_in_cart pr ON p.id = pr.product_id AND category_id = ?
GROUP BY product_name, pr.price 
ORDER BY price DESC