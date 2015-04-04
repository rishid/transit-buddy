package com.transitbuddy.Model;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.types.*;
import com.common.utilities.ResponseResult;
import com.common.utilities.ResponseResult.ResponseStatus;
import com.common.commands.*;
import com.common.enumerations.*;
import com.common.connection.CommandTransporter;

import java.io.IOException;
import java.net.UnknownHostException;

import android.util.Log;

public class TransitBuddyModel 
{
	TransitData mTransitBuddyData = null;
	
	String 		mSystemID 	= null;
	RouteType 	mRouteType 	= null;
	String 		mRouteID 	= null;
	String 		mTripID 	= null;
	
	
	final int 		PORT_NUM 		= 1099;
	final int 		COMMAND_TIMEOUT = 5000;	// milliseconds
	final String 	HOST_ADDR 		= "129.10.128.235";
	

	/**
	 * Default Constructor
	 */
	public TransitBuddyModel()
	{
		mTransitBuddyData = new TransitData();
		
		mSystemID 	= null;
		mRouteType 	= null;
		mRouteID 	= null;
		mTripID 	= null;
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
	 * Sets currently active route ID.
	 * 
	 * @param systemID The route ID to set as active.
	 */
	public void setRouteID(String routeID)
	{
		mRouteID = routeID;
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
	 * Retrieves the available transit systems/cities from the server
	 * 
	 * @return A List containing all the supported transit buddy systems
	 */
	public ResponseStatus getTransitSystems(ArrayList<String> systems)
	{
		ResponseStatus status = ResponseStatus.Failed;
		
		try
		{
			Log.d("TransitBuddy Model getTransitSystems", "Creating CommandTransporter");
			
			CommandTransporter transporter = new CommandTransporter(HOST_ADDR, PORT_NUM, COMMAND_TIMEOUT);
			
			Log.d("TransitBuddy Model getTransitSystems", "CommandTransporter created");
			
			// Go to the server to get the list of transit systems.
			GetTransitSystems systemCmd = new GetTransitSystems();
			
			Log.d("TransitBuddy Model getTransitSystems", "Sending GetTransitSytems command");
			
			// Send the command and get the result.
			ResponseResult res = transporter.sendCommand(systemCmd);

			Log.d("TransitBuddy Model getTransitSystems", "Received GetTransitSystems command response");
			
			if(res.getResponseStatus() == ResponseStatus.Completed)
			{
				Log.d("TransitBuddy Model getTransitSystems", "Processing received transit systems");
				
				// Load the routes into our TransitData object
				TransitData transitData = (TransitData)(res.getResultData());
				
				mTransitBuddyData = new TransitData(transitData.getTransitSystems());
				
				// Now load the system string into the systems array
				if(systems == null)
					systems = new ArrayList<String>();
			
				for(TransitSystem system : mTransitBuddyData.getTransitSystems())
					systems.add(system.getName());

				Log.d("TransitBuddy Model getTransitSystems", "Finished processing transit systems");
			}
			
			status = res.getResponseStatus();
		}
		catch(IOException ioe)
		{
			//TODO Handle the ioe exception; Log it somewhere?
		}
		
		return status;
	}
	
	public ResponseStatus getTransitRouteTypes(String systemName,
											   ArrayList<String> routeTypes)
	{
		ResponseStatus status = ResponseStatus.Failed;
		
		try
		{
			Log.d("TransitBuddy Model getTransitRouteTypes", "Creating CommandTransporter");
			
			CommandTransporter transporter = new CommandTransporter(HOST_ADDR, PORT_NUM, COMMAND_TIMEOUT);
			
			Log.d("TransitBuddy Model getTransitRouteTypes", "CommandTransporter created");
			
			String systemID = mTransitBuddyData.lookup(systemName);
			
			mSystemID = systemID;
			
			// Go to the server to get the list of route types.
			GetRoutes routesCmd = new GetRoutes(mSystemID);
			
			Log.d("TransitBuddy Model getTransitRouteTypes", "Sending GetRoutes command");
			
			// Send the command and get the result.
			ResponseResult res = transporter.sendCommand(routesCmd);
			
			Log.d("TransitBuddy Model getTransitRouteTypes", "Received GetRoutes command response");
			
			if(res.getResponseStatus() == ResponseStatus.Completed)
			{
				Log.d("TransitBuddy Model getTransitRouteTypes", "Processing received route types");
				
				// Load the routes into our TransitData object
				TransitSystem transitSystem = (TransitSystem) res.getResultData();
				
				mTransitBuddyData.getTransitSystem(mSystemID).setTransitRoutes(transitSystem.getTransitRouteHashMap());
				
				// Now load the route type strings into the routeTypes array
				if(routeTypes == null)
					routeTypes = new ArrayList<String>();
				
				for(RouteType routeType : mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoutesTypes())
					routeTypes.add(routeType.getName());
				
				Log.d("TransitBuddy Model getTransitRouteTypes", "Finished processing route types");
			}
			
			status = res.getResponseStatus();
		}
		catch(IOException ioe)
		{
			//TODO Handle the ioe exception; Log it somewhere?
		}
		
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
		
		Log.d("TransitBuddy Model getTransitRoutes", "Processing transit routes");
		
		ArrayList<TransitRoute> routeList = 
			mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoutes(routeType);
		
		if(routeNames == null)
			routeNames = new ArrayList<String>();
		
		for(TransitRoute route : routeList)
		{
			routeNames.add(route.getName());
		}
		
		Log.d("TransitBuddy Model getTransitRoutes", "Finished processing transit routes");
		
		return ResponseStatus.Completed;
	}
	
	/**
	 * 
	 * @note Must be called after calling getTransitRoutes
	 * @param routeID The selected route
	 * @param trips The list of trips given the route.
	 * @return The response result.
	 */
	public ResponseStatus getTransitTrips(String routeID, ArrayList<String> trips)
	{
		ResponseStatus status = ResponseStatus.Failed;
		
		try
		{
			Log.d("TransitBuddy Model getTransitTrips", "Creating CommandTransporter");

			CommandTransporter transporter = new CommandTransporter(HOST_ADDR, PORT_NUM, COMMAND_TIMEOUT);

			Log.d("TransitBuddy Model getTransitTrips", "CommandTransporter created");

			mRouteID = routeID;
		
			// Go to the server to get the list of route types.
			GetTrips tripsCmd = new GetTrips(mSystemID, mRouteID);
			
			Log.d("TransitBuddy Model getTransitTrips", "Sending GetTrips command");

			// Send the command and get the result.
			ResponseResult res = transporter.sendCommand(tripsCmd);

			Log.d("TransitBuddy Model getTransitTrips", "Received GetTrips command response");

			if(res.getResponseStatus() == ResponseStatus.Completed)
			{
				Log.d("TransitBuddy Model getTransitTrips", "Processing received transit trips");
				
				// Load the trips into our TransitData object
				TransitRoute transitRoute = (TransitRoute) res.getResultData();
				
				mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoute(mRouteType, mRouteID).setTrips(transitRoute.getTrips());
				
				// Now load the route type strings into the routeTypes array
				if(trips == null)
					trips = new ArrayList<String>();
				
				for(TransitTrip trip : transitRoute.getTrips())
					trips.add(trip.getName());
				
				Log.d("TransitBuddy Model getTransitTrips", "Finished processing transit trips");
			}
			
			status = res.getResponseStatus();
		}
		catch(IOException ioe)
		{
			//TODO Handle the ioe exception; Log it somewhere?
		}
		
		return status;
	}
	
	/**
	 * 
	 * @note Must be called after calling getTransitTrips
	 * @param tripID The selected trip
	 * @param stops The list of stops given the trip.
	 * @return The response result.
	 */
	public ResponseStatus getTransitStops(String tripID, ArrayList<TransitStop> stops)
	{
		ResponseStatus status = ResponseStatus.Failed;
		
		try
		{
			Log.d("TransitBuddy Model getTransitStops", "Creating CommandTransporter");

			CommandTransporter transporter = new CommandTransporter(HOST_ADDR, PORT_NUM, COMMAND_TIMEOUT);
			
			Log.d("TransitBuddy Model getTransitStops", "CommandTransporter created");

			mTripID = tripID;
			
			// Go to the server to get the list of route types.
			GetStops stopsCmd = new GetStops(mSystemID, 
										  	 mRouteID,
											 mTripID,
											 GetStops.ScheduleType.ALL_TIMES);
			
			Log.d("TransitBuddy Model getTransitStops", "Sending GetStops command");

			// Send the command and get the result.
			ResponseResult res = transporter.sendCommand(stopsCmd);
			
			Log.d("TransitBuddy Model getTransitStops", "Received GetStops command response");

			if(res.getResponseStatus() == ResponseStatus.Completed)
			{
				Log.d("TransitBuddy Model getTransitStops", "Processing received transit stops");
				
				// Load the trips into our TransitData object
				TransitTrip transitTrip = (TransitTrip) res.getResultData();
				
				mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoute(mRouteType, mRouteID).getTrip(mTripID).setStops(transitTrip.getStops());
				
				// Now load the route type strings into the routeTypes array
				stops.addAll(mTransitBuddyData.getTransitSystem(mSystemID).getTransitRoute(mRouteType, mRouteID).getTrip(mTripID).getStops());
				
				Log.d("TransitBuddy Model getTransitStops", "Finished processing transit stops");
			}

			status = res.getResponseStatus();
		}
		catch(IOException ioe)
		{
			//TODO Handle the ioe exception; Log it somewhere?
		}
		
		return status;
	}
}
