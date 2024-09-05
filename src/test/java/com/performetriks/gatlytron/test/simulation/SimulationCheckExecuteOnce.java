package com.performetriks.gatlytron.test.simulation;
 
import com.performetriks.gatlytron.test.scenario.SampleScenario;
import com.performetriks.gatlytron.test.settings.TestSettings;

import io.gatling.javaapi.core.Simulation;
 
 
public class SimulationCheckExecuteOnce extends Simulation {
 
    {
        //======================================================================
        // Runs every script once, useful to check if all the scripts are working.
        //======================================================================
        setUp(
            new SampleScenario().buildRunOnce()
        ).protocols(TestSettings.getProtocol())
        ;
  }
}