package com.transitbuddy.parsing;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.common.types.TransitSystem;

public class MbtaTransitSystemParser implements TransitSystemParser
{
	private static final Logger LOGGER = 
		Logger.getLogger(MbtaTransitSystemParser.class);
	
	private String mPageText;

	/**
	 * Constructor
	 */
	public MbtaTransitSystemParser()
	{
	}

	/**
	 * @return The page text to be parsed
	 */
	@Override
	public String getPageText()
	{
		return mPageText;
	}

	/**
	 * Sets the page text to be parsed to the given string
	 * @param pageText The text to be parsed
	 */
	@Override
	public void setPageText(String pageText)
	{
		mPageText = pageText;
	}
	
	/**
	 * 
	 * @return An array of transit information
	 */
	public ArrayList<TransitSystem> getTransitSystem()
	{
		ArrayList<TransitSystem> transitSystemArray = new ArrayList<TransitSystem>();

		return transitSystemArray;
	}

}
