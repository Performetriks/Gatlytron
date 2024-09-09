
# Gatlytron

Gatlytron is a little framework that assists you in creating Gatling tests and provides some additional features like reporting.
Example code that uses gatling can be found in this repository under `src/test/java/` in the package `com.performetriks.gatlytron.test`.


# Running Specific Simulations
To run a specific simulation define the parameter "-Dgatling.simulationClass":

```
mvn compile gatling:test -Dgatling.simulationClass=com.performetriks.gatlytron.test.simulation.SimulationCheckDebug
```

# Enable Graphite Receiver and Reporting
Gatlytron comes with an built-in graphite receiver, which is used to get the real time data and send it to a data store or otherwise process it through GatlytronReporter(feel free to extend this one yourself).

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

# Reporting to Databases
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