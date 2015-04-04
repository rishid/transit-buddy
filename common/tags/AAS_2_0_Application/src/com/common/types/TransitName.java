package com.common.types;

import java.io.Serializable;

public class TransitName implements Serializable
{
	private static final long serialVersionUID = -3742109068605318963L;
	private String mName;
	private String mId;

	/**
	 * 
	 * 
	 * @param name
	 */
	public TransitName(String name, String id)
	{
		mName = name;
		mId = id;
	}

	/**
	 * 
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		mName = name;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getName()
	{
		return mName;
	}

	/**
	 * 
	 * 
	 * @param id
	 */
	public void setId(String id)
	{
		mId = id;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getId()
	{
		return mId;
	}
}
