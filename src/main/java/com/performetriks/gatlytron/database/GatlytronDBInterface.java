package com.performetriks.gatlytron.database;

import java.util.ArrayList;

import com.performetriks.gatlytron.base.Gatlytron;
import com.performetriks.gatlytron.base.GatlytronScenario;
import com.performetriks.gatlytron.stats.GatlytronRecordStats;

public class GatlytronDBInterface {
	
	private DBInterface db;
	
	public final String tablenamePrefix;
	public final String tablenameStats;
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
		this.tablenameStats = tablenamePrefix+"_stats";
		this.tablenameTestsettings = tablenamePrefix+"_testsettings";

		createTableSQLMain = GatlytronRecordStats.getSQLCreateTableTemplate(tablenameStats);
		createTableSQLTestSettings = GatlytronScenario.getSQLCreateTableTemplate(tablenameTestsettings);
	}
	
	/****************************************************************************
	 * Create the Gatlytron tables in the database
	 ****************************************************************************/
	public void createTables() {
		
		if(db == null) { return; }
		
		db.preparedExecute(createTableSQLMain);
		db.preparedExecute(createTableSQLTestSettings);
		
		alterTables();
	}

	/****************************************************************************
	 * 
	 ****************************************************************************/
	private void alterTables() {
		//----------------------------
		// Add P25 Column
		String addOkP25Column = "ALTER TABLE "+tablenameStats+" ADD IF NOT EXISTS ok_p25 DECIMAL(32,3);";
		db.preparedExecute(addOkP25Column);
		
		String addKoP25Column = "ALTER TABLE "+tablenameStats+" ADD IF NOT EXISTS ko_p25 DECIMAL(32,3);";
		db.preparedExecute(addKoP25Column);
		
		//----------------------------
		// Add endTime to testsettings
		String endtime = "ALTER TABLE "+tablenameTestsettings+" ADD IF NOT EXISTS endtime BIGINT;";
		db.preparedExecute(endtime);
	}
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	public void reportRecords(ArrayList<GatlytronRecordStats> records) {
		
		for(GatlytronRecordStats record : records ) {
			record.insertIntoDatabase(db, tablenameStats);
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
	
	/****************************************************************************
	 * 
	 ****************************************************************************/
	public void reportTestSettingsEndTime() {
		
		long endTime = System.currentTimeMillis();
		
		String sqlUpdateTime = "UPDATE "+tablenameTestsettings
				+ " SET endtime = "+endTime
				+ " WHERE execid = '"+Gatlytron.EXECUTION_ID+"'";
		
		db.preparedExecute(sqlUpdateTime);
		
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
