package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.database.DBInterface;
import com.performetriks.gatlytron.database.GatlytronDBInterface;

/***************************************************************************
 * This reporter stores the records in a database which is accessible with JDBC.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public abstract class GatlytronReporterDatabaseJDBC implements GatlytronReporterDatabase {

	private DBInterface db;
	private GatlytronDBInterface gtronDB;
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
		
		gtronDB = this.getGatlytronDB(db, tableName);

		gtronDB.createTables();
	}
	
	/****************************************************************************
	 * Implement this class to return instance of GatlytronDBInterface.
	 * This allows you to make changes to SQLs defined in the GatlytronInterface
	 * to make any adaptions needed for your specific database.
	 * 
	 ****************************************************************************/
	public abstract GatlytronDBInterface getGatlytronDB(DBInterface dbInterface, String tableName);


	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void reportRecords(ArrayList<GatlytronCarbonRecord> records) {
		gtronDB.reportRecords(records);
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void reportTestSettings(String simulationName) {
		gtronDB.reportTestSettings(simulationName);
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	@Override
	public void terminate() {
		
	}

}
