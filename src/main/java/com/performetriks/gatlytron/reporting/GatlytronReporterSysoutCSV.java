package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.stats.GatlytronRecordStats;

/***************************************************************************
 * This reporter prints the records as CSV data to sysout.
 * Useful for debugging.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronReporterSysoutCSV implements GatlytronReporter {

	private String separator = ";";
	/****************************************************************************
	 * 
	 ****************************************************************************/
	public GatlytronReporterSysoutCSV (String separator){
		this.separator = separator;
	}

	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void reportRecords(ArrayList<GatlytronRecordStats> records) {
		
		System.out.println("\r\n" + GatlytronRecordStats.getCSVHeader(separator) );
		for(GatlytronRecordStats record : records ) {
			System.out.println( record.toCSV(separator) );
		}
		System.out.println(" ");
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void terminate() {
		// nothing to do
	}

}
