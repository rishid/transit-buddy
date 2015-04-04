package com.transitbuddy.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.common.commands.GetTrips;
import com.common.commands.ICommand;
import com.common.types.TransitRoute;
import com.common.types.TransitTrip;

public class RetrieveTripsQuery extends CommandableQuery
{
	private static final Logger LOGGER = Logger
	    .getLogger(RetrieveTripsQuery.class);

	protected static final String TRIP_HEADSIGN_COL = "trip_headsign";
	protected static final String TRIP_ID_COL = "trip_id";
	protected static final String ROUTE_LONG_NAME_COL = "route_long_name";
	protected static final String ROUTE_SHORT_NAME_COL = "route_short_name";
	protected static final String ROUTE_ID_COL = "route_id";
	protected static final String ROUTE_TYPE_COL = "route_type";

	/** The agency identifier of the trips to be retrieved */
	private String mAgencyId;
	/** The route name of the trips to be retrieved */
	private String mRouteName;
	
	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException
	{
		TransitRoute route = null;
		
		while (rs.next())
		{
			// Check if the route has been initialized
			if (route == null)
			{
				// Get the route name
				String routeShortName = rs.getString(ROUTE_SHORT_NAME_COL);
				String routeLongName = rs.getString(ROUTE_LONG_NAME_COL);
				String routeId = rs.getString(ROUTE_ID_COL);
				int routeTypeInt = rs.getInt(ROUTE_TYPE_COL);
				
				if (routeLongName != null && routeTypeInt != 3)
				{
					route = new TransitRoute(routeLongName, routeId);
					LOGGER.info("New route: " + routeLongName + " " + routeId);
				}
				else
				{
					route = new TransitRoute(routeShortName, routeId);
					LOGGER.info("New route: " + routeShortName + " " + routeId);
				}				
			}
			if (route != null)
			{
				// Get the trip headsign and id
				String headsign = rs.getString(TRIP_HEADSIGN_COL);
				String tripId = rs.getString(TRIP_ID_COL);
				route.addTrip(new TransitTrip(headsign, tripId));
				LOGGER.info("Adding Trip: " + headsign + " " + tripId);
			}
		}
		try
		{
			rs.close();
		}
		catch (Exception e)
		{
			LOGGER.error("Got exception while closing result set. Exception: "
			    + e.getMessage());
		}
		
		LOGGER.info("Built a TransitRoute Object: name/id/trips_size " + route.getName() + "/" + route.getId() + "/" +
				route.getTrips().size());
		
		return route;
	}
	
	/**
   * @return the mAgencyId
   */
  public String getAgencyId()
  {
  	return mAgencyId;
  }

	/**
   * @return the mRouteName
   */
  public String getRouteName()
  {
  	return mRouteName;
  }

	/**
   * @param agencyId the mAgencyId to set
   */
  public void setAgencyId(String agencyId)
  {
  	mAgencyId = agencyId;
  }

	/**
   * @param routeName the mRouteName to set
   */
  public void setRouteName(String routeName)
  {
  	mRouteName = routeName;
  }

	@Override
	public void reset()
	{
		setAgencyId(null);
		setRouteName(null);
	}

	@Override
	public void validateCommandAndSetQueryValues(ICommand command)
	    throws IllegalArgumentException
	{
		// Ensure the command is not null
		if (command == null)
		{
			throw new IllegalArgumentException("Command is null. Cannot create get"
			    + " trips SQL statement.");
		}
		// Cast the command
		GetTrips tripsCommand = (GetTrips) command;

		// Set the command's member values
		String agencyId = tripsCommand.getAgencyID();
		if (agencyId == null || agencyId.equals(""))
		{
			throw new IllegalArgumentException("Command agency id is null or blank." +
					" Cannot create get trips SQL statement.");
		}

		String routeName = tripsCommand.getRouteName();
		if (routeName == null || routeName.equals(""))
		{
			throw new IllegalArgumentException("Command route name is null or " +
					"blank. Cannot create get trips SQL statement.");
		}

		// Set the values in the query
		setRouteName(routeName);
		setAgencyId(agencyId);
		
		LOGGER.info("Received valid GetTrips command: agencyId/routeName " + agencyId + "/" + routeName);
	}

}
