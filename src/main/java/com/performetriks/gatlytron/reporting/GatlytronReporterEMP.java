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

/***************************************************************************
 * This reporter send the received records and sends them to an instance of
 * the open source tool Engineered Monitoring Platform(EMP), which you can find
 * <a href="https://github.com/xresch/EngineeredMonitoringPlatform">here</a>.
 * 
 * Data is pushed to the EMP API Endpoints 
 * 		EAVStats.pushStats and 
 * 		EAVStats.pushStatsCSV 
 * and the token used for the connection needs permission to use that endpoint.
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
	private static String CSV_HEADER = "category,entity,attributes";
	
	static {
		
		for(String metric : GatlytronCarbonRecord.metricNames) {
			CSV_HEADER += ","+metric;
		}

		CSV_HEADER = CSV_HEADER
						.replace(",", SEPARATOR)
						.replace("mean", "avg")  // emp uses name "avg"
						;
	}
	
	private static final String ATTRIBUTES_OK = "\"{type: \\\"ok\\\"}\""; 
	private static final String ATTRIBUTES_KO = "\"{type: \\\"ko\\\"}\""; 
	private static final String ATTRIBUTES_ALL = "\"{type: \\\"all\\\"}\""; 
	private static final String ATTRIBUTES_USER = "\"{type: \\\"user\\\"}\""; 
	
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
	public void report(ArrayList<GatlytronCarbonRecord> records) {
		
		URI apiEndpoint = URI.create(empURL+"/app/api?apiName=EAVStats&actionName=pushStatsCSV"
				+ "&SEPARATOR="+SEPARATOR);
		
		StringBuilder csv = new StringBuilder();
		csv.append(CSV_HEADER);
		
		//----------------------------------
		// Create Request Data
		for(GatlytronCarbonRecord record : records) {
			
			if(record.isRequestRecord()) {
				this.addCSVRequestRecords(csv, record);
			}else {
				
			}
		}
		
		String postBody = csv.toString();
		
		logger.debug("==== EMP: CSV Body ====");
		logger.debug(postBody);
		System.out.println(postBody);
		
		//----------------------------------
		// create CSV Records
		HttpRequest request = HttpRequest.newBuilder(apiEndpoint)
				.POST(HttpRequest.BodyPublishers.ofString(postBody))
				.header("API-Token", apiToken)
				.build();
		
		try {
			HttpResponse<String> response = 
					client.send(
							  request
							, HttpResponse.BodyHandlers.ofString()
						);
		} catch (Exception e) {
			logger.error("EMP: An Error occured while calling the API.", e);
		}
		
		
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	private void addCSVRequestRecords(StringBuilder csv, GatlytronCarbonRecord record) {
		
		if(record.isUserRecord()) {
			logger.warn("Unexpected type of Carbon record. Expected type 'request' but got type 'user'.");
			return;
		}
		
		//-------------------------------
		// Initialize Values
		String category = categoryPrefix+record.getSimulation();
		String entityName = record.getRequest();
		
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
		
		String recordOK  = commonInfo+ATTRIBUTES_OK; 
		String recordKO  = commonInfo+ATTRIBUTES_KO; 
		String recordALL = commonInfo+ATTRIBUTES_ALL; 
		
		//-------------------------------
		// Common information

		for(String metric : GatlytronCarbonRecord.metricNames) {
			
			BigDecimal valueOK = record.getValue("ok_"+metric);
			BigDecimal valueKO = record.getValue("ko_"+metric);
			BigDecimal valueALL = record.getValue("all_"+metric);
			
			recordOK += SEPARATOR + ( (valueOK != null) ? valueOK : "") ;
			recordKO += SEPARATOR + ( (valueKO != null) ? valueKO : "");
			recordALL += SEPARATOR + ( (valueALL != null) ? valueALL : "");
		}
		
		if(record.hasRequestData()) {
			csv.append("\r\n"+recordALL);
			
			if(!record.hasRequestDataOK()) { csv.append("\r\n"+recordOK); }
			if(!record.hasRequestDataKO()) {csv.append("\r\n"+recordKO); }
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
