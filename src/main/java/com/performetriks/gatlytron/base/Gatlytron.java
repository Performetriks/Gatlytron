package com.performetriks.gatlytron.base;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.performetriks.gatlytron.database.GatlytronAgeOutConfig;
import com.performetriks.gatlytron.reporting.GatlytronReporter;
import com.performetriks.gatlytron.stats.GatlytronStatsEngine;

import ch.qos.logback.classic.Level;
import io.gatling.core.config.GatlingConfiguration;
import scala.concurrent.duration.FiniteDuration;

/***************************************************************************
 * Main Gatlytron class used for configuration and controlling the framework.
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
	
	private static String simulationName = "UnknownSimulation";
	private static boolean debug = false;
	private static boolean keepEmptyRecords = false;
	
	private static boolean databaseAgeOut = false;
	private static GatlytronAgeOutConfig databaseAgeOutConfig = new GatlytronAgeOutConfig(); // use defaults
	
	
	private static boolean rawDataToSysout = false;
	private static String rawdataLogPath = null;
	private static BufferedWriter rawDataLogWriter = null;
	
	public static final String EXECUTION_ID = UUID.randomUUID().toString();
	public static final long STARTTIME_MILLIS = System.currentTimeMillis();
	
	private static int reportingIntervalSec = 15; 
	
	/******************************************************************
	 * Starts Gatlytron and the reporting engine.
	 * 
	 * @param reportingInterval number of seconds for the reporting
	 * used to aggregate statistics and reporting them to the various
	 * reporters.
	 * 
	 ******************************************************************/
	public static void start(int reportingInterval) {
		reportingIntervalSec = reportingInterval;
		GatlytronStatsEngine.start(reportingInterval);
	}
	
	/******************************************************************
	 * Add reporters to the list.
	 ******************************************************************/
	public static void addReporter(GatlytronReporter reporter) {
		logger.info("Adding Reporter: " + reporter.getClass().getSimpleName());
		reporterList.add(reporter);
	}
	
	/******************************************************************
	 * Returns the list of the reporters.
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
	 * Enable or disable writing raw data to sysout.
	 ******************************************************************/
	public static void setRawDataToSysout(boolean rawDataToSysout) {
		Gatlytron.rawDataToSysout = rawDataToSysout;
	}
	
	/******************************************************************
	 * Set the log path for raw data.
	 * Will also activate the logging of raw data.
	 ******************************************************************/
	public static void setRawDataLogPath(String rawdataLogPath) {
		Gatlytron.rawdataLogPath = rawdataLogPath;
		
		try {
			rawDataLogWriter = new BufferedWriter(new FileWriter(rawdataLogPath, true));
		} catch (IOException e) {
			logger.error("Error while initializing raw data log writer.", e);
		}
	}
	
	/******************************************************************
	 * 
	 ******************************************************************/
	public static String getRawDataLogPath() {
		return Gatlytron.rawdataLogPath;
	}
	
	/******************************************************************
	 * Returns The console write Period in seconds.
	 ******************************************************************/
	public static int getConsoleWritePeriodSeconds() {
		FiniteDuration duration = GatlingConfiguration.load().data().console().writePeriod();
		
		return (int)duration.toSeconds();
	}
	
	/******************************************************************
	 * INTERNAL USE ONLY
	 * This method writes the raw data to the raw data log file. 
	 ******************************************************************/
	public static void writeToRawDataLog(String rawData) {

		if(rawDataLogWriter == null) { return; }
		
		try {
			rawDataLogWriter.write(rawData);
		} catch (IOException e) {
			logger.error("Error while writing raw data.", e);
		}
			
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
	public static String getSimulationName() {
		return simulationName;
	}

	/******************************************************************
	 * 
	 ******************************************************************/
	public static void setSimulationName(String simulationName) {
		Gatlytron.simulationName = simulationName;
	}
	

	/******************************************************************
	 * Enable age out of data that was reported to Databases.
	 * Default is false.
	 * This must be called before constructors of DB Reporters are called
	 * to have an effect, as the age out will be triggered in the
	 * constructor.
	 * 
	 * @param doAgeOut 
	 ******************************************************************/
	public static void setAgeOut(boolean doAgeOut) {
		Gatlytron.databaseAgeOut = doAgeOut;
	}
	
	/******************************************************************
	 * 
	 ******************************************************************/
	public static boolean isAgeOut() {
		return databaseAgeOut;
	}
	
	/******************************************************************
	 * Sets the age out config.
	 * Only takes effect if AgeOut has been enabled.
	 * 
	 * @param config 
	 ******************************************************************/
	public static void setAgeOutConfig(GatlytronAgeOutConfig config) {
		Gatlytron.databaseAgeOutConfig = config;
	}

	
	/******************************************************************
	 * Sets the age out config.
	 * Only takes effect if AgeOut has been enabled.
	 * 
	 * @param config
	 * 
	 * @return the AgeOutConfig 
	 ******************************************************************/
	public static GatlytronAgeOutConfig getAgeOutConfig() {
		return Gatlytron.databaseAgeOutConfig;
	}
	
	/******************************************************************
	 * Toggles certain debug information.
	 * You can use this to also toggle your own debug logs by
	 * retrieving this values using the isDebug()-method.
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
	
	/******************************************************************
	 * Returns the report interval in seconds.
	 ******************************************************************/
	public static int getReportInterval() {
		return reportingIntervalSec;
	}
	
	/******************************************************************
	 * Terminates Gatlytron.
	 * 
	 ******************************************************************/
	public static void terminate() {
		logger.info("Terminating Gatlytron");

		//--------------------------------
		// Stop Stats Engine
		try {
			GatlytronStatsEngine.stop();
		} catch (Exception e) {
			logger.error("Error while stopping GatlytronStatsEngine.", e);
		}
		
		//--------------------------------
		// Close Raw Log Writer
		if(rawDataLogWriter != null) {
			try {
				rawDataLogWriter.flush();
				rawDataLogWriter.close();
			} catch (IOException e) {
				logger.error("Error while closing raw data log writer.", e);
			}
		}
		
		//--------------------------------
		// Terminate Reporters
		for(GatlytronReporter reporter : reporterList) {
			try {
				reporter.terminate();
			} catch (Throwable e) {
				logger.warn("Error while terminating Reporter: "+e.getMessage(), e);
			}
		}
	}
	
	
	
	
	
}
