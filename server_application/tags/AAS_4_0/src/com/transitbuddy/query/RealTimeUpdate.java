package com.transitbuddy.query;

import com.transitbuddy.main.updator.Predictions.Direction.Stop;

public class RealTimeUpdate extends Update
{
	protected String mAgency;
	protected String mRouteTag;
	protected String mHeadsign;
	protected String mDirectionName;
	protected Stop mStop;

	public void setAgency(String agency)
	{
		mAgency = agency;
	}

	public void setRouteTag(String routetag)
	{
		mRouteTag = routetag;
	}

	public void setDirectionName(String directionname)
	{
		mDirectionName = directionname;
	}

	public void setHeadsign(String headsign)
	{
		mHeadsign = headsign;
	}

	public void setStop(Stop s)
	{
		mStop = s;
	}
}
