package com.transitbuddy.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.common.commands.GetStops;
import com.common.commands.ICommand;
import com.common.types.Coordinate;
import com.common.types.TransitStop;
import com.common.types.TransitTrip;
import com.transitbuddy.main.server.TransitInfoServerRunner;
import com.transitbuddy.main.updator.TransitInfoUpdatorRunner;

public class RetrieveRealTimeStopsQuery extends CommandableQuery
{
	private static final Logger LOGGER = Logger
	    .getLogger(RetrieveRealTimeStopsQuery.class);

	protected static final String STOP_NAME_COL = "stop_name";
	protected static final String STOP_ID_COL = "stop_id";
	protected static final String STOP_LAT_COL = "stop_lat";
	protected static final String STOP_LON_COL = "stop_lon";
	protected static final String STOP_ARRIVAL_TIME1_COL = "arrival_time1";
	protected static final String STOP_ARRIVAL_TIME2_COL = "arrival_time2";
	protected static final String STOP_ARRIVAL_TIME3_COL = "arrival_time3";
	protected static final String STOP_ARRIVAL_TIME4_COL = "arrival_time4";
	protected static final String STOP_ARRIVAL_TIME5_COL = "arrival_time5";

	private String mAgencyId;
	private String mRouteName;
	private String mTripHeadsign;

	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException
	{
		TransitTrip trip = null;

		// Get the trip headsign
		String tripHeadsign = getTripHeadsign();

		if (tripHeadsign == null)
		{
			LOGGER.error("Trip Headsign is null. Cannot execute get"
			    + " stops SQL statement.");
			return null;
		}

		trip = new TransitTrip(tripHeadsign, tripHeadsign);

		String stopId = "";
		float stopLat = 0.0f;
		float stopLon = 0.0f;
		ArrayList<Date> stopTimes = new ArrayList<Date>();
		String stopName = "";

		while (rs.next())
		{
			// Get the stop name, id, lat, lon
			stopName = rs.getString(STOP_NAME_COL);

			stopId = rs.getString(STOP_ID_COL);
			stopLat = rs.getFloat(STOP_LAT_COL);
			stopLon = rs.getFloat(STOP_LON_COL);
			stopTimes = new ArrayList<Date>();

			// Get the stop times
			try
			{
				getTimes(rs, STOP_ARRIVAL_TIME1_COL, stopTimes);
			}
			catch (Exception e)
			{

			}
			try
			{
				getTimes(rs, STOP_ARRIVAL_TIME2_COL, stopTimes);
			}
			catch (Exception e)
			{

			}
			try
			{
				getTimes(rs, STOP_ARRIVAL_TIME3_COL, stopTimes);
			}
			catch (Exception e)
			{

			}
			try
			{
				getTimes(rs, STOP_ARRIVAL_TIME4_COL, stopTimes);
			}
			catch (Exception e)
			{

			}
			try
			{
				getTimes(rs, STOP_ARRIVAL_TIME5_COL, stopTimes);
			}
			catch (Exception e)
			{

			}

			trip.addStop(new TransitStop(stopName, stopId, new Coordinate(stopLat,
			    stopLon), stopTimes));
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

		return trip;
	}

	private void getTimes(ResultSet rs, String colName, ArrayList<Date> stopTimes)
	    throws SQLException
	{
		Long arrivalTime = rs.getLong(colName);
		if (arrivalTime != null)
		{
			stopTimes.add(new Date(arrivalTime));
		}
	}

	@Override
	public void validateCommandAndSetQueryValues(ICommand command)
	    throws IllegalArgumentException
	{
		GetStops stopsCommand = (GetStops) command;

		// Set the command's member values
		String agencyId = stopsCommand.getAgencyID();
		if (agencyId == null || agencyId.equals(""))
		{
			throw new IllegalArgumentException("Command agency id is null or blank."
			    + " Cannot create get stops SQL statement.");
		}

		String routeName = stopsCommand.getRouteName();
		if (routeName == null || routeName.equals(""))
		{
			throw new IllegalArgumentException("Command route name is null or "
			    + "blank. Cannot create get stops SQL statement.");
		}

		String tripHeadsign = stopsCommand.getTripHeadsign();
		if (tripHeadsign == null || tripHeadsign.equals(""))
		{
			throw new IllegalArgumentException("Command trip headsign is null or "
			    + "blank. Cannot create get stops SQL statement.");
		}
		TransitInfoUpdatorRunner updator = TransitInfoServerRunner.getsUpdator();
		if (updator != null
		    && !updator.isRealtimeAvailable(agencyId, routeName, tripHeadsign))
		{
			throw new IllegalArgumentException("Real time stop information is not"
			    + " available for agency ID:" + agencyId + ", Route Name: "
			    + routeName + ", and Trip Headsign: " + tripHeadsign);
		}
		setAgencyId(agencyId);
		setRouteName(routeName);
		setTripHeadsign(tripHeadsign);
	}

	@Override
	public void reset()
	{
		setAgencyId(null);
		setTripHeadsign(null);
		setRouteName(null);
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
	 * @return the mTripHeadsign
	 */
	public String getTripHeadsign()
	{
		return mTripHeadsign;
	}

	/**
	 * @param agencyId
	 *          the mAgencyId to set
	 */
	public void setAgencyId(String agencyId)
	{
		mAgencyId = agencyId;
	}

	/**
	 * @param routeName
	 *          the mRouteName to set
	 */
	public void setRouteName(String routeName)
	{
		mRouteName = routeName;
	}

	/**
	 * @param tripHeadsign
	 *          the mTripHeadsign to set
	 */
	public void setTripHeadsign(String tripHeadsign)
	{
		mTripHeadsign = tripHeadsign;
	}
}
