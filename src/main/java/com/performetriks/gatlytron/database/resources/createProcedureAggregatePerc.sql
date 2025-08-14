CREATE PROCEDURE AGGREGATE_PERC (
    IN p_column_name VARCHAR(50),
    IN p_percentile NUMERIC,
    IN p_start_time TIMESTAMP,
    IN p_end_time TIMESTAMP,
    IN p_granularity NUMERIC,
    OUT p_result NUMERIC
)
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE val NUMERIC;
    DECLARE total_rows NUMERIC;
    DECLARE row_index NUMERIC DEFAULT 0;

    -- Count total rows in the filtered set
    SELECT COUNT(*) INTO total_rows
    FROM CFW_EAV_STATS
    WHERE TIME >= p_start_time
      AND TIME < p_end_time
      AND GRANULARITY < p_granularity;

    -- Cursor to iterate over ordered values
    DECLARE cur CURSOR FOR
        SELECT CASE 
                 WHEN p_column_name = 'p25' THEN p25
                 WHEN p_column_name = 'p50' THEN p50
                 WHEN p_column_name = 'p75' THEN p75
                 WHEN p_column_name = 'p95' THEN p95
                 WHEN p_column_name = 'p99' THEN p99
                 ELSE 0
               END AS val
        FROM CFW_EAV_STATS
        WHERE TIME >= p_start_time
          AND TIME < p_end_time
          AND GRANULARITY < p_granularity
        ORDER BY val;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO val;
        SET row_index = row_index + 1;
        IF row_index >= CEIL(total_rows * p_percentile) THEN
            SET p_result = val;
            LEAVE read_loop;
        END IF;
        IF done = 1 THEN
            LEAVE read_loop;
        END IF;
    END LOOP;
    CLOSE cur;
END;
