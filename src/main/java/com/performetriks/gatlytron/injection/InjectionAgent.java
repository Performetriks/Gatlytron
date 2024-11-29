package com.performetriks.gatlytron.injection;

import java.lang.instrument.Instrumentation;

/***************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class InjectionAgent {

	/*****************************************************************************
	 * 
	 *****************************************************************************/
	public static void agentmain(String args, Instrumentation instr) {
		InjectionAgent.log("INFO", "execute agentmain()...");
		
		premain(args, instr);
	}
	
	
	/*****************************************************************************
	 * 
	 *****************************************************************************/
	public static void premain(String args, Instrumentation instr) {
			
		InjectionAgent.log("INFO", "Add Bytecode Transformer");
		instr.addTransformer(new BytecodeTransformer());
	}


	/*****************************************************************************
	 * 
	 *****************************************************************************/
	public static void log(String level, String message) {
		System.out.println("["+level+"] Gatlytron InjectionAgent: "+message);
	}
	
	/*****************************************************************************
	 * 
	 *****************************************************************************/
	public static void log(String level, String message, Throwable e) {
		
		StringBuffer errorBuffer = new StringBuffer(e.toString());
		
		for(StackTraceElement s : e.getStackTrace()) {
			errorBuffer.append("\n"+s.toString());
		}
		
		message += errorBuffer.toString();
		
		InjectionAgent.log(level, message);
	}

}
