-- Alphabetical and full sum of products per state INCLUDING quantity --
SELECT tot.state_name, SUM(tot.price) AS price 
FROM (SELECT s.state_name, (pr.price * quantity) AS price
FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id)
JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot
GROUP BY tot.state_name ORDER BY tot.state_name
LIMIT 0
-- ORDER BY price DESC

-- Products per state and full pricing --
SELECT tot.state_name, product_name, SUM(tot.price) as price
FROM (SELECT s.state_name, pr.product_name, (pr.price * quantity) AS price
FROM (state s JOIN person p ON p.state_id = s.id) JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id)
JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot
GROUP BY tot.state_name, product_name ORDER BY tot.state_name

