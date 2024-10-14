package com.performetriks.gatlytron.reporting;

/***************************************************************************
 * Interface for reporting data received through Graphite protocol.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public interface GatlytronReporterDatabase extends GatlytronReporter {

	public abstract void reportTestSettings(String simulationName);
	
}
