package com.transitbuddy.query.mysql;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.transitbuddy.query.RetrieveRoutesQuery;

public class RetrieveRoutesMySqlQuery extends RetrieveRoutesQuery
{
	private static final Logger LOGGER = Logger
	    .getLogger(RetrieveRoutesMySqlQuery.class);

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
			LOGGER.error("Agency ID is null. Cannot create get routes"
			    + " SQL statement.");
			return null;
		}

		Calendar calendar = Calendar.getInstance();
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);

		ret = "select agency_name, if(route_type != 3 and route_long_name is not null, route_long_name, route_short_name) as route_name, routes.route_id, "
				+ " (select case when route_type = 0 then 1 else route_type end) AS pseudo_route_type "
		    + " from agency, routes, trips, stop_times, "
		    + "(select service_id from calendar  where now() >= start_date and now() <= end_date and if (dayofweek(now()) = "
		    + weekday
		    + ", 1, 0) = "
		    + dayOfWeekToString(weekday)
		    + ") cal"
		    + " where agency.agency_id = routes.agency_id and routes.route_id = trips.route_id and trips.service_id = cal.service_id"
		    + " and agency.agency_id = '" + agencyId + "'"
		    + " and trips.trip_id = stop_times.trip_id and stop_times.departure_time > now() "
		    + " group by route_name order by cast(route_name as unsigned)";
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
