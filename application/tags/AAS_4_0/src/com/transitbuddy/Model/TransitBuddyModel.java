package com.transitbuddy.Model;

import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

import com.common.commands.GetNearbyStops;
import com.common.commands.GetRoutes;
import com.common.commands.GetStops;
import com.common.commands.GetTransitSystems;
import com.common.commands.GetTrips;
import com.common.connection.CommandTransporter;
import com.common.enumerations.RouteType;
import com.common.types.Coordinate;
import com.common.types.NearbyStops;
import com.common.types.TransitData;
import com.common.types.TransitRoute;
import com.common.types.TransitStop;
import com.common.types.TransitSystem;
import com.common.types.TransitTrip;
import com.common.utilities.ResponseResult;
import com.common.utilities.ResponseResult.ResponseStatus;
import com.transitbuddy.enumerations.MapType;

public class TransitBuddyModel 
{
	TransitData mTransitBuddyData = null;
	ArrayList<TransitStop> mNearbyStops = null;
	
	String 		mSystemID 	= null;
	RouteType 	mRouteType 	= null;
	String 		mRouteName 	= null;
	String		mRouteID	= null;
	String 		mTripID 	= null;
	MapType     mMapID      = null;
	
	int mPortNum = 0;
	int mTimeout = 0;
	String mHostAddr = "";

	/**
	 * Default Constructor
	 * 
	 * @param hostAddr The TransitBuddy server address
	 * @param portNum The port number to communicate over.
	 * @param timeout The command timeout in milliseconds.
	 */
	public TransitBuddyModel(String hostAddr, int portNum, int timeout)
	{
		mTransitBuddyData = new TransitData();
		
		mSystemID 	= null;
		mRouteType 	= null;
		mRouteName 	= null;
		mRouteID	= null;
		mTripID 	= null;
		mMapID      = null;
		
		mHostAddr = hostAddr;
		mPortNum = portNum;
		mTimeout = timeout;
	}
	
	/**
	 * Sets the transit data to the given data
	 * 
	 * @param data The new transit data
	 */
	public void setTransitBuddyData(TransitData data)
	{
		mTransitBuddyData = data;
	}
	
	/**
	 * Retrieves the currently active system ID.
	 * 
	 * @return The currently active system ID; returns null if no system is active
	 */
	public String getSystemID()
	{
		return mSystemID;
	}
	
	/**
	 * Gets the system ID at the specified index
	 * @param index The index within the transit system list returned by getTransitSystems
	 * @return The ID if the index is valid; else null
	 */
	public String getSystemID(int index)
	{
		String systemID = null;
		
		if(mTransitBuddyData != null)
		{
			if(mTransitBuddyData.getTransitSystems().size() > index)
			{
				systemID = mTransitBuddyData.getTransitSystems().get(index).getId();
			}
		}
		
		return systemID;
	}
	
	/**
	 * Gets the transit system ID of the system name
	 * @param systemName The transit system name of the system within the support transit system list.
	 * @return The ID if the transit system name is valid; else null
	 */
	public String getSystemID(String systemName)
	{
		String systemID = null;
		
		if(mTransitBuddyData != null)
		{
			systemID = mTransitBuddyData.lookup(systemName);
		}
		
		return systemID;
	}
	
	/**
	 * Sets currently active system ID.
	 * 
	 * @param systemID The system ID to set as active.
	 */
	public void setSystemID(String systemID)
	{
		mSystemID = systemID;
	}
	
	/**
	 * Retrieves the currently active route type.
	 * 
	 * @return The currently active route type; returns null if no route type is active
	 */
	public RouteType getRouteType()
	{
		return mRouteType;
	}
	
	/**
	 * Gets the route type at the specified index
	 * @param index The index within the route type list returned by getTransitRouteTypes
	 * @return The route type if the index is valid; else null
	 * @note The systemID must have been set before calling this function.
	 */
	public RouteType getRouteTypeID(int index)
	{
		RouteType routeType = null;
		int localIndex = 0;
		
		if(mTransitBuddyData != null)
		{
			for(RouteType type : mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoutesTypes())
			{
				if(localIndex == index)
					routeType = type;
			}
		}
		
		return routeType;
	}
	
	/**
	 * Sets currently active route type.
	 * 
	 * @param routeType The route type to set as active.
	 */
	public void setRouteType(RouteType routeType)
	{
		mRouteType = routeType;
	}
	
