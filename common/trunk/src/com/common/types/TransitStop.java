package com.common.types;

import java.util.ArrayList;
import java.util.Date;

public class TransitStop extends TransitName
{
	private static final long serialVersionUID = -2256613482660417993L;
	private Coordinate mLocation;
	private ArrayList<Date> mStopTimes;

	/**
	 * Constructor that allows the name and the latitude and longitude
	 * 
	 * @param name
	 * @param id
	 * @param coords
	 */
	public TransitStop(String name, String id, Coordinate location,
	    ArrayList<Date> stopTimes)
	{
		super(name, id);

		mLocation = location;

		if (stopTimes == null)
			mStopTimes = new ArrayList<Date>();
		else
			mStopTimes = stopTimes;
	}

	/**
	 * Constructor that allows the name and the latitude and longitude
	 * 
	 * @param name
	 * @param id
	 */
	public TransitStop(String name, String id, Coordinate location)
	{
		this(name, id, location, null);
	}

	/**
	 * 
	 * @return
	 */
	public Coordinate getCoordinates()
	{
		return mLocation;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Date> getStopTimes()
	{
		return mStopTimes;
	}

	/**
	 * 
	 * @param stopTime
	 */
	public void addStopTime(Date stopTime)
	{
		mStopTimes.add(stopTime);
	}
}
