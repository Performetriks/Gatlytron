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
public class GatlytronReporterSysoutJson implements GatlytronReporter {


	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void reportRecords(ArrayList<GatlytronDataRecord> records) {
		
		for(GatlytronDataRecord record : records ) {
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
