package com.transitbuddy.query;

import com.common.types.TransitSystem;

public class InsertTransitSystemUpdate extends Update 
{
	/** The transit system information to be inserted */
	protected TransitSystem mInfo;
	
	/**
	 * Default Constructor
	 */
	public InsertTransitSystemUpdate() 
	{
		mInfo = null;
	}
	public InsertTransitSystemUpdate(TransitSystem info) 
	{
		mInfo = info;
	}
	
	public void setTransitSystem(TransitSystem info)
	{
		mInfo = info;
	}

}
