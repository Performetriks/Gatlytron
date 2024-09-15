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
public class GatlytronReporterSysout implements GatlytronReporter {


	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void report(ArrayList<GatlytronCarbonRecord> records) {
		
		for(GatlytronCarbonRecord record : records ) {
			if( record.hasRequestData() || record.isUserRecord() ) {
				System.out.println( record.toJsonString() );
			}
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
