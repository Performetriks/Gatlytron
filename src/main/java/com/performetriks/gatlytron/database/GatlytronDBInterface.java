package com.performetriks.gatlytron.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.performetriks.gatlytron.base.Gatlytron;
import com.performetriks.gatlytron.base.GatlytronScenario;
import com.performetriks.gatlytron.stats.GatlytronRecordRaw;
import com.performetriks.gatlytron.stats.GatlytronRecordStats;
import com.performetriks.gatlytron.stats.GatlytronRecordStats.RecordMetric;
import com.performetriks.gatlytron.utils.GatlytronFiles;
import com.performetriks.gatlytron.utils.GatlytronTime;
import com.performetriks.gatlytron.utils.GatlytronTime.GatlytronTimeUnit;

public class GatlytronDBInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(GatlytronDBInterface.class);
	
	private DBInterface db;
	
	public final String tablenamePrefix;
	public final String tablenameStats;
	public final String tablenameTestsettings;
	public final String tablenameTempAggregation;
	
	private String sqlCreateTableStats;
	private String sqlCreateTableTestSettings;
	private String sqlAggregateStats;
	
	public static final String PACKAGE_RESOURCES = "com.performetriks.gatlytron.database.resources";
	static { GatlytronFiles.addAllowedPackage(PACKAGE_RESOURCES); }
	

	private static final String PROCEDURE_AGGREGATE_PERC = "AGGREGATE_PERC";
	
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
		this.tablenameTempAggregation = tablenamePrefix+"_temp_aggregation";

		this.setCreateTableSQLStats( GatlytronRecordStats.getSQLCreateTableTemplate(tablenameStats) );
		this.setCreateTableSQLTestSettings( GatlytronScenario.getSQLCreateTableTemplate(tablenameTestsettings) );
		this.setAggregateSQL( GatlytronRecordStats.createAggregationSQL(tablenameStats, tablenameTempAggregation) );
	}
	
	/****************************************************************************
	 * Create the Gatlytron tables in the database
	 ****************************************************************************/
	public void initializeDB() {
		
		if(db == null) { return; }
		
		//---------------------------
		// CREATE TABLES
		db.preparedExecute(sqlCreateTableStats);
		db.preparedExecute(sqlCreateTableTestSettings);
		
		//---------------------------
		// ALLTER TABLES
		alterTables();
		
		//---------------------------
		// CREATE PROCEDURE
//		try {
//			db.preparedExecute("DROP PROCEDURE "+PROCEDURE_AGGREGATE_PERC);
//			
//		}catch(Throwable e) {
//			/* Do nothing */
//		}
		
		//String createProcedure =  GatlytronFiles.readPackageResource(PACKAGE_RESOURCES, "createProcedureAggregatePerc.sql");

		//db.preparedExecute(createProcedure);
		
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
		// Add granularity Column
		String addGranularityColumn = "ALTER TABLE "+tablenameStats+" ADD IF NOT EXISTS granularity INTEGER DEFAULT 0;";
		db.preparedExecute(addGranularityColumn);
		
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
	
	/***************************************************************
	 * Get the timestamp of the oldest record that has a ganularity lower
	 * than the one specified by the parameter.
	 * @param granularity
	 * @return timestamp
	 ****************************************************************/
	private Long getOldestAgedRecord(int granularity, long ageOutTime  ) {

		String sql = 
				  " SELECT time FROM " + tablenameStats
				+ " WHERE granularity < ?"
				+ " AND time <= ?"
				+ " ORDER BY time"
				+ " LIMIT 1";
		
		ResultSet result = db.preparedExecuteQuery(sql, granularity, ageOutTime);
		
		return new ResultSetConverter(db, result).getFirstAsLong();
		
	}
	
	/***************************************************************
	 * Get the timestamp of the oldest record that has a ganularity lower
	 * than the one specified by the parameter.
	 * @param granularity
	 * @return timestamp
	 ****************************************************************/
	private Long getYoungestAgedRecord(int granularity, long ageOutTime  ) {

		String sql = 
				  " SELECT time FROM " + tablenameStats
				+ " WHERE granularity < ?"
				+ " AND time <= ?"
				+ " ORDER BY time DESC"
				+ " LIMIT 1";
		
		ResultSet result = db.preparedExecuteQuery(sql, granularity, ageOutTime);
		
		return new ResultSetConverter(db, result).getFirstAsLong();
		
	}

	
	/***************************************************************
	 * Aggregates the statistics in the given timeframe.
	 * 
	 * @return true if successful, false otherwise
	 ****************************************************************/
	private boolean aggregateStatistics(Long startTime, Long endTime, int newGranularity) {
				
		db.transactionStart();
		boolean success = true;
		int cacheCounter = 0;
		
		//--------------------------------------------
		// Check if there is anything to aggregate
		
		String sql = 
				  " SELECT COUNT(*) FROM " + tablenameStats
				  + " WHERE time >= ?"
				  + " AND time < ?" 
				  + " AND granularity < ?;"
				  ;
		
		ResultSet result = db.preparedExecuteQuery(sql, startTime, endTime, newGranularity);
		
		int count =  new ResultSetConverter(db, result).getFirstAsCount();

		if(count == 0) {
			db.transactionRollback();
			return true;
		}
		
		//--------------------------------------------
		// Create Temp Table
		String createTempTable = 
				GatlytronRecordStats.getSQLCreateTableTemplate(tablenameTempAggregation);

		db.preparedExecute(createTempTable);
		
		//--------------------------------------------
		// Aggregate Statistics in Temp Table
		
		success &= db.preparedExecute(
						  sqlAggregateStats
						, newGranularity
						, startTime
						, endTime
						, newGranularity
					);
		

		//--------------------------------------------
		// Delete Old Stats in stats table
		String sqlDeleteOldStats = 
						"DELETE FROM " + tablenameStats
						+ " WHERE time >= ?"
						+ " AND time < ?"
						+ " AND granularity < ?;"
						;
		
		success &= db.preparedExecute(
						  sqlDeleteOldStats		
						, startTime
						, endTime
						, newGranularity
					);


		//--------------------------------------------
		// Move Temp Stats to EAVTable
		String sqlMoveStats = 
				"INSERT INTO " + tablenameStats + " " + GatlytronRecordStats.getSQLTableColumnNames()
				+" SELECT * FROM "+tablenameTempAggregation+";"
				;

		success &= db.preparedExecute(
				  sqlMoveStats
			);

		//--------------------------------------------
		// Drop Temp Table
		String sqlDropTempTable = 
				"DROP TABLE " +tablenameTempAggregation+";"
				;

		// success &= db.preparedExecute(sqlDropTempTable); // results in count 0
		db.preparedExecute(sqlDropTempTable);

		db.transactionEnd(success);
		
		logger.debug(">>> AgeOut Success: "+success+" for "+GatlytronTime.formatMillisAsTimestamp(startTime) + " to "+ GatlytronTime.formatMillisAsTimestamp(endTime));
		
		
		return success;
	}
	
	/****************************************************************************
	 * Will age out the statistics stored in the database to reduce
	 * database size.
	 ****************************************************************************/
	public void ageOutStatistics() {
		
		//----------------------------
		// Iterate all granularities
		for(int granularitySec : GatlytronTime.AGE_OUT_GRANULARITIES) {
			//--------------------------
			// Get Age Out Time
			long ageOutTime = this.getAgeOutTime(granularitySec);
			
			//--------------------------
			// Get timespan 
			Long oldest = getOldestAgedRecord(granularitySec, ageOutTime);
			Long youngest = getYoungestAgedRecord(granularitySec, ageOutTime);
			if(oldest == null || youngest == null ) {
				//nothing to aggregate for this granularity
				continue;
			}
			
			logger.info("DB: Age Out statistics with granularity smaller than: "+granularitySec+" seconds");
			logger.info(">>> Age Out earliest time: "+GatlytronTime.formatMillisAsTimestamp(oldest));
			logger.info(">>> Age Out latest time: "+GatlytronTime.formatMillisAsTimestamp(youngest));


			//--------------------------
			// Get Start Time
			// Cannot take oldest as start time, as it might offset deep into 
			// the timerange that still should be kept
			Long startTime = GatlytronTimeUnit.s.offset(oldest, +1);
			
			while(startTime > oldest) {
				startTime = GatlytronTimeUnit.s.offset(startTime, -granularitySec);
			}
			
			//--------------------------
			// Iterate with offsets
			Long endTime =  GatlytronTimeUnit.s.offset(startTime, granularitySec);
			
			// do-while to execute at least once, else would not work if (endTime - startTime) < granularity
			do {

				aggregateStatistics(startTime, endTime, granularitySec);
				startTime =  GatlytronTimeUnit.s.offset(startTime, granularitySec);
				endTime = GatlytronTimeUnit.s.offset(endTime, granularitySec);

			} while(endTime < youngest);

		}
		
	}
	
	
	
	/********************************************************************************************
	 * Get the default age out time.
	 * @return timestamp
	 ********************************************************************************************/
	public long  getAgeOutTime(int granularitySeconds) {
		
		GatlytronAgeOutConfig config = Gatlytron.getAgeOutConfig();
		
		long ageOutOffset;
		
		if		(granularitySeconds <= GatlytronTime.SECONDS_OF_1MIN) 	{ ageOutOffset = GatlytronTimeUnit.s.offset(null, -1 * (int)config.keep1MinFor().get(ChronoUnit.SECONDS) ); }
		else if	(granularitySeconds <= GatlytronTime.SECONDS_OF_5MIN) 	{ ageOutOffset = GatlytronTimeUnit.s.offset(null, -1 * (int)config.keep5MinFor().get(ChronoUnit.SECONDS)); }
		else if (granularitySeconds <= GatlytronTime.SECONDS_OF_10MIN) 	{ ageOutOffset = GatlytronTimeUnit.s.offset(null, -1 * (int)config.keep10MinFor().get(ChronoUnit.SECONDS)); }
		else if (granularitySeconds <= GatlytronTime.SECONDS_OF_15MIN) 	{ ageOutOffset = GatlytronTimeUnit.s.offset(null, -1 * (int)config.keep15MinFor().get(ChronoUnit.SECONDS)); }
		else if (granularitySeconds <= GatlytronTime.SECONDS_OF_60MIN) 	{ ageOutOffset = GatlytronTimeUnit.s.offset(null, -1 * (int)config.keep60MinFor().get(ChronoUnit.SECONDS)); }
		else  															{ ageOutOffset = GatlytronTimeUnit.s.offset(null, -1 * (int)config.keep60MinFor().get(ChronoUnit.SECONDS)); }

		return ageOutOffset;
	}
	
	
	//###########################################################################################
	// GETTERS & SETTERS
	//###########################################################################################

	public String getCreateTableSQLStats() {
		return sqlCreateTableStats;
	}

	public void setCreateTableSQLStats(String statsSQL) {
		this.sqlCreateTableStats = statsSQL;
	}

	public String getCreateTableSQLTestSettings() {
		return sqlCreateTableTestSettings;
	}

	public void setCreateTableSQLTestSettings(String createTableSQLTestSettings) {
		this.sqlCreateTableTestSettings = createTableSQLTestSettings;
	}
	
	public String getAggregateSQL() {
		return sqlAggregateStats;
	}
	
	public void setAggregateSQL(String aggregateSQL) {
		this.sqlAggregateStats = aggregateSQL;
	}
	
	
	
	
	
	

}
