package com.performetriks.gatlytron.test.settings;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import com.performetriks.gatlytron.base.Gatlytron;
import com.performetriks.gatlytron.reporting.GatlytronReporterCSV;
import com.performetriks.gatlytron.reporting.GatlytronReporterDatabasePostGres;
import com.performetriks.gatlytron.reporting.GatlytronReporterJsonFile;
import com.performetriks.gatlytron.reporting.GatlytronReporterSysout;

public class TestSettings {

	public static final boolean DEBUG = false;

	public static final String URL_BASE = "https://www.google.com";
	
	public static FeederBuilder.Batchable<String> dataFeeder = csv("testdata.csv").circular();

	public static FeederBuilder.Batchable<String> getDataFeeder() { return dataFeeder; }

	/****************************************************************************
	 * 
	 ****************************************************************************/
	public static void commonInitialization() {
		
		// You can add sysem properties if y
    	// System.setProperty("gatling.graphite.host", "localhost");
    	// System.setProperty("gatling.graphite.port", "2003");
		// System.setProperty("gatling.graphite.writePeriod", "5");
		
    	Gatlytron.enableGraphiteReceiver(2003);
    	Gatlytron.addReporter(new GatlytronReporterJsonFile("./target/gatlytron.json"));
    	Gatlytron.addReporter(new GatlytronReporterCSV("./target/gatlytron.csv", ";"));
    	Gatlytron.addReporter(new GatlytronReporterSysout());
    	Gatlytron.addReporter(
    			new GatlytronReporterDatabasePostGres(
	    			"localhost"
	    			, 5432
	    			, "postgres"
	    			, "gatlytron"
	    			, "postgres"
	    			, "postgres"
    			)
    		);
    			
    	
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	public static HttpProtocolBuilder getProtocol() { 
		HttpProtocolBuilder httpProtocol= http
				.baseUrl(URL_BASE)
				.disableUrlEncoding()
				.inferHtmlResources(AllowList(), DenyList(
						  ".*\\.js"
						, ".*\\.css"
						, ".*\\.gif"
						, ".*\\.jpeg"
						, ".*\\.jpg"
						, ".*\\.png"
						, ".*\\.ico"
						, ".*\\.woff"
						, ".*\\.woff2"
						, ".*\\.(t|o)tf"
						, ".*\\.svg"
						, ".*detectportal\\.firefox\\.com.*"))
				.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"); 
		
		return httpProtocol;
	}
}