package com.performetriks.gatlytron.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**************************************************************************************************************
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 **************************************************************************************************************/
public class GatlytronTime {
	
	public static final String FORMAT_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSS"; // IMPORTANT: DO NOT CHANGE THIS
	public static final String FORMAT_TIMESTAMP_READABLE = "yyyy-MM-dd HH:mm"; // IMPORTANT: DO NOT CHANGE THIS
	public static final String FORMAT_ISO8601_DATE = "yyyy-MM-dd";
	
	private static TimeZone machineTimezone = TimeZone.getDefault();
			
	private static DateTimeFormatter formatterTimestamp = DateTimeFormatter.ofPattern(GatlytronTime.FORMAT_TIMESTAMP).withZone(machineTimezone.toZoneId());
	private static DateTimeFormatter formatterISODate = DateTimeFormatter.ofPattern(GatlytronTime.FORMAT_ISO8601_DATE).withZone(machineTimezone.toZoneId());
	
	public static final int SECONDS_OF_1MIN = 60;
	public static final int SECONDS_OF_5MIN = 60 * 5;
	public static final int SECONDS_OF_10MIN = 60 * 10;
	public static final int SECONDS_OF_15MIN = 60 * 15;
	public static final int SECONDS_OF_60MIN = 60 * 60;

	public static final int[] AGE_OUT_GRANULARITIES = new int[] {
			  SECONDS_OF_1MIN
			, SECONDS_OF_5MIN
			, SECONDS_OF_10MIN
			, SECONDS_OF_15MIN
			, SECONDS_OF_60MIN
			};

	/********************************************************************************************
	 * The mother of all time enumerations! :-P
	 ********************************************************************************************/
	public enum GatlytronTimeUnit {
		//  ns("nanosecond", 	TimeUnit.NANOSECONDS, 	ChronoUnit.NANOS, 	null)
		//, us("microsecond", TimeUnit.MICROSECONDS, 	ChronoUnit.MICROS, 	null)
		 ms("milliseconds", TimeUnit.MILLISECONDS, 	ChronoUnit.MILLIS, 	Calendar.MILLISECOND)
		, s("seconds", 		TimeUnit.SECONDS, 		ChronoUnit.SECONDS, Calendar.SECOND)
		, m("minutes", 		TimeUnit.MINUTES,		ChronoUnit.MINUTES, Calendar.MINUTE)
		, h("hours", 		TimeUnit.HOURS, 		ChronoUnit.HOURS, 	Calendar.HOUR_OF_DAY)
		, d("days", 		TimeUnit.DAYS, 			ChronoUnit.DAYS, 	Calendar.DAY_OF_YEAR)
		, M("months", 		null, 					ChronoUnit.MONTHS, 	Calendar.MONTH)
		, y("years", 		null, 					ChronoUnit.YEARS, 	Calendar.YEAR)
		;
		
		//==============================
		// Caches
		private static TreeSet<String> enumNames = null;
		private static String optionsString = null;
		private static String optionsHTMLList = null;
		
		//==============================
		// Fields
		private String longName;
		private TimeUnit timeUnit;
		private ChronoUnit chronoUnit;
		private Integer calendarUnit;
		
		private GatlytronTimeUnit(String longName, TimeUnit timeUnit, ChronoUnit chronoUnit,  Integer calendarUnit) {
			this.longName = longName;
			this.timeUnit = timeUnit;
			this.chronoUnit = chronoUnit;
			this.calendarUnit = calendarUnit;
		}
				
		public String longName() { return this.longName; }
		public TimeUnit timeUnit() { return this.timeUnit; }
		public ChronoUnit chronoUnit() { return this.chronoUnit; }
		public Integer calendarUnit() { return this.calendarUnit; }
		
		public long toMillis() { return chronoUnit.getDuration().toMillis(); }
		public long toMillis(int amount) { return amount * chronoUnit.getDuration().toMillis(); }
		public Duration toDuration() { return chronoUnit.getDuration(); }
		
