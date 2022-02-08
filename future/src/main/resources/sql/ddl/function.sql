DROP FUNCTION IF EXISTS STR_SPLIT;
DELIMITER $$
CREATE FUNCTION STR_SPLIT(s TEXT, del CHAR(1), i INT)
    RETURNS VARCHAR(32)
    DETERMINISTIC -- always returns same results for same input parameters
    SQL SECURITY INVOKER
BEGIN
    DECLARE n INT;
    DECLARE idx INT;
    -- get max number of items
    SET n = LENGTH(s) - LENGTH(REPLACE(s, del, '')) + 1;
    IF ABS(i) > n THEN
        RETURN NULL ;
    ELSE
        IF i < 0 THEN SET idx = 1; ELSE SET idx = -1; END IF;
        RETURN SUBSTRING_INDEX(SUBSTRING_INDEX(s, del, i), del, idx) ;
    END IF;
END$$
DELIMITER ;