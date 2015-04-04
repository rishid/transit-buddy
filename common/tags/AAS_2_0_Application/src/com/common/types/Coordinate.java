package com.common.types;

import java.io.Serializable;

public class Coordinate implements Serializable
{
  private static final long serialVersionUID = -3748126973873924638L;
	private float mLat = 0.0f;
	private float mLon = 0.0f;

	/**
	 * Coordinate constructor
	 * 
	 * @param lat
	 * @param lon
	 */
	public Coordinate(float lat, float lon)
	{
		mLat = lat;
		mLon = lon;
	}

	public float getLatitude()
	{
		return mLat;
	}

	public float getLongitude()
	{
		return mLon;
	}

	public void setLatitude(float lat)
	{
		mLat = lat;
	}

	public void setLongitude(float lon)
	{
		mLon = lon;
	}
}