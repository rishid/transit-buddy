package com.transitbuddy.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.common.commands.ICommand;
import com.common.types.TransitData;
import com.common.types.TransitSystem;

public class RetrieveTransitSystemQuery extends CommandableQuery
{

	private static final Logger LOGGER = 
		Logger.getLogger(RetrieveTransitSystemQuery.class);
	
	protected String AGENCY_ID_COL = "agency_id";
	protected String AGENCY_NAME_COL = "agency_name";
	
	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException
	{
		// System = MBTA
		TransitData data = new TransitData();
		while(rs.next())
		{
			String agencyId = rs.getString(AGENCY_ID_COL);
			String agencyName = rs.getString(AGENCY_NAME_COL);
			if(agencyId != null && agencyName != null)
			{
				data.addTransitSystem(new TransitSystem(agencyName, agencyId));
			}
			else
			{
				LOGGER.debug("Got null agency name or agency id while executing" +
						" command: " + this.getStatementString());
			}
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
		return data;
	}

	@Override
	public void reset()
	{
		// Nothing to do
	}

	@Override
	public void validateCommandAndSetQueryValues(ICommand command)
	    throws IllegalArgumentException
	{
		// Ensure the command is not null
		if (command == null)
		{
			throw new IllegalArgumentException("Command is null. Cannot create get"
			    + " transit systems SQL statement.");
		}
	}
	
}
