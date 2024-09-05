package com.performetriks.gatlytron.test.simulation;
 
import com.performetriks.gatlytron.test.scenario.SampleScenario;
import com.performetriks.gatlytron.test.settings.TestSettings;

import io.gatling.javaapi.core.Simulation;
 
 
public class SimulationCheckDebug extends Simulation {
 
    {
        //======================================================================
        // Use this simulation class for testing and debugging.
        // This is done to have an easy way to develop while not messing
        // up the real thing. Feel free to do your worst in this class.
        // (I hope I won't regret writting this)
        //======================================================================
       
        setUp(
            new SampleScenario().buildRunOnce()
        ).protocols(TestSettings.getProtocol())
        ;
 
 
    }
}
 