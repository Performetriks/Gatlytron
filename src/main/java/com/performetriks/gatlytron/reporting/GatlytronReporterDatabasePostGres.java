package com.performetriks.gatlytron.reporting;

import java.util.ArrayList;

import com.performetriks.gatlytron.database.DBInterface;
import com.performetriks.gatlytron.database.GatlytronDBInterface;
import com.performetriks.gatlytron.stats.GatlytronRecordStats;

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
public class GatlytronReporterDatabasePostGres implements GatlytronReporterDatabase {

	private DBInterface db;
	GatlytronDBInterface gtronDB;
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
		
		gtronDB = new GatlytronDBInterface(db, tableName);
		gtronDB.createTables();
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
