package com.common.types;

import java.io.Serializable;
import java.util.ArrayList;

public class TransitData implements Serializable
{
	private static final long serialVersionUID = 1L;
	private ArrayList<TransitSystem> mTransitSystems;

	/**
	 * 
	 * @param transitSystems
	 */
	public TransitData(ArrayList<TransitSystem> transitSystems)
	{
		if (transitSystems == null)
		{
			mTransitSystems = new ArrayList<TransitSystem>();
		}
		else
		{
			mTransitSystems = transitSystems;
		}
	}

	/**
	 * 
	 * @param name
	 * @param id
	 */
	public TransitData()
	{
		mTransitSystems = new ArrayList<TransitSystem>();
	}

	/**
	 * 
	 * 
	 * @return The transit systems that are supported by TransitBuddy.
	 */
	public ArrayList<String> getTransitSystemStrings()
	{
		ArrayList<String> transitSystems = new ArrayList<String>();

		for (TransitSystem system : mTransitSystems)
		{
			transitSystems.add(system.getName());
		}

		return transitSystems;
	}
	
	/**
	 * 
	 * 
	 * @return The transit systems that are supported by TransitBuddy.
	 */
	public ArrayList<TransitSystem> getTransitSystems()
	{
		return mTransitSystems;
	}
	
	/**
	 * Looks up the transit system id give the transit system name
	 * @param name The name of the transit system
	 * @return The ID of the transit system if found; else null
	 */
	public String lookup(String name)
	{
		String id = null;
		
		for(TransitSystem system : mTransitSystems)
		{
			if(system.getName().compareToIgnoreCase(name) == 0)
			{
				id = system.getId();
				break;
			}
		}
		
		return id;
	}

	/**
	 * 
	 * @param system
	 */
	public void addTransitSystem(TransitSystem system)
	{
		mTransitSystems.add(system);
	}
	
	public TransitSystem getTransitSystem(String systemID)
	{
		for(TransitSystem system : mTransitSystems)
		{
			if(system.getId().compareToIgnoreCase(systemID) == 0)
				return system;
		}
		
		return null;
	}
}
