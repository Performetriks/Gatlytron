package com.performetriks.gatlytron.reporting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***************************************************************************
 * This reporter writes json data to a file. the file will be written as
 * one json object per line. Every line is a valid JSON string.
 * The whole file itself is not a valid JSON string as it is not an array.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronReporterJsonFile implements GatlytronReporter {

	private static final Logger logger = LoggerFactory.getLogger(GatlytronReporterJsonFile.class);
	
	String filepath;
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	public GatlytronReporterJsonFile(String filepath) {
		
		this.filepath = filepath;
		try {
			
			Files.deleteIfExists(Path.of(filepath));

		} catch (IOException e) {
			logger.error("Error while deleting JSON file.", e);
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
			logger.error("Error while writing JSON data to file.", e);
		}finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.error("Error while closing JSON file.", e);
				}
			}
		}
			
	}

	
	
}
