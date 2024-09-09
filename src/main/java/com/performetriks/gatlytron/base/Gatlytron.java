package com.performetriks.gatlytron.base;

import java.util.ArrayList;

import com.performetriks.gatlytron.reporting.GatlytronCarbonReceiver;
import com.performetriks.gatlytron.reporting.GatlytronReporter;

/***************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class Gatlytron {

	private static ArrayList<GatlytronReporter> reporterList = new ArrayList<>();
	
	
	/******************************************************************
	 * Add reporters to the list.
	 ******************************************************************/
	public static void addReporter(GatlytronReporter reporter) {
		reporterList.add(reporter);
	}
	
	/******************************************************************
	 * Returns the list of added reporters.
	 * 
	 ******************************************************************/
	@SuppressWarnings("unchecked")
	public static ArrayList<GatlytronReporter> getReporterList() {
		return (ArrayList<GatlytronReporter>) reporterList.clone();
	}
	
	/******************************************************************
	 * Enables the Gatlytron Graphite Receiver to do custom reports.
	 * @param port
	 ******************************************************************/
	public static void enableGraphiteReceiver(int port) {
		GatlytronCarbonReceiver.start(port);
	}
	
	/******************************************************************
	 * Returns the list of added reporters.
	 * 
	 ******************************************************************/
	@SuppressWarnings("unchecked")
	public static void terminate() {
		
		GatlytronCarbonReceiver.terminate();
		
		for(GatlytronReporter reporter : reporterList) {
			reporter.terminate();
		}
	}
	
	
	
	
}
