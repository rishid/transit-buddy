package com.transitbuddy.main.updator;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.common.types.TransitSystem;
import com.transitbuddy.parsing.TransitSystemParser;
import com.transitbuddy.parsing.TransitSystemParserFactory;

public class TransitInfoScraper implements Callable<ArrayList<TransitSystem>>
{
	private static final Logger LOGGER = 
		Logger.getLogger(TransitInfoScraper.class);
	private HTTPHelper mHelper;
	private TransitSystemParser mParser;
	
	public TransitInfoScraper(String url, String type) 
	throws IllegalArgumentException
	{
		if (url == null || url.equals(""))
		{
			LOGGER.error("Url was null or blank. Cannot " +
					"create Transit Info scraper.");
			throw new IllegalArgumentException("Url was null or blank. Cannot " +
					"create Transit Info scraper.");
		}
		mHelper = new HTTPHelper();
		mHelper.setUrl(url);
		mParser = TransitSystemParserFactory.getParserByType(type);
	}
	
	public String getPageText()
	{
		String ret = "";
		// Setup the HTTP client
		if (mHelper != null)
		{
			// Read the page material
			ret = mHelper.getPageText();
		}
		return ret;
	}
	
	@Override
	public ArrayList<TransitSystem> call() throws Exception
	{
		ArrayList<TransitSystem> transitSystems = new ArrayList<TransitSystem>();
		String page = getPageText();

		if (page != null && !page.equals(""))
		{
			// Parse the json page for the agency
			mParser.setPageText(page);
			try
			{
				transitSystems = mParser.getTransitSystem();
			}
			catch (Exception e)
			{
				LOGGER.error("Got exception while getting presales: " + e.getMessage() +
						". Agency URL = " + mHelper.getUrl());
			}
		}
		else
		{
			LOGGER.error("Page text was null or blank for url: " + mHelper.getUrl() +
					".");
		}
		
		return transitSystems;
	}
	
	
}
