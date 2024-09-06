package com.performetriks.gatlytron.base;

import com.performetriks.gatlytron.reporting.GatlytronCarbonReceiver;

/***************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class Gatlytron {

	
	/******************************************************************
	 * Enables the Gatlytron Graphite Receiver to do custom reports.
	 * @param port
	 ******************************************************************/
	public static void enableGraphiteReceiver(int port) {
		GatlytronCarbonReceiver.start(port);
	}
	
	
}
