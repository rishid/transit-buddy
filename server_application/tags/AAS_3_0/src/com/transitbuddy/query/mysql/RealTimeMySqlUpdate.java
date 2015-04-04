package com.transitbuddy.query.mysql;

import com.transitbuddy.query.RealTimeUpdate;

public class RealTimeMySqlUpdate extends RealTimeUpdate
{
	public String getStatementString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE real_time SET ");
		sb.append("headsign='" + mHeadsign.replace("'", "''") + "',");
		sb.append("direction_name='" + mDirectionName.replace("'", "''") + "',");
		sb.append("stop_tag='" + mStop.mStopTag + "',");
		sb.append("stop_title='" + mStop.mStopTitle.replace("'", "''") + "',");
		sb.append("last_updated=NOW(),");
		for (int i = 1; i <= mStop.mPredictions.size(); i++)
		{
			sb.append("arrival_time" + i + "='"
			    + mStop.mPredictions.get(i - 1).mArrivalTime + "',");
			sb.append("vehicle" + i + "='" + mStop.mPredictions.get(i - 1).mVehicle
			    + "',");
		}
		// empty out old data
		for (int i = 5; i > mStop.mPredictions.size(); i--)
		{
			sb.append("arrival_time" + i + "='0',");
			sb.append("vehicle" + i + "='0',");
		}
		// Trim trailing ","
		String temp = sb.substring(0, sb.length() - 1);

		temp += " WHERE agency_id='" + mAgency + "' AND " + "route_tag='"
		    + mRouteTag + "' AND " + "headsign='" + mHeadsign.replace("'", "''")
		    + "' AND " + "stop_tag='" + mStop.mStopTag + "';";

		return temp;
	}

}
