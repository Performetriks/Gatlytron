INSERT INTO {tempTableName} {tableColumnNames}
SELECT 
      MIN("TIME") + ((MAX("TIME") - MIN("TIME"))/2) AS "CENTER_TIME"
    , {namesWithoutTimeOrGranularity}
    , ? AS "GRANULARITY"
    {valuesAggregation}
FROM {originalTableName}
WHERE 
	"TIME" >= ? 
AND "TIME" < ? 
AND "GRANULARITY" < ?
GROUP BY {groupByNames}
;