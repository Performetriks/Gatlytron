package com.performetriks.gatlytron.injection;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

/***************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class BytecodeTransformer implements ClassFileTransformer {
	
	/*******************************************************************************
	 * 
	 *******************************************************************************/
	@SuppressWarnings("rawtypes")
	public byte[] transform(ClassLoader loader, String className,
			Class classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		
		byte[] byteCode = classfileBuffer;

		//InjectionAgent.log("[INFO] "+className);
		
		if (className.equals("io/gatling/core/stats/DataWritersStatsEngine")) {
			
			byteCode = adjustScalaDataWriterStatsEngine(loader, className, classfileBuffer, byteCode); 
			return byteCode;
		}
		// currently not needed,
//		else if (className.equals("io/gatling/core/stats/writer/ConsoleDataWriter")) {
//			
//			byteCode = adjustScalaConsoleDataWriter(loader, className, classfileBuffer, byteCode); 
//			return byteCode;
//		}	

		return byteCode;

	}
	
	/*******************************************************************************
	 * 
	 *******************************************************************************/
	private byte[] adjustScalaDataWriterStatsEngine(ClassLoader loader, String className, byte[] classfileBuffer, byte[] byteCode) {
		
		try {
			
			InjectionAgent.log("INFO", "Inject Bytecode into class: "+className);
			
			//----------------------------------
			// Load Class
			ClassPool pool = ClassPool.getDefault();
			pool.insertClassPath(new LoaderClassPath(loader));
			
			CtClass transformedClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

			//----------------------------------
			// Debug: Print list of all Methods
			//for(CtMethod method : transformedClass.getDeclaredMethods() ) {
			//	InjectionAgent.log("DEBUG", method.getName());
			//}
			
			//----------------------------------
			// Transform Method: logResponse
			CtMethod methodLogResponse = transformedClass.getDeclaredMethod("logResponse");

			methodLogResponse.insertBefore("com.performetriks.gatlytron.injection.InjectedDataReceiver"
										+".logResponse(scenario, groups, requestName, startTimestamp, endTimestamp, status, responseCode, message);");
			
			//----------------------------------
			// Transform Method: logUserStart
			CtMethod methodLogUserStart = transformedClass.getDeclaredMethod("logUserStart");
			
			methodLogUserStart.insertBefore("com.performetriks.gatlytron.injection.InjectedDataReceiver"
					+".logUserStart(scenario);");
			
			//----------------------------------
			// Transform Method: logUserEnd
			CtMethod methodLogUserEnd = transformedClass.getDeclaredMethod("logUserEnd");
			
			methodLogUserEnd.insertBefore("com.performetriks.gatlytron.injection.InjectedDataReceiver"
					+".logUserEnd(scenario);");
			
			//----------------------------------
			// Detach
			byteCode = transformedClass.toBytecode();
			transformedClass.detach();
			
		} catch (Exception e) {
			InjectionAgent.log("ERROR", "Error while transforming class: "+className, e);
			
		}
		
		InjectionAgent.log("INFO", "End Instrumenting class: "+className);
		
		return byteCode;
	}
	
	/*******************************************************************************
	 * 
	 *******************************************************************************/
//	private byte[] adjustScalaConsoleDataWriter(ClassLoader loader, String className, byte[] classfileBuffer, byte[] byteCode) {
//		
//		try {
//			
//			InjectionAgent.log("INFO", "Inject Bytecode into class: "+className);
//			
//			//----------------------------------
//			// Load Class
//			ClassPool pool = ClassPool.getDefault();
//			pool.insertClassPath(new LoaderClassPath(loader));
//			
//			CtClass transformedClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
//
//			//----------------------------------
//			// Transform Method: logResponse
//			CtMethod methodOnInit = transformedClass.getDeclaredMethod("onInit");
//
//			methodOnInit.insertBefore("com.performetriks.gatlytron.stats.GatlytronStats"
//										+".setConsoleWritePeriod("
//										   + "this.configuration.data().console().writePeriod()"
//										+ ");");
//			
//
//
//			//----------------------------------
//			// Detach
//			byteCode = transformedClass.toBytecode();
//			transformedClass.detach();
//			
//		} catch (Exception e) {
//			InjectionAgent.log("ERROR", "Error while transforming class: "+className, e);
//			
//		}
//		
//		InjectionAgent.log("INFO", "End Instrumenting class: "+className);
//		
//		return byteCode;
//	}

	/*******************************************************************************
	 * 
	 *******************************************************************************/
