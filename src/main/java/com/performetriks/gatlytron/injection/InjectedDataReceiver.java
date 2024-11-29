package com.performetriks.gatlytron.injection;

import io.gatling.commons.stats.Status;
import scala.Option;
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
			, List groups
			, String requestName
			, long startTimestamp
			, long endTimestamp
			, Status status
			, Option responseCode
			, Option message
			){
		
		System.out.println("================== Data Types ==============");
		System.out.println("scenario:"+scenario);
		System.out.println("groups:"+groups.size());
		System.out.println("requestName:"+requestName);
		System.out.println("startTimestamp:"+startTimestamp);
		System.out.println("endTimestamp:"+endTimestamp);
		System.out.println("status:"+status.name());
		System.out.println("responseCode:"+responseCode);
		System.out.println("message:"+message);
		System.out.println("============================================");
	}
}
