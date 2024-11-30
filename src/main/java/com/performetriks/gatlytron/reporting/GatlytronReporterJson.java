package com.performetriks.gatlytron.reporting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
public class GatlytronReporterJson implements GatlytronReporter {

	private static final Logger logger = LoggerFactory.getLogger(GatlytronReporterJson.class);
	
	private String filepath;

	private boolean makeArray = false;
	private String arrayPrefix = "";
	
	BufferedWriter writer = null;
	
	/****************************************************************************
	 * 
	 * @param the path of the file to write the data to.
	 * @param makeArray set to true to make the file content a JSON Array.
	 *					If false, writes a JSON Object string per line.
	 ****************************************************************************/
	public GatlytronReporterJson(String filepath, boolean makeArray) {
		
		this.makeArray = makeArray;
		if(makeArray) {
			arrayPrefix = ", ";
		}
		
		this.filepath = filepath;
		try {
			Path path = Path.of(filepath);
			Files.deleteIfExists(path);
			
			writer = new BufferedWriter(new FileWriter(filepath, true));
		    
			if(makeArray) {
				writer.write("[\n");
			}
		} catch (IOException e) {
			logger.error("Error while deleting JSON file.", e);
		}
		
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void reportRecords(ArrayList<GatlytronDataRecord> records) {

		try {

			for(GatlytronDataRecord record : records ) {
				writer.write(record.toJsonString() + "\r\n" + arrayPrefix);
				
			}

		} catch (IOException e) {
			logger.error("Error while writing JSON data to file.", e);
		}
			
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void terminate() {
		try {
			
			if(makeArray) {
				writer.write("]");
			}

		} catch (IOException e) {
			logger.error("Error while writing JSON data to file.", e);
		}finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.error("Error while closing JSON file writer.", e);
				}
			}
		}
	}

	
	
}
