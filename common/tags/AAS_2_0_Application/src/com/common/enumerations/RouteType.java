package com.common.enumerations;

import java.io.Serializable;
import java.util.TreeMap;

public enum RouteType implements Serializable
{
	Tram("Tram", 0),
	Subway("Subway", 1),
	Rail("Rail", 2),
	Bus("Bus", 3),
	Ferry("Ferry", 4),
	CableCar("CableCar", 5),
	Gondola("Gondola", 6),
	Funicular("Funicular", 7);

	private static TreeMap<String, RouteType> sNameMap;
	private static TreeMap<Integer, RouteType> sValueMap;
	private String mName;
	private int mValue;

	RouteType(String name, int value)
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
		sValueMap = new TreeMap<Integer, RouteType>();
		sNameMap = new TreeMap<String, RouteType>();

		for (RouteType num : RouteType.values())
		{
			sValueMap.put(new Integer(num.getValue()), num);
			sNameMap.put(num.getName(), num);
		}
	}

	public static RouteType lookup(int value)
	{
		return sValueMap.get(new Integer(value));
	}

	public static RouteType lookup(String name)
	{
		return sNameMap.get(name);
	}
}