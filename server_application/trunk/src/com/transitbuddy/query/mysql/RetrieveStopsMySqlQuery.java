package com.transitbuddy.query.mysql;

import java.util.Calendar;

import com.transitbuddy.query.RetrieveStopsQuery;

public class RetrieveStopsMySqlQuery extends RetrieveStopsQuery
{
	/**
	 * Query Statement accessor
	 * 
	 * @return The statement for this query
	 */
	public String getStatementString()
	{
		String ret = null;
		String agencyId = getAgencyId();
		String routeName = getRouteName();
		String tripHeadsign = getTripHeadsign();

		// This clause will get times after now
		// Don't insert this clause if the default number of times per stop is used
		// since that means we want all times
		String nowClause = "";
		if (getNumTimesPerStop() != DEFAULT_NUM_TIMES_PER_STOP)
		{
			nowClause = "and stop_times." + STOP_DEPARTURE_TIME_COL + " > now() ";
		}

		Calendar calendar = Calendar.getInstance();
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);

		ret = "SELECT " + STOP_NAME_COL + ", " + STOP_ID_COL + ", " + STOP_LAT_COL
		+ ", " + STOP_LON_COL + ", " + STOP_DEPARTURE_TIME_COL + ", " + "stop_sequence "
		+ " FROM "
		+ "(SELECT " + STOP_NAME_COL + ", " + STOP_ID_COL + ", " + STOP_LAT_COL
		+ ", " + STOP_LON_COL + ", " + STOP_DEPARTURE_TIME_COL + ", stop_sequence, "
		+ "@r := IF(@g=" + STOP_NAME_COL + ", @r+1, 1) RowNum, "
		+ "@g := " + STOP_NAME_COL + " "
		+ "FROM (SELECT @g:=null, @r:=1) initvars INNER JOIN ( "
		+ "SELECT "
		+ "stops." + STOP_NAME_COL + ", stops." + STOP_ID_COL + ", stops."
		+ STOP_LAT_COL + ", stops." + STOP_LON_COL + ", stop_times."
		+ STOP_DEPARTURE_TIME_COL + ", stop_sequence "
		+ "FROM agency, routes, trips, stop_times, stops, "
		+ "(select service_id from calendar where now() >= start_date and now()"
		+	" <= end_date and if (dayofweek(now()) = "
		+ weekday
		+ ", 1, 0) = "
		+ dayOfWeekToString(weekday)
		+ ") cal "
		+ "WHERE "
		+ "routes.agency_id=agency.agency_id and "
		+ "trips.route_id=routes.route_id and "
		+ "stop_times.trip_id=trips.trip_id and "
		+ "stops." + STOP_ID_COL + "=stop_times." + STOP_ID_COL + " and "
		+ "trips.service_id = cal.service_id and "
		+ "agency.agency_id='"
		+ agencyId
		+ "' and "
		+ " (routes.route_short_name = '" + routeName + "' or " 
		+ " routes.route_long_name='" + routeName + "')"
		+ " and "
		+ "trips.trip_headsign='"
		+ tripHeadsign
		+ "' "
		+ nowClause
		+ " ORDER BY "
		+ "" + STOP_NAME_COL + " ASC, "
		+ "" + STOP_DEPARTURE_TIME_COL + " ASC "
		+ ") T ) U WHERE "
		+ "RowNum <= "
		+ getNumTimesPerStop()
		+ " ORDER BY stop_sequence ASC, stop_name ASC, departure_time ASC";

		return ret;
	}

	private String dayOfWeekToString(int dow)
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
