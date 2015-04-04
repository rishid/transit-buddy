package com.transitbuddy.query;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.common.types.TransitSystem;

public class TransitSystemExistsQuery extends Query 
{
	private static final Logger LOGGER = 
		Logger.getLogger(TransitSystemExistsQuery.class);
	
	/** Name of the column in the result set containing the count */
	private static final String COUNT_COL_NAME = "Count";
	
	protected TransitSystem mInfo;

	/**
	 * @see com.transitbuddy.query.Query#getAnswer(java.sql.ResultSet)
	 */
	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException 
	{
		String ret = "";
		while(rs.next())
		{
			ret += rs.getString(COUNT_COL_NAME);
		}
		try
		{
			rs.close();
		}
		catch (Exception e)
		{
			LOGGER.error("Got exception while closing result set. Exception: " + 
					e.getMessage());
		}
		return ret;
	}

	public void setTransitSystem(TransitSystem info)
	{
		mInfo = info;
	}

}
