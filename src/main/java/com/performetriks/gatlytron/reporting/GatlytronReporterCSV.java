package com.performetriks.gatlytron.reporting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.performetriks.gatlytron.stats.GatlytronRecordStats;

/***************************************************************************
 * This reporter writes report data to a CSV file.
 * You might choose the separator for your CSV data so that you can properly delimit your data.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronReporterCSV implements GatlytronReporter {

	private static final Logger logger = LoggerFactory.getLogger(GatlytronReporterCSV.class);
	
	private String separator;
	private String filepath;
	private Path path;
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	public GatlytronReporterCSV(String filepath, String separator) {
		
		this.filepath = filepath;
		this.separator = separator;
		try {
			path = Path.of(filepath);
			String header = GatlytronRecordStats.getCSVHeader(separator);
			Files.deleteIfExists(path);
			
			Files.write(path, header.getBytes() 
					, StandardOpenOption.WRITE
					, StandardOpenOption.CREATE
					, StandardOpenOption.SYNC
					);
			
		} catch (IOException e) {
			logger.error("Error while initializing CSV file.", e);
		}
		
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void reportRecords(ArrayList<GatlytronRecordStats> records) {
		BufferedWriter writer = null;
		try {
			
			writer = new BufferedWriter(new FileWriter(filepath, true));
	    
			for(GatlytronRecordStats record : records ) {
				writer.write(record.toCSV(separator)+"\r\n");
			}
			
			
		} catch (IOException e) {
			logger.error("Error while writing CSV data.", e);
		}finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.error("Error while closing CSV file.", e);
				}
			}
		}
			
	}
	
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void terminate() {
		// nothing to do
	}

	
	
}
