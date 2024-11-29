package com.performetriks.gatlytron.injection;

import java.lang.instrument.Instrumentation;

public class InjectionAgent {

	/*****************************************************************************
	 * 
	 *****************************************************************************/
	public static void agentmain(String args, Instrumentation instr) {
		InjectionAgent.log("[INFO] execute agentmain()...");
		
		premain(args, instr);
	}
	
	
	/*****************************************************************************
	 * 
	 *****************************************************************************/
	public static void premain(String args, Instrumentation instr) {
			
		InjectionAgent.log("\r\n[INFO] ======================== Load Gatlytron Agent  ======================== ");
		instr.addTransformer(new BytecodeTransformer());
	}


	/*****************************************************************************
	 * 
	 *****************************************************************************/
	public static void log(String message) {
		System.out.println(message);
	}
	
	/*****************************************************************************
	 * 
	 *****************************************************************************/
	public static void log(String message, Throwable e) {
		
		StringBuffer errorBuffer = new StringBuffer(e.toString());
		
		for(StackTraceElement s : e.getStackTrace()) {
			errorBuffer.append("\n"+s.toString());
		}
		
		message += errorBuffer.toString();
		
		InjectionAgent.log(message);
	}

}
