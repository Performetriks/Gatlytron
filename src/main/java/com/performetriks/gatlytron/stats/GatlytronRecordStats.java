package com.performetriks.gatlytron.stats;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.performetriks.gatlytron.database.DBInterface;
import com.performetriks.gatlytron.stats.GatlytronRecordRaw.GatlytronRecordType;

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
	
	private static final Logger logger = LoggerFactory.getLogger(GatlytronRecordStats.class);
	
	private long time;
	private GatlytronRecordType type;
	private String simulation;
	private String scenario;
	private String metricName;
	private String code;
	private String statsIdentifier;
	private HashMap<String, BigDecimal> values = new HashMap<>();
	
	// list of metric names
	public static final String[] metricNames = new String[] {
		  "count"
		, "min"
		, "max"
		, "mean"
		, "stdev"
		, "p50"
		, "p75"
		, "p95"
		, "p99"
	};
	
	// value names consist of type + "_" + metric
	public static final String[] valueNames = new String[] {		
		  "ok_count"
		, "ok_min"
		, "ok_max"
		, "ok_mean"
		, "ok_stdev"
		, "ok_p50"
		, "ok_p75"
		, "ok_p95"
		, "ok_p99"
			
		, "ko_count"
		, "ko_min"
		, "ko_max"
		, "ko_mean"
		, "ko_stdev"
		, "ko_p50"
		, "ko_p75"
		, "ko_p95"
		, "ko_p99"
	};		
//		, "all_count"
//		, "all_min"
//		, "all_max"
//		, "all_mean"
//		, "all_stdev"
//		, "all_p50"
//		, "all_p75"
//		, "all_p95"
//		, "all_p99"
//	};
	
	private static String csvHeaderTemplate = "time,type,simulation,scenario,metric,code";
	private static String sqlCreateTableTemplate = "CREATE TABLE IF NOT EXISTS {tablename} ("
			+ "		  time BIGINT,\r\n"
			+ "		  type VARCHAR(16),\r\n"
			+ "		  simulation VARCHAR(4096),\r\n"
			+ "		  scenario VARCHAR(4096),\r\n"
			+ "		  metric VARCHAR(4096),\r\n"
			+ "		  code VARCHAR(16)"
			;
	
	private static String sqlInsertIntoTemplate = "INSERT INTO {tablename} (time,type,simulation,scenario,metric,code";

	static {
		
		//-----------------------------------------
		// CSV Template
		for(String name : valueNames) {
			csvHeaderTemplate += ","+name;
		}
		//csvHeaderTemplate += "\r\n";
		
		//-----------------------------------------
		// SQL Create Table Template
		for(String name : valueNames) {
			sqlCreateTableTemplate += ",\r\n		  "+name+" DECIMAL(32,3)";
		}
		sqlCreateTableTemplate += ");";

		//-----------------------------------------
		// SQL Insert Into Template
		String sqlInsertValues = "VALUES (?, ?, ?, ?, ?, ?";
		for(String name : valueNames) {
			sqlInsertIntoTemplate += ","+name;
			sqlInsertValues += ", ?";
		}
		sqlInsertValues += ")";
		sqlInsertIntoTemplate += ") " + sqlInsertValues;
		
		
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
		this.code = record.getResponseCode();
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
		
		targetForData.addValue(statusLower, "count", count.toPlainString());
		targetForData.addValue(statusLower, "min", min.toPlainString());
		targetForData.addValue(statusLower, "max", max.toPlainString());
		targetForData.addValue(statusLower, "mean", mean.toPlainString());
		targetForData.addValue(statusLower, "stdev", stdev.toPlainString());
		targetForData.addValue(statusLower, "p50", p50.toPlainString());
		targetForData.addValue(statusLower, "p75", p75.toPlainString());
		targetForData.addValue(statusLower, "p95", p95.toPlainString());
		targetForData.addValue(statusLower, "p99", p99.toPlainString());
		
	}	
	
	/***********************************************************************
	 * 
	 * @param type either ok | ko | all | users
	 ***********************************************************************/
	private void addValue(String type, String metric, String value) {
		
		BigDecimal parsedInt = new BigDecimal(value);
		String finalMetric = metric
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
					+ separator + metricName.replace(separator, "_")  
					+ separator + code.replace(separator, "_")  
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
		
		object.addProperty("time", time);
		object.addProperty("type", type.threeLetters());
		object.addProperty("simulation", simulation);
		object.addProperty("scenario", scenario);
		object.addProperty("metric", metricName);
		object.addProperty("code", code);
		
		for(String name : valueNames) {
			object.addProperty(name, this.getValue(name));
		}
		
		return object;

	}
	
	/***********************************************************************
	 * Returns a CSV header
	 ***********************************************************************/
	public static String getSQLCreateTableTemplate(String tableName) {
		return sqlCreateTableTemplate.replace("{tablename}", tableName);
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
		valueList.add(metricName);
		valueList.add(code);
		
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
	 * Returns the time of this record.
	 ***********************************************************************/
	public GatlytronRecordType getType() {
		return type;
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
	
	
	/***********************************************************************
	 * 
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
