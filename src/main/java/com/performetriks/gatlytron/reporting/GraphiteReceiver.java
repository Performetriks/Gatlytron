package com.performetriks.gatlytron.reporting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GraphiteReceiver {
	
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
		        
				// TODO Auto-generated method stub
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
								System.out.println("graphiteMessage: "+graphiteMessage);
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
