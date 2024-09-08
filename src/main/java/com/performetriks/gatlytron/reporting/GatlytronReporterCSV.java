package com.performetriks.gatlytron.reporting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class GatlytronReporterCSV implements GatlytronReporter {

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
			String header = GatlytronCarbonRecord.getCSVHeader(separator);
			Files.deleteIfExists(path);
			
			Files.write(path, header.getBytes() 
					, StandardOpenOption.WRITE
					, StandardOpenOption.CREATE
					, StandardOpenOption.SYNC
					);
			
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
				
				writer.write(record.toCSV(separator)+"\r\n");
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
