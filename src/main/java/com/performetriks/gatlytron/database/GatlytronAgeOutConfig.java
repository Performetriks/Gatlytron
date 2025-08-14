package com.performetriks.gatlytron.database;

import java.time.Duration;

public class GatlytronAgeOutConfig {
	
	private Duration keep1MinFor = Duration.ofDays(90); 		// Default 3 months
	private Duration keep5MinFor = Duration.ofDays(180);		// Default 6 months
	private Duration keep10MinFor = Duration.ofDays(365);		// Default 1 year
	private Duration keep15MinFor = Duration.ofDays(365 * 3);	// Default 3 years
	private Duration keep60MinFor = Duration.ofDays(365 * 20);	// Default 20 years
	


	/***************************************************************************
	 *  Returns the duration for the granularity mentioned in the method name. */
	public Duration keep1MinFor() { return keep1MinFor; }
	
	/***************************************************************************
	 *  Returns the duration for the granularity mentioned in the method name. */
	public Duration keep5MinFor() { return keep5MinFor; }
	
	/***************************************************************************
	 *  Returns the duration for the granularity mentioned in the method name. */
	public Duration keep10MinFor() { return keep10MinFor; }
	
	/***************************************************************************
	 *  Returns the duration for the granularity mentioned in the method name. */
	public Duration keep15MinFor() { return keep15MinFor; }
	
	/***************************************************************************
	 *  Returns the duration for the granularity mentioned in the method name. */
	public Duration keep60MinFor() { return keep60MinFor; }
	
	/*************************************************************
	 * Sets the Duration the granularity mentioned in the method
	 * name should be kept. All granularities lower than that
	 * granularity will be kept too.
	 * 
	 * @param how long the Granularity should be kept for.
	 *************************************************************/
	public GatlytronAgeOutConfig keep1MinFor(Duration duration) {
		keep1MinFor = duration;
		return this;
	}
	
	/*************************************************************
	 * Sets the Duration the granularity mentioned in the method
	 * name should be kept. All granularities lower than that
	 * granularity will be kept too.
	 * 
	 * @param how long the Granularity should be kept for
	 *************************************************************/
	public GatlytronAgeOutConfig keep5MinFor(Duration duration) {
		keep5MinFor = duration;
		return this;
	}
	
	/*************************************************************
	 * Sets the Duration the granularity mentioned in the method
	 * name should be kept. All granularities lower than that
	 * granularity will be kept too.
	 * 
	 * @param how long the Granularity should be kept for
	 *************************************************************/
	public GatlytronAgeOutConfig keep10MinFor(Duration duration) {
		keep10MinFor = duration;
		return this;
	}
	
	/*************************************************************
	 * Sets the Duration the granularity mentioned in the method
	 * name should be kept. All granularities lower than that
	 * granularity will be kept too.
	 * 
	 * @param how long the Granularity should be kept for
	 *************************************************************/
	public GatlytronAgeOutConfig keep15MinFor(Duration duration) {
		keep15MinFor = duration;
		return this;
	}
	
	/*************************************************************
	 * Sets the Duration the granularity mentioned in the method
	 * name should be kept. All granularities lower than that
	 * granularity will be kept too.
	 * 
	 * @param how long the Granularity should be kept for
	 *************************************************************/
	public GatlytronAgeOutConfig keep60MinFor(Duration duration) {
		keep60MinFor = duration;
		return this;
	}
	

}
