package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.stats.GatlytronRecordStats;

/***************************************************************************
 * Interface for reporting data received through Graphite protocol.
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
