package com.transitbuddy.query.mysql;

import java.util.Map;

import com.transitbuddy.query.InsertRealTimeUpdate;

public class InsertRealTimeMySqlUpdate extends InsertRealTimeUpdate
{	
	/**
	 * Query Statement getter
	 * @return The statement for this query 
	 */
	public String getStatementString()
	{	
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO real_time VALUES(");
		sb.append(mPredictionTime.agency + ", ");
		sb.append(mPredictionTime.route + ", ");
		sb.append(mPredictionTime.stopId + ", ");
		sb.append("now(), ");
		
		for (Map.Entry<Integer, Long> entry : mPredictionTime.tripTimes.entrySet()) {
	    Integer key = entry.getKey();
	    Long value = entry.getValue();
	    sb.append(key + ", ");
			sb.append(value + ", ");
	}
		// Trim trailing ", " and append ");"
		String temp = sb.substring(0, sb.length() - 2);
		temp += ");";
		
		return temp;
	}
}
