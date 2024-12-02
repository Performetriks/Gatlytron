package com.performetriks.gatlytron.base;

import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.performetriks.gatlytron.reporting.GatlytronReporter;

import ch.qos.logback.classic.Level;

/***************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class Gatlytron {
	
	private static final Logger logger = LoggerFactory.getLogger(Gatlytron.class);
	
	private static ArrayList<GatlytronReporter> reporterList = new ArrayList<>();
	private static ArrayList<GatlytronScenario> scenarioList = new ArrayList<>();
	
	private static boolean debug = false;
	private static boolean keepEmptyRecords = false;
	
	private static boolean rawDataToSysout = false;
	private static boolean rawStatsToLogFile = false;
	
	public static final String EXECUTION_ID = UUID.randomUUID().toString();
	public static final long STARTTIME_MILLIS = System.currentTimeMillis();
	public static final long STARTTIME_SECONDS = STARTTIME_MILLIS / 1000;
	
	
	/******************************************************************
	 * Add reporters to the list.
	 ******************************************************************/
	public static void addReporter(GatlytronReporter reporter) {
		logger.info("Adding Reporter: " + reporter.getClass().getSimpleName());
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
	 * For internal use only.
	 * Adds a scenario to the list of scenarios.
	 ******************************************************************/
	public static void addScenario(GatlytronScenario scenario) {
		scenarioList.add(scenario);
	}
	
	/******************************************************************
	 * Returns the list of scenarios.
	 * 
	 ******************************************************************/
	@SuppressWarnings("unchecked")
	public static ArrayList<GatlytronScenario> getScenarioList() {
		return (ArrayList<GatlytronScenario>) scenarioList.clone();
	}
	
	/******************************************************************
	 * Terminates the reporters.
	 * 
	 ******************************************************************/
	@SuppressWarnings("unchecked")
	public static void terminate() {
		logger.info("Terminating Gatlytron");

		for(GatlytronReporter reporter : reporterList) {
			reporter.terminate();
		}
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public static boolean isKeepEmptyRecords() {
		return keepEmptyRecords;
	}
	

	/******************************************************************
	 * 
	 ******************************************************************/
	public static void setKeepEmptyRecords(boolean skipEmptyRecords) {
		Gatlytron.keepEmptyRecords = skipEmptyRecords;
	}
	
	
	/******************************************************************
	 * 
	 ******************************************************************/
	public static boolean isRawDataToSysout() {
		return rawDataToSysout;
	}
	
	/******************************************************************
	 * 
	 ******************************************************************/
	public static void setRawDataToSysout(boolean rawDataToSysout) {
		Gatlytron.rawDataToSysout = rawDataToSysout;
	}



	/******************************************************************
	 * Sets the level of the logback root logger.
	 ******************************************************************/
	public static void setLogLevelRoot(Level level) {

		String loggerName = ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME;
		
		setLogLevel(level, loggerName);
		
	}
	
	/******************************************************************
	 * Sets the level of the logback of the selected logger.
	 ******************************************************************/
	public static void setLogLevel(Level level, String loggerName) {
		ch.qos.logback.classic.Logger logger = 
				(ch.qos.logback.classic.Logger) 
				org.slf4j.LoggerFactory.getLogger(loggerName);
		
	    logger.setLevel(level);
	}
	
	/******************************************************************
	 * 
	 ******************************************************************/
	public static void setDebug(boolean debug) {
		Gatlytron.debug = debug;
	}
	
	/******************************************************************
	 * 
	 ******************************************************************/
	public static boolean isDebug() {
		return debug;
	}
	
	
	
	
	
}
