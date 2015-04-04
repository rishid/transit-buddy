package com.transitbuddy.parsing;

public class TransitSystemParserFactory
{
	public static TransitSystemParser getParserByType(String type)
	{
		TransitSystemParser parser = null;
		if (type == null || type.equals(""))
		{
			throw new IllegalArgumentException("Cannot create transit info parser." +
					"Parser type was null or blank.");
		}
		if (type.equals("mbta"))
		{
			parser = new MbtaTransitSystemParser();
		}
		return parser;
	}
}
