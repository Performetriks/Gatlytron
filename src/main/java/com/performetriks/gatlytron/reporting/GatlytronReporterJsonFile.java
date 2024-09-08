package com.performetriks.gatlytron.reporting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class GatlytronReporterJsonFile implements GatlytronReporter {

	String filepath;
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	public GatlytronReporterJsonFile(String filepath) {
		
		this.filepath = filepath;
		try {
			
			Files.deleteIfExists(Path.of(filepath));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void report(ArrayList<GatlytronCarbonRecord> records) {
		BufferedWriter writer = null;
		try {
			
			writer = new BufferedWriter(new FileWriter(filepath, true));
	    
			for(GatlytronCarbonRecord record : records ) {
				
				writer.write(record.toJsonString()+"\r\n");
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			
	}

	
	
}
