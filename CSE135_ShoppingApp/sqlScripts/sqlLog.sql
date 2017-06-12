CREATE TABLE logTest(
	ID SERIAL PRIMARY KEY,
    prod_id INTEGER NOT NULL,
    state_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL CHECK(price >= 0.0),
    bought_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC')
);
SELECT * FROM logTest;

DROP TABLE logOwner;
CREATE TABLE logOwner(
    user_id INTEGER REFERENCES person(id),
    last_refresh TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC')
);
INSERT INTO logOwner(SELECT id, (now() AT TIME ZONE 'UTC') as time FROM person);


DROP FUNCTION logIt() CASCADE;

/* Trigger and log table */
CREATE FUNCTION logIt() RETURNS trigger AS $logIt$
BEGIN
-- Check insertion/update made purchase true --
IF (SELECT is_purchased FROM shopping_cart WHERE id = NEW.cart_id) IS false THEN 
	RETURN null;
END IF;
INSERT INTO logTest(prod_id, state_id, category_id, price, bought_time) VALUES 
	(NEW.product_id, (SELECT DISTINCT state_id FROM person p, shopping_cart sc
                      WHERE sc.person_id = p.id AND sc.id = NEW.cart_id),(SELECT p.category_id FROM product p WHERE p.id = NEW.product_id) , NEW.price * NEW.quantity, (now() AT TIME ZONE 'UTC'));
RETURN NULL;
END;
$logIt$ LANGUAGE plpgsql;


CREATE TRIGGER boughtprod AFTER INSERT
ON products_in_cart
FOR EACH ROW
EXECUTE PROCEDURE logIt();


/* Update Queries for precomputed tables */
/*UPDATE State_Precomputed
SET price = State_Precomputed.price + (logT.price)
FROM (SELECT state_id, category_id, SUM(price) AS price FROM logTest GROUP BY state_id, category_id) as logT
WHERE State_Precomputed.state_id = logT.state_id
AND State_Precomputed.category_id = logT.category_id;*/
UPDATE State_Precomputed
SET price = State_Precomputed.price + lt.price, time = (now() AT TIME ZONE 'UTC')
FROM (SELECT state_id, category_id, SUM(price) AS price FROM logTest GROUP BY state_id, category_id) 
	AS lt JOIN logTest lt1 ON lt.state_id = lt1.state_id AND lt.category_id = lt1.category_id
WHERE State_Precomputed.state_id = lt.state_id
AND State_Precomputed.category_id = lt.category_id
AND State_Precomputed.time < lt1.bought_time;

/*UPDATE Products_Precomputed
SET price = Products_Precomputed.price + (logT.price)
FROM (SELECT prod_id, SUM(price) AS price FROM logTest GROUP BY prod_id) as logT
WHERE Products_Precomputed.product_id = logT.prod_id;
*/
UPDATE Products_Precomputed
SET price = Products_Precomputed.price + (lt.price)
FROM (SELECT prod_id, SUM(price) AS price FROM logTest GROUP BY prod_id) as lt 
	JOIN logTest lt1 ON lt.prod_id = lt1.prod_id
WHERE Products_Precomputed.product_id = lt.prod_id
AND Products_Precomputed.time < lt1.bought_time;

/*UPDATE States_Products_Precomputed
SET price = States_Products_Precomputed.price + (logT.price)
FROM (SELECT prod_id, state_id, SUM(price) AS price FROM logTest GROUP BY prod_id, state_id) as logT
WHERE States_Products_Precomputed.product_id = logT.prod_id
AND States_Products_Precomputed.state_id = logT.state_id;*/
UPDATE States_Products_Precomputed
SET price = States_Products_Precomputed.price + (lt.price)
FROM (SELECT prod_id, state_id, SUM(price) AS price FROM logTest GROUP BY prod_id, state_id) as lt 
	JOIN logTest lt1 ON lt.prod_id = lt1.prod_id
WHERE States_Products_Precomputed.product_id = lt.prod_id
AND States_Products_Precomputed.state_id = lt.state_id
AND States_Products_Precomputed.time < lt1.bought_time;




DELETE FROM logTest;