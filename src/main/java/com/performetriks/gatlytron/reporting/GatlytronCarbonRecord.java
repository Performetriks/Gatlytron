package com.performetriks.gatlytron.reporting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.performetriks.gatlytron.database.DBInterface;

/***************************************************************************
 * 
 * 
 * 
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronCarbonRecord {

/*
Following are the expected input string formats:
gatling.{simulationName}.users.{scenarioName}.active 1 1725619275
gatling.{simulationName}.users.{scenarioName}.waiting 0 1725619275
gatling.{simulationName}.users.{scenarioName}.done 1 1725619275

gatling.{simulationName}.{requestName}.{ok|ko|all}.count 1 1725627230
gatling.{simulationName}.{requestName}.{ok|ko|all}.min 272 1725627230
gatling.{simulationName}.{requestName}.{ok|ko|all}.max 273 1725627230
gatling.{simulationName}.{requestName}.{ok|ko|all}.mean 273 1725627230
gatling.{simulationName}.{requestName}.{ok|ko|all}.stdDev 0 1725627230
gatling.{simulationName}.{requestName}.{ok|ko|all}.percentiles50 273 1725627230
gatling.{simulationName}.{requestName}.{ok|ko|all}.percentiles75 273 1725627230
gatling.{simulationName}.{requestName}.{ok|ko|all}.percentiles95 273 1725627230
gatling.{simulationName}.{requestName}.{ok|ko|all}.percentiles99 273 1725627230

Target tabular format:
TIMESTAMP, SIMULATION, REQUEST, USER_GROUP, users_active, users_waiting, users_done, ok_count, ok_min ...

 */
	
	private static final Logger logger = LoggerFactory.getLogger(GatlytronCarbonRecord.class);
	
	private String time = null;
	private String simulation = null;
	private String request = null;
	private String user_group = null;
	private HashMap<String, BigDecimal> values = new HashMap<>();
	
	// value Names consist of type + "_" + metric
	private static String[] valueNames = new String[] {
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
	 * Parse the Gatling Carbon Message
	 ***********************************************************************/
	public GatlytronCarbonRecord(String carbonMessage, LinkedHashMap<GatlytronCarbonRecord, GatlytronCarbonRecord> existingRecords) {
		
		if(carbonMessage != null) {

			//-----------------------------------
			// Parse Message
			String[] splittedRecord = carbonMessage.split(" ");
			String[] splittedMetricPath = splittedRecord[0].split("\\.");
			if(splittedMetricPath.length != 5) { logger.warn("!!!Unexpected message format: "+carbonMessage); return; }
			
			simulation = splittedMetricPath[1];
			String type = splittedMetricPath[3];  
			String metric = splittedMetricPath[4];
			String value = splittedRecord[1];
			time = splittedRecord[2];
			
			switch(type) {
				
				case "ok":
				case "ko":
				case "all":
					request = splittedMetricPath[2];
				break;
					
				
				default:
					user_group = type;
					type = "users";
				break;
			}
			
			
			//-----------------------------------
			// Parse Message
			GatlytronCarbonRecord targetForData = existingRecords.get(this);
			
			if(targetForData == null) {
				targetForData = this;
				existingRecords.put(this, this);
			}
			
			targetForData.addValue(type, metric, value);
			
		}
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
			BigDecimal val = values.get(name);
			val = (val == null) ? BigDecimal.ZERO : val;
			object.addProperty(name, val);
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
		
		valueList.add(Integer.parseInt(time));
		valueList.add(simulation);
		valueList.add(request);
		valueList.add(user_group);
		
		for(String name : valueNames) {
			BigDecimal val = values.get(name);
			val = (val == null) ? BigDecimal.ZERO : val;
			valueList.add(val);
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
	
	public String time() {
		return time;
	}
	
	
	
}