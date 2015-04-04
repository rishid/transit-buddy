package com.transitbuddy.test.stress;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.common.commands.CommandCode;
import com.common.commands.GetNearbyStops;
import com.common.commands.GetRoutes;
import com.common.commands.GetStops;
import com.common.commands.GetStops.ScheduleType;
import com.common.commands.GetTransitSystems;
import com.common.commands.GetTrips;
import com.common.commands.SerializableCommand;
import com.common.connection.CommandTransporter;
import com.common.types.Coordinate;
import com.common.utilities.ResponseResult;

public class TestRunner implements Callable<CommandResponseResult>
{
	private static final Logger LOGGER = Logger.getLogger(TestRunner.class);
	private CommandCode mCommandCode;
	private CommandTransporter mTransporter;

	public TestRunner(String host, int port, CommandCode commandCode)
	    throws IllegalArgumentException, UnknownHostException, IOException
	{
		if (commandCode == null || commandCode == CommandCode.INVALID_COMMAND)
		{
			throw new IllegalArgumentException(
			    "CommandCode was null or invalid. Cannot create TestRunner");
		}
		if (host == null || host.equals(""))
		{
			throw new IllegalArgumentException(
			    "Server host name was null or blank. Cannot create TestRunner");
		}
		if (port <= 0)
		{
			throw new IllegalArgumentException("Server port was less than or"
			    + " equal to 0. Cannot create TestRunner");
		}
		mCommandCode = commandCode;

		// Make a connection to the server
		mTransporter = new CommandTransporter(host, port, 1000000);
	}

	private CommandResponseResult sendCommand()
	{
		CommandResponseResult ret = null;
		ResponseResult result = null;
		SerializableCommand command = null;
		switch (mCommandCode)
		{
			case GET_NEARBY_STOPS:
				command = new GetNearbyStops(
		    		1609, // 1 mile
		    		new Coordinate(42.395428f, -71.142483f), 5); // Alewife Station
				break;

			case GET_TRANSIT_SYSTEM:
				command = new GetTransitSystems();
				break;

			case GET_STOPS:
				command = new GetStops("1","Red Line","Alewife",ScheduleType.ALL_TIMES);
				break;

			case GET_TRIPS:
				command = new GetTrips("1","Red Line");
				break;

			case GET_ROUTES:
				command = new GetRoutes("1"); // 1 - mbta
				break;
			default:
				LOGGER.error("Got unsupported command code (" + mCommandCode
				    + "). Cannot run test.");
				break;
		}

		if (command != null)
		{
			result = mTransporter.sendCommand(command);
		}

		// If we got a result, then create a CommandResponseResult to return
		if (result != null)
		{
			ret = new CommandResponseResult(mCommandCode, result);
		}
		return ret;
	}

	public CommandResponseResult call() throws Exception
	{
		// Create and send the command based on the command type
		return sendCommand();
	}

}
