package com.transitbuddy.main.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.common.commands.CommandCode;
import com.common.commands.GetStops;
import com.common.commands.GetTrips;
import com.common.commands.ICommand;
import com.common.enumerations.RouteType;
import com.common.types.Coordinate;
import com.common.types.NearbyStops;
import com.common.types.TransitData;
import com.common.types.TransitRoute;
import com.common.types.TransitStop;
import com.common.types.TransitSystem;
import com.common.types.TransitTrip;

public class TestDataRequestProtocol extends TransitInfoRequestProtocol
{
	private static final Logger LOGGER = 
		Logger.getLogger(TransitInfoRequestProtocol.class);
	
	public TestDataRequestProtocol() throws FileNotFoundException,
	    ClassNotFoundException, SQLException, IOException
	{
		super();
	}
	
	/**
	 * Gets the requested data based on the given command's type
	 * @param command The command object specifying which data to retrieve
	 * @return The requested Serializable data. null is returned if the command's
	 *         type is INVALID_COMMAND, or if a SQLException is encountered.
	 */
	public Serializable processCommand(ICommand command)
	{
		Serializable output = null;
		CommandCode commandType = CommandCode.INVALID_COMMAND;
		if (command != null) 
		{
			commandType = command.getCommandCode();
		}

		if (commandType != CommandCode.INVALID_COMMAND)
		{
			switch(commandType)
			{
				case GET_NEARBY_STOPS:
					
					// returen the same stops no matter what
					NearbyStops stops = new NearbyStops("","");
					stops.addNearbyStop(new TransitStop("Alewife Station", "1", 
							new Coordinate(42.395428f, -71.142483f)));
					stops.addNearbyStop(new TransitStop("Davis Station", "2", 
							new Coordinate(42.395357f, -71.122571f)));
					stops.addNearbyStop(new TransitStop("Porter Square Station", "3", 
							new Coordinate(42.388400f, -71.119149f)));
					stops.addNearbyStop(new TransitStop("Harvard Station", "4", 
							new Coordinate(42.373362f, -71.118956f)));
					stops.addNearbyStop(new TransitStop("Central Square Station", "5", 
							new Coordinate(42.365486f, -71.103802f)));
					output = stops;
					break;
					
				case GET_TRANSIT_SYSTEM:
					// Create a transit data object and add a transit system to it
					TransitData data = new TransitData();
					
					// System = MBTA
					data.addTransitSystem(new TransitSystem("mbta","1"));
					output = data;
					break;
					
				case GET_STOPS:
					// Create a GetStops command from the given command
					GetStops stopsCommand = (GetStops)command;
					if (stopsCommand != null)
					{
						// Get the route ID
						String routeId = stopsCommand.getRouteID();
						if (routeId != null)
						{
							String tripId = stopsCommand.getTripID();
							if (tripId != null)
							{
								TransitTrip trip = new TransitTrip("", tripId);
								
								ArrayList<Date> stopTimes = new ArrayList<Date>();
								Calendar mCalendar = Calendar.getInstance();
								int routeIdInt = Integer.parseInt(routeId) + 5;
								
								for (int i = 1; i < 3; i++)
								{
									mCalendar.set(Calendar.HOUR, routeIdInt);
									mCalendar.set(Calendar.MINUTE, 0);
									mCalendar.set(Calendar.SECOND, 0);
									stopTimes.add(mCalendar.getTime());
									mCalendar.set(Calendar.MINUTE, 15);
									stopTimes.add(mCalendar.getTime());
									mCalendar.set(Calendar.MINUTE, 30);
									stopTimes.add(mCalendar.getTime());
									mCalendar.set(Calendar.MINUTE, 45);
									stopTimes.add(mCalendar.getTime());
									routeIdInt++;
								}
								
								// red line, Alewife
								if (routeId.equals("1") && tripId.equals("1"))
								{
									trip.addStop(new TransitStop("Alewife Station", "1", 
											new Coordinate(42.395428f, -71.142483f), stopTimes));
									trip.addStop(new TransitStop("Davis Station", "2", 
											new Coordinate(42.395357f, -71.122571f), stopTimes));
									trip.addStop(new TransitStop("Porter Square Station", "3", 
											new Coordinate(42.388400f, -71.119149f), stopTimes));
									trip.addStop(new TransitStop("Harvard Station", "4", 
											new Coordinate(42.373362f, -71.118956f), stopTimes));
									trip.addStop(new TransitStop("Central Square Station", "5", 
											new Coordinate(42.365486f, -71.103802f), stopTimes));
									output = trip;
								}
								// red line, Braintree
								else if (routeId.equals("1") && tripId.equals("1"))
								{
									trip.addStop(new TransitStop("Braintree Station", "1", 
											new Coordinate(42.207854f, -71.001138f), stopTimes));
									trip.addStop(new TransitStop("Quincy Adams Station", "2", 
											new Coordinate(42.23339f, -71.007153f), stopTimes));
									output = trip;
								}
								// green line, Lechmere
								else if (routeId.equals("2") && tripId.equals("3"))
								{
									trip.addStop(new TransitStop("Lechmere Station", "6", 
											new Coordinate(42.370772f, -71.076536f), stopTimes));
									trip.addStop(new TransitStop("Science Park Station", "7", 
											new Coordinate(42.366664f, -71.067666f), stopTimes));
									trip.addStop(new TransitStop("North Station", "8", 
											new Coordinate(42.36235f, -71.061122f), stopTimes));
									trip.addStop(new TransitStop("Haymarket Station", "9", 
											new Coordinate(42.363021f, -71.058290f), stopTimes));
									trip.addStop(new TransitStop("Government Center Station","10", 
											new Coordinate(42.358195f, -71.062276f), stopTimes));
									output = trip;
								}
								// green line, Boston College Station
								else if (routeId.equals("2") && tripId.equals("4"))
								{
									trip.addStop(new TransitStop("Boston College Station", "3", 
											new Coordinate(42.340081f, -71.166769f), stopTimes));
									trip.addStop(new TransitStop("South Street Station", "4", 
											new Coordinate(42.339531f, -71.157489f), stopTimes));
									output = trip;
								}
							}
							else
							{
								LOGGER.error("Cannot get stops. Trip ID in command was null.");
							}
						}
						else
						{
							LOGGER.error("Cannot get stops. Route ID in command was null.");
						}
					}
					else
					{
						LOGGER.error("Cannot get stops. Could not cast command to" +
								" a GetStops command.");
					}
					break;
					
				case GET_TRIPS:
					// Create a GetTrips command from the given command
					GetTrips tripsCommand = (GetTrips)command;
					if (tripsCommand != null)
					{
						// Get the route ID
						String routeId = tripsCommand.getRouteID();
						if (routeId != null)
						{
							TransitRoute route = new TransitRoute("",routeId,null);
							if (routeId.equals("1")) // red line
							{
								route.addTrip(new TransitTrip("Alewife","1"));
								route.addTrip(new TransitTrip("Braintree","2"));
								output = route;
							}
							else if (routeId.equals("2")) // green line
							{
								route.addTrip(new TransitTrip("Lechmere","3"));
								route.addTrip(new TransitTrip("Boston College Station","4"));
								output = route;
							}
						}
						else
						{
							LOGGER.error("Cannot get trips. Route ID in command was null.");
						}
					}
					else
					{
						LOGGER.error("Cannot get trips. Could not cast command to" +
								" a GetTrips command.");
					}
					break;
					
				case GET_ROUTES:
					// Create a Transit system object and add routes to it
					TransitSystem system = new TransitSystem("mbta","1");
					system.addRoute(RouteType.Subway, 
							new TransitRoute("Red Line", "1"));
					system.addRoute(RouteType.Subway, 
							new TransitRoute("Green Line", "2"));
					system.addRoute(RouteType.Subway, 
							new TransitRoute("Blue Line", "3"));
					system.addRoute(RouteType.Subway, 
							new TransitRoute("Orange Line", "4"));
					system.addRoute(RouteType.Subway, 
							new TransitRoute("Silver Line", "5"));
					output = system;
					break;
			}
		}

		return output;
	}

}