	/**
	 * Retrieves the currently active route ID.
	 * 
	 * @return The currently active route ID; returns null if no route is active
	 */
	public String getRouteID()
	{
		return mRouteID;
	}
	
	/**
	 * Retrieves the currently active route Name.
	 * 
	 * @return The currently active route Name; returns null if no route is active
	 */
	public String getRouteName()
	{
		return mRouteName;
	}
	
	/**
	 * Gets the route ID at the specified index
	 * @param index The index within route list returned by getTransitRoutes
	 * @return The ID if the index is valid; else null
	 * @note The systemID and route type must have been set before calling 
	 * this function.
	 */
	public String getRouteID(int index)
	{
		String routeID = null;
		
		if(mTransitBuddyData != null)
		{
			if(mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoutes(mRouteType).size() > index)
			{
				routeID = mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoutes(mRouteType).get(index).getId();
			}
		}
		
		return routeID;
	}
	
	/**
	 * Gets the route name at the specified index
	 * @param index The index within route list returned by getTransitRoutes
	 * @return The route name if the index is valid; else null
	 * @note The systemID and route type must have been set before calling 
	 * this function.
	 */
	public String getRouteName(int index)
	{
		String routeName = null;
		
		if(mTransitBuddyData != null)
		{
			if(mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoutes(mRouteType).size() > index)
			{
				routeName = mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoutes(mRouteType).get(index).getName();
			}
		}
		
		return routeName;
	}
	
	/**
	 * Sets currently active route name.
	 * 
	 * @param systemID The route name to set as active.
	 */
	public void setRouteID(String routeID)
	{
		mRouteID = routeID;
		
		// All server queries are using the route name now so we need to set that too.
		ArrayList<TransitRoute> routes = mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoutes(mRouteType);
		for(TransitRoute route : routes)
		{
			if(route.getId().compareToIgnoreCase(routeID) == 0)
			{
				mRouteName = route.getName();
				break;
			}
		} 
	}
	
	/**
	 * Retrieves the currently active trip ID.
	 * 
	 * @return The currently active trip ID; returns null if no trip is active
	 */
	public String getTripID()
	{
		return mTripID;
	}
	
	/**
	 * Gets the trip ID at the specified index
	 * @param index The index within the trip list returned by getTransitTrips
	 * @return The ID if the index is valid; else null
	 * @note The systemID, route type and routeID must have been set before calling 
	 * this function.
	 */
	public String getTripID(int index)
	{
		String tripID = null;
		
		if(mTransitBuddyData != null)
		{
			if(mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoute(mRouteType, mRouteID).getTrips().size() > index)
			{
				tripID = mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoute(mRouteType, mRouteID).getTrips().get(index).getId();
			}
		}
		
		return tripID;
	}
	
	/**
	 * Sets currently active trip ID.
	 * 
	 * @param systemID The trip ID to set as active.
	 */
	public void setTripID(String tripID)
	{
		mTripID = tripID;
	}
	
	
    /**
     * Gets the currently set map ID
     * 
     * @return the current active map ID
     */
	public MapType  getMapID()
	{	
		return mMapID;
	}
	
	/**
	 * Sets the currently active map ID
	 * 
	 * @param mapID the map ID to set as active
	 */
	public void setMapID(MapType mapID)
	{  
		mMapID = mapID;
	}
		
	/**
	 * Retrieves the available transit systems/cities from the server
	 * 
	 * @return A List containing all the supported transit buddy systems
	 * @throws IOException - if the server connection is interrupted, or if the 
	 *         connection could not be made
	 *         before the set timeout period
	 */
	public ResponseStatus getTransitSystems(ArrayList<String> systems) throws IOException
	{
		ResponseStatus status = ResponseStatus.Failed;
		
		Log.d("TransitBuddyModel", "getTransitSystems - Creating CommandTransporter");
		
		CommandTransporter transporter = new CommandTransporter(mHostAddr, mPortNum, mTimeout);
		
		Log.d("TransitBuddyModel", "getTransitSystems - CommandTransporter created");
		
		// Go to the server to get the list of transit systems.
		GetTransitSystems systemCmd = new GetTransitSystems();
		
		Log.d("TransitBuddyModel", "getTransitSystems - Sending GetTransitSytems command");
		
		// Send the command and get the result.
		ResponseResult res = transporter.sendCommand(systemCmd);

		Log.d("TransitBuddy Model", "getTransitSystems - Received GetTransitSystems command response");
		
		if(res.getResponseStatus() == ResponseStatus.Completed)
		{
			Log.d("TransitBuddyModel", "getTransitSystems - Processing received transit systems");
			
			// Load the routes into our TransitData object
			TransitData transitData = (TransitData)(res.getResultData());
			
			mTransitBuddyData = new TransitData(transitData.getTransitSystems());
			
			// Now load the system string into the systems array
			if(systems == null)
				systems = new ArrayList<String>();
		
			for(TransitSystem system : mTransitBuddyData.getTransitSystems())
				systems.add(system.getName());

			Log.d("TransitBuddyModel", "getTransitSystems - Finished processing transit systems");
		}
		
		status = res.getResponseStatus();
		
		return status;
	}
	
