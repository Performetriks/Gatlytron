package com.performetriks.gatlytron.injection;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;

public class BytecodeTransformer implements ClassFileTransformer {
	
	
	/*******************************************************************************
	 * 
	 *******************************************************************************/
	public byte[] transform(ClassLoader loader, String className,
			Class classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		
		byte[] byteCode = classfileBuffer;

		//Agent.log("[INFO] "+className);
//		if (className.startsWith("io/gatling/core/stats")) {
//			System.out.println("[CALL TRANSFORM] "+className);
//		}
		
		if (className.equals("io/gatling/core/stats/DataWritersStatsEngine")) {
			System.out.println("Found CLASS!!!  Woohoo!!! "+className);
			//byteCode = adjustScalaDataWriterStatsEngine(loader, className, classfileBuffer, byteCode); 
			return byteCode;
		}else if (className.equals("io/gatling/core/stats/StatsEngine")) {
			System.out.println("Found CLASS!!!  Woohoo!!! "+className);
			//byteCode = adjustScalaDataWriterStatsEngine(loader, className, classfileBuffer, byteCode); 
			return byteCode;
		}	

		return byteCode;

	}
	
	/*******************************************************************************
	 * 
	 *******************************************************************************/
	private byte[] adjustScalaDataWriterStatsEngine(ClassLoader loader, String className, byte[] classfileBuffer, byte[] byteCode) {
		
		try {
			ClassPool pool = ClassPool.getDefault();
			pool.insertClassPath(new LoaderClassPath(loader));
			
			CtClass htmlTemplateClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

			InjectionAgent.log("[INFO] Start Instrumenting AbstractTemplateHTML: "+className);
			
			//------------------------
			// Change Html Method
			//------------------------
			CtClass requestClass = pool.get("javax.servlet.http.HttpServletRequest");
			CtConstructor constructor =  htmlTemplateClass.getDeclaredConstructor(new CtClass[]{requestClass});

			constructor.insertAfter("this.addCSSFile(\"/custom/css/injected.css\");"); 
			constructor.insertAfter("this.addJSFileBottom(\"/custom/js/injected.js\");"); 

			byteCode = htmlTemplateClass.toBytecode();
			htmlTemplateClass.detach();
			
		} catch (Exception e) {
			InjectionAgent.log("[ERROR] AgentTransformer.adjustHTMLTemplate()", e);
			
		}
		
		InjectionAgent.log("[INFO] End Instrumenting AbstractTemplateHTML");
		
		return byteCode;
	}

	/*******************************************************************************
	 * 
	 *******************************************************************************/
	private byte[] replaceWholeMethodBody(ClassLoader loader, String className, byte[] classfileBuffer, byte[] byteCode) {
		
		try {
			ClassPool pool = ClassPool.getDefault();
			pool.insertClassPath(new LoaderClassPath(loader));
			
			CtClass snapshotView = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

			InjectionAgent.log("[INFO] Start Instrumenting SnapshotView: "+className);
			
			//------------------------
			// Change Html Method
			//------------------------
			CtClass printWriterClass = pool.get("java.io.PrintWriter");
			CtMethod printHtml = snapshotView.getDeclaredMethod("printHtml", new CtClass[]{printWriterClass});

			printHtml.addLocalVariable("myOut", printWriterClass);
			//printHtml.addLocalVariable("path", pool.get("java.nio.file.Path"));
			
			printHtml.setBody(
					"{ java.io.PrintWriter myOut = $1; "+
					  "try{"
						+ "java.io.File file = new java.io.File(\"content.html\");"
						+ "java.nio.file.Path path = java.nio.file.Paths.get(file.toURI());"
						+ "java.util.List fileContent = java.nio.file.Files.readAllLines(path);"
						
						+ "for(int i = 0; i < fileContent.size(); i++) {" 
						+    "myOut.println(fileContent.get(i).toString());"
						+ "}"
					+ "}catch (Exception e) {"
						+ "myOut.print(\"ERROR reading file content.html: \"+ e.getMessage()); "
						+ "com.peng.spm.extention.agent.Agent.log(\"ERROR reading file content.html:\", e);"
						
					+ "}"	+ 
					" }");
			
			
			// This is more or less the inserted code above
			// can uncommented for checking if it compiles.
			
//				try {
//					java.io.File file = new java.io.File(Agent.CUSTOM_DIR_PATH+"/content.html");
//					java.nio.file.Path path = java.nio.file.Paths.get(file.toURI());
//					java.util.List fileContent = java.nio.file.Files.readAllLines(path);
//					
//					for(int i = 0; i < fileContent.size(); i++) {
//						fileContent.get(i).toString();
//					}
//				} catch (Exception e) {
//					//com.peng.spm.extention.agent.Agent.log("ERROR reading file content.html:", e);
//				}
			
			byteCode = snapshotView.toBytecode();
			snapshotView.detach();
			
		} catch (Exception e) {
			InjectionAgent.log("[ERROR] AgentTransformer.adjustSnapshotView()", e);
			
		}
		
		InjectionAgent.log("[INFO] End Instrumenting SnapshotView");
		
		return byteCode;
	}
	
	
	/*******************************************************************************
	 * 
	 *******************************************************************************/
	private static byte[] modifyVariableExample(ClassLoader loader, String className, byte[] classfileBuffer, byte[] byteCode) {
			
		try {
			ClassPool pool = ClassPool.getDefault();
			pool.insertClassPath(new LoaderClassPath(loader));
			
			CtClass healthView = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

			InjectionAgent.log("[INFO] Start Instrumenting HealthView: "+className);
			
			CtClass requestWrapperClass = pool.get("com.pengtoolbox.util.html.RequestWrapper");
			CtClass requestEventClass = pool.get("com.pengtoolbox.util.html.RequestEvent");
			CtMethod makeProjectList = healthView.getDeclaredMethod("makeProjectList", new CtClass[]{requestWrapperClass, requestEventClass});
			
			makeProjectList.insertBefore("this.widths = new String[] { \"25%\", \"65%\", \"8%\", \"2%\" };");

			byteCode = healthView.toBytecode();
			healthView.detach();
			
		} catch (Exception e) {
			InjectionAgent.log("[ERROR] AgentTransformer.adjustHealthView()", e);
			
		}
		
		InjectionAgent.log("[INFO] End Instrumenting HealthView");
		
		return byteCode;

  }

}
