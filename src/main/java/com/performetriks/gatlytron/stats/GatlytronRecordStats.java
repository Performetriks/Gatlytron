package com.performetriks.gatlytron.stats;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gson.JsonObject;
import com.performetriks.gatlytron.base.Gatlytron;
import com.performetriks.gatlytron.database.DBInterface;
import com.performetriks.gatlytron.database.GatlytronDBInterface;
import com.performetriks.gatlytron.stats.GatlytronRecordRaw.GatlytronRecordType;
import com.performetriks.gatlytron.utils.GatlytronFiles;

/***************************************************************************
 * This record holds one record of statistical data.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronRecordStats {
	
	//private static final Logger logger = LoggerFactory.getLogger(GatlytronRecordStats.class);
	
	public static final String TEMP_TABLE_AGGREGATION = "TEMP_STATS_AGGREGATION";
	
	private long time;
	private GatlytronRecordType type;
	private String simulation;		// the name of the simulation
	private String scenario;		// the name of the scenario
	private String metricName;		// the name of the metric, one of the items in the lost metricNames
	private String groupsPath;		// 
	private String metricPath;
	private String metricPathFull;
	private String code;
	private int granularity;
	private String statsIdentifier;
	private HashMap<String, BigDecimal> values = new HashMap<>();
	
	/***********************************************************************
	 * Lists of the names of the metrics 
	 ***********************************************************************/
	public enum RecordField {
		  time("DECIMAL(19, 0)") // ANSI SQL for Long value
		, type("VARCHAR(16)")
		, simulation("VARCHAR(4096)")
		, scenario("VARCHAR(4096)")
		, groups("VARCHAR(4096)")
		, metric("VARCHAR(4096)")
		, code("VARCHAR(16)")
		, granularity("INTEGER")
		;
		
		private RecordField(String dbColumnType){
			this.dbColumnType = dbColumnType;
		}
		
		private static ArrayList<String> names = new ArrayList<>();
		static {
			for(RecordField type : RecordField.values()) { 
				names.add(type.name());
			}
		}

		private String dbColumnType;		
		public  String getDBColumnType(){ return dbColumnType; }
		
		public static boolean has(String value) { return names.contains(value); }
		
		public static ArrayList<String> getFieldNames() { 
			ArrayList<String> valueNames =  new ArrayList<>();
			
			for(String name : names) { valueNames.add(name); }
			
			return valueNames;
		}
	}
	

	
	/***********************************************************************
	 * Lists of the names of the metrics 
	 ***********************************************************************/
	
	// !#!#!#!#!#!#!# IMPORTANT !#!#!#!#!#!#!#
	// If you change this in any way make sure to test the ageOut mechanism again.
	// Easiest way to test: Generate data in the database and use a DB tool to adjust the time column. 
	// !#!#!#!#!# END OF IMPORTANCE !#!#!#!#!#

	public enum RecordMetric {
		  count("SUM(\"{type}_count\")")
		, min("MIN(\"{type}_min\")")
		, max("MAX(\"{type}_max\")")
		, mean("AVG(\"{type}_mean\")")
		, stdev("STDDEV(\"{type}_stdev\")")
		, p25("PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY \"{type}_p25\")") // (CALL AGGREGATE_PERC('p50', 0.50, ?, ?, ?, ?))
		, p50("PERCENTILE_CONT(0.50) WITHIN GROUP (ORDER BY \"{type}_p50\")")
		, p75("PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY \"{type}_p75\")")
		, p95("PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY \"{type}_p95\")") 
		, p99("PERCENTILE_CONT(0.99) WITHIN GROUP (ORDER BY \"{type}_p99\")")
		;
		
		private String sqlAggregation = "";
		private RecordMetric(String sqlAggregationString){
			this.sqlAggregation = sqlAggregationString;
		}
		
		private static ArrayList<String> names = new ArrayList<>();
		private static String sqlAggregationPart = "";
		static {
			for(RecordMetric type : RecordMetric.values()) { 
				names.add(type.name()); 
				sqlAggregationPart += "\r\n, "
						+ type.sqlAggregation.replace("{type}", "ok")
						+ " AS \"ok_"+type.name()+"\""
						;
			}
			for(RecordMetric type : RecordMetric.values()) { 
				sqlAggregationPart += "\r\n, "
								+ type.sqlAggregation.replace("{type}", "ko")
								+ " AS \"ko_"+type.name()+"\"";
			}
		}

		public static boolean has(String value) { return names.contains(value); }
		
		public static ArrayList<String> getNames() { 
			ArrayList<String> copy =  new ArrayList<>();
			copy.addAll(names); 
			return copy;
		}
		
		public static ArrayList<String> getValueNames() { 
			ArrayList<String> valueNames =  new ArrayList<>();
			
			for(String name : names) { valueNames.add("ok_"+name); }
			for(String name : names) { valueNames.add("ko_"+name); }
			
			return valueNames;
		}
		
		public static String getSQLAggregationPart() { 
			return sqlAggregationPart;
		}
		
		
	}
	
	// list of field names
	public static final ArrayList<String> fieldNames = RecordField.getFieldNames();
	public static final String fieldNamesJoined = "\""+String.join("\",\"", fieldNames.toArray(new String[0]))+"\"";
	
	// list of metric names e.g. min, mean, max ...
	public static final ArrayList<String> metricNames = RecordMetric.getNames();
	public static final String metricNamesJoined = "\""+String.join("\",\"", metricNames.toArray(new String[0]))+"\"";
	
	// value names consist of type + "_" + metric, e.g. ok_min, ok_mean, ok_max ... ko_min, ko_mean, ko_max ...
	public static final  ArrayList<String> valueNames = RecordMetric.getValueNames();
	public static final String valueNamesJoined = "\""+String.join("\",\"", valueNames.toArray(new String[0]))+"\"";
	
	
	private static String sqlTableColumnDefinitions;
	private static String sqlTableColumnNames;
	static {
		
		sqlTableColumnDefinitions = "(";
		sqlTableColumnNames = "(";
		
			//-----------------------------------------
			// SQL Create Table Template
			for(RecordField field : RecordField.values()) {
				sqlTableColumnDefinitions += field.name()+" "+field.getDBColumnType() +",\r\n";
				sqlTableColumnNames += field.name()+",\r\n";
			}
			
			//-----------------------------------------
			// SQL Create Table Template
			for(String name : valueNames) {
				sqlTableColumnDefinitions += name+" DECIMAL(32,3),\r\n";
				sqlTableColumnNames += name+",\r\n";
			}
			
			// remove last comma and newline
			sqlTableColumnDefinitions = sqlTableColumnDefinitions.substring(0, sqlTableColumnDefinitions.length()-3);
			sqlTableColumnNames = sqlTableColumnNames.substring(0, sqlTableColumnNames.length()-3);
		
		sqlTableColumnDefinitions += ")";
		sqlTableColumnNames += ")";
		
	}
	

	
	private static String csvHeaderTemplate = fieldNamesJoined+","+valueNamesJoined;
	private static String sqlCreateTableTemplate = "CREATE TABLE IF NOT EXISTS {tablename} "+sqlTableColumnDefinitions;
			;
	
	private static String sqlInsertIntoTemplate = "INSERT INTO {tablename} "+sqlTableColumnNames
													  + " VALUES (?"+ 
													  			", ?".repeat( fieldNames.size() + valueNames.size() - 1 ) 
													  +")";

	static {
		
		//-----------------------------------------
		// CSV Template
		for(String name : valueNames) {
			csvHeaderTemplate += ","+name;
		}
		//csvHeaderTemplate += "\r\n";
		
		//-----------------------------------------
		// SQL Create Table Template
//		for(String name : valueNames) {
//			sqlCreateTableTemplate += ", "+name+" DECIMAL(32,3)";
//		}
//		sqlCreateTableTemplate += ");";

		//-----------------------------------------
		// SQL Insert Into Template

//		String sqlInsertValues = "VALUES (?, ?, ?, ?, ?, ?, ?";
//		for(String name : valueNames) {
//			//sqlInsertIntoTemplate += ","+name;
//			sqlInsertValues += ", ?";
//		}
//		sqlInsertValues += ")";
//		sqlInsertIntoTemplate += ") " + sqlInsertValues;
		
		
	}
	

	/***********************************************************************
	 * Creates a record containing request statistics.
	 * 
	 * @param statsRecordList the list to which the stats record should be added too.
	 * @param record one of the records of the 
	 ***********************************************************************/
	public GatlytronRecordStats(
							  LinkedHashMap<GatlytronRecordStats, GatlytronRecordStats> statsRecordList
							, GatlytronRecordRaw record
						    , long timeMillis
							, BigDecimal count 
							, BigDecimal mean 
							, BigDecimal min 		
							, BigDecimal max 			
							, BigDecimal stdev 	
							, BigDecimal p25 		
							, BigDecimal p50 		
							, BigDecimal p75 		
							, BigDecimal p95 		
							, BigDecimal p99 	
						){	
		
		//-----------------------------------
		// Parse Message
		this.time = timeMillis;
		this.type = record.getType();
		this.simulation = record.getSimulation();
		this.scenario = record.getScenario();
		this.metricName = record.getMetricName();
		this.groupsPath = record.getGroupsAsString(" / ", "");
		this.metricPath = record.getMetricPath();
		this.metricPathFull = record.getMetricPathFull();
		this.code = record.getResponseCode();
		this.granularity = Gatlytron.getReportInterval();
		this.statsIdentifier = record.getStatsIdentifier();

		//-----------------------------------
		// Get Target Record
		GatlytronRecordStats targetForData = statsRecordList.get(this);
		
		if(targetForData == null) {
			targetForData = this;
			statsRecordList.put(this, this);
		}
		
		//-----------------------------------
		// Add Values
		String statusLower = record.getStatus().toLowerCase();  
		
		targetForData.addValue(statusLower, RecordMetric.count, count.toPlainString());
		targetForData.addValue(statusLower, RecordMetric.min, min.toPlainString());
		targetForData.addValue(statusLower, RecordMetric.max, max.toPlainString());
		targetForData.addValue(statusLower, RecordMetric.mean, mean.toPlainString());
		targetForData.addValue(statusLower, RecordMetric.stdev, stdev.toPlainString());
		targetForData.addValue(statusLower, RecordMetric.p25, p25.toPlainString());
		targetForData.addValue(statusLower, RecordMetric.p50, p50.toPlainString());
		targetForData.addValue(statusLower, RecordMetric.p75, p75.toPlainString());
		targetForData.addValue(statusLower, RecordMetric.p95, p95.toPlainString());
		targetForData.addValue(statusLower, RecordMetric.p99, p99.toPlainString());
		
	}	
	
	/***********************************************************************
	 * 
	 * @param type either ok | ko | all | users
	 ***********************************************************************/
	private void addValue(String type, RecordMetric metric, String value) {
		
		BigDecimal parsedInt = new BigDecimal(value);
		String finalMetric = metric.toString()
//								.replace("percentiles", "p")
//								.replace("stdDev", "stdev")
								;  // make it shorter to reduce footprint

		values.put(type +"_"+finalMetric, parsedInt);

	}
	
	
	
	/***********************************************************************
	 * Returns a CSV header
	 ***********************************************************************/
	public static String getCSVHeader(String separator) {
		return csvHeaderTemplate.replace(",", separator);
	}
	
	/***********************************************************************
	 * Returns the record as a CSV data record.
	 * Will not contain the header, use getCSVHeader() for that.
	 ***********************************************************************/
	public String toCSV(String separator) {
		
		String csv = time 
					+ separator + type.threeLetters()
					+ separator + simulation.replace(separator, "_") 
					+ separator + scenario.replace(separator, "_")  
					+ separator + groupsPath.replace(separator, "_")  
					+ separator + metricName.replace(separator, "_")  
					+ separator + code.replace(separator, "_")  
					+ separator + granularity
					;
				
		for(String name : valueNames) {
			BigDecimal val = values.get(name);
			val = (val == null) ? BigDecimal.ZERO : val;
			csv += separator + val.toPlainString(); 
		}
		
		return csv;

	}
	
	/***********************************************************************
	 * 
	 ***********************************************************************/
	public String toJsonString() {
		return this.toJson().toString();
	}
	
	
	/***********************************************************************
	 * 
	 ***********************************************************************/
	public JsonObject toJson() {
		
		JsonObject object = new JsonObject();
		
		object.addProperty(RecordField.time.toString(), 		time);
		object.addProperty(RecordField.type.toString(), 		type.threeLetters());
		object.addProperty(RecordField.simulation.toString(), 	simulation);
		object.addProperty(RecordField.scenario.toString(), 	scenario);
		object.addProperty(RecordField.groups.toString(), 		groupsPath);
		object.addProperty(RecordField.metric.toString(), 		metricName);
		object.addProperty(RecordField.code.toString(), 		code);
		object.addProperty(RecordField.granularity.toString(), 	granularity);
		
		for(String name : valueNames) {
			object.addProperty(name, this.getValue(name));
		}
		
		return object;

	}
	
	/***********************************************************************
	 * Returns a SQL Create Table statement for the statistics table
	 * with the provided table name inserted.
	 ***********************************************************************/
	public static String getSQLCreateTableTemplate(String tableName) {
		return sqlCreateTableTemplate.replace("{tablename}", tableName);
	}
	
	/***********************************************************************
	 * Returns a humongous SQL for aggregating SQL.
	 * Here is an example:
	 * <pre><code>
INSERT INTO TEMP_STATS_AGGREGATION (time,
type,
simulation,
scenario,
groups,
metric,
code,
granularity,
ok_count,
ok_min,
ok_max,
ok_mean,
ok_stdev,
ok_p25,
ok_p50,
ok_p75,
ok_p95,
ok_p99,
ko_count,
ko_min,
ko_max,
ko_mean,
ko_stdev,
ko_p25,
ko_p50,
ko_p75,
ko_p95,
ko_p99)
SELECT 
      MIN("time") + ((MAX("time") - MIN("time"))/2) AS "time"
    , "type","simulation","scenario","groups","metric","code"
    , ? AS "granularity"
    
, SUM("ok_count") AS "ok_count"
, MIN("ok_min") AS "ok_min"
, MAX("ok_max") AS "ok_max"
, AVG("ok_mean") AS "ok_mean"
, STDDEV("ok_stdev") AS "ok_stdev"
, PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY "ok_p25") AS "ok_p25"
, PERCENTILE_CONT(0.50) WITHIN GROUP (ORDER BY "ok_p50") AS "ok_p50"
, PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY "ok_p75") AS "ok_p75"
, PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY "ok_p95") AS "ok_p95"
, PERCENTILE_CONT(0.99) WITHIN GROUP (ORDER BY "ok_p99") AS "ok_p99"
, SUM("ko_count") AS "ko_count"
, MIN("ko_min") AS "ko_min"
, MAX("ko_max") AS "ko_max"
, AVG("ko_mean") AS "ko_mean"
, STDDEV("ko_stdev") AS "ko_stdev"
, PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY "ko_p25") AS "ko_p25"
, PERCENTILE_CONT(0.50) WITHIN GROUP (ORDER BY "ko_p50") AS "ko_p50"
, PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY "ko_p75") AS "ko_p75"
, PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY "ko_p95") AS "ko_p95"
, PERCENTILE_CONT(0.99) WITHIN GROUP (ORDER BY "ko_p99") AS "ko_p99"
FROM gatlytronx_stats
WHERE 
	"time" >= ? 
AND "time" < ? 
AND "granularity" < ?
GROUP BY "time","type","simulation","scenario","groups","metric","code","granularity"
	 * </code></pre>
	 ***********************************************************************/
	public static String createAggregationSQL(String tablenameStats) {

		String sqlAggregateTempStats =  GatlytronFiles.readPackageResource(GatlytronDBInterface.PACKAGE_RESOURCES, "sql_createTempAggregatedStatistics.sql");
		
		// it's ridiculously complicated, but well... 
		// at least the next guy adjusting anything will be able to backtrack the problem
		sqlAggregateTempStats = sqlAggregateTempStats
							.replaceAll("\\{tempTableName\\}", TEMP_TABLE_AGGREGATION)
							.replaceAll("\\{tableColumnNames\\}", sqlTableColumnNames)
							.replaceAll("\\{originalTableName\\}", tablenameStats)
							.replaceAll("\\{namesWithoutTimeOrGranularity\\}"
									   , fieldNamesJoined
									   			.replaceAll("\"time\",", "")
									   			.replaceAll(",\"granularity\"", "")
									   )
							.replaceAll("\\{groupByNames\\}", fieldNamesJoined)
							.replaceAll("\\{valuesAggregation\\}", RecordMetric.getSQLAggregationPart())
							;
		
		return sqlAggregateTempStats;
	}
	
	/***********************************************************************
	 * Returns a string like "(ColumnName, ColumnName, ...)" containing
	 * the column names of the stats table.
	 ***********************************************************************/
	public static String getSQLTableColumnNames() {
		return sqlTableColumnNames;
	}
	
	/***********************************************************************
	 * Returns an insert statement 
	 ***********************************************************************/
	public boolean insertIntoDatabase(DBInterface db, String tableName) {

		if(db == null || tableName == null) { return false; }
		
		String insertSQL = sqlInsertIntoTemplate.replace("{tablename}", tableName);
	
		ArrayList<Object> valueList = new ArrayList<>();
		
		valueList.add(time);
		valueList.add(type.threeLetters());
		valueList.add(simulation);
		valueList.add(scenario);
		valueList.add(groupsPath);
		valueList.add(metricName);
		valueList.add(code);
		valueList.add(granularity);
		
		for(String name : valueNames) {
			valueList.add(this.getValue(name));
		}
		
		return db.preparedExecute(insertSQL, valueList.toArray());
		

	}
	
	/***********************************************************************
	 * Override hash to group records 
	 ***********************************************************************/
	@Override
	public int hashCode() {
		return statsIdentifier.hashCode();
	}
	
	/***********************************************************************
	 * Override equals to group records 
	 ***********************************************************************/
	@Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }
	
	/***********************************************************************
	 * Returns the time of this record.
	 ***********************************************************************/
	public long getTime() {
		return time;
	}
	
	/***********************************************************************
	 * Returns the type of this record.
	 ***********************************************************************/
	public GatlytronRecordType getType() {
		return type;
	}
	
	/***********************************************************************
	 * Returns the code of the record.
	 ***********************************************************************/
	public String getCode() {
		return code;
	}
	
	/***********************************************************************
	 * Returns the groups path of the record.
	 ***********************************************************************/
	public String getGroupsPath() {
		return groupsPath;
	}
	
	/***********************************************************************
	 * Returns the name of the gatling simulation.
	 ***********************************************************************/
	public String getSimulation() {
		return simulation;
	}
	
	/***********************************************************************
	 * Returns the name of the gatling simulation.
	 ***********************************************************************/
	public String getScenario() {
		return scenario;
	}
	
	/***********************************************************************
	 * Returns the name of the request, or null if this is a user record.
	 ***********************************************************************/
	public String getMetricName() {
		return metricName;
	}
	
	/******************************************************************
	 * Returns the metric path of the metric including groups:
	 *   {group}.{metricName}
	 ******************************************************************/
	public String getMetricPath() {
		return metricPath;
	}
	
	/******************************************************************
	 * Returns the full path of the metric including simulation, scenario
	 * and groups:
	 *   {simulation}.{scenario}.{group}.{metricName}
	 ******************************************************************/
	public String getMetricPathFull() {
		return metricPathFull;
	}
	
	
	/***********************************************************************
	 * 
	 * @param name of a value, e.g. "ko_count" or "ok_max"
	 * @return the value for the given name
	 ***********************************************************************/
	public BigDecimal getValue(String name) {
		BigDecimal val = values.get(name);
		//val = (val == null) ? BigDecimal.ZERO : val;
		return val;
	}
	
	/***********************************************************************
	 * Returns a clone of the values. 
	 ***********************************************************************/
	public HashMap<String, BigDecimal> getValues() {
		HashMap<String, BigDecimal> clone = new HashMap<>();
		clone.putAll(values);
		return clone;
	}
	
	/***********************************************************************
	 * Checks if there is any data in this record.
	 ***********************************************************************/
	public boolean hasData() {
		return this.hasData("ok") || this.hasData("ko") ;
	}
	
	/***********************************************************************
	 * Checks if there is 'ok' data in this record.
	 ***********************************************************************/
	public boolean hasDataOK() {
		return this.hasData("ok");
	}
	
	/***********************************************************************
	 * Checks if there is 'ok' data in this record.
	 ***********************************************************************/
	public boolean hasDataKO() {
		return this.hasData("ko");
	}
			
	/***********************************************************************
	 * Checks if the data is empty.
	 * @param status either 'ok', 'ko' or 'all'. If null checks 'all'
	 ***********************************************************************/
	private boolean hasData(String status) {

		if(status == null) { return false; }
		
		BigDecimal value = this.getValue(status+"_count");
		
		return  (value != null && value.compareTo(BigDecimal.ZERO) != 0) ;

	}
		
}
