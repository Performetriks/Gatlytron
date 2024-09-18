package com.performetriks.gatlytron.test.settings;

import static io.gatling.javaapi.core.CoreDsl.AllowList;
import static io.gatling.javaapi.core.CoreDsl.DenyList;
import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.http.HttpDsl.http;

import com.performetriks.gatlytron.base.Gatlytron;
import com.performetriks.gatlytron.reporting.GatlytronReporterCSV;
import com.performetriks.gatlytron.reporting.GatlytronReporterDatabasePostGres;
import com.performetriks.gatlytron.reporting.GatlytronReporterEMP;
import com.performetriks.gatlytron.reporting.GatlytronReporterJson;
import com.performetriks.gatlytron.reporting.GatlytronReporterSysoutCSV;
import com.performetriks.gatlytron.reporting.GatlytronReporterSysoutJson;

import ch.qos.logback.classic.Level;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class TestGlobals {

	public static final String URL_BASE = "http://www.nasa.gov/";
	
	public static FeederBuilder.Batchable<String> dataFeeder = csv("testdata.csv").circular();

	public static FeederBuilder.Batchable<String> getDataFeeder() { return dataFeeder; }

	/****************************************************************************
	 * 
	 ****************************************************************************/
	public static void commonInitialization() {
		
		// You can add system properties if you don't want to to use gatling.conf
    	// System.setProperty("gatling.graphite.host", "localhost");
    	// System.setProperty("gatling.graphite.port", "2003");
		// System.setProperty("gatling.graphite.writePeriod", "15");
		
		//Gatlytron.setKeepEmptyRecords(false);
		
		Gatlytron.setDebug(false);
		Gatlytron.setLogLevelRoot(Level.INFO);
		Gatlytron.setLogLevel(Level.DEBUG, "com.performetriks.gatlytron");
		
    	Gatlytron.enableGraphiteReceiver(2003);
    	Gatlytron.addReporter(new GatlytronReporterJson("./target/gatlytron.json"));
    	Gatlytron.addReporter(new GatlytronReporterCSV("./target/gatlytron.csv", ";"));
    	
    	//Gatlytron.addReporter(new GatlytronReporterSysoutJson());
    	//Gatlytron.addReporter(new GatlytronReporterSysoutCSV(";"));
    	
    	Gatlytron.addReporter(
    			new GatlytronReporterEMP(
    					"http://localhost:8888"
    					,"gatlytron-test-token-MSGIUzrLyUsOypYOkekVgmlfjMpLbRCA"
    				)
    			);
    	
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
	public static void commonTermination() {
		Gatlytron.terminate();
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