		/********************************************************************************************
		 * Returns the calendar unit lower than the specified unit.
		 * Returns Milliseconds if the unit is milliseconds.
		 * @param calendar which time should be truncated
		 * @return nothing
		 ********************************************************************************************/
		public GatlytronTimeUnit calendarUnitLower() { 
			switch(this) {
				case y:	 return GatlytronTimeUnit.M;
				case M:	 return GatlytronTimeUnit.d;
				case d:	 return GatlytronTimeUnit.h;
				case h:	 return GatlytronTimeUnit.m;
				case m:	 return GatlytronTimeUnit.s;
				case s:	 return GatlytronTimeUnit.ms;
				case ms: return GatlytronTimeUnit.ms;
				default: return GatlytronTimeUnit.ms;
						
			}
		}
		/********************************************************************************************
		 * Returns a set with all names
		 ********************************************************************************************/
		public static TreeSet<String> getNames() {
			if(enumNames == null) {
				enumNames = new TreeSet<>();
				
				for(GatlytronTimeUnit unit : GatlytronTimeUnit.values()) {
					enumNames.add(unit.name());
				}
			}
			return enumNames;
		}
		
		/********************************************************************************************
		 * Returns a set with all names
		 ********************************************************************************************/
		public static String getOptionsString() {
			if(optionsString == null) {
				return String.join(" | ", getNames());
			}
			return optionsString;
		}
		
		/********************************************************************************************
		 * Returns a set with all names
		 ********************************************************************************************/
		public static String getOptionsHTMLList() {
			if(optionsHTMLList == null) {
				optionsHTMLList = "<ul>";
				for(GatlytronTimeUnit unit : GatlytronTimeUnit.values()) {
					optionsHTMLList += "<li><b>"+unit.name()+":&nbsp</b>"+unit.longName()+"</li>";
				}
				optionsHTMLList += "</ul>";
			}
			return optionsHTMLList;
		}
		/********************************************************************************************
		 * Return time with an offset starting from the given time.
		 * Use positive values to go to the future, use negative values to go to the past.
		 * @param epochMillis the time in milliseconds which should be offset.
		 * @param amount to offset for the selected time unit.
		 * @return offset time in epoch milliseconds
		 ********************************************************************************************/
		public static boolean has(String enumName) {
			return getNames().contains(enumName);
		}
		
		/********************************************************************************************
		 * Return time with an offset starting from the given time.
		 * Use positive values to go to the future, use negative values to go to the past.
		 * @param epochMillis the time in milliseconds which should be offset. If null takes current time.
		 * @param amount to offset for the selected time unit.
		 * @return offset time in epoch milliseconds
		 ********************************************************************************************/
		public long offset(Long epochMillis, int amount) { 
			
			if(epochMillis == null) {
				epochMillis = System.currentTimeMillis();
			}
			
			Calendar calendar = Calendar.getInstance();
			
			calendar.setTimeInMillis(epochMillis);
			calendar.add(this.calendarUnit, amount);
			
			return calendar.getTimeInMillis();
		}
		
		/********************************************************************************************
		 * Return time rounded to certain values.
		 * @param epochMillis the time in milliseconds which should be rounded.
		 * @param amount to round to.
		 * @return offset time in epoch milliseconds
		 ********************************************************************************************/
		public long round(long epochMillis, int amount) { 
						
			Calendar calendar = Calendar.getInstance();
			
			calendar.setTimeInMillis(epochMillis);
			
			int unit = this.calendarUnit;
			
			// don't round on days of month, will shift more values to 07th and reduce on 28th
//			if(unit == Calendar.DAY_OF_YEAR) {
//				unit = Calendar.DAY_OF_MONTH;
//			}
			int valueToRound = calendar.get(unit);
			
			
			int modulo = (valueToRound % amount);

			if(modulo != 0) {
				
				// decide to round up or down
				int diff = 0;
				if(modulo < ((float)amount / 2)) {
					diff = modulo*-1;
				}else {
					diff = amount - modulo;
				}
												
				calendar.add(this.calendarUnit, diff);

			}
			truncate(calendar);
			
			return calendar.getTimeInMillis();
		}
		
		/********************************************************************************************
		 * Truncates every time unit which is lower than this time unit.
		 * For Example, if time unit is minute, it will truncate seconds and below.
		 * @param epochMillis the time in milliseconds which should be truncated.
		 * @return truncated time in epoch milliseconds
		 ********************************************************************************************/
		public long truncate(long epochMillis) { 
						
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(epochMillis);
			truncate(calendar);
			
			return calendar.getTimeInMillis();
		}
		
