package com.performetriks.gatlytron.injection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.performetriks.gatlytron.base.Gatlytron;
import com.performetriks.gatlytron.stats.GatlytronRecordRaw;
import com.performetriks.gatlytron.stats.GatlytronRecordRaw.GatlytronRecordType;
import com.performetriks.gatlytron.stats.GatlytronStatsEngine;

import io.gatling.commons.stats.Status;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;

/***************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class InjectedDataReceiver {
	
	private static final Object SYNC_LOCK = new Object();
	
	// Contains scenario name and user count
	private static final LinkedHashMap<String, Integer> scenarioUsersActive = new LinkedHashMap<>();
	private static final LinkedHashMap<String, Integer> scenarioUsersTotalStarted = new LinkedHashMap<>();
	private static final LinkedHashMap<String, Integer> scenarioUsersTotalStopped = new LinkedHashMap<>();
	
	/***************************************************************************
	 * 
	 ***************************************************************************/
	private static void countUsers(String scenario, boolean isStart) {
		
		//-------------------------------
		// Initialize
		synchronized (SYNC_LOCK) {
			if(!scenarioUsersActive.containsKey(scenario)) {		scenarioUsersActive.put(scenario, 0); }
			if(!scenarioUsersTotalStarted.containsKey(scenario)) {	scenarioUsersTotalStarted.put(scenario, 0); }
			if(!scenarioUsersTotalStopped.containsKey(scenario)) {	scenarioUsersTotalStopped.put(scenario, 0); }
		}
	
		//-------------------------------
		// Make Count
		int current = scenarioUsersActive.get(scenario);

		if(isStart) {
			scenarioUsersActive.put(scenario, ++current);
			
			int started = scenarioUsersTotalStarted.get(scenario);
			scenarioUsersTotalStarted.put(scenario, ++started);
			
		}else {
			scenarioUsersActive.put(scenario, --current);
			
			int stopped = scenarioUsersTotalStopped.get(scenario);
			scenarioUsersTotalStopped.put(scenario, ++stopped);
			
		}
		
	}
	
	/***************************************************************************
	 * 
	 ***************************************************************************/
	private static int getUsersActive(String scenario) {
		return scenarioUsersActive.get(scenario);
	}
	/***************************************************************************
	 * 
	 ***************************************************************************/
	private static int getUsersStarted(String scenario) {
		return scenarioUsersTotalStarted.get(scenario);
	}
	
	/***************************************************************************
	 * 
	 ***************************************************************************/
	private static int getUsersStopped(String scenario) {
		return scenarioUsersTotalStopped.get(scenario);
	}
	
	/***************************************************************************
	 * Call of this method is injected into gatling code.
	 * 
	 ***************************************************************************/
	public static void logUserStart(String scenario) {
		countUsers(scenario, true);
	}
	
	/***************************************************************************
	 * Call of this method is injected into gatling.
	 * 
	 ***************************************************************************/
	public static void logUserEnd(String scenario) {
		countUsers(scenario, false);
	}
	
	/***************************************************************************
	 * Call of this method is injected into gatling.
	 * 
	 ***************************************************************************/
	public static void logResponse(
			  String scenario
			, List<String> groups
			, String requestName
			, long startTimestamp
			, long endTimestamp
			, Status status
			, Option<?> responseCode
			, Option<?> message
			){
		
		long duration = -1;
		if(endTimestamp > 0 ) {
			duration = endTimestamp - startTimestamp;
		}
		
		createRecord(
				  GatlytronRecordType.REQUEST
				, scenario
				, JavaConverters.asJava(groups)
				, requestName
				, startTimestamp
				, endTimestamp
				, status.name()
				, ( ( responseCode.isDefined() ) ? responseCode.get().toString() : "000" )
				, message.toString()
				, duration
				);	
	}
	
	
	
	
	/***************************************************************************
	 * INTERNAL METHOD
	 * Will be called by the GatlytronStatsEngine to create records, periodically.
	 * 
	 ***************************************************************************/
	public static void createUserRecords() {
		
		long now = System.currentTimeMillis();
		
		//---------------------------------------
		// Users Active
		for(String scenario : scenarioUsersActive.keySet()) {
			createRecord(
					  GatlytronRecordType.USER
					, scenario
					, null
					, "users.active"
					, now
					, now
					, "OK"
					, "000"
					, "NONE"
					, getUsersActive(scenario)
				);	
		}

		
		//---------------------------------------
		// Users Started
		for(String scenario : scenarioUsersTotalStarted.keySet()) {
			createRecord(
					  GatlytronRecordType.USER
					, scenario
					, null
					, "users.total_started"
					, now
					, now
					, "OK"
					, "000"
					, "NONE"
					, getUsersStarted(scenario)
				);	
		}
		
		//---------------------------------------
		// Users Stopped
		for(String scenario : scenarioUsersTotalStopped.keySet()) {
			createRecord(
					GatlytronRecordType.USER
					, scenario
					, null
					, "users.total_stopped"
					, now
					, now
					, "OK"
					, "000"
					, "NONE"
					, getUsersStopped(scenario)
				);	
		}
	}
	
	/***************************************************************************
	 * Shorthand function that creates a record that will be 
	 * included in the reported values.
	 * 
	 * @param type the record type
	 * @param scenario the name of the scenario
	 * @param metricName the name of the metric
	 * @param startTimestamp the start time in epoch milliseconds
	 * @param endTimestamp  the end time in epoch milliseconds
	 * @param status the status, either "KO", "OK" or "ALL"
	 * @param responseCode a response code, e.g. HTTP Status Code
	 * @param metricValue 
	 ***************************************************************************/
	public static void createRecord(
			  String scenario
			, String metricName
			, long startTimestamp
			, long endTimestamp
			, String status
			, String responseCode
			, long metricValue
			){
		
		createRecord(
				  GatlytronRecordType.REQUEST
				, scenario
				, null
				, metricName
				, startTimestamp
				, endTimestamp
				, status
				, responseCode
				, "NONE"
				, metricValue
			);
	}
	
	
	/***************************************************************************
	 * Creates a record that will be included in the reported values.
	 * 
	 * @param type the record type
	 * @param scenario the name of the scenario
	 * @param groups the groups as a list of strings
	 * @param metricName the name of the metric
	 * @param startTimestamp the start time in epoch milliseconds
	 * @param endTimestamp  the end time in epoch milliseconds
	 * @param status the status, either "KO", "OK" or "ALL"
	 * @param responseCode a response code, e.g. HTTP Status Code
	 * @param message custom message
	 * @param metricValue 
	 ***************************************************************************/
	public static void createRecord(
			  GatlytronRecordType type
			, String scenario
			, java.util.List<String> groups
			, String metricName
			, long startTimestamp
			, long endTimestamp
			, String status
			, String responseCode
			, String message
			, long metricValue
			){
		
		//----------------------------------
		// Create Record
		GatlytronRecordRaw record = new GatlytronRecordRaw(
				  type
				, Gatlytron.getSimulationName()
				, scenario
				, groups
				, metricName
				, startTimestamp
				, endTimestamp
				, status
				, responseCode
				, message
				, new BigDecimal(metricValue)
				);
		
		//----------------------------------
		// Print Sysout
		if(Gatlytron.isRawDataToSysout()) {
			System.out.println(record.toLogString());
		}
		
		//----------------------------------
		// Print Raw
		Gatlytron.writeToRawDataLog(record.toLogString()+"\n");
		
		//----------------------------------
		// Stats 
		GatlytronStatsEngine.addRecord(record);
		
	}

}