	public ResponseStatus getTransitRouteTypes(String systemID,
											   ArrayList<String> routeTypes) throws IOException
	{
		ResponseStatus status = ResponseStatus.Failed;
		
		Log.d("TransitBuddyModel", "getTransitRouteTypes - Creating CommandTransporter");
			
		CommandTransporter transporter = new CommandTransporter(mHostAddr, mPortNum, mTimeout);
		
		Log.d("TransitBuddyModel", "getTransitRouteTypes - CommandTransporter created");
		
		mSystemID = systemID;
		
		// Go to the server to get the list of route types.
		GetRoutes routesCmd = new GetRoutes(mSystemID);
		
		Log.d("TransitBuddyModel", "getTransitRouteTypes - Sending GetRoutes command");
		
		// Send the command and get the result.
		ResponseResult res = transporter.sendCommand(routesCmd);
		
		Log.d("TransitBuddyModel", "getTransitRouteTypes - Received GetRoutes command response");
		
		if(res.getResponseStatus() == ResponseStatus.Completed)
		{
			Log.d("TransitBuddyModel", "getTransitRouteTypes - Processing received route types");
			
			// Load the routes into our TransitData object
			TransitSystem transitSystem = (TransitSystem) res.getResultData();
			
			mTransitBuddyData.getTransitSystem(mSystemID).setTransitRoutes(transitSystem.getTransitRouteHashMap());
			
			// Now load the route type strings into the routeTypes array
			if(routeTypes == null)
				routeTypes = new ArrayList<String>();
			
			for(RouteType routeType : mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoutesTypes())
				routeTypes.add(routeType.getName());
			
			Log.d("TransitBuddyModel", "getTransitRouteTypes - Finished processing route types");
		}
		
		status = res.getResponseStatus();
		
		return status;
	}
	
	/**
	 * 
	 * @note Must be called after calling getTransitRouteTypes
	 * @param routeType The selected route type
	 * @param routeNames The list of route names given the route type.
	 * @return The response result.
	 */
	public ResponseStatus getTransitRoutes(RouteType routeType,
										   ArrayList<String> routeNames)
	{
		mRouteType = routeType;
		
		Log.d("TransitBuddyModel", "getTransitRoutes - Processing transit routes");
		
		ArrayList<TransitRoute> routeList = 
			mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoutes(routeType);
		
		if(routeNames == null)
			routeNames = new ArrayList<String>();
		
		for(TransitRoute route : routeList)
		{
			routeNames.add(route.getName());
		}
		
		Log.d("TransitBuddyModel", "getTransitRoutes - Finished processing transit routes");
		
		return ResponseStatus.Completed;
	}
	
	/**
	 * 
	 * @note Must be called after calling getTransitRoutes
	 * @param routeID The selected route
	 * @param trips The list of trips given the route.
	 * @return The response result.
	 */
	public ResponseStatus getTransitTrips(String routeID, 
										  ArrayList<String> trips) throws IOException
	{
		ResponseStatus status = ResponseStatus.Failed;
		
		Log.d("TransitBuddyModel", "getTransitTrips - Creating CommandTransporter");

		CommandTransporter transporter = new CommandTransporter(mHostAddr, mPortNum, mTimeout);

		Log.d("TransitBuddyModel", "getTransitTrips - CommandTransporter created");

		setRouteID(routeID);
	
		// Go to the server to get the list of route types.
		GetTrips tripsCmd = new GetTrips(mSystemID, mRouteName);
		
		Log.d("TransitBuddyModel", "getTransitTrips - Sending GetTrips command");

		// Send the command and get the result.
		ResponseResult res = transporter.sendCommand(tripsCmd);

		Log.d("TransitBuddyModel", "getTransitTrips - Received GetTrips command response");

		if(res.getResponseStatus() == ResponseStatus.Completed)
		{
			Log.d("TransitBuddyModel", "getTransitTrips - Processing received transit trips");
			
			// Load the trips into our TransitData object
			TransitRoute transitRoute = (TransitRoute) res.getResultData();
			
			mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoute(mRouteType, mRouteID).setTrips(transitRoute.getTrips());
			
			// Now load the route type strings into the routeTypes array
			if(trips == null)
				trips = new ArrayList<String>();
			
			for(TransitTrip trip : transitRoute.getTrips())
				trips.add(trip.getName());
			
			Log.d("TransitBuddyModel", "getTransitTrips - Finished processing transit trips");
		}
		
		status = res.getResponseStatus();
		
		return status;
	}
	