		/********************************************************************************************
		 * Truncates every time unit which is lower than this time unit.
		 * For Example, if time unit is minute, it will truncate seconds and below.
		 * @param calendar which time should be truncated
		 * @return nothing
		 ********************************************************************************************/
		public void truncate(Calendar calendar) { 
			switch(this) {
				case y:	 calendar.set(Calendar.MONTH, 0);
				case M:	 calendar.set(Calendar.DAY_OF_MONTH, 0);
				case d:	 calendar.set(Calendar.HOUR_OF_DAY, 0);
				case h:	 calendar.set(Calendar.MINUTE, 0);
				case m:	 calendar.set(Calendar.SECOND, 0);
				case s:	 calendar.set(Calendar.MILLISECOND, 0);
				default: break;
						
			}
		}
		
		/********************************************************************************************
		 * Return the difference of two times.
		 * The returned time will be the difference of the two times in the selected time unit.
		 * @param earlier the time in milliseconds of the earlier time
		 * @param later the time in milliseconds of the later time
		 * @return truncated time in epoch milliseconds
		 ********************************************************************************************/
		public float difference(Calendar earlier, Calendar later) { 		
			return difference(earlier.getTimeInMillis(), later.getTimeInMillis());
		}
		
		
		/********************************************************************************************
		 * Return the difference of two times.
		 * The returned time will be the difference of the two times in the selected time unit.
		 * @param earlier the time in milliseconds of the earlier time
		 * @param later the time in milliseconds of the later time
		 * @return truncated time in epoch milliseconds
		 ********************************************************************************************/
		public float difference(long earlier, long later) { 
			
			if(earlier == later) {
				return 0;
			}
			
			long diff = later - earlier;

			return convertPrecise(diff);
		}
		
		/********************************************************************************************
		 * Converts the given milliseconds to a representation of the selected time unit.
		 * @param millis the time in milliseconds of the earlier time
		 * @return float value representing milliseconds
		 ********************************************************************************************/
		public float convertPrecise(long millis) { 
			
			return millis / ((float)this.toMillis());
		}
		
		/********************************************************************************************
		 * Converts the given milliseconds to a representation of the selected time unit.
		 * @param millis the time in milliseconds of the earlier time
		 * @return long value representing milliseconds, rounding towards zero will occur
		 ********************************************************************************************/
		public long convert(long millis) { 
			
			return millis / this.toMillis();
		}
		
		
	}
	
	TimeZone timeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
	
	
	/********************************************************************************************
	 * Returns TimeZone for for offset minutes.
	 * 
	 * * @param timezoneOffsetMinutes offset for the clients time zone.
	 * You can use CFWTimeframe with data from browser, or the following 
	 * in javascript to get this value:
	 * 		var timeZoneOffset = new Date().getTimezoneOffset();
	 * 
	 ********************************************************************************************/
	public static TimeZone timezoneFromTimeZoneOffset(int timezoneOffsetMinutes) {
		ZoneOffset offset = zonedOffsetFromTimeZoneOffset(timezoneOffsetMinutes);
		TimeZone timeZone = TimeZone.getTimeZone(offset);
		return timeZone;
	}
	
	/********************************************************************************************
	 * Returns ZoneOffset for offset minutes.
	 * 
	 * * @param timezoneOffsetMinutes offset for the clients time zone.
	 * You can use CFWTimeframe with data from browser, or the following 
	 * in javascript to get this value:
	 * 		var timeZoneOffset = new Date().getTimezoneOffset();
	 ********************************************************************************************/
	public static ZoneOffset zonedOffsetFromTimeZoneOffset(int timezoneOffsetMinutes) {
		return ZoneOffset.ofTotalSeconds(timezoneOffsetMinutes*-60); // multiply by minus as javascript uses opposite values
	}
	
	/********************************************************************************************
	 * Returns a local date from epoch milliseconds with system default timezone.
	 ********************************************************************************************/
	public static ZonedDateTime zonedTimeFromEpoch(long epochMilliseconds, ZoneId zoneID) {
		Instant instant = Instant.ofEpochMilli(epochMilliseconds);
		ZonedDateTime zonedDateTime = instant.atZone(zoneID);
		return zonedDateTime;
		
	}
	
