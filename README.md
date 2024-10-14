
# Gatlytron

Gatlytron is a little framework that assists you in creating Gatling tests and provides some additional features like reporting.

# Creating and Running Tests
Example code that uses gatling can be found in this repository under `src/test/java/` in the package `com.performetriks.gatlytron.test`.

To create a Scenario, create a class that extends `GatlytronScenario.java`:

``` java
public class SampleScenario extends GatlytronScenario {
  
    public SampleScenario() {
        super("MyScenario");
      
        this
        	  //.debug(TestGlobals.DEBUG) // default obtained from Gatlytron.isDebu();
            .feederBuilder(TestGlobals.getDataFeeder())
            .scenarioSteps(
                exec(
					http("callURL").get("")
                )
        );
    }
}
```

In your simulation, you can add your test scenarios using the `.build*()` methods.
Using these methods you can easily create Standard load test with proper load distribution, just run a scenario once or just get a plain scenario and add your `inject*()` definition yourself:

``` java

public class SimulationExample extends Simulation {
 
    private static final Duration TEST_DURATION = Duration.ofSeconds(15);

    {
    	setUp(
                new SampleScenario().buildStandardLoad(10, 600, 0, 2)
              , new SampleScenario().buildRunOnce()
              , new SampleScenario().buildScenario(0).injectOpen(...)
           ).protocols(TestGlobals.getProtocol())
            .maxDuration(TEST_DURATION)
           ;

    }
    
    @Override
    public void after() {
		Gatlytron.terminate();
    }
}
```




## Running Specific Simulations
If you define multiple simulations in your project, to run a specific simulation define the parameter "-Dgatling.simulationClass":

```
mvn compile gatling:test -Dgatling.simulationClass=com.performetriks.gatlytron.test.simulation.SimulationCheckDebug
```

## Logging
Gatlytron provides some methods to set log levels for logback in code. This helps keeping all the config in one place instead of having it distributed in code and config files.

``` java
Gatlytron.setDebug(false); // common debug flag, can be accessed with Gatlytron.isDebug()
Gatlytron.setLogLevelRoot(Level.INFO);
Gatlytron.setLogLevel(Level.INFO, "com.performetriks.gatlytron");
```

# Reporting
Gatlytron comes with an built-in carbon(graphite) receiver, which is used to get the real time data and send it to a data store or otherwise process it through GatlytronReporter(feel free to extend this one yourself).

To increase performance, the reporting feature will take the carbon metrics and aggregate them into records with multiple metrics. The fields available in the reported data are as follows:

* **time:** Time in epoch seconds.
* **simulation:** Name of the simulation in lower case.
* **request:** The name of the request, or null if the record relates to user statistics.
* **user_group:** The name of the user group, or null if the record relates to a request.
* **users_active, users_waiting, users_done:** Fields that contain user count values.

* **ok_count, ok_min, ok_max, ok_mean, ok_stdev, ok_p50, ok_p75, ok_p95, ok_p99:** Fields that countain the metrics for request in status "ok".
* **ko_count, ko_min, ko_max, ko_mean, ko_stdev, ko_p50, ko_p75, ko_p95, ko_p99:** Fields that countain the metrics for request in status "ko".
* **all_count, all_min, all_max, all_mean, all_stdev, all_p50, all_p75, all_p95, all_p99:** Fields that countain the metrics for all requests (ok and ko combined).

Here is a JSON example of three records. the first two are user statistic records, the next two are request records:

