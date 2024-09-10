
# Gatlytron

Gatlytron is a little framework that assists you in creating Gatling tests and provides some additional features like reporting.

# Creating and Running Tests
Example code that uses gatling can be found in this repository under `src/test/java/` in the package `com.performetriks.gatlytron.test`.

To create a Scenario, create a class that extends `GatlytronScenario.java`:

``` java
public class SampleScenario extends GatlytronScenario {
  
    public SampleScenario() {
        super("MyScenario");
      
        this.debug(TestGlobals.DEBUG)
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



## Enable Graphite Receiver and Reporting

To enable it, add `Gatlytron.enableGraphiteReceiver(<port>);` and `Gatlytron.addReporter(new GatlytronReporter*());` some to your simulation, for example:

```java
{ 	
	Gatlytron.enableGraphiteReceiver(2003);
	Gatlytron.addReporter(new GatlytronReporterJsonFile("./target/gatlytron.json"));
   Gatlytron.addReporter(new GatlytronReporterCSV("./target/gatlytron.csv", ";"));
   Gatlytron.addReporter(new GatlytronReporterSysout());
    	
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
For this to work you will need to include the respective driver dependency. Following an example for PostGres SQL:

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