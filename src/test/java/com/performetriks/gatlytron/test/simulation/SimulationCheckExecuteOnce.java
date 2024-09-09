package com.performetriks.gatlytron.test.simulation;
 
import com.performetriks.gatlytron.test.scenario.SampleScenario;
import com.performetriks.gatlytron.test.settings.TestGlobals;

import io.gatling.javaapi.core.Simulation;
 
 
public class SimulationCheckExecuteOnce extends Simulation {
	
    {
    	TestGlobals.commonInitialization();
    	
        //======================================================================
        // Runs every script once, useful to check if all the scripts are working.
        //======================================================================
        setUp(
            new SampleScenario().buildRunOnce()
        ).protocols(TestGlobals.getProtocol())
        ;
  }
}