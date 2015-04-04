package com.transitbuddy.query.mysql;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.transitbuddy.query.RetrieveTripsQuery;

public class RetrieveTripsMySqlQuery extends RetrieveTripsQuery
{
	private static final Logger LOGGER = Logger
	    .getLogger(RetrieveTripsMySqlQuery.class);

	/**
	 * Query Statement accessor
	 * 
	 * @return The statement for this query
	 */
	public String getStatementString()
	{
		String ret = null;
		String agencyId = getAgencyId();
		if (agencyId == null)
		{
			LOGGER.error("Agency ID is null. Cannot create get trips"
			    + " SQL statement.");
			return null;
		}
		String routeName = getRouteName();
		if (routeName == null)
		{
			LOGGER.error("Route Name is null. Cannot create get trips"
			    + " SQL statement.");
			return null;
		}

		Calendar calendar = Calendar.getInstance();
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);

		ret = "select routes." + ROUTE_ID_COL + ", routes." + ROUTE_SHORT_NAME_COL
		+ ", routes." + ROUTE_LONG_NAME_COL + ", routes." + ROUTE_TYPE_COL
		+ ", trips." + TRIP_ID_COL + ", trips." + TRIP_HEADSIGN_COL
		+ " from routes, trips, "
		+ "(select service_id from calendar where now() >= start_date and now()"
		+	" <= end_date and if (dayofweek(now()) = "
		+ weekday
		+ ", 1, 0) = "
		+ dayOfWeekToString(weekday)
		+ ") cal "
		+ "where routes." + ROUTE_ID_COL+ " = trips." + ROUTE_ID_COL
		+ " and	trips.service_id = cal.service_id "
		+ " and (routes.route_short_name = '" + routeName + "' or " 
		+ " routes.route_long_name='" + routeName + "')"
		+ " group by	" + TRIP_HEADSIGN_COL + "";		
		return ret;
	}

	private static String dayOfWeekToString(int dow)
	{
		switch (dow)
		{
			case 1:
				return "sunday";
			case 2:
				return "monday";
			case 3:
				return "tuesday";
			case 4:
				return "wednesday";
			case 5:
				return "thursday";
			case 6:
				return "friday";
			default:
				return "saturday";
		}
	}
}
