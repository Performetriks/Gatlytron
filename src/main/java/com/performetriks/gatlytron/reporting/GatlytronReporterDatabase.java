package com.performetriks.gatlytron.reporting;

/***************************************************************************
 * Interface for reporting data to a database.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public abstract class GatlytronReporterDatabase implements GatlytronReporter {

	public abstract void reportTestSettings(String simulationName);
	
}
