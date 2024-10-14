package com.performetriks.gatlytron.test.scenario;
 
import com.performetriks.gatlytron.base.GatlytronScenario;
import com.performetriks.gatlytron.test.settings.TestGlobals;

import java.util.HashMap;
import java.util.Map;
 
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
 
public class SampleScenarioTwo extends GatlytronScenario {
  
    public static final String SCENARIO_NAME = "Website";
    public static final String URL = TestGlobals.URL_BASE + "";
  
    /***************************************************************************
     *
     ***************************************************************************/
    public SampleScenarioTwo() {
        super(SCENARIO_NAME);
      
        this
            .feederBuilder(TestGlobals.getDataFeeder())
            .scenarioSteps(
                exec(
                	http("openHomepage").get(URL)
                )
        );
 
    }

}