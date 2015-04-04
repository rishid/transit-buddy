package com.transitbuddy.main.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.common.commands.CommandCode;
import com.common.commands.ICommand;
import com.transitbuddy.connection.ConnectionManager;
import com.transitbuddy.connection.ConnectionManagerFactory;

public class TransitInfoRequestProtocol
{
	private static final Logger LOGGER = 
		Logger.getLogger(TransitInfoRequestProtocol.class);
	
	private static final String DB_TYPE_PROPERTY = "type";
	private static final String TRANSIT_INFO_DB_PREFIX = "db.transitBuddy.";
	
	private ConnectionManager mTransitSystemDbManager;
	
	public TransitInfoRequestProtocol()throws FileNotFoundException, 
	ClassNotFoundException, SQLException, IOException
	{
		Properties props = TransitInfoServerRunner.getProperties();
		if (props != null)
		{
			// Create the TransitSystem DB Manager
			String dbType = props.getProperty(TRANSIT_INFO_DB_PREFIX + 
					DB_TYPE_PROPERTY);
			mTransitSystemDbManager = ConnectionManagerFactory.getConnectionManager(
					dbType, TRANSIT_INFO_DB_PREFIX, props);
		}
	}
	
	protected void finalize() throws Throwable 
	{
		try 
		{
			if (mTransitSystemDbManager != null)
			{
				mTransitSystemDbManager.closeConnection();
			}
		} 
		catch (SQLException e) 
		{
			LOGGER.error("Got exception trying to close database " +
			"connection.");
		}
	}
	
	/**
	 * Gets the requested data based on the given command's type
	 * @param command The command object specifying which data to retrieve
	 * @return The requested Serializable data. null is returned if the command's
	 *         type is INVALID_COMMAND, or if a SQLException is encountered.
	 */
	public Serializable processCommand(ICommand command)
	{
		Serializable output = null;
		CommandCode commandType = CommandCode.INVALID_COMMAND;
		if (command != null) 
		{
			commandType = command.getCommandCode();
		}
		
		try
    {
	    if (commandType != CommandCode.INVALID_COMMAND)
	    {
	    	switch(commandType)
	    	{
	    		case GET_NEARBY_STOPS:
	    			output = mTransitSystemDbManager.getNearbyStops();
	    			break;
	    		case GET_TRANSIT_SYSTEM:
	    			output = mTransitSystemDbManager.getTransitSystem(command);
	    			break;
	    		case GET_STOPS:
	    			output = mTransitSystemDbManager.getStops(command);
	    			break;
	    		case GET_TRIPS:
	    			output = mTransitSystemDbManager.getTrips(command);
	    			break;
	    		case GET_ROUTES:
	    			output = mTransitSystemDbManager.getRoutes(command);
	    			break;
	    	}
	    }
    }
    catch (SQLException e)
    {
    	LOGGER.error("Could not process command type " + commandType + 
    			". Got SQLException: " + e.getMessage());
    }
		
		return output;
	}

}
