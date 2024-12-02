package com.performetriks.gatlytron.injection;

import com.performetriks.gatlytron.base.Gatlytron;
import com.performetriks.gatlytron.stats.GatlytronRecordSingle;

import io.gatling.commons.stats.Status;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;

/***************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class InjectedDataReceiver {
	
	public static void logResponse(
			  String scenario
			, List<String> groups
			, String requestName
			, long startTimestamp
			, long endTimestamp
			, Status status
			, Option responseCode
			, Option message
			){
		
		
		
		//----------------------------------
		// Create Record
		GatlytronRecordSingle record = new GatlytronRecordSingle(
				scenario
				, JavaConverters.asJava(groups)
				, requestName
				, startTimestamp
				, endTimestamp
				, status.name()
				, responseCode.get().toString()
				, message.toString()
				);
		
		
		//----------------------------------
		// Print Sysout
		if(Gatlytron.isRawDataToSysout()) {
			System.out.println(record.toLogString());
		}
		
	}
}
