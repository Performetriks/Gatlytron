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
    private static BufferedReader in;
    
	private static LinkedHashMap<GatlytronCarbonRecord,GatlytronCarbonRecord> existingRecords = new LinkedHashMap<>();
	private static String lastTime = null;
	private static Thread thread;
    
    private static int sleepInterval = 500;
    
    /***************************************************************************
     *
     ***************************************************************************/
    public static void start(int port) {
    	
	   thread = new Thread(createRunnable(port));
	   thread.start();
    	
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
			        
			        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			        
		    	}catch(IOException e) {
		    		logger.error("Error while initializing socket connection on port "+port, e);
		    	}
		        

				while (!Thread.currentThread().isInterrupted()) {
					
					try {
						handleCarbonData();
					} catch (IOException e) {
						logger.error("IO Exception while reading carbon data.", e);
					}
					
					//---------------------------------
					// Wait for more input
					try {
						Thread.sleep(sleepInterval);
					} catch (InterruptedException e) {
						logger.error("Thread interrupted while waiting for carbon protocol data.", e);
						Thread.currentThread().interrupt();
					}
				}
			}
		};
    	
    }

    /***************************************************************************
     * @throws IOException 
     ***************************************************************************/
	private static void handleCarbonData() throws IOException {

		//-------------------------------
		// Iterate all Messages
		String graphiteMessage = null;

		if(!clientSocket.isClosed()
		&& !serverSocket.isClosed()) {
			
			while( (graphiteMessage = in.readLine()) != null) {
				GatlytronCarbonRecord record = new GatlytronCarbonRecord(graphiteMessage, existingRecords);
				
				if(lastTime == null) {
					lastTime = record.time();
				}else if( !lastTime.equals(record.time()) ) {
					lastTime = record.time();
					
					existingRecords.remove(record); // remove as timestamp has changed
					
						sendRecordsToReporter();

					existingRecords.put(record, record); // put into new collection
					
				}
			}
		}
	}

	/***************************************************************************
     * Send the records to the Reporters, resets the existingRecords.
     * 
     ***************************************************************************/
	private static void sendRecordsToReporter() {
		for (GatlytronReporter reporter : Gatlytron.getReporterList()){
			ArrayList<GatlytronCarbonRecord> clone = new ArrayList<>();
			clone.addAll(existingRecords.values());
		    reporter.report(clone);
		}
		
		existingRecords = new LinkedHashMap<>();
	}
	
   /***************************************************************************
    *
    ***************************************************************************/
	public static void terminate() {

		//---------------------------------
		// Terminate Thread
		thread.interrupt();
		try {
			Thread.sleep(sleepInterval);
		} catch (InterruptedException e) {
			logger.error("Thread interrupted while waiting for carbon protocol data.", e);
			Thread.currentThread().interrupt();
		}
		
		//---------------------------------
		// Closing Things
		try {
			//in.close();
			//out.close();
	        //clientSocket.close();
	        serverSocket.close();
		} catch (IOException e) {
			logger.error("Error while closing socket connection.", e);
		}
		
		System.err.println("Z");
		
		//---------------------------------
		// Last Round of Writing Data
		System.err.println("THE LAST ROUND");
		try {
			handleCarbonData();
		} catch (IOException e) {
			logger.error("Error while reading carbon data.", e);
		}
		System.err.println("YYYY!!!???");
		if( !existingRecords.isEmpty() ) {
			sendRecordsToReporter();
			
		}
	       
	}
    
}
