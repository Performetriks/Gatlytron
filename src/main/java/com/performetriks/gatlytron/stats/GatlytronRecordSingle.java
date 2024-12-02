package com.performetriks.gatlytron.stats;

import java.util.ArrayList;
import java.util.List;

/***************************************************************************
 * This class holds one single record retrieved from Gatling.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronRecordSingle {
	
	private String scenario = "unnamedScenario";
	
	private List<String> groups = new ArrayList<>();
	private String requestName = "unnamedRequest";
	private String identifierString = "";
	private long startTimestamp = -1;
	private long endTimestamp = -1;
	private long duration = -1;
	private String status = "??";
	private String responseCode = "XXX";
	private String message = "none";
	
	private String logString = null;

	/******************************************************************
	 * 
	 ******************************************************************/
	public GatlytronRecordSingle(
			String scenario
			, List<String> groups
			, String requestName
			, long startTimestamp
			, long endTimestamp
			, String status
			, String responseCode
			, String message
			){

		//-----------------------
		// Initialize null-safe
		if(scenario != null && !scenario.isBlank() ) {			this.scenario = scenario; }
		if(groups != null ) {									this.groups = groups; }
		if(requestName != null && !requestName.isBlank() ) {	this.requestName = requestName; }
		if(status != null && !status.isBlank() ) {				this.status = status; }
		if(responseCode != null && !responseCode.isBlank() ) {	this.responseCode = responseCode; }
		if(message != null && !message.isBlank() ) {			this.message = message; }
		
		this.startTimestamp = startTimestamp; 
		this.endTimestamp = endTimestamp; 
		
		//-----------------------
		// Calculate Duration
		if(this.endTimestamp > 0) {
			this.duration = this.endTimestamp - this.startTimestamp;
		}
		
		//-----------------------
		// Create Fullname
		this.identifierString += scenario;
		if( !groups.isEmpty() ) {
			this.identifierString += "/" + getGroupsAsString("/", "");
		}	
		
		this.identifierString += requestName;
		this.identifierString += status;
		
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
			.append( status ).append(" ")
			.append( responseCode ).append(" ")
			.append( startTimestamp ).append(" ")
			.append( endTimestamp ).append(" ")
			.append( scenario.replaceAll(" ", "_") ).append(" ")
			.append( getGroupsAsString("/", "noGroup").replaceAll(" ", "_") ).append(" ")
			.append( requestName.replaceAll(" ", "_") ).append(" ")
			.append( duration ).append(" ")
				;
		
		logString = builder.toString();
		return logString;
	}
	
	
}
