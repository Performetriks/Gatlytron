package com.performetriks.gatlytron.base;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.FeederBuilder;

import java.time.Duration;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.performetriks.gatlytron.database.DBInterface;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.rampConcurrentUsers;

/***************************************************************************
 * Extend this class to make your scenario a GaltytronScenario.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronScenario {
	
	private static Logger logger = LoggerFactory.getLogger(GatlytronScenario.class.getName());

	private String scenarioName;
	// set default to not be null
	private ArrayList<FeederBuilder<?>> feederBuilderList = new ArrayList<>();
	private ChainBuilder scenarioSteps;
	private boolean debug = Gatlytron.isDebug();

	private int users = -1;
	private int execsHour = -1;
	private long offset = -1;
	private int rampUp = -1;
	private int rampUpInterval = -1;
	private int pacingSeconds = -1;
	
	private static String sqlCreateTableTemplate = "CREATE TABLE IF NOT EXISTS {tablename} ("
			+ "		    time BIGINT \r\n"
			+ "		  , endtime BIGINT \r\n"
			+ "		  , execID VARCHAR(4096) \r\n"
			+ "		  , simulation VARCHAR(4096) \r\n"
			+ "		  , scenario VARCHAR(4096) \r\n"
			+ "		  , users INT \r\n"
			+ "		  , execsHour INT \r\n"
			+ "		  , startOffset INT \r\n"
			+ "		  , rampUp INT \r\n"
			+ "		  , rampUpInterval INT \r\n"
			+ "		  , pacingSeconds INT \r\n"
			+ ")"
			;
	
	private static String sqlInsertIntoTemplate = 
						  "INSERT INTO {tablename} "
						+ " (time, endtime, execID, simulation, scenario, users, execsHour, startOffset, rampUp, rampUpInterval, pacingSeconds) "
						+ " VALUES (?,?,?,?,?,?,?,?,?,?,?)"
						;

	
	/***************************************************************************
	 *
	 ***************************************************************************/
	public GatlytronScenario(String scenarioName) {
		this.scenarioName = scenarioName;
		
		Gatlytron.addScenario(this);
	}
	
	/***********************************************************************
	 * Returns a SQL template for creating the database table.
	 ***********************************************************************/
	public static String getSQLCreateTableTemplate(String tableName) {
		return sqlCreateTableTemplate.replace("{tablename}", tableName);
	}
	
	/***********************************************************************
	 * Returns an insert statement 
	 ***********************************************************************/
	public boolean insertIntoDatabase(DBInterface db, String tableName, String simulationName) {
		
		if(db == null || tableName == null) { return false; }

		
		String insertSQL = sqlInsertIntoTemplate.replace("{tablename}", tableName);
	
		ArrayList<Object> valueList = new ArrayList<>();
		
		valueList.add(Gatlytron.STARTTIME_MILLIS);
		valueList.add(null); //report nothing for endtime
		valueList.add(Gatlytron.EXECUTION_ID);
		valueList.add(simulationName);
		valueList.add(scenarioName);
		valueList.add(users);
		valueList.add(execsHour);
		valueList.add(offset);
		valueList.add(rampUp);
		valueList.add(rampUpInterval);
		valueList.add(pacingSeconds);
		
		return db.preparedExecute(insertSQL, valueList.toArray());
		
	}

	/***************************************************************************
	 * This method creates a standard load pattern:
	 * <ul>
	 * <li>Ramping up users at the start of the test</li>
	 * <li>Keeping users at a constant level</li>
	 * <li>Adds pacing to the use cases.</li>
	 * </ul>
	 *
	 * This method will calculate the pacing and ramp up interval based on the input
	 * values. The users and execHours will be scaled by the percentage.
	 * 
	 * <pre>
	 * <code>
	 * int pacingSeconds = 3600 / (execsHour / users);
	 * int rampUpInterval = pacingSeconds / users * rampUp;
	 * </code>
	 * </pre>
	 *
	 * @param percent   percentage (0-100, or more) that will be used to scale the number of 
	 * 					users and execsHour
	 * @param users     number of users to run constantly for this scenario
	 * @param execsHour targeted number of executions per hour
	 * @param offset    in seconds from the test start
	 * @param rampUp    number of users to increase per ramp up
	 ***************************************************************************/
	public PopulationBuilder buildStandardLoad(int percent, int users, int execsHour, long offset, int rampUp) {
		users = (int)Math.ceil( users * (percent / 100.0f) );
		execsHour = (int)Math.ceil( execsHour * (percent / 100.0f) );
		
		return buildStandardLoad(users, execsHour, offset, rampUp);
	}
	
	/***************************************************************************
	 * This method creates a standard load pattern:
	 * <ul>
	 * <li>Ramping up users at the start of the test</li>
	 * <li>Keeping users at a constant level</li>
	 * <li>Adds pacing to the use cases.</li>
	 * </ul>
	 *
	 * This method will calculate the pacing and ramp up interval based on the input
	 * values.
	 * 
	 * <pre>
	 * <code>
	 * int pacingSeconds = 3600 / (execsHour / users);
	 * int rampUpInterval = pacingSeconds / users * rampUp;
	 * </code>
	 * </pre>
	 *
	 * @param users     number of users to run constantly for this scenario
	 * @param execsHour targeted number of executions per hour
	 * @param offset    in seconds from the test start
	 * @param rampUp    number of users to increase per ramp up
	 ***************************************************************************/
	public PopulationBuilder buildStandardLoad(int users, int execsHour, long offset, int rampUp) {

		if (scenarioSteps == null) {
			throw new IllegalStateException("Scenario Steps cannot be null.");
		}
		
		this.users = users;
		this.execsHour = execsHour;
		this.offset = offset;
		this.rampUp = rampUp;
		
		// -----------------------------------------------
		// Calculate Load Parameters
		// -----------------------------------------------
		int pacingSeconds = 3600 / (execsHour / users);
		int rampUpInterval = (int)Math.ceil( (1f * pacingSeconds / users) * rampUp );

		
		// -----------------------------------------------
		// Log infos
		// -----------------------------------------------
		logger.info("============== Load Parameters ==============");
		logger.info("Scenario: " + scenarioName);
		logger.info("Target Users: " + users);
		logger.info("Executions/Hour: " + execsHour);
		logger.info("StartOffset: " + offset);
		logger.info("RampUp Users: " + rampUp);
		logger.info("RampUp Interval(s): " + rampUpInterval);
		logger.info("Pacing(s): " + pacingSeconds);
		logger.info("============================================");
		
		// -----------------------------------------------
		// Log Warnings
		// -----------------------------------------------
		if(rampUpInterval == 0) {
			logger.warn("===> Ramp up interval is 0, all users will be started at the same time.");
		}
		
		if(pacingSeconds < 10) {
			logger.warn("===> Pacing is below 10 seconds, make sure your scenario duration can fit into that time.");
		}
		
		this.rampUpInterval = rampUpInterval;
		this.pacingSeconds = pacingSeconds;
		
		ScenarioBuilder SCENARIO = buildScenario(pacingSeconds);

		return SCENARIO.injectClosed(constantConcurrentUsers(0).during(Duration.ofSeconds(offset)),
				rampConcurrentUsers(rampUp).to(users).during(pacingSeconds));
	}

	/***************************************************************************
	 * Builds a scenario using the feeders and scenario Steps. Also adds a debug
	 * output that will be triggered if debug is set to true.
	 ***************************************************************************/
	public ScenarioBuilder buildScenario(int pacingSeconds) {
		// -----------------------------------------------
		// Add all feeders
		// -----------------------------------------------
		ScenarioBuilder SCENARIO = scenario(scenarioName);
		for (FeederBuilder<?> builder : feederBuilderList) {
			SCENARIO = SCENARIO.feed(builder);
		}

		// -----------------------------------------------
		// Endless Loop until end of test
		// -----------------------------------------------
		SCENARIO = SCENARIO.forever().on(scenarioSteps.pace(pacingSeconds));

		// -----------------------------------------------
		// Add debugging data if enabled
		// -----------------------------------------------
		GatlytronScenario.addDebug(SCENARIO, debug);

		return SCENARIO;
	}

	/***************************************************************************
	 *
	 ***************************************************************************/
	public PopulationBuilder buildRunOnce() {

		if (scenarioSteps == null) {
			throw new IllegalStateException("Scenario Steps cannot be null.");
		}

		// -----------------------------------------------
		// Add all feeders
		// -----------------------------------------------
		ScenarioBuilder SCENARIO = scenario(scenarioName);
		for (FeederBuilder<?> builder : feederBuilderList) {
			SCENARIO = SCENARIO.feed(builder);
		}

		// -----------------------------------------------
		// Endless Loop until end of test
		// -----------------------------------------------
		SCENARIO = SCENARIO.exec(scenarioSteps);

		// -----------------------------------------------
		// Add debugging data if enabled
		// -----------------------------------------------
		GatlytronScenario.addDebug(SCENARIO, debug);

		return SCENARIO.injectOpen(atOnceUsers(1));
	}

	/***************************************************************************
	 * Adds a debug step to the scenario if debugEnabled is set to true.
	 ***************************************************************************/
	public static void addDebug(ScenarioBuilder scenario, boolean debugEnabled) {

		if (debugEnabled) {
			scenario.exec(session -> {
				System.out.println("Response Body: " + session.scenario());
				System.out.println(session.getString("responseBody"));
				return session;
			});
		}
	}

	/***************************************************************************
	 *
	 ***************************************************************************/
	public String scenarioName() {
		return scenarioName;
	}

	/***************************************************************************
	 *
	 ***************************************************************************/
	public GatlytronScenario scenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
		return this;
	}

	/***************************************************************************
	 *
	 ***************************************************************************/
	public ArrayList<FeederBuilder<?>> feederList() {
		return feederBuilderList;
	}

	/***************************************************************************
	 *
	 ***************************************************************************/
	public GatlytronScenario feederBuilder(FeederBuilder<?> feederBuilder) {
		this.feederBuilderList.add(feederBuilder);
		return this;
	}

	/***************************************************************************
	 *
	 ***************************************************************************/
	public ChainBuilder scenarioSteps() {
		return scenarioSteps;
	}

	/***************************************************************************
	 *
	 ***************************************************************************/
	public GatlytronScenario scenarioSteps(ChainBuilder scenarioSteps) {
		this.scenarioSteps = scenarioSteps;
		return this;
	}

	/***************************************************************************
	 *
	 ***************************************************************************/
	public boolean isDebug() {
		return debug;
	}

	/***************************************************************************
	 * Set if debug logs should be printed.
	 * Default value is obtained from Gatlytron.isDebug();
	 ***************************************************************************/
	public GatlytronScenario debug(boolean debug) {
		this.debug = debug;
		return this;
	}
}
