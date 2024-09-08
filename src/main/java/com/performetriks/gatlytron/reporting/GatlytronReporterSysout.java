package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

public class GatlytronReporterSysout implements GatlytronReporter {

	/****************************************************************************
	 * 
	 ****************************************************************************/
	public GatlytronReporterSysout() {
		

		
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void report(ArrayList<GatlytronCarbonRecord> records) {
		
		for(GatlytronCarbonRecord record : records ) {
			System.out.println( record.toJsonString() );
		}

	}

	
	
}
