package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.stats.GatlytronRecordStats;

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
public class GatlytronReporterSysoutJson implements GatlytronReporter {


	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void reportRecords(ArrayList<GatlytronRecordStats> records) {
		
		for(GatlytronRecordStats record : records ) {
			System.out.println( record.toJsonString() );
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
