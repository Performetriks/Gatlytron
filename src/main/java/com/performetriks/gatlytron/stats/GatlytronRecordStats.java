package com.performetriks.gatlytron.stats;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.performetriks.gatlytron.database.DBInterface;

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
	private String simulation = null;
	private String request = null;
	private String user_group = null;
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
		  "users_active"
		, "users_waiting"
		, "users_done"
			
		, "ok_count"
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
			
		, "all_count"
		, "all_min"
		, "all_max"
		, "all_mean"
		, "all_stdev"
		, "all_p50"
		, "all_p75"
		, "all_p95"
		, "all_p99"
	};
	
	private static String csvHeaderTemplate = "time,simulation,request,user_group";
	private static String sqlCreateTableTemplate = "CREATE TABLE IF NOT EXISTS {tablename} ("
			+ "		  time BIGINT,\r\n"
			+ "		  simulation VARCHAR(4096),\r\n"
			+ "		  request VARCHAR(4096),\r\n"
			+ "		  user_group VARCHAR(4096)"
			;
	
	private static String sqlInsertIntoTemplate = "INSERT INTO {tablename} (time,simulation,request,user_group";

	static {
		
		//-----------------------------------------
		// CSV Template
		for(String name : valueNames) {
			csvHeaderTemplate += ","+name;
		}
		csvHeaderTemplate += "\r\n";
		
		//-----------------------------------------
		// SQL Create Table Template
		for(String name : valueNames) {
			sqlCreateTableTemplate += ",\r\n		  "+name+" DECIMAL(32,3)";
		}
		sqlCreateTableTemplate += ");";

		//-----------------------------------------
		// SQL Insert Into Template
		String sqlInsertValues = "VALUES (?, ?, ?, ?";
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
	 ***********************************************************************/
	public GatlytronRecordStats(
							  LinkedHashMap<GatlytronRecordStats, GatlytronRecordStats> existingRecords
							, String status
						    , long timeMillis
							, String simulation
							, String metricName
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
		this.simulation = simulation;
		this.request = metricName;

		
		//-----------------------------------
		// Get Target Record
		GatlytronRecordStats targetForData = existingRecords.get(this);
		
		if(targetForData == null) {
			targetForData = this;
			existingRecords.put(this, this);
		}
		
		//-----------------------------------
		// Add Values
		String statusLower = status.trim().toLowerCase();  
		
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
								.replace("percentiles", "p")
								.replace("stdDev", "stdev")
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
					+ separator + simulation 
					+ separator + request 
					+ separator + user_group
					;
				
		for(String name : valueNames) {
			BigDecimal val = values.get(name);
			val = (val == null) ? BigDecimal.ZERO : val;
			csv += separator + val; 
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
		object.addProperty("simulation", simulation);
		object.addProperty("request", request);
		object.addProperty("user_group", user_group);
		
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
		valueList.add(simulation);
		valueList.add(request);
		valueList.add(user_group);
		
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
		return (time + simulation + request + user_group).hashCode();
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
	 * Returns the name of the gatling simulation.
	 ***********************************************************************/
	public String getSimulation() {
		return simulation;
	}
	
	/***********************************************************************
	 * Returns the name of the request, or null if this is a user record.
	 ***********************************************************************/
	public String getRequest() {
		return request;
	}
	
	/***********************************************************************
	 * Returns the name of the user group, or null if this is a user record.
	 ***********************************************************************/
	public String getUserGroup() {
		return user_group;
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
	 * Checks if there is no data in this record.
	 ***********************************************************************/
	public boolean hasRequestData() {
		return this.hasRequestData("ok") || this.hasRequestData("ko") ;
	}
	
	/***********************************************************************
	 * Checks if there is no 'ok' data in this record.
	 ***********************************************************************/
	public boolean hasRequestDataOK() {
		return this.hasRequestData("ok");
	}
	
	/***********************************************************************
	 * Checks if there is no 'ok' data in this record.
	 ***********************************************************************/
	public boolean hasRequestDataKO() {
		return this.hasRequestData("ko");
	}
			
	/***********************************************************************
	 * Checks if the data is empty.
	 * @param type either 'ok', 'ko' or 'all'. If null checks 'all'
	 ***********************************************************************/
	private boolean hasRequestData(String type) {

		if(type == null) { type = "all"; }
		
		BigDecimal value = this.getValue(type+"_count");
		
		return  (value != null && value.compareTo(BigDecimal.ZERO) != 0) ;

	}
	
	/***********************************************************************
	 * Returns true if this is a request record.
	 ***********************************************************************/
	public boolean isRequestRecord() {
		return  (request != null);
	}
	
	/***********************************************************************
	 * Returns true if this is a user record.
	 ***********************************************************************/
	public boolean isUserRecord() {
		return  (user_group != null);
	}
	
	
	
	
}
