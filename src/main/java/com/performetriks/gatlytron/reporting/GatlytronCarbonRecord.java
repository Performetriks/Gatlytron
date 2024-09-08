package com.performetriks.gatlytron.reporting;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

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
	private HashMap<String, Integer> values = new HashMap<>();
	
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
	
	static {
		
		for(String name : valueNames) {
			csvHeaderTemplate += ","+name;
		}
		csvHeaderTemplate += "\r\n";
		
	}

	/***********************************************************************
	 * Parse the Gatling Carbon Message
	 ***********************************************************************/
	public GatlytronCarbonRecord(String carbonMessage, LinkedHashMap<GatlytronCarbonRecord, GatlytronCarbonRecord> existingRecords) {
		
		if(carbonMessage != null) {

			//-----------------------------------
			// Parse Message
			String[] splitted = carbonMessage.split("\\.");
			
			if(splitted.length != 5) { logger.warn("!!!Unexpected message format: "+carbonMessage); return; }
			
			simulation = splitted[1];
			String type = splitted[3];  
			
			switch(type) {
				
				case "ok":
				case "ko":
				case "all":
					request = splitted[2];
				break;
					
				
				default:
					user_group = type;
					type = "users";
				break;
			}
			
			String[] metricValueTimestamp = splitted[4].split(" ");
			String metric = metricValueTimestamp[0];
			String value = metricValueTimestamp[1];
			time = metricValueTimestamp[2];
			
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
		
		int parsedInt = Integer.parseInt(value);
		String finalMetric = metric.replace("percentiles", "p");  // make it shorter to reduce footprint

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
			Integer val = values.get(name);
			val = (val == null) ? 0 : val;
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
			Integer val = values.get(name);
			val = (val == null) ? 0 : val;
			object.addProperty(name, val);
		}
		
		return object;

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
