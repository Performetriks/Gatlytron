package com.performetriks.gatlytron.stats;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.performetriks.gatlytron.base.Gatlytron;
import com.performetriks.gatlytron.reporting.GatlytronReporter;
import com.performetriks.gatlytron.stats.GatlytronRecordRaw.GatlytronRecordType;

/***************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronStatsEngine {
	
	private static final Logger logger = LoggerFactory.getLogger(GatlytronStatsEngine.class);
	
	private static final Object SYNC_LOCK = new Object();

	// key is a group name, value are all records that are part of the group
	private static TreeMap<String, ArrayList<GatlytronRecordRaw> > groupedRecords = new TreeMap<>();

	private static int reportInterval = 15;
	private static Thread reporterThread;
	private static boolean isStopped;
	
	/***************************************************************************
	 * Starts the reporting of the statistics.
	 *  
	 ***************************************************************************/
	public static void start(int reportInterval) {
		GatlytronStatsEngine.reportInterval = reportInterval;
		
		reporterThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				try {
					while( !Thread.currentThread().isInterrupted()
						&& !isStopped
						){
						Thread.sleep(reportInterval * 1000);
						aggregateAndReport();
					}
				
				}catch(InterruptedException e) {
					logger.info("GatlytronStatsEngine has been stopped.");
				}
			}
		});
		
		reporterThread.start();
	}
	
	/***************************************************************************
	 * Stops the stats engine
	 ***************************************************************************/
	public static void stop() {
		
		isStopped = true;
		reporterThread.interrupt();
	}
	
	/***************************************************************************
	 * 
	 ***************************************************************************/
	public static void addRecord(GatlytronRecordRaw record) {

		synchronized (SYNC_LOCK) {
			String id = record.getStatsIdentifier();
			
			if( !groupedRecords.containsKey(id) ) {
				groupedRecords.put(id, new ArrayList<>() );
			}
			
			groupedRecords.get(id).add(record);
		}
	
	}

	/***************************************************************************
	 * 
	 ***************************************************************************/
	private static void aggregateAndReport() {
		
		LinkedHashMap<GatlytronRecordStats, GatlytronRecordStats> statsRecords = new LinkedHashMap<>();
		
		//----------------------------------------
		// Steal Reference to not block writing
		// new records
		TreeMap<String, ArrayList<GatlytronRecordRaw> > groupedRecordsCurrent;
		synchronized (SYNC_LOCK) {
			groupedRecordsCurrent = groupedRecords;
			groupedRecords = new TreeMap<>();
		}
		

		//----------------------------------------
		// Iterate Groups
		for(Entry<String, ArrayList<GatlytronRecordRaw>> entry : groupedRecordsCurrent.entrySet()) {
			
			ArrayList<GatlytronRecordRaw> records = entry.getValue();
			
			//---------------------------
			// Make list of Sorted Values
			ArrayList<BigDecimal> values = new ArrayList<>();
			BigDecimal sum = BigDecimal.ZERO;
			
			for(GatlytronRecordRaw raw : records) {
				BigDecimal value = raw.getMetricValue();
				if(value != null) {
					values.add(value);
					sum = sum.add(value);
				}
			}
			
			// skip if group is empty
			if(values.isEmpty()) { continue; }
			
			values.sort(null);
			
			//---------------------------
			// Calculate Stats
			BigDecimal count 	= new BigDecimal(values.size());
			BigDecimal avg 		= sum.divide(count, RoundingMode.HALF_UP);
			BigDecimal min 		= values.get(0);
			BigDecimal max 		= values.get( values.size()-1 );
			BigDecimal stdev 	= bigStdev(values, avg, false);
			BigDecimal p50 		= bigPercentile(50, values);
			BigDecimal p75 		= bigPercentile(50, values);
			BigDecimal p95 		= bigPercentile(95, values);
			BigDecimal p99 		= bigPercentile(99, values);
			
			//---------------------------
			// Create StatsRecord
			GatlytronRecordRaw firstRecord = records.get(0);
			
			if(firstRecord.getType().equals(GatlytronRecordType.REQUEST) ) {

				new GatlytronRecordStats(
					  statsRecords
					, firstRecord.getStatus()
				    , System.currentTimeMillis()
					, Gatlytron.getSimulationName()
					, firstRecord.getMetricPath()
					, count 
					, avg 
					, min 		
					, max 			
					, stdev 	
					, p50 		
					, p75 		
					, p95 		
					, p99 	
				);

			}
			
		}
		
		//-------------------------------
		// Report Stats
		sendRecordsToReporter(statsRecords);
		
	}
	
	/***************************************************************************
     * Send the records to the Reporters, resets the existingRecords.
     * 
     ***************************************************************************/
	private static void sendRecordsToReporter(
			LinkedHashMap<GatlytronRecordStats, GatlytronRecordStats> statsRecords
			){
		
		System.out.println("%%%%%%%% statsRecords.size(): "+statsRecords.size());
		//-------------------------
		// Filter Records
		ArrayList<GatlytronRecordStats> finalRecords = new ArrayList<>();
		for (GatlytronRecordStats record : statsRecords.values()){
			
			if( Gatlytron.isKeepEmptyRecords()
			 || record.hasRequestData() 
			 || record.isUserRecord() 
			 ){
				finalRecords.add(record);
			}
		}
		
		//-------------------------
		// Send Clone of list to each Reporter
		for (GatlytronReporter reporter : Gatlytron.getReporterList()){
			ArrayList<GatlytronRecordStats> clone = new ArrayList<>();
			clone.addAll(finalRecords);
			logger.debug("Report data to: "+reporter.getClass().getName());
		    reporter.reportRecords(clone);
		}

	}
	
	/***************************************************************************
	 * Send the test settings to Database Reporters.
	 * 
	 ***************************************************************************/
