package com.performetriks.gatlytron.base;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.FeederBuilder;

import java.time.Duration;
import java.util.ArrayList;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.rampConcurrentUsers;

/***************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronScenario {
	private String scenarioName;
	// set default to not be null
	private ArrayList<FeederBuilder<?>> feederBuilderList = new ArrayList<>();
	private ChainBuilder scenarioSteps;
	private boolean debug = false;

	
	/***************************************************************************
	 *
	 ***************************************************************************/
	public GatlytronScenario(String scenarioName) {
		this.scenarioName = scenarioName;
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
		// -----------------------------------------------
		// Calculate Load Parameters
		// -----------------------------------------------
		int pacingSeconds = 3600 / (execsHour / users);
		int rampUpInterval = pacingSeconds / users * rampUp;

		if (debug) {
			System.err.println("============== Load Parameters ==============");
			System.err.println("Scenario: " + scenarioName);
			System.err.println("Target Users: " + users);
			System.err.println("Executions/Hour: " + execsHour);
			System.err.println("StartOffset: " + offset);
			System.err.println("RampUp Users: " + rampUp);
			System.err.println("RampUp Interval(s): " + rampUpInterval);
			System.err.println("Pacing(s): " + pacingSeconds);
			System.err.println("============================================");
		}

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
	 *
	 ***************************************************************************/
	public GatlytronScenario debug(boolean debug) {
		this.debug = debug;
		return this;
	}
}
