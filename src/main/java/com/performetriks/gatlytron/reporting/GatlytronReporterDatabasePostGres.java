package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.database.DBInterface;
import com.performetriks.gatlytron.database.GatlytronDBInterface;
import com.performetriks.gatlytron.stats.GatlytronRecordStats;

/***************************************************************************
 * This reporter stores the data in a Postgres Database.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class GatlytronReporterDatabasePostGres implements GatlytronReporterDatabase {

	private DBInterface db;
	GatlytronDBInterface gtronDB;
	
	/****************************************************************************
	 * 
	 * @param servername name of the database server
	 * @param port the database port
	 * @param dbName the name of the database
	 * @param tableNamePrefix the name prefix that should be used for the tables (will be created)
	 * @param username the username for accessing the database
	 * @param password the password for accessing the database
	 ****************************************************************************/
	public GatlytronReporterDatabasePostGres(
			  String servername
			, int port
			, String dbName
			, String tableNamePrefix
			, String username
			, String password) {
		
		String uniqueName = servername + port + dbName;
		
		db = DBInterface.createDBInterfacePostgres(uniqueName, servername, port, dbName, username, password);
		
		gtronDB = new GatlytronDBInterface(db, tableNamePrefix);
		gtronDB.createTables();
		
		//----------------------------
		// Add P25 Column
		String addOkP25Column = "ALTER TABLE "+gtronDB.tablenameStats+" ADD IF NOT EXISTS ok_p25 DECIMAL(32,3);";
		db.preparedExecute(addOkP25Column);
		
		String addKoP25Column = "ALTER TABLE "+gtronDB.tablenameStats+" ADD IF NOT EXISTS ko_p25 DECIMAL(32,3);";
		db.preparedExecute(addKoP25Column);
	}			

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
		// nothing to do
	}

}
