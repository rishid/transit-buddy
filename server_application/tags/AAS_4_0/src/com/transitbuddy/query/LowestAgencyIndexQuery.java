package com.transitbuddy.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public abstract class LowestAgencyIndexQuery extends Query
{

	private static final Logger LOGGER = 
		Logger.getLogger(TransitSystemExistsQuery.class);
	
	/**
	 * Default Constructor
	 */
	public LowestAgencyIndexQuery() 
	{
	}

	/**
	 * @see com.transitbuddy.query.Query#getAnswer(java.sql.ResultSet)
	 */
	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException 
	{
		String ret = "";
		while(rs.next())
		{
			ret += rs.getString(getIndexColumnName());
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
	


	@Override
  public void reset()
  {
  }
	
	protected abstract String getIndexColumnName();
}
