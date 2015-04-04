package com.transitbuddy.main.updator;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteConfig
{
	public String mAgency;
	public String mRouteTag;
	public HashMap<String, String> mStops = new HashMap<String, String>();
	public ArrayList<Direction> mDirections = new ArrayList<RouteConfig.Direction>();

	public class Direction
	{
		public String mHeadsign;
		public String mDirectionName; // inbound / outbound
		public HashMap<String, String> mStops = new HashMap<String, String>();
	}
}