	/**
	 * 
	 * @note Must be called after calling getTransitTrips
	 * @param tripID The selected trip
	 * @param stops The list of stops given the trip.
	 * @return The response result.
	 */
	public ResponseStatus getTransitStops(String tripID, 
										  ArrayList<TransitStop> stops, 
										  GetStops.ScheduleType type) throws IOException
	{
		ResponseStatus status = ResponseStatus.Failed;
		
		Log.d("TransitBuddyModel", "getTransitStops - Creating CommandTransporter");

		CommandTransporter transporter = new CommandTransporter(mHostAddr, mPortNum, mTimeout);
		
		Log.d("TransitBuddyModel", "getTransitStops - CommandTransporter created");

		mTripID = tripID;
		
		String tripHeadsign = 
			mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoute(mRouteType, mRouteID).getTrip(mTripID).getName();
		
		// Go to the server to get the list of route types.
		GetStops stopsCmd = new GetStops(mSystemID, 
									  	 mRouteName,
									  	 tripHeadsign,
										 type);
		
		Log.d("TransitBuddyModel", "getTransitStops - Sending GetStops command");

		// Send the command and get the result.
		ResponseResult res = transporter.sendCommand(stopsCmd);
		
		Log.d("TransitBuddyModel", "getTransitStops - Received GetStops command response");

		if(res.getResponseStatus() == ResponseStatus.Completed)
		{
			Log.d("TransitBuddyModel", "getTransitStops - Processing received transit stops");
			
			// Load the trips into our TransitData object
			TransitTrip transitTrip = (TransitTrip) res.getResultData();
			
			mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoute(mRouteType, mRouteID).getTrip(mTripID).setStops(transitTrip.getStops());
			
			// Now load the route type strings into the routeTypes array
			stops.addAll(mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoute(mRouteType, mRouteID).getTrip(mTripID).getStops());
			
			Log.d("TransitBuddyModel", "getTransitStops - Finished processing transit stops");
		}

		status = res.getResponseStatus();
		
		return status;
	}
	
	/**
	 * Gets the nearby stops.
	 * @param vicinityMeters The vicinity in meters.
	 * @param coordinates The coordinates of the user.
	 * @param maxStops The maximum number of stops to retrieve.
	 * @return The response result.
	 */
	public ResponseStatus getNearbyStops(int vicinityMeters, 
										 Coordinate coordinates, 
										 int maxStops,
										 ArrayList<TransitStop> nearbyStops) throws IOException
	{
		ResponseStatus status = ResponseStatus.Failed;
		
		Log.d("TransitBuddyModel", "getNearbyStops - Creating CommandTransporter");

		CommandTransporter transporter = new CommandTransporter(mHostAddr, mPortNum, mTimeout);
		
		Log.d("TransitBuddyModel", "getNearbyStops - CommandTransporter created");
		
		// Go to the server to get the list of route types.
		GetNearbyStops nearByStops = new GetNearbyStops(vicinityMeters,
														coordinates,
														maxStops);
		
		Log.d("TransitBuddyModel", "getNearbyStops - Sending GetNearbyStops command");

		// Send the command and get the result.
		ResponseResult res = transporter.sendCommand(nearByStops);
		
		Log.d("TransitBuddyModel", "getNearbyStops - Received GetNearbyStops command response");

		if(res.getResponseStatus() == ResponseStatus.Completed)
		{
			Log.d("TransitBuddyModel", "getNearbyStops - Processing received nearby transit stops");
			
			NearbyStops listOfNearbyStops = (NearbyStops) res.getResultData();
			
			nearbyStops.addAll(listOfNearbyStops.getStops());
			
			Log.d("TransitBuddyModel", "getNearbyStops - Finished processing nearby transit stops");
		}

		status = res.getResponseStatus();
		
		return status;
	}
}
