package com.performetriks.gatlytron.database;

import java.util.ArrayList;

import com.performetriks.gatlytron.base.Gatlytron;
import com.performetriks.gatlytron.base.GatlytronScenario;
import com.performetriks.gatlytron.reporting.GatlytronDataRecord;

public class GatlytronDBInterface {
	
	private DBInterface db;
	
	public final String tablenamePrefix;
	public final String tablenameMain;
	public final String tablenameTestsettings;
	
	private String createTableSQLMain;
	private String createTableSQLTestSettings;
	
	/************************************************************************
	 * 
	 * @param db
	 * @param tablenamePrefix
	 ************************************************************************/
	public GatlytronDBInterface(DBInterface db, String tablenamePrefix) {
		
		this.db = db;
		this.tablenamePrefix = tablenamePrefix;
		this.tablenameMain = tablenamePrefix;
		this.tablenameTestsettings = tablenamePrefix+"_testsettings";

		createTableSQLMain = GatlytronDataRecord.getSQLCreateTableTemplate(tablenameMain);
		createTableSQLTestSettings = GatlytronScenario.getSQLCreateTableTemplate(tablenameTestsettings);
	}
	
	/****************************************************************************
	 * Create the Gatlytron tables in the database
	 ****************************************************************************/
	public void createTables() {
		
		if(db == null) { return; }
		
		db.preparedExecute(createTableSQLMain);
		db.preparedExecute(createTableSQLTestSettings);
		
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	public void reportRecords(ArrayList<GatlytronDataRecord> records) {
		
		for(GatlytronDataRecord record : records ) {
			record.insertIntoDatabase(db, tablenameMain);
		}

	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	public void reportTestSettings(String simulationName) {
		
		ArrayList<GatlytronScenario> scenarioList = Gatlytron.getScenarioList();
		
		for(GatlytronScenario scenario : scenarioList ) {
			scenario.insertIntoDatabase(db, tablenameTestsettings, simulationName);
		}
	}
	
	
	//###########################################################################################
	// GETTERS & SETTERS
	//###########################################################################################

	public String getCreateTableSQLMain() {
		return createTableSQLMain;
	}

	public void setCreateTableSQLMain(String createTableSQLMain) {
		this.createTableSQLMain = createTableSQLMain;
	}

	public String getCreateTableSQLTestSettings() {
		return createTableSQLTestSettings;
	}

	public void setCreateTableSQLTestSettings(String createTableSQLTestSettings) {
		this.createTableSQLTestSettings = createTableSQLTestSettings;
	}
	
	
	
	
	
	
	

}
