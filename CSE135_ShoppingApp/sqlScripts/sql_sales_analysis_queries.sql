/* SQL STATE QUERIES */

/* QUERY 1 */
-- Alphabetical and full sum of products per state with and without category filter --
SELECT tot.state_name, SUM(tot.price) AS price
FROM (SELECT s.state_name, COALESCE((pr.price * quantity), 0) AS price
FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id)
JOIN shopping_cart sc ON pc.cart_id = sc.id /*AND category_id = ?*/) ON p.id = person_id) as tot
GROUP BY tot.state_name ORDER BY state_name
-- ORDER BY price DESC
-- LIMIT 20 OFFSET ?

/* QUERY 2 */
-- Products per state and full pricing --
SELECT product_name, COALESCE(SUM(pr.price*quantity),0) as price
FROM product p LEFT OUTER JOIN products_in_cart pr ON p.id = pr.product_id
GROUP BY product_name, pr.price
ORDER BY product_name
-- ORDER BY price DESC
-- LIMIT 10 OFFSET

/* QUERY 3 */
-- Alphabetical product ordering with and without category filter
SELECT product_name, SUM(pr.price*quantity) as price
FROM product p JOIN products_in_cart pr ON p.id = pr.product_id -- AND category_id = ?
GROUP BY product_name, pr.price
ORDER BY product_name
-- ORDER BY price DESC
-- LIMIT 10 OFFSET ?

/* QUERY 4 */
-- Basic state x product value --
SELECT tot.state_name, product_name, SUM(tot.price) as price
FROM (SELECT s.state_name, pr.product_name, (pr.price * quantity) AS price
FROM (state s JOIN person p ON p.state_id = s.id) JOIN
((products_in_cart pc JOIN product pr ON pc.product_id = pr.id)
JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot
GROUP BY tot.state_name, product_name

/* QUERY 5 */
-- Products per customer and full pricing with alphabetical sort --
SELECT p.id, p.person_name, COALESCE(SUM(pi.price*pi.quantity),0) as price
FROM (person p LEFT OUTER JOIN shopping_cart s ON p.id = s.person_id)
LEFT OUTER JOIN (product pr LEFT OUTER JOIN products_in_cart pi
ON pr.id = pi.product_id) on s.id = pi.cart_id
GROUP BY p.id, p.person_name
ORDER BY p.person_name;
-- ORDER BY price DESC
-- LIMIT 20 OFFSET ?;

/* QUERY 6 */
-- Person purchases with category filter --
SELECT p.id, p.person_name, COALESCE(SUM(pi.price*pi.quantity),0) as price
FROM (person p LEFT OUTER JOIN shopping_cart s ON p.id = s.person_id)
LEFT OUTER JOIN (product pr JOIN products_in_cart pi
ON pr.id = pi.product_id /*AND category_id = ?*/) on s.id = pi.cart_id
GROUP BY p.id, p.person_name
ORDER BY price DESC
-- person_name
-- LIMIT 20 OFFSET ? --

/* QUERY 7 */
-- Basic Person x Product cell value
SELECT p.id, p.person_name, pr.product_name, SUM(pi.price*pi.quantity) as price
FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id)
JOIN (product pr JOIN products_in_cart pi
ON pr.id = pi.product_id) on s.id = pi.cart_id
GROUP BY p.id, p.person_name, pr.product_name
ORDER BY p.person_name


/*Cosine_Similarity(non-normalized)*/
FROM(WITH subquery AS(
SELECT pr.id as Product_id, pr.product_name, p.id as Person_id, p.person_name, SUM(pi.price*pi.quantity) as Total
FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) LEFT OUTER JOIN (product pr LEFT OUTER JOIN products_in_cart pi ON pr.id = pi.product_id) on s.id = pi.cart_id
GROUP BY pr.id, pr.product_name, p.id,p.person_name
ORDER BY pr.product_name)
SELECT q1.Product_id as id_1, q1.product_name as p1, q2.Product_id as id_2, q2.product_name as p2, SUM(q1.Total*q2.Total) as Cosine_Similarity
FROM subquery q1
JOIN subquery q2 on q1.person_name = q2.person_name
WHERE q1.product_name < q2.product_name
GROUP BY  q1.Product_id, q1.product_name, q2.Product_id, q2.product_name
ORDER BY Cosine_Similarity desc
LIMIT 100)

/*attempt at Normalized_Cosine*/
With den1 as (select Total
from Purchase_Sales
where product_id = table1.id_1)
With den2 as (select Total
from Purchase_Sales
where product_id = table1.id_2)
SELECT table1.Id_1, table1.p1, table1.Id_2, table1.p2, ((table1.cosine_similarity)/(den1)*(den2)) as Normalized_Cosine
FROM(WITH subquery AS(
SELECT pr.id as Product_id, pr.product_name, p.id as Person_id, p.person_name, SUM(pi.price*pi.quantity) as Total
FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) LEFT OUTER JOIN (product pr LEFT OUTER JOIN products_in_cart pi ON pr.id = pi.product_id) on s.id = pi.cart_id
GROUP BY pr.id, pr.product_name, p.id,p.person_name
ORDER BY pr.product_name)
SELECT q1.Product_id as id_1, q1.product_name as p1, q2.Product_id as id_2, q2.product_name as p2, SUM(q1.Total*q2.Total) as Cosine_Similarity
FROM subquery q1
JOIN subquery q2 on q1.person_name = q2.person_name
WHERE q1.product_name < q2.product_name
GROUP BY  q1.Product_id, q1.product_name, q2.Product_id, q2.product_name
ORDER BY Cosine_Similarity desc
LIMIT 100) table1
