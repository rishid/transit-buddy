package com.transitbuddy.test;

import java.io.IOException;
import java.net.UnknownHostException;

import com.common.commands.GetNearbyStops;
import com.common.commands.GetRoutes;
import com.common.commands.GetStops;
import com.common.commands.GetStops.ScheduleType;
import com.common.commands.GetTransitSystems;
import com.common.commands.GetTrips;
import com.common.connection.CommandTransporter;
import com.common.types.Coordinate;
import com.common.utilities.ResponseResult;


public class RealServerConnectionTest implements Runnable
{
	private static final int NUM_ARGS = 2;
	private static final int INVALID_PORT = -1;
	
	private String mHost = "";
	private int mPort = INVALID_PORT;
	
	/**
	 * Construstor.
	 * @param host The server host name or IP address
	 * @param port The server's port to listen on
	 * @throws NumberFormatException - if the port is not an integer
	 */
	public RealServerConnectionTest(String host, String port) 
	throws NumberFormatException
	{
		System.out.println("Host = " + host);
		System.out.println("Port = " + port);
		mHost = host;
		mPort = Integer.valueOf(port);
	}

	/**
	 * Prints the usage of this test to the command line
	 */
	private static void printUsage()
	{
		System.out.println("Usage: java -jar ServerConnectionTest.jar HOST PORT");
		System.out.println("Where HOST = IP address or name of the server to " +
				"connect to.");
		System.out.println("And PORT = port of the server to connect to.");
	}

	@Override
  public void run()
  {
		ResponseResult getTripsResult;
		ResponseResult getTransitSystemsResult;
		ResponseResult getRoutesResult;
		ResponseResult getStopsResult;
		ResponseResult getRealTimeStopsResult;
		ResponseResult getNearbyStopsResult;

	  try
    {
	    // Transit Systems test
	  	CommandTransporter transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    System.out.println("");
	    System.out.println("Test 1 - Transit Systems");
	    System.out.println("Sending GetTransitSystems command.");
	    GetTransitSystems command4 = new GetTransitSystems();
	    getTransitSystemsResult = transporter.sendCommand(command4);
	    processResult(getTransitSystemsResult);
	    
	    // Make a connection to the server
	    transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    
	    // Routes test
	    System.out.println("");
	    System.out.println("Test 2 - Routes Test");
	    System.out.println("Sending GetRoutes command.");
	    GetRoutes command = new GetRoutes("1"); // 1 - mbta
	    getRoutesResult = transporter.sendCommand(command);
	    processResult(getRoutesResult);
	    
	    // Trips test
	    transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    System.out.println("");
	    System.out.println("Test 3 - Trips Test");
	    System.out.println("Sending GetTrips command.");
	    GetTrips command1 = new GetTrips("1","Red Line"); // mbta, Red Line
	    getTripsResult = transporter.sendCommand(command1);
	    processResult(getTripsResult);
	    
	    // Nearby Stops Test
	    transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    System.out.println("");
	    System.out.println("Test 4 - Nearby Stops Test");
	    System.out.println("Sending GetNearbyStops command.");
	    GetNearbyStops command3 = new GetNearbyStops(
	    		1609, // 1 mile
	    		new Coordinate(42.395428f, -71.142483f), 5); // Alewife Station
	    getNearbyStopsResult = transporter.sendCommand(command3);
	    processResult(getNearbyStopsResult);
	    
	    // Stops test - all times
	    transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    System.out.println("");
	    System.out.println("Test 5 - Stops Test (All Times)");
	    System.out.println("Sending GetStops command.");
	    GetStops command2 = 
	    	new GetStops("1","Red Line","Alewife",ScheduleType.ALL_TIMES);
	    getStopsResult = transporter.sendCommand(command2);
	    processResult(getStopsResult);

	    // Stops test - new 5
	    transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    System.out.println("");
	    System.out.println("Test 5 - Real Time Stops Test (Next 5)");
	    System.out.println("Sending GetStops command.");
	    GetStops command5 = 
	    	new GetStops("1","1","Harvard Station via Mass. Ave.",ScheduleType.NEXT_FIVE);
	    getRealTimeStopsResult = transporter.sendCommand(command5);
	    processResult(getRealTimeStopsResult);
	    
    }
    catch (UnknownHostException e)
    {
	    System.err.println("Got UnknownHostException: " + e.getMessage());
    }
    catch (IOException e)
    {
	    System.err.println("Got IOException: " + e.getMessage());
    }

    System.out.println("******************************");
    System.out.println("End of Test");
    System.out.println("******************************");
  }
	
	private void processResult(ResponseResult result)
	{
	// Get the response status
    System.out.println("Getting Response");
    ResponseResult.ResponseStatus status = result.getResponseStatus();
    switch (status)
    {
    	case Completed:
    		System.out.println("Got Completed status from server. " + 
    				result.getMessage());
    		break;
    	case TimedOut:
    		System.err.println("Got TimedOut status from server. " + 
    				result.getMessage());
    		break;
    	case Failed:
    		System.err.println("Got Failed status from server. " + 
    				result.getMessage());
    		break;
    };
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// Command Line validation
		if (args.length != NUM_ARGS)
		{
			printUsage();
		}
		else
		{
			try
			{
				// Create a ServerConnectionTest object and run it
				RealServerConnectionTest test = 
					new RealServerConnectionTest(args[0], args[1]);
				Thread testThread = new Thread(test);
				testThread.start();
			}
			catch (Exception e) 
			{
				System.err.println("Got "+ e.getClass() + ":" + e.getMessage());
			}
		}
	}
}
