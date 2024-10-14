package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

/***************************************************************************
 * This reporter prints the records as JSON data to sysout.
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
	public void reportRecords(ArrayList<GatlytronCarbonRecord> records) {
		
		System.out.println( GatlytronCarbonRecord.getCSVHeader(separator) );
		for(GatlytronCarbonRecord record : records ) {
			System.out.println( record.toCSV(separator) );
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
