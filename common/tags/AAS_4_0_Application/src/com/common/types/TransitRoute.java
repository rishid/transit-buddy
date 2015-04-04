package com.common.types;

import java.util.ArrayList;


public class TransitRoute extends TransitName
{
	private static final long serialVersionUID = -2441890509088336623L;
	private ArrayList<TransitTrip> mTrips;

	/**
	 * 
	 * @param name
	 * @param id
	 * @param trips
	 */
	public TransitRoute(String name, String id, ArrayList<TransitTrip> trips)
	{
		super(name, id);

		if (trips == null)
		{
			mTrips = new ArrayList<TransitTrip>();
		}
		else
		{
			mTrips = trips;
		}
	}

	/**
	 * 
	 * @param name
	 */
	public TransitRoute(String name, String id)
	{
		this(name, id, null);
	}

	/**
	 * 
	 * @param trip
	 */
	public void addTrip(TransitTrip trip)
	{
		mTrips.add(trip);
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<TransitTrip> getTrips()
	{
		return mTrips;
	}
	
	public void setTrips(ArrayList<TransitTrip> trips)
	{
		mTrips = trips;
	}
	
	public TransitTrip getTrip(String tripID)
	{
		for(TransitTrip trip : mTrips)
		{
			if(trip.getId().compareToIgnoreCase(tripID) == 0)
				return trip;
		}
		
		return null;
	}
}
