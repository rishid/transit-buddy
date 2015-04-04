package com.common.types;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.enumerations.RouteType;

public class TransitSystem extends TransitName
{
	private static final long serialVersionUID = 7831602131876715156L;
	private HashMap<RouteType, ArrayList<TransitRoute>> mRoutes;

	/**
	 * 
	 * @param name
	 * @param id
	 * @param transitRoutes
	 */
	public TransitSystem(String name, String id,
	    HashMap<RouteType, ArrayList<TransitRoute>> routes)
	{
		super(name, id);

		if (routes == null)
		{
			mRoutes = new HashMap<RouteType, ArrayList<TransitRoute>>();
		}
		else
		{
			mRoutes = routes;
		}
	}

	/**
	 * 
	 * @param name
	 * @param id
	 */
	public TransitSystem(String name, String id)
	{
		this(name, id, null);
	}

	/**
	 * Retrieves the Transit route types for this transit system.
	 * 
	 * @return The routes that are supported by this city.
	 */
	public ArrayList<RouteType> getTransitRoutesTypes()
	{
		ArrayList<RouteType> routeTypes = new ArrayList<RouteType>();

		for (RouteType routeType : mRoutes.keySet())
		{
			routeTypes.add(routeType);
		}

		return routeTypes;
	}

	/**
	 * 
	 * @param tt
	 */
	public void addRoute(RouteType type, TransitRoute route)
	{
		if (mRoutes.containsKey(type))
		{
			ArrayList<TransitRoute> routes = mRoutes.get(type);
			routes.add(route);

			mRoutes.put(type, routes);
		}
		else
		{
			ArrayList<TransitRoute> routes = new ArrayList<TransitRoute>();
			routes.add(route);

			mRoutes.put(type, routes);
		}
	}

	public TransitRoute getTransitRoute(RouteType routeType, String routeID)
	{
		ArrayList<TransitRoute> routeList = getTransitRoutes(routeType);

		for (TransitRoute route : routeList)
		{
			if (route.getId().compareToIgnoreCase(routeID) == 0)
			{
				return route;
			}
		}

		return null;
	}

	public ArrayList<TransitRoute> getTransitRoutes(RouteType routeType)
	{
		if (mRoutes.containsKey(routeType))
			return mRoutes.get(routeType);
		else
			return null;
	}
	
	public HashMap<RouteType, ArrayList<TransitRoute>> getTransitRouteHashMap()
	{
		return mRoutes;
	}

	public void setTransitRoutes(RouteType routeType,
	    ArrayList<TransitRoute> routeList)
	{
		if (mRoutes.containsKey(routeType))
		{
			mRoutes.put(routeType, routeList);
		}
	}
	
	public void setTransitRoutes(HashMap<RouteType, ArrayList<TransitRoute>> routeList)
	{
		mRoutes = routeList;
	}
}
