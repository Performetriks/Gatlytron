package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.database.DBInterface;
import com.performetriks.gatlytron.database.GatlytronDBInterface;
import com.performetriks.gatlytron.stats.GatlytronRecordStats;

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
	
	/****************************************************************************
	 * 
	 * @param driverName the name of the JDBC driver class
	 * @param jdbcURL the url used to connect to the jdbc database
	 * @param tableNamePrefix the name prefix that should be used for the tables (will be created)
	 * @param username the username for accessing the database
	 * @param password the password for accessing the database
	 ****************************************************************************/
	public GatlytronReporterDatabaseJDBC(
			  String driverName
			, String jdbcURL
			, String tableNamePrefix
			, String username
			, String password) {
				
		String uniqueName = jdbcURL;
		
		db = DBInterface.createDBInterface(uniqueName, driverName, jdbcURL, username, password);
		
		gtronDB = this.getGatlytronDB(db, tableNamePrefix);

		gtronDB.createTables();
		
		//----------------------------
		// Add P25 Column
		String addOkP25Column = "ALTER TABLE "+gtronDB.tablenameStats+" ADD IF NOT EXISTS ok_p25 DECIMAL(32,3);";
		db.preparedExecute(addOkP25Column);
		
		String addKoP25Column = "ALTER TABLE "+gtronDB.tablenameStats+" ADD IF NOT EXISTS ko_p25 DECIMAL(32,3);";
		db.preparedExecute(addKoP25Column);
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
	public void reportRecords(ArrayList<GatlytronRecordStats> records) {
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
