package com.transitbuddy.query.mysql;

import com.transitbuddy.query.RetrieveRealTimeStopsQuery;

public class RetrieveRealTimeStopsMySqlQuery extends RetrieveRealTimeStopsQuery
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
		 
		ret = "select stop_title as stop_name, stop_tag as stop_id,  stop_lat, stop_lon, arrival_time1, arrival_time2, arrival_time3, arrival_time4, arrival_time5"
		    + " from real_time, stops, agency"
		    + " where real_time.stop_tag = stops.stop_id and"
		    + " agency.agency_id='"
		    + agencyId
		    + "' and "
		    + " route_tag = '"
		    + routeName + "'" + " and headsign='" + tripHeadsign + "' ";
		return ret;
	}
}