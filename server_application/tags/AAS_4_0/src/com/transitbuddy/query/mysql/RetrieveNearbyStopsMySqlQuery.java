package com.transitbuddy.query.mysql;

import com.common.types.Coordinate;
import com.transitbuddy.query.RetrieveNearbyStopsQuery;

public class RetrieveNearbyStopsMySqlQuery extends RetrieveNearbyStopsQuery
{
	private final static float METERS_IN_A_MILE = 0.000621371192f;

	/**
	 * Query Statement accessor
	 * 
	 * @return The statement for this query
	 */
	public String getStatementString()
	{
		String ret = "";
		Coordinate coord = getCoord();
		float vicinityMiles = getVicinityMeters() * METERS_IN_A_MILE;
		float lat = coord.getLatitude();
		float lon = coord.getLongitude();
		
		ret = "SELECT " + STOP_NAME_COL + ", " + STOP_ID_COL + ", " + STOP_LAT_COL
		    + ", " + STOP_LON_COL + ", " + STOP_DEPARTURE_TIME_COL + ", "
		    + STOP_DISTANCE_COL + "" + " FROM ( SELECT " + STOP_NAME_COL + ", "
		    + STOP_ID_COL + ", " + STOP_LAT_COL + ", " + STOP_LON_COL + ", "
		    + STOP_DEPARTURE_TIME_COL + ", " + STOP_DISTANCE_COL + ", "
		    + "@r := IF(@g=" + STOP_NAME_COL + ", @r+1, 1) RowNum," + "@g := "
		    + STOP_NAME_COL + " FROM (SELECT @g:=null, @r:=1) initvars"
		    + " INNER JOIN ( SELECT stops." + STOP_NAME_COL + ", stops."
		    + STOP_ID_COL + ", stops." + STOP_LAT_COL + ", stops." + STOP_LON_COL
		    + ", stop_times." + STOP_DEPARTURE_TIME_COL + ","
		    + " 3956 * 2 * ASIN(SQRT(POWER(SIN((" + lat+ " - "
		    + "abs(" + STOP_LAT_COL + ")) * pi()/180 / 2), 2) +"
		    + " COS(" + lat+ " * pi()/180 ) * COS(abs(" + STOP_LAT_COL + ")"
		    +	" * pi()/180) * POWER(SIN((" + lon + " - " + STOP_LON_COL + ")"
		    + " * pi()/180 / 2), 2) )) as distance"
		    + " FROM stops, stop_times"
		    + " WHERE"
		    + " stops." + STOP_ID_COL + "=stop_times." + STOP_ID_COL + " AND"        
		    + " stop_times." + STOP_DEPARTURE_TIME_COL + " > NOW() AND"
		    + " stop_times." + STOP_DEPARTURE_TIME_COL + " < ADDTIME(NOW()," +
		    		" '00:30:00')"
		    + " HAVING"
		    + " " +STOP_DISTANCE_COL + " < " + vicinityMiles
		    + " ORDER BY " +STOP_DISTANCE_COL + " ASC) T) U"
		    + " WHERE RowNum <= 3 ORDER BY " + STOP_DISTANCE_COL + " ASC, "
		    + STOP_DEPARTURE_TIME_COL + " ASC;";

		return ret;
	}
}
