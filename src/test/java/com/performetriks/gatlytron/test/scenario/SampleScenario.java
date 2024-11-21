package com.performetriks.gatlytron.test.scenario;
 
import com.performetriks.gatlytron.base.GatlytronScenario;
import com.performetriks.gatlytron.test.settings.TestGlobals;

import java.util.HashMap;
import java.util.Map;
 
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
 
public class SampleScenario extends GatlytronScenario {
  
    public static final String SCENARIO_NAME = "API.callInterface";
    public static final String URL_API = TestGlobals.URL_BASE + "";
  
    /***************************************************************************
     *
     ***************************************************************************/
    public SampleScenario() {
        super(SCENARIO_NAME);
      
        this
        	//.debug(TestGlobals.DEBUG) // default is obtained from Gatlytron.isDebug()
            .feederBuilder(TestGlobals.getDataFeeder())
            .scenarioSteps(
            	group("MyTest").on(
	                exec( 
	                http(SCENARIO_NAME+".fetchInfo")
	                        .get(URL_API)
	                        //.body(ElFileBody("postbody.json")) //.asJson()
	                        //.headers(getHeader())
	                        //.check(bodyString().saveAs("responseBody"))
	                )
            	)
        );
 
    }
 
    public Map<CharSequence, String> getHeader(){
        Map<CharSequence, String> headers_space = new HashMap<>();
        headers_space.put("Accept", "application/json");
        headers_space.put("Accept-Encoding", "gzip, deflate, br, zstd");
        headers_space.put("Cache-Control", "no-cache");
        headers_space.put("Origin", TestGlobals.URL_BASE);
        headers_space.put("Content-Type", "application/json");
        //headers_space.put("authorization", "Bearer "+ CustomSettings.myToken);
        //headers_space.put("x-dynatrace", "customDynatraceLabel");
 
        return headers_space;
    }
 
}