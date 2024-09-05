package com.performetriks.gatlytron.test.simulation;
 
import java.time.Duration;

import com.performetriks.gatlytron.test.scenario.SampleScenario;
import com.performetriks.gatlytron.test.settings.TestSettings;

import io.gatling.javaapi.core.Simulation;
 
 
public class SimulationLoadAverage extends Simulation {
 
    private static final Duration TEST_DURATION = Duration.ofMinutes(3);
 
    {
        //======================================================================
        // Average Load executed in FAT environment, simulates production-like
        // load scaled to the FAT environment (33% of PROD size)
        //======================================================================
        setUp(
             new SampleScenario().buildStandardLoad(10, 600, 0, 2)
        ).protocols(TestSettings.getProtocol())
         .maxDuration(TEST_DURATION)
        ;
  }
}