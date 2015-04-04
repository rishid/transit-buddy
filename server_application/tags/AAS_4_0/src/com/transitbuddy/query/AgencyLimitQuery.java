package com.transitbuddy.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public abstract class AgencyLimitQuery extends Query
{
	private static final Logger LOGGER = 
		Logger.getLogger(AgencyLimitQuery.class);
	public static final String AGENCY_URL_DELIMITER = "|";
	protected int mIndex;
	protected int mLimit;
	public AgencyLimitQuery(int index, int limit)
	{
		mIndex = index;
		mLimit = limit;
	}
	
	public int getIndex()
	{
		return mIndex;
	}

	public void setIndex(int index)
	{
		mIndex = index;
	}

	public int getLimit()
	{
		return mLimit;
	}

	public void setLimit(int limit)
	{
		mLimit = limit;
	}

	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException
	{
		String ret = "";
		try 
		{
			while(rs.next())
			{
				ret += rs.getString(getAgencyIdColumn()) + 
					AGENCY_URL_DELIMITER;
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Got exception while parsing results: " + e.getMessage());
		}
		
		// Remove the last delimiter
		if (ret.length() > 0)
		{
			char lastChar = ret.charAt(ret.length() - 1);
			if (lastChar == AGENCY_URL_DELIMITER.charAt(0))
			{
				ret = ret.substring(0, ret.length() - 1);
			}
		}
		return ret;
	}
	
	@Override
	public void reset()
	{
	}
	
	protected abstract String getAgencyIdColumn();

}
