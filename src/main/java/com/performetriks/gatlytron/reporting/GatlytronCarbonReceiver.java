package com.performetriks.gatlytron.reporting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.performetriks.gatlytron.base.Gatlytron;

/***************************************************************************
 * This class is used to start a thread that listens on a port for data
 * sent in Carbon protocol format by gatling. (https://graphite.readthedocs.io/en/latest/feeding-carbon.html)
 * 
 * Currently supported format: <metric path> <metric value> <metric timestamp>
 * Unsupported format: [(path, (timestamp, value)), ...]
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronCarbonReceiver {
	
	private static final Logger logger = LoggerFactory.getLogger(GatlytronCarbonReceiver.class);
	
	private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    
    private static int sleepInterval = 100;
    /***************************************************************************
     *
     ***************************************************************************/
    public static void start(int port) {
    	
	    new Thread(createRunnable(port)).start();
    	
    }
    
    /***************************************************************************
    *
    ***************************************************************************/
    private static Runnable createRunnable(int port) {
    	
    	return new Runnable() {
			
			@Override
			public void run() {
				
		    	try {
			        serverSocket = new ServerSocket(port);
			        clientSocket = serverSocket.accept();
			        
			        out = new PrintWriter(clientSocket.getOutputStream(), true);
			        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			        
		    	}catch(IOException e) {
		    		logger.error("Error while initializing socket connection on port "+port, e);
		    	}
		        
		    	LinkedHashMap<GatlytronCarbonRecord,GatlytronCarbonRecord> existingRecords = new LinkedHashMap<>();
		    	String lastTime = null;
				while (true) {

					//-------------------------------
					// Read Message
					try {
						
						//-------------------------------
						// Iterate all Messages
						String graphiteMessage = null;
						
						
						if(in.ready() 
						&& !clientSocket.isClosed()
						&& !serverSocket.isClosed()) {
							while( (graphiteMessage = in.readLine()) != null) {
								GatlytronCarbonRecord record = new GatlytronCarbonRecord(graphiteMessage, existingRecords);
								
								if(lastTime == null) {
									lastTime = record.time();
								}else if( !lastTime.equals(record.time()) ) {
									lastTime = record.time();
									
									existingRecords.remove(record); // remove as timestamp has changed
									
									
									for (GatlytronReporter reporter : Gatlytron.getReporterList()){
										ArrayList<GatlytronCarbonRecord> clone = new ArrayList<>();
										clone.addAll(existingRecords.values());
									    reporter.report(clone);
									}

									
									existingRecords = new LinkedHashMap<>();
									existingRecords.put(record, record); // put into new collection
								}
							}
						}
						
						//---------------------------------
						// Wait for more input
						try {
							Thread.sleep(sleepInterval);
						} catch (InterruptedException e) {
							logger.error("Thread interrupted while waiting for carbon protocol data.", e);
							Thread.currentThread().interrupt();
						}
						
					} catch (IOException e) {
						logger.error("Error while reading carbon protocol data.", e);
					}
				}
			}
		};
    	
    	
    }

    /***************************************************************************
     *
     ***************************************************************************/
    public static void stop() {
        try {
			in.close();
			out.close();
	        clientSocket.close();
	        serverSocket.close();
		} catch (IOException e) {
			logger.error("Error while closing socket connection.", e);
		}
        
    }
    
}
