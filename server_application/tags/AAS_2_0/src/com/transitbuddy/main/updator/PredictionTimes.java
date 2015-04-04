package com.transitbuddy.main.updator;

import java.util.HashMap;
import java.util.Map;

public class PredictionTimes
{
	public String agency;
	public String route;
	public String stopTag;
	public String stopId;
	public Map<Integer, Long> tripTimes;

	public PredictionTimes()
	{
		tripTimes = new HashMap<Integer, Long>();
	}

	public String toString()
	{
		String rc = new String();
		rc += "Agency: " + agency + "\n";
		rc += "Route: " + route + "\n";
		rc += "Stop Tag: " + stopTag;
		rc += " Stop ID: " + stopId + "\n";
		for (Map.Entry<Integer, Long> entry : tripTimes.entrySet())
		{
			rc += "tripId: " + entry.getKey() + " time: " + entry.getValue() + "\n";
		}
		return rc;
	}
}
