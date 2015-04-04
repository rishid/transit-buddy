package com.common.types;

import java.util.ArrayList;

public class NearbyStops extends TransitName
{
	private static final long serialVersionUID = -8430908519431146016L;
	private ArrayList<TransitStop> mStops;

	/**
	 * 
	 * 
	 * @param name
	 * @param lat
	 * @param lon
	 */
	public NearbyStops(String name, String id, ArrayList<TransitStop> stops)
	{
		super(name, id);

		if (stops == null)
		{
			mStops = new ArrayList<TransitStop>();
		}
		else
		{
			mStops = stops;
		}
	}

	/**
	 * 
	 * 
	 * @param name
	 */
	public NearbyStops(String name, String id)
	{
		this(name, id, null);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public void addNearbyStop(TransitStop stop)
	{
		mStops.add(stop);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public TransitStop getStop(int index)
	{
		if ((index > 0) && (index < mStops.size()))
		{
			return mStops.get(index);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Looks up the transit stop id give the transit stop name
	 * @param name The name of the transit stop
	 * @return The ID of the transit stop if found; else null
	 */
	public String lookup(String name)
	{
		String id = null;
		
		for(TransitStop stop : mStops)
		{
			if(stop.getName().compareToIgnoreCase(name) == 0)
			{
				id = stop.getId();
				break;
			}
		}
		
		return id;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public ArrayList<TransitStop> getStops()
	{
		return mStops;
	}
}
