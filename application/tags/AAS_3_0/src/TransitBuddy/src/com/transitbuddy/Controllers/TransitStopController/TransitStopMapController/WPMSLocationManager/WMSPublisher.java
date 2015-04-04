package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;

import java.util.LinkedList;
import java.util.List;

public abstract class WMSPublisher
{
	private List<WMSSubscriber> subscribers;
	protected MapData currentMapData;
	
	public WMSPublisher()
	{
		subscribers = new LinkedList<WMSSubscriber>();
		currentMapData = new MapData();
	}
	
	/**
	 * Add a subscriber to the subscriber list
	 * @param subscriber
	 */
	public void subscribe(WMSSubscriber subscriber)
	{
		// If this subscriber doesn't exist already, add it and call its update
		if (!subscribers.contains(subscriber))
		{
			subscribers.add(subscriber);
			subscriber.update(currentMapData);
		}
	}
	
	/**
	 * Remove a subscriber from the subscriber list
	 * @param subscriber
	 */
	public void unsubscribe(WMSSubscriber subscriber)
	{
		subscribers.remove(subscriber);
	}
	
	
	/**
	 * Update subscribers with new map data
	 * @param mapData
	 * TODO: Refactor away from passed parameter
	 */
	protected void publish(MapData mapData)
	{
		for (WMSSubscriber subscriber : subscribers)
		{
			subscriber.update(currentMapData);
		}
	}

}
