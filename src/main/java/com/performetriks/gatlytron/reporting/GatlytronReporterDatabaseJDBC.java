package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.database.DBInterface;

/***************************************************************************
 * This reporter stores the records in a database which is accessible with JDBC.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public abstract class GatlytronReporterDatabaseJDBC implements GatlytronReporter {

	private DBInterface db;
	private String tableName;
	
	/****************************************************************************
	 * 
	 * @param drivername the name of the JDBC driver class
	 * @param jdbcURL the url used to connect to the jdbc database
	 * @param tableName the name that should be used for the table (will be created)
	 * @param username the username for accessing the database
	 * @param password the password for accessing the database
	 ****************************************************************************/
	public GatlytronReporterDatabaseJDBC(
			  String driverName
			, String jdbcURL
			, String tableName
			, String username
			, String password) {
		
		this.tableName = tableName;
		
		String uniqueName = jdbcURL;
		
		db = DBInterface.createDBInterface(uniqueName, driverName, jdbcURL, username, password);
		
		String createTableSQL = this.getCreateTableSQL();
		this.createTable(createTableSQL);
	}
	
	/****************************************************************************
	 * Implement this class to return a SQL string to create the table.
	 * You can use the following method to create a template.
	 * Depending on your database, you might need to adjust the data types etc.
	 * 
	 * GatlytronCarbonRecord.getSQLCreateTableTemplate(tableName)
	 * 
	 ****************************************************************************/
	public abstract String getCreateTableSQL();

	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	private void createTable(String createTableSQL) {
		
		if(db == null) { return; }
		
		db.preparedExecute(createTableSQL);

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
