package com.performetriks.gatlytron.base;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.performetriks.gatlytron.reporting.GatlytronCarbonReceiver;
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
	
	private static boolean debug = false;
	private static boolean keepEmptyRecords = false;
	
	/******************************************************************
	 * Enables the Gatlytron Graphite Receiver to do custom reports.
	 * @param port
	 ******************************************************************/
	public static void enableGraphiteReceiver(int port) {
		logger.info("Starting Carbon Receiver");
		GatlytronCarbonReceiver.start(port);
	}
	
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
	 * Returns the list of added reporters.
	 * 
	 ******************************************************************/
	@SuppressWarnings("unchecked")
	public static void terminate() {
		logger.info("Terminating Gatlytron");
		GatlytronCarbonReceiver.terminate();
		
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
	public static boolean isDebug() {
		return debug;
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
	
	
	
	
	
}
