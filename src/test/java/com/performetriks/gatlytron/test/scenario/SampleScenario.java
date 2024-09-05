package com.performetriks.gatlytron.test.scenario;
 
import com.performetriks.gatlytron.test.settings.TestSettings;
import com.performetriks.gatlytron.utils.GatlingBaseScenario;
 
import java.util.HashMap;
import java.util.Map;
 
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
 
public class SampleScenario extends GatlingBaseScenario {
  
    public static final String SCENARIO_NAME = "API.callInterface";
    public static final String URL_API = TestSettings.URL_BASE + "";
  
    /***************************************************************************
     *
     ***************************************************************************/
    public SampleScenario() {
        super(SCENARIO_NAME);
      
        this.debug(TestSettings.DEBUG)
            .feederBuilder(TestSettings.getDataFeeder())
            .scenarioSteps(
                exec(
                http("callAPI")
                        .get(URL_API)
                        //.body(ElFileBody("postbody.json")) //.asJson()
                        //.headers(getHeader())
                        //.check(bodyString().saveAs("responseBody"))
                )
        );
 
    }
 
    public Map<CharSequence, String> getHeader(){
        Map<CharSequence, String> headers_space = new HashMap<>();
        headers_space.put("Accept", "application/json");
        headers_space.put("Accept-Encoding", "gzip, deflate, br, zstd");
        headers_space.put("Cache-Control", "no-cache");
        headers_space.put("Origin", TestSettings.URL_BASE);
        headers_space.put("Content-Type", "application/json");
        //headers_space.put("authorization", "Bearer "+ CustomSettings.myToken);
        //headers_space.put("x-dynatrace", "customDynatraceLabel");
 
        return headers_space;
    }
 
}