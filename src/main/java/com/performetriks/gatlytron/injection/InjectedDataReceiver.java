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
		
		System.out.println(record.toLogString());
		
		//----------------------------------
		// Debug
//		if(Gatlytron.isDebug()) {
//			System.out.println("================== Data Types ==============");
//			System.out.println("scenario:"+scenario);
//			System.out.println("groups:"+groups.mkString("-%|%-"));
//			System.out.println("requestName:"+requestName);
//			System.out.println("startTimestamp:"+startTimestamp);
//			System.out.println("endTimestamp:"+endTimestamp);
//			System.out.println("status:"+status.name());
//			System.out.println("responseCode:"+responseCode.get().toString());
//			System.out.println("message:"+message);
//			System.out.println("============================================");
//		}
		
		
	}
}
