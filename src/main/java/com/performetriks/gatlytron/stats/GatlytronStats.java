package com.performetriks.gatlytron.stats;

import java.util.ArrayList;
import java.util.TreeMap;

import scala.concurrent.duration.FiniteDuration;

/***************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronStats {
	
	private static TreeMap<String, ArrayList<GatlytronRecordSingle> > groupedRecords = new TreeMap<>();

	private static long writePeriod = 15;

	/******************************************************************
	 * Call of this method is injected into gatling code.
	 ******************************************************************/
	public static void setConsoleWritePeriod(FiniteDuration duration) {
		
		writePeriod = duration.toSeconds();
		System.out.println("WritePeriod: " + writePeriod);
		
	}
	/***************************************************************************
	 * 
	 ***************************************************************************/
	public static void addRecord(GatlytronRecordSingle record) {
		
		String id = record.getStatsIdentifier();
		
		if( !groupedRecords.containsKey(id) ) {
			groupedRecords.put(id, new ArrayList<>() );
		}
		
		groupedRecords.get(id).add(record);
	}
	
	
	
	
	
	
}
