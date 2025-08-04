package com.performetriks.gatlytron.base;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.feed;
import static io.gatling.javaapi.core.CoreDsl.rampConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;

import java.time.Duration;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.performetriks.gatlytron.database.DBInterface;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ClosedInjectionStep;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;

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
	private boolean feedersPrepended = false;

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
	
	/***********************************************************************
	 * Checks if scenario steps are defined, then prepends the feeders 
	 * to the scenarioSteps
	 ***********************************************************************/
	private void prependFeedersToScenarioSteps() {
		
		if (scenarioSteps == null) {
			throw new IllegalStateException("Scenario Steps cannot be null, please add them to the scenario using the function scenarioSteps().");
		}
		
		if(!feedersPrepended 
		&& feederBuilderList.size() > 0) {
			ChainBuilder chainedFeeders = feed(feederBuilderList.get(0) );
			
			for (int i = 1; i < feederBuilderList.size(); i++) {
				chainedFeeders = feed( feederBuilderList.get(i));
			}
			
			scenarioSteps = chainedFeeders.exec(scenarioSteps);
			feedersPrepended = true;
		}
		
		
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
		return buildStandardLoad(percent, users, execsHour, offset, rampUp, null);
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
	 * @param percent   percentage (0-100, or more) that will be used to scale 
	 * 					the number of users and execsHour
	 * @param users     number of users to run constantly for this scenario
	 * @param execsHour targeted number of executions per hour
	 * @param offset    in seconds from the test start
	 * @param rampUp    number of users to increase per ramp up
	 * @param duration  the time the scenario should run with a constant amount 
	 * 					of users, forever if null
	 ***************************************************************************/
	public PopulationBuilder buildStandardLoad(int percent, int users, int execsHour, long offset, int rampUp, Duration duration) {
		users = (int)Math.ceil( users * (percent / 100.0f) );
		execsHour = (int)Math.ceil( execsHour * (percent / 100.0f) );
		
		return buildStandardLoad(users, execsHour, offset, rampUp, duration);
	}
	
	/***************************************************************************
	 * The scenario will be set to run forever, therefore make sure to
	 * set maxDuration in your simulation:
	 * 
	 * <pre>
	 * <code>
	 * setUp(...).maxDuration(Duration.ofMinutes(15))
	 * </code>
	 * </pre>
	 * 
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
	 * 
	 ***************************************************************************/
	public PopulationBuilder buildStandardLoad(int users, int execsHour, long offset, int rampUp) {
		return buildStandardLoad(users, execsHour, offset, rampUp, null);
	}
	
	/***************************************************************************
	 * The scenario will be set to run forever, therefore make sure to
	 * set maxDuration in your simulation:
	 * 
	 * <pre>
	 * <code>
	 * setUp(...).maxDuration(Duration.ofMinutes(15))
	 * </code>
	 * </pre>
	 * 
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
	 * @param duration  the time the scenario should run with a constant amount of users, forever if null
	 * 
	 ***************************************************************************/
	public PopulationBuilder buildStandardLoad(int users, int execsHour, long offset, int rampUp, Duration duration) {

		if (scenarioSteps == null) {
			throw new IllegalStateException("Scenario Steps cannot be null.");
		}
		
		this.users = users;
		this.execsHour = execsHour;
		this.offset = offset;
		
		if(rampUp > users) { rampUp = users; } // prevent issues with ramp up
		this.rampUp = rampUp;
		
		// -----------------------------------------------
		// Calculate Load Parameters
		// -----------------------------------------------
		int pacingSeconds = (int)Math.ceil( 3600 / ( 1f * execsHour / users) );
		int rampUpInterval = (int)Math.ceil( (1f * pacingSeconds / users) * rampUp );
		
		this.rampUpInterval = rampUpInterval;
		this.pacingSeconds = pacingSeconds;
		
		// -----------------------------------------------
		// Log Warnings
		// -----------------------------------------------
		String sides = "=".repeat(16);
		String title = " Load Config: "+scenarioName+" ";
		logger.info(sides + title + sides);
		
		if(rampUpInterval == 0) {
			logger.warn("==> Ramp up interval is 0, all users will be started at the same time.");
		}
		
		if(pacingSeconds < 10) {
			logger.warn("==> Pacing is below 10 seconds, make sure  one iteration of your scenario can execute in that time.");
		}
		
		if(pacingSeconds == 0) {
			pacingSeconds = 1; // needed or else ramp up might not load any users
			logger.warn("==> Calculated Pacing was 0 seconds, set to 1 second.");
		}
		
		// -----------------------------------------------
		// Log infos
		// -----------------------------------------------

		logger.info("Scenario: " + scenarioName);
		logger.info("Target Users: " + users);
		logger.info("Executions/Hour: " + execsHour);
		logger.info("StartOffset: " + offset);
		logger.info("RampUp Users: " + rampUp);
		logger.info("RampUp Interval(s): " + rampUpInterval);
		logger.info("Pacing(s): " + pacingSeconds);
		logger.info(sides.repeat(2) + "=".repeat( title.length()) ); // cosmetics, just because because we can!
		
		// -----------------------------------------------
		// Build Scenario
		// -----------------------------------------------

		ArrayList<ClosedInjectionStep> steps = new ArrayList<>();
		
		// Start Offset
		steps.add(constantConcurrentUsers(0)
				.during(Duration.ofSeconds(offset))
			);
		
		// Ramp Up
		steps.add(
			rampConcurrentUsers(rampUp)
				.to(users)
				.during(pacingSeconds)
		);
		
		if(duration != null) {
			// Keep Constant Amount of Users
			Duration pacingDuration = Duration.ofSeconds(pacingSeconds);
			Duration constantDuration = duration.minus(pacingDuration);
			
			steps.add(constantConcurrentUsers(users)
					.during(constantDuration)
				);
		}
		
		ScenarioBuilder SCENARIO = buildScenario(pacingSeconds, duration);

		return SCENARIO.injectClosed( 
				steps.toArray(new ClosedInjectionStep[] {}) 
			);
	}

	/***************************************************************************
	 * Builds a scenario using the feeders and scenario Steps. Also adds a debug
	 * output that will be triggered if debug is set to true.
	 * 
	 * The scenario will be set to run forever(9999 days), therefore make sure to
	 * set maxDuration in your simulation:
	 * 
	 * <pre>
	 * <code>
	 * setUp(...).maxDuration(Duration.ofMinutes(15))
	 * </code>
	 * </pre>
	 ***************************************************************************/
	public ScenarioBuilder buildScenario(int pacingSeconds) {
		return buildScenario(pacingSeconds, null);
	}
	/***************************************************************************
	 * Builds a scenario using the feeders and scenario Steps. Also adds a debug
	 * output that will be triggered if debug is set to true.
	 * 
	 * The scenario will be set to run forever if duration is null, in that case
	 * make sure to set maxDuration in your simulation:
	 * 
	 * <pre>
	 * <code>
	 * setUp(...).maxDuration(Duration.ofMinutes(15))
	 * </code>
	 * </pre>
	 * 
	 * @param pacingSeconds the pacing for the scenario
	 * @param duration the execution duration for the scenario, if null, use 9999 days(~forever).
	 ***************************************************************************/
	public ScenarioBuilder buildScenario(int pacingSeconds, Duration duration) {
		
		if(duration == null) {
			duration = Duration.ofDays(9999);
		}
		// -----------------------------------------------
		// Add all feeders
		// -----------------------------------------------
		prependFeedersToScenarioSteps();
		
		//
		//for (FeederBuilder<?> builder : feederBuilderList) {
		//	SCENARIO = SCENARIO.feed(builder);
		//}

		// -----------------------------------------------
		// Endless Loop until end of test
		// -----------------------------------------------
		ScenarioBuilder SCENARIO = scenario(scenarioName);
		SCENARIO = SCENARIO.during(duration).on(scenarioSteps.pace(pacingSeconds));

		// -----------------------------------------------
		// Add debugging data if enabled
		// -----------------------------------------------
		GatlytronScenario.addDebug(SCENARIO, debug);

		return SCENARIO;
	}

	/***************************************************************************
	 * Runs the scenario once, 
	 ***************************************************************************/
	public PopulationBuilder buildRunOnce() {

		// -----------------------------------------------
		// Add all feeders
		// -----------------------------------------------
		prependFeedersToScenarioSteps();

		// -----------------------------------------------
		// Endless Loop until end of test
		// -----------------------------------------------
		ScenarioBuilder SCENARIO = scenario(scenarioName);
		SCENARIO = SCENARIO.exec(scenarioSteps);

		// -----------------------------------------------
		// Add debugging data if enabled
		// -----------------------------------------------
		GatlytronScenario.addDebug(SCENARIO, debug);

		return SCENARIO.injectOpen(atOnceUsers(1));
	}
	
	/***************************************************************************
	 * Repeats the Scenario for the specified amount of times
	 * in sequence with a single user.
	 * 
	 *  @param times number of times to repeat the scenario
	 ***************************************************************************/
	public PopulationBuilder buildRepeat(int times) {

		if (scenarioSteps == null) {
			throw new IllegalStateException("Scenario Steps cannot be null.");
		}

		// -----------------------------------------------
		// Add all feeders
		// -----------------------------------------------
		prependFeedersToScenarioSteps();

		// -----------------------------------------------
		// Endless Loop until end of test
		// -----------------------------------------------
		ScenarioBuilder SCENARIO = scenario(scenarioName);
		SCENARIO = SCENARIO.repeat(times).on(scenarioSteps);

		// -----------------------------------------------
		// Add debugging data if enabled
		// -----------------------------------------------
		GatlytronScenario.addDebug(SCENARIO, debug);

		return SCENARIO.injectOpen(atOnceUsers(1));
	}
	
	/***************************************************************************
	 * Repeats the specified Scenario for the highest amount of records of any
	 * of the data feeders added to the scenario.
	 * Runs all the repetitions in sequence with a single user.
	 * 
	 ***************************************************************************/
	public PopulationBuilder buildDatacheck() {

		int maxRecords = 0;
		for (FeederBuilder<?> builder : feederBuilderList) {
			int count = builder.recordsCount();
			if(count > maxRecords) {
				maxRecords = count;
			}
		}

		return buildRepeat(maxRecords);
		
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
	 * Returns the name of the scenario.
	 * 
	 ***************************************************************************/
	public String scenarioName() {
		return scenarioName;
	}

	/***************************************************************************
	 * Set the name of the scenario.
	 * 
	 * @return the scenario instance for chaining
	 ***************************************************************************/
	public GatlytronScenario scenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
		return this;
	}

	/***************************************************************************
	 * Returns the list of all the feeders added to this scenario.
	 * 
	 ***************************************************************************/
	public ArrayList<FeederBuilder<?>> feederList() {
		return feederBuilderList;
	}

	/***************************************************************************
	 * Add a feeder to this scenario.
	 * 
	 * @param feederBuilder a feeder that should be used in this scenario.
	 * @return the scenario instance for chaining
	 ***************************************************************************/
	public GatlytronScenario feederBuilder(FeederBuilder<?> feederBuilder) {
		this.feederBuilderList.add(feederBuilder);
		return this;
	}

	/***************************************************************************
	 * Returns the steps that where added to this scenario.
	 ***************************************************************************/
	public ChainBuilder scenarioSteps() {
		return scenarioSteps;
	}

	/***************************************************************************
	 * Sets or replaces the steps of this scenario.
	 * 
	 * @param scenarioSteps the steps for this scenario.
	 ***************************************************************************/
	public GatlytronScenario scenarioSteps(ChainBuilder scenarioSteps) {
		this.scenarioSteps = scenarioSteps;
		return this;
	}

	/***************************************************************************
	 * Return if debug is enabled for this scenario.
	 * Default value is obtained from Gatlytron.isDebug();
	 * 
	 * @return boolean
	 ***************************************************************************/
	public boolean isDebug() {
		return debug;
	}

	/***************************************************************************
	 * Set if debug logs should be printed.
	 * Default value is obtained from Gatlytron.isDebug();
	 * 
	 * @return the scenario instance for chaining
	 ***************************************************************************/
	public GatlytronScenario debug(boolean debug) {
		this.debug = debug;
		return this;
	}
}
