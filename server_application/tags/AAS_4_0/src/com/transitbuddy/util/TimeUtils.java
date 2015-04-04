package com.transitbuddy.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.log4j.Logger;

public class TimeUtils
{
	static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * Converts time from sourceTZ TimeZone to destTZ TimeZone.
	 *
	 * @return converted time, or the original time, in case the datetime could
	 *  not be parsed
	 * @throws ParseException if the beginning of the specified time string
	 *  cannot be parsed.
	 */
	public static String convTimeZone(String time, String sourceTZ, 
			String destTZ) throws ParseException
	{

		time = time.substring(0, time.indexOf(".") - 1);
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);

		Date specifiedTime;
		if (sourceTZ != null)
		{
			sdf.setTimeZone(TimeZone.getTimeZone(sourceTZ));
		}
		else
		{
			sdf.setTimeZone(TimeZone.getDefault()); // default to server's timezone
		}
		specifiedTime = sdf.parse(time);

		// switch timezone
		if (destTZ != null)
		{
			sdf.setTimeZone(TimeZone.getTimeZone(destTZ));
		}
		else
		{
			sdf.setTimeZone(TimeZone.getDefault()); // default to server's timezone
		}
		return sdf.format(specifiedTime);
	}
	
	public static Timestamp convTime(String time) throws ParseException
	{
		Timestamp ret = null;
		time = time.replace("<br>", " ");
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MM/dd/yy hh:mm aa");

		Date specifiedTime = sdf.parse(time);
		ret = new Timestamp(specifiedTime.getTime());
		return ret;
	}
	
	public static void LogTime(Logger logger, String message)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		String formattedDateTime = sdf.format(Calendar.getInstance().getTime());
		logger.info("Time = " + formattedDateTime + ". " + message);
	}
}
