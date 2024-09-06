
# Gatlytron

Gatlytron is a little framework that assists you in creating 

# Running Specific simulations
To run a specific simulation define the parameter "-Dgatling.simulationClass":

```
mvn compile gatling:test -Dgatling.simulationClass=com.performetriks.gatlytron.test.simulation.SimulationCheckDebug
```

# Enable Graphite Receiver
Gatlytron comes with an built-in graphite receiver, which is used to get the real time data and send it to a data store or otherwise process it.

To enable it, add `Gatlytron.enableGraphiteReceiver(<port>);` to your Simulation, for example:

```
{ 	
	Gatlytron.enableGraphiteReceiver(2003);

	setUp(
			new SampleScenario().buildStandardLoad(10, 600, 0, 2)
	   ).protocols(TestSettings.getProtocol())
		.maxDuration(TEST_DURATION)
	   ;
    	
}

```

Also you have to enable graphite in your gatling.conf by adding "graphite" to the writers and uncomment the graphite section:

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
      writePeriod = 5                       
    }
  }
  ...
}
```