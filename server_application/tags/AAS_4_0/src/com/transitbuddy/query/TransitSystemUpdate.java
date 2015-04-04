package com.transitbuddy.query;

import com.common.types.TransitSystem;

public class TransitSystemUpdate extends Update
{
	private TransitSystem mTransitSystem;
	
	public TransitSystemUpdate()
	{
	}
	
	public TransitSystemUpdate(String stmt)
	{
		super(stmt);
	}

	/**
	 * Sets the transit information that will be stored
	 * @param info The transit information to be stored
	 */
	public void setTransitSystem(TransitSystem info)
	{
		mTransitSystem = info;
	}

}
