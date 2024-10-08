package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.database.DBInterface;

/***************************************************************************
 * This reporter stores the data in a Postgres Database.
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
	 * @param servername name of the database server
	 * @param port the database port
	 * @param dbName the name of the database
	 * @param tableName the name that should be used for the table (will be created)
	 * @param username the username for accessing the database
	 * @param password the password for accessing the database
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
