package com.transitbuddy.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.common.commands.GetStops;
import com.common.commands.GetStops.ScheduleType;
import com.common.commands.ICommand;
import com.common.types.Coordinate;
import com.common.types.TransitStop;
import com.common.types.TransitTrip;

public class RetrieveStopsQuery extends CommandableQuery
{
	private static final Logger LOGGER = Logger
  .getLogger(RetrieveStopsQuery.class);
	
	protected static final String STOP_NAME_COL = "stop_name";
	protected static final String STOP_ID_COL = "stop_id"; 
	protected static final String STOP_LAT_COL = "stop_lat";
	protected static final String STOP_LON_COL = "stop_lon";
	protected static final String STOP_DEPARTURE_TIME_COL = "departure_time";
	
	protected static final int DEFAULT_NUM_TIMES_PER_STOP = 1000;
	
	/** The agency identifier of the stops to be retrieved */
	private String mAgencyId;
	/** The route name of the stops to be retrieved */
	private String mRouteName;
	/** The trip headsign of the stops to be retrieved */
	private String mTripHeadsign;
	/** The number of times per stop to get */
	private int mNumTimesPerStop = DEFAULT_NUM_TIMES_PER_STOP;

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
   * @return the mTripHeadsign
   */
  public String getTripHeadsign()
  {
  	return mTripHeadsign;
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

	/**
   * @param tripHeadsign the mTripHeadsign to set
   */
  public void setTripHeadsign(String tripHeadsign)
  {
  	mTripHeadsign = tripHeadsign;
  }
  
  /**
   * @return the mNumTimesPerStop
   */
  public int getNumTimesPerStop()
  {
  	return mNumTimesPerStop;
  }

	/**
   * @param numTimesPerStop the mNumTimesPerStop to set
   */
  public void setmNumTimesPerStop(int numTimesPerStop)
  {
  	mNumTimesPerStop = numTimesPerStop;
  }

	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException
	{
		TransitTrip trip = null;

		// Get the trip headsign
		String tripHeadsign = getTripHeadsign();

		if (tripHeadsign == null)
		{
			LOGGER.error("Trip Headsign is null. Cannot execute get" +
			" stops SQL statement.");
			return null;
		}

		trip = new TransitTrip(tripHeadsign,tripHeadsign);
		
		String currStopName = "";
		String stopId = "";
		float stopLat = 0.0f;
		float stopLon = 0.0f;
		ArrayList<Date> stopTimes = new ArrayList<Date>();
		String stopName = "";

		while (rs.next())
		{
			// Get the stop name, id, lat, lon
			stopName = rs.getString(STOP_NAME_COL);
			if(!currStopName.equals(stopName))
			{
				// New stop, add previous stop
				if (currStopName != null && !currStopName.equals(""))
				{
					trip.addStop(new TransitStop(currStopName, stopId,
							new Coordinate(stopLat, stopLon), stopTimes));
				}
				currStopName = stopName;
				stopId = rs.getString(STOP_ID_COL);
				stopLat = rs.getFloat(STOP_LAT_COL);
				stopLon = rs.getFloat(STOP_LON_COL);
				stopTimes = new ArrayList<Date>();
			}
			try
      {
	      Time departureTime = rs.getTime(STOP_DEPARTURE_TIME_COL);
	      stopTimes.add(departureTime);
      }
      catch (SQLException e)
      {
	      LOGGER.warn("Got SQLException while reading the time. " 
	      		+ e.getMessage() + " Ignoring this time.");
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
		
		// Add the final stop
		if (currStopName != null && !currStopName.equals(""))
		{
			trip.addStop(new TransitStop(currStopName, stopId,
					new Coordinate(stopLat, stopLon), stopTimes));
		}
		
		return trip;
	}
	
	@Override
	public void reset()
	{
		setAgencyId(null);
		setTripHeadsign(null);
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
			    + " stops SQL statement.");
		}
		// Cast the command
		GetStops stopsCommand = (GetStops) command;
		
		// Set the number of times to get
		ScheduleType type = stopsCommand.getType();
		if (type.equals(ScheduleType.NEXT_FIVE))
		{
			setmNumTimesPerStop(5);
		}
		
		
		// Set the command's member values
		String agencyId = stopsCommand.getAgencyID();
		if (agencyId == null || agencyId.equals(""))
		{
			throw new IllegalArgumentException("Command agency id is null or blank." +
					" Cannot create get stops SQL statement.");
		}

		String routeName = stopsCommand.getRouteName();
		if (routeName == null || routeName.equals(""))
		{
			throw new IllegalArgumentException("Command route name is null or " +
					"blank. Cannot create get stops SQL statement.");
		}
		
		String tripHeadsign = stopsCommand.getTripHeadsign();
		if (tripHeadsign == null || tripHeadsign.equals(""))
		{
			throw new IllegalArgumentException("Command trip headsign is null or " +
					"blank. Cannot create get stops SQL statement.");
		}

		// Set the values in the query
		setAgencyId(agencyId);
		setTripHeadsign(tripHeadsign);
		setRouteName(routeName);
	}

}
