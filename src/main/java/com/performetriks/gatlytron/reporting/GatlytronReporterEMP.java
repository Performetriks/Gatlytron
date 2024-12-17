package com.performetriks.gatlytron.reporting;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.performetriks.gatlytron.base.Gatlytron;
import com.performetriks.gatlytron.stats.GatlytronRecordStats;

/***************************************************************************
 * This reporter takes the received records and sends them to an instance of
 * the open source tool Engineered Monitoring Platform(EMP), which you can find
 * <a href="https://github.com/xresch/EngineeredMonitoringPlatform">here</a>.
 * 
 * Data is pushed to the EMP API Endpoints 
 * 		EAVStats.pushStats and 
 * 		EAVStats.pushStatsCSV 
 * and the token used for the connection needs permission to use these endpoins.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronReporterEMP implements GatlytronReporter {

	private static final Logger logger = LoggerFactory.getLogger(GatlytronReporterEMP.class);
	
	private static final String SEPARATOR = ";";
	private static String CSV_HEADER_START = "category"+SEPARATOR+"entity"+SEPARATOR+"attributes";
	private static String CSV_HEADER = CSV_HEADER_START;
	
	static {
		
		for(String metric : GatlytronRecordStats.metricNames) {
			CSV_HEADER += SEPARATOR+metric;
		}

		CSV_HEADER = CSV_HEADER
						.replace("mean", "avg")  // emp uses name "avg"
						;
	}
	
	private static final String ATTRIBUTES = "\"{"
									+ "  status: \\\"$statusPlaceholder$\\\""
									+ ", code: \\\"$codePlaceholder$\\\""
									+ ", type: \\\"$typePlaceholder$\\\""
									+ ", groups: \\\"$groupsPlaceholder$\\\""
									+ ", scenario: \\\"$scenarioPlaceholder$\\\""
									+ "}\""; 

	private String empURL;
	private String apiToken;
	private String categoryPrefix = "GTRON:";
	private HttpClient client = HttpClient.newBuilder()
									.connectTimeout(Duration.ofSeconds(15))
									.build();
	
	/****************************************************************************
	 * 
	 * 
	 * @param empURL the url of EMP including protocol, hostnme and if neccessary port.
	 *        No other things needed.
	 *        
	 * @param apiToken the API token used for accessing the EMP API. Login with
	 *           an admin account in EMP and go to "Tools >> API >> Manage Tokens".
	 *           The token will need access to the API Endpoints EAVStats.pushStats 
	 *           and EAVStats.pushStatsCSV.
	 *           
	 * @param categoryPrefix a prefix for the EAV category, could be useful to
	 * 		  differentiate between different environments like UAT or PRD.
	 * 
	 ****************************************************************************/
	public GatlytronReporterEMP(
			  String empURL
			, String apiToken
			, String categoryPrefix
			) {
		
		this(empURL, apiToken);
		this.categoryPrefix = categoryPrefix;
	}
	
	/****************************************************************************
	 * 
	 * @param empURL the url of EMP including protocol, hostnme and if neccessary port.
	 *        No other things needed.
	 * @apiToken apiToken the API token used for accessing the EMP API. Login with
	 *           an admin account in EMP and go to "Tools >> API >> Manage Tokens".
	 *           The token will need access to the API Endpoints EAVStats.pushStats 
	 *           and EAVStats.pushStatsCSV.
	 ****************************************************************************/
	public GatlytronReporterEMP(
			  String empURL
			, String apiToken
			) {
		
		if(empURL.endsWith("/")) {
			empURL = empURL.substring(0, empURL.length()-1);
		}
		
		this.empURL = empURL;
		this.apiToken = apiToken;
				
	}
			

	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void reportRecords(ArrayList<GatlytronRecordStats> records) {
		
		URI apiEndpoint = URI.create(empURL+"/app/api?apiName=EAVStats&actionName=pushStatsCSV"
				+ "&SEPARATOR="+SEPARATOR);
		
		StringBuilder csvRecordsRequest = new StringBuilder();
		StringBuilder csvRecordsUser = new StringBuilder();
		
		csvRecordsRequest.append(CSV_HEADER);
		csvRecordsUser.append(CSV_HEADER_START + SEPARATOR + "avg");
		
		//----------------------------------
		// Create Request Data
		for(GatlytronRecordStats record : records) {
			
			this.addCSVRecord(csvRecordsRequest, record);

		}
		
		String postBodyRequest = csvRecordsRequest.toString();
		String postBodyUser = csvRecordsUser.toString();
		
		logger.debug("==== EMP: CSV Body (Requests) ====\n"+postBodyRequest);
		logger.debug("==== EMP: CSV Body (User) ====\n"+postBodyUser);

		//----------------------------------
		// Send CSV Records Request
		callEMPAPI(apiEndpoint, postBodyRequest);
		callEMPAPI(apiEndpoint, postBodyUser);
		
	}

	/****************************************************************************
	 * 
	 ****************************************************************************/
	private void callEMPAPI(URI apiEndpoint, String postBody) {
		HttpRequest request = HttpRequest.newBuilder(apiEndpoint)
				.POST(HttpRequest.BodyPublishers.ofString(postBody))
				.header("API-Token", apiToken)
				.build();
		
		try {
			client.send( request, HttpResponse.BodyHandlers.ofString() );
			
		} catch (Exception e) {
			logger.error("EMP: An Error occured while calling the API.", e);
		}
	}
		
	/****************************************************************************
	 * 
	 ****************************************************************************/
	private void addCSVRecord(StringBuilder csv, GatlytronRecordStats record) {
				
		//-------------------------------
		// Initialize Values
		String category = categoryPrefix+record.getSimulation();
		String entityName = record.getMetricName();

		
		//-------------------------------
		// Escape Quotes
		category = category.replace("\"", "\\\"");
		entityName = entityName.replace("\"", "\\\"");
		
		//-------------------------------
		// Create base
		String commonInfo = 
				"\""+category+"\""
						+ SEPARATOR
						+ "\""+entityName+"\""
						+ SEPARATOR;
		
		
		String attrPrepared = ATTRIBUTES
							.replace("$codePlaceholder$",  record.getCode() )
							.replace("$typePlaceholder$",  record.getType().threeLetters() )
							.replace("$groupsPlaceholder$",  record.getGroupsPath() )
							.replace("$scenarioPlaceholder$",  record.getScenario().replace("\"", "\\\"") )
							;
		
		String recordOK  = commonInfo + attrPrepared.replace("$statusPlaceholder$",  "ok"); 
		String recordKO  = commonInfo + attrPrepared.replace("$statusPlaceholder$",  "ko"); 
		
		//-------------------------------
		// Common information
		
		for(String metric : GatlytronRecordStats.metricNames) {
			
			BigDecimal valueOK = record.getValue("ok_"+metric);
			BigDecimal valueKO = record.getValue("ko_"+metric);
			
			recordOK += SEPARATOR + ( (valueOK != null) ? valueOK : "") ;
			recordKO += SEPARATOR + ( (valueKO != null) ? valueKO : "");
		}
		
		if( Gatlytron.isKeepEmptyRecords() || record.hasData()) {
			
			if(Gatlytron.isKeepEmptyRecords() || record.hasDataOK()) { csv.append("\r\n"+recordOK); }
			if(Gatlytron.isKeepEmptyRecords() || record.hasDataKO()) { csv.append("\r\n"+recordKO); }
		}
		
	}

	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void terminate() {
		// nothing to do
	}

}
