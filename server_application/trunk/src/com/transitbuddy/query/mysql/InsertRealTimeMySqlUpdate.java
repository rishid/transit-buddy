package com.transitbuddy.query.mysql;

import com.transitbuddy.query.InsertRealTimeUpdate;

public class InsertRealTimeMySqlUpdate extends InsertRealTimeUpdate
{
	/**
	 * Query Statement getter
	 * 
	 * @return The statement for this query
	 */
	public String getStatementString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO real_time VALUES(");
		sb.append("'" + mAgency + "',");
		sb.append("'" + mRouteTag + "',");
		sb.append("'" + mHeadsign.replace("'", "''") + "',");
		sb.append("'" + mDirectionName.replace("'", "''") + "',");
		sb.append("'" + mStop.mStopTag + "',");
		sb.append("'" + mStop.mStopTitle.replace("'", "''") + "',");
		sb.append("now(),");
		for (int i = 1; i <= mStop.mPredictions.size(); i++)
		{
			sb.append("'" + mStop.mPredictions.get(i - 1).mArrivalTime + "',");
			sb.append("'" + mStop.mPredictions.get(i - 1).mVehicle + "',");
		}
		// empty out old data
		for (int i = 5; i > mStop.mPredictions.size(); i--)
		{
			sb.append("'0','0',");
		}

		// Trim trailing ", " and append ");"
		String temp = sb.substring(0, sb.length() - 1);
		temp += ");";

		return temp;
	}
}