``` json
{"time":"1725964648","simulation":"simulationcheckdebug","request":null,"user_group":"myScenario","users_active":2,"users_waiting":0,"users_done":0,"ok_count":0,"ok_min":0,"ok_max":0,"ok_mean":0,"ok_stdev":0,"ok_p50":0,"ok_p75":0,"ok_p95":0,"ok_p99":0,"ko_count":0,"ko_min":0,"ko_max":0,"ko_mean":0,"ko_stdev":0,"ko_p50":0,"ko_p75":0,"ko_p95":0,"ko_p99":0,"all_count":0,"all_min":0,"all_max":0,"all_mean":0,"all_stdev":0,"all_p50":0,"all_p75":0,"all_p95":0,"all_p99":0}
{"time":"1725964648","simulation":"simulationcheckdebug","request":null,"user_group":"allUsers","users_active":2,"users_waiting":0,"users_done":0,"ok_count":0,"ok_min":0,"ok_max":0,"ok_mean":0,"ok_stdev":0,"ok_p50":0,"ok_p75":0,"ok_p95":0,"ok_p99":0,"ko_count":0,"ko_min":0,"ko_max":0,"ko_mean":0,"ko_stdev":0,"ko_p50":0,"ko_p75":0,"ko_p95":0,"ko_p99":0,"all_count":0,"all_min":0,"all_max":0,"all_mean":0,"all_stdev":0,"all_p50":0,"all_p75":0,"all_p95":0,"all_p99":0}
{"time":"1725964648","simulation":"simulationcheckdebug","request":"myRequest","user_group":null,"users_active":0,"users_waiting":0,"users_done":0,"ok_count":4,"ok_min":338,"ok_max":373,"ok_mean":357,"ok_stdev":14,"ok_p50":347,"ok_p75":369,"ok_p95":373,"ok_p99":373,"ko_count":0,"ko_min":0,"ko_max":0,"ko_mean":0,"ko_stdev":0,"ko_p50":0,"ko_p75":0,"ko_p95":0,"ko_p99":0,"all_count":4,"all_min":338,"all_max":373,"all_mean":357,"all_stdev":14,"all_p50":347,"all_p75":369,"all_p95":373,"all_p99":373}
{"time":"1725965555","simulation":"simulationcheckdebug","request":"myFailingRequest","user_group":null,"users_active":0,"users_waiting":0,"users_done":0,"ok_count":0,"ok_min":0,"ok_max":0,"ok_mean":0,"ok_stdev":0,"ok_p50":0,"ok_p75":0,"ok_p95":0,"ok_p99":0,"ko_count":4,"ko_min":119,"ko_max":179,"ko_mean":150,"ko_stdev":26,"ko_p50":130,"ko_p75":173,"ko_p95":179,"ko_p99":179,"all_count":4,"all_min":119,"all_max":179,"all_mean":150,"all_stdev":26,"all_p50":130,"all_p75":173,"all_p95":179,"all_p99":179}


```


## Enable Graphite Receiver and Reporting

To enable it, add `Gatlytron.enableGraphiteReceiver(<port>);` and `Gatlytron.addReporter(new GatlytronReporter*());` some to your simulation, for example:

```java
{ 	
	Gatlytron.enableGraphiteReceiver(2003);
	//Gatlytron.setKeepEmptyRecords(false); 
	Gatlytron.addReporter(new GatlytronReporterJson("./target/gatlytron.json", true));
   Gatlytron.addReporter(new GatlytronReporterCSV("./target/gatlytron.csv", ";"));
   Gatlytron.addReporter(new GatlytronReporterSysoutJson());
    	
	setUp(
			new SampleScenario().buildStandardLoad(10, 600, 0, 2)
	   ).protocols(TestSettings.getProtocol())
		.maxDuration(TEST_DURATION)
	   ;
    	
}

```

Also you have to enable graphite in your gatling.conf by adding "graphite" to the writers and uncomment the graphite section. It is also highly recommended to set the writePeriod to 15 or higher to decrease performance impact of writing data:

```
gatling {
  ...
  data {
    writers = [console, file, graphite]     
    graphite {
      host = "localhost"                    
      port = 2003                           
      protocol = "tcp"                      
      rootPathPrefix = "gatling"           
      bufferSize = 8192                     
      writePeriod = 15                       
    }
  }
  ...
}
```

## Reporting to Databases

There are Gatlytron reporters which allow you to report metrics to a database.
For this to work you will need to include the respective driver dependency. 

