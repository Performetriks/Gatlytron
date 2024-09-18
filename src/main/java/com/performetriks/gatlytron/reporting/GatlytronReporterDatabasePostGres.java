package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.database.DBInterface;

/***************************************************************************
 * This reporter prints the records as JSON data to sysout.
 * Useful for debugging.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronReporterDatabasePostGres implements GatlytronReporter {

	private DBInterface db;
	private String tableName;
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	public GatlytronReporterDatabasePostGres(
			  String servername
			, int port
			, String dbName
			, String tableName
			, String username
			, String password) {
		
		this.tableName = tableName;
		
		String uniqueName = servername + port + dbName;
		
		db = DBInterface.createDBInterfacePostgres(uniqueName, servername, port, dbName, username, password);
		
		this.createTable();
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	private void createTable() {
		
		if(db == null) { return; }
		
		String createTable = GatlytronCarbonRecord.getSQLCreateTableTemplate(tableName);
		db.preparedExecute(createTable);
		
		
	}
			

	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void report(ArrayList<GatlytronCarbonRecord> records) {
		
		for(GatlytronCarbonRecord record : records ) {
			record.insertIntoDatabase(db, tableName);
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