	/********************************************************************************************
	 * Returns a local date from epoch milliseconds with the specified timezoneOffset.
	 ********************************************************************************************/
	public static ZonedDateTime zonedTimeFromEpoch(long epochMilliseconds, int timezoneOffsetMinutes) {
		ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneOffsetMinutes*-60);
		return zonedTimeFromEpoch(epochMilliseconds, offset);
	}
	/********************************************************************************************
	 * Returns a local date from epoch milliseconds with system default timezone.
	 ********************************************************************************************/
	public static ZonedDateTime zonedTimeFromEpoch(long epochMilliseconds) {
		return zonedTimeFromEpoch(epochMilliseconds, machineTimezone.toZoneId());
		
	}
	
	/********************************************************************************************
	 * Returns a local date from epoch milliseconds with time zone UTC.
	 ********************************************************************************************/
	public static ZonedDateTime zonedTimeFromEpochUTC(long epochMilliseconds) {
		return zonedTimeFromEpoch(epochMilliseconds, ZoneOffset.UTC);
	}
	
	/********************************************************************************************
	 * Parses a time string as UTC time and returns epoch milliseconds.
	 * @param formatPattern of SimpleDateFormat, like "yyyy-MM-dd'T'HH:mm:ss.SSS"
	 * @param parseThis the string to parse
	 * 
	 * @throws ParseException 
	 ********************************************************************************************/
	public static long parseTime(String formatPattern, String parseThis) throws ParseException {
		
		// Using SimpleDateFormat because DateTimeFormatter is implemented so that it is hard to 
		// to work with variable patterns
		SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.parse(parseThis).getTime();

	}

	/********************************************************************************************
	 * Replaces timeframe placeholders with earliest as "now - offset" and latest as "now".
	 ********************************************************************************************/
	public static String replaceTimeframePlaceholders(String value, Integer offsetMinutes) {
		
		if( value != null 
		&& ! value.isBlank()
		&& offsetMinutes != null) {
			long latest = System.currentTimeMillis();
			long earliest = latest - (offsetMinutes * 60l * 1000l); 
			value = replaceTimeframePlaceholders(value, earliest, latest, 0);
		}
		
		return value;
		
	}
	
	
	/********************************************************************************************
	 * Replaces timeframe placeholders.
	 * If you are working with CFWField.FormFieldType.TIMEZONEPICKER, here an example on how to get a 
	 * timezone with timezone name:
	 * <pre><code>
	 * //-----------------------------
	 * // Resolve Timezone Offsets
	 * TimeZone timezone;
	 * String timezoneParam = (String)parameters.getField(FIELDNAME_TIMEZONE).getValue();
	 * if(!Strings.isNullOrEmpty(timezoneParam)) {
	 * 	timezone = TimeZone.getTimeZone(timezoneParam);
	 * 	
	 * }else {
	 * 	// default value
	 * }

	 * </code></pre>
	 ********************************************************************************************/
	public static String replaceTimeframePlaceholders(String value, long earliest, long latest, TimeZone timezone) {
		
		earliest +=  timezone.getOffset(earliest);
		latest +=  timezone.getOffset(latest);
		
		return replaceTimeframePlaceholders(
				value
				, earliest
				, latest
				, 0
				);
	}

		
	/********************************************************************************************
	 * Replaces timeframe placeholders in strings.
	 * 
	 * @param timezoneOffsetMinutes offset for the clients time zone.
	 * You can use CFWTimeframe with data from browser, or the following 
	 * in javascript to get this value:
	 * 		var timeZoneOffset = new Date().getTimezoneOffset();
	 * 
	 ********************************************************************************************/
	public static String replaceTimeframePlaceholders(String value, long earliest, long latest, int timezoneOffsetMinutes) {
				
		ZonedDateTime earliestZoned = zonedTimeFromEpoch(earliest, timezoneOffsetMinutes);
		ZonedDateTime latestZoned = zonedTimeFromEpoch(latest, timezoneOffsetMinutes);
		
		if(value != null 
		&& ! value.isBlank()
		){
			if(value.contains("$")) {
				
				//--------------------------------
				// Replace Epoch millis
				value = value
							.replace("$earliest$", ""+earliest)
							.replace("$latest$", ""+latest)
							;
				
				//--------------------------------
				// Replace Earliest 
				if(value.contains("$earliest_")) {
					value = value
							.replace("$earliest_Y$", ""+earliestZoned.getYear())
							.replace("$earliest_M$", ""+earliestZoned.getMonthValue() )
							.replace("$earliest_D$", ""+earliestZoned.getDayOfMonth())
							.replace("$earliest_h$", ""+earliestZoned.getHour())
							.replace("$earliest_m$", ""+earliestZoned.getMinute())
							.replace("$earliest_s$", ""+earliestZoned.getSecond())
							.replace("$earliest_ms$", ""+(earliestZoned.getNano() / 1000000))
							;
				}
				
				//--------------------------------
				// Replace Latest 
				if(value.contains("$latest_")) {
					value = value
							.replace("$latest_Y$", ""+latestZoned.getYear())
							.replace("$latest_M$", ""+latestZoned.getMonthValue() )
							.replace("$latest_D$", ""+latestZoned.getDayOfMonth())
							.replace("$latest_h$", ""+latestZoned.getHour())
							.replace("$latest_m$", ""+latestZoned.getMinute())
							.replace("$latest_s$", ""+latestZoned.getSecond())
							.replace("$latest_ms$", ""+(latestZoned.getNano() / 1000000))
							;
				}

			}
		}
		
		return value;
		
	}

	/********************************************************************************************
	 * 
	 * 
	 ********************************************************************************************/
	public static TimeZone getMachineTimeZone(){
		return machineTimezone;
	}
	
	/********************************************************************************************
	 * Returns an offset similar to what you will get from javascript:
	 *     var timeZoneOffset = new Date().getTimezoneOffset();
	 * The offset will represent minutes you have to add to a time to get UTC, e.g. GMT+2 will 
	 * result in -120.
	 * 
	 * @return offset in minutes for the machine
	 ********************************************************************************************/
	public static int getMachineTimeZoneOffSetMinutes(){
		int offsetMillis = GatlytronTime.getMachineTimeZone().getOffset(System.currentTimeMillis());

		return (int) ( -1 * GatlytronTimeUnit.m.convert(offsetMillis) );
		
	}
	

	
	/********************************************************************************************
	 * 
	 * 
	 ********************************************************************************************/
	public static void setMachineTimeZone(TimeZone timezone){
		machineTimezone = timezone;
	}
	

	/********************************************************************************************
	 * 
	 * 
	 ********************************************************************************************/
	public static long machineTimeMillis(){
		long millis = System.currentTimeMillis();
		return millis + machineTimezone.getOffset(millis);
	}
	
	/********************************************************************************************
	 * Get a string representation of the date in the format  "yyyy-MM-dd'T'HH:mm:ss.SSS".
	 * 
	 ********************************************************************************************/
	public static String currentTimestamp(){
		
		return GatlytronTime.formatDateAsTimestamp(ZonedDateTime.now());
	}
	
	/********************************************************************************************
	 * Get a string representation of the epoch millis.
	 ********************************************************************************************/
	public static String formatMillis(long timeMillis, String format){
		
		return formatDate(zonedTimeFromEpochUTC(timeMillis), format);
	}
	
	/********************************************************************************************
	 * Get a string representation of the epoch millis in the format  "yyyy-MM-dd'T'HH:mm:ss.SSS".
	 ********************************************************************************************/
	public static String formatMillisAsTimestamp(long timeMillis){
		
		return formatterTimestamp.format(
				zonedTimeFromEpochUTC(timeMillis)
			);
	}
	
	
	/********************************************************************************************
	 * Get a string representation of the date in the format  "yyyy-MM-dd'T'HH:mm:ss.SSS".
	 ********************************************************************************************/
	public static String formatDateAsTimestamp(ZonedDateTime date){
		return formatterTimestamp.format(date);
	}
	
	/********************************************************************************************
	 * Get a string representation of the date in the format  "yyyy-MM-dd".
	 ********************************************************************************************/
	public static String formatMillisAsISODate(long millis){
		return formatDateAsISO(
					zonedTimeFromEpochUTC(millis)
				);
	}
	/********************************************************************************************
	 * Get a string representation of the date in the format  "yyyy-MM-dd".
	 ********************************************************************************************/
	public static String formatDateAsISO(ZonedDateTime date){
		return formatterISODate.format(date);
	}
	
	/********************************************************************************************
	 * Get a string representation of the date in the given format
	 ********************************************************************************************/
	public static String formatDate(ZonedDateTime date, String timeFormat, int timezoneOffsetMinutes){
		
		ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneOffsetMinutes*-60);
		return formatDate(date, timeFormat, offset);
	}
	
	/********************************************************************************************
	 * Get a string representation of the date in the given format
	 ********************************************************************************************/
	public static String formatDate(ZonedDateTime date, String timeFormat){
		return formatDate(date, timeFormat, machineTimezone.toZoneId());
	}
	
	/********************************************************************************************
	 * Get a string representation of the date in the given format
	 ********************************************************************************************/
	public static String formatDate(ZonedDateTime date, String timeFormat, ZoneId zone){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat).withZone(zone);
		
		return formatter.format(date);
	}
	
	/********************************************************************************************
	 * Return a new instance of Date with the given offset from the current time.
	 * Use positive values to go back to the future, use negative values to go to the past.
	 * @return date
	 ********************************************************************************************/
	public static Date getCurrentDateWithOffset(int years, int days, int hours, int minutes) {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.add(Calendar.YEAR, years);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		calendar.add(Calendar.MINUTE, minutes);
		
		return calendar.getTime();
	}
	
	/********************************************************************************************
	 * Return a new instance of Timestamp with the given offset from the current time.
	 * Use positive values to go to the future, use negative values to go to the past.
	 * @return timestamp
	 ********************************************************************************************/
	public static Timestamp getCurrentTimestampWithOffset(int years, int months, int days, int hours, int minutes) {
		return offsetTimestamp(new Timestamp(System.currentTimeMillis()), years, months, days, hours, minutes);
	}
	
	/********************************************************************************************
	 * Return a new instance of Timestamp with an offset starting from the given timestamp.
	 * Use positive values to go to the future, use negative values to go to the past.
	 * @return timestamp
	 ********************************************************************************************/
	public static Timestamp offsetTimestamp(Timestamp timestamp, int years, int months, int days) {
		
		return new Timestamp(
				offsetTime(timestamp.getTime(), years, months, days)
			);
	}
	
	/********************************************************************************************
	 * Return a new instance of Timestamp with an offset starting from the given timestamp.
	 * Use positive values to go to the future, use negative values to go to the past.
	 * @return timestamp
	 ********************************************************************************************/
	public static Timestamp offsetTimestamp(Timestamp timestamp, int years, int months, int days, int hours, int minutes) {		
		return new Timestamp(
				offsetTime(timestamp.getTime(), years, months, days, hours, minutes)
			);
	}
	
	/********************************************************************************************
	 * Return a new instance of Timestamp with an offset starting from the given timestamp.
	 * Use positive values to go to the future, use negative values to go to the past.
	 * @return timestamp
	 ********************************************************************************************/
	public static Timestamp offsetTimestamp(Timestamp timestamp
											, int years
											, int months
											, int days
											, int hours
											, int minutes
											, int seconds
											, int milliseconds) {
				
		return new Timestamp(
				offsetTime(timestamp.getTime(), years, months, days, hours, minutes, seconds, milliseconds)
			);
	}
	
	/********************************************************************************************
	 * Return time with an offset starting from the given time.
	 * Use positive values to go to the future, use negative values to go to the past.
	 * @return time in epoch milliseconds
	 ********************************************************************************************/
	public static long offsetTime(long timeMillis, int years, int months, int days) {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTimeInMillis(timeMillis);
		calendar.add(Calendar.YEAR, years);
		calendar.add(Calendar.MONTH, months);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		
		return calendar.getTimeInMillis();
	}
	
	/********************************************************************************************
	 * Return time with an offset starting from the given time.
	 * Use positive values to go to the future, use negative values to go to the past.
	 * @return time in epoch milliseconds
	 ********************************************************************************************/
	public static long offsetTime(long timeMillis, int years, int months, int days, int hours, int minutes) {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTimeInMillis(timeMillis);
		calendar.add(Calendar.YEAR, years);
		calendar.add(Calendar.MONTH, months);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		calendar.add(Calendar.MINUTE, minutes);
		
		return calendar.getTimeInMillis();
	}
	
	/********************************************************************************************
	 * Return time in millis with an offset starting from the given time.
	 * Use positive values to go to the future, use negative values to go to the past.
	 * @return time in epoch milliseconds
	 ********************************************************************************************/
	public static long offsetTime(long timeMillis
								, int years
								, int months
								, int days
								, int hours
								, int minutes
								, int seconds
								, int milliseconds) {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTimeInMillis(timeMillis);
		calendar.add(Calendar.YEAR, years);
		calendar.add(Calendar.MONTH, months);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		calendar.add(Calendar.MINUTE, minutes);
		calendar.add(Calendar.SECOND, seconds);
		calendar.add(Calendar.MILLISECOND, milliseconds);
		
		return calendar.getTimeInMillis();
	}
	
	/********************************************************************************************
	 * Get the default age out time of the application.
	 * @return timestamp
	 ********************************************************************************************/
	public static Timestamp getDefaultAgeOutTime(int granularityMinutes) {
		Timestamp ageOutOffset = null;
		
		if		(granularityMinutes <= 3) 		{ ageOutOffset = GatlytronTime.getCurrentTimestampWithOffset(0, 0, 0, 0, -30); }
		else if (granularityMinutes <= 15) 		{ ageOutOffset = GatlytronTime.getCurrentTimestampWithOffset(0, 0, 0, -1, 0); }
		else if (granularityMinutes <= 60) 		{ ageOutOffset = GatlytronTime.getCurrentTimestampWithOffset(0, 0, -1, 0, 0); }
		else if (granularityMinutes <= 240) 	{ ageOutOffset = GatlytronTime.getCurrentTimestampWithOffset(0, 0, -7, 0, 0); }
		else if (granularityMinutes <= 720) 	{ ageOutOffset = GatlytronTime.getCurrentTimestampWithOffset(0, 0, -14, 0, 0); }
		else if (granularityMinutes <= 1440) 	{ ageOutOffset = GatlytronTime.getCurrentTimestampWithOffset(0, 0, -30, 0, 0); }
		else  									{ ageOutOffset = GatlytronTime.getCurrentTimestampWithOffset(0, 0, -90, 0, 0); }

		return ageOutOffset;
	}
	
	/********************************************************************************************
	 * Returns the number of seconds between two epoch 
	 * @return string
	 ********************************************************************************************/
	public static long calculateSecondsBetween(long earliestMillis, long latestMillis) {
		Duration d = Duration.between(Instant.ofEpochMilli(earliestMillis), Instant.ofEpochMilli(latestMillis));
		
		long seconds = d.toMillis() / 1000;
		
		return seconds;
	}
	
	/********************************************************************************************
	 * Returns an interval string like "15s", "5m" or "1h". 
	 * @param earliestMillis
	 * @param latestMillis
	 * @param maxPoints max number of datapoints for the given interval
	 * @param separator (optional)will be put in between the amount and the time unit
	 * @return string
	 ********************************************************************************************/
	public static String calculateDatapointInterval(long earliestMillis, long latestMillis, int maxPoints, String separator) {
		
		String s = (separator != null) ? separator : "";
		
		long seconds = calculateSecondsBetween(earliestMillis, latestMillis);

		if((seconds / 15) < maxPoints) { return "15"+s+"s"; }
		else if((seconds / 30) < maxPoints) { return "30"+s+"s"; }
		else if((seconds / 60) < maxPoints) { return "1"+s+"m"; }
		else if((seconds / 120) < maxPoints) { return "2"+s+"m"; }
		else if((seconds / 300) < maxPoints) { return "5"+s+"m"; }
		else if((seconds / 600) < maxPoints) { return "10"+s+"m"; }
		else if((seconds / 900) < maxPoints) { return "15"+s+"m"; }
		else if((seconds / 1200) < maxPoints) { return "20"+s+"m"; }
		else if((seconds / 1800) < maxPoints) { return "30"+s+"m"; }
		else if((seconds / 3600) < maxPoints) { return "1"+s+"h"; }
		else if((seconds / (3600*2) ) < maxPoints) { return "2"+s+"h"; }
		else if((seconds / (3600*4) ) < maxPoints) { return "4"+s+"h"; }
		else if((seconds / (3600*6) ) < maxPoints) { return "6"+s+"h"; }
		else if((seconds / (3600*12) ) < maxPoints) { return "12"+s+"h"; }
		else if((seconds / (3600*24) ) < maxPoints) { return "24"+s+"h"; }
		
		return "7"+s+"d";
	}
	
	
	/********************************************************************************************
	 * Converts a time from one unit to another unit.
	 * @return long value rounded down
	 ********************************************************************************************/
	public static long convertTimeToUnit(long fromValue, TimeUnit fromUnit, TimeUnit toUnit) {
		
		switch(toUnit) {
			case DAYS: 			return fromUnit.toDays(fromValue);
			case HOURS:			return fromUnit.toHours(fromValue);
			case MICROSECONDS: 	return fromUnit.toMicros(fromValue);
			case MILLISECONDS: 	return fromUnit.toMillis(fromValue);
			case MINUTES: 		return fromUnit.toMinutes(fromValue);
			case NANOSECONDS: 	return fromUnit.toNanos(fromValue);
			case SECONDS: 		return fromUnit.toSeconds(fromValue);
			default: 			return -1L;
		}

		
	}


}