### Tables
There will be two tables created in the database:
* **{tableName}:** The base table containing all the metrics. (fields are explained above under section "Reporting")
* **{tableName}_testsettings:** This table will contain the settings for the scenarios that have been executed:
	* **time:** Time in epoch seconds.
	* **execID:** A unique id for the test execution.
	* **simulation:** The name of the simulation.
	* **scenario:** The name of the scenario.
	* **users:** The target number of users for the scenario.
	* **execsHour:** The target number of executions per hour for the scenario.
	* **startOffset:** The start offset in seconds for the scenario.
	* **rampUp:**  The number of users to ramp up per interval.
	* **rampUpInterval:** The ramp up interval in seconds.
	* **pacingSeconds:** The pacing of the use case in seconds. 
	

### Reporting to Postgres
Following an example for PostGres SQL:

```xml
<!-- https://mvnrepository.com/artifact/org.postgresql/postgresql -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.4</version>
    <scope>provided</scope>
</dependency>
```

Now you can add the Reporter for the database. The reporter will create the table with the given name if not exists:

```java
{ 	
	Gatlytron.enableGraphiteReceiver(2003);
	Gatlytron.addReporter(
    			new GatlytronReporterDatabasePostGres(
	    			"localhost"
	    			, 5432
	    			, "postgres"  // database name
	    			, "gatlytron" // table name
	    			, "dbuser"
	    			, "dbpassowrd"  
    			)
    		);
    	
	setUp(
			new SampleScenario().buildStandardLoad(10, 600, 0, 2)
	   ).protocols(TestSettings.getProtocol())
		.maxDuration(TEST_DURATION)
	   ;
    	
}

```

### Reporting to any JDBC Database
You can use the class `GatlytronReporterDatabaseJDBC` to connect to any SQL database that is availabie through JDBC.
You need to include the dependency which provides the driver for the database you want to connect to.
Following an example for an H2 database:

``` xml
<!-- https://mvnrepository.com/artifact/com.h2database/h2 -->
<!dependency>
	<groupId>com.h2database</groupId>
	<artifactId>h2</artifactId>
	<version>2.2.224</version> 
</dependency>
```

Now you can add the Reporter for the database.
You will need to implement the method `getCreateTableSQL()` and may or may not adjust the SQL to create the table:

```java
//------------------------------
// JDBC DB Reporter
Gatlytron.addReporter(
		new GatlytronReporterDatabaseJDBC("org.h2.Driver"
				, "jdbc:h2:tcp://localhost:8889/./datastore/h2database;MODE=MYSQL;IGNORECASE=TRUE"
				, REPORTING_TABLE_NAME
				, "sa"
				, "sa") {
			
			@Override
			public String getCreateTableSQL() {
				return GatlytronCarbonRecord.getSQLCreateTableTemplate(REPORTING_TABLE_NAME);
			}
		}
	);
```

## Setup EMP Dashboards
Gatlytron provides templates for EMP dashboards. 
If you want to use EMP to show your Gatling simulation data, here is how:

1. Download the latest release of EMP: https://github.com/xresch/EngineeredMonitoringPlatform/releases
2. Setup Totorial for EMP: https://www.youtube.com/watch?v=0Ug1daCedfs

**EMP:** 
For showing data sent to EMP, just import the template dashboard: https://github.com/Performetriks/Gatlytron/tree/main/docs/templates
	
**Postgres:**
1. In EMP, go to "Admin >> Context Settings >> Add >> Postgres Environment" and fill in the connection details.
2. Import Gatlytron Dashboard template for Postgres: https://github.com/Performetriks/Gatlytron/tree/main/docs/templates
3. On the Postgres Dashboard:
  	- Open the Dashboard
  	- Click the "Edit" button in the top left
  	- Click on the button "Params"
  	- Change "ID" of 3 parameters:
  		- **database_id:** Select the context setting from the dropdown.
  		- **'simulation' and 'request':** In the queries remove existing "environment={...}" and use Ctrl+Space for autocomplete and inserting the new one which you have created.
