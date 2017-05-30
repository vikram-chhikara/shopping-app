/* SQL STATE QUERIES */

/* INDEX 1 */
-- Alphabetical and full sum of products per state with and without category filter --
SELECT tot.state_name, SUM(tot.price) AS price 
FROM (SELECT s.state_name, COALESCE((pr.price * quantity), 0) AS price 
FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) 
JOIN shopping_cart sc ON pc.cart_id = sc.id /*AND category_id = ?*/) ON p.id = person_id) as tot 
GROUP BY tot.state_name ORDER BY state_name
-- ORDER BY price DESC
-- LIMIT 20 OFFSET ?

/* INDEX 2 */
-- Products per state and full pricing --
SELECT product_name, COALESCE(SUM(pr.price*quantity),0) as price 
FROM product p LEFT OUTER JOIN products_in_cart pr ON p.id = pr.product_id 
GROUP BY product_name, pr.price 
ORDER BY product_name 
-- ORDER BY price DESC
-- LIMIT 10 OFFSET 

/* INDEX 3 */
-- Alphabetical product ordering with and without category filter
SELECT product_name, SUM(pr.price*quantity) as price 
FROM product p JOIN products_in_cart pr ON p.id = pr.product_id -- AND category_id = ? 
GROUP BY product_name, pr.price 
ORDER BY product_name
-- ORDER BY price DESC
-- LIMIT 10 OFFSET ?

/* INDEX 4 */
-- Sum of products per state with and without limited by category --
SELECT tot.state_name, SUM(tot.price) AS price 
FROM (SELECT s.state_name, COALESCE((pr.price * quantity), 0) AS price 
FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) 
JOIN shopping_cart sc ON pc.cart_id = sc.id /* AND category_id = ? */) ON p.id = person_id) as tot 
GROUP BY tot.state_name ORDER BY price DESC

/* INDEX 5 */
-- Basic state x product value --
SELECT tot.state_name, product_name, SUM(tot.price) as price 
FROM (SELECT s.state_name, pr.product_name, (pr.price * quantity) AS price 
FROM (state s JOIN person p ON p.state_id = s.id) JOIN 
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) 
JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot 
GROUP BY tot.state_name, product_name

/* INDEX 6 */
-- Products per customer and full pricing with alphabetical sort --
SELECT p.id, p.person_name, COALESCE(SUM(pi.price*pi.quantity),0) as price 
FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) 
LEFT OUTER JOIN (product pr LEFT OUTER JOIN products_in_cart pi ON 
pr.id = pi.product_id) on s.id = pi.cart_id 
GROUP BY p.id, p.person_name ORDER BY p.person_name;
-- LIMIT 20 OFFSET ?;

/* INDEX 7 */
-- Person purchases sorted by top --
SELECT p.id, p.person_name, COALESCE(SUM(pi.price*pi.quantity),0) as price 
FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) 
LEFT OUTER JOIN (product pr LEFT OUTER JOIN products_in_cart pi 
ON pr.id = pi.product_id) on s.id = pi.cart_id GROUP BY p.id, p.person_name 
ORDER BY price DESC 
-- LIMIT 20 OFFSET ?

/* INDEX 8 */
-- Person purchases with category filter --
SELECT p.id, p.person_name, COALESCE(SUM(pi.price*pi.quantity),0) as price 
FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) 
LEFT OUTER JOIN (product pr JOIN products_in_cart pi 
ON pr.id = pi.product_id AND category_id = ?) on s.id = pi.cart_id 
GROUP BY p.id, p.person_name 
ORDER BY price DESC 
-- person_name
-- LIMIT 20 OFFSET ? --

/* INDEX 9 */
-- Basic Person x Product cell value
SELECT p.id, p.person_name, pr.product_name, SUM(pi.price*pi.quantity) as price 
FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) 
JOIN (product pr JOIN products_in_cart pi 
ON pr.id = pi.product_id) on s.id = pi.cart_id GROUP BY p.id, p.person_name, pr.product_name 
ORDER BY p.person_name