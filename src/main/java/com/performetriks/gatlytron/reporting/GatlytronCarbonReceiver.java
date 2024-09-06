package com.performetriks.gatlytron.reporting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;

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
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
		    	}
		        
		    	LinkedHashMap<GatlytronCarbonRecord,GatlytronCarbonRecord> existingRecords = new LinkedHashMap<>();
				// TODO Auto-generated method stub
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
									System.out.println("===== start =====");
									for(GatlytronCarbonRecord printThis : existingRecords.values()) {
										System.out.println(printThis.toJsonString());
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
							e.printStackTrace();
							Thread.currentThread().interrupt();
						}
						
					} catch (IOException e) {
						e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
}
