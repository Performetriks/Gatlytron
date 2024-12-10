package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.stats.GatlytronRecordStats;

/***************************************************************************
 * Interface for creating a reporter.
 * This interface receives statistical data and can store it wherever your
 * heart wishes to have the data be stored.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public interface GatlytronReporter {

	public void reportRecords(ArrayList<GatlytronRecordStats> records);
	
	public void terminate();
	
}
