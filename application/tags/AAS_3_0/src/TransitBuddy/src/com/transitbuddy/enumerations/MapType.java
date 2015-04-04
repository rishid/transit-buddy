package com.transitbuddy.enumerations;

import java.util.TreeMap;

import com.transitbuddy.enumerations.MapType;

public enum MapType {
	
	NearbyStops("NearbyStops", 0),
	TransitTrip("TransitTrip", 1);
	
	private static TreeMap<String, MapType> sNameMap;
	private static TreeMap<Integer, MapType> sValueMap;
	private String mName;
	private int mValue;

	MapType(String name, int value)
	{
		mName = name;
		mValue = value;
	}

	@Override
	public String toString()
	{
		return mName;
	}

	public String getName()
	{
		return mName;
	}

	public int getValue()
	{
		return mValue;
	}

	static
	{
		sValueMap = new TreeMap<Integer, MapType>();
		sNameMap = new TreeMap<String, MapType>();

		for (MapType num : MapType.values())
		{
			sValueMap.put(new Integer(num.getValue()), num);
			sNameMap.put(num.getName(), num);
		}
	}

	public static MapType lookup(int value)
	{
		return sValueMap.get(new Integer(value));
	}

	public static MapType lookup(String name)
	{
		return sNameMap.get(name);
	}

}
