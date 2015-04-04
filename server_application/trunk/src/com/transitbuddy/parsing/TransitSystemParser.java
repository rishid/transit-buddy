package com.transitbuddy.parsing;

import java.util.ArrayList;

import com.common.types.TransitSystem;

public interface TransitSystemParser
{
	public ArrayList<TransitSystem> getTransitSystem();
	
	/**
	 * @return The page text to be parsed
	 */
	public String getPageText();

	/**
	 * Sets the page text to be parsed to the given string
	 * @param pageText The text to be parsed
	 */
	public void setPageText(String pageText);
}
