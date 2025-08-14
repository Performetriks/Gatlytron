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
import com.performetriks.gatlytron.utils.GatlytronTime.CFWTimeUnit;

public class GatlytronDBInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(GatlytronDBInterface.class);
	
	private DBInterface db;
	
	public final String tablenamePrefix;
	public final String tablenameStats;
	public final String tablenameTestsettings;
	
	private String sqlCreateTableStats;
	private String sqlCreateTableTestSettings;
	
	private static final String PACKAGE_RESOURCES = "com.performetriks.gatlytron.database.resources";
	static { GatlytronFiles.addAllowedPackage(PACKAGE_RESOURCES); }
	
	private static final String TEMP_TABLE_AGGREGATION = "TEMP_STATS_AGGREGATION";
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

		sqlCreateTableStats = GatlytronRecordStats.getSQLCreateTableTemplate(tablenameStats);
		sqlCreateTableTestSettings = GatlytronScenario.getSQLCreateTableTemplate(tablenameTestsettings);
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
		String addGranularityColumn = "ALTER TABLE "+tablenameStats+" ADD IF NOT EXISTS granularity INTEGER;";
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
	private Long getOldestAgedRecord(int granularity, Timestamp ageOutTime  ) {

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
	private Long getYoungestAgedRecord(int granularity, Timestamp ageOutTime  ) {

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
				  " SELECT COUNT(*) " + tablenameStats
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

		String sqlCreateTempTable =  sqlCreateTableStats.replaceAll(tablenameStats, TEMP_TABLE_AGGREGATION);

		success &= db.preparedExecute(sqlCreateTempTable);
		

		//--------------------------------------------
		// Aggregate Statistics in Temp Table
		String sqlAggregateTempStats =  GatlytronFiles.readPackageResource(PACKAGE_RESOURCES, "sql_createTempAggregatedStatistics.sql");
		
		// it's ridiculously complicated, but well... 
		// at least the next guy adjusting anything will be able to backtrack the problem
		sqlAggregateTempStats = sqlAggregateTempStats
							.replaceAll("{tempTableName}", TEMP_TABLE_AGGREGATION)
							.replaceAll("{originalTableName}", tablenameStats)
							.replaceAll("{namesWithoutTimeOrGranularity}"
									   , GatlytronRecordStats.fieldNamesJoined
									   						 .replaceAll("\"time\",", "")
									   						 .replaceAll(",\"granularity\"", "")
									   )
							.replaceAll("{groupByNames}", GatlytronRecordStats.fieldNamesJoined)
							.replaceAll("{valuesAggregation}", RecordMetric.getSQLAggregationPart())
							;
		
		System.out.println(sqlAggregateTempStats);
		success &= db.preparedExecute(
						  sqlAggregateTempStats
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
				"INSERT INTO" + tablenameStats + " " + GatlytronRecordStats.getSQLTableColumnNames()
				+" SELECT * FROM "+TEMP_TABLE_AGGREGATION+";"
				;

		success &= db.preparedExecute(
				  sqlMoveStats
			);

		//--------------------------------------------
		// Drop Temp Table
		String sqlDropTempTable = 
				"DROP TABLE " +TEMP_TABLE_AGGREGATION+";"
				;

		success &= db.preparedExecute(sqlDropTempTable );
		

		
		db.transactionEnd(success);
		
		
		return success;
	}
	
	/****************************************************************************
	 * Will age out the statistics stored in the database to reduce
	 * database size.
	 ****************************************************************************/
	public void ageOutStatistics() {
		
		//----------------------------
		// Iterate all granularities
		for(int granularity : GatlytronTime.AGE_OUT_GRANULARITIES) {
			//--------------------------
			// Get Age Out Time
			Timestamp ageOutTime = this.getAgeOutTime(granularity);
			
			//--------------------------
			// Get timespan 
			Long oldest = getOldestAgedRecord(granularity, ageOutTime);
			Long youngest = getYoungestAgedRecord(granularity, ageOutTime);
			if(oldest == null || youngest == null ) {
				//nothing to aggregate for this granularity
				continue;
			}
			
			logger.info("DB: Age Out statistics with granularity smaller than: "+granularity+" seconds");
			//--------------------------
			// Get Start Time
			// Cannot take oldest as start time, as it might offset deep into 
			// the timerange that still should be kept
			Long startTime = GatlytronTime.offsetTime(oldest, 0, 0, 0, 0, +1);
			
			while(startTime > oldest) {
				startTime = GatlytronTime.offsetTime(startTime, 0, 0, 0, 0, -granularity);
			}
			
			//--------------------------
			// Iterate with offsets
			Long endTime = GatlytronTime.offsetTime(startTime, 0, 0, 0, 0, granularity);
			
			// do-while to execute at least once, else would not work if (endTime - startTime) < granularity
			do {
				aggregateStatistics(startTime, endTime, granularity);
				startTime =  GatlytronTime.offsetTime(startTime, 0, 0, 0, 0, granularity);
				endTime = GatlytronTime.offsetTime(endTime, 0, 0, 0, 0, granularity);

			} while(endTime < youngest);

		}
		
	}
	
	
	
	/********************************************************************************************
	 * Get the default age out time of the application.
	 * @return timestamp
	 ********************************************************************************************/
	public Timestamp getAgeOutTime(int granularitySeconds) {
		
		GatlytronAgeOutConfig config = Gatlytron.getAgeOutConfig();
		
		long ageOutOffset;
		
		if		(granularitySeconds <= GatlytronTime.SECONDS_OF_1MIN) 	{ ageOutOffset = CFWTimeUnit.m.offset(null, (int)config.keep1MinFor().get(ChronoUnit.MINUTES) ); }
		else if	(granularitySeconds <= GatlytronTime.SECONDS_OF_5MIN) 	{ ageOutOffset = CFWTimeUnit.m.offset(null, (int)config.keep5MinFor().get(ChronoUnit.MINUTES)); }
		else if (granularitySeconds <= GatlytronTime.SECONDS_OF_10MIN) 	{ ageOutOffset = CFWTimeUnit.m.offset(null, (int)config.keep10MinFor().get(ChronoUnit.MINUTES)); }
		else if (granularitySeconds <= GatlytronTime.SECONDS_OF_15MIN) 	{ ageOutOffset = CFWTimeUnit.m.offset(null, (int)config.keep15MinFor().get(ChronoUnit.MINUTES)); }
		else if (granularitySeconds <= GatlytronTime.SECONDS_OF_60MIN) 	{ ageOutOffset = CFWTimeUnit.m.offset(null, (int)config.keep60MinFor().get(ChronoUnit.MINUTES)); }
		else  														{ ageOutOffset = CFWTimeUnit.m.offset(null, (int)config.keep60MinFor().get(ChronoUnit.MINUTES)); }

		return new Timestamp(ageOutOffset);
	}
	
	
	//###########################################################################################
	// GETTERS & SETTERS
	//###########################################################################################

	public String getCreateTableSQLMain() {
		return sqlCreateTableStats;
	}

	public void setCreateTableSQLMain(String createTableSQLMain) {
		this.sqlCreateTableStats = createTableSQLMain;
	}

	public String getCreateTableSQLTestSettings() {
		return sqlCreateTableTestSettings;
	}

	public void setCreateTableSQLTestSettings(String createTableSQLTestSettings) {
		this.sqlCreateTableTestSettings = createTableSQLTestSettings;
	}
	
	
	
	
	
	
	

}
