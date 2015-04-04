package com.transitbuddy.main.updator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;


public class HTTPHelper
{
	private static final Logger LOGGER = Logger.getLogger(HTTPHelper.class);

	/** The website whose contents will be retrieved */
	private String mUrl;


	/**
	 * Constructor
	 */
	public HTTPHelper()
	{
	}

	/**
	 * @return The website whose contents will be retrieved
	 */
	public String getUrl()
	{
		return this.mUrl;
	}

	/**
	 * Sets the website whose contents will be retrieved to the given one
	 * @param url The new URL
	 */
	public void setUrl(final String url)
	{
		mUrl = url;
	}

	/**
	 * Retrieves the text on the page from the URL
	 * @return A page of text
	 */
	public String getPageText()
	{
		HttpURLConnection hcon = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		String page = "";
		try
		{
			LOGGER.info("URL =  [" + mUrl + "]");
			URL url = new URL(mUrl);

			// Get an input stream for reading
			hcon = (HttpURLConnection) url.openConnection();
			if (hcon != null)
			{
				sb = new StringBuilder();
				int rc = hcon.getResponseCode();
				if (rc == HttpURLConnection.HTTP_OK)
				{
					br = new BufferedReader(new InputStreamReader(hcon.getInputStream()));

					String line = "";
					while ((line = br.readLine()) != null)
					{
						sb.append(line + '\n');
					}

					// Create a page text from the string buffer
					page = new String(sb.toString());
				}
				else
				{
					LOGGER.error("HTTP error:"+rc + ". URL = " + mUrl);
				}
			}
			else
			{
				LOGGER.error("HTTP connection was null. URL = " + mUrl);
			}

		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage() + "URL = " + mUrl, e);
		}
		finally
		{
			if (hcon != null)
			{
				hcon.disconnect();
			}
			try
			{
				if (br != null)
				{
					br.close();
				}
			}
			catch (final IOException ioe)
			{
				LOGGER.error(ioe.getMessage(), ioe);
			}
		}// end try/catch/finally

		return page;
	}
	
	public String formatUrlDate( String dateVal ) 
	{
		String[] tmp = dateVal.split(" ");
    return tmp[0];
	}
}
