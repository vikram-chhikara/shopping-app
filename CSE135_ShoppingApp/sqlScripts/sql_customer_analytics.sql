/*Create shopping cart for each customer*/
INSERT INTO shopping_cart (person_id,is_purchased) VALUES (2,True) ;
INSERT INTO shopping_cart (person_id,is_purchased) VALUES (3,True) ;
INSERT INTO shopping_cart (person_id,is_purchased) VALUES (4,True) ;

/*Fill each customer's cart with their purchased products*/
/*For bob; nike boost shoe, mac and adi shirt*/
TRUNCATE TABLE products_in_cart;
INSERT INTO products_in_cart (cart_id,product_id,price,quantity) VALUES (1,1,25,1) ;
INSERT INTO products_in_cart (cart_id,product_id,price,quantity) VALUES (1,2,8.99,1) ;
INSERT INTO products_in_cart (cart_id,product_id,price,quantity) VALUES (1,3,980,1) ;
/*for raj; alienware and aldo shoe*/
INSERT INTO products_in_cart (cart_id,product_id,price,quantity) VALUES (2,4,789,1) ;
INSERT INTO products_in_cart (cart_id,product_id,price,quantity) VALUES (2,6,100,1) ;
/*for steven; shirt and dell*/
INSERT INTO products_in_cart (cart_id,product_id,price,quantity) VALUES (3,8,40,1) ;
INSERT INTO products_in_cart (cart_id,product_id,price,quantity) VALUES (3,5,346,1) ;

/*Simple Query; gives customers and their purchased products and price*/
SELECT p.person_name, pr.product_name, pi.price
FROM person p, product pr, shopping_cart s, products_in_cart pi
WHERE p.id = s.id and s.id = pi.cart_id and pr.id = pi.product_id
ORDER BY p.person_name

/*Better query than before; gives quantity of each product*/
SELECT t.person_name, t.product_name, t.price, COUNT(t.product_name) AS Quanatity
FROM
(SELECT p.person_name, pr.product_name, pi.price
FROM person p, product pr, shopping_cart s, products_in_cart pi
WHERE p.id = s.id and s.id = pi.cart_id and pr.id = pi.product_id
ORDER BY p.person_name) t
GROUP BY t.person_name, t.product_name, t.price
ORDER BY t.person_name

/*Query that gives sum total spent by each customer */
SELECT t.person_name, SUM(t.price) AS Total
FROM
(SELECT p.person_name, pr.product_name, pi.price
FROM person p, product pr, shopping_cart s, products_in_cart pi
WHERE p.id = s.id and s.id = pi.cart_id and pr.id = pi.product_id
ORDER BY p.person_name) t
GROUP BY t.person_name
ORDER BY Total DESC 
