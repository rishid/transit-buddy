package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;

import java.util.LinkedList;
import java.util.List;

public abstract class WPSPublisher
{
	private List<WPSSubscriber> subscribers;
	protected WPMSLocation currentLocation;
	
	public WPSPublisher()
	{
		subscribers = new LinkedList<WPSSubscriber>();
		currentLocation = null;
	}
	
	/**
	 * Add a subscriber to the subscriber list
	 * @param subscriber
	 */
	public void subscribe(WPSSubscriber subscriber)
	{
		// If this subscriber doesn't exist already, add it and call its update
		if (!subscribers.contains(subscriber))
		{
			subscribers.add(subscriber);
			//subscriber.update(currentPositionData);
		}
	}
	
	/**
	 * Remove a subscriber from the subscriber list
	 * @param subscriber
	 */
	public void unsubscribe(WPSSubscriber subscriber)
	{
		subscribers.remove(subscriber);
	}
	
	
	/**
	 * Update subscribers with new position data
	 * @param positionData
	 */
	protected void publish(WPMSLocation location)
	{
		for (WPSSubscriber subscriber : subscribers)
		{
			subscriber.update(currentLocation);
		}
	}


}