//	private static void sendTestSettingsToDBReporter() {
//		
//		//-------------------------
//		// Send Clone of list to each Reporter
//		for (GatlytronReporter reporter : Gatlytron.getReporterList()){
//			if(reporter instanceof GatlytronReporterDatabase) {
//				logger.debug("Send TestSettings Data to: "+reporter.getClass().getName());
//				((GatlytronReporterDatabase)reporter).reportTestSettings(firstRecord.getSimulation());
//			}
//		}
//		
//	}
	
	
	/***********************************************************************************************
	 * 
	 * @param percentile a value between 0 and 100
	 * @param valuesSorted a value between 0 and 100
	 * 
	 ***********************************************************************************************/
	public static BigDecimal bigPercentile(int percentile, List<BigDecimal> valuesSorted) {
		
		while( valuesSorted.remove(null) ); // remove all null values
		
		int count = valuesSorted.size();
		
		if(count == 0) {
			return null;
		}
				
		int percentilePosition = (int)Math.ceil( count * (percentile / 100f) );
		
		//---------------------------
		// Retrieve number
		
		if(percentilePosition > 0) {
			// one-based position, minus 1 to get index
			return valuesSorted.get(percentilePosition-1);
		}else {
			return valuesSorted.get(0);
		}
		
	}
	
	/***********************************************************************************************
	 * 
	 ***********************************************************************************************/
	public static BigDecimal bigStdev(List<BigDecimal> values, BigDecimal average, boolean usePopulation) {
		
		//while( values.remove(null) );
		
		// zero or one number will have standard deviation 0
		if(values.size() <= 1) {
			return BigDecimal.ZERO;
		}
	
//		How to calculate standard deviation:
//		Step 1: Find the mean/average.
//		Step 2: For each data point, find the square of its distance to the mean.
//		Step 3: Sum the values from Step 2.
//		Step 4: Divide by the number of data points.
//		Step 5: Take the square root.
		
		//-----------------------------------------
		// STEP 1: Find Average
		BigDecimal count = new BigDecimal(values.size());
		
		BigDecimal sumDistanceSquared = BigDecimal.ZERO;
		
		for(BigDecimal value : values) {
			//-----------------------------------------
			// STEP 2: For each data point, find the 
			// square of its distance to the mean.
			BigDecimal distance = value.subtract(average);
			//-----------------------------------------
			// STEP 3: Sum the values from Step 2.
			sumDistanceSquared = sumDistanceSquared.add(distance.pow(2));
		}
		
		//-----------------------------------------
		// STEP 4 & 5: Divide and take square root
		
		BigDecimal divisor = (usePopulation) ? count : count.subtract(BigDecimal.ONE);
		
		BigDecimal divided = sumDistanceSquared.divide(divisor, RoundingMode.HALF_UP);
		
		// TODO JDK8 Migration: should work with JDK 9
		MathContext mc = new MathContext(3, RoundingMode.HALF_UP);
		BigDecimal standardDeviation = divided.sqrt(mc);
		
		return standardDeviation;
	}
	
	
	
	
	
	
}