//	private byte[] replaceWholeMethodBody(ClassLoader loader, String className, byte[] classfileBuffer, byte[] byteCode) {
//		
//		try {
//			ClassPool pool = ClassPool.getDefault();
//			pool.insertClassPath(new LoaderClassPath(loader));
//			
//			CtClass snapshotView = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
//
//			InjectionAgent.log("INFO", "Start Instrumenting SnapshotView: "+className);
//			
//			//------------------------
//			// Change Html Method
//			//------------------------
//			CtClass printWriterClass = pool.get("java.io.PrintWriter");
//			CtMethod printHtml = snapshotView.getDeclaredMethod("printHtml", new CtClass[]{printWriterClass});
//
//			printHtml.addLocalVariable("myOut", printWriterClass);
//			//printHtml.addLocalVariable("path", pool.get("java.nio.file.Path"));
//			
//			printHtml.setBody(
//					"{ java.io.PrintWriter myOut = $1; "+
//					  "try{"
//						+ "java.io.File file = new java.io.File(\"content.html\");"
//						+ "java.nio.file.Path path = java.nio.file.Paths.get(file.toURI());"
//						+ "java.util.List fileContent = java.nio.file.Files.readAllLines(path);"
//						
//						+ "for(int i = 0; i < fileContent.size(); i++) {" 
//						+    "myOut.println(fileContent.get(i).toString());"
//						+ "}"
//					+ "}catch (Exception e) {"
//						+ "myOut.print(\"ERROR reading file content.html: \"+ e.getMessage()); "
//						+ "com.peng.spm.extention.agent.Agent.log(\"ERROR reading file content.html:\", e);"
//						
//					+ "}"	+ 
//					" }");
//			
//			
//			// This is more or less the inserted code above
//			// can uncommented for checking if it compiles.
//			
////				try {
////					java.io.File file = new java.io.File(Agent.CUSTOM_DIR_PATH+"/content.html");
////					java.nio.file.Path path = java.nio.file.Paths.get(file.toURI());
////					java.util.List fileContent = java.nio.file.Files.readAllLines(path);
////					
////					for(int i = 0; i < fileContent.size(); i++) {
////						fileContent.get(i).toString();
////					}
////				} catch (Exception e) {
////					//com.peng.spm.extention.agent.Agent.log("ERROR reading file content.html:", e);
////				}
//			
//			byteCode = snapshotView.toBytecode();
//			snapshotView.detach();
//			
//		} catch (Exception e) {
//			InjectionAgent.log("ERROR","AgentTransformer.adjustSnapshotView()", e);
//			
//		}
//		
//		InjectionAgent.log("INFO", "End Instrumenting SnapshotView");
//		
//		return byteCode;
//	}
	
	
	/*******************************************************************************
	 * just an example, not used
	 *******************************************************************************/
//	private static byte[] modifyVariableExample(ClassLoader loader, String className, byte[] classfileBuffer, byte[] byteCode) {
//			
//		try {
//			ClassPool pool = ClassPool.getDefault();
//			pool.insertClassPath(new LoaderClassPath(loader));
//			
//			CtClass healthView = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
//
//			InjectionAgent.log("INFO", "Start Instrumenting HealthView: "+className);
//			
//			CtClass requestWrapperClass = pool.get("com.pengtoolbox.util.html.RequestWrapper");
//			CtClass requestEventClass = pool.get("com.pengtoolbox.util.html.RequestEvent");
//			CtMethod makeProjectList = healthView.getDeclaredMethod("makeProjectList", new CtClass[]{requestWrapperClass, requestEventClass});
//			
//			makeProjectList.insertBefore("this.widths = new String[] { \"25%\", \"65%\", \"8%\", \"2%\" };");
//
//			byteCode = healthView.toBytecode();
//			healthView.detach();
//			
//		} catch (Exception e) {
//			InjectionAgent.log("ERROR","AgentTransformer.adjustHealthView()", e);
//			
//		}
//		
//		InjectionAgent.log("INFO","End Instrumenting HealthView");
//		
//		return byteCode;
//
//  }

}
