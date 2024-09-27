package com.performetriks.gatlytron.test.simulation;
 
import java.time.Duration;

import com.performetriks.gatlytron.test.scenario.SampleScenario;
import com.performetriks.gatlytron.test.settings.TestGlobals;

import io.gatling.javaapi.core.Simulation;
 
 
public class SimulationLoadAverage extends Simulation {
 
    private static final Duration TEST_DURATION = Duration.ofMinutes(90);
 
    {
    	TestGlobals.commonInitialization();
    	
        //======================================================================
        // Average Load Example Scenario
        //======================================================================
        setUp(
             new SampleScenario().buildStandardLoad(30, 6000, 0, 2)
        ).protocols(TestGlobals.getProtocol())
         .maxDuration(TEST_DURATION)
        ;
	}
}