package com.transitbuddy.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.common.commands.GetNearbyStops;
import com.common.commands.ICommand;
import com.common.types.Coordinate;
import com.common.types.NearbyStops;
import com.common.types.TransitStop;

public class RetrieveNearbyStopsQuery extends CommandableQuery
{
	private static final Logger LOGGER = Logger
	    .getLogger(RetrieveNearbyStopsQuery.class);
	
	protected static final String STOP_ID_COL = "stop_id";
	protected static final String STOP_NAME_COL = "stop_name";
	protected static final String STOP_LAT_COL = "stop_lat";
	protected static final String STOP_LON_COL = "stop_lon";
	protected static final String STOP_DISTANCE_COL = "distance";
	protected static final String STOP_DEPARTURE_TIME_COL = "departure_time";

	/** The default maximum number of stops to retrieve from the database */
	public static final int DEFAULT_MAX_STOPS = 5;
	/** Indicates an invalid max number of stops to be retrieved from the db */
	public static final int INVALID_MAX_STOPS = -1;
	/**
	 * Indicates an invalid vicinity, where the vicinity is the number of meters
	 * around the user's coordinate within which to search for stops
	 */
	public static final int INVALID_VICINITY = -1;

	/** The coordinate of the users's location */
	private Coordinate mCoord;
	/** The maximum number of stops to retrieve */
	private int mMaxStops;
	/**
	 * The number of meters around the user's coordinate within which to search
	 * for stops
	 */
	private int mVicinityMeters;

	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException
	{
		NearbyStops stops = new NearbyStops("","");
		
		int maxStops = getMaxStops();
		if (maxStops == INVALID_MAX_STOPS)
		{
			maxStops = DEFAULT_MAX_STOPS;
			LOGGER.warn("Max stops is invalid. Using default number of max stops: "
					+ DEFAULT_MAX_STOPS+ ".");
		}
		
		while (rs.next())
		{
			try
      {
				// Get the stop name, id, lat, lon, and departure time
	      Time departureTime = rs.getTime(STOP_DEPARTURE_TIME_COL);
	      String stopName = rs.getString(STOP_NAME_COL);
				String stopId = rs.getString(STOP_ID_COL);
				float stopLat = rs.getFloat(STOP_LAT_COL);
				float stopLon = rs.getFloat(STOP_LON_COL);
				addStop(stops, stopName, stopId, stopLat, stopLon, departureTime,
							maxStops);
      }
      catch (SQLException e)
      {
	      LOGGER.error("Got SQLException while reading the time. " 
	      		+ e.getMessage());
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
		
		return stops;
	}
	

	/**
	 * @return the mCoord
	 */
	public Coordinate getCoord()
	{
		return mCoord;
	}

	/**
	 * @return the mMaxStops
	 */
	public int getMaxStops()
	{
		return mMaxStops;
	}

	/**
	 * @return the mVicinityMeters
	 */
	public int getVicinityMeters()
	{
		return mVicinityMeters;
	}

	/**
	 * @param coord
	 *          the mCoord to set
	 */
	protected void setCoord(Coordinate coord)
	{
		mCoord = coord;
	}

	/**
	 * @param maxStops
	 *          the mMaxStops to set
	 */
	protected void setMaxStops(int maxStops)
	{
		mMaxStops = maxStops;
	}

	/**
	 * @param vicinityMeters
	 *          the mVicinityMeters to set
	 */
	protected void setVicinityMeters(int vicinityMeters)
	{
		mVicinityMeters = vicinityMeters;
	}

	@Override
	public void reset()
	{
		setVicinityMeters(INVALID_VICINITY);
		setMaxStops(INVALID_MAX_STOPS);
		setCoord(null);
	}

	@Override
	public void validateCommandAndSetQueryValues(ICommand command)
	    throws IllegalArgumentException
	{
		// Ensure the command is not null
		if (command == null)
		{
			throw new IllegalArgumentException("Command is null. Cannot create get"
			    + " nearby stops SQL statement.");
		}
		// Cast the command
		GetNearbyStops nearbyStopsCommand = (GetNearbyStops) command;
		
		// Set the command's member values
		Coordinate coord = nearbyStopsCommand.getCoordinate();
		if (coord == null)
		{
			throw new IllegalArgumentException("Command coordinate is null. Cannot"
			    + " create get nearby stops SQL statement.");
		}

		int maxStops = nearbyStopsCommand.getMaxStops();
		if (maxStops == INVALID_MAX_STOPS || maxStops <= 0)
		{
			throw new IllegalArgumentException("Command max stops is invalid. "
			    + "Cannot create get nearby stops SQL statement.");
		}

		int vicinity = nearbyStopsCommand.getVicinityMeters();
		if (vicinity == INVALID_VICINITY || vicinity < 0)
		{
			throw new IllegalArgumentException("Command vicinity is invalid. "
			    + "Cannot create get nearby stops SQL statement.");
		}

		// Set the values in the query
		setCoord(coord);
		setMaxStops(maxStops);
		setVicinityMeters(vicinity);
	}	
	
	/**
	 * Adds the a new TransitStop with the given name, ID, coordinate, and
	 * departure time if a stop with the same name does not already exist
	 * in the stops object and the max number of stops has not already been met.
	 * 
	 * Adds the departure time to the end of the list of stop times in the stop
	 * with the given name if the stop time does not already exist in the list of
	 * stops.
	 * 
	 * The given name, id, and time must not be null in order to add a stop or
	 * time.
	 * 
	 * @param stops NearbyStops object to contain the new stop if it meets all
	 *  requirements
	 * @param stopName The name of the stop to be added
	 * @param stopId The id of the stop to be added
	 * @param stopLat The latitude in decimal degrees of the stop to be added
	 * @param stopLonThe longitude in decimal degrees of the stop to be added
	 * @param departureTime The departure time to be added to the stop's list
	 *  of times
	 * @param maxStops The maximum number of stops to allow in the nearby stops
	 *  object
	 */
	private static void addStop(NearbyStops stops, String stopName,
			String stopId, float stopLat, float stopLon, Time departureTime,
			int maxStops)
	{
		// Return right away if the max number of stops is already present in the
		// stops object
		if (stops != null && stops.getStops() != null)
		{
			if(stops.getStops().size() >= maxStops)
			{
				return;
			}
		}
		// Validate the inputs
		if (stopName != null && stopId != null && 
				departureTime != null)
		{
			// Initialize variables
			if (stops == null)
			{
				// Initialize the stops if it's null
				stops = new NearbyStops("","");
			}
			ArrayList<TransitStop> stopList = stops.getStops();
			if (stopList == null)
			{
				// Initialize the stop list if it's null
				stopList = new ArrayList<TransitStop>();
			}

			boolean bFoundStop = false;
			for (TransitStop stop : stopList)
			{
				// Determine if the stop name is already in the stops list
				if (stop != null && stop.getName().equals(stopName))
				{
					bFoundStop = true;
					ArrayList<Date> stopTimes = stop.getStopTimes();
					if (stopTimes == null)
					{
						// Initialize the stop times list
						stopTimes = new ArrayList<Date>();
					}

					// Determine if the departure time is already in the stop list
					boolean bFoundTime = false;
					for (Date date : stopTimes)
					{
						if (date != null && date.equals(departureTime))
						{
							bFoundTime = true;
							break;
						}
					}
					// If the time wasn't found in the list, add it
					if (!bFoundTime)
					{
						stopTimes.add(departureTime);
					}
					break;

				}
			}

			// If the stop wasn't in the list and we are not at the max number of
			// stops, add it
			if (!bFoundStop && 
					(stopList.size() < maxStops))
			{
				ArrayList<Date> times = new ArrayList<Date>();
				times.add(departureTime);
				stopList.add(new TransitStop(stopName, stopId, 
						new Coordinate(stopLat, stopLon), times));
			}
		}
		
	}
	
	public static void main (String[] args)
	{
		NearbyStops stops = new NearbyStops("","");
		final int maxStops = 2;
		final String[] stopNames = {"Stop 1", "Stop 2", "Stop 3"};
		final String[] stopIds = {"Stop 1","Stop 2","Stop 3"};
		final int fiveAmMilliseconds = 1000 * 60 * 60 * 5;
		final Time[] times = {new Time(fiveAmMilliseconds),
				new Time(fiveAmMilliseconds + 5000),
				new Time(fiveAmMilliseconds + 10000)};
		
		// Stop 1
		RetrieveNearbyStopsQuery.addStop(stops, stopNames[0], stopIds[0],
				0.0f, 0.0f, times[0], maxStops);
		
		// Ensure there is one stop in the stops
		if (stops.getStops().size() != 1)
		{
			System.out.println("Adding new stop test failed!");
			System.exit(1);
		}
		
		// Try adding a duplicate of the same stop
		RetrieveNearbyStopsQuery.addStop(stops, stopNames[0], stopIds[0],
				0.0f, 0.0f, times[0], maxStops);

		// Ensure there is one stop in the stops, and there is only one time
		// in the times
		if (stops.getStops().size() != 1 &&
				stops.getStop(0).getStopTimes().size() != 1)
		{
			System.out.println("Adding duplicate time test failed!");
			System.exit(1);
		}

		// Try adding another time to Stop 1
		RetrieveNearbyStopsQuery.addStop(stops, stopNames[0], stopIds[0],
				0.0f, 0.0f, times[1], maxStops);

		// Ensure there is one stop in the stops, and there are 2 time
		// in the times
		if (stops.getStops().size() != 1 &&
				stops.getStop(0).getStopTimes().size() != 2)
		{
			System.out.println("Adding second time test failed!");
			System.exit(1);
		}
		
		// Add a new stop to the list
		RetrieveNearbyStopsQuery.addStop(stops, stopNames[1], stopIds[1],
				0.0f, 0.0f, times[0], maxStops);

		// Ensure there is one stop in the stops, and there are 2 times
		// in the first stop's times list, and there is 1 time in the second stop's
		// time list
		if (stops.getStops().size() != 2 &&
				stops.getStop(0).getStopTimes().size() != 2 &&
				stops.getStop(1).getStopTimes().size() != 1)
		{
			System.out.println("Adding second stop test failed!");
			System.exit(1);
		}
		
		// Add a new stop to the list
		RetrieveNearbyStopsQuery.addStop(stops, stopNames[2], stopIds[2],
				0.0f, 0.0f, times[0], maxStops);

		// Ensure the new stop was not added since we have reached the maxStops
		if (stops.getStops().size() != 2 &&
				stops.getStop(0).getStopTimes().size() != 2 &&
				stops.getStop(1).getStopTimes().size() != 1)
		{
			System.out.println("Max Stops test failed!");
			System.exit(1);
		}

		System.out.println("All passed!");
	}

}
