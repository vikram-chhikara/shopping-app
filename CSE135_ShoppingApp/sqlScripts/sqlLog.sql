DROP TABLE logTest CASCADE;
CREATE TABLE logTest(
	ID SERIAL PRIMARY KEY,
    prod_id INTEGER NOT NULL,
    state_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL CHECK(price >= 0.0),
    bought_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC')
);
SELECT * FROM logTest;

DROP TABLE IF EXISTS logOwner CASCADE;
CREATE TABLE logOwner(
    user_id INTEGER REFERENCES person(id),
    last_refresh TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC')
);
INSERT INTO logOwner(SELECT id, (now() AT TIME ZONE 'UTC') as time FROM person);

CREATE INDEX logUser ON logOwner(user_id);

SELECT * FROM logOwner;

DROP FUNCTION logIt() CASCADE;

/* Trigger and log table */
CREATE FUNCTION logIt() RETURNS trigger AS $logIt$
BEGIN
-- Check insertion/update made purchase true --
IF (SELECT is_purchased FROM shopping_cart WHERE id = NEW.cart_id) IS false THEN 
	RETURN null;
END IF;

IF (SELECT EXISTS (SELECT 1 FROM logTest WHERE prod_id = NEW.product_id AND state_id = 
                   (SELECT DISTINCT state_id FROM person p, shopping_cart sc
    WHERE sc.person_id = p.id AND sc.id = NEW.cart_id)))
THEN
	UPDATE logTest SET price = price + (NEW.price*NEW.quantity), bought_time = (now() AT TIME ZONE 'UTC') 
    WHERE prod_id = NEW.product_id AND state_id = (SELECT DISTINCT state_id FROM person p, shopping_cart sc
    WHERE sc.person_id = p.id AND sc.id = NEW.cart_id);
ELSE 
    INSERT INTO logTest(prod_id, state_id, category_id, price, bought_time) VALUES 
		(NEW.product_id, (SELECT DISTINCT state_id FROM person p, shopping_cart sc
    WHERE sc.person_id = p.id AND sc.id = NEW.cart_id),(SELECT p.category_id FROM product p WHERE p.id = NEW.product_id) 
         	, NEW.price * NEW.quantity, (now() AT TIME ZONE 'UTC'));
END IF;
RETURN NULL;
    
END;
$logIt$ LANGUAGE plpgsql;


CREATE TRIGGER boughtprod AFTER INSERT
ON products_in_cart
FOR EACH ROW
EXECUTE PROCEDURE logIt();