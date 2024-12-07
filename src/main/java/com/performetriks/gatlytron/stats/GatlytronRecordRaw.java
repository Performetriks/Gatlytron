package com.performetriks.gatlytron.stats;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/***************************************************************************
 * This class holds one single raw record retrieved from Gatling.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronRecordRaw {
	
	private String scenario = "unnamedScenario";
	
	private List<String> groups = new ArrayList<>();
	private GatlytronRecordType type = GatlytronRecordType.UNKNOWN;
	private String metricName = "unnamedRequest";
	private String statsIdentifier = "";
	private long startTimestamp = -1;
	private long endTimestamp = -1;
	private String status = "??";
	private String responseCode = "000";
	private String message = "none";
	
	private BigDecimal metricValue = null;
	
	private String logString = null;

	public enum GatlytronRecordType{
		  REQUEST("REQ")
		, USER("USR")
		, UNKNOWN("???")
		;
		
		private String threeLetters;
		
		private GatlytronRecordType(String threeLetters) {
			this.threeLetters = threeLetters;
		}
		
		public String threeLetters() {
			return threeLetters;
		}
	}
	/******************************************************************
	 * 
	 ******************************************************************/
	public GatlytronRecordRaw(
			  GatlytronRecordType type
			, String scenario
			, List<String> groups
			, String metricName
			, long startTimestamp
			, long endTimestamp
			, String status
			, String responseCode
			, String message
			, BigDecimal metricValue
			){

		//-----------------------
		// Initialize null-safe
		if(type != null ) {		this.type = type; }
		if(groups != null ) {	this.groups = groups; }
		
		if(scenario != null && !scenario.isBlank() ) {			this.scenario = scenario; }
		if(metricName != null && !metricName.isBlank() ) {	this.metricName = metricName; }
		if(status != null && !status.isBlank() ) {				this.status = status; }
		if(responseCode != null && !responseCode.isBlank() ) {	this.responseCode = responseCode; }
		if(message != null && !message.isBlank() ) {			this.message = message; }
		
		this.startTimestamp = startTimestamp; 
		this.endTimestamp = endTimestamp; 
		this.metricValue = metricValue; 
		
		//-----------------------
		// Create Fullname
		this.statsIdentifier += type;
		this.statsIdentifier += scenario;
		if( !this.groups.isEmpty() ) {
			this.statsIdentifier += "/" + getGroupsAsString("/", "");
		}	
		
		this.statsIdentifier += metricName;
		this.statsIdentifier += status;
		this.statsIdentifier += responseCode;
		
	}
	
	
	/******************************************************************
	 * 
	 ******************************************************************/
	public String getStatsIdentifier() {
		return statsIdentifier;
	}
	/******************************************************************
	 * 
	 ******************************************************************/
	public String getGroupsAsString(String separator, String fallbackForNoGrouping) {
		if(groups.isEmpty()) { return fallbackForNoGrouping; }
		
		return String.join(separator, groups);
		
	}
	
	/******************************************************************
	 * 
	 ******************************************************************/
	public String toLogString() {
		
		//--------------------------
		// Return Cached String
		if(logString != null) { return logString; }
		
		//--------------------------
		// Create Log String
		StringBuilder builder = new StringBuilder();
		
		builder
			.append( type.threeLetters() ).append(" ")
			.append( status ).append(" ")
			.append( responseCode ).append(" ")
			.append( startTimestamp ).append(" ")
			.append( endTimestamp ).append(" ")
			.append( scenario.replaceAll(" ", "_") ).append(" ")
			.append( getGroupsAsString("/", "noGroup").replaceAll(" ", "_") ).append(" ")
			.append( metricName.replaceAll(" ", "_") ).append(" ")
			.append( metricValue ).append(" ")
				;
		
		logString = builder.toString();
		return logString;
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public String getScenario() {
		return scenario;
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public List<String> getGroups() {
		return groups;
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public GatlytronRecordType getType() {
		return type;
	}

	/******************************************************************
	 * Returns the full name of the metric:
	 * 	{scenario}.{group}.{metricName}
	 ******************************************************************/
	public String getMetricPath() {
		
		if(groups.isEmpty()) {
			return scenario.replaceAll(" ", "_")
		   + "." + metricName.replaceAll(" ", "_")
		   ;
		}
		
		return scenario.replaceAll(" ", "_") 
				+ "." + getGroupsAsString(".", "noGroup").replaceAll(" ", "_")
				+ "." + metricName.replaceAll(" ", "_");
		
	}
	
	/******************************************************************
	 * Returns the simple name of the metric
	 ******************************************************************/
	public String getMetricName() {
		return metricName;
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public long getStartTimestamp() {
		return startTimestamp;
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public long getEndTimestamp() {
		return endTimestamp;
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public String getStatus() {
		return status;
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public String getResponseCode() {
		return responseCode;
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public String getMessage() {
		return message;
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public BigDecimal getMetricValue() {
		return metricValue;
	}
	
	/******************************************************************
	 * 
	 ******************************************************************/
	public String getLogString() {
		return logString;
	}
	
	
	
	
}